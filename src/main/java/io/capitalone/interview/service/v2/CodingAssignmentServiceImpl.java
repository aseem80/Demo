package io.capitalone.interview.service.v2;

import io.capitalone.interview.restClient.v2.LevelMoneyClient;
import io.capitalone.interview.restClient.v2.LevelMoneyClientImpl;
import io.capitalone.interview.schema.v2.ApiAuthentication;
import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.schema.v2.Transaction;
import io.capitalone.interview.schema.v2.Transactions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Map<String,Budget> getMonthlyIncomeAndExpenses() {

        Transactions transactions = client.getAllTransactions(authRequestBody);
        List<Transaction> txns = transactions.getTransactions();
 //       txns.sort((Transaction t1, Transaction t2)->t2.getTransactionTime().compareTo(t1.getTransactionTime()));
        Map<String,Budget> monthlyIncomeAndSpent = new LinkedHashMap<>();
        txns.stream().forEach( txn -> {
            Date txnTime = txn.getTransactionTime();
            String yearMonthFormatValue = sdf.format(txnTime);
            Budget budget = monthlyIncomeAndSpent.get(yearMonthFormatValue);
            if(budget!=null) {

                if(txn.getAmount().compareTo( BigDecimal.ZERO ) < 0) {
                    budget = budget.addSpent(txn.getAmount().abs());
                } else {
                    budget = budget.addIncome(txn.getAmount());
                }
            } else {
                budget = new Budget();
                if(txn.getAmount().compareTo( BigDecimal.ZERO ) < 0) {
                    budget.setSpent(txn.getAmount().abs());
                } else {
                    budget.setIncome(txn.getAmount());
                }

            }
            //Map operation Outside IF Block since Changed Value needs to be put back again
            monthlyIncomeAndSpent.put(yearMonthFormatValue, budget);



        } );

        Budget averageBudget = new Budget();
        if(!monthlyIncomeAndSpent.isEmpty()) {
            averageBudget = getAverageBudget(monthlyIncomeAndSpent);
        }

        monthlyIncomeAndSpent.put( AVERAGE_KEY, averageBudget);

        return monthlyIncomeAndSpent;


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
