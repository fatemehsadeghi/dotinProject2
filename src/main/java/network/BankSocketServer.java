package network;

import deposit.Deposit;
import exceptions.DepositNotFoundException;
import exceptions.InitialBalanceBiggerThanUpperBoundException;
import exceptions.NegativeInitialBalanceException;
import exceptions.TransactionTypeNotFoundException;
import logHandling.LogHandler;
import org.json.simple.parser.ParseException;
import fileHandling.JsonHandler;
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
    private BigDecimal newInitialBalance;
    private String responseToTerminal;

     public static void main(String args[]) throws IOException, ParseException, InterruptedException {
        BankSocketServer socketServerExample=new BankSocketServer();
        Thread serverThread =new Thread(socketServerExample);
        serverThread.start();
        }
     //compare deposit in transaction with a list of depositId ;
     //if find equals so return deposit id
     private String findDeposit(TreeMap depositMap, Transaction transaction)
            throws DepositNotFoundException {
        String depositMapKey = null;
        Boolean aBoolean = false;
        for (Object keyMap : depositMap.keySet()) {
            if (keyMap.equals(transaction.getDeposit())) {
                depositMapKey = (String) keyMap;
                aBoolean = true;
                break;
            }
            else {
                aBoolean=false;
            }
        }
        if(!aBoolean)throw new DepositNotFoundException();
        return depositMapKey;
    }
    //add initialBalance and amount
    private synchronized void calculateDeposit(TreeMap depositMap, String depositMapKey, Transaction transaction)
            throws InitialBalanceBiggerThanUpperBoundException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        //critical section in thread
        this.newInitialBalance = transaction.getAmount().add(depositInitialBalance);
        //check amount of upperBound be true
        if (newInitialBalance.compareTo(deposit.getUpperBound()) == 1)
            throw new InitialBalanceBiggerThanUpperBoundException();
        else {
            deposit.setInitialBalance(newInitialBalance);
            System.out.println("new initial balance is " + newInitialBalance + ((Deposit) depositMap.get(depositMapKey)).getInitialBalance());
        }
    }
    //subtract value of amount from initialBalance and check for non negative result
    private synchronized void calculateWithdraw(TreeMap depositMap, String depositMapKey, Transaction transaction)
            throws NegativeInitialBalanceException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        //critical section in thread
        this.newInitialBalance = depositInitialBalance.subtract(transaction.getAmount());
        //check amount of initial balance...it should be bigger than 0
        if (newInitialBalance.compareTo(BigDecimal.valueOf(0)) == -1)
            throw new NegativeInitialBalanceException();
        else {
            deposit.setInitialBalance(newInitialBalance);
        }
    }
    //manage tow part of calculation.deposit and withdraw
    private String calculate(TreeMap depositMap, Transaction transaction)
            throws InitialBalanceBiggerThanUpperBoundException, DepositNotFoundException, NegativeInitialBalanceException, TransactionTypeNotFoundException, IOException {
        String depositMapKey;
        String transactionType = transaction.getTransactionType();
        //invoke findDeposit for depositId
        BankSocketServer socketServerExample = new BankSocketServer();
        depositMapKey = socketServerExample.findDeposit( depositMap , transaction);
        //check transaction type
        if(!transactionType.equals("deposit")&& !transaction.getTransactionType().equals("withdraw"))
            throw new TransactionTypeNotFoundException("transactionType does not exist!");
        //invoke calculateDeposit for doing deposit calculation
        else if (transactionType.equals("deposit"))
        {
            BankSocketServer serverExample = new BankSocketServer();
            serverExample.calculateDeposit(depositMap,depositMapKey,transaction);
        }
        //invoke calculateWithdraw for doing withdraw calculation
        else if (transaction.getTransactionType().equals("withdraw"))
        {
            BankSocketServer serverExample = new BankSocketServer();
            serverExample.calculateWithdraw(depositMap,depositMapKey,transaction);
        }
        return "successful";
    }
    //main method run to handle multi threading
    public void run() {
        JsonHandler jsonHandler = new JsonHandler();
        TreeMap<String, Deposit> depositMap = null;
        String loggerMassage;
        try {
            //parse a jsonFile and store the result to a keyMap
            depositMap = jsonHandler.parseJson();
            int port = 8080;
            server = new ServerSocket(port);
            loggerMassage = "server created!";
            LogHandler logHandler = new LogHandler();
            logHandler.writeToLogFile(loggerMassage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (true) {
            Socket socket = null;
            try {
                loggerMassage = "waiting for terminal!";
                LogHandler logHandler = new LogHandler();
                logHandler.writeToLogFile(loggerMassage);
                //waiting to connecting to a terminal
                socket = server.accept();
                loggerMassage = "a terminal is connected!";
                logHandler.writeToLogFile(loggerMassage);
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Transaction transaction = (Transaction) objectInputStream.readObject();
                //invoke calculator
                BankSocketServer socketServerExample = new BankSocketServer();
                responseToTerminal = socketServerExample.calculate(depositMap,transaction);
                logHandler.writeToLogFile(responseToTerminal);

            } catch (exceptions.DepositNotFoundException e) {
                System.out.print("this deposit not exist!");
                responseToTerminal="this deposit not exist!";
            } catch (exceptions.InitialBalanceBiggerThanUpperBoundException e) {
                System.out.println("InitialBalanceBiggerThanUpperBoundException");
                 responseToTerminal= "InitialBalanceBiggerThanUpperBoundException";
            } catch (exceptions.NegativeInitialBalanceException e) {
                System.out.println("initial balance is less than amount of withdraw");
                 responseToTerminal="initial balance is less than amount of withdraw";
            } catch (exceptions.TransactionTypeNotFoundException e) {
                System.out.println("transactionType does not exist!");
                responseToTerminal="transactionType does not exist!";
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                assert socket != null;
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(responseToTerminal);
                objectOutputStream.flush();
                loggerMassage = "send result to terminal";
                LogHandler logHandler = new LogHandler();
                logHandler.writeToLogFile(loggerMassage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}