package il.ac.technion.cs.sd.pay.test;

import il.ac.technion.cs.sd.pay.app.PayBookReader;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static il.ac.technion.cs.sd.pay.test.TestUtils.mainTest;
import static il.ac.technion.cs.sd.pay.test.TestUtils.mapFrom;
import static il.ac.technion.cs.sd.pay.test.TestUtils.setupAndGetReader;
import static il.ac.technion.cs.sd.pay.test.TestUtils.toEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StaffTest {
  @Test
  public void paidTo() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertTrue(reader.paidTo("rVFp6", "b08wUI"));
      assertFalse(reader.paidTo("rvfP6", "b08wUI"));
      assertFalse(reader.paidTo("rVFp6", "b08Wui"));
    });
  }

  @Test
  public void getPayment() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals(9, (int) reader.getPayment("rYDEh", "bHRKG2ToyI").getAsDouble());
      assertFalse(reader.getPayment("rYDEh", "bhrkg2tOYi").isPresent());
      assertFalse(reader.getPayment("rydeH", "bAwySIk").isPresent());
    });
  }

  @Test
  public void getBiggestSpenders() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals(Arrays.asList(
      	"rCpOUA", "rbdPF13hR", "rfSHK", "rHKDEFLYb", 
      	"rHDF", "rQqB2JT", "rm4i", "rzr6ykBpp0", 
      	"r9u5h2E4Mb", "r4mx2d6b5"),
      		reader.getBiggestSpenders());
    });
  }

  @Test
  public void getRichestSellers() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals(Arrays.asList(
      	"bsHSgRd", "bJqRi9H", "bQ9ZwKP", "b5GWKksuJ", 
      	"bbJ7yD", "bEO8T", "b0Ybwadoc3", "bqCEr", 
      	"bcT1QzyFfT", "byiRiF"),
      		reader.getRichestSellers());
    });
  }

  @Test
  public void getFavoriteSeller() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals("b6y6ne",
              reader.getFavoriteSeller("rBFd").get());
      assertEquals("bZji3JH82k",
              reader.getFavoriteSeller("r2ksA").get());
    });
  }

  @Test
  public void getBiggestClient() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals("rBFd",
              reader.getBiggestClient("b6y6ne").get());
      assertEquals("rxM",
              reader.getBiggestClient("bevIWMya").get());
    });
  }

  @Test
  public void getBiggestPaymentsToSellers() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals(mapFrom(
          toEntry("bmmkd9", 11),
          toEntry("b00UopEej6", 10),
          toEntry("b01PIufO", 10),
          toEntry("b026xx", 10),
          toEntry("bb9FmZ7PQ0", 16),
          toEntry("bDtX", 18),
          toEntry("b958Thn", 15),
          toEntry("b01s", 10),
          toEntry("b007Ina95C", 10),
          toEntry("bN7vnMjYeq", 15)),
              reader.getBiggestPaymentsToSellers());
    });
  }

  @Test
  public void getBiggestPaymentsFromClients() throws Exception {
    PayBookReader reader = setupAndGetReader("medium.xml");
    mainTest(() -> {
      assertEquals(mapFrom(
          toEntry("roKNFqO", 15),
          toEntry("rUfaTuK0H", 11),
          toEntry("rTpJQeAuKz", 15),
          toEntry("r01Z", 10),
          toEntry("reZW", 18),
          toEntry("r03AC", 10),
          toEntry("r01iL7usa", 10),
          toEntry("rijl", 16),
          toEntry("r00gjjS", 10),
          toEntry("r04GT", 10)),
              reader.getBiggestPaymentsFromClients());
    });
  }
  
  @Test
  public void largeMixTest1() throws Exception {
    PayBookReader reader = setupAndGetReader("large.xml");
    mainTest(() -> {
      assertEquals("bfT3Vm",
              reader.getFavoriteSeller("r2M3Cx").get());
      assertEquals("rTArHt",
              reader.getBiggestClient("bT8zgT").get());
      assertEquals(4,
              (int) reader.getPayment("rAFO9xbkfi", "bwLBUB8ER").getAsDouble());
    });
  }


  @Test
  public void largeMixTest2() throws Exception {
    PayBookReader reader = setupAndGetReader("large.xml");
    mainTest(() -> {
      assertTrue(reader.paidTo("rUNsyE", "bsG597PO"));
      assertEquals(15,
              (int) reader.getPayment("reZW", "bR2snoTtsr").getAsDouble());
      assertEquals("bkm6R",
              reader.getFavoriteSeller("rx5kY").get());
    });
  }
}
