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
        PersistentDatabase persistentDatabase = new PersistentDatabase(new FakeSecureDatabaseFactory());
        persistentDatabase.dbInstance("test");
        Map<String, List<Payment>> emptyMap = Collections.emptyMap();
        persistentDatabase.saveToDb(emptyMap);  //shouldn't throw
        persistentDatabase.saveToDb(testMap);
        assertEquals(PAYMENT_LIST, persistentDatabase.get("123"));
        assertEquals(PAYMENT_LIST1, persistentDatabase.get("shai"));

    }

}
