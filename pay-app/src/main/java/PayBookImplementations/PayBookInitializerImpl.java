package PayBookImplementations;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

// TODO: 20-May-18 use maven to define dependencies for these libraries or something?

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


    //    public static final String TOP_PAYMENTS_CLIENTS = "top payments clients";
//    public static final String TOP_PAYMENTS_SELLERS = "top payments sellers";
    private final PersistentDatabase dbByClients;
    private final PersistentDatabase dbBySellers;
    private final PersistentDatabase queryDb;

//    @Inject
//    public PayBookInitializerImpl(@Named("dbByClients") PersistentDatabase dbByClients,
//                                  @Named("dbBySellers") PersistentDatabase dbBySellers) {
//        this.dbByClients = dbByClients;
//        this.dbByClients.dbInstance(CLIENTS);
//        this.dbBySellers = dbBySellers;
//        this.dbBySellers.dbInstance(SELLERS);
//    }

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

//            Map<String, List<byte[]>> clientsByteMap = convertToByteMap(clients);
//            Map<String, List<byte[]>> sellersByteMap = convertToByteMap(sellers);   // TODO: 21-May-18 more efficient way for this might be required

            // TODO: 20-May-18 sort the whole lib usage here...
            /* use library to open the databases required (currently like 5? sellers, clients, topPaymentsClients,
                topPaymentsSellers, need to think about more)
             */

            //add everything to SecureDB. accept an entire map? TODO use byte[] instead of pojo class so no dependency
//            dbByClients.saveToDb(clientsByteMap);

//            Comparator<List<Payment>> comparator = Comparator.comparing(l -> l.stream().mapToLong(p -> p.getValue()).sum());

            // Primary compare sum of purchases, secondary compare ID
            // TODO: might need to reverse sorting order in one or more categories, and maybe the order between them
            // TODO: It is possible to reverse order by calling .reverse() on the comparator, as long as it DOESN'T use lambdas
            Comparator<Map.Entry<String, List<Payment>>> comparator =
                    Comparator.comparing(e -> e.getValue().stream()
                            .mapToLong(Payment::getValue).sum());
            comparator = comparator.reversed();
            comparator = comparator.thenComparing(Map.Entry::getKey);

            // TOP PAYING CLIENTS
            ArrayList<String> topPayingClients = clients.entrySet().stream()
                    .sorted(
                            comparator
//                            (e1, e2) -> e1.getValue().stream().mapToLong(p -> p.getValue()).sum().compareTo(
//                                    e2.getValue().stream().mapToLong(p -> p.getValue()).sum())
                    )
                    .map(Map.Entry::getKey)
                    .limit(10)
                    .collect(Collectors.toCollection(ArrayList::new));
            queryDb.saveToDb(topClients, topPayingClients);

            // TOP EARNING SELLERS
            ArrayList<String> topEarningSellers = sellers.entrySet().stream()
                    .sorted(comparator)
                    .limit(10)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
            queryDb.saveToDb(topSellers, topEarningSellers);

            ArrayList<Payment> sellerTopPayments = sellers.entrySet().stream()
                    .sorted(Comparator.comparing(this::getHighestPayment))
                    .limit(10)
                    .map(e -> new Payment(e.getKey(), getHighestPayment(e)))
                    .collect(Collectors.toCollection(ArrayList::new));
            queryDb.saveToDb(topPaymentsSellers, sellerTopPayments);

            ArrayList<Payment> clientTopPayments = clients.entrySet().stream()
                    .sorted(Comparator.comparing(this::getHighestPayment))
                    .limit(10)
                    .map(e -> new Payment(e.getKey(), getHighestPayment(e)))
                    .collect(Collectors.toCollection(ArrayList::new));
            queryDb.saveToDb(topPaymentsClients, clientTopPayments);

//            Map<String, Integer> clientsWithTopPayments = clients.entrySet().stream()
//
////                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), getHighestPayment(e)))
////                    .sorted(Comparator.comparing(AbstractMap.SimpleEntry::getValue))
//
//                    .sorted(Comparator.comparing(this::getHighestPayment))
//                    .limit(10)
//                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), getHighestPayment(e)))
//                    // TODO: mapping can be before or after the limit, depends on what takes longer - "getHighestPayment"
//                    // TODO: or the mapping itself
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//            HashSet<Pair<String, Integer>> clientsWithTopPaymentsSet = clientsWithTopPayments.entrySet().stream()
//                    .map(e -> new Pair<>(e.getKey(), e.getValue()))
//                    .collect(Collectors.toCollection(HashSet::new));

//            queryDb.saveToDb(topPaymentsClients, clientsWithTopPaymentsSet);

//            Map<String, Integer> sellersWithTopPayments = sellers.entrySet().stream()
//
////                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), getHighestPayment(e)))
////                    .sorted(Comparator.comparing(AbstractMap.SimpleEntry::getValue))
//
//                    .sorted(Comparator.comparing(this::getHighestPayment))
//                    .limit(10)
//                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), getHighestPayment(e)))
//                    // TODO: mapping can be before or after the limit, depends on what takes longer - "getHighestPayment"
//                    // TODO: or the mapping itself
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//            HashSet<Pair<String, Integer>> sellersWithTopPaymentsSet = sellersWithTopPayments.entrySet().stream()
//                    .map(e -> new Pair<>(e.getKey(), e.getValue()))
//                    .collect(Collectors.toCollection(HashSet::new));
//
//            queryDb.saveToDb(topPaymentsSellers, sellersWithTopPaymentsSet);

            dbByClients.saveToDb(clients);
//            dbBySellers.saveToDb(sellersByteMap);
            dbBySellers.saveToDb(sellers);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // TODO: this is returning Integer because that's the end result expected by the reader :/
    private Integer getHighestPayment(Map.Entry<String, List<Payment>> entry) {
        try {
            List<Integer> res = entry.getValue().stream()
                    .sorted(Comparator.comparing(Payment::getValue))
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
        // TODO: 20-May-18 go over this.
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
            String clientStr = getStringFromElement(clientElement);

            //go over sellers for a client
            for (int j = 0; j < payments.getLength(); j++) {
                Element paymentElement = (Element) payments.item(j);
                NodeList sellerID = paymentElement.getElementsByTagName("Id"),
                        amount = paymentElement.getElementsByTagName("Amount");

                Element sellerElement = (Element) sellerID.item(0);
                Element amountElement = (Element) amount.item(0);
                Payment payment = new Payment(getStringFromElement(sellerElement),
                        Integer.parseInt(getStringFromElement(amountElement)));

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

//    private Map<String, List<byte[]>> convertToByteMap(Map<String, List<Payment>> paymentMap) {
//        Map<String, List<byte[]>> result = new HashMap<>();
//        for (String key : paymentMap.keySet()) {
//            List<byte[]> byteArrayList = paymentMap.get(key).stream()
//                    .map(Payment::toBytes)
//                    .collect(Collectors.toList());
//            result.put(key, byteArrayList);
//        }
//
//        return result;
//    }

    private String getStringFromElement(Element element) {
        Node node = element.getFirstChild();
        if (node instanceof CharacterData) {
            return ((CharacterData) node).getData();
        }
        System.out.println("shouldn't have gotten here");
        return "";
    }
}
