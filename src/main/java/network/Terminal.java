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
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Terminal {
    private Integer serverPort;
    private List<String> logMessageList = new ArrayList<String>();
    private LogHandler logHandler = new LogHandler();

    public static void main(String[] args) {
        try {
            String terminalName = "terminal";
            String responseFile = "response";
            Terminal terminalSocket = new Terminal();
            terminalSocket.run(terminalName ,responseFile);
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

   public void run(String terminalName, String responseFile) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, InterruptedException, TransformerException {
        ArrayList<String> terminalAttributeList = new ArrayList<String>();
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;

        XmlHandler xmlHandler = new XmlHandler();
        List transactionList = xmlHandler.parseXmlFile(terminalAttributeList, terminalName);
        int transactionSize = transactionList.size();
       // String terminalId = terminalAttributeList.get(0);
     //   String terminalType = terminalAttributeList.get(1);
     //   String localhost = terminalAttributeList.get(2);
        serverPort = Integer.parseInt(terminalAttributeList.get(3));
        Map<String, String> resultMap = new TreeMap<String, String>();
        Socket terminalSocket;
        terminalSocket = new Socket("10.20.15.160", serverPort);
        objectOutputStream = new ObjectOutputStream(terminalSocket.getOutputStream());
        objectOutputStream.writeObject(transactionSize);
        String logMessage;
        for (Object transactionObject : transactionList) {
            logMessage = "client is connecting!";
            logMessageList.add(logMessage);
            System.out.println("Sending request to Socket Server");
            logMessage = "Sending request to Socket Server";
            logMessageList.add(logMessage);
            Transaction transaction = (Transaction) transactionObject;
            objectOutputStream.writeObject(transaction);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(terminalSocket.getInputStream());
            String message = (String) objectInputStream.readObject();
            System.out.println("Message from server is : " + message);
            logMessageList.add(logMessage);
            resultMap.put(transaction.getTransactionId(), message);
            Thread.sleep(3000);
        }
        XmlWriter xmlWriter = new XmlWriter();
        xmlWriter.saveTransactionResult((TreeMap) resultMap, responseFile);
        logMessage = "write to XML File";
        logMessageList.add(logMessage);
        for (String aLogMessageList : logMessageList) {
            logHandler.writeToLogFile(aLogMessageList);
        }
    }

}