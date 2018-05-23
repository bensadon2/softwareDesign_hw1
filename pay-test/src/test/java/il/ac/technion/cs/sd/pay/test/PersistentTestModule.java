package il.ac.technion.cs.sd.pay.test;


import PayBookImplementations.PayBookInitializerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import org.mockito.Mockito;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.Arrays;

public class PersistentTestModule extends AbstractModule {
    protected void configure() {
        //add configuration logic here
//        bind(PersistentDatabase.class).to(provideMockPDB().getClass());
    }

    @Provides
    PersistentDatabase provideMockPDB() {
        PersistentDatabase mock = Mockito.mock(PersistentDatabase.class);
        Mockito.when(mock.get(PayBookInitializerImpl.topPaymentsClients)).thenReturn(Arrays.asList(new Payment("Foobar", 10), new Payment("Moobar", 7)));
//        Mockito.when(mock.get(PayBookInitializerImpl.topClients)).thenReturn(Arrays.asList(new Payment("Foobar", 10), new Payment("Moobar", 7)));
        Mockito.when(mock.getQueryAnswer(PayBookInitializerImpl.topClients)).thenReturn(Arrays.asList("Foobar", "Lol"));
        return mock;
    }
}
