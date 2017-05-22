package logHandling;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogHandler  {
    private FileHandler logFile;
    Logger logger;
    public LogHandler(String logFileName){
        try {
            createLogFile(logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createLogFile(String logFileName) throws IOException {
        logFile = new FileHandler("src\\main\\resources\\" + logFileName +".log" , true);
        logger = Logger.getLogger(LogHandler.class.getName());
        logger.addHandler(logFile);
    }
    public void writeToLogFile(String message) throws IOException {

        logger.info(message);
       // logger.removeHandler(logFile);
      //  logFile.close();
       // logFile.clear();
       // raf.close();
    }
}
