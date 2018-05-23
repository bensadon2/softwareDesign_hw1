import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class LibTest {

    private static final List<Payment> PAYMENT_LIST = Arrays.asList(new Payment("Foobar", 10), new Payment("Moobar", 7));
    private static final List<Payment> PAYMENT_LIST1 = Arrays.asList(new Payment("Foobar", 4), new Payment("Boobar", 2));
    private static final List<Payment> PAYMENT_LIST2 = Arrays.asList(new Payment("Foobar", 3), new Payment("Boobar", 3));
    private static final String ID2 = "456";
    private static final String ID1 = "123";
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);
    private HashMap<String, List<Payment>> testMap;

    @Before
    public void setup() {
        testMap = new HashMap<>();
        testMap.put(ID1, PAYMENT_LIST);
        testMap.put(ID2, PAYMENT_LIST1);
    }

    @Test
    public void simpleTest() {
        PersistentDatabase persistentDatabase = getPersistentDbTestInst();
        Map<String, List<Payment>> emptyMap = Collections.emptyMap();
        persistentDatabase.saveToDb(emptyMap);  //shouldn't throw
        persistentDatabase.saveToDb(testMap);
        List<Payment> queryResult = persistentDatabase.get(ID1);
        checkPaymentList(queryResult, PAYMENT_LIST);
        List<Payment> queryResult2 = persistentDatabase.get(ID2);
        checkPaymentList(queryResult2, PAYMENT_LIST1);
    }

    @Test
    public void overrideSaveTest() {
        PersistentDatabase persistentDatabase = getPersistentDbTestInst();
        persistentDatabase.saveToDb(ID2, PAYMENT_LIST2);
        List<Payment> paymentList = persistentDatabase.get(ID2);
        checkPaymentList(paymentList, PAYMENT_LIST2);

        persistentDatabase.saveToDb(testMap);
        paymentList = persistentDatabase.get(ID1);
        checkPaymentList(paymentList, PAYMENT_LIST);
        List<Payment> paymentList2 = persistentDatabase.get(ID2);
        checkPaymentList(paymentList2, PAYMENT_LIST1);

        persistentDatabase.saveToDb(ID1, PAYMENT_LIST2);
        paymentList = persistentDatabase.get(ID1);
        checkPaymentList(paymentList, PAYMENT_LIST2);

        persistentDatabase.saveToDb(testMap);
        paymentList = persistentDatabase.get(ID1);
        checkPaymentList(paymentList, PAYMENT_LIST);
        paymentList2 = persistentDatabase.get(ID2);
        checkPaymentList(paymentList2, PAYMENT_LIST1);
    }

    ///////////////////////
    // utility
    ///////////////////////

    private PersistentDatabase getPersistentDbTestInst() {
        Injector injector = Guice.createInjector(new LibTestModule());
        PersistentDatabase persistentDatabase = new PersistentDatabase(injector.getInstance(SecureDatabaseFactory.class));
        persistentDatabase.dbInstance("test");
        return persistentDatabase;
    }

    private void checkPaymentList(List<Payment> queryResult, List<Payment> paymentList) {
        for (int i = 0; i < queryResult.size(); i++) {
            assertEquals(paymentList.get(i), queryResult.get(i));
        }
    }

}
