package io.capitalone.interview.restClient.v2;

import io.capitalone.interview.exception.v2.LevelMoneyClientException;
import io.capitalone.interview.schema.v2.ApiAuthentication;
import io.capitalone.interview.schema.v2.Error;
import io.capitalone.interview.schema.v2.Transactions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by aseem80 on 4/4/17.
 */
@Component
public class LevelMoneyClientImpl implements LevelMoneyClient{


    private static final Logger LOGGER = Logger.getLogger(LevelMoneyClientImpl.class);

    @Autowired
    private Client client;

    @Value("${demo.levelMoney.baseUrl}")
    private String levelMoneyBaseUrl;

    @Override
    public Transactions getAllTransactions(ApiAuthentication authenticationInfo) {
        Map<String,ApiAuthentication> requestBody = new HashMap<>();
        requestBody.put( "args", authenticationInfo);
        Transactions transactions = null;
        try {
            WebTarget webTarget = client.target( levelMoneyBaseUrl ).path( "get-all-transactions" );
            Invocation.Builder invocationBuilder = webTarget.request( MediaType.APPLICATION_JSON );
            Response response = invocationBuilder.post( Entity.entity(requestBody, MediaType
                    .APPLICATION_JSON_TYPE));
            int httpStatusCode = response.getStatus();
            LOGGER.info( "Received HTTP status code : " +  httpStatusCode);
            if(httpStatusCode==200) {
                transactions = response.readEntity( Transactions.class );
            } else {
                throw new LevelMoneyClientException(new Error("Expecting HTTP status code 200 but received : " +
                        httpStatusCode, 1000));
            }
        } catch(Exception e) {
            if(!(e instanceof LevelMoneyClientException)) {
                LOGGER.error( "Exception invoking get-all-transactions to LevelService : " + ExceptionUtils.getStackTrace( e ) );

                throw new LevelMoneyClientException( );
            } else {
                throw e;
            }
        }
        finally {
            client.close();
        }
        return transactions;
    }
}
