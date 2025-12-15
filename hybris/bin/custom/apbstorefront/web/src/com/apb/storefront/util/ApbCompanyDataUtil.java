package com.apb.storefront.util;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.facades.checkout.data.B2BUnitDeliveryAddressData;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.forms.ApbCompanyDeliveryAddressForm;
import com.apb.storefront.forms.ApbCompanyDetailsForm;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * ApbCompanyDataUtil for company details page for sending email Notification(Customer Care Team)
 */
public class ApbCompanyDataUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbCompanyDataUtil.class);

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	/**
	 * @param source
	 * @param target
	 */
	public void convertBasic(final ApbCompanyData source, final ApbCompanyDetailsForm target)
	{
		target.setAccountNumber(source.getAccountNumber());
		target.setAcccountName(source.getAcccountName());
		target.setTradingName(source.getTradingName());
		target.setAbn(source.getAbn());
		target.setLiquorLicense(source.getLiquorLicense());
	}

	/**
	 * @param source
	 * @param target
	 */
	public void convert(final ApbCompanyData source, final ApbCompanyDetailsForm target)
	{
		convertBasic(source, target);
		target.setCompanyBillingAddress(source.getCompanyBillingAddress());
		target.setCompanyPhone(source.getCompanyPhone());
		target.setCompanyMobilePhone(source.getCompanyMobilePhone());
		target.setCompanyFax(source.getCompanyFax());
		target.setCompanyEmailAddress(source.getCompanyEmailAddress());
		target.setTradingName(source.getTradingName());
		target.setDeliveryCalendar(source.getDeliveryCalendar());
		target.setB2bUnitDeliveryAddressDataList(source.getDeliveryAddresses());
	}

	/**
	 * @param source
	 * @param target
	 */
	public void convertFromDateFrame(final ApbCompanyData source, final ApbCompanyDetailsForm target)
	{
		final List<B2BUnitDeliveryAddressData> newB2bUnitDeliveryAddressDataListNew = new LinkedList<B2BUnitDeliveryAddressData>();
		final List<ApbCompanyDeliveryAddressForm> apbCompanyDeliveryAddressFormList = target.getApbCompanyDeliveryAddressForm();

		if (CollectionUtils.isNotEmpty(apbCompanyDeliveryAddressFormList))
		{
			final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData = new B2BUnitDeliveryAddressData();
			final List<AddressData> addressDataList = new LinkedList<>();
			for (final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm : apbCompanyDeliveryAddressFormList)
			{

				final AddressData addressData = new AddressData();
				addressData.setDeliveryAddress(apbCompanyDeliveryAddressForm.getDeliveryAddress());
				addressData.setDeliveryTimeFrameFromHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH());
				addressData.setDeliveryTimeFrameFromMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM());
				addressData.setDeliveryTimeFrameToHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH());
				addressData.setDeliveryTimeFrameToMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM());
				addressData.setDeliveryInstruction(apbCompanyDeliveryAddressForm.getDeliveryInstruction());
				addressData.setDeliveryCalendar(apbCompanyDeliveryAddressForm.getDeliveryCalendar());
				addressDataList.add(addressData);
			}
			b2bUnitDeliveryAddressData.setDeliveryAddresses(addressDataList);
			newB2bUnitDeliveryAddressDataListNew.add(b2bUnitDeliveryAddressData);
		}
		target.setB2bUnitDeliveryAddressDataList(newB2bUnitDeliveryAddressDataListNew);
	}

	/**
	 * @param b2bUnitDeliveryAddressData
	 * @param b2bUnitDeliveryAddressDataNew
	 * @param count
	 * @param addressDataList
	 * @param apbCompanyDeliveryAddressForm
	 * @param addressData
	 */
	protected void checkDbDeliveryAddress(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData,
			final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressDataNew, final int counter,
			final List<AddressData> addressDataList, final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm,
			final AddressData addressData)
	{
		final int count = 0;
		if (b2bUnitDeliveryAddressData.getDeliveryAddresses() != null)
		{
			if (StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryAddress()) && !apbCompanyDeliveryAddressForm
					.getDeliveryAddress().equals(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryAddress()))
			{
				addressData.setDeliveryAddress(apbCompanyDeliveryAddressForm.getDeliveryAddress());
			}
			else
			{
				addressData.setDeliveryAddress(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryAddress());
			}
			timeFrameDeliveryAddress(b2bUnitDeliveryAddressData, count, apbCompanyDeliveryAddressForm, addressData);
			setDeliveryInstruction(b2bUnitDeliveryAddressData, count, apbCompanyDeliveryAddressForm);
			setDeliveryCalendar(b2bUnitDeliveryAddressData, count, apbCompanyDeliveryAddressForm);

			removeChangeDeliveryAddress(b2bUnitDeliveryAddressData, count, apbCompanyDeliveryAddressForm, addressData);
			addressData.setDeliveryInstruction(apbCompanyDeliveryAddressForm.getDeliveryInstruction());
			addressData.setDeliveryCalendar(apbCompanyDeliveryAddressForm.getDeliveryCalendar());
			changeRequestAddress(apbCompanyDeliveryAddressForm, addressData);
			if (apbCompanyDeliveryAddressForm.getChangeRequestAddressOnAddbutton() != null
					&& apbCompanyDeliveryAddressForm.getChangeRequestAddressOnAddbutton().equals(ApbStoreFrontContants.ADDED))
			{
				addressData.setAddressId(ApbStoreFrontContants.NA);
			}
			else
			{
				addressData.setAddressId(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(counter).getAddressId());
			}
			addressDataList.add(addressData);
			b2bUnitDeliveryAddressDataNew.setDeliveryAddresses(addressDataList);
		}
	}

	/**
	 * @param b2bUnitDeliveryAddressData
	 * @param b2bUnitDeliveryAddressDataNew
	 * @param count
	 * @param addressDataList
	 * @param apbCompanyDeliveryAddressForm
	 * @param addressData
	 */
	private void addedNewDeliveryAddressCheckFromDb(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData,
			final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressDataNew, final int count, final List<AddressData> addressDataList,
			final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm, final AddressData addressData)
	{
		if ((apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH() != null
				&& apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM() != null
				&& apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH() != null
				&& apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM() != null)
				|| (StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryAddress())
						|| StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryInstruction())
						|| StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryCalendar())))
		{
			if (b2bUnitDeliveryAddressData != null && b2bUnitDeliveryAddressData.getDeliveryAddresses().size() > 0)
			{
				checkDbDeliveryAddress(b2bUnitDeliveryAddressData, b2bUnitDeliveryAddressDataNew, count, addressDataList,
						apbCompanyDeliveryAddressForm, addressData);
			}
			else
			{
				addressData.setDeliveryAddress(apbCompanyDeliveryAddressForm.getDeliveryAddress());
				addressData.setDeliveryInstruction(apbCompanyDeliveryAddressForm.getDeliveryInstruction());
				addressData.setDeliveryCalendar(apbCompanyDeliveryAddressForm.getDeliveryCalendar());
				addressData.setDeliveryTimeFrameFromHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH());
				addressData.setDeliveryTimeFrameFromMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM());
				addressData.setDeliveryTimeFrameToHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH());
				addressData.setDeliveryTimeFrameToMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM());
				addressData.setChangeRequestAddress(apbCompanyDeliveryAddressForm.getChangeRequestAddressOnAddbutton());
				addressDataList.add(addressData);
				b2bUnitDeliveryAddressDataNew.setDeliveryAddresses(addressDataList);
			}
		}
	}

	/**
	 * @param b2bUnitDeliveryAddressData
	 * @param count
	 * @param apbCompanyDeliveryAddressForm
	 * @param addressData
	 */
	private void timeFrameDeliveryAddress(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData, final int count,
			final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm, final AddressData addressData)
	{
		if (apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH() != null
				&& !(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH()
						.equals(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameFromHH())))
		{
			addressData.setDeliveryTimeFrameFromHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromHH());
		}
		else
		{
			addressData.setDeliveryTimeFrameFromHH(
					b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameFromHH());
			changeRequestAddress(apbCompanyDeliveryAddressForm, addressData);
		}

		if (apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM() != null
				&& !(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM()
						.equals(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameFromMM())))
		{
			addressData.setDeliveryTimeFrameFromMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameFromMM());
			changeRequestAddress(apbCompanyDeliveryAddressForm, addressData);
		}
		else
		{
			addressData.setDeliveryTimeFrameFromMM(
					b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameFromMM());
		}

		if (apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH() != null
				&& !(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH()
						.equals(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameToHH())))
		{
			addressData.setDeliveryTimeFrameToHH(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToHH());
			changeRequestAddress(apbCompanyDeliveryAddressForm, addressData);
		}
		else
		{
			addressData
					.setDeliveryTimeFrameToHH(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameToHH());
		}
		if (apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM() != null
				&& !(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM()
						.equals(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameToMM())))
		{
			addressData.setDeliveryTimeFrameToMM(apbCompanyDeliveryAddressForm.getDeliveryTimeFrameToMM());
			changeRequestAddress(apbCompanyDeliveryAddressForm, addressData);
		}
		else
		{
			addressData
					.setDeliveryTimeFrameToMM(b2bUnitDeliveryAddressData.getDeliveryAddresses().get(count).getDeliveryTimeFrameToMM());
		}
	}

	/**
	 * @param b2bUnitDeliveryAddressData
	 * @param count
	 * @param apbCompanyDeliveryAddressForm
	 */
	private void setDeliveryCalendar(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData, final int count,
			final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm)
	{

		if (StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryCalendar()))
		{
			apbCompanyDeliveryAddressForm.setDeliveryCalendar(apbCompanyDeliveryAddressForm.getDeliveryCalendar());
		}
		else
		{
			apbCompanyDeliveryAddressForm.setDeliveryCalendar(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 * @param b2bUnitDeliveryAddressData
	 * @param count
	 * @param apbCompanyDeliveryAddressForm
	 */
	private void setDeliveryInstruction(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData, final int count,
			final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm)
	{

		if (StringUtils.isNotEmpty(apbCompanyDeliveryAddressForm.getDeliveryInstruction()))
		{
			apbCompanyDeliveryAddressForm.setDeliveryInstruction(apbCompanyDeliveryAddressForm.getDeliveryInstruction());
		}
		else
		{
			apbCompanyDeliveryAddressForm.setDeliveryInstruction(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 * @param apbCompanyDetailsForm
	 * @return b2bUnitDeliveryAddressDataList
	 */
	private List<B2BUnitDeliveryAddressData> validateDeliveryAddressFromDb(final ApbCompanyDetailsForm apbCompanyDetailsForm)
	{
		final List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList = new LinkedList<B2BUnitDeliveryAddressData>();
		for (final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData : apbCompanyDetailsForm
				.getB2bUnitDeliveryAddressDataList())
		{
			final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressDataNew = new B2BUnitDeliveryAddressData();
			int count = 0;
			final List<ApbCompanyDeliveryAddressForm> apbCompanyDeliveryAddressFormList = apbCompanyDetailsForm
					.getApbCompanyDeliveryAddressForm();
			final List<AddressData> addressDataList = new LinkedList<>();
			if (CollectionUtils.isNotEmpty(apbCompanyDeliveryAddressFormList))
			{
				for (final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm : apbCompanyDeliveryAddressFormList)
				{
					final AddressData addressData = new AddressData();
					try
					{
						addedNewDeliveryAddressCheckFromDb(b2bUnitDeliveryAddressData, b2bUnitDeliveryAddressDataNew, count,
								addressDataList, apbCompanyDeliveryAddressForm, addressData);
						count = count + 1;
					}
					catch (final IndexOutOfBoundsException ioe)
					{
						LOG.error("Delivery Address Data not found in B2B Unit ", ioe);
					}
				}
				b2bUnitDeliveryAddressDataList.add(b2bUnitDeliveryAddressDataNew);
			}
		}
		return b2bUnitDeliveryAddressDataList;

	}

	/**
	 * @param apbCompanyDetailsForm
	 * @return apbCompanyData
	 */
	public ApbCompanyData setUpdateCompanyDetailsData(final ApbCompanyDetailsForm apbCompanyDetailsForm)
	{
		final ApbCompanyData apbCompanyData = new ApbCompanyData();
		apbCompanyData.setAccountNumber(apbCompanyDetailsForm.getAccountNumber());
		apbCompanyData.setAcccountName(apbCompanyDetailsForm.getAcccountName());
		apbCompanyData.setAbn(apbCompanyDetailsForm.getAbn());
		apbCompanyData.setCompanyBillingAddress(apbCompanyDetailsForm.getCompanyBillingAddress());
		apbCompanyData.setCompanyEmailAddress(apbCompanyDetailsForm.getCompanyEmailAddress());
		apbCompanyData.setCompanyFax(apbCompanyDetailsForm.getCompanyFax());
		apbCompanyData.setCompanyMobilePhone(apbCompanyDetailsForm.getCompanyMobilePhone());
		final List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList = validateDeliveryAddressFromDb(
				apbCompanyDetailsForm);
		apbCompanyData.setDeliveryAddresses(b2bUnitDeliveryAddressDataList);
		apbCompanyData.setLiquorLicense(apbCompanyDetailsForm.getLiquorLicense());
		apbCompanyData.setSameasInvoiceAddress(apbCompanyDetailsForm.isSameasInvoiceAddress());
		apbCompanyData.setTradingName(apbCompanyDetailsForm.getTradingName());
		apbCompanyData.setCompanyPhone(apbCompanyDetailsForm.getCompanyPhone());
		return apbCompanyData;
	}

	/**
	 * @param apbCompanyDetailsForm
	 * @param companyData
	 */
	public void setFormDataValue(final ApbCompanyDetailsForm apbCompanyDetailsForm, final ApbCompanyData companyData)
	{
		companyData.setTradingName(apbCompanyDetailsForm.getTradingName());
		companyData.setCompanyEmailAddress(apbCompanyDetailsForm.getCompanyEmailAddress());
		companyData.setAbn(apbCompanyDetailsForm.getAbn());
		companyData.setLiquorLicense(apbCompanyDetailsForm.getLiquorLicense());
		companyData.setCompanyBillingAddress(apbCompanyDetailsForm.getCompanyBillingAddress());
		companyData.setCompanyPhone(apbCompanyDetailsForm.getCompanyPhone());
		companyData.setCompanyMobilePhone(apbCompanyDetailsForm.getCompanyMobilePhone());
		companyData.setCompanyFax(apbCompanyDetailsForm.getCompanyFax());
	}

	/**
	 * Current Form data set after validated from database value
	 *
	 * @param apbCompanyDetailsForm
	 * @return companyData
	 */
	public ApbCompanyDetailsForm checkValueFromDb(final ApbCompanyDetailsForm apbCompanyDetailsForm)
	{
		final ApbCompanyData companyData = sabmCustomerFacade.getB2BCustomerData();
		if (companyData != null)
		{
			if (StringUtils.isNotEmpty(companyData.getTradingName())
					&& (companyData.getTradingName().equalsIgnoreCase(apbCompanyDetailsForm.getTradingName())))
			{
				apbCompanyDetailsForm.setTradingName(ApbStoreFrontContants.NIL);
			}
			else if ((StringUtils.isEmpty(companyData.getTradingName()) || (StringUtils.isNotEmpty(companyData.getTradingName())
					&& !(companyData.getTradingName().equalsIgnoreCase(apbCompanyDetailsForm.getTradingName())))))
			{
				apbCompanyDetailsForm.setTradingName(apbCompanyDetailsForm.getTradingName());
			}
			if (StringUtils.isNotEmpty(companyData.getAbn())
					&& (companyData.getAbn().equalsIgnoreCase(apbCompanyDetailsForm.getAbn())))
			{
				apbCompanyDetailsForm.setAbn(ApbStoreFrontContants.NIL);
			}
			else if ((StringUtils.isEmpty(companyData.getAbn()) || (StringUtils.isNotEmpty(companyData.getAbn())
					&& !(companyData.getAbn().equalsIgnoreCase(apbCompanyDetailsForm.getAbn())))))
			{
				apbCompanyDetailsForm.setAbn(apbCompanyDetailsForm.getAbn());
			}
			liquorLicenseValidate(apbCompanyDetailsForm, companyData);
			if (StringUtils.isNotEmpty(companyData.getCompanyBillingAddress())
					&& (companyData.getCompanyBillingAddress().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyBillingAddress())))
			{
				apbCompanyDetailsForm.setCompanyBillingAddress(ApbStoreFrontContants.NIL);
			}
			else if ((StringUtils.isEmpty(companyData.getCompanyBillingAddress()) || (StringUtils
					.isNotEmpty(companyData.getCompanyBillingAddress())
					&& !(companyData.getCompanyBillingAddress().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyBillingAddress())))))
			{
				apbCompanyDetailsForm.setCompanyBillingAddress(apbCompanyDetailsForm.getCompanyBillingAddress());
			}
			companyPhoneValidate(apbCompanyDetailsForm, companyData);
			companyMobilePhoneValidate(apbCompanyDetailsForm, companyData);
			companyFaxValidate(apbCompanyDetailsForm, companyData);

			if (StringUtils.isNotEmpty(companyData.getCompanyEmailAddress())
					&& (companyData.getCompanyEmailAddress().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyEmailAddress())))
			{
				apbCompanyDetailsForm.setCompanyEmailAddress(ApbStoreFrontContants.NIL);
			}
			else if ((StringUtils.isEmpty(companyData.getCompanyEmailAddress()) || (StringUtils
					.isNotEmpty(companyData.getCompanyEmailAddress())
					&& !(companyData.getCompanyEmailAddress().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyEmailAddress())))))
			{
				apbCompanyDetailsForm.setCompanyEmailAddress(apbCompanyDetailsForm.getCompanyEmailAddress());
			}
		}
		return apbCompanyDetailsForm;
	}

	/**
	 * Liquor License (Optional) : Value changed = Display in Email Value
	 *
	 * Not Changed = Not Display
	 *
	 * Value Removed= Show <Nil>
	 *
	 * @param apbCompanyDetailsForm
	 * @param companyData
	 */
	private void liquorLicenseValidate(final ApbCompanyDetailsForm apbCompanyDetailsForm, final ApbCompanyData companyData)
	{
		if ((StringUtils.isNotEmpty(companyData.getLiquorLicense())
				&& (companyData.getLiquorLicense().equalsIgnoreCase(apbCompanyDetailsForm.getLiquorLicense())))
				|| (StringUtils.isEmpty(companyData.getLiquorLicense())
						&& StringUtils.isEmpty(apbCompanyDetailsForm.getLiquorLicense())))
		{
			apbCompanyDetailsForm.setLiquorLicense(ApbStoreFrontContants.NO_CHANGED);
		}
		else if (StringUtils.isEmpty(companyData.getLiquorLicense())
				&& StringUtils.isNotEmpty(apbCompanyDetailsForm.getLiquorLicense()))
		{
			apbCompanyDetailsForm.setLiquorLicense(apbCompanyDetailsForm.getLiquorLicense());
		}
		else if ((StringUtils.isNotEmpty(companyData.getLiquorLicense()))
				&& (StringUtils.isNotEmpty(apbCompanyDetailsForm.getLiquorLicense()))
				&& !(companyData.getLiquorLicense().equalsIgnoreCase(apbCompanyDetailsForm.getLiquorLicense())))
		{
			apbCompanyDetailsForm.setLiquorLicense(apbCompanyDetailsForm.getLiquorLicense());
		}
		else
		{
			apbCompanyDetailsForm.setLiquorLicense(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 * Company Fax (Optional) : Value changed = Display in Email Value
	 *
	 * Not Changed = Not Display
	 *
	 * Value Removed= Show <Nil>
	 *
	 * @param apbCompanyDetailsForm
	 * @param companyData
	 */
	private void companyFaxValidate(final ApbCompanyDetailsForm apbCompanyDetailsForm, final ApbCompanyData companyData)
	{
		if ((StringUtils.isNotEmpty(companyData.getCompanyFax())
				&& (companyData.getCompanyFax().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyFax())))
				|| (StringUtils.isEmpty(companyData.getCompanyFax()) && StringUtils.isEmpty(apbCompanyDetailsForm.getCompanyFax())))
		{
			apbCompanyDetailsForm.setCompanyFax(ApbStoreFrontContants.NO_CHANGED);
		}
		else if (StringUtils.isEmpty(companyData.getCompanyFax()) && StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyFax()))
		{
			apbCompanyDetailsForm.setCompanyFax(apbCompanyDetailsForm.getCompanyFax());
		}
		else if ((StringUtils.isNotEmpty(companyData.getCompanyFax()))
				&& (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyFax()))
				&& !(companyData.getCompanyFax().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyFax())))
		{
			apbCompanyDetailsForm.setCompanyFax(apbCompanyDetailsForm.getCompanyFax());
		}
		else
		{
			apbCompanyDetailsForm.setCompanyFax(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 * Company mobile phone(Optional) : Value changed = Display in Email Value
	 *
	 * Not Changed = Not Display
	 *
	 * Value Removed= Show <Nil>
	 *
	 * @param apbCompanyDetailsForm
	 * @param companyData
	 */
	private void companyMobilePhoneValidate(final ApbCompanyDetailsForm apbCompanyDetailsForm, final ApbCompanyData companyData)
	{
		if (StringUtils.isNotEmpty(companyData.getCompanyMobilePhone())
				&& (companyData.getCompanyMobilePhone().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyMobilePhone()))
				|| (StringUtils.isEmpty(companyData.getCompanyMobilePhone())
						&& StringUtils.isEmpty(apbCompanyDetailsForm.getCompanyMobilePhone())))
		{
			apbCompanyDetailsForm.setCompanyMobilePhone(ApbStoreFrontContants.NO_CHANGED);
		}
		else if (StringUtils.isEmpty(companyData.getCompanyMobilePhone())
				&& StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyMobilePhone()))
		{
			apbCompanyDetailsForm.setCompanyMobilePhone(apbCompanyDetailsForm.getCompanyMobilePhone());
		}
		else if ((StringUtils.isNotEmpty(companyData.getCompanyMobilePhone()))
				&& (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyMobilePhone()))
				&& !(companyData.getCompanyMobilePhone().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyMobilePhone())))
		{
			apbCompanyDetailsForm.setCompanyMobilePhone(apbCompanyDetailsForm.getCompanyMobilePhone());
		}
		else
		{
			apbCompanyDetailsForm.setCompanyMobilePhone(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 * Company Phone (Optional) : Value changed = Display in Email Value
	 *
	 * Not Changed = Not Display
	 *
	 * Value Removed= Show <Nil>
	 *
	 *
	 * @param apbCompanyDetailsForm
	 * @param companyData
	 */
	private void companyPhoneValidate(final ApbCompanyDetailsForm apbCompanyDetailsForm, final ApbCompanyData companyData)
	{
		if ((StringUtils.isNotEmpty(companyData.getCompanyPhone())
				&& (companyData.getCompanyPhone().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyPhone())))
				|| (StringUtils.isEmpty(companyData.getCompanyPhone())
						&& StringUtils.isEmpty(apbCompanyDetailsForm.getCompanyPhone())))
		{
			apbCompanyDetailsForm.setCompanyPhone(ApbStoreFrontContants.NO_CHANGED);
		}
		else if (StringUtils.isEmpty(companyData.getCompanyPhone())
				&& StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyPhone()))
		{
			apbCompanyDetailsForm.setCompanyPhone(apbCompanyDetailsForm.getCompanyPhone());
		}
		else if ((StringUtils.isNotEmpty(companyData.getCompanyPhone()))
				&& (StringUtils.isNotEmpty(apbCompanyDetailsForm.getCompanyPhone()))
				&& !(companyData.getCompanyPhone().equalsIgnoreCase(apbCompanyDetailsForm.getCompanyPhone())))
		{
			apbCompanyDetailsForm.setCompanyPhone(apbCompanyDetailsForm.getCompanyPhone());
		}
		else
		{
			apbCompanyDetailsForm.setCompanyPhone(ApbStoreFrontContants.NIL);
		}
	}

	/**
	 ** If removed delivery address
	 *
	 * removeRequestAddress is 1
	 *
	 * @param b2bUnitDeliveryAddressData
	 * @param count
	 * @param apbCompanyDeliveryAddressForm
	 * @param addressData
	 */
	private void removeChangeDeliveryAddress(final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData, final int count,
			final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm, final AddressData addressData)
	{
		if (apbCompanyDeliveryAddressForm.getRemoveRequestAddress() != null)
		{
			addressData.setRemoveRequestAddress(apbCompanyDeliveryAddressForm.getRemoveRequestAddress());
		}
	}

	/**
	 * If any changes in Delivery attribute attribute value
	 *
	 * changeRequestAddress is 1 - for Edit attribute value
	 *
	 * changeRequestAddress is 102 - for added new delivery address
	 *
	 * @param apbCompanyDeliveryAddressForm
	 * @param addressData
	 */
	private void changeRequestAddress(final ApbCompanyDeliveryAddressForm apbCompanyDeliveryAddressForm,
			final AddressData addressData)
	{
		if (apbCompanyDeliveryAddressForm.getChangeRequestAddress() != null
				&& apbCompanyDeliveryAddressForm.getChangeRequestAddressOnAddbutton() == null)
		{
			addressData.setChangeRequestAddress(apbCompanyDeliveryAddressForm.getChangeRequestAddress());
		}
		else
		{
			addressData.setChangeRequestAddress(apbCompanyDeliveryAddressForm.getChangeRequestAddressOnAddbutton());
		}
	}

	/**
	 * @return userFacade
	 */
	public UserFacade getUserFacade()
	{
		return userFacade;
	}

	/**
	 * @param userFacade
	 */
	public void setUserFacade(final UserFacade userFacade)
	{
		this.userFacade = userFacade;
	}

	/**
	 * @return i18NFacade
	 */
	public I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	/**
	 * @param i18nFacade
	 */
	public void setI18NFacade(final I18NFacade i18nFacade)
	{
		i18NFacade = i18nFacade;
	}

}
