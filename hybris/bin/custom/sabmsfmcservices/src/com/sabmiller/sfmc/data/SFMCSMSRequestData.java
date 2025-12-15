package com.sabmiller.sfmc.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * POJO Class for SMS Request Data
 */
public class SFMCSMSRequestData {

    @JsonProperty(value = "subscribe", required = true)
    public Boolean subscribe;

    @JsonProperty(value = "resubscribe", required = true)
    public Boolean resubscribe;

    @JsonProperty(value = "keyword", required = true)
    public String keyword;

    @JsonProperty(value = "override", required = true)
    public Boolean override;

    @JsonProperty(value = "subscribers", required = true)
    public List<SFMCSmsRequestToData> subscribers;

    private static Boolean DEFAULT_OVERRIDE=false;

    private static Boolean DEFAULT_SUBSCRIBE=true;

    private static Boolean DEFAULT_RESUBSCRIBE=true;

    private static String DEFAULT_SMS_KEYWORD="CUBSERVICES";

    public SFMCSMSRequestData()
    {
        //Default constructor which can be set
    }

    /**
     * Default constructor which takes mutiple To list for sending multiple emails.
     * @param toList
     */
    public SFMCSMSRequestData(List<SFMCSmsRequestToData> toList){
        this.subscribers = toList;
        this.override = DEFAULT_OVERRIDE;
        this.subscribe = DEFAULT_SUBSCRIBE;
        this.resubscribe = DEFAULT_RESUBSCRIBE;
        this.keyword = DEFAULT_SMS_KEYWORD;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(final boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isResubscribe() {
        return resubscribe;
    }

    public void setResubscribe(final boolean resubscribe) {
        this.resubscribe = resubscribe;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }


    public List<SFMCSmsRequestToData> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(final List<SFMCSmsRequestToData> subscribers) {
        this.subscribers = subscribers;
    }

    public Boolean getOverride() {
        return override;
    }

    public void setOverride(final Boolean override) {
        this.override = override;
    }

}
