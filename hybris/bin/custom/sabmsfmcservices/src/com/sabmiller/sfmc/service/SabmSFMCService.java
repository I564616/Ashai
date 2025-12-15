package com.sabmiller.sfmc.service;

import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;
import com.sabmiller.salesforcerestclient.SFTokenResponse;



/**
 * Interface for SFMC Service
 */
public interface SabmSFMCService {

    /**
     * Send Email method
     * @param request
     * @throws SFMCClientException
     * @throws SFMCRequestPayloadException
     * @throws SFMCRequestKeyNotFoundException
     * @throws SFMCEmptySubscribersException
     */
    public abstract Boolean sendEmail(SFMCRequest request)
            throws SFMCClientException, SFMCRequestPayloadException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException;

    /**
     * Send SMS method
     * @param request
     * @throws SFMCClientException
     * @throws SFMCRequestPayloadException
     * @throws SFMCRequestKeyNotFoundException
     * @throws SFMCEmptySubscribersException
     */
    public abstract Boolean sendSMS(SFMCRequest request)
            throws SFMCClientException, SFMCRequestPayloadException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException;

    /**
    * Generate SFMC Oauth Token
    */
    public SFTokenResponse generateOauthToken();

    /**
     * Send Keg Issue Composite request
     * @param SabmKegIssueData
     */
    public SFCompositeResponse sendKegIssueRequest(SabmKegIssueData data);

    /**
     * Build SFCompositeResponse
     * @param Status
     * @param Message
     */
    public SFCompositeResponse buildResponse(String status, String message);
}
