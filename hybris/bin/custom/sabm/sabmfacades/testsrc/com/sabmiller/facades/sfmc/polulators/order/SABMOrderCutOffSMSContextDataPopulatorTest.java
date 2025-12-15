package com.sabmiller.facades.sfmc.polulators.order;

import com.sabmiller.facades.sfmc.context.SABMOrderCutOffSMSContextData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

@UnitTest
public class SABMOrderCutOffSMSContextDataPopulatorTest {

    private SABMOrderCutOffSMSContextDataPopulator sabmOrderCutOffSMSContextDataPopulator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sabmOrderCutOffSMSContextDataPopulator = new SABMOrderCutOffSMSContextDataPopulator();

    }

    @Test
    public void populateWithOutException() {

        Map map = new HashMap<>();
        map.put("accountNumber", "0085372615");
        map.put("cutOffPreference", Integer.valueOf(240));
        map.put("cutOffTime", "31/10/2018 12:00 PM");


        SABMOrderCutOffSMSContextData sabmOrderCutOffSMSContextData = new SABMOrderCutOffSMSContextData();


        sabmOrderCutOffSMSContextDataPopulator.populate(map, sabmOrderCutOffSMSContextData);

        /*Assert.assertEquals("12:00 PM",sabmOrderCutOffSMSContextData.getCutOffHour());*/
        Assert.assertEquals("12:00 PM",sabmOrderCutOffSMSContextData.getCutOffTime());
        Assert.assertEquals("31/10/2018",sabmOrderCutOffSMSContextData.getCutOffDay());
        Assert.assertEquals("240 minutes",sabmOrderCutOffSMSContextData.getCutOffPreference());
    }

    @Test
    public void populateWithException() {

        Map map = new HashMap<>();
        map.put("accountNumber", "0085372615");
        map.put("cutOffPreference", null);
        map.put("cutOffTime", "");


        SABMOrderCutOffSMSContextData sabmOrderCutOffSMSContextData = new SABMOrderCutOffSMSContextData();


        sabmOrderCutOffSMSContextDataPopulator.populate(map, sabmOrderCutOffSMSContextData);


        Assert.assertEquals(null,sabmOrderCutOffSMSContextData.getCutOffDay());
		/* Assert.assertEquals(null,sabmOrderCutOffSMSContextData.getCutOffHour()); */
        Assert.assertEquals(null,sabmOrderCutOffSMSContextData.getCutOffTime());
        Assert.assertEquals("null minutes", sabmOrderCutOffSMSContextData.getCutOffPreference() );
    }
}