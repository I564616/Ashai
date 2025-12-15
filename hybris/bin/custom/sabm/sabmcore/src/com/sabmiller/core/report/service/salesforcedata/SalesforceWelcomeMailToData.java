package com.sabmiller.core.report.service.salesforcedata;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabmiller.facades.salesforce.welcomemail.CustomerWelcomeMailData;


/**
 * POJO Class for SMS Request To Data
 */
public class SalesforceWelcomeMailToData
{


	@JsonProperty(value = "Event_Type__c", required = true)
	public String eventType;

	@JsonProperty(value = "Event_Id__c", required = true)
	public String eventId;

	Map<String, Object> dynamicValue = new LinkedHashMap<>();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty(value = "registrationDate__c", required = true)
	private Date registrationDate;


	public SalesforceWelcomeMailToData()
	{
		//default constructor. Fill in if necessary
	}

	/**
	 * Overloaded constructor
	 *
	 * @param mobileNumber
	 * @param subscriberKey
	 * @param object
	 */
	public SalesforceWelcomeMailToData(final Object object)
	{

		this.eventType = "Email";
		this.eventId = "Welcome Email";
		
		final Date regDate = ((CustomerWelcomeMailData) object).getRegistrationDate();
		this.registrationDate = regDate;
		
		setDynamicAttributesData(object);
	}

	@JsonAnyGetter
	public Map<String, Object> getDynamicValue()
	{
		return dynamicValue;
	}

	public Map<String, Object> setDynamicAttributesData(final Object object)
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		final Map<String, Object> map = mapper.convertValue(object, new TypeReference<Map<String, Object>>()
		{});
		final Map<String, Object> sfMap = new LinkedHashMap<String, Object>();
		map.forEach((k, v) -> sfMap.put(k + "__c", v));

		if (sfMap.containsKey("registrationDate__c"))
		{			
			sfMap.remove("registrationDate__c");
		}

		final Map<String, Object> dynamicData = getDynamicValue();
		dynamicData.putAll(sfMap);
		return dynamicData;
	}

}
