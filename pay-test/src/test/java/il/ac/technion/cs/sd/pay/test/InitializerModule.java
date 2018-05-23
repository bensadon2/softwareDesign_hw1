package il.ac.technion.cs.sd.pay.test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.util.Pair;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import persistentDatabase.PersistentDatabase;
import structs.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class InitializerModule extends AbstractModule {

//    @Mock
//    PersistentDatabase myDb;

    //    Map<String, List<Payment>> clientDb = new HashMap<>();
//    Map<String, List<Payment>> sellerDb = new HashMap<>();


    //    @Provides
//    Pair<PersistentDatabase, Map<String, List>> makeMock() {
//        PersistentDatabase myDb = Mockito.mock(PersistentDatabase.class);
//        Map<String, List> db = new HashMap<>();
//
//        Mockito.doNothing().when(myDb).dbInstance(any(String.class));
//
//        Mockito.doAnswer(invocation -> db.putAll(invocation.getArgument(0)))
//                .when(myDb).saveToDb(any(Map.class));
//
//        Mockito.doAnswer(
//                invocation -> db.put(invocation.getArgument(0), invocation.getArgument(1))
//        ).when(myDb).saveToDb(any(String.class), any(List.class));
//
//        when(myDb.get(any(String.class))).then(arg ->
//        {
//            String key = arg.getArgument(0);
//            if (db.containsKey(key)) {
//                return db.get(key);
//            }
//            throw new NoSuchElementException();
//        });
//
//        when(myDb.getQueryAnswer(any(String.class))).then(arg ->
//        {
//            String key = arg.getArgument(0);
//            if (db.containsKey(key)) {
//                return db.get(key);
//            }
//            throw new NoSuchElementException();
//        });
//        return new Pair<>(myDb, db);
//    }

    protected void configure() {
        bind(PersistentDatabase.class).to(PersistentDatabaseFake.class);
        //add configuration logic here
    }
}
