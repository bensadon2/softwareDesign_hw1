import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

public class LibTestModule extends AbstractModule {
    protected void configure() {
        //add configuration logic here
        bind(SecureDatabaseFactory.class).to(FakeSecureDatabaseFactory.class);
    }
}
