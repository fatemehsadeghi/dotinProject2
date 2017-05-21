package deposit;

import exceptions.DepositNotFoundException;
import exceptions.InitialBalanceBiggerThanUpperBoundException;
import exceptions.NegativeInitialBalanceException;
import exceptions.TransactionTypeNotFoundException;
import logHandling.LogHandler;
import transaction.Transaction;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

public class Deposit implements Serializable {
    private String customer;
    private String customerId;
    private BigDecimal initialBalance;
    private BigDecimal upperBound;
    private BigDecimal newInitialBalance;
    private LogHandler logHandler = new LogHandler();

    public Deposit() {
    }

    public Deposit(String customer, String customerId, BigDecimal initialBalance, BigDecimal upperBound) {
        this.customer = customer;
        this.customerId = customerId;
        this.initialBalance = initialBalance;
        this.upperBound = upperBound;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    private String findDeposit(TreeMap depositMap, Transaction transaction) throws DepositNotFoundException, IOException {
        String depositMapKey = null;
        Boolean aBoolean = false;
        for (Object keyMap : depositMap.keySet()) {
            if (keyMap.equals(transaction.getDeposit())) {
                depositMapKey = (String) keyMap;
                aBoolean = true;
                //logHandler.writeToLogFile(depositMapKey);
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
            //logHandler.writeToLogFile(String.valueOf(newInitialBalance));
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
