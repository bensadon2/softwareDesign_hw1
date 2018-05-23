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
    private static final List<Payment> PAYMENT_LIST2 = Arrays.asList(new Payment("Foobar", 2), new Payment("Boobar", 2));
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);
    private HashMap<String, List<Payment>> testMap;

    @Before
    public void setup() {
        testMap = new HashMap<>();
        testMap.put("123", PAYMENT_LIST);
        testMap.put("shai", PAYMENT_LIST1);
    }

    @Test
    public void simpleTest() {
        PersistentDatabase persistentDatabase = getPersistentDbTestInst();
        Map<String, List<Payment>> emptyMap = Collections.emptyMap();
        persistentDatabase.saveToDb(emptyMap);  //shouldn't throw
        persistentDatabase.saveToDb(testMap);
        List<Payment> queryResult = persistentDatabase.get("123");
        for (int i = 0; i < queryResult.size(); i++) {
            assertEquals(PAYMENT_LIST.get(i), queryResult.get(i));
        }
        List<Payment> queryResult2 = persistentDatabase.get("shai");
        for (int i = 0; i < queryResult2.size(); i++) {
            assertEquals(PAYMENT_LIST1.get(i), queryResult2.get(i));
        }
    }

//    @Test
//    public void overrideSaveTest() {
//        PersistentDatabase persistentDatabase = getPersistentDbTestInst();
//        persistentDatabase.saveToDb("shai", );
//    }

    ///////////////////////
    // utility
    ///////////////////////

    private PersistentDatabase getPersistentDbTestInst() {
        Injector injector = Guice.createInjector(new LibTestModule());
        PersistentDatabase persistentDatabase = new PersistentDatabase(injector.getInstance(SecureDatabaseFactory.class));
        persistentDatabase.dbInstance("test");
        return persistentDatabase;
    }

}
