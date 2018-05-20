package PayBookImplementations;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
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

// TODO: 20-May-18 use maven to define depencies for these libraries or something?

public class PayBookInitializerImpl implements PayBookInitializer {

    public static final String SELLERS = "sellers";
    public static final String CLIENTS = "clients";
    public static final String TOP_PAYMENTS_CLIENTS = "top payments clients";
    public static final String TOP_PAYMENTS_SELLERS = "top payments sellers";

    @Inject
    public PayBookInitializerImpl(SecureDatabase secureDatabase) {
    }


    @Override
    public void setup(String xmlData) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(xmlData));
            Document doc = documentBuilder.parse(inputSource);
//            Map<String, List<byte[]>> clients = new HashMap<>();
//            Map<String, List<byte[]>> sellers = new HashMap<>();
            Map<String, List<Payment>> clients = new HashMap<>();
            Map<String, List<Payment>> sellers = new HashMap<>();


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

            // TODO: 20-May-18 go over this.
            // fill the sellers dictionary. assumes the client dict is valid i.e. has done all the parsing and
            // summed payments from same client to same seller.
            for (String clientId : clients.keySet()) {
                for (Payment payment : clients.get(clientId)) {
                    if (sellers.containsKey(payment.getId())){
                        sellers.get(payment.getId()).add(new Payment(clientId, payment.getValue()));
                    } else {
                        List<Payment> paymentList = new ArrayList<>();
                        paymentList.add(new Payment(clientId,payment.getValue()));
                        sellers.put(payment.getId(), paymentList);
                    }
                }
            }


            // TODO: 20-May-18 sort the whole lib usage here...
            /* use library to open the databases required (currently like 5? sellers, clients, topPaymentsClients,
                topPaymentsSellers, need to think about more)
             */
            PersistentDatabase pdb = new PersistentDatabase();
            pdb.dbInstance(CLIENTS);
            pdb.dbInstance(SELLERS);
            pdb.dbInstance(TOP_PAYMENTS_CLIENTS);
            pdb.dbInstance(TOP_PAYMENTS_SELLERS);

            //add everything to SecureDB. accept an entire map? TODO use byte[] instead of pojo class so no dependency
//            pdb.fillDb(CLIENTS, clients);
//            pdb.fillDb(SELLERS, sellers);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getStringFromElement(Element element) {
        Node node = element.getFirstChild();
        if (node instanceof CharacterData) {
            return ((CharacterData) node).getData();
        }
        System.out.println("shouldn't have gotten here");
        return "";
    }
}
