package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * POJO Class for Request To data
 */
public class SFMCEmailRequestToData extends SFMCRequestToData {

    @JsonProperty(value = "address", required = true)
    private String address;

    @JsonProperty(value = "contactAttributes", required = false)
    public ContactAttributes contactAttributes;

    public SFMCEmailRequestToData() {
        //default constructor. Fill in if necessary
    }

    /**
     * Overloaded constructor to initate To object with to email address,  subscriber key and a dynamic object which is context object.
     *
     * @param address
     * @param subscriberKey
     * @param object
     */
    public SFMCEmailRequestToData(String address, String subscriberKey, Object object) {
        this.address = address;
        this.subscriberKey = subscriberKey;
        this.contactAttributes = new ContactAttributes(object);
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public class ContactAttributes {
        @JsonProperty(value = "subscriberAttributes", required = false)
        public SFMCDynamicData subscriberAttributes;

        public ContactAttributes(Object object) {
            this.subscriberAttributes = getSubscriberAttributesData(object);
        }

        public SFMCDynamicData getSubscriberAttributes() {
            return subscriberAttributes;
        }

        public void setSubscriberAttributes(SFMCDynamicData subscriberAttributes) {
            this.subscriberAttributes = subscriberAttributes;
        }

        /**
         * Method to set Dynamic Data in the request object.
         *
         * @param object
         * @return
         */
        public SFMCDynamicData getSubscriberAttributesData(Object object) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            Map<String, Object> map = mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
            });
            return new SFMCDynamicData(map);
        }

    }

}
