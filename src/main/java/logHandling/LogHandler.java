package logHandling;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogHandler implements Serializable {
    private FileHandler fileLog;
    public LogHandler(){
        try {
            createLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createLogFile() throws IOException {
        boolean append = true;
        fileLog = new FileHandler("src\\main\\resources\\LogFile.log" , append);
    }
    public void writeToLogFile(String message) throws IOException {
       // File file = new File(fileName);
        //RandomAccessFile raf = new RandomAccessFile(file, "rw");
        Logger serverLogger = Logger.getLogger(LogHandler.class.getName());
        serverLogger.addHandler(fileLog);
        serverLogger.info(message);
        serverLogger.removeHandler(fileLog);
        fileLog.close();
       // logFile.clear();
       // raf.close();
    }
    /*
    private void writeIntoAccessFile(String fileName) {
        try {
            File file = new File(fileName);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.writeBytes("Terminal ID " + terminalId + "\n");
            raf.seek(file.length());
            raf.writeBytes("\n");

            int i = 0;
            for (String str : logFile) {
                raf.writeBytes("\n id = " + i);
                raf.writeBytes("\n  " + str);
                i++;
            }
            logFile.clear();
            raf.close();
        } catch (IOException e) {
            System.out.println("IOException:");
        }
    }
    */
}
