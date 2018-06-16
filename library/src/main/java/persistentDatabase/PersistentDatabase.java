package persistentDatabase;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.pay.ext.SecureDatabase;
import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

import structs.Payment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static foo.spliceUtil.chunkArray;
import static foo.spliceUtil.getChunkNum;

public class PersistentDatabase {

    private final SecureDatabaseFactory SDBF;
    private SecureDatabase idLengthSecureDatabase;
    private SecureDatabase idChunksSecureDatabase;
    private SecureDatabase amountLengthSecureDatabase;
    private SecureDatabase amountChunkSecureDatabase;

    private final Integer chunkSize = 100;

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
        this.idLengthSecureDatabase = this.SDBF.open(dbName + "Id" + "Length");
        this.idChunksSecureDatabase = this.SDBF.open(dbName + "Id" + "Chunks");
        this.amountLengthSecureDatabase = this.SDBF.open(dbName + "Amount" + "Length");
        this.amountChunkSecureDatabase = this.SDBF.open(dbName + "Amount" + "Chunk");
    }

    public void saveToDb(Map<String, List<Payment>> data) {
        if (data == null) {
            return;
        }
        for (Map.Entry<String, List<Payment>> entry : data.entrySet()) {
            StringBuilder paymentIdsStrBuilder = new StringBuilder();
            StringBuilder paymentAmountsStrBuilder = new StringBuilder();
            try {

                for (Payment payment : entry.getValue()) {  // build separate lists, pad with 0 between to get 0-bytes.
                    paymentIdsStrBuilder.append(payment.getId()).append('\0');
                    paymentAmountsStrBuilder.append(payment.getValue()).append('\0');
                }

                byte[] paymentIdsBytes = paymentIdsStrBuilder.toString().getBytes();
                byte[] paymentAmountsBytes = paymentAmountsStrBuilder.toString().getBytes();

                Integer idChunkNum = getChunkNum(paymentIdsBytes, chunkSize);
                Integer amountChunkNum = getChunkNum(paymentAmountsBytes, chunkSize);

                byte[][] idChunks = chunkArray(paymentIdsBytes, chunkSize);
                byte[][] amountChunks = chunkArray(paymentAmountsBytes, chunkSize);

                idLengthSecureDatabase.addEntry(entry.getKey().getBytes(), idChunkNum.toString().getBytes());
                amountLengthSecureDatabase.addEntry(entry.getKey().getBytes(), amountChunkNum.toString().getBytes());

                for (int i = 0; i < idChunks.length; i++) {
                    idChunksSecureDatabase.addEntry(getSubKey(entry.getKey(), i), idChunks[i]);
                }

                for (int i = 0; i < amountChunks.length; i++) {
                    amountChunkSecureDatabase.addEntry(getSubKey(entry.getKey(), i), amountChunks[i]);
                }
//                this.idLengthSecureDatabase.addEntry(entry.getKey().getBytes(), payemntIdsStrBuilder.toString().getBytes());
//                this.amountLengthSecureDatabase.addEntry(entry.getKey().getBytes(), paymentAmountsStrBuilder.toString().getBytes());

                paymentIdsStrBuilder.setLength(0);
                paymentAmountsStrBuilder.setLength(0);

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
    public void saveToDb(String id, List<Payment> paymentCollection) {
        StringBuilder paymentIdsStrBuilder = new StringBuilder();
        StringBuilder paymentAmountsStrBuilder = new StringBuilder();
        for (Object o : paymentCollection) {
            Payment payment = (Payment) o;
            paymentIdsStrBuilder.append(payment.getId()).append('\0');
            paymentAmountsStrBuilder.append(payment.getValue()).append('\0');
        } // TODO: 13-Jun-18 Split every 100 bytes into a value and collect to map of key_i, value

        byte[] paymentIdsBytes = paymentIdsStrBuilder.toString().getBytes();
        byte[] paymentAmountsBytes = paymentAmountsStrBuilder.toString().getBytes();

        Integer idChunkNum = getChunkNum(paymentIdsBytes, chunkSize);
        Integer amountChunkNum = getChunkNum(paymentAmountsBytes, chunkSize);

        byte[][] idChunks = chunkArray(paymentIdsBytes, chunkSize);
        byte[][] amountChunks = chunkArray(paymentAmountsBytes, chunkSize);

        try {

            idLengthSecureDatabase.addEntry(id.getBytes(), idChunkNum.toString().getBytes());
            amountLengthSecureDatabase.addEntry(id.getBytes(), amountChunkNum.toString().getBytes());

            for (int i = 0; i < idChunks.length; i++) {
                idChunksSecureDatabase.addEntry(getSubKey(id, i), idChunks[i]);
            }

            for (int i = 0; i < amountChunks.length; i++) {
                amountChunkSecureDatabase.addEntry(getSubKey(id, i), amountChunks[i]);
            }

//            this.idLengthSecureDatabase.addEntry(id.getBytes(), paymentIdsStrBuilder.toString().getBytes());
//            this.amountLengthSecureDatabase.addEntry(id.getBytes(), paymentAmountsStrBuilder.toString().getBytes());
        } catch (Exception e) {
            throw new RuntimeException("bad data for db");
        }
    }

    public void saveToDb2(String id, List<String> idCollection) {
        StringBuilder idsStrBuilder = new StringBuilder();
        for (Object o : idCollection) {
            String curId = (String) o;
            idsStrBuilder.append(curId).append('\0');
        }
        try {
            byte[] idsBytes = idsStrBuilder.toString().getBytes();
//            byte[] paymentAmountsBytes = paymentAmountsStrBuilder.toString().getBytes();

            Integer idChunkNum = getChunkNum(idsBytes, chunkSize);
//            Integer amountChunkNum = getChunkNum(paymentAmountsBytes, chunkSize);

            byte[][] idChunks = chunkArray(idsBytes, chunkSize);
//            byte[][] amountChunks = chunkArray(paymentAmountsBytes, chunkSize);

            idLengthSecureDatabase.addEntry(id.getBytes(), idChunkNum.toString().getBytes());
//            amountLengthSecureDatabase.addEntry(id.getBytes(), amountChunkNum.toString().getBytes());

            for (int i = 0; i < idChunks.length; i++) {
                idChunksSecureDatabase.addEntry(getSubKey(id, i), idChunks[i]);
            }
//
//            for (int i = 0; i < amountChunks.length; i++) {
//                amountChunkSecureDatabase.addEntry((id + '_' + i).getBytes(), amountChunks[i]);
//            }

//            this.idLengthSecureDatabase.addEntry(id.getBytes(), idsStrBuilder.toString().getBytes());
        } catch (Exception e) {
            throw new RuntimeException("bad data for db");
        }
    }

    public List<Payment> get(String id) {
        try {
            byte[] idBytes = id.getBytes();
            byte[] idLengthBytes = this.idLengthSecureDatabase.get(idBytes);
            String idLengthString = new String(idLengthBytes);
            Integer idChunkNum = Integer.valueOf(idLengthString);
            byte[] amountChunkNumBytes = this.amountLengthSecureDatabase.get(idBytes);
            String amountChunkNumString = new String(amountChunkNumBytes);
            Integer amountChunkNum = Integer.valueOf(amountChunkNumString);
            List<Payment> result = new ArrayList<>();

            StringBuilder ids = new StringBuilder();
            StringBuilder amounts = new StringBuilder();

            for (int i = 0; i < idChunkNum; i++) {
                ids.append(new String(idChunksSecureDatabase.get(getSubKey(id, i))));
            }

            for (int i = 0; i < amountChunkNum; i++) {
                amounts.append(new String(amountChunkSecureDatabase.get(getSubKey(id, i))));
            }

            String[] idArray = ids.toString().split("\0");
            Integer[] amountArray = Arrays.stream(amounts.toString().split("\0")).map(Integer::valueOf).toArray(Integer[]::new);

            // TODO: 13-Jun-18 Remove assert or not it's all a lost cause
            assert (idArray.length == amountArray.length);

            for (int i = 0; i < idArray.length; i++) {
                result.add(new Payment(idArray[i], amountArray[i]));
            }

//            byte[] amountLengthBytes = this.amountLengthSecureDatabase.get(id.getBytes());

//            ArrayList<Payment> result = new ArrayList<>();
//
//            String idStr = new String(resIdBytes);
//            String amountStr = new String(resAmountBytes);
//            String[] idStrings = idStr.split("\0");
//            String[] amountStrings = amountStr.split("\0");
//            if (idStrings.length != amountStrings.length) {
//                throw new IllegalStateException("got different size lists of amounts and IDs in DB entry");
//            }
//
//            for (int i = 0; i < idStrings.length; i++) {
//                result.add(new Payment(idStrings[i], Integer.valueOf(amountStrings[i])));
//            }

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

    public List<String> getQueryAnswer(String queryCode) {
        try {
//            byte[] res = this.idLengthSecureDatabase.get(queryCode.getBytes());
            byte[] length = this.idLengthSecureDatabase.get(queryCode.getBytes());
            String lengthStr = new String(length);
            Integer lengthInt = Integer.valueOf(lengthStr);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lengthInt; i++) {
                sb.append(new String(idChunksSecureDatabase.get(getSubKey(queryCode, i))));
            }
            String[] result = sb.toString().split("\0");
            return Arrays.asList(result);
        } catch (InterruptedException e) {
            return null;
            // TODO: something with this exception
        }
    }

    private byte[] getSubKey(String key, Integer index) {
        return (key + '_' + index).getBytes();
    }
}
