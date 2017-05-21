package fileHandling;

import deposit.Deposit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

public class JsonHandler {
    public static void main(String args[]) throws Exception {
      //  JsonHandler jsonFileParser = new JsonHandler();
      //  int PORT=0;
      //  jsonFileParser.parseJson(PORT);
    }

    public TreeMap<String, Deposit> parseJson(List serverAttribList) throws IOException, ParseException {
        TreeMap<String, Deposit> depositMap = new TreeMap<String, Deposit>();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("C:\\Users\\dotinschool1\\Desktop\\dotinProject2\\src\\main\\resources\\core.json"));
        JSONObject jsonObject = (JSONObject) object;
        String port = (String) jsonObject.get("port");
        String outLog = (String) jsonObject.get("outLog");
        serverAttribList.add(0,port);
        serverAttribList.add(1,outLog);
      // PORT=Integer.parseInt(port);
        //System.out.println(PORT);
        JSONArray depositsArray = (JSONArray) jsonObject.get("deposits");
        for (Object obj : depositsArray) {
            JSONObject depositJson = (JSONObject) obj;
            String customer = (String) depositJson.get("customer");
            String customerId = (String) depositJson.get("id");
            String initialBalanceStr = (String) depositJson.get("initialBalance");
            String upperBoundStr = (String) depositJson.get("upperBound");
            BigDecimal initialBalance = new BigDecimal(initialBalanceStr);
            BigDecimal upperBound = new BigDecimal(upperBoundStr);
            Deposit deposit = new Deposit(customer, customerId, initialBalance, upperBound);
            depositMap.put(customerId, deposit);
        }
        return depositMap;
    }
}
