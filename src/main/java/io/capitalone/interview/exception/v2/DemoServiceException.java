package io.capitalone.interview.exception.v2;

import io.capitalone.interview.schema.v2.Error;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aseem80 on 4/6/17.
 */
public class DemoServiceException extends RuntimeException {

    List<Error> errors = new ArrayList<>(  );

    public DemoServiceException() {
    }

    public DemoServiceException(Error error) {
        errors.add(error);
    }

    public DemoServiceException(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
