package io.capitalone.interview.service.v2;

import io.capitalone.interview.exception.v2.DemoServiceException;
import io.capitalone.interview.exception.v2.DemoServiceValidationException;
import io.capitalone.interview.restClient.v2.LevelMoneyClient;
import io.capitalone.interview.restClient.v2.LevelMoneyClientImpl;
import io.capitalone.interview.schema.v2.ApiAuthentication;
import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.schema.v2.Error;
import io.capitalone.interview.schema.v2.Transaction;
import io.capitalone.interview.schema.v2.Transactions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by aseem80 on 4/5/17.
 */
@Service
public class CodingAssignmentServiceImpl implements CodingAssignmentService {

    //A static value in API right now. Can be loaded dynamically from OAuth or CAS if available
    private static ApiAuthentication authRequestBody = new ApiAuthentication( 1110590645,
            "5C321C026426ABCF32D71734478264FC", "AppTokenForInterview", false, false );

    private static final Logger LOGGER = Logger.getLogger( LevelMoneyClientImpl.class );

    private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM" );

    private static final String AVERAGE_KEY = "average";
    private static final String ACTUAL_KEY = "actual";
    private static final String EXPECTED_KEY = "expected";
    private static final String CREDIT_CARD_PAYMENT_KEY = "credit_card_payments";

    private static final Long MILLISECONDS_IN_24_HR = 86400000L;


    @Autowired
    private LevelMoneyClient client;

    @Override
    public Map<String, Budget> getMonthlyIncomeAndExpenses(Map<String, List<Object>> excludeTransactions, Boolean ignoreCCPayments) {
        Transactions transactions = client.getAllTransactions( authRequestBody );
        List<Transaction> txns = transactions.getTransactions( );
        Map<String, Budget> monthlyIncomeAndSpent = new LinkedHashMap<>( );
        if (!txns.isEmpty( )) {
            Set<Transaction> creditCardPayments = new LinkedHashSet<>();
            if(ignoreCCPayments!=null && ignoreCCPayments) {
                creditCardPayments = getCreditCardPayments( txns );
            }

            setMonthlyBudget( monthlyIncomeAndSpent, txns, excludeTransactions, creditCardPayments );
            Budget averageBudget = new Budget( );
            averageBudget = getAverageBudget( monthlyIncomeAndSpent );
            monthlyIncomeAndSpent.put( AVERAGE_KEY, averageBudget );
        }
        return monthlyIncomeAndSpent;

    }

    @Override
    public Map<String, List<Transaction>> getActualAndProjectedTransactions(Integer year, Integer month) {
        Map<String, List<Transaction>> actualExpectedTransactionsMap = new LinkedHashMap<>( );
        Transactions transactions = client.getAllTransactions( authRequestBody );
        List<Transaction> txns = transactions.getTransactions( );

        Map<String, List<Transaction>> monthlyTxnMap = getMonthlyTransactionMap( txns );


        DateTime startDateTimeOfCurrentMonth = new DateTime( year, month, 1, 0, 0 );
        Interval queriedPeriod = new Interval( startDateTimeOfCurrentMonth, startDateTimeOfCurrentMonth.plusMonths( 1
        ) );
        DateTime today = new DateTime( );
        //If Past month and year is queried we return actual txns assuming we have all past data
        String monthOfYear = year.toString( ) + "-" + month.toString( );
        String mapKey = null;
        if(month < 10) {
            mapKey = year.toString( ) + "-0" + month.toString();
        } else {
            mapKey = year.toString( ) + "-" + month.toString();
        }
        List<Transaction> pastTransactions = monthlyTxnMap.get(mapKey);
        if (queriedPeriod.isBefore( today )) {
            if (pastTransactions.isEmpty( )) {
                throw new DemoServiceValidationException( new Error( "monthOfYear", "Data is Archived for provided "
                        + "monthOfYear", 1000, monthOfYear ) );
            }
            actualExpectedTransactionsMap.put( ACTUAL_KEY, pastTransactions );
            return actualExpectedTransactionsMap;
        }
        else if (queriedPeriod.contains( today )) {

            if (!pastTransactions.isEmpty( )) {
                actualExpectedTransactionsMap.put( ACTUAL_KEY, pastTransactions );
            }
            actualExpectedTransactionsMap.put( EXPECTED_KEY, getForecastTransactions( monthlyTxnMap, year, month,
                    today ) );
        }
        else {
            actualExpectedTransactionsMap.put( EXPECTED_KEY, getForecastTransactions( monthlyTxnMap, year, month,
                    null ) );

        }
        return actualExpectedTransactionsMap;

    }

