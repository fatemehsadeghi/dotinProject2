package network;

import deposit.Deposit;
import exceptions.DepositNotFoundException;
import fileHandling.JsonHandler;
import logHandling.LogHandler;
import org.json.simple.parser.ParseException;
import transaction.Transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class BankSocketServer extends Thread {

    private List logList = new ArrayList();
    private Socket socket;
    private TreeMap<String, Deposit> depositMap;
    private Integer transactionSize;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String responseToTerminal;
    private String loggerMessage;

    public static void main(String args[]) {

        BankSocketServer bankSocketServer = new BankSocketServer();
        try {
            bankSocketServer.runServer();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void runServer() throws IOException, ClassNotFoundException, ParseException {
        JsonHandler jsonHandler = new JsonHandler();
        final List serverAttributeList = new ArrayList();
        depositMap = jsonHandler.parseJson(serverAttributeList);
        int PORT = Integer.parseInt((String) serverAttributeList.get(0));
        String outLogPath = (String) serverAttributeList.get(1);
        final LogHandler logHandler = new LogHandler(outLogPath);
        System.out.println("Current port is : " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Current IP address : " + ip.getHostAddress());
        while (true) {
            socket = serverSocket.accept();
            new Thread() {
                public void run() {
                    try {
                        objectInputStream = new ObjectInputStream(socket.getInputStream());
                        transactionSize = (Integer) objectInputStream.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int i = 0;
                    while (i < transactionSize) {
                        try {
                            Transaction transaction = (Transaction) objectInputStream.readObject();
                            System.out.println("thread name is : " + currentThread().getName() + "object number is :" + i);
                            System.out.println("transaction is : " + transaction.toString());
                            Deposit deposit = new Deposit();
                            responseToTerminal = deposit.calculate(serverAttributeList, depositMap, transaction);
                            loggerMessage = "send result to terminal";
                            logList.add(loggerMessage);
                            logHandler.writeToLogFile(loggerMessage);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DepositNotFoundException e) {
                            e.printStackTrace();
                            responseToTerminal = "this deposit does not exist!";
                            System.out.println(responseToTerminal);
                        } catch (exceptions.InitialBalanceBiggerThanUpperBoundException e) {
                            responseToTerminal = "InitialBalanceBiggerThanUpperBoundException";
                            System.out.println(responseToTerminal);
                        } catch (exceptions.NegativeInitialBalanceException e) {
                            responseToTerminal = "initial balance is less than amount of withdraw";
                            System.out.println(responseToTerminal);
                        } catch (exceptions.TransactionTypeNotFoundException e) {
                            responseToTerminal = "transactionType does not exist!";
                            System.out.println(responseToTerminal);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert socket != null;
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            objectOutputStream.writeObject(responseToTerminal);
                            logHandler.writeToLogFile(responseToTerminal);
                            loggerMessage = "send result to terminal";
                            logList.add(loggerMessage);
                            logHandler.writeToLogFile(loggerMessage);
                        } catch (IOException er) {
                            er.printStackTrace();
                        } finally {
                            i++;
                        }
                    }
                }

            }.start();
        }

    }
}