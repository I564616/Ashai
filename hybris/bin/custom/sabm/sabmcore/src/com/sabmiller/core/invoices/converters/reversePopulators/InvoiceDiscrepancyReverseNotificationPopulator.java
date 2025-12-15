package com.sabmiller.core.invoices.converters.reversePopulators;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Created by zhuo.a.jiang on 13/9/18.
 */
public class InvoiceDiscrepancyReverseNotificationPopulator
        implements Populator<UserModel, InvoiceDiscrepancyRequestNotificationEmailModel> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(UserModel source, InvoiceDiscrepancyRequestNotificationEmailModel target) throws ConversionException {

        if (source instanceof B2BCustomerModel) {
            target.setEmailAddress(((B2BCustomerModel) source).getEmail());
           String user_Pk = ((B2BCustomerModel) source).getPk().toString();
            target.setUserPk(user_Pk);

        }
        if (source instanceof BDECustomerModel) {
            target.setEmailAddress(((BDECustomerModel) source).getEmail());
            String user_Pk = ((BDECustomerModel) source).getPk().toString();
            target.setUserPk(user_Pk);

        }
        target.setCustomerName(source.getName());
    }

}
