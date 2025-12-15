package com.sabmiller.sfmc.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.sabmiller.sfmc.enums.SFMCRequestType;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.service.SabmSFMCService;
import com.sabmiller.sfmc.strategy.SABMGlobalSFMCStrategy;
import static com.sabmiller.sfmc.constants.SabmsfmcservicesConstants.*;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;

import com.sabmiller.salesforcerestclient.SFTokenRequest;
import com.sabmiller.salesforcerestclient.SFTokenResponse;
import com.sabmiller.salesforcerestclient.SABMSalesForceAccessTokenRequestHandler;
import com.sabmiller.sfmc.pojo.SFCompositeRequest;
import com.sabmiller.sfmc.pojo.SFCompositeSubRequest;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;
import com.sabmiller.salesforcerestclient.SabmSalesForceAsyncLogHelper;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservices.log.data.WebServiceLogData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.session.SessionService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Implementation class for SFMC Service
 */
public class SabmSFMCServiceImpl implements SabmSFMCService {

    @Resource(name = "sabmSalesForceAccessTokenRequestHandler")
    SABMSalesForceAccessTokenRequestHandler sabmSalesForceAccessTokenRequestHandler;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "sabmSalesForceAsyncLogHelper")
    private SabmSalesForceAsyncLogHelper asyncHelper;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "sessionService")
    private SessionService sessionService;

    private static final Logger LOG = LoggerFactory.getLogger(SabmSFMCServiceImpl.class);

    private Map<SFMCRequestType, SABMGlobalSFMCStrategy> strategyMap = new HashMap<>();

    @Override
    public Boolean sendEmail(SFMCRequest request)
            throws SFMCClientException, SFMCRequestPayloadException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {
       return this.strategyMap.get(SFMCRequestType.EMAIL).send(request);
    }

    @Override
    public Boolean sendSMS(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {
        return this.strategyMap.get(SFMCRequestType.SMS).send(request);
    }

    public Map<SFMCRequestType, SABMGlobalSFMCStrategy> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(final Map<SFMCRequestType, SABMGlobalSFMCStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

/**
 * HybrisSVOC : WebToCase Composite API Implementation
 */

/*
 * Keg Issue Request to salesforce
 */
    public SFCompositeResponse sendKegIssueRequest(final SabmKegIssueData data)
    {
        if (data == null){
            return buildResponse(FAILED_STATUS, "Request data is null.");
        }

        final SFTokenResponse tokenResponse = generateOauthToken();

        if (tokenResponse == null || ObjectUtils.isEmpty(tokenResponse.getAccessToken())) {
            return buildResponse(FAILED_STATUS, "Failed to retrieve Salesforce access token.");
        }

        final WebServiceLogData logData = new WebServiceLogData();
        logData.setUserId(data.getEmailAddress() != null ? data.getEmailAddress() : userService.getCurrentUser().getUid());
        logData.setSessionId(sessionService.getCurrentSession() != null
                ? sessionService.getCurrentSession().getSessionId() : "No current session");

        final ObjectMapper objectMapper = new ObjectMapper();
        final SFCompositeRequest compositeRequest = buildCompositeRequestBody(data);

        try {
            String requestJson = objectMapper.writeValueAsString(compositeRequest);
            logData.setRequest(requestJson);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize composite request", e);
            return buildResponse(FAILED_STATUS, "Error processing request payload.");
        }

        final String apiUrl = tokenResponse.getInstanceUrl() + SERVICES_DATA_STRING
                + configurationService.getConfiguration().getString(API_VERSION, DEFAULT_API_VERSION) + COMPOSITE;

        logData.setUrl(apiUrl);
        logData.setRequestDate(new Date());

        try {
            Optional<String> responseOpt = sendCompositeRequestToSalesforce(
            tokenResponse.getAccessToken(),
            tokenResponse.getInstanceUrl(),
            compositeRequest);

            // Log response in RestWebserviceLogs
            if (responseOpt.isPresent()) {
                String response = responseOpt.get();
                boolean isValid = validateCompositeResponse(response);
                logData.setFailed(!isValid);
                logData.setResponse(response);
                logData.setResponseStatus(isValid ? SUCCESS_STATUS : FAILED_STATUS);
                logData.setResponseDate(new Date());
                asyncHelper.traceRequest(logData);

                return isValid
                        ? buildResponse(SUCCESS_STATUS, "Keg issue case created successfully.")
                        : buildResponse(FAILED_STATUS, "Salesforce responded with errors during case creation.");

            } else {
                logData.setResponseStatus("No response");
                logData.setResponseDate(new Date());
                logData.setFailed(true);
                asyncHelper.traceRequest(logData);
                return buildResponse(FAILED_STATUS, "Failed to retrieve a valid response from Salesforce.");
            }

        } catch (Exception e) {
            LOG.error("Exception while processing Salesforce request: ", e);
            logData.setResponse("Exception: " + e.getMessage());
            logData.setResponseDate(new Date());
            logData.setResponseStatus(FAILED_STATUS);
            logData.setFailed(true);
            asyncHelper.traceRequest(logData);
            return buildResponse(FAILED_STATUS, "Exception occurred: " + e.getMessage());
        }
    }
/*
 * generates Oauth Token
 */
    public SFTokenResponse generateOauthToken()
    {
        try {
            SFTokenRequest sfTokenRequest = new SFTokenRequest();
            String authDetails = sfTokenRequest.getSaleforceAUthoniticateInfo();
            return sabmSalesForceAccessTokenRequestHandler.sendPostTokenRequest(authDetails, ADMIN_USER);
        } catch (Exception e) {
            LOG.error("Exception while generating Salesforce OAuth token", e);
            return null;
        }
    }

/*
 * Builds the different APIs in the composite API
 */
    private SFCompositeRequest buildCompositeRequestBody(final SabmKegIssueData data) {
        List<SFCompositeSubRequest> subRequests = new ArrayList<>();
        String apiVersion = configurationService.getConfiguration().getString(API_VERSION, DEFAULT_API_VERSION);

        // 1. Get Identifier
        final String identifierQuery = String.format(
                configurationService.getConfiguration().getString(QUERY_IDENTIFIER, DEFAULT_QUERY_IDENTIFIER),
                data.getKegBrand());
        subRequests.add(buildGetRequest(GET_IDENTIFIER_PARAMETER,
                SERVICES_DATA_STRING + apiVersion + QUERY_STRING + encodeSoql(identifierQuery)));

        // 2. Get Customer Trade Org Hierarchy
        final String hierarchyQuery = configurationService.getConfiguration().getString(
                QUERY_CUSTOMER_HIERARCHY, DEFAULT_QUERY_CUSTOMER_HIERARCHY);
        final String rawHierarchyUrl = SERVICES_DATA_STRING + apiVersion + QUERY_STRING + hierarchyQuery;
        subRequests.add(buildGetRequest(GET_CUSTOMER_TRADE_ORG_HIERARCHY_PARAMETER, rawHierarchyUrl));

        // 3. Get Outlet
        final String outletQuery = configurationService.getConfiguration().getString(
                QUERY_OUTLET, DEFAULT_QUERY_OUTLET);
        final String rawOutletUrl = SERVICES_DATA_STRING + apiVersion + QUERY_STRING + outletQuery;
        subRequests.add(buildGetRequest(GET_OUTLET_PARAMETER, rawOutletUrl));

        // 4. Get Contact
        final String userPk = data.getUserPk();
        final String rawEmail = data.getEmailAddress();

        //Handle Apostrophe in  attribute
        final String encodedEmail = (rawEmail != null && rawEmail.contains("'"))
                ? rawEmail.replace("'", "\\'") // two backslashes + apostrophe
                : rawEmail;

        final String contactQuery = String.format(
                configurationService.getConfiguration().getString(QUERY_CONTACT, DEFAULT_QUERY_CONTACT),
                userPk, encodedEmail, encodedEmail);
        subRequests.add(buildGetRequest(GET_CONTACT_PARAMETER,
                SERVICES_DATA_STRING + apiVersion + QUERY_STRING + encodeSoql(contactQuery)));

        // 5. Create Case
        Map<String, Object> caseBody = new HashMap<>();
        caseBody.put(SUPPLIED_EMAIL_PARAMETER, rawEmail);
        caseBody.put(ACCOUNT_ID_PARAMETER, GET_ACCOUNTID);
        caseBody.put(CONTACT_ID_PARAMETER, GET_CONTACTID);
        caseBody.put(RECORD_TYPE_ID_PARAMETER, configurationService.getConfiguration().getString(RECORD_TYPE_KEG_COMPLAINT)); // Record Type will be constant for each environment
        caseBody.put(OUTLET_PARAMETER, GET_OUTLET);
        caseBody.put(DESCRIPTION_PARAMETER, data.getKegProblem());
        caseBody.put(TYPE_PARAMETER, data.getRequestType());
        caseBody.put(ORIGIN_PARAMETER, configurationService.getConfiguration().getString(CASE_ORIGIN, DEFAULT_ORIGIN));
        caseBody.put(BUSINESS_UNIT_PARAMETER, configurationService.getConfiguration().getString(BUSINESS_UNIT, DEFAULT_BUSINESS_UNIT));
        caseBody.put(CONTACT_PHONE_NUMBER_PARAMETER, data.getPhoneNumber());
        caseBody.put(REASON_CODE_PARAMETER, data.getReasonCode());

        subRequests.add(buildPostRequest(CREATE_CASE_PARAMETER,
                SERVICES_DATA_STRING + apiVersion + SOBJECTS_CASE_STRING, caseBody));

        // 6. Get Product
        final String productQuery = String.format(
                configurationService.getConfiguration().getString(QUERY_PRODUCT, DEFAULT_QUERY_PRODUCT),
                data.getSku());
        subRequests.add(buildGetRequest(GET_PRODUCT_PARAMETER,
                SERVICES_DATA_STRING + apiVersion + QUERY_STRING + encodeSoql(productQuery)));

        // 7. Create CaseProduct
        Map<String, Object> caseProductBody = new HashMap<>();
        caseProductBody.put(CASE_PARAMETER, GET_CASE);
        caseProductBody.put(PRODUCT_PARAMETER, GET_PRODUCT);
        caseProductBody.put(KEG_SERIAL_NUMBER_PARAMETER, data.getKegNumber());
        caseProductBody.put(BEST_BEFORE_DATE_AVAILABLE_PARAMETER, data.getBestBeforeDateAvailable());

        if ("yes".equalsIgnoreCase(data.getBestBeforeDateAvailable())) {
            caseProductBody.put(BEST_BEFORE_DATE_PARAMETER, data.getBestBeforeDateString());
        }

        caseProductBody.put(PRODUCTION_PLANT_CODE_PARAMETER, data.getPlantcode());
        caseProductBody.put(TIME_CODE_PARAMETER, data.getTimecode());

        subRequests.add(buildPostRequest(CREATE_CASE_PRODUCT_PARAMETER,
                SERVICES_DATA_STRING + apiVersion + SOBJECTS_CASEPRODUCT_STRING, caseProductBody));

        SFCompositeRequest request = new SFCompositeRequest();
        request.setAllOrNone(true);
        request.setCompositeRequest(subRequests);
        return request;
    }

    /**
 * buildGetRequest : Helper method
 */
    private SFCompositeSubRequest buildGetRequest(String referenceId, String url) {
        SFCompositeSubRequest request = new SFCompositeSubRequest();
        request.setMethod("GET");
        request.setReferenceId(referenceId);
        request.setUrl(url);
        return request;
    }

/**
 * buildPostRequest : Helper method
 */
    private SFCompositeSubRequest buildPostRequest(String referenceId, String url, Map<String, Object> body) {
        SFCompositeSubRequest request = new SFCompositeSubRequest();
        request.setMethod("POST");
        request.setReferenceId(referenceId);
        request.setUrl(url);
        request.setBody(body);
        return request;
    }

/**
 * Sends the Composite API to Salesforce
 */

    private Optional<String> sendCompositeRequestToSalesforce(final String accessToken,final String instanceUrl,final SFCompositeRequest compositeRequest) {
        final String apiVersion = configurationService.getConfiguration()
                .getString(API_VERSION, DEFAULT_API_VERSION);
        final String compositeUrl = instanceUrl + SERVICES_DATA_STRING + apiVersion + COMPOSITE;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(compositeRequest);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(compositeUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.getBody());
            } else {
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            LOG.error("Salesforce composite API HTTP error: {}", e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing composite request body", e);
        } catch (Exception e) {
            LOG.error("Unexpected error calling Salesforce composite API", e);
        }

        return Optional.empty();
    }

/**
 * Validates the reponses of each API in the composite call
 */
    private boolean validateCompositeResponse(final String response) {
        if (ObjectUtils.isEmpty(response)) {
            return false;
        }
        try {
            final JsonNode root = new ObjectMapper().readTree(response);
            final JsonNode compositeResponses = root.get("compositeResponse");

            if (compositeResponses == null || !compositeResponses.isArray()) {
                LOG.error("Invalid Salesforce response: missing compositeResponse array");
                return false;
            }

            for (JsonNode node : compositeResponses) {
                int statusCode = node.path("httpStatusCode").asInt();
                if (statusCode >= RESPONSE_CODE_400) {
                    LOG.error("Composite API error in referenceID {}: {}", node.path("referenceId").asText(), node.path("body"));
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            LOG.error("Failed to parse Salesforce composite response", e);
            return false;
        }
    }


/**
 * Encodes the parameters in the URL so that it can be passed safely
 */
    private static String encodeSoql(final String soql) {
        try {
            return URLEncoder.encode(soql, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error encoding SOQL query: {}", soql, e);
            return soql;
        }
    }

/**
 * buildResponse : Helper method
 */
    public SFCompositeResponse buildResponse(final String status,final String message) {
        SFCompositeResponse response = new SFCompositeResponse();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }

}
