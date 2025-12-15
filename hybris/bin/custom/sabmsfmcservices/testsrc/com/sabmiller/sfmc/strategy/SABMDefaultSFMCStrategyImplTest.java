package com.sabmiller.sfmc.strategy;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.exacttarget.fuelsdk.ETClient;
import com.exacttarget.fuelsdk.ETRestConnection;
import com.exacttarget.fuelsdk.ETSdkException;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.sfmc.client.SabmSFMCClient;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;
import com.sabmiller.sfmc.strategy.impl.SABMDefaultSFMCStrategyImpl;

@UnitTest
public class SABMDefaultSFMCStrategyImplTest {

    @InjectMocks
    SABMDefaultSFMCStrategyImpl sabmDefaultSFMCStrategy;

    @Mock
    private ModelService modelService;

    @Mock
    protected SabmSFMCClient sabmSFMCClient;


    private ETClient etClient;

    private SFMCRequest sfmcRequest;

    private ETRestConnection etRestConnection;

    private static final String TEST_KEY_TO="TEST";

    private static final String TEST_PK="8809692332037test";

    private static final String TEST_EMAIL="test@test.com";

    private static final String DUMMY_USER_ID="dummyUser";

    private static final String DUMMY_PAYLOAD_REQUEST_RESPONSE="PAYLOAD";

    private static final String DUMMY_URL = "/messaging/v1/messageDefinitionSends/key:test/send";

    private static final String DEFAULT_CLIENT_ID="33xp2749gsk90pyb3mvj6fxp";

    private static final String DEFAULT_CLIENT_SECRET="Kli3bEXRkUSLxwKsuTvedlTO";


    private ETRestConnection.Response response;

    @Mock
    WebServiceLogModel webServiceLogModel;

    WebServiceLogData webServiceLogData;

    public Map strategyToKeyMap;

    @Before
    public void setUp() throws ETSdkException {
        MockitoAnnotations.initMocks(this);
        strategyToKeyMap = new HashMap<>();
        sfmcRequest= new SFMCRequest();
        sfmcRequest.setKey(TEST_KEY_TO);
        SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
        sfmcRequestTo.setPk(TEST_PK);
        sfmcRequestTo.setTo(TEST_KEY_TO);
        sfmcRequest.setToList(new ArrayList<>(Arrays.asList(sfmcRequestTo)));
        sfmcRequest.setInitiatorEmail(TEST_EMAIL);
        webServiceLogData = new WebServiceLogData();
        webServiceLogData.setUserId(DUMMY_USER_ID);
        webServiceLogData.setRequestDate(new Date());
        webServiceLogData.setRequest(DUMMY_PAYLOAD_REQUEST_RESPONSE);
        webServiceLogData.setResponse(DUMMY_PAYLOAD_REQUEST_RESPONSE);
        webServiceLogData.setUrl(DUMMY_URL);
    }

    @Test
    public void testSend()
            throws SFMCClientException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException, SFMCRequestPayloadException {
        Assert.assertTrue(sabmDefaultSFMCStrategy.send(sfmcRequest));
    }

    @Test
    public void testCreateUrl_withbatch()
    {
        Assert.assertNull(sabmDefaultSFMCStrategy.createUrl(TEST_KEY_TO,true));
    }

    @Test
    public void testCreateUrl_withoutbatch()
    {
        Assert.assertNull(sabmDefaultSFMCStrategy.createUrl(TEST_KEY_TO,false));
    }

    @Test
    public void testLogRequestAndResponse_save()
    {
        when(this.modelService.create(WebServiceLogModel.class)).thenReturn(webServiceLogModel);
        sabmDefaultSFMCStrategy.logRequestAndResponse(webServiceLogData);
        verify(this.modelService, times(1)).save(webServiceLogModel);
    }

}


