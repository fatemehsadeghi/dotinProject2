package transaction;

import deposit.Deposit;
import exceptions.DepositNotFoundException;
import exceptions.InitialBalanceBiggerThanUpperBoundException;
import exceptions.NegativeInitialBalanceException;
import exceptions.TransactionTypeNotFoundException;
import logHandling.LogHandler;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

public class Transaction implements Serializable {
    private String transactionId;
    private String transactionType;
    private String deposit;
    private BigDecimal amount;
    private BigDecimal newInitialBalance;
    private LogHandler logHandler = new LogHandler();

    public Transaction(String transactionId, String transactionType, BigDecimal amount, String deposit) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.deposit = deposit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDeposit() {
        return deposit;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

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
            //  logHandler.writeToLogFile(String.valueOf(newInitialBalance));
        }
    }

    public String calculate(TreeMap depositMap, Transaction transaction) throws InitialBalanceBiggerThanUpperBoundException, DepositNotFoundException, NegativeInitialBalanceException, TransactionTypeNotFoundException, IOException {
        String depositMapKey;
        String transactionType = transaction.getTransactionType();
        depositMapKey = findDeposit(depositMap, transaction);
        if (!transactionType.equals("deposit") && !transaction.getTransactionType().equals("withdraw"))
            throw new TransactionTypeNotFoundException("transactionType does not exist!");
        synchronized (this) {
            if (transactionType.equals("deposit")) {

                calculateDeposit(depositMap, depositMapKey, transaction);
            } else if (transaction.getTransactionType().equals("withdraw")) {
                calculateWithdraw(depositMap, depositMapKey, transaction);
            }
        }
        return "successful";
    }

}
