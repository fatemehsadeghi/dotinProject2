package transaction;

import java.io.Serializable;
import java.math.BigDecimal;

public class Transaction implements Serializable {
    private String transactionId;
    private String transactionType;
    private String deposit;
    private BigDecimal amount;

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
}
