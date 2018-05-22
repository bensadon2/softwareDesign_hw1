package persistentDatabase;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import structs.Payment;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class PersistentDatabase {

    private final SecureDatabaseFactory SDBF;
    private SecureDatabase secureDatabase;

    @Inject
    public PersistentDatabase(/*@Named("SecureDatabaseFactory")*/ SecureDatabaseFactory SBDF) {
        this.SDBF = SBDF;
    }

    // TODO: 22-May-18  it only allows one instance of SecureDB. mention it somehere?
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

//    public void saveToDbByte(Map<String, List<byte[]>> data) {
//        for (Map.Entry<String, List<byte[]>> entry : data.entrySet()) {
//            byte[] concatList = concatList(entry.getValue());
//            try {
//                this.secureDatabase.addEntry(entry.getKey().getBytes(), concatList);
//            } catch (DataFormatException e) {
//                // TODO: do something about a list too long
//                throw new RuntimeException("bad data for db");
//            }
//        }
////        throw new UnsupportedOperationException("not implemented");
//    }

    public void saveToDb(Map<String, List<Payment>> data) {
        if (data == null) {
            return;
        }
        for (Map.Entry<String, List<Payment>> entry : data.entrySet()) {
            List<byte[]> byteList = entry.getValue().stream().map(Payment::toBytes).collect(Collectors.toList());
            byte[] byteArrFromList = concatList(byteList);
//            ArrayList<Payment> serializableList = new ArrayList<>(entry.getValue());
//            byte[] bytes = SerializationUtils.serialize(serializableList);
            try {
                this.secureDatabase.addEntry(entry.getKey().getBytes(), byteArrFromList);
            } catch (DataFormatException e) {
                // TODO: do something about a list too long
                throw new RuntimeException("bad data for db");
            }
        }
//        throw new UnsupportedOperationException("not implemented");
    }

//    public void saveToDb2(Map<String, Integer> data) {
////        for (Map.Entry<String, Integer> entry : data.entrySet()) {
////            byte[] value = entry.getValue().toString().getBytes();
////            byte[] key = entry.getKey().getBytes();
////            try {
////                this.secureDatabase.addEntry(key, value);
////            } catch (DataFormatException e) {
////                // TODO: something with this exception
////            }
////        }
////    }

//    public <T extends Collection<String> & Serializable> void saveToDb(String id, T paymentCollection) {
//        try {
//            this.secureDatabase.addEntry(id.getBytes(), SerializationUtils.serialize(paymentCollection));
//        } catch (DataFormatException e) {
//            // TODO: something with exception
//        }
//    }

    // TODO: 22-May-18 change this to not use serialize
    public <T extends Collection & Serializable> void saveToDb(String id, T paymentCollection) {
        try {
            this.secureDatabase.addEntry(id.getBytes(), SerializationUtils.serialize(paymentCollection));
        } catch (DataFormatException e) {
            // TODO: something with exception
            throw new RuntimeException("bad data for db");
        }
    }

//    public List<byte[]> get(String id) {
//        throw new UnsupportedOperationException("not implemented");
//    }

    public List<Payment> get(String id) {
        try {
            byte[] res = this.secureDatabase.get(id.getBytes());
            return SerializationUtils.<ArrayList<Payment>>deserialize(res);
        } catch (InterruptedException e) {
            return null;
            // TODO: something with this exception
        }
//        throw new UnsupportedOperationException("not implemented");
    }

    public Set<Pair<String,Integer>> getSet(String id) {
        try {
            byte[] res = this.secureDatabase.get(id.getBytes());
            return SerializationUtils.deserialize(res);
        } catch (InterruptedException e) {
            return null;
            // TODO: something with this exception
        }
    }

    public List<String> getIds(String id) {
        try {
            byte[] res = this.secureDatabase.get(id.getBytes());
            return SerializationUtils.deserialize(res);
        } catch (InterruptedException e) {
            return null;
            // TODO: something with this exception
        }
    }

//    public List<List<byte[]>> getAllValues() {
//        throw new UnsupportedOperationException("not implemented");
//    }
//
//    public List<byte[]> getAllKeys() {
//        throw new UnsupportedOperationException("not implemented");
//    }
}
