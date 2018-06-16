package il.ac.technion.cs.sd.pay.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.pay.app.PayBookInitializer;
import il.ac.technion.cs.sd.pay.app.PayBookReader;

import java.io.File;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

class TestUtils {
  private TestUtils() { /* utility class */ }
  public interface ThrowingRunnable {
    void run() throws Exception;
  }

  public static <K, V> Map.Entry<K, V> toEntry(K k, V v) {
    return new AbstractMap.SimpleEntry(k, v);
  }

  public static <K, V> Map<K, V> mapFrom(Map.Entry<K, V>... entries) {
    Map<K, V> $ = new HashMap<>();
    for (Map.Entry<K, V> e : entries) {
      $.put(e.getKey(), e.getValue());
    }
    return $;
  }

  public static <T> T wait(Callable<T> runnable, Duration timeout) throws Exception {
    try {
      return CompletableFuture.supplyAsync(() -> {
        try {
          return runnable.call();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      // Since we wrap all exceptions, we need to unstack twice to get the root cause
      Throwable cause = e.getCause().getCause();
      // Fookin' Java
      if (cause instanceof Error)
        throw (Error) cause;
      else if (cause instanceof Exception)
        throw (Exception) cause;
      else
        throw new RuntimeException(e);
    }
  }

  public static void mainTest(ThrowingRunnable runnable) throws Exception {
    wait(() -> {
      runnable.run();
      return null;
    }, Duration.ofSeconds(40)); // 10 extra seconds as a safeguard
  }

  public static PayBookReader setupAndGetReader(String fileName) throws Exception {
    String fileContents =
        new Scanner(new File(StaffTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    assert !fileContents.isEmpty();
    return wait(() -> {
      // The injector isn't supposed to take any time to create its classes, but just in case...
      Injector $ = Guice.createInjector(new PayBookModule(), new TestingSecureDatabaseModule());
      $.getInstance(PayBookInitializer.class).setup(fileContents);
      return $.getInstance(PayBookReader.class);
    }, Duration.ofMinutes(3)); // 3 minutes should be enough setup time even for the large files

  }

}
