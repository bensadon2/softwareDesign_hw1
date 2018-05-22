import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;

public class FakeSecureDatabaseFactory implements SecureDatabaseFactory {
    private class FakeSecureDatabase implements SecureDatabase{

        private HashMap<byte[], byte[]> map;

        FakeSecureDatabase() {
            this.map = new HashMap<>();
        }

        @Override
        public void addEntry(byte[] key, byte[] value) throws DataFormatException {
            if (value.length > 100) throw new DataFormatException();
            map.put(key,value);
        }

        @Override
        public byte[] get(byte[] key) throws InterruptedException {
            if (!map.containsKey(key)) throw new NoSuchElementException();
            return map.get(key);
        }
    }

    private HashMap<String, FakeSecureDatabase> dbmap;

    public FakeSecureDatabaseFactory() {
        this.dbmap = new HashMap<>();
    }

    @Override
    public SecureDatabase open(String s) {
        if (!dbmap.containsKey(s)){
            dbmap.put(s, new FakeSecureDatabase());
        }
        return dbmap.get(s);
    }
}
