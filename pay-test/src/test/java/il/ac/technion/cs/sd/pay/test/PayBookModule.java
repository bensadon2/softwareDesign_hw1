package il.ac.technion.cs.sd.pay.test;

import PayBookImplementations.PayBookInitializerImpl;
import PayBookImplementations.PayBookReaderImpl;
import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import il.ac.technion.cs.sd.pay.app.PayBookReader;


// This module is in the testing project, so that it could easily bind all dependencies from all levels.
class PayBookModule extends AbstractModule {
    @Override
    protected void configure() {

//        install(new SecureDatabaseModule());

//        Injector injector = Guice.createInjector(new SecureDatabaseModule());
//        injector.getInstance(SecureDatabaseFactory.class);

        this.bind(PayBookInitializer.class).to(PayBookInitializerImpl.class);
        this.bind(PayBookReader.class).to(PayBookReaderImpl.class);
//        this.bind(PersistentDatabase.class).annotatedWith(Names.named("dbByClients")).toInstance(new PersistentDatabase(injector.getInstance(SecureDatabaseFactory.class)));
//        this.bind(PersistentDatabase.class).annotatedWith(Names.named("dbByClients")).toInstance(new PersistentDatabase(new SecureDatabaseFactory()));
//        this.bind(PersistentDatabase.class).annotatedWith(Names.named("dbByClients")).to(PersistentDatabase.class);
//        this.bind(PersistentDatabase.class).annotatedWith(Names.named("dbBySellers")).toInstance(new PersistentDatabase(injector.getInstance(SecureDatabaseFactory.class)));
        // TODO: need to inject a secureDatabaseFactory here somehow, or get binding from other module?
//    throw new UnsupportedOperationException("Not implemented");
    }
}
