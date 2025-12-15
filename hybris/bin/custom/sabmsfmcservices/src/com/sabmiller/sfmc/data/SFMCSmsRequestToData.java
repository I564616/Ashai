package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * POJO Class for SMS Request To Data
 */
public class SFMCSmsRequestToData extends SFMCRequestToData {

    @JsonProperty(value = "mobileNumber", required = true)
    public String mobileNumber;

    @JsonProperty(value = "attributes", required = false)
    public SFMCDynamicData attributes;

    public SFMCSmsRequestToData() {
        //default constructor. Fill in if necessary
    }

    /**
     * Overloaded constructor
     *
     * @param mobileNumber
     * @param subscriberKey
     * @param object
     */
    public SFMCSmsRequestToData(String mobileNumber, String subscriberKey, Object object) {
        this.mobileNumber = mobileNumber;
        this.subscriberKey = subscriberKey;
        this.attributes = getAttributes(object);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public SFMCDynamicData getAttributes(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Map<String, Object> map = mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
        });
        return new SFMCDynamicData(map);
    }

    public void setAttributes(SFMCDynamicData attributes) {
        this.attributes = attributes;
    }
}
