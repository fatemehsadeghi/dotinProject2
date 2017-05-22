package network;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Terminal1 extends Thread {
    private static String terminalName = "terminal1";
    private static String responseFile = "response1";

    public static void main(String[] args) throws InterruptedException, ParserConfigurationException, TransformerException, IOException, SAXException, ClassNotFoundException {
        Terminal terminal = new Terminal();
        terminal.run(terminalName, responseFile);
    }
}
