package network;

import deposit.Deposit;
import exceptions.DepositNotFoundException;
import exceptions.InitialBalanceBiggerThanUpperBoundException;
import exceptions.NegativeInitialBalanceException;
import exceptions.TransactionTypeNotFoundException;
import fileHandling.JsonHandler;
import logHandling.LogHandler;
import org.json.simple.parser.ParseException;
import transaction.Transaction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

public class BankSocketServer implements Runnable {
    private static ServerSocket server;
    Socket socket;
    TreeMap<String, Deposit> depositMap;
    private BigDecimal newInitialBalance;
    private String responseToTerminal;
    private LogHandler logHandler = new LogHandler();
    private String loggerMessage;

    public static void main(String args[]) throws IOException, ParseException, InterruptedException {
        BankSocketServer bankSocketServer = new BankSocketServer();
        bankSocketServer.runs();
    }

    public void runs() {
        try {
            JsonHandler jsonHandler = new JsonHandler();
            depositMap = jsonHandler.parseJson();
            int port = 8080;
            server = new ServerSocket(port);
            loggerMessage = "server created!";
            logHandler.writeToLogFile(loggerMessage);
            loggerMessage = "waiting for terminal!";
            logHandler.writeToLogFile(loggerMessage);
            socket = server.accept();
            loggerMessage = "a terminal is connected!";
            logHandler.writeToLogFile(loggerMessage);
            BankSocketServer bankSocketServer = new BankSocketServer();
            Thread serverThread = new Thread(bankSocketServer);
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void run() {
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
/*
    private String findDeposit(TreeMap depositMap, Transaction transaction) throws DepositNotFoundException, IOException {
        String depositMapKey = null;
        Boolean aBoolean = false;
        for (Object keyMap : depositMap.keySet()) {
            if (keyMap.equals(transaction.getDeposit())) {
                depositMapKey = (String) keyMap;
                aBoolean = true;
                logHandler.writeToLogFile(depositMapKey);
                break;
            } else {
                aBoolean = false;
            }
        }
        if (!aBoolean) throw new DepositNotFoundException();
        return depositMapKey;
    }

    private void calculateDeposit(TreeMap depositMap, String depositMapKey, Transaction transaction) throws InitialBalanceBiggerThanUpperBoundException, IOException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        this.newInitialBalance = transaction.getAmount().add(depositInitialBalance);
        if (newInitialBalance.compareTo(deposit.getUpperBound()) == 1)
            throw new InitialBalanceBiggerThanUpperBoundException();
        else {
            deposit.setInitialBalance(newInitialBalance);
            logHandler.writeToLogFile(String.valueOf(newInitialBalance));
        }
    }

    private void calculateWithdraw(TreeMap depositMap, String depositMapKey, Transaction transaction) throws NegativeInitialBalanceException, IOException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        this.newInitialBalance = depositInitialBalance.subtract(transaction.getAmount());
        if (newInitialBalance.compareTo(BigDecimal.valueOf(0)) == -1)
            throw new NegativeInitialBalanceException();
        else {
            deposit.setInitialBalance(newInitialBalance);
            logHandler.writeToLogFile(String.valueOf(newInitialBalance));
        }
    }

    private String calculate(TreeMap depositMap, Transaction transaction) throws InitialBalanceBiggerThanUpperBoundException, DepositNotFoundException, NegativeInitialBalanceException, TransactionTypeNotFoundException, IOException {
        String depositMapKey;
        String transactionType = transaction.getTransactionType();
        BankSocketServer socketServerExample = new BankSocketServer();
        depositMapKey = socketServerExample.findDeposit(depositMap, transaction);
        if (!transactionType.equals("deposit") && !transaction.getTransactionType().equals("withdraw"))
            throw new TransactionTypeNotFoundException("transactionType does not exist!");
        synchronized (this) {
            if (transactionType.equals("deposit")) {
                BankSocketServer serverExample = new BankSocketServer();
                serverExample.calculateDeposit(depositMap, depositMapKey, transaction);
            } else if (transaction.getTransactionType().equals("withdraw")) {
                BankSocketServer serverExample = new BankSocketServer();
                serverExample.calculateWithdraw(depositMap, depositMapKey, transaction);
            }
        }
        return "successful";
    }
*/
}