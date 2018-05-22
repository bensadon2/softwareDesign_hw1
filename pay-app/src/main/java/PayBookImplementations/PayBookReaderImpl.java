package PayBookImplementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import il.ac.technion.cs.sd.pay.app.PayBookReader;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.*;
import java.util.stream.Collectors;

import static PayBookImplementations.PayBookInitializerImpl.CLIENTS;
import static PayBookImplementations.PayBookInitializerImpl.SELLERS;
import static java.lang.Math.min;

public class PayBookReaderImpl implements PayBookReader {

    private final PersistentDatabase dbByClients;
    private final PersistentDatabase dbBySellers;

//    @Inject
//    public PayBookReaderImpl(@Named("dbByClients") PersistentDatabase dbByClients,
//                             @Named("dbBySellers") PersistentDatabase dbBySellers) {
//        this.dbByClients = dbByClients;
//        this.dbByClients.dbInstance(CLIENTS);
//        this.dbBySellers = dbBySellers;
//        this.dbBySellers.dbInstance(SELLERS);
//    }

    @Inject
    public PayBookReaderImpl(PersistentDatabase dbByClients, PersistentDatabase dbBySellers) {
        this.dbByClients = dbByClients;
        this.dbByClients.dbInstance(CLIENTS);
        this.dbBySellers = dbBySellers;
        this.dbBySellers.dbInstance(SELLERS);
    }

    @Override
    public boolean paidTo(String clientId, String sellerId) {
        List<byte[]> paymentsByteList = dbByClients.get(clientId);
        List<Payment> paymentsToSeller = paymentsByteList.stream().map(Payment::new)
                .filter(payment -> payment.getId().equals(sellerId))
                .collect(Collectors.toList());
        List<Payment> paymentsToSeller = getClientSellerPayments(clientId, sellerId);
        return !paymentsToSeller.isEmpty();
    }

    @Override
    public OptionalDouble getPayment(String clientId, String sellerId) {
        List<Payment> clientSellerPayments = getClientSellerPayments(clientId, sellerId);
        if (!clientSellerPayments.isEmpty()){
            return OptionalDouble.of(clientSellerPayments.get(0).getValue());
        }
        return OptionalDouble.empty();
    }

    @Override
    public List<String> getBiggestSpenders() {
        // this assumes the usual sort is "biggest" first
        Map<String, Long> map = sumMap(dbByClients);
        return top10FromMap(map);
    }

    @Override
    public List<String> getRichestSellers() {
        // this assumes the usual sort is "biggest" first
        Map<String, Long> map = sumMap(dbBySellers);
        return top10FromMap(map);
    }

    @Override
    public Optional<String> getFavoriteSeller(String clientId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getBiggestClient(String sellerId) {
        return Optional.empty();
    }

    @Override
    public Map<String, Integer> getBiggestPaymentsToSellers() {
        return null;
    }

    @Override
    public Map<String, Integer> getBiggestPaymentsFromClients() {
        return null;
    }

    private List<Payment> getClientSellerPayments(String clientId, String sellerId) {
        List<byte[]> paymentsByteList = dbByClients.get(clientId);
        return paymentsByteList.stream().map(bytes -> new Payment(bytes))
                .filter(payment -> payment.getId().equals(sellerId))
                .collect(Collectors.toList());
    }

    /**
     * makes a map of the given {@link PersistentDatabase}'s keys and the total sum of their relevant payments.
     * @param persistentDb the db to perform the operation on.
     * @return a map with the keys of the db, with their respective sums as values.
     */
    private Map<String, Long> sumMap(PersistentDatabase persistentDb) {
        List<byte[]> idList = persistentDb.getAllKeys();
        Map<String, Long> map = new HashMap<>();
        for (byte[] id : idList) {
            String idStr = new String(id);
            List<byte[]> paymentsBytes = persistentDb.get(idStr);
            long sum = paymentsBytes.stream().mapToLong(bytes -> (new Payment(bytes)).getValue()).sum();
            map.put(idStr, sum);
        }
        return map;
    }

    /**
     * returns the top 10 IDs and their sums (descending order). sorting is by values, then keys
     * @param map the map that will be sorted.
     * @return a sublist (if the map is big enough) of the highest 10 entries.
     */
    private List<String> top10FromMap(Map<String, Long> map) {
        ArrayList<Map.Entry<String, Long>> entries = sortEntries(map);
        // get the top 10 if the exist.
        return entries.subList(0,min(9,entries.size())).stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * sorts the map, by the values, then by the keys.
     * @param map a <String, Long> map
     * @return a sorted list of entries
     */
    private ArrayList<Map.Entry<String, Long>> sortEntries(Map<String, Long> map) {
        ArrayList<Map.Entry<String, Long>> entries = new ArrayList<>(map.entrySet());
        entries.sort((entry1, entry2) -> {
            int comp1 = entry1.getValue().compareTo(entry2.getValue());
            if (comp1 != 0) {
                return comp1;
            } else return entry1.getKey().compareTo(entry2.getKey());
        });
        return entries;
    }
}
