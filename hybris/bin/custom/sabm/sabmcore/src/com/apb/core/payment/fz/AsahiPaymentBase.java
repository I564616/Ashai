package com.apb.core.payment.fz;

import com.apb.core.checkout.dao.impl.ApbCheckoutDaoImpl;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * Represents the base object class for payment integration  models, methods etc.
 */
public abstract class AsahiPaymentBase {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("AsahiPaymentBase");
    /**
     * Maps a response code to a message
     */
    protected static final String[] RESPONSE_MAP = {
            "Approved",
            "Refer to Card Issuer",
            "Refer to Card Issuer",
            "No Merchant",
            "Refer to Card Issuer",
            "Refer to Card Issuer",
            "Merchant/Acquirer Error",
            "Refer to Card Issuer",
            "Approved",
            "Acquirer Busy",
            "Approved",
            "Approved",
            "Invalid Transaction",
            "Invalid Amount",
            "Invalid Card Number",
            "No Issuer",
            "Approved",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Bank Not Supported",
            "Declined",
            "Expired Card",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Insufficient Funds",
            "Declined",
            "Declined",
            "Expired Card",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined - Please Retry",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined",
            "Declined"
    };

    /**
     * The GSON builder for output
     */
    public static final Gson PRETTY_GSON = new GsonBuilder().
            setPrettyPrinting().
            serializeNulls().
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
            create();

    /**
     * Outputs the object as a string representation
     * @return object as string
     */
    @Override public String toString() {
        return String.format(
                "<%s@%s id=%s> JSON: %s",
                this.getClass().getName(),
                System.identityHashCode(this),
                this.getIdString(),
                PRETTY_GSON.toJson(this));
    }

    /**
     * Gets the objects ID, if available
     * @return the object ID or an empty string
     */
    private Object getIdString() {
        try {
            Field idField = this.getClass().getDeclaredField("id");
            return idField.get(this);
        } catch (SecurityException e) {
        		LOGGER.error("Security Exception "+e.getMessage());
        	return "";
        } catch (NoSuchFieldException e) {
    		LOGGER.error("NoSuchField Exception "+e.getMessage());        	
            return "";
        } catch (IllegalArgumentException e) {
    		LOGGER.error("IllegalArgument Exception "+e.getMessage());        	
            return "";
        } catch (IllegalAccessException e) {
    		LOGGER.error("IllegalAccess Exception "+e.getMessage());        	
            return "";
        }
    }

    /**
     * Gets the response message from the transaction
     * @return the response message, or unknown
     */
    public String getResponseMessage() {
        try {
            Field responseField = this.getClass().getDeclaredField("response_code");
            String code = (String)responseField.get(this);

            return RESPONSE_MAP[Integer.parseInt(code)];
        } catch(Exception e) {
    		LOGGER.error(e.getMessage());         	
            return "Unknown";
        }
    }
}
