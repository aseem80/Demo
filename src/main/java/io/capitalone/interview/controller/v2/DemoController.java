package io.capitalone.interview.controller.v2;

import io.capitalone.interview.exception.v2.DemoServiceException;
import io.capitalone.interview.exception.v2.DemoServiceValidationException;
import io.capitalone.interview.exception.v2.LevelMoneyClientException;
import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.schema.v2.Transaction;
import io.capitalone.interview.service.v2.CodingAssignmentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.capitalone.interview.schema.v2.Error;

/**
 * Created by aseem80 on 4/4/17.
 */

@RestController
@RequestMapping("/demo/v2")
public class DemoController {

    private static final Map<String, List<Object>> categoryToTxnFieldMap = new HashMap<>( );
    private static final Map<String, String> categoryKeyToFieldMap = new HashMap<>( );

    private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM" );


    static {
        List<Object> donutMerchants = new ArrayList<>( );
        donutMerchants.add( "Krispy Kreme Donuts" );
        donutMerchants.add( "DUNKIN #336784" );
        categoryToTxnFieldMap.put( "donuts", donutMerchants );

        categoryKeyToFieldMap.put( "donuts", "rawMerchant" );
    }

    @Autowired
    private CodingAssignmentService codingAssignmentService;

    @RequestMapping("/home")
    public String home() {
        return "Interview Assignment Home Page!";
    }

    @RequestMapping("/monthlySummary")
    public Map<String, Budget> monthlyBudget(@RequestParam(value = "ignore-category", required = false) List<String>
            excludeCategories, @RequestParam(value="ignore-cc-payments", required=false) Boolean ignoreCCPayments) {
        Map<String, List<Object>> excludeTransactions = new HashMap<>( );
        if (excludeCategories != null && !excludeCategories.isEmpty( )) {
            if (excludeCategories.size( ) > categoryToTxnFieldMap.size( )) {
                throw new DemoServiceValidationException( new Error( "ignoreCategory", "Supported Ignore Categories"
                        + " Values : " + categoryToTxnFieldMap.keySet( ), 1000, excludeCategories ) );
            }
            for (String category : excludeCategories) {
                if (!categoryToTxnFieldMap.keySet( ).contains( StringUtils.lowerCase( category ) )) {
                    throw new DemoServiceValidationException( new Error( "ignoreCategory", "Supported Ignore " +
                            "Categories" + " Values : " + categoryToTxnFieldMap.keySet( ), 1000, excludeCategories ) );
                }
                excludeTransactions.put( categoryKeyToFieldMap.get( category ), categoryToTxnFieldMap.get( category ) );
            }


        }

        return codingAssignmentService.getMonthlyIncomeAndExpenses( excludeTransactions, ignoreCCPayments );
    }

    @RequestMapping("/getProjectedTransactionsForMonth")
    public Map<String, List<Transaction>> getProjectedTransactionsForAnyMonth(@RequestParam(value = "monthOfYear",
            required = false) String monthOfYear) {
        Integer month = null;
        Integer year = null;
        if (null != monthOfYear && !monthOfYear.isEmpty( )) {
            try {
                Date date = sdf.parse( monthOfYear );
                DateTime dateTime = new DateTime( date );
                year = dateTime.getYear( );
                month = dateTime.getMonthOfYear( );
            } catch (ParseException e) {
                throw new DemoServiceValidationException( new Error( "monthOfYear", "Supported monthOfYear format : "
                        + sdf.toPattern( ), 1000, monthOfYear ) );

            }
        }
        else {
            DateTime today = new DateTime( );
            year = today.getYear( );
            month = today.getMonthOfYear( );
        }
        return codingAssignmentService.getActualAndProjectedTransactions( year, month );

    }

    @RequestMapping("/creditCardPayments")
    public Map<String, Set<Transaction>> getCreditCardPayments() {
        return codingAssignmentService.getCreditCardPayments( );
    }


    @ExceptionHandler(DemoServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<Error> handleDemoServiceException(DemoServiceException e) {
        return e.getErrors( ); // use message from the original exception
    }

    @ExceptionHandler(DemoServiceValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleDemoServiceValidationException(DemoServiceValidationException e) {
        return e.getErrors( ); // use message from the original exception
    }

    @ExceptionHandler(LevelMoneyClientException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public List<Error> handleLevelMoneyClientException(LevelMoneyClientException e) {
        return e.getErrors( ); // use message from the original exception
    }


}
