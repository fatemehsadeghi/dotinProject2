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
            Terminal terminalSocket  = new Terminal();
            terminalSocket.run();
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
        ObjectOutputStream objectOutputStream ;
        ObjectInputStream objectInputStream ;
        XmlHandler xmlHandler = new XmlHandler();
        List transactionList = xmlHandler.parseXmlFile();
        Boolean connection = true;
        while (connection) {
            for (Object transactionObject : transactionList) {
                socket = new Socket(host.getHostName(), 8080);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Sending request to Socket Server");
                objectOutputStream.writeObject(transactionObject);
                objectOutputStream.flush();
                Transaction transaction = (Transaction)transactionObject;
                //read the server response message
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String message = (String) objectInputStream.readObject();
                System.out.println("Message from server is : " + message);
                XmlWriter xmlWriter = new XmlWriter();
                xmlWriter.saveTransactionResult(transaction.getTransactionId() , message);
                //close resources
                objectInputStream.close();
                objectOutputStream.close();
                Thread.sleep(2500);
            }
            connection=false;
        }
    }

}