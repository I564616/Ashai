package com.sabmiller.storefront.controllers.pages.merchant.suite;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.facades.order.SABMCheckoutFacade;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.facade.SABMMerchantSuitePaymentFacade;

@Controller
@RequestMapping(value = "/postback")
public class WebHookController
{
    private static final Logger LOG = LoggerFactory.getLogger(WebHookController.class.getName());

    @Resource(name = "sabmCheckoutFacade")
    private SABMCheckoutFacade checkoutFacade;

    @Resource(name = "cartFacade")
    private CartFacade cartFacade;
    
    
    @Resource
    SABMMerchantSuitePaymentFacade sabmMerchantSuitePaymentFacade;

    public static final Pattern XSS_REGEX = Pattern.compile(Config.getString("XSS.REGEX", "\\b[A-Za-z0-9-\\s()$'\":;+-_]*\\b"));

    @PostMapping("/invoice")
    public void postbackInvoice(@RequestBody final String payload)
    {
        LOG.info("Webhook recieved for invoice " + payload);
    }

    @PostMapping("/checkout")
    public void postbackCheckout(@RequestBody final String payload)
    {
        LOG.info("Webhook recieved for checkout " + payload);
    }

    @GetMapping("/invoice")
    public void postbackInvoiceGET(@RequestBody final String payload)
    {
        LOG.info("Webhook recieved for invoice :GET " + payload);
    }

    @GetMapping("/checkout")
    public void postbackCheckoutGET(@RequestBody final String payload)
    {
        LOG.info("Webhook recieved for checkout : GET " + payload);
    }

    @PostMapping("/invoicen")
    public ResponseEntity postbackInvoices(HttpServletRequest request) throws IOException {
        final String json = IOUtils.toString(request.getInputStream());

        LOG.info("Webhook recieved for invoice ");
        try {
            sabmMerchantSuitePaymentFacade.processInvoiceAuthKeyCCTxnForPostback(json);
        } catch (SABMMerchantSuitePaymentErrorException e) {
            LOG.error("Error processing Invoice Payment: Merchant Suite Payment Exception", e);
        } catch (SABMMerchantSuiteConfigurationException | SABMMerchantSuiteAPIRequestInvalidException e ) {
            LOG.error("Error processing Invoice Payment: Data Exception", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/checkoutn")
    public ResponseEntity postbackCheckouts(HttpServletRequest request) throws IOException
    {
        final String json = IOUtils.toString(request.getInputStream());
        LOG.info("Webhook recieved for checkout ");
        try {
            SABMMerchantSuiteTransactionProcessData txnData = sabmMerchantSuitePaymentFacade.processCheckoutAuthKeyCCTxnForPostback(json);
            checkoutFacade.placeOrderForPostback(txnData);
        } catch (SABMMerchantSuitePaymentErrorException e) {
            LOG.error("Error processing Invoice Payment: Payment Exception", e);
        } catch (InvalidCartException e) {
        } catch (SABMMerchantSuiteConfigurationException e) {
            LOG.error("Error processing Invoice Payment: Data Exception", e);
        } catch (SABMMerchantSuiteAPIRequestInvalidException e) {
            LOG.error("Error processing Invoice Payment", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }



    protected boolean isInputValid(final String value)
    {
        if (StringUtils.isNotBlank(value))
        {
            final Matcher matcher = XSS_REGEX.matcher(value);

            if (StringUtils.length(value) > 255 || !matcher.matches())
            {
                return false;
            }
        }
        return true;
    }
}
