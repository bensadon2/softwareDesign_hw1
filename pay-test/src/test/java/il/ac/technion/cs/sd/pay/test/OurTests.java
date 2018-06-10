package il.ac.technion.cs.sd.pay.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import il.ac.technion.cs.sd.pay.app.PayBookReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import structs.Payment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class OurTests {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);
    private Map<String, Integer> q8res;
    private Map<String, Integer> q7res;
    private List<String> q4res;
    private List<String> q3res;

    private static PayBookReader setupAndGetReader() {
        Injector injector = Guice.createInjector(new PayBookModule(), new PersistentTestModule());
        return injector.getInstance(PayBookReader.class);
    }

    private static PayBookReader setupAndGetReaderInitializer(String fileName) throws FileNotFoundException {
        String fileContents =
                new Scanner(new File(OurTests.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
        Injector injector = Guice.createInjector(new PayBookModule(), new InitializerModule());
        injector.getInstance(PayBookInitializer.class).setup(fileContents);
        return injector.getInstance(PayBookReader.class);
    }

    @Before
    public void setUp() throws Exception {

        List<Payment> query8Results = Arrays.asList(
                new Payment("884", 13),
                new Payment("123", 10),
                new Payment("313", 10),
                new Payment("818", 10),
                new Payment("917", 10),
                new Payment("121", 9),
                new Payment("171", 9),
                new Payment("191", 9),
                new Payment("414", 9),
                new Payment("444", 9)
        );
        q8res = query8Results.stream().collect(Collectors.toMap(Payment::getId, Payment::getValue));

        List<Payment> query7Results = Arrays.asList(
                new Payment("123", 13),
                new Payment("Coobar", 10),
                new Payment("Foobar", 10),
                new Payment("Poobar", 10),
                new Payment("171", 9),
                new Payment("181", 9),
                new Payment("212", 9),
                new Payment("414", 9),
                new Payment("Moobar", 9),
                new Payment("111", 8)
        );
        q7res = query7Results.stream().collect(Collectors.toMap(Payment::getId, Payment::getValue));

        q4res = Arrays.asList(
                "Coobar",
                "Foobar",
                "Moobar",
                "Poobar",
                "123",
                "151",
                "181",
                "212",
                "414",
                "Boobar"
        );

        q3res = Arrays.asList(
                "414",
                "181",
                "123",
                "818",
                "121",
                "313",
                "444",
                "664",
                "171",
                "191"
        );
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
    public void testMedium() throws Exception {
        PayBookReader reader = setupAndGetReaderInitializer("our_medium.xml");
        assertEquals(reader.getBiggestSpenders(), q3res);
        assertEquals(reader.getRichestSellers(), q4res);
        assertEquals(reader.getBiggestPaymentsFromClients(), q8res);
        assertEquals(reader.getBiggestPaymentsToSellers(), q7res);
    }

    @Test
    public void testMedium2() throws Exception {
        PayBookReader reader = setupAndGetReaderInitializer("our_medium.xml");
        assertTrue(reader.paidTo("151", "Poobar"));
        assertEquals(OptionalDouble.of(3), reader.getPayment("151", "Poobar"));
        assertFalse(reader.paidTo("414", "Poobar"));
        assertEquals(OptionalDouble.of(13), reader.getPayment("884", "123"));
        assertEquals(OptionalDouble.of(7), reader.getPayment("763", "Noobar"));
    }

    @Test
    public void testMedium3() throws FileNotFoundException {
        PayBookReader reader = setupAndGetReaderInitializer("our_medium.xml");
        assertEquals(Optional.of("123"), reader.getBiggestClient("Foobar"));
        assertEquals(Optional.of("884"), reader.getBiggestClient("123"));
        assertEquals(Optional.of("444"), reader.getBiggestClient("171"));
        assertEquals(Optional.of("313"), reader.getBiggestClient("Poobar"));
    }

}
