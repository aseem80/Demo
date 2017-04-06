package io.capitalone.interview.schema.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;


public class Error {

    private static final String MESSAGE_DELIMITER = "::";
    private String parameter;
    private String message;
    private Integer errorCode;
    private Object invalidValue;


    public Error() {
    }

    public Error(String message, Integer errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public Error(String parameter, String message, Integer errorCode) {
        this.parameter = parameter;
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(Object invalidValue) {
        this.invalidValue = invalidValue;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("parameter", parameter)
                .append("message", message)
                .append("errorCode", errorCode)
                .append("invalidValue", invalidValue)
                .toString();
    }
}

