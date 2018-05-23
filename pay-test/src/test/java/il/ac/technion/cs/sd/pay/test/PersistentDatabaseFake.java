package il.ac.technion.cs.sd.pay.test;

import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentDatabaseFake extends PersistentDatabase {

    private Map<String, List> db;
    public static Map<String, Map<String, List>> dbMap = new HashMap<>();

    PersistentDatabaseFake() {
        db = new HashMap<>();
    }

    @Override
    public void dbInstance(String dbName) {
        if (!dbMap.containsKey(dbName)) {
            dbMap.put(dbName, db);
        }
        this.db = dbMap.get(dbName);
    }

    @Override
    public void saveToDb(Map<String, List<Payment>> data) {
        this.db.putAll(data);
    }

    @Override
    public void saveToDb(String id, List paymentCollection) {
        this.db.put(id, paymentCollection);
    }

    @Override
    public List<Payment> get(String id) {
        return this.db.get(id);
    }

    @Override
    public List<String> getQueryAnswer(String queryCode) {
        return this.db.get(queryCode);
    }
}
