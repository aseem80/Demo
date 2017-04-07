package io.capitalone.interview.schema.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by aseem80 on 4/4/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    private BigDecimal amount;
    @JsonProperty(value="is-pending")
    private boolean isPending;
    @JsonProperty(value="aggregation-time")
    private Long appregationTimeInUnixEpochMillis;
    @JsonProperty(value="account-id")
    private String accountId;
    @JsonProperty(value="clear-date")
    private Long clearDateInUnixEpochMillis;
    @JsonProperty(value="transaction-id")
    private String transactionId;
    @JsonProperty(value="raw-merchant")
    private String rawMerchant;
    private String categorization;
    private String merchant;
    @JsonProperty(value="transaction-time")
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date transactionTime;

    public Transaction() {
    }

    public Transaction(BigDecimal amount, boolean isPending, Long appregationTimeInUnixEpochMillis, String accountId,
            Long clearDateInUnixEpochMillis, String transactionId, String rawMerchant, String categorization, String
            merchant, Date transactionTime) {
        this.amount = amount;
        this.isPending = isPending;
        this.appregationTimeInUnixEpochMillis = appregationTimeInUnixEpochMillis;
        this.accountId = accountId;
        this.clearDateInUnixEpochMillis = clearDateInUnixEpochMillis;
        this.transactionId = transactionId;
        this.rawMerchant = rawMerchant;
        this.categorization = categorization;
        this.merchant = merchant;
        this.transactionTime = transactionTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public Long getAppregationTimeInUnixEpochMillis() {
        return appregationTimeInUnixEpochMillis;
    }

    public void setAppregationTimeInUnixEpochMillis(Long appregationTimeInUnixEpochMillis) {
        this.appregationTimeInUnixEpochMillis = appregationTimeInUnixEpochMillis;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getClearDateInUnixEpochMillis() {
        return clearDateInUnixEpochMillis;
    }

    public void setClearDateInUnixEpochMillis(Long clearDateInUnixEpochMillis) {
        this.clearDateInUnixEpochMillis = clearDateInUnixEpochMillis;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRawMerchant() {
        return rawMerchant;
    }

    public void setRawMerchant(String rawMerchant) {
        this.rawMerchant = rawMerchant;
    }

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass( ) != o.getClass( ))
            return false;

        Transaction that = (Transaction) o;

        return new EqualsBuilder( ).append( isPending, that.isPending ).append( amount, that.amount ).append(
                accountId, that.accountId ).append( transactionId, that.transactionId ).append( rawMerchant, that
                .rawMerchant ).append( transactionTime, that.transactionTime ).isEquals( );
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder( 17, 37 ).append( amount ).append( isPending ).append( accountId )
                .append( transactionId ).append( rawMerchant ).append( transactionTime ).toHashCode( );
    }

    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "amount", amount ).append( "isPending", isPending ).append(
                "appregationTimeInUnixEpochMillis", appregationTimeInUnixEpochMillis ).append( "accountId", accountId
        ).append( "clearDateInUnixEpochMillis", clearDateInUnixEpochMillis ).append( "transactionId", transactionId )
                .append( "rawMerchant", rawMerchant ).append( "categorization", categorization ).append( "merchant",
                        merchant ).append( "transactionTime", transactionTime ).toString( );
    }
}
