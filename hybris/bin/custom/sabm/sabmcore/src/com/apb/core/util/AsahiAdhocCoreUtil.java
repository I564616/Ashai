package com.apb.core.util;

import com.apb.core.model.ConfigurationModel;
import com.apb.model.ApbHtmlCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

public class AsahiAdhocCoreUtil {

    private static final Logger LOGGER = Logger.getLogger(AsahiAdhocCoreUtil.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    public String getConfigValue(String configKey) {
        FlexibleSearchService flexibleSearchService = (FlexibleSearchService) Registry.getApplicationContext().getBean("flexibleSearchService");
        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery("SELECT {" + ConfigurationModel.PK + "} FROM {" + ConfigurationModel._TYPECODE + "} WHERE {" + ConfigurationModel.CONFIGKEY + "}=?configKey");
        fsQuery.addQueryParameter("configKey", configKey);
        SearchResult<ConfigurationModel> result = flexibleSearchService.search(fsQuery);
        Optional<ConfigurationModel> optional = result.getResult().stream().findAny();
        return optional.isPresent() ? optional.get().getConfigValue() : "";
    }

    public String getOrderDateInUserTimeZone(final String userTimeOffsetSec, final Date date){
        StringBuffer orderPlacedDate = new StringBuffer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar cal = new GregorianCalendar();
        DateTime userDateTime = getUserDateTime(userTimeOffsetSec);
        if(userDateTime!=null)
        {
        DateTimeZone zone = userDateTime.getZone();
        dateFormat.setTimeZone(zone.toTimeZone());

        cal.setTime(date);
        cal.setTimeZone(zone.toTimeZone());

        orderPlacedDate.append(new SimpleDateFormat("MMM").format(cal.getTime())).append(" ").append(cal.get(Calendar.DAY_OF_MONTH)).append(", ").append(cal.get(Calendar.YEAR)).append(" ").append(dateFormat.format(cal.getTime()));

        return orderPlacedDate.toString();
        }
        else
        {
      	  return userTimeOffsetSec;
        }
    }

    
    public DateTime getUserDateTime(final String userTimeOffsetSec)
    {
   	 DateTime userDateTime = null;
       
       if (!userTimeOffsetSec.isEmpty()) {
           char prefix = userTimeOffsetSec.charAt(0);
           final StringBuffer timeOffset = new StringBuffer();
           if (Character.toString(prefix).contains("-")) {
               timeOffset.append(userTimeOffsetSec.replace("-", "+"));
           } else if(Character.toString(prefix).contains("+")){
               timeOffset.append(userTimeOffsetSec.replace("+", "-"));
           }
           else {
               timeOffset.append("-").append(userTimeOffsetSec);
           }
           final int timeOffsetInt = Integer.parseInt(timeOffset.toString());
           userDateTime = new DateTime(DateTimeZone.forOffsetMillis(timeOffsetInt));
       }
       
       return userDateTime;
    }
    

    public ApbHtmlCMSComponentModel validateAndGetModel(final AbstractCMSComponentModel c) {
        if (c instanceof ApbHtmlCMSComponentModel) {
            LOGGER.info("the component [" + c.getUid() + "] is instance of apbhtmlcmscomponent ");
            return (ApbHtmlCMSComponentModel) c;
        } else {
            ApbHtmlCMSComponentModel expected = new ApbHtmlCMSComponentModel();
            expected.setCatalogVersion(c.getCatalogVersion());
            expected.setUid(c.getUid());

            try {
                return flexibleSearchService.getModelByExample(expected);
            } catch (ModelNotFoundException | AmbiguousIdentifierException e) {
                return null;
            }
        }
    }

    public boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        if (i >= minValueInclusive && i <= maxValueInclusive)
            return true;
        else
            return false;
    }
}
