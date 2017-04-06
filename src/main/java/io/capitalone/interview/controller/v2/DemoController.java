package io.capitalone.interview.controller.v2;

import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.service.v2.CodingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by aseem80 on 4/4/17.
 */

@RestController
@RequestMapping("/budget")
public class DemoController {

    @Autowired
    private CodingAssignmentService codingAssignmentService;

    @RequestMapping("/home")
    String home() {
        return "Interview Assignment Home Page!";
    }

    @RequestMapping("/monthly")
    Map<String,Budget> monthlyBudget() {
        return codingAssignmentService.getMonthlyIncomeAndExpenses();
    }

}
