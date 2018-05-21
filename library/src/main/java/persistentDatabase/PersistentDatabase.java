package persistentDatabase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class PersistentDatabase {

    private final SecureDatabaseFactory SDBF;
    private SecureDatabase secureDatabase;

    @Inject
    public PersistentDatabase(@Named("SecureDatabaseFactory") SecureDatabaseFactory SBDF) {
        this.SDBF = SBDF;
    }

    /**
     * Makes an instance of the requested database. TODO returns it? not sure this method is needed
     *
     * @param dbName - the requested database name
     */
    public void dbInstance(String dbName) {
        this.secureDatabase = this.SDBF.open(dbName);
//        throw new UnsupportedOperationException("not implemented");
        // use external "open" method here
    }

    private byte[] concatList(List<byte[]> list) {
        byte[] res = new byte[]{};
        for (byte[] entry : list) {
            res = ArrayUtils.addAll(res, entry);
        }
        return res;
    }

    public void saveToDb(Map<String, List<byte[]>> data) {
        for (Map.Entry<String, List<byte[]>> entry : data.entrySet()) {
            byte[] concatList = concatList(entry.getValue());
            try {
                this.secureDatabase.addEntry(entry.getKey().getBytes(), concatList);
            } catch (DataFormatException e) {
                // TODO: do something about a list too long
            }
        }
//        throw new UnsupportedOperationException("not implemented");
    }
}
