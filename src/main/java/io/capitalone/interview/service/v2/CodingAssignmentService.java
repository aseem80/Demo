package io.capitalone.interview.service.v2;

import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.schema.v2.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by aseem80 on 4/5/17.
 */
public interface CodingAssignmentService {

    Map<String,Budget> getMonthlyIncomeAndExpenses(Map<String, List<Object>> excludeTransactions, Boolean ignoreCCPayments);
    Map<String,List<Transaction>> getActualAndProjectedTransactions(Integer year, Integer month);
    Map<String,Set<Transaction>> getCreditCardPayments();

}
