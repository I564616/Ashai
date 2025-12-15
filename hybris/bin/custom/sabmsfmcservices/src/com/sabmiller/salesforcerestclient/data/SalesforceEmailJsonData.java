package com.sabmiller.salesforcerestclient.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabmiller.sfmc.enums.SFMCEmailRequestType;
import com.sabmiller.sfmc.data.SFMCDynamicData;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * POJO Class for Request To data
 */
public class SalesforceEmailJsonData {

	@JsonProperty(value = "Event_Type__c", required = false)
    public String eventType;
    
    @JsonProperty(value = "Event_Id__c", required = false)
    public String eventId;
	
	@JsonProperty(value = "subscriberKey__c", required = true)
    public String subscriberKey;
	
	@JsonProperty(value = "address__c", required = true)
    private String address;
	
	@JsonProperty(value = "requestType__c", required = true)
    private String requestType;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@JsonProperty(value = "transactionDate__c", required = true)
    private Date transactionDate;
	
	Map<String, Object> dynamicValue = new LinkedHashMap<>();

    public SalesforceEmailJsonData() {
        //default constructor. Fill in if necessary
    }

    /**
     * Overloaded constructor to initate To object with to email address,  subscriber key and a dynamic object which is context object.
     *
     * @param address
     * @param subscriberKey
     * @param object
     */
    public SalesforceEmailJsonData(String address, String subscriberKey, Object object,String eventId) {
        this.address = address;
        this.subscriberKey = subscriberKey;
        //this.contactAttributes = new ContactAttributes(object);
        setSubscriberAttributesData(object);
        this.requestType = SFMCEmailRequestType.SYNC.toString();
        this.eventType = "Email";
        this.eventId = eventId;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
    
    public void setSubscriberKey(final String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    public String getSubscriberKey() {
        return subscriberKey;
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
