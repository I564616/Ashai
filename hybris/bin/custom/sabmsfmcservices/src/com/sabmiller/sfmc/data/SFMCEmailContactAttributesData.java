package com.sabmiller.sfmc.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  POJO Object for Email contact attributes
 */
public class SFMCEmailContactAttributesData {

    @JsonProperty(value = "subscriberAttributes", required = false)
    public SFMCDynamicData subscriberAttributes;

    public SFMCEmailContactAttributesData()
    {
        //Insert any default constructor code if there is any.
    }

    /**
     * Overloaded constructor for setting Dynamic data
     * @param emailSubscriberAttributesData
     */
    public SFMCEmailContactAttributesData(Map<String,Object> emailSubscriberAttributesData){
        this.subscriberAttributes=createDynamicData(emailSubscriberAttributesData);
    }

    private SFMCDynamicData createDynamicData(Map<String,Object> emailSubscriberAttributesData)
    {
        return new SFMCDynamicData(emailSubscriberAttributesData);
    }


    //getter
    public SFMCDynamicData getSubscriberAttributes() {
        return subscriberAttributes;
    }

    //setter
    public void setSubscriberAttributes(final SFMCDynamicData emailSubscriberAttributesData) {
        this.subscriberAttributes = emailSubscriberAttributesData;
    }
}
