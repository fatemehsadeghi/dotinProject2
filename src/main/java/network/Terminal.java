package network;

import fileHandling.XmlHandler;
import fileHandling.XmlWriter;
import logHandling.LogHandler;
import org.xml.sax.SAXException;
import transaction.Transaction;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Terminal {
    private String logMessage;
    private LogHandler logHandler = new LogHandler();

    public static void main(String[] args) {
        try {
            Terminal terminalSocket = new Terminal();
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
        ArrayList<String> terminalAttributeList = new ArrayList<String>();
        InetAddress host = InetAddress.getLocalHost();
        Socket socket;
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;
        XmlHandler xmlHandler = new XmlHandler();
        List transactionList = xmlHandler.parseXmlFile(terminalAttributeList);
        String hostName = terminalAttributeList.get(2);
        System.out.println(hostName);
        int port = Integer.parseInt(terminalAttributeList.get(3));
        System.out.println(port);
        Boolean connection = true;
        Map<String, String> resultMap = new TreeMap<String, String>();
        while (connection) {
            for (Object transactionObject : transactionList) {
                socket = new Socket(hostName , port);
                logMessage = "client is connecting!";
                logHandler.writeToLogFile(logMessage);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Sending request to Socket Server");
                logMessage = "Sending request to Socket Server";
                logHandler.writeToLogFile(logMessage);
                objectOutputStream.writeObject(transactionObject);
                objectOutputStream.flush();
                Transaction transaction = (Transaction) transactionObject;
                //read the server response message
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String message = (String) objectInputStream.readObject();
                System.out.println("Message from server is : " + message);
                logHandler.writeToLogFile(message);
                resultMap.put(transaction.getTransactionId(), message);
                //close resources
                objectInputStream.close();
                objectOutputStream.close();
                Thread.sleep(2500);
            }
            connection = false;
        }
        XmlWriter xmlWriter = new XmlWriter();
        xmlWriter.saveTransactionResult((TreeMap) resultMap);
        logMessage = "write to XML File";
        logHandler.writeToLogFile(logMessage);
    }

}