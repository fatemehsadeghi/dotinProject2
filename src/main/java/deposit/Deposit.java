package deposit;

import java.io.Serializable;
import java.math.BigDecimal;

public class Deposit implements Serializable {
    private String customer;
    private String customerId;
    private BigDecimal initialBalance;
    private BigDecimal upperBound;

    public Deposit(String customer , String customerId ,BigDecimal initialBalance , BigDecimal upperBound) {
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


}
