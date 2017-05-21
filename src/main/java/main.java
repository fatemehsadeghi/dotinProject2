import network.Terminal;
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
        Terminal terminal = new Terminal();
        try {
            terminalSocket.run(terminalName ,responseFile);
            terminal.run(terminalName1 , responseFile1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
