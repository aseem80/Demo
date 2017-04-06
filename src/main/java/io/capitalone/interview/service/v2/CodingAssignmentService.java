package io.capitalone.interview.service.v2;

import io.capitalone.interview.schema.v2.Budget;

import java.util.List;
import java.util.Map;

/**
 * Created by aseem80 on 4/5/17.
 */
public interface CodingAssignmentService {

    Map<String,Budget> getMonthlyIncomeAndExpenses(Map<String, List<Object>> excludeTransactions);

}
