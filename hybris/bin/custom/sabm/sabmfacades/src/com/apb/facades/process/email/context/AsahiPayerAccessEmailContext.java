package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.AsahiPayerAccessProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;


/**
 * The Context file for Payer Access Email
 */
public class AsahiPayerAccessEmailContext extends AbstractEmailContext<AsahiPayerAccessProcessModel>
{
	private static final String CUSTOMER_NAME = "customerName";
	private static final String CUSTOMER_NO = "customerNo";
	private static final String ACCESS_ID = "accessID";
	private static final String CREATED_BY_NAME = "superUserName";
	@Resource
 	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void init(final AsahiPayerAccessProcessModel payerAccessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(payerAccessProcessModel, emailPageModel);
		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) payerAccessProcessModel.getCustomer();
		put(DISPLAY_NAME, b2bCustomerModel.getName());
		if ((payerAccessProcessModel.getProcessDefinitionName().equalsIgnoreCase(
				ApbCoreConstants.PAYER_ACCESS_REQUEST_EMAIL_PROCESS) || payerAccessProcessModel.getProcessDefinitionName()
				.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_SUPERUSER_REQUEST_EMAIL_PROCESS))
				&& null != b2bCustomerModel.getDefaultB2BUnit() && b2bCustomerModel.getDefaultB2BUnit() instanceof AsahiB2BUnitModel)
		{
			AsahiB2BUnitModel payer = null;
			if(asahiSiteUtil.isSga()) {
				AsahiSAMAccessModel asahiSAMAccessModel = payerAccessProcessModel.getPayAccess();
				payer = asahiSAMAccessModel.getPayer();
			} else {
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit();
				payer = b2bUnit.getPayerAccount();
			}
			
			if (null != payer && null != payer.getEmailAddress())
			{
				put(EMAIL, payer.getEmailAddress());
			}
		}
		else
		{
			put(EMAIL, b2bCustomerModel.getUid());
		}
		if(asahiSiteUtil.isSga()) {
			AsahiSAMAccessModel asahiSAMAccessModel = payerAccessProcessModel.getPayAccess();
			AsahiB2BUnitModel b2bUnit = asahiSAMAccessModel.getParentAccount();
			if (StringUtils.isNotEmpty(b2bUnit.getLocName()))
			{
				put(CUSTOMER_NAME, b2bUnit.getLocName());
			}
			if (StringUtils.isNotEmpty(b2bUnit.getAccountNum()))
			{
				put(CUSTOMER_NO, b2bUnit.getAccountNum());
			}
		}
		else if (b2bCustomerModel.getDefaultB2BUnit() instanceof AsahiB2BUnitModel)
		{
			if (StringUtils.isNotEmpty(b2bCustomerModel.getDefaultB2BUnit().getLocName()))
			{
				put(CUSTOMER_NAME, b2bCustomerModel.getDefaultB2BUnit().getLocName());
			}
			if (StringUtils.isNotEmpty(((AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit()).getAccountNum()))
			{
				put(CUSTOMER_NO, (((AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit()).getAccountNum()));
			}
		}
		if (null != payerAccessProcessModel.getPayAccess())
		{
			put(ACCESS_ID, payerAccessProcessModel.getPayAccess().getPk().toString());
		}

		if (payerAccessProcessModel.getProcessDefinitionName().equalsIgnoreCase(
				ApbCoreConstants.PAYER_ACCESS_SUPERUSER_REQUEST_EMAIL_PROCESS)
				&& null != payerAccessProcessModel.getUser())
		{
			put(CREATED_BY_NAME, payerAccessProcessModel.getUser().getName());
		}
	}

	@Override
	protected BaseSiteModel getSite(final AsahiPayerAccessProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected LanguageModel getEmailLanguage(final AsahiPayerAccessProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	@Override
	protected CustomerModel getCustomer(final AsahiPayerAccessProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}
}
