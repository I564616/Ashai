package com.apb.core.payment.fz.net;

import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;

public class AsahiCreditCardPaymentProxy extends AsahiPaymentResource {
    @Autowired
    private SessionService sessionService;

    public HttpsURLConnection createPostConnection(final String url, final Object payloadObject,
                                                   final AsahiPaymentGatewayContext ctx) throws IOException {

        final int retry = asahiConfigurationService.getConfiguration().getInteger("asahi.credit.card.payment.max.attempt", 3);
        final String paymentMode = ctx.isDirectDebit() ? "Direct Debit## " : "Normal Payment## ";
        LOG.info("################# Payment request is going to be sent to FatZebra #################");
        LOG.info("Payment mode: " + paymentMode  + "Payment request --> " + GSON.toJson(payloadObject));
        try {
            return getConnection(url, payloadObject, ctx);
        } catch (IOException e) {
            if (asahiConfigurationService.getBoolean("asahi.payment.admin.retry", true)) {
                LOG.error("Exception occurred while creating connection, payment process will be tried again with admin access...", e);
                return sessionService.executeInLocalView(new SessionExecutionBody() {
                    int count = 0;
                    @Override
                    public HttpsURLConnection execute() {
                        count++;
                        try {
                            LOG.info("Trying to make payment attempt - " + count);
                            return getConnection(url, payloadObject, ctx);
                        } catch (IOException e) {
                            LOG.error("Exception in reattempt .", e);
                            if (count < retry) {
                                return this.execute();
                            }
                            return null;
                        }
                    }
                }, userService.getAdminUser());
            }
        }
        return null;
    }
}