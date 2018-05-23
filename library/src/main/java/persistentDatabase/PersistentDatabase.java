package persistentDatabase;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import structs.Payment;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;

public class PersistentDatabase {

    private final SecureDatabaseFactory SDBF;
    //    private SecureDatabase idSecureDatabase;
    private SecureDatabase idSecureDatabase;
    private SecureDatabase amountSecureDatabase;

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
        this.idSecureDatabase = this.SDBF.open(dbName + "Id");
        this.amountSecureDatabase = this.SDBF.open(dbName + "Amount");
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
//                this.idSecureDatabase.addEntry(entry.getKey().getBytes(), concatList);
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
            StringBuilder payemntIdsStrBuilder = new StringBuilder();
            StringBuilder paymentAmountsStrBuilder = new StringBuilder();
            try {
                for (Payment payment : entry.getValue()) {  // build seperate lists, pad with 0 between to get 0-bytes.
                    payemntIdsStrBuilder.append(payment.getId()).append('\0');
                    paymentAmountsStrBuilder.append(payment.getValue()).append('\0');
                }
//                List<byte[]> byteList = entry.getValue().stream().map(Payment::toBytes).collect(Collectors.toList());
//                byte[] byteArrFromList = concatList(byteList);
//                byte[] byteArrFromList = Payment.payListToBytes(entry.getValue());
//                ArrayList<Payment> serializableList = new ArrayList<>(entry.getValue());
//                byte[] bytes = SerializationUtils.serialize(serializableList);

                this.idSecureDatabase.addEntry(entry.getKey().getBytes(), payemntIdsStrBuilder.toString().getBytes());
                this.amountSecureDatabase.addEntry(entry.getKey().getBytes(), paymentAmountsStrBuilder.toString().getBytes());
            } catch (DataFormatException e) {
                e.printStackTrace();
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
////                this.idSecureDatabase.addEntry(key, value);
////            } catch (DataFormatException e) {
////                // TODO: something with this exception
////            }
////        }
////    }

//    public <T extends Collection<String> & Serializable> void saveToDb(String id, T paymentCollection) {
//        try {
//            this.idSecureDatabase.addEntry(id.getBytes(), SerializationUtils.serialize(paymentCollection));
//        } catch (DataFormatException e) {
//            // TODO: something with exception
//        }
//    }

    // TODO: 22-May-18 change this to not use serialize
//    public <T extends Collection & Serializable> void saveToDb(String id, T paymentCollection) {
//        try {
//            this.idSecureDatabase.addEntry(id.getBytes(), SerializationUtils.serialize(paymentCollection));
//        } catch (DataFormatException e) {
//            // TODO: something with exception
//            throw new RuntimeException("bad data for db");
//        }
//    }

    /**
     * saves one user and his payments to the database. NOTE: will override previous entry completely.
     * @param id the user id
     * @param paymentCollection some collection containing Payment objects
     */
    public void saveToDb(String id, Collection paymentCollection) {
        StringBuilder paymentIdsStrBuilder = new StringBuilder();
        StringBuilder paymentAmountsStrBuilder = new StringBuilder();
        for (Object o : paymentCollection) {
            Payment payment = (Payment) o;
            paymentIdsStrBuilder.append(payment.getId()).append('\0');
            paymentAmountsStrBuilder.append(payment.getValue()).append('\0');
        }
        try {
            this.idSecureDatabase.addEntry(id.getBytes(), paymentIdsStrBuilder.toString().getBytes());
            this.amountSecureDatabase.addEntry(id.getBytes(), paymentAmountsStrBuilder.toString().getBytes());
//            byte[] bytes = Payment.payListToBytes(paymentCollection);
//            this.idSecureDatabase.addEntry(id.getBytes(), SerializationUtils.serialize(paymentCollection));
        } catch (Exception e) {
            throw new RuntimeException("bad data for db");
        }
    }

//    public List<byte[]> get(String id) {
//        throw new UnsupportedOperationException("not implemented");
//    }

    public List<Payment> get(String id) {
        try {
            byte[] resIdBytes = this.idSecureDatabase.get(id.getBytes());
            byte[] resAmountBytes = this.amountSecureDatabase.get(id.getBytes());
//            ArrayList<Payment> result = Payment.bytesToPayList(resIdBytes);
            ArrayList<Payment> result = new ArrayList<>();

            String idStr = new String(resIdBytes);
            String amountStr = new String(resAmountBytes);
            String[] idStrings = idStr.split("\0");
            String[] amountStrings = amountStr.split("\0");
            if (idStrings.length != amountStrings.length) {
                throw new IllegalStateException("got different size lists of amounts and IDs in DB entry");
            }

            for (int i = 0; i < idStrings.length; i++) {
                result.add(new Payment(idStrings[i], Integer.valueOf(amountStrings[i])));
            }

            return result;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            System.out.println("PersistentDatabase.get found no matching element for \"" + id + "\"");
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("got InterruptedException from SecureDatabase.get");
            return null;
        }
    }

//    public Set<Pair<String,Integer>> getSet(String id) {
//        try {
//            byte[] res = this.idSecureDatabase.get(id.getBytes());
//            return SerializationUtils.deserialize(res);
//        } catch (InterruptedException e) {
//            return null;
//            // TODO: something with this exception
//        }
//    }

    public List<String> getIds(String id) {
        try {
            byte[] res = this.idSecureDatabase.get(id.getBytes());
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
