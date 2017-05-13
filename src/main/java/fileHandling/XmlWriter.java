package fileHandling;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.TreeMap;

public class XmlWriter {

    public void saveTransactionResult(TreeMap resultMap ) throws ParserConfigurationException, IOException,
            TransformerFactoryConfigurationError, TransformerException,
            SAXException {

        File xmlFile = new File("src\\response.xml");
        Document document;
        Element rootElement;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();
        document = documentBuilder.newDocument();
        rootElement = document.createElement("Transactions");
        document.appendChild(rootElement);
        for(int i = 0 ; i<resultMap.size();i++) {
            Element itemElement = document.createElement("transaction");
            rootElement.appendChild(itemElement);

            Element idElement = document.createElement("transactionId");
            Text id = document.createTextNode((String) resultMap.keySet().toArray()[i]);
            idElement.appendChild(id);
            itemElement.appendChild(idElement);

            Element resultElement = document.createElement("result");
            Text resultText = document.createTextNode((String) resultMap.get(resultMap.keySet().toArray()[i]));
            resultElement.appendChild(resultText);
            itemElement.appendChild(resultElement);
        }
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);
        String xmlString = streamResult.getWriter().toString();

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(xmlFile ,true)));
        bufferedWriter.write(xmlString);
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}