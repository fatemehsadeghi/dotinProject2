import network.Terminal;
import network.Terminal1;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class main {
    public static void main(String args[]){
        String terminalName = "terminal";
        String responseFile = "response";
        String terminalName1 = "terminal1";
         String responseFile1 = "response1";
        Terminal terminalSocket = new Terminal();
        Terminal1 terminal1 = new Terminal1();

            terminalSocket.start();
            terminal1.start();

    }
}
