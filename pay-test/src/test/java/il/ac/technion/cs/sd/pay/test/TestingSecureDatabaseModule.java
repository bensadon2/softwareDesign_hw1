package il.ac.technion.cs.sd.pay.test;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

class TestingSecureDatabaseModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(SecureDatabaseFactory.class).to(SecureDatabaseFactoryImpl.class).in(Singleton.class);
  }
}
