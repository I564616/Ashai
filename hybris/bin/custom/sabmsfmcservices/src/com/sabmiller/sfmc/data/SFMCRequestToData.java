package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO Class for To Object
 */
public class SFMCRequestToData {

    @JsonProperty(value = "subscriberKey", required = true)
    public String subscriberKey;

    public void setSubscriberKey(final String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    public String getSubscriberKey() {
        return subscriberKey;
    }

}
