package il.ac.technion.cs.sd.pay.test;

import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import java.util.*;
import java.util.zip.DataFormatException;

class SecureDatabaseFactoryImpl implements SecureDatabaseFactory {
  private static class SecureDatabaseImpl implements SecureDatabase {
    private final Map<String, String> map = new HashMap<>();

    /** Adds a new security entry. If the key already exists then it is replaced. */
    public void addEntry(byte[] key, byte[] value) throws DataFormatException {
      if(value.length > 100)
        throw new DataFormatException();
      map.put(new String(key), new String(value));
    }

    /**
     * Returns the contents of the entry that was stored with the key k.
     * @throws java.util.NoSuchElementException If the key doesn't exist.
     */
    public byte[] get(byte[] key) throws InterruptedException {
      String strValue = map.get(new String(key));
      if(null == strValue)
        throw new NoSuchElementException();
      byte[] value = strValue.getBytes();
      Thread.sleep(value.length);
      return value;
    }
  }

  private final Map<String, SecureDatabase> dbs = new HashMap<>();

  // this really should have thrown an interrupted exception :| oh well
  @Override
  public SecureDatabase open(String dbName) {
    try {
      Thread.sleep(dbs.size() * 100);
      if (!dbs.containsKey(dbName))
        dbs.put(dbName, new SecureDatabaseImpl());
      return dbs.get(dbName);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
