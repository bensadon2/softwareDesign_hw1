package il.ac.technion.cs.sd.pay.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import il.ac.technion.cs.sd.pay.app.PayBookReader;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import structs.Payment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static PayBookImplementations.PayBookInitializerImpl.topPaymentsClients;
import static org.junit.Assert.*;

public class OurTests {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    private static PayBookReader setupAndGetReader() {
//        String fileContents =
//                new Scanner(new File(OurTests.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
        Injector injector = Guice.createInjector(new PayBookModule(), new PersistentTestModule());
//        injector.getInstance(PayBookInitializer.class).setup(fileContents);
        return injector.getInstance(PayBookReader.class);
    }

    private static PayBookReader setupAndGetReaderInitializer(String fileName) throws FileNotFoundException {
        String fileContents =
                new Scanner(new File(OurTests.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
        Injector injector = Guice.createInjector(new PayBookModule(), new InitializerModule());
        injector.getInstance(PayBookInitializer.class).setup(fileContents);
        return injector.getInstance(PayBookReader.class);
    }

    @Test
    public void testSimple() throws Exception {
        PayBookReader reader = setupAndGetReader();
        Map<String, Integer> res1 = new HashMap<>();
        res1.put("Moobar", 7);
        res1.put("Foobar", 10);
        assertEquals(reader.getBiggestPaymentsFromClients(), res1);
        assertEquals(reader.getBiggestSpenders(), Arrays.asList("Foobar", "Lol"));
        assertTrue(reader.paidTo("paidTo", "joey"));
        assertFalse(reader.paidTo("paidTo", "ross"));
        assertEquals(1.0, reader.getPayment("paidTo", "joey").getAsDouble(), 0.00001);
        assertEquals(3.0, reader.getPayment("paidTo", "monica").getAsDouble(), 0.00001);
        assertFalse(reader.getPayment("paidTo", "pheobe").isPresent());
    }

    @Test
    public void testMedium2() throws Exception {
        PayBookReader reader = setupAndGetReaderInitializer("medium.xml");
        assertTrue(reader.paidTo("151", "Poobar"));
        assertEquals(OptionalDouble.of(3), reader.getPayment("151", "Poobar"));
        assertFalse(reader.paidTo("414", "Poobar"));
        assertEquals(OptionalDouble.of(13), reader.getPayment("884", "123"));
        assertEquals(OptionalDouble.of(7), reader.getPayment("763", "Noobar"));
    }

    @Test
    public void testMedium3() throws FileNotFoundException {
        PayBookReader reader = setupAndGetReaderInitializer("medium.xml");
        assertEquals(Optional.of("123"), reader.getBiggestClient("Foobar"));
        assertEquals(Optional.of("884"), reader.getBiggestClient("123"));
        assertEquals(Optional.of("444"), reader.getBiggestClient("171"));
        assertEquals(Optional.of("313"), reader.getBiggestClient("Poobar"));
//        assertEquals(, reader.getRichestSellers());
        // TODO: 24-May-18 add some query for this - number 4

    }
}
