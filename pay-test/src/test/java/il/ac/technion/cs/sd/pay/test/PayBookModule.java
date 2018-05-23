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
        this.bind(PayBookInitializer.class).to(PayBookInitializerImpl.class);
        this.bind(PayBookReader.class).to(PayBookReaderImpl.class);
    }
}
