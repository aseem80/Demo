package io.capitalone.interview.controller.v2;

import io.capitalone.interview.exception.v2.DemoServiceException;
import io.capitalone.interview.schema.v2.Budget;
import io.capitalone.interview.service.v2.CodingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.capitalone.interview.schema.v2.Error;

/**
 * Created by aseem80 on 4/4/17.
 */

@RestController
@RequestMapping("/budget")
public class DemoController {

    private static final Map<String,List<Object>> categoryToTxnFieldMap = new HashMap<>();
    private static final Map<String,String> categoryKeyToFieldMap = new HashMap<>();

    static {
        List<Object> donutMerchants = new ArrayList<>();
        donutMerchants.add("Krispy Kreme Donuts");
        donutMerchants.add("DUNKIN #336784");
        categoryToTxnFieldMap.put("donuts", donutMerchants);

        categoryKeyToFieldMap.put("donuts","merchant");
    }

    @Autowired
    private CodingAssignmentService codingAssignmentService;

    @RequestMapping("/home")
    public String home() {
        return "Interview Assignment Home Page!";
    }

    @RequestMapping("/monthlySummary")
    public Map<String,Budget> monthlyBudget(@RequestParam(value="ignoreCategory", required=false) List<String> excludeCategories) {
        Map<String, List<Object>> excludeTransactions = new HashMap<>();
        if(excludeCategories!=null && !excludeCategories.isEmpty()) {
            if(excludeCategories.size() > categoryToTxnFieldMap.size()) {
                throw new DemoServiceException(new Error("ignoreCategory", "Supported Ignore Categories" +
                        " Values : " + categoryToTxnFieldMap.keySet(), 1000, excludeCategories));
            }
            for(String category : excludeCategories) {
                if (!categoryToTxnFieldMap.keySet( ).contains( StringUtils.lowerCase( category ) )) {
                    throw new DemoServiceException( new Error("Service Exception encountered",-1));
                }
                excludeTransactions.put( categoryKeyToFieldMap.get( category ), categoryToTxnFieldMap.get( category ) );
            }


        }


        return codingAssignmentService.getMonthlyIncomeAndExpenses(excludeTransactions);
    }

}
