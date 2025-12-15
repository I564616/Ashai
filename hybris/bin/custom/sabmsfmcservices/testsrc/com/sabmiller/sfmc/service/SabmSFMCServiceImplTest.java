package com.sabmiller.sfmc.service;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.sfmc.enums.SFMCRequestType;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;
import com.sabmiller.sfmc.service.impl.SabmSFMCServiceImpl;
import com.sabmiller.sfmc.strategy.SABMGlobalSFMCStrategy;
import com.sabmiller.sfmc.strategy.impl.SABMEmailSFMCStrategy;
import com.sabmiller.sfmc.strategy.impl.SABMSmsSFMCStrategy;

@UnitTest
public class SabmSFMCServiceImplTest {

    SabmSFMCServiceImpl sabmSFMCService;

    private Map<SFMCRequestType, SABMGlobalSFMCStrategy> strategyMap;

    SFMCRequest sfmcRequest;

    @Mock
    SABMEmailSFMCStrategy sabmEmailSFMCStrategy;


    @Mock
    SABMSmsSFMCStrategy sabmSmsSFMCStrategy;



    @Rule
    public final ExpectedException exception = none();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        strategyMap = new HashMap<>();
        sabmSFMCService = new SabmSFMCServiceImpl();
        strategyMap.put(SFMCRequestType.EMAIL, sabmEmailSFMCStrategy);
        strategyMap.put(SFMCRequestType.SMS, sabmSmsSFMCStrategy);
        sabmSFMCService.setStrategyMap(strategyMap);
        sfmcRequest= new SFMCRequest();
        sfmcRequest.setKey("TEST");
        SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
        sfmcRequestTo.setPk("8809692332037test");
        sfmcRequestTo.setTo("Test");
        sfmcRequest.setToList(new ArrayList<>(Arrays.asList(sfmcRequestTo)));
        sfmcRequest.setInitiatorEmail("test@test.com");
    }


    @Test
    public void testSendSuccesfullEmail()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenReturn(true);
        Assert.assertTrue(sabmSFMCService.sendEmail(sfmcRequest));
    }

    @Test
    public void testSendUnsuccesfullEmail()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenReturn(false);
        Assert.assertFalse(sabmSFMCService.sendEmail(sfmcRequest));
    }


    @Test(expected=SFMCClientException.class)
    public void testSendUnsuccesfulldEmail_throwSFMCClientException()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenThrow(SFMCClientException.class);
        sabmSFMCService.sendEmail(sfmcRequest);
    }

    @Test(expected=SFMCRequestKeyNotFoundException.class)
    public void testSendUnsuccesfulldEmail_throwSFMCRequestKeyNotFoundException()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenThrow(SFMCRequestKeyNotFoundException.class);
        sabmSFMCService.sendEmail(sfmcRequest);
    }

    @Test(expected=SFMCEmptySubscribersException.class)
    public void testSendUnsuccesfulldEmail_throwSFMCEmptySubscribersException()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenThrow(SFMCEmptySubscribersException.class);
        sabmSFMCService.sendEmail(sfmcRequest);
    }

    @Test(expected=SFMCRequestPayloadException.class)
    public void testSendUnsuccesfulldEmail_throwSFMCRequestPayloadException()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        when(sabmEmailSFMCStrategy.send(sfmcRequest)).thenThrow(SFMCRequestPayloadException.class);
        sabmSFMCService.sendEmail(sfmcRequest);
    }
}
