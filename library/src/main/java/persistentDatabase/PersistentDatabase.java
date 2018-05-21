package persistentDatabase;

import java.util.List;
import java.util.Map;

public class PersistentDatabase {
    /**
     * Makes an instance of the requested database. TODO returns it? not sure this method is needed
     *
     * @param dbName - the requested database name
     */
    public void dbInstance(String dbName) {
        throw new UnsupportedOperationException("not implemented");
        // use external "open" method here
    }

    public void saveToDb(Map<String, List<byte[]>> data) {
        throw new UnsupportedOperationException("not implemented");
    }
}
