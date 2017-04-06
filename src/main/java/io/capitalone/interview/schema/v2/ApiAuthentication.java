package io.capitalone.interview.schema.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by aseem80 on 4/4/17.
 */
public class ApiAuthentication {

    private Integer uid;
    private String token;
    @JsonProperty(value="api-token")
    private String apiToken;
    @JsonProperty("json-strict-mode")
    private boolean jsonStrictMode = false;
    @JsonProperty("json-verbose-response")
    private boolean jsonVerboseResponse = true;


    public ApiAuthentication() {
    }

    public ApiAuthentication(Integer uid, String token, String apiToken, boolean jsonStrictMode, boolean
            jsonVerboseResponse) {
        this.uid = uid;
        this.token = token;
        this.apiToken = apiToken;
        this.jsonStrictMode = jsonStrictMode;
        this.jsonVerboseResponse = jsonVerboseResponse;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public boolean isJsonStrictMode() {
        return jsonStrictMode;
    }

    public void setJsonStrictMode(boolean jsonStrictMode) {
        this.jsonStrictMode = jsonStrictMode;
    }

    public boolean isJsonVerboseResponse() {
        return jsonVerboseResponse;
    }

    public void setJsonVerboseResponse(boolean jsonVerboseResponse) {
        this.jsonVerboseResponse = jsonVerboseResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass( ) != o.getClass( ))
            return false;

        ApiAuthentication that = (ApiAuthentication) o;

        return new EqualsBuilder( ).append( jsonStrictMode, that.jsonStrictMode )
                .append( jsonVerboseResponse, that.jsonVerboseResponse ).append( uid, that.uid ).append( token, that
                        .token ).append( apiToken, that.apiToken ).isEquals( );
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder( 17, 37 ).append( uid ).append( token ).append( apiToken ).append( jsonStrictMode ).
                append( jsonVerboseResponse ).toHashCode( );
    }

    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "uid", uid ).append( "token", token ).append( "apiToken", apiToken
        ).append( "jsonStrictMode", jsonStrictMode ).append( "jsonVerboseResponse", jsonVerboseResponse ).toString( );
    }
}
