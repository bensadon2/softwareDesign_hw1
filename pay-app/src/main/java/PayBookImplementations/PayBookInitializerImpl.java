package PayBookImplementations;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * topSellers, topClients queries return a List<String>
 * topPayments return a List<Payment>
 */
public class PayBookInitializerImpl implements PayBookInitializer {

    public static final String SELLERS = "sellers";
    public static final String CLIENTS = "clients";
    public static final String QUERIES = "queries";

    public static final String topClients = "3";
    public static final String topSellers = "4";
    public static final String topPaymentsSellers = "7";
    public static final String topPaymentsClients = "8";

    private final PersistentDatabase dbByClients;
    private final PersistentDatabase dbBySellers;
    private final PersistentDatabase queryDb;

    @Inject
    public PayBookInitializerImpl(PersistentDatabase dbByClients, PersistentDatabase dbBySellers, PersistentDatabase queryDb) {
        this.dbByClients = dbByClients;
        this.dbByClients.dbInstance(CLIENTS);
        this.dbBySellers = dbBySellers;
        this.dbBySellers.dbInstance(SELLERS);
        this.queryDb = queryDb;
        this.queryDb.dbInstance(QUERIES);
    }


    @Override
    public void setup(String xmlData) {
        try {

            Map<String, List<Payment>> clients = ParseXMLToMap(xmlData);

            // fill the sellers dictionary. assumes the client dict is valid i.e. has done all the parsing and
            // summed payments from same client to same seller.
            Map<String, List<Payment>> sellers = buildSellerMap(clients);

            // TOP PAYING CLIENTS
            ArrayList<String> topPayingClients = getTopTen(clients);
            queryDb.saveToDb2(topClients, topPayingClients);

            // TOP EARNING SELLERS
            ArrayList<String> topEarningSellers = getTopTen(sellers);
            queryDb.saveToDb2(topSellers, topEarningSellers);

            ArrayList<Payment> sellerTopPayments = getTopTenPayments(sellers);
            queryDb.saveToDb(topPaymentsSellers, sellerTopPayments);

            ArrayList<Payment> clientTopPayments = getTopTenPayments(clients);
            queryDb.saveToDb(topPaymentsClients, clientTopPayments);

            dbByClients.saveToDb(clients);
            dbBySellers.saveToDb(sellers);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Payment> getTopTenPayments(Map<String, List<Payment>> sellers) {
        return sellers.entrySet().stream()
                .sorted(
                        Comparator.comparing(this::getHighestPayment)
                                .reversed()
                                .thenComparing(Map.Entry::getKey)
                )
                .limit(10)
                .map(e -> new Payment(e.getKey(), getHighestPayment(e)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<String> getTopTen(Map<String, List<Payment>> clientsOrSellers) {
        Comparator<Map.Entry<String, List<Payment>>> comparator =
                Comparator.comparing(e -> e.getValue().stream()
                        .mapToLong(Payment::getValue).sum());
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Map.Entry::getKey);

        return clientsOrSellers.entrySet().stream()
                .sorted(comparator)
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Integer getHighestPayment(Map.Entry<String, List<Payment>> entry) {
        try {
            List<Integer> res = entry.getValue().stream()
                    .sorted(Comparator.comparing(Payment::getValue)
                            .reversed()
                            .thenComparing(Payment::getId))
                    .limit(1)
                    .mapToInt(Payment::getValue)
                    .boxed()
                    .collect(Collectors.toList());
            return res.get(0);
        } catch (Exception e) {
            return 0;
        }
    }

    private Map<String, List<Payment>> buildSellerMap(Map<String, List<Payment>> clients) {
        Map<String, List<Payment>> sellers = new HashMap<>();
        for (String clientId : clients.keySet()) {
            for (Payment payment : clients.get(clientId)) {
                if (sellers.containsKey(payment.getId())) {
                    sellers.get(payment.getId()).add(new Payment(clientId, payment.getValue()));
                } else {
                    List<Payment> paymentList = new ArrayList<>();
                    paymentList.add(new Payment(clientId, payment.getValue()));
                    sellers.put(payment.getId(), paymentList);
                }
            }
        }
        return sellers;
    }

    private Map<String, List<Payment>> ParseXMLToMap(String xmlData) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlData));
        Document doc = documentBuilder.parse(inputSource);
        Map<String, List<Payment>> clients = new HashMap<>();


        // parse xml string
        NodeList nodes = doc.getElementsByTagName("Client");
        // go over clients
        for (int i = 0; i < nodes.getLength(); i++) {
            Element clientElement = (Element) nodes.item(i);
            NodeList payments = clientElement.getElementsByTagName("Payment");
            String clientStr = clientElement.getAttribute("Id");

            //go over sellers for a client
            for (int j = 0; j < payments.getLength(); j++) {
                Element paymentElement = (Element) payments.item(j);
                NodeList sellerID = paymentElement.getElementsByTagName("Id"),
                        amount = paymentElement.getElementsByTagName("Amount");

                Element sellerElement = (Element) sellerID.item(0);
                Element amountElement = (Element) amount.item(0);
                Payment payment = new Payment(getStringFromElement(sellerElement),
                        Integer.valueOf(getStringFromElement(amountElement).replaceAll("[^\\d]", "")));

                // if already have payment for this seller, add instead of creating new entry
                if (clients.containsKey(clientStr)) {
                    List<Payment> paymentsList = clients.get(clientStr);
                    List<Payment> filterList = paymentsList.stream()
                            .filter(current_payment -> current_payment.getId().equals(payment.getId()))
                            .collect(Collectors.toList());
                    if (!filterList.isEmpty()) {
                        int oldValue = filterList.get(0).getValue();
                        payment.setValue(oldValue + payment.getValue());
                        if (!Collections.replaceAll(paymentsList, filterList.get(0), payment)) {
                            throw new Exception("thought there was an existing payment, but failed to replace old with new");
                        }
                    } else {
                        paymentsList.add(payment);
                    }
                } else {
                    ArrayList<Payment> newPaymentList = new ArrayList<>();
                    newPaymentList.add(payment);
                    clients.put(clientStr, newPaymentList);
                }
            }
        }
        return clients;
    }

    private String getStringFromElement(Element element) {
        Node node = element.getFirstChild();
        if (node instanceof CharacterData) {
            return ((CharacterData) node).getData().trim();
        }
        System.out.println("shouldn't have gotten here");
        return "";
    }
}
