package io.capitalone.interview.restClient.v2;

import io.capitalone.interview.schema.v2.ApiAuthentication;
import io.capitalone.interview.schema.v2.Transactions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.util.List;

/**
 * Created by aseem80 on 4/4/17.
 */
public interface LevelMoneyClient {

    @Retryable(maxAttempts=3,value={IOException.class},backoff = @Backoff(delay = 5000, multiplier=1.5))
    Transactions getAllTransactions(ApiAuthentication authenticationInfo);

}
