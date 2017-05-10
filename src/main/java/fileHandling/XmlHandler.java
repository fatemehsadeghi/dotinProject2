package fileHandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import transaction.Transaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
public class XmlHandler {

    public List parseXmlFile() throws ParserConfigurationException, IOException, SAXException {
        List transactionList = new ArrayList();
        File inputXmlFile = new File("src//terminal.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputXmlFile);
        Element docElement = document.getDocumentElement();
        String terminalId = docElement.getAttributes().getNamedItem("id").getNodeValue();
        String terminalType = docElement.getAttributes().getNamedItem("type").getNodeValue();
        List<String> terminalPropertyList = new ArrayList();
        terminalPropertyList.add(terminalId);
        terminalPropertyList.add(terminalType);
        NodeList nodeList = docElement.getElementsByTagName("transaction");
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int transactionNum = 0; transactionNum < nodeList.getLength(); transactionNum++) {
                String transactionId = nodeList.item(transactionNum).getAttributes().getNamedItem("id").getNodeValue();
                String transactionType = nodeList.item(transactionNum).getAttributes().getNamedItem("type").getNodeValue();
                String amountStr = nodeList.item(transactionNum).getAttributes().getNamedItem("amount").getNodeValue();
                String deposit = nodeList.item(transactionNum).getAttributes().getNamedItem("deposit").getNodeValue();
                BigDecimal amount = new BigDecimal(amountStr);
                Transaction transaction = new Transaction(transactionId , transactionType , amount ,deposit);
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }
    public static void main(String args[]) throws  Exception{
        XmlHandler xmlHandler = new XmlHandler();
        xmlHandler.parseXmlFile();
    }
}