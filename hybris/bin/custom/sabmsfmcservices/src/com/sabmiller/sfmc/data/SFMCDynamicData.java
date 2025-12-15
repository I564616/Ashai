package com.sabmiller.sfmc.data;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * SFMC Dynamic Data is dynamic object which will be sent with the SMS and Email Payload as context object for Emails and SMS
 *
 */
public class SFMCDynamicData {

    protected Map<String,Object> attributes = new HashMap<String,Object>();

    public SFMCDynamicData( Map<String,Object> subscriberAttributes){
        this.attributes=subscriberAttributes;
    }

    // "any getter" needed for serialization
    @JsonAnyGetter
    public Map<String,Object> any() {
        return attributes;
    }

    // "any getter" needed for deserialization
    @JsonAnySetter
    public void set(String name, Object value) {
        attributes.put(name, value);
    }
}
