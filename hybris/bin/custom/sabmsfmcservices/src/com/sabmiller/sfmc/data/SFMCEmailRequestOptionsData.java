package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO Class for Request Options Data
 */
public class SFMCEmailRequestOptionsData {

    @JsonProperty(value = "requestType", required = true)
    private String requestType;

    public SFMCEmailRequestOptionsData()
    {
        // default constructor
    }

    public SFMCEmailRequestOptionsData(String requestType)
    {
        this.requestType=requestType;
    }

    public void setRequestType(final String requestType)
    {
        this.requestType = requestType;
    }

    public String getRequestType()
    {
        return requestType;
    }
}
