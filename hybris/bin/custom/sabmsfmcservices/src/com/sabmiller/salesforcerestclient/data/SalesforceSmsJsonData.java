package com.sabmiller.salesforcerestclient.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * POJO Class for SMS Request To Data
 */
public class SalesforceSmsJsonData {

    
    @JsonProperty(value = "Event_Type__c", required = true)
    public String eventType;
    
    @JsonProperty(value = "Event_Id__c", required = true)
    public String eventId;
    
    @JsonProperty(value = "subscribe__c", required = true)
    public Boolean subscribe;

    @JsonProperty(value = "resubscribe__c", required = true)
    public Boolean resubscribe;

    @JsonProperty(value = "keyword__c", required = true)
    public String keyword;

    @JsonProperty(value = "override__c", required = true)
    public Boolean override;
    
    @JsonProperty(value = "mobileNumber__c", required = true)
    public String mobileNumber;
    
    @JsonProperty(value = "subscriberKey__c", required = true)
    public String subscriberKey;
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@JsonProperty(value = "transactionDate__c", required = true)
    private Date transactionDate;

        
    Map<String, Object> dynamicValue = new LinkedHashMap<>();
    
    private static Boolean DEFAULT_OVERRIDE=false;

    private static Boolean DEFAULT_SUBSCRIBE=true;

    private static Boolean DEFAULT_RESUBSCRIBE=true;

    private static String DEFAULT_SMS_KEYWORD="CUBSERVICES";

    public SalesforceSmsJsonData() {
        //default constructor. Fill in if necessary
    }

    /**
     * Overloaded constructor
     *
     * @param mobileNumber
     * @param subscriberKey
     * @param object
     */
    public SalesforceSmsJsonData(String mobileNumber, String subscriberKey, Object object, String eventId) {
        this.mobileNumber = mobileNumber;
        this.subscriberKey = subscriberKey;       
        this.eventType = "SMS";
        this.eventId = eventId;
        this.override = DEFAULT_OVERRIDE;
        this.subscribe = DEFAULT_SUBSCRIBE;
        this.resubscribe = DEFAULT_RESUBSCRIBE;
        this.keyword = DEFAULT_SMS_KEYWORD;
        setSubscriberAttributesData(object);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicValue() {
		return dynamicValue;
	}
    
    
    public Map<String, Object> setSubscriberAttributesData(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Map<String, Object> map = mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> sfMap = new LinkedHashMap<String, Object>();
        map.forEach((k, v) -> sfMap.put(k+"__c", v));
        if(sfMap.containsKey("transactionDate__c")){
        	String dateInString = (String)sfMap.get("transactionDate__c");
        	
        	if (StringUtils.isNotBlank(dateInString)){        		
        		Date date = getDate(dateInString, "MM/dd/yyyy hh:mm:ss a");        		
        		
        		this.transactionDate = date;
        		
    			SimpleDateFormat onlyTimeFormatter = new SimpleDateFormat("hh:mm aa");   
    			sfMap.put("transactiontime__c", onlyTimeFormatter.format(date));
        		
        	}
        	sfMap.remove("transactionDate__c");
		}
        Map<String, Object> dynamicData = getDynamicValue();
        dynamicData.putAll(sfMap);
        return dynamicData;
    }
    
    public Date getDate(final String dateStr, final String format)
	{
		if (StringUtils.isNotBlank(dateStr))
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				return new Date();				
			}
		}
		return new Date();
	}
}
