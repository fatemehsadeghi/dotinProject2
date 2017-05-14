package fileHandling;

import deposit.Deposit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.TreeMap;

public class JsonHandler {
    public TreeMap<String , Deposit> parseJson() throws IOException, ParseException {
        TreeMap<String , Deposit> depositMap = new TreeMap<String, Deposit>();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("src\\core.json"));
        JSONObject jsonObject =(JSONObject)object;
        JSONArray depositsArray = (JSONArray)jsonObject.get("deposits");
        for (Object obj : depositsArray) {
            JSONObject depositJson = (JSONObject) obj;
            String customer = (String) depositJson.get("customer");
            String customerId = (String) depositJson.get("id");
            String initialBalanceStr = (String) depositJson.get("initialBalance");
            String upperBoundStr = (String) depositJson.get("upperBound");
            BigDecimal initialBalance = new BigDecimal(initialBalanceStr);
            BigDecimal upperBound = new BigDecimal(upperBoundStr);
            Deposit deposit = new Deposit(customer ,customerId,initialBalance,upperBound);
            depositMap.put(customerId , deposit);
        }
        return depositMap;
    }
    public static void main(String args[])throws Exception{
        JsonHandler jsonFileParser = new JsonHandler();
        jsonFileParser.parseJson();
    }
}
