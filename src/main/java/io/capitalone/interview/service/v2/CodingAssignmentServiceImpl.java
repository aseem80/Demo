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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aseem80 on 4/5/17.
 */
@Service
public class CodingAssignmentServiceImpl implements CodingAssignmentService {

    //A static value in API right now. Can be loaded dynamically from OAuth or CAS if available
    private static ApiAuthentication authRequestBody = new ApiAuthentication(1110590645,
            "5C321C026426ABCF32D71734478264FC",
            "AppTokenForInterview", false, false);

    private static final Logger LOGGER = Logger.getLogger(LevelMoneyClientImpl.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

    private static final String AVERAGE_KEY = "average";


    @Autowired
    LevelMoneyClient client;

    @Override
    public Map<String,Budget> getMonthlyIncomeAndExpenses(Map<String, List<Object>> excludeTransactions) {

        Transactions transactions = client.getAllTransactions(authRequestBody);
        List<Transaction> txns = transactions.getTransactions();
        Map<String,Budget> monthlyIncomeAndSpent = new LinkedHashMap<>();
        if(!txns.isEmpty()) {
            setMonthlyBudget(monthlyIncomeAndSpent, txns, excludeTransactions );
            Budget averageBudget = new Budget();
            averageBudget = getAverageBudget( monthlyIncomeAndSpent );
            monthlyIncomeAndSpent.put( AVERAGE_KEY, averageBudget );
        }

        return monthlyIncomeAndSpent;


    }


    private void setMonthlyBudget(Map<String,Budget> monthlyIncomeAndSpent, List<Transaction> txns,
            Map<String, List<Object>> excludeTransactions) {



        txns.stream().forEach( txn -> {
            boolean filter = false;
            if(null!=excludeTransactions && !excludeTransactions.isEmpty()) {
                for (String fieldName : excludeTransactions.keySet( )) {
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor( fieldName, Transaction.class );
                        Method getter = pd.getReadMethod( );
                        Object fieldValue = getter.invoke( txn );
                        if(excludeTransactions.get(fieldName).contains(fieldValue)) {
                            filter = true;
                            LOGGER.info("filter : true for field Value : " + fieldValue);

                        }
                    } catch(Exception e) {
                        LOGGER.error("Exception encountered for getting field value : " + ExceptionUtils
                                .getStackTrace(e));
                        throw new DemoServiceException( new Error("Service exception encountered", -1) );

                    }


                }
            }
                    if(!filter) {
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

    private Budget getAverageBudget(Map<String,Budget> monthlyIncomeAndSpent) {
        BigDecimal totalMonthlyIncome = BigDecimal.ZERO;
        BigDecimal totalMonthlySpent = BigDecimal.ZERO;
        for(String monthAndYear : monthlyIncomeAndSpent.keySet()) {
            Budget budget = monthlyIncomeAndSpent.get(monthAndYear);
            totalMonthlyIncome = totalMonthlyIncome.add(budget.getIncome());
            totalMonthlySpent = totalMonthlySpent.add(budget.getSpent());
        }
        BigDecimal numberOfMonths = new BigDecimal(monthlyIncomeAndSpent.size());
        Budget averageBudget = new Budget(totalMonthlySpent.divide(numberOfMonths, MathContext.DECIMAL32).setScale( 2,
                RoundingMode.HALF_UP ),
                totalMonthlyIncome.divide(
                numberOfMonths, MathContext.DECIMAL32).setScale( 2, RoundingMode.HALF_UP ));

        return averageBudget;

    }
}
