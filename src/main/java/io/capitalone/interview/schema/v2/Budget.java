package io.capitalone.interview.schema.v2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.capitalone.interview.jackson.CurrencySerializer;

import java.math.BigDecimal;

/**
 * Created by aseem80 on 4/5/17.
 */
public class Budget {

    @JsonSerialize(using = CurrencySerializer.class)
    private BigDecimal spent = BigDecimal.ZERO;
    @JsonSerialize(using = CurrencySerializer.class)

    private BigDecimal income = BigDecimal.ZERO;

    public Budget() {
    }

    public Budget(BigDecimal spent, BigDecimal income) {
        this.spent = spent;
        this.income = income;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public Budget addIncome(BigDecimal income) {
        Budget result = new Budget(spent, this.income.add(income));
        return result;
    }

    public Budget addSpent(BigDecimal spent) {
        Budget result = new Budget(this.spent.add(spent), income);
        return result;
    }

}
