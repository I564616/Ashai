package com.sabmiller.core.invoices.converters.populators;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class InvoiceDiscrepancyNotificationsPopulator implements Populator<InvoiceDiscrepancyRequestNotificationEmailModel, CustomerData> {

    // @Resource(name = "customerConverter")
    // private Converter<UserModel, CustomerData> customerConverter;

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param target the source object
     * @param source the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceDiscrepancyRequestNotificationEmailModel source, CustomerData target) throws ConversionException {

        target.setName(source.getCustomerName());

        target.setEmail(source.getEmailAddress());

    }
}
