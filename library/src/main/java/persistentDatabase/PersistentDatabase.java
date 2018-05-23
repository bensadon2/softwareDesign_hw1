package persistentDatabase;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import structs.Payment;

import java.util.*;
import java.util.zip.DataFormatException;

public class PersistentDatabase {

    private final SecureDatabaseFactory SDBF;
    private SecureDatabase idSecureDatabase;
    private SecureDatabase amountSecureDatabase;

    protected PersistentDatabase() {
        this.SDBF = null;
    }

    @Inject
    public PersistentDatabase(SecureDatabaseFactory SBDF) {
        this.SDBF = SBDF;
    }

    /**
     * Makes an instance of the requested database.
     *
     * @param dbName - the requested database name
     */
    public void dbInstance(String dbName) {
        this.idSecureDatabase = this.SDBF.open(dbName + "Id");
        this.amountSecureDatabase = this.SDBF.open(dbName + "Amount");
    }

    public void saveToDb(Map<String, List<Payment>> data) {
        if (data == null) {
            return;
        }
        for (Map.Entry<String, List<Payment>> entry : data.entrySet()) {
            StringBuilder payemntIdsStrBuilder = new StringBuilder();
            StringBuilder paymentAmountsStrBuilder = new StringBuilder();
            try {
                for (Payment payment : entry.getValue()) {  // build separate lists, pad with 0 between to get 0-bytes.
                    payemntIdsStrBuilder.append(payment.getId()).append('\0');
                    paymentAmountsStrBuilder.append(payment.getValue()).append('\0');
                }

                this.idSecureDatabase.addEntry(entry.getKey().getBytes(), payemntIdsStrBuilder.toString().getBytes());
                this.amountSecureDatabase.addEntry(entry.getKey().getBytes(), paymentAmountsStrBuilder.toString().getBytes());
            } catch (DataFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("bad data for db");
            }
        }
    }

    /**
     * saves one user and his payments to the database. NOTE: will override previous entry completely.
     *
     * @param id                the user id
     * @param paymentCollection some collection containing Payment objects
     */
    public void saveToDb(String id, List paymentCollection) {
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
        } catch (Exception e) {
            throw new RuntimeException("bad data for db");
        }
    }

    public List<Payment> get(String id) {
        try {
            byte[] resIdBytes = this.idSecureDatabase.get(id.getBytes());
            byte[] resAmountBytes = this.amountSecureDatabase.get(id.getBytes());
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

    /**
     * @param queryCode
     * @return
     */
    public List<String> getQueryAnswer(String queryCode) {
        try {
            byte[] res = this.idSecureDatabase.get(queryCode.getBytes());
            String[] result = new String(res).split("\0");
            List<String> list = Arrays.asList(result);
            return list;
        } catch (InterruptedException e) {
            return null;
            // TODO: something with this exception
        }
    }
}