    @Override
    public Map<String, Set<Transaction>> getCreditCardPayments() {
        Transactions transactions = client.getAllTransactions( authRequestBody );
        List<Transaction> txns = transactions.getTransactions( );
        Set<Transaction> creditCardPayments = getCreditCardPayments(txns);
        Map<String, Set<Transaction>> creditCardPaymentsMap = new LinkedHashMap<>();
        creditCardPaymentsMap.put(CREDIT_CARD_PAYMENT_KEY, creditCardPayments);
        return creditCardPaymentsMap;
    }

    private Set<Transaction> getCreditCardPayments(List<Transaction> allTxns) {
        Set<Transaction> creditCardPayments = new LinkedHashSet<>();

        allTxns.forEach( txn -> {
            //Alreday added corresponding Txn will not need to be checked again
            if(!creditCardPayments.contains( txn )) {
                DateTime txnDateTime = new DateTime(txn.getTransactionTime());
                BigDecimal txnAmt = txn.getAmount();

                allTxns.forEach( correspondingTxn ->{
                    if(txnAmt.add(correspondingTxn.getAmount()).compareTo( BigDecimal.ZERO )==0) {
                        DateTime correspondingtxnDateTime = new DateTime( correspondingTxn.getTransactionTime( ) );
                        if(Math.abs(correspondingtxnDateTime.getMillis()-txnDateTime.getMillis()) <=
                                MILLISECONDS_IN_24_HR) {
                            creditCardPayments.add(txn);
                            //Add Corrsponding Txn as well to make it faster
                            creditCardPayments.add(correspondingTxn);

                        }
                    }


                } );
            }
        });
        return creditCardPayments;

    }


    private List<Transaction> getForecastTransactions(Map<String, List<Transaction>> monthlyTxnMap, Integer year,
            Integer month, DateTime from) {
        List<Transaction> expectedTransactions = new ArrayList<>( );

        //This is LinkedHashSet and will keep the order
        Set<String> monthsOfDataAvailability = monthlyTxnMap.keySet( );
        if (!monthsOfDataAvailability.isEmpty( )) {
            //This is LinkedHashSet and will keep the order
            Object[] monthsInOrder = monthsOfDataAvailability.toArray( );
            //Find txn for last to current month to forecast better for upcoming month
            int lastToCurrentMonthIndex = monthsInOrder.length - 2;
            String monthOfLastKnownData = (String) monthsInOrder[lastToCurrentMonthIndex];
            List<Transaction> lastKnownMonthTransaction = monthlyTxnMap.get( monthOfLastKnownData );
            lastKnownMonthTransaction.forEach( lastToCurrentMonthTxn -> {
                DateTime txnDateTime = new DateTime( lastToCurrentMonthTxn.getTransactionTime( ) );
                DateTime startOfMonthDateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
                int maxDayValueOfRespectiveMonth = startOfMonthDateTime.dayOfMonth().getMaximumValue();
                DateTime expectedDateTime = new DateTime( year, month, Math.min(maxDayValueOfRespectiveMonth,
                        txnDateTime.getDayOfMonth()), txnDateTime.getHourOfDay(), 0, 0 );
                if (null != from) {
                    if (txnDateTime.getDayOfMonth( ) >= from.getDayOfMonth( )) {
                        expectedTransactions.add( getExpectedTransaction( lastToCurrentMonthTxn, expectedDateTime ) );

                    }
                }
                else {
                    expectedTransactions.add( getExpectedTransaction( lastToCurrentMonthTxn, expectedDateTime ) );
                }


            } );
        }
        return expectedTransactions;
    }


    private Transaction getExpectedTransaction(Transaction lastToCurrentMonthTxn, DateTime expectedDateTime) {

        Transaction expectedTxn = new Transaction( lastToCurrentMonthTxn.getAmount( ), false, expectedDateTime
                .getMillis( ), lastToCurrentMonthTxn.getAccountId( ), null, null, lastToCurrentMonthTxn
                .getRawMerchant( ), lastToCurrentMonthTxn.getCategorization( ), lastToCurrentMonthTxn.getMerchant( ),
                expectedDateTime.toDate( ) );
        return expectedTxn;
    }


