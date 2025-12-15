package com.sabmiller.core.invoices.converters.populators;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceItemData;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class InvoiceDiscrepancyPopulator implements Populator<InvoiceDiscrepancyRequestModel, SABMInvoiceDiscrepancyData> {

    private InvoiceDiscrepancyItemDataPopulator invoiceDiscrepancyItemDataPopulator;

    private InvoiceDiscrepancyNotificationsPopulator invoiceDiscrepancyNotificationsPopulator;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;



	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
    /**
     * The date format.
     */

    private final String dateFormat = "dd/MM/YYYY";

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(final InvoiceDiscrepancyRequestModel source, final SABMInvoiceDiscrepancyData target) throws ConversionException {

        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setInvoiceType(source.getType() != null ? source.getType().getCode() : null);

        if (source.getRaisedBy() != null) {
            if (BooleanUtils.isTrue(source.getRaisedByBDE())) {
                target.setRaisedBy(source.getRaisedByName() + " (CUB)");
                target.setRaisedByBDE(true);
            } else {
                target.setRaisedByBDE(false);
                target.setRaisedBy(source.getRaisedByName());
            }

            target.setRaisedBy_email(source.getRaisedBy().getEmail());
        }

		final String raisedBydate = SabmDateUtils.toString(getStoreDate(source.getRaisedDate()), dateFormat);
        target.setRaisedDate(raisedBydate);

        target.setExpectedTotalAmount(
                source.getTotalDiscountExpected() != null ? Double.toString(source.getTotalDiscountExpected()) : null);

        target.setFreightChargedAmount(
                source.getTotalFreightDiscountCharged() != null ? Double.toString(source.getTotalFreightDiscountCharged()) : null);

        target.setFreightExpectedAmount(
                source.getTotalFreightDiscountExpected() != null ? Double.toString(source.getTotalFreightDiscountExpected()) : null);

        target.setSoldTo(source.getB2bUnit() != null ? source.getB2bUnit().getName() : null);

        target.setRequestDescription(source.getDescription());

        //populate CreditAdjustmentStatus

        if (Objects.isNull(source.getProcessResult()) || (!Objects.isNull(source.getProcessResult()) && "PROCESSING"
                .equals(source.getProcessResult().getCode()))) {
            target.setCreditAdjustmentStatus("Received");
        }

        if (!Objects.isNull(source.getProcessResult()) && "APPROVED".equals(source.getProcessResult().getCode())) {
            target.setCreditAdjustmentStatus("Approved");
        }

        final List<SABMInvoiceItemData> invoices = new ArrayList<>();

        for (final InvoiceDiscrepancyRequestItemDetailModel model : source.getItems()) {

            final SABMInvoiceItemData invoiceItemData = new SABMInvoiceItemData();
            invoiceDiscrepancyItemDataPopulator.populate(model, invoiceItemData);

            invoices.add(invoiceItemData);

        }
        target.setInvoices(invoices);

        final List<String> notifications = new ArrayList<>();
        // include user who raised discrepancy into confirmation email name list
        if (source.getRaisedBy() != null) {
            if (BooleanUtils.isTrue(source.getRaisedByBDE())) {
                notifications.add(source.getRaisedBy().getName() + " (CUB)");
            } else {
                notifications.add(source.getRaisedBy().getName());
            }

        }

        for (final InvoiceDiscrepancyRequestNotificationEmailModel model : source.getConfirmationEmailList()) {

            final CustomerData data = new CustomerData();
            invoiceDiscrepancyNotificationsPopulator.populate(model, data);

            notifications.add(data.getName());

        }
        target.setNotificationList(notifications);

    }

    public InvoiceDiscrepancyItemDataPopulator getInvoiceDiscrepancyItemDataPopulator() {
        return invoiceDiscrepancyItemDataPopulator;
    }

    public void setInvoiceDiscrepancyItemDataPopulator(final InvoiceDiscrepancyItemDataPopulator invoiceDiscrepancyItemDataPopulator) {
        this.invoiceDiscrepancyItemDataPopulator = invoiceDiscrepancyItemDataPopulator;
    }

    public InvoiceDiscrepancyNotificationsPopulator getInvoiceDiscrepancyNotificationsPopulator() {
        return invoiceDiscrepancyNotificationsPopulator;
    }

    public void setInvoiceDiscrepancyNotificationsPopulator(
            final InvoiceDiscrepancyNotificationsPopulator invoiceDiscrepancyNotificationsPopulator) {
        this.invoiceDiscrepancyNotificationsPopulator = invoiceDiscrepancyNotificationsPopulator;
    }

    private Date getStoreDate(final Date raisedDate)
	{

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


		TimeZone storeTimeZone = null;

		//Getting BaseStore timezone
		if (baseStore != null && baseStore.getTimeZone() != null)
		{
			storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
		}

		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();
		Date storeTime = null;
		if (storeTimeZone != null)
		{
			storeTime = new Date(raisedDate.getTime() - serverTimeZone.getOffset(raisedDate.getTime()) + storeTimeZone.getOffset(raisedDate.getTime()));
		}
		else
		{
			storeTime =raisedDate;
		}

		final Calendar storeDateTime = Calendar.getInstance();
		storeDateTime.setTime(storeTime);

		return storeDateTime.getTime();

	}
}
