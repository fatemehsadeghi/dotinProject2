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

    private Socket socket;
    private TreeMap<String, Deposit> depositMap;
    private Integer transactionSize;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String responseToTerminal;
    private LogHandler logHandler = new LogHandler();
    private String loggerMessage;
    List logList = new ArrayList();

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
        List serverAttributeList = new ArrayList();
        depositMap = jsonHandler.parseJson(serverAttributeList);
        int PORT = Integer.parseInt((String) serverAttributeList.get(0));
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
                            System.out.println("thread name is : " + currentThread().getName() + "object number is :" + i );
                            System.out.println("transaction is : " + transaction.toString());
                            Deposit deposit = new Deposit();
                            responseToTerminal = deposit.calculate(depositMap, transaction);
                            loggerMessage = "send result to terminal";
                            logList.add(loggerMessage);
                            //logHandler.writeToLogFile(loggerMessage);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DepositNotFoundException e) {
                            e.printStackTrace();
                            responseToTerminal = "this deposit not exist!";
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
                        }
                        try {
                            assert socket != null;
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            objectOutputStream.writeObject(responseToTerminal);
                            logHandler.writeToLogFile(responseToTerminal);
                            loggerMessage = "send result to terminal";
                            logList.add(loggerMessage);
                            //logHandler.writeToLogFile(loggerMessage);
                        } catch (IOException er) {
                            er.printStackTrace();
                        } finally {
                            i++;
                        }
                    }
                }

            }.start();
            write();
        }
        //for ( int j=0 ; j<logList.size() ; j++) {
         //   logHandler.writeToLogFile(loggerMessage);
       // }
    }
    public void write() throws IOException {
        for ( int j=0 ; j<logList.size() ; j++) {
            logHandler.writeToLogFile(loggerMessage);
             }
    }
}

    /*

    public void run() {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            transactionSize = (Integer) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!transactionSize.equals(0)) {
            try {
                Transaction transaction = (Transaction) objectInputStream.readObject();
                System.out.println("transaction is : " + transaction.toString());
                Deposit deposit = new Deposit();
                responseToTerminal = deposit.calculate(depositMap, transaction);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransactionTypeNotFoundException e) {
                e.printStackTrace();
            } catch (InitialBalanceBiggerThanUpperBoundException e) {
                e.printStackTrace();
            } catch (NegativeInitialBalanceException e) {
                e.printStackTrace();
            } catch (DepositNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    */

    /*

    public void runs() {
        while(true) {
            try {
                JsonHandler jsonHandler = new JsonHandler();
                depositMap = jsonHandler.parseJson();
                int port = 8080;
                server = new ServerSocket(port); loggerMessage = "server created!";
                logHandler.writeToLogFile(loggerMessage);
                loggerMessage = "waiting for terminal!";
                logHandler.writeToLogFile(loggerMessage);
                socket = server.accept();
                loggerMessage = "a terminal is connected!";
                logHandler.writeToLogFile(loggerMessage);
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Transaction transaction = (Transaction) objectInputStream.readObject();
                Deposit deposit = new Deposit();
                responseToTerminal = deposit.calculate(depositMap, transaction);
                logHandler.writeToLogFile(responseToTerminal);
                InetAddress addr = socket.getInetAddress();
                BankSocketServer bankSocketServer = new BankSocketServer();
                Thread serverThread = new Thread(bankSocketServer);
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (InitialBalanceBiggerThanUpperBoundException e) {
                e.printStackTrace();
            } catch (TransactionTypeNotFoundException e) {
                e.printStackTrace();
            } catch (DepositNotFoundException e) {
                e.printStackTrace();
            } catch (NegativeInitialBalanceException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        /*
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Transaction transaction = (Transaction) objectInputStream.readObject();
            responseToTerminal = transaction.calculate(depositMap, transaction);
            logHandler.writeToLogFile(responseToTerminal);
        } catch (exceptions.DepositNotFoundException e) {
            responseToTerminal = "this deposit not exist!";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    /*
        try {
            assert socket != null;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(responseToTerminal);
            logHandler.writeToLogFile(responseToTerminal);
            objectOutputStream.flush();
            objectOutputStream.close();
            loggerMessage = "send result to terminal";
            logHandler.writeToLogFile(loggerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

*/