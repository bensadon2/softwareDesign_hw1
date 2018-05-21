package il.ac.technion.cs.sd.pay.app;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public interface PayBookInitializer {
  /** Saves the XML data persistently, so that it could be run using PayBookReader. */
  void setup(String xmlData);
}

