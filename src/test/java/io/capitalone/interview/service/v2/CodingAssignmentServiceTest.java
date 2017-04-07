package io.capitalone.interview.service.v2;

import io.capitalone.interview.restClient.v2.LevelMoneyClient;
import io.capitalone.interview.schema.v2.ApiAuthentication;
import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.schema.v2.Transaction;
import io.capitalone.interview.schema.v2.Transactions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

/**
 * Created by aseem80 on 4/6/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CodingAssignmentServiceTest {


    @MockBean
    private LevelMoneyClient client;

    @Autowired
    private CodingAssignmentService codingAssignmentService;



    @Test
    public void getMonthlyIncomeAndExpenses() {
        Mockito.when(client.getAllTransactions(any( ApiAuthentication.class ) )).thenReturn(getMockTransactions());
        Map<String,Budget> map = codingAssignmentService.getMonthlyIncomeAndExpenses(null,null);
        assertEquals(3, map.size());
        Budget month1Budget = map.get( "2017-01" );
        assertNotNull(month1Budget);
        assertTrue( new BigDecimal( 2500 ).compareTo(month1Budget.getIncome()) == 0);
        assertTrue( new BigDecimal( 520 ).compareTo(month1Budget.getSpent()) == 0);

        Budget month2Budget = map.get( "2017-02" );
        assertNotNull(month2Budget);
        assertTrue( new BigDecimal( 2000 ).compareTo(month2Budget.getIncome()) == 0);
        assertTrue( new BigDecimal( 0 ).compareTo(month2Budget.getSpent()) == 0);

        Budget average = map.get( "average" );

        assertNotNull(average);
        assertTrue( new BigDecimal( 2250 ).compareTo(average.getIncome()) == 0);
        assertTrue( new BigDecimal( 260 ).compareTo(average.getSpent()) == 0);


        Mockito.verify(client, Mockito.times( 1 )).getAllTransactions(any( ApiAuthentication.class ));

    }

    private Transactions getMockTransactions() {
        Transactions transactions = new Transactions();
        transactions.setError( "no-error" );
        List<Transaction> txns = new ArrayList<>();
        Transaction incomeTxn1 = new Transaction();
        incomeTxn1.setAmount( new BigDecimal( 1000 ) );
        incomeTxn1.setTransactionTime( new DateTime(2017,1,1,0,0,0).toDate() );
        txns.add( incomeTxn1 );


        Transaction spentTxn1 = new Transaction();
        spentTxn1.setAmount( new BigDecimal( -500 ) );
        spentTxn1.setTransactionTime( new DateTime(2017,1,2,0,0,0).toDate() );
        txns.add( spentTxn1 );

        Transaction incomeTxn2 = new Transaction();
        incomeTxn2.setAmount( new BigDecimal( 1500 ) );
        incomeTxn2.setTransactionTime( new DateTime(2017,1,15,0,0,0).toDate() );
        txns.add( incomeTxn2 );

        Transaction incomeTxn3 = new Transaction();
        incomeTxn3.setAmount( new BigDecimal( 2000 ) );
        incomeTxn3.setTransactionTime( new DateTime(2017,2,15,0,0,0).toDate() );
        txns.add( incomeTxn3 );


        Transaction spentDonutTxn1 = new Transaction();
        spentDonutTxn1.setAmount( new BigDecimal( -20 ) );
        spentDonutTxn1.setTransactionTime( new DateTime(2017,1,2,0,0,0).toDate() );
        txns.add( spentDonutTxn1 );

        transactions.setTransactions(txns);
        return transactions;
    }
}
