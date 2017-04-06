package io.capitalone.interview.schema.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aseem80 on 4/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transactions {

    private String error;
    private List<Transaction> transactions = new ArrayList<>();


    public Transactions() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "error", error ).append( "transactions", transactions ).toString( );
    }
}
