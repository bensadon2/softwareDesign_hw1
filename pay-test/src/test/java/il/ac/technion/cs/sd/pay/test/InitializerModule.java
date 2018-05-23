package il.ac.technion.cs.sd.pay.test;

import com.google.inject.AbstractModule;
import persistentDatabase.PersistentDatabase;

public class InitializerModule extends AbstractModule {

    protected void configure() {
        bind(PersistentDatabase.class).to(PersistentDatabaseFake.class);
        //add configuration logic here
    }
}
