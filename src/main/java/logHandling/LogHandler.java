package logHandling;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogHandler {
    private FileHandler fileLog;
    public LogHandler(){
        try {
            createLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createLogFile() throws IOException {
        fileLog = new FileHandler("LogFile.log");
    }
    public void writeToLogFile(String message) throws IOException {
        Logger serverLogger = Logger.getLogger(LogHandler.class.getName());
        serverLogger.addHandler(fileLog);
        serverLogger.info(message);
        serverLogger.removeHandler(fileLog);
        fileLog.close();
    }
}
