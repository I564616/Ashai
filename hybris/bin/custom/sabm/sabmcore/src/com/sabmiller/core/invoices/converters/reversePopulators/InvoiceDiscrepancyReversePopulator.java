package com.sabmiller.core.invoices.converters.reversePopulators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.core.enums.InvoiceDiscrepancyRaisedFromEnum;
import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import com.sabmiller.core.util.SabmNumberUtils;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceItemData;


/**
 * Created by zhuo.a.jiang on 20/8/18.
 */
public class InvoiceDiscrepancyReversePopulator implements Populator<SABMInvoiceDiscrepancyData, InvoiceDiscrepancyRequestModel>
{

	/**
	 * The b2b commerce unit service.
	 */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	private InvoiceDiscrepancyItemDataReversePopulator invoiceDiscrepancyItemDataReversePopulator;

	private InvoiceDiscrepancyReverseNotificationPopulator invoiceDiscrepancyReverseNotificationPopulator;


	@Resource(name = "userService")
	private UserService userService;


	@Resource(name = "modelService")
	private ModelService modelService;

	private final String DATE_PATTERN = "dd/MM/yyyy";

	private static final Logger LOG = Logger.getLogger(InvoiceDiscrepancyReversePopulator.class);

	/**
	 * Populate the target instance with values from the source instance.
	 *
	 * @param source
	 *           the source object
	 * @param target
	 *           the target to fill
	 * @throws ConversionException
	 *            if an error occurs
	 */
	@Override
	public void populate(final SABMInvoiceDiscrepancyData source, final InvoiceDiscrepancyRequestModel target)
			throws ConversionException
	{

		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

		try
		{
			target.setInvoiceNumber(source.getInvoiceNumber());

			if (!Objects.isNull(source.getInvoiceDate()))
			{
				try
				{
					target.setInvoiceDate(sdf.parse(source.getInvoiceDate()));
				}
				catch (final ParseException e)
				{
					LOG.error("parse date error:  " + source.getInvoiceDate());
				}
			}
			target.setDescription(source.getRequestDescription());
			target.setRaisedDate(new Date());
			target.setB2bUnit(b2bCommerceUnitService.getParentUnit());

			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) userService.getCurrentUser();
			target.setRaisedBy(b2bCustomer);
			target.setRaisedByName(b2bCustomer.getName());
			target.setRaisedByBDE(source.getRaisedByBDE());
			target.setRaisedFrom(InvoiceDiscrepancyRaisedFromEnum.HYBRIS);

			if (source.getInvoiceType().equals(InvoiceDiscrepancyType.FREIGHT.getCode()))
			{
				target.setType(InvoiceDiscrepancyType.FREIGHT);

				if (source.getFreightExpectedAmount() != null)
				{
					final String discountExpected = SabmNumberUtils
							.formattingDouble(Double.valueOf(source.getFreightExpectedAmount()));
					target.setTotalFreightDiscountExpected(Double.valueOf(discountExpected));
				}

				else
				{
					target.setTotalFreightDiscountExpected(null);
				}
				if (source.getFreightChargedAmount() != null)
				{
					final String discountCharged = SabmNumberUtils.formattingDouble(Double.valueOf(source.getFreightChargedAmount()));
					target.setTotalFreightDiscountCharged(Double.valueOf(discountCharged));
				}

				else
				{
					target.setTotalFreightDiscountCharged(null);
				}

			}
			if (source.getInvoiceType().equals(InvoiceDiscrepancyType.PRICE.getCode()))
			{
				target.setType(InvoiceDiscrepancyType.PRICE);

				final List<InvoiceDiscrepancyRequestItemDetailModel> models = new ArrayList<>();

				for (final SABMInvoiceItemData data : source.getInvoices())
				{

					final InvoiceDiscrepancyRequestItemDetailModel model = modelService
							.create(InvoiceDiscrepancyRequestItemDetailModel._TYPECODE);

					invoiceDiscrepancyItemDataReversePopulator.populate(data, model);

					models.add(model);
				}
				target.setItems(models);
			}

			/**
			 * set Notifications as List of InvoiceDiscrepancyRequestNotificationEmailModel
			 */

			final List<InvoiceDiscrepancyRequestNotificationEmailModel> notifications = new ArrayList<>();


			final List<String> ccEmailList = source.getNotificationList().stream()
					.filter(uid -> !uid.equals(userService.getCurrentUser().getUid())).collect(Collectors.toList());

			for (final String notifyUserId : ccEmailList)
			{
				final InvoiceDiscrepancyRequestNotificationEmailModel model = modelService
						.create(InvoiceDiscrepancyRequestNotificationEmailModel.class);

				//findExistingB2BCustomwer, it has to be an existing B2B or BDE customer
				final UserModel userModel = userService.getUserForUID(notifyUserId);

				if (!Objects.isNull(userModel))
				{
					invoiceDiscrepancyReverseNotificationPopulator.populate(userModel, model);

					notifications.add(model);
				}

			}
			target.setConfirmationEmailList(notifications);


		}
		catch (final ConversionException e)
		{
			LOG.error(e.getMessage(), e);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

	}

	public InvoiceDiscrepancyItemDataReversePopulator getInvoiceDiscrepancyItemDataReversePopulator()
	{
		return invoiceDiscrepancyItemDataReversePopulator;
	}

	public void setInvoiceDiscrepancyItemDataReversePopulator(
			final InvoiceDiscrepancyItemDataReversePopulator invoiceDiscrepancyItemDataReversePopulator)
	{
		this.invoiceDiscrepancyItemDataReversePopulator = invoiceDiscrepancyItemDataReversePopulator;
	}

	public InvoiceDiscrepancyReverseNotificationPopulator getInvoiceDiscrepancyReverseNotificationPopulator()
	{
		return invoiceDiscrepancyReverseNotificationPopulator;
	}

	public void setInvoiceDiscrepancyReverseNotificationPopulator(
			final InvoiceDiscrepancyReverseNotificationPopulator invoiceDiscrepancyReverseNotificationPopulator)
	{
		this.invoiceDiscrepancyReverseNotificationPopulator = invoiceDiscrepancyReverseNotificationPopulator;
	}

	
}
