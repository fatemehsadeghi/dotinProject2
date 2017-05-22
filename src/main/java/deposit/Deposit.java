package deposit;

import exceptions.DepositNotFoundException;
import exceptions.InitialBalanceBiggerThanUpperBoundException;
import exceptions.NegativeInitialBalanceException;
import exceptions.TransactionTypeNotFoundException;
import logHandling.LogHandler;
import org.json.simple.parser.ParseException;
import transaction.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

public class Deposit {
    private String customer;
    private String customerId;
    private BigDecimal initialBalance;
    private BigDecimal upperBound;
    private BigDecimal newInitialBalance;

    public Deposit() {
    }

    public Deposit(String customer, String customerId, BigDecimal initialBalance, BigDecimal upperBound) {
        this.customer = customer;
        this.customerId = customerId;
        this.initialBalance = initialBalance;
        this.upperBound = upperBound;
    }

    private BigDecimal getUpperBound() {
        return upperBound;
    }

    private BigDecimal getInitialBalance() {
        return initialBalance;
    }

    private void setInitialBalance(BigDecimal initialBalance) {
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

    private BigDecimal calculateDeposit(TreeMap depositMap, String depositMapKey, Transaction transaction) throws InitialBalanceBiggerThanUpperBoundException, IOException, ParseException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        this.newInitialBalance = transaction.getAmount().add(depositInitialBalance);
        if (newInitialBalance.compareTo(deposit.getUpperBound()) == 1)
            throw new InitialBalanceBiggerThanUpperBoundException();
        else {
            deposit.setInitialBalance(newInitialBalance);
        }
        return newInitialBalance;
    }

    private BigDecimal calculateWithdraw(TreeMap depositMap, String depositMapKey, Transaction transaction) throws NegativeInitialBalanceException, IOException {
        BigDecimal depositInitialBalance;
        Deposit deposit = (Deposit) depositMap.get(depositMapKey);
        depositInitialBalance = deposit.getInitialBalance();
        this.newInitialBalance = depositInitialBalance.subtract(transaction.getAmount());
        if (newInitialBalance.compareTo(BigDecimal.valueOf(0)) == -1)
            throw new NegativeInitialBalanceException();
        else {
            deposit.setInitialBalance(newInitialBalance);
        }
        return newInitialBalance;
    }

    public String calculate(List serverAttributeList, TreeMap depositMap, Transaction transaction) throws InitialBalanceBiggerThanUpperBoundException, DepositNotFoundException, NegativeInitialBalanceException, TransactionTypeNotFoundException, IOException, ParseException {
        String outLogPath = (String) serverAttributeList.get(1);
        LogHandler logHandler = new LogHandler(outLogPath);
        BigDecimal newInitialBalance;
        String depositMapKey;
        String transactionType = transaction.getTransactionType();
        depositMapKey = findDeposit(depositMap, transaction);
        if (!transactionType.equals("deposit") && !transaction.getTransactionType().equals("withdraw"))
            throw new TransactionTypeNotFoundException("transactionType does not exist!");
        synchronized (this) {
            if (transactionType.equals("deposit")) {
                newInitialBalance = calculateDeposit(depositMap, depositMapKey, transaction);
                logHandler.writeToLogFile("result of deposit for transaction id :" + transaction.getTransactionId() + "and depositId :" + transaction.getDeposit() + "is :" + String.valueOf(newInitialBalance));
            } else if (transaction.getTransactionType().equals("withdraw")) {
                newInitialBalance = calculateWithdraw(depositMap, depositMapKey, transaction);
                logHandler.writeToLogFile("result of deposit for transaction id :" + transaction.getTransactionId() + "and depositId :" + transaction.getDeposit() + "is :" + String.valueOf(newInitialBalance));
            }
        }
        return "successful";
    }

}
