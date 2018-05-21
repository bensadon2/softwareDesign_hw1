package PayBookImplementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import il.ac.technion.cs.sd.pay.app.PayBookReader;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static PayBookImplementations.PayBookInitializerImpl.CLIENTS;
import static PayBookImplementations.PayBookInitializerImpl.SELLERS;

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
        List<byte[]> paymentsByteList = dbByClients.get(clientId);
        List<Payment> paymentsToSeller = paymentsByteList.stream().map(bytes -> new Payment(bytes))
                .filter(payment -> payment.getId().equals(sellerId))
                .collect(Collectors.toList());
        return !paymentsToSeller.isEmpty();
    }

    @Override
    public OptionalDouble getPayment(String clientId, String sellerId) {
        return null;
    }

    @Override
    public List<String> getBiggestSpenders() {
        return null;
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
}
