package com.apb.storefront.util;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;

import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.facades.kegreturn.data.ApbKegReturnData;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.storefront.checkout.form.CustomerCheckoutForm;
import com.apb.storefront.forms.ApbKegReturnForm;
import com.apb.storefront.forms.ApbKegReturnKegSizForm;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * ApbKegReturnUtil for Manage Keg Return email of Keg return of keg return and checkout page
 */
public class ApbKegReturnUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbCompanyDataUtil.class);

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	/**
	 * Form Value Set into ApbKegReturnData
	 *
	 * @param form
	 *
	 * @return apbKegReturnData
	 */
	public ApbKegReturnData setKegReturnData(final Object form)
	{
		if (form instanceof CustomerCheckoutForm)
		{
			final CustomerCheckoutForm storeFrom = (CustomerCheckoutForm) form;
			return getKegReturnDataCustomerCheckoutForm(storeFrom);
		}
		else if (form instanceof ApbKegReturnForm)
		{
			final ApbKegReturnForm storeFrom = (ApbKegReturnForm) form;
			return getKegReturnDataKegReturnForm(storeFrom);
		}
		return null;
	}

	/**
	 * @param kegReturnForm
	 * @return apbKegReturnData
	 */
	private ApbKegReturnData getKegReturnDataKegReturnForm(final ApbKegReturnForm kegReturnForm)
	{
		final ApbKegReturnData apbKegReturnData = new ApbKegReturnData();
		if (CollectionUtils.isNotEmpty(kegReturnForm.getApbKegReturnKegSizForm()))
		{
			final List<KegSizeData> kegSizeDataList = new LinkedList<>();
			for (final ApbKegReturnKegSizForm apbKegReturnKegSizForm : kegReturnForm.getApbKegReturnKegSizForm())
			{
				if (!apbKegReturnKegSizForm.getKegQuantity().equals(0))
				{
					final KegSizeData kegSizeData = new KegSizeData();
					kegSizeData.setKegSize(apbKegReturnKegSizForm.getKegSize());
					kegSizeData.setKegQuantity(apbKegReturnKegSizForm.getKegQuantity());
					kegSizeDataList.add(kegSizeData);
				}
			}
			apbKegReturnData.setKegSize(kegSizeDataList);
		}
		apbKegReturnData.setPickupAddressId(kegReturnForm.getPickupAddress());
		apbKegReturnData.setKegComments(kegReturnForm.getKegComments());
		return apbKegReturnData;
	}

	/**
	 * After validate the page current Keg Size form data set
	 *
	 * @param customerCheckoutForm
	 * @return formDataExists
	 */
	public CustomerCheckoutForm validateKegReturnCustomerCheckoutForm(final CustomerCheckoutForm customerCheckoutForm)
	{
		final CustomerCheckoutForm formDataExists = new CustomerCheckoutForm();
		if (CollectionUtils.isNotEmpty(customerCheckoutForm.getApbKegReturnKegSizForm()))
		{
			final List<KegSizeData> kegSizeDataList = new LinkedList<>();
			for (final ApbKegReturnKegSizForm apbKegReturnKegSizForm : customerCheckoutForm.getApbKegReturnKegSizForm())
			{
				final KegSizeData kegSizeData = new KegSizeData();
				kegSizeData.setKegSize(apbKegReturnKegSizForm.getKegSize());
				kegSizeData.setKegQuantity(apbKegReturnKegSizForm.getKegQuantity());
				kegSizeDataList.add(kegSizeData);
			}
			formDataExists.setKegSizeDataList(kegSizeDataList);
		}
		formDataExists.setKegComments(customerCheckoutForm.getKegComments());
		formDataExists.setKegReturnFlag(customerCheckoutForm.getKegReturnFlag());
		return formDataExists;
	}

	/**
	 * @param customerCheckoutForm
	 * @param apbKegReturnData
	 */
	private ApbKegReturnData getKegReturnDataCustomerCheckoutForm(final CustomerCheckoutForm customerCheckoutForm)
	{
		final ApbKegReturnData apbKegReturnData = new ApbKegReturnData();
		if (CollectionUtils.isNotEmpty(customerCheckoutForm.getApbKegReturnKegSizForm()))
		{
			final List<KegSizeData> kegSizeDataList = new LinkedList<>();
			for (final ApbKegReturnKegSizForm apbKegReturnKegSizForm : customerCheckoutForm.getApbKegReturnKegSizForm())
			{
				if (!apbKegReturnKegSizForm.getKegQuantity().equals(0))
				{
					final KegSizeData kegSizeData = new KegSizeData();
					kegSizeData.setKegSize(apbKegReturnKegSizForm.getKegSize());
					kegSizeData.setKegQuantity(apbKegReturnKegSizForm.getKegQuantity());
					kegSizeDataList.add(kegSizeData);
				}
			}
			apbKegReturnData.setKegSize(kegSizeDataList);
		}
		apbKegReturnData.setPickupAddressId(customerCheckoutForm.getDeliveryAddressId());
		apbKegReturnData.setKegComments(customerCheckoutForm.getKegComments());
		return apbKegReturnData;
	}
}
