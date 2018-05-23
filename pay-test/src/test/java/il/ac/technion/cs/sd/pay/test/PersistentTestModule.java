package il.ac.technion.cs.sd.pay.test;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.mockito.Mockito;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.Arrays;
import java.util.List;

import static PayBookImplementations.PayBookInitializerImpl.*;

public class PersistentTestModule extends AbstractModule {

    private static final List<Payment> TOP_PAY_CLINTS_LIST = Arrays.asList(new Payment("Foobar", 10), new Payment("Moobar", 7));
    private static final List<String> TOP_CLIENTS_LIST = Arrays.asList("Foobar", "Lol");
    private static final List<Payment> PAID_TO_LIST = Arrays.asList(new Payment("joey", 1), new Payment("chandler", 2), new Payment("monica", 3));

    protected void configure() {
    }

    @Provides
    PersistentDatabase provideMockPDB() {
        PersistentDatabase mock = Mockito.mock(PersistentDatabase.class);
        Mockito.when(mock.get(topPaymentsClients)).thenReturn(TOP_PAY_CLINTS_LIST);
        Mockito.when(mock.getQueryAnswer(topClients)).thenReturn(TOP_CLIENTS_LIST);
        Mockito.when(mock.get("paidTo")).thenReturn(PAID_TO_LIST);

        return mock;
    }
}
