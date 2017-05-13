package logHandling;

import network.Terminal;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogHandler {
    public void writeToLogFile(String message) throws IOException {
        Logger serverLogger = Logger.getLogger(Terminal.class.getName());
        FileHandler fileLog = new FileHandler("LogFile.log");
        serverLogger.addHandler(fileLog);
        serverLogger.info(message);
    }
}