    private Map<String, List<Transaction>> getMonthlyTransactionMap(List<Transaction> txns) {
        Map<String, List<Transaction>> monthlyTxnMap = new LinkedHashMap<>( );
        txns.forEach( txn -> {
            Date txnTime = txn.getTransactionTime( );
            String yearMonthFormatValue = sdf.format( txnTime );
            List<Transaction> monthlyTxns = monthlyTxnMap.get( yearMonthFormatValue );
            if (monthlyTxns == null) {
                monthlyTxns = new ArrayList<Transaction>( );
                monthlyTxns.add( txn );
                monthlyTxnMap.put(yearMonthFormatValue,  monthlyTxns);
            }
            else {
                monthlyTxns.add( txn );
            }
        } );
        return monthlyTxnMap;
    }

    private void setMonthlyBudget(final Map<String, Budget> monthlyIncomeAndSpent, final List<Transaction> txns,
            final Map<String, List<Object>> excludeTransactions, final Set<Transaction> creditCardPayments) {

        txns.stream( ).forEach( txn -> {
            boolean filter = false;
            if (null != excludeTransactions && !excludeTransactions.isEmpty( )) {
                for (String fieldName : excludeTransactions.keySet( )) {
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor( fieldName, Transaction.class );
                        Method getter = pd.getReadMethod( );
                        Object fieldValue = getter.invoke( txn );
                        if (excludeTransactions.get( fieldName ).contains( fieldValue )) {
                            filter = true;
                            LOGGER.info( "filter : true for Txn : " + txn );

                        }
                    } catch (Exception e) {
                        LOGGER.error( "Exception encountered for getting field value : " + ExceptionUtils
                                .getStackTrace( e ) );
                        throw new DemoServiceException( new Error( "Service exception encountered", -1 ) );

                    }


                }
            }
            if(creditCardPayments.contains( txn )) {
                filter = true;
                LOGGER.info( " Credit Card Payment filter : true for Txn : " + txn );
            }

            if (!filter) {
                Date txnTime = txn.getTransactionTime( );
                String yearMonthFormatValue = sdf.format( txnTime );
                Budget budget = monthlyIncomeAndSpent.get( yearMonthFormatValue );
                if (budget != null) {
                    if (txn.getAmount( ).compareTo( BigDecimal.ZERO ) < 0) {
                        budget = budget.addSpent( txn.getAmount( ).abs( ) );
                    }
                    else {
                        budget = budget.addIncome( txn.getAmount( ) );
                    }
                }
                else {
                    budget = new Budget( );
                    if (txn.getAmount( ).compareTo( BigDecimal.ZERO ) < 0) {
                        budget.setSpent( txn.getAmount( ).abs( ) );
                    }
                    else {
                        budget.setIncome( txn.getAmount( ) );
                    }

                }
                //Map operation Outside IF Block since Changed Value needs to be put back again
                monthlyIncomeAndSpent.put( yearMonthFormatValue, budget );
            }

        } );


    }

    private Budget getAverageBudget(Map<String, Budget> monthlyIncomeAndSpent) {
        BigDecimal totalMonthlyIncome = BigDecimal.ZERO;
        BigDecimal totalMonthlySpent = BigDecimal.ZERO;
        for (String monthAndYear : monthlyIncomeAndSpent.keySet( )) {
            Budget budget = monthlyIncomeAndSpent.get( monthAndYear );
            totalMonthlyIncome = totalMonthlyIncome.add( budget.getIncome( ) );
            totalMonthlySpent = totalMonthlySpent.add( budget.getSpent( ) );
        }
        BigDecimal numberOfMonths = new BigDecimal( monthlyIncomeAndSpent.size( ) );
        Budget averageBudget = new Budget( totalMonthlySpent.divide( numberOfMonths, MathContext.DECIMAL32 ).setScale
                ( 2, RoundingMode.HALF_UP ), totalMonthlyIncome.divide( numberOfMonths, MathContext.DECIMAL32 )
                .setScale( 2, RoundingMode.HALF_UP ) );

        return averageBudget;

    }
}
