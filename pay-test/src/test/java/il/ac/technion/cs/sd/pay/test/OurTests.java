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
import static org.junit.Assert.assertEquals;

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

    @Test
    public void testSimple() throws Exception {
        PayBookReader reader = setupAndGetReader();
        Map<String, Integer> res1 = new HashMap<>();
        res1.put("Moobar", 7);
        res1.put("Foobar", 10);
        assertEquals(reader.getBiggestPaymentsFromClients(), res1);
        assertEquals(reader.getBiggestSpenders(), Arrays.asList("Foobar", "Lol"));
//        Mockito.when(mock.getQueryAnswer(PayBookInitializerImpl.topClients)).thenReturn(Arrays.asList("Foobar", "Lol"));
//        assertEquals(Arrays.asList("Foobar", "Boobar", "Moobar"), reader.getRichestSellers());
//        assertEquals(OptionalDouble.of(10.0), reader.getPayment("123", "Foobar"));
//        assertEquals(Optional.empty(), reader.getFavoriteSeller("124"));
    }
}
