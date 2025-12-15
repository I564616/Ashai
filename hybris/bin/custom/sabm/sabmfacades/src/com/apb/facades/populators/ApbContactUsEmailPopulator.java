package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;
import com.apb.facades.contactust.data.DeliveryDiscrepancyData;
import com.apb.facades.contactust.data.PriceDiscrepancyData;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.model.DiscrepancyDetailsModel;
/**
 * ApbContactUsPopulator implementation of {@link Populator}
 *
 * Convert source(AsahiB2BUnitModel) to target(ApbContactUsData)
 *
 */
public class ApbContactUsEmailPopulator implements Populator<ContactUsQueryEmailModel, ApbContactUsData>
{

	@Override
	public void populate(final ContactUsQueryEmailModel source, final ApbContactUsData target) throws ConversionException
	{
		target.setAccountNumber(source.getAccountNumber());
		target.setCompanyName(source.getCompanyName());
		target.setName(source.getName());
		target.setContactNumber(source.getContactNumber());
		target.setEmailAddress(source.getEmailAddress());
		target.setRequestRefNumber(source.getReferenceNumber());
		target.setSubject(source.getSubject());
		target.setSubjectOther(source.getOtherSubject());
		target.setFurtherDetail(source.getFurtherDetail());
		final AsahiContactUsSaleRepData contactUsSalesData = new AsahiContactUsSaleRepData();
		contactUsSalesData.setName(source.getSalesRepName());
		contactUsSalesData.setEmailAddress(source.getSalesRepEmail());
		target.setAsahiContactUsSaleRepData(contactUsSalesData);

		// New attributes added here

		target.setMessage(source.getMessage());
		target.setAddInfo(source.getAddInfo());
		target.setDeliveryNumber(source.getDeliveryNumber());
		target.setEnquiryType(source.getEnquiryType());
		target.setEnquirySubType(source.getEnquirySubType());
		target.setContact(source.getContact());
		final List<PriceDiscrepancyData> priceDiscrepancyDtos = new ArrayList<PriceDiscrepancyData>();
		final List<DeliveryDiscrepancyData> deliveryDiscrepancyDtos = new ArrayList<DeliveryDiscrepancyData>();

		final List<DiscrepancyDetailsModel> discrepancies = source.getDiscrepancies();

		if(null != source.getEnquiryType() && source.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.INCORRECT_CHARGE.getCode())) {
			for (final DiscrepancyDetailsModel discrepancy : discrepancies) {
				final PriceDiscrepancyData pricedto = new PriceDiscrepancyData();
				pricedto.setMaterialNumber(discrepancy.getMaterialNumber());
				pricedto.setExpectedTotalPay(discrepancy.getExpectedTotalPay());
				pricedto.setAmtCharged(discrepancy.getAmtCharged());
				priceDiscrepancyDtos.add(pricedto);
			}
			target.setPriceDiscrepancyDTOs(priceDiscrepancyDtos);
		}

		if(null != source.getEnquiryType() && source.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.REPORT_DEL_ISSUE.getCode())) {
			for (final DiscrepancyDetailsModel discrepancy : discrepancies) {
				final DeliveryDiscrepancyData deliverydto = new DeliveryDiscrepancyData();
				deliverydto.setMaterialNumber(discrepancy.getMaterialNumber());
				deliverydto.setQtyWithDelIssue(discrepancy.getQtyWithDelIssue());
				deliverydto.setQtyReceived(discrepancy.getQtyReceived());
				deliverydto.setExpectedQty(discrepancy.getExpectedQty());
				deliveryDiscrepancyDtos.add(deliverydto);
			}
			target.setDeliveryDiscrepancyDTOs(deliveryDiscrepancyDtos);
		}

	}
}
