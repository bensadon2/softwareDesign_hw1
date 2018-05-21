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

    @Inject
    public PayBookReaderImpl(@Named("dbByClients") PersistentDatabase dbByClients,
                             @Named("dbBySellers") PersistentDatabase dbBySellers) {
        this.dbByClients = dbByClients;
        this.dbByClients.dbInstance(CLIENTS);
        this.dbBySellers = dbBySellers;
        this.dbBySellers.dbInstance(SELLERS);
    }

    @Override
    public boolean paidTo(String clientId, String sellerId) {
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
        List<byte[]> clientsList = dbByClients.getAllKeys();
        Map<String, Long> map = getClientSumMap(clientsList);

        ArrayList<Map.Entry<String, Long>> entries = sortEntries(map);

        // get the top 10 if the exist.
        return entries.subList(0,min(9,entries.size())).stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRichestSellers() {
        return null;
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

    private Map<String, Long> getClientSumMap(List<byte[]> clientsList) {
        Map<String, Long> map = new HashMap<>();
        for (byte[] clientId : clientsList) {
            String clientIdStr = new String(clientId);
            List<byte[]> clientPaymentsBytes = dbByClients.get(clientIdStr);
            long clientSum = clientPaymentsBytes.stream().mapToLong(bytes -> (new Payment(bytes)).getValue()).sum();
            map.put(clientIdStr, clientSum);
        }
        return map;
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
