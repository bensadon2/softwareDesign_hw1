import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;
import org.mockito.Mockito;
import persistentDatabase.PersistentDatabase;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SecureDatabaseFactory.class).toInstance(SDBF -> {
            SecureDatabase mockDb = Mockito.mock(SecureDatabase.class);
            // TODO: 22-May-18 use something like this for mocking?
//            Mockito.when(mockDb.get());
            return mockDb;
        });

        // TODO: 22-May-18 how to bind to the PersistentDatabase mock?
        bind(PersistentDatabase.class).to(provideMockPDB().getClass());
    }

    @Provides
    PersistentDatabase provideMockPDB(){
        PersistentDatabase mock = Mockito.mock(PersistentDatabase.class);
//        Mockito.when()
        return mock;
    }

}
