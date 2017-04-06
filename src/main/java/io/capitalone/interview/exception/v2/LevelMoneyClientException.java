package io.capitalone.interview.exception.v2;

import io.capitalone.interview.schema.v2.Error;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aseem80 on 4/4/17.
 */
public class LevelMoneyClientException extends RuntimeException{

    List<Error> errors = new ArrayList<>(  );

    public LevelMoneyClientException() {
    }

    public LevelMoneyClientException(Error error) {
        errors.add(error);
    }

    public LevelMoneyClientException(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
