package network;

import fileHandling.XmlWriter;
import org.xml.sax.SAXException;
import fileHandling.XmlHandler;
import transaction.Transaction;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

public class Terminal {
    private static Logger theLogger =
            Logger.getLogger(Terminal.class.getName());

    public static void main(String[] args)  {
        try{
            Terminal socketClientExample = new Terminal();
            socketClientExample.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, InterruptedException, TransformerException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket;
        ObjectOutputStream oos ;
        ObjectInputStream ois ;
        XmlHandler xmlHandler = new XmlHandler();
        List transactionList = xmlHandler.parseXmlFile();
        Boolean connection = true;
        while (connection) {
            for (Object transactionObject : transactionList) {
                socket = new Socket(host.getHostName(), 9876);
                oos = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Sending request to Socket Server");
                oos.writeObject(transactionObject);
                oos.flush();
                Transaction transaction = (Transaction)transactionObject;
                //read the server response message
                ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message from server is : " + message);
                XmlWriter xmlWriter = new XmlWriter();
                xmlWriter.saveTransactionResult(transaction.getTransactionId() , message);
                //close resources
                //ois.close();
                //oos.close();
                Thread.sleep(2500);
            }
            connection=false;
        }
    }

}