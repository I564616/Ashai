package com.apb.facades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.AsahiB2BUnitModel;


@SuppressWarnings("deprecation")
public class AsahiB2BUserFacadeImpl extends DefaultB2BUserFacade
{

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	private Populator<CustomerData, B2BCustomerModel> b2BCustomerReversePopulator;
	private CompanyB2BCommerceService companyB2BCommerceService;

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "asahiUserIntegrationService")
	private AsahiUserIntegrationService asahiUserIntegrationService;

	@Resource(name = "asahiConfigurationService")
 	private AsahiConfigurationService asahiConfigurationService;
	@Resource
	private CustomerNameStrategy customerNameStrategy;

	private static final String B2BAdminGroup = "b2badmingroup";
	private static final String SEND_CUSTOMER_TO_SF = "integration.salesforce.users.enable";
	private static final Logger LOG = LoggerFactory.getLogger(AsahiB2BUserFacadeImpl.class);

	@Override
	public void updateCustomer(final CustomerData customerData)
	{
		if(!asahiSiteUtil.isCub())
		{
   		validateParameterNotNullStandardMessage("customerData", customerData);
   		if (!asahiSiteUtil.isSga())
   		{
   			Assert.hasText(customerData.getTitleCode(), "The field [TitleCode] cannot be empty");
   		}
   		Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
   		Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
			B2BCustomerModel customerModel = null;
   		if (StringUtils.isEmpty(customerData.getUid()))
			{
				final UserModel user = asahiCoreUtil.checkIfUserExists(customerData.getEmail());
				if (null != user && user instanceof B2BCustomerModel
						&& sabmB2BCustomerService.isRegistrationAllowed(user, customerData.getUnit().getUid()))
				{
					customerModel = (B2BCustomerModel) user;
					if (BooleanUtils.isTrue(customerModel.isLoginDisabled()))
					{
						customerModel.setLoginDisabled(Boolean.FALSE);
					}
					//Preventive check , even though all inactive customers would have been marked as active with a one-time script execution
					if (BooleanUtils.isFalse(customerModel.getActive()))
					{
						customerModel.setActive(Boolean.TRUE);

						//add all b2bunits to list of disabled user list
						for (final PrincipalGroupModel group : customerModel.getGroups())
						{
							if (group instanceof AsahiB2BUnitModel)
							{
								final Collection<String> disabledUsers = new HashSet<String>();
								disabledUsers.addAll(((AsahiB2BUnitModel) group).getDisabledUser());
								disabledUsers.add(customerModel.getUid());
								((AsahiB2BUnitModel) group).setDisabledUser(disabledUsers);
								getModelService().save(group);
								customerModel.setModifiedtime(new Date());
								getModelService().save(customerModel);
							}
							else if (group instanceof B2BUnitModel)
							{
								final Collection<String> disabledUsers = new HashSet<String>();
								disabledUsers.addAll(((B2BUnitModel) group).getCubDisabledUsers());
								disabledUsers.add(customerModel.getUid());
								((B2BUnitModel) group).setCubDisabledUsers(disabledUsers);
								customerModel.setModifiedtime(new Date());
								getModelService().save(customerModel);
								getModelService().save(group);
							}

						}
					}
				}
				else
				{
					customerModel = this.getModelService().create(B2BCustomerModel.class);
					customerModel.setCreatedBy((B2BCustomerModel) getUserService().getCurrentUser());
				}
				//Set the false in case a new user created...
   			customerModel.setLoggedInBefore(true);
   		}
   		else
   		{
   			customerModel = getCompanyB2BCommerceService().getCustomerForUid(customerData.getUid());
   		}
			if (StringUtils.isBlank(customerData.getTitleCode()) && null != customerModel.getTitle())
			{
				customerData.setTitleCode(customerModel.getTitle().getCode());
			}
   		getB2BCustomerReverseConverter().convert(customerData, customerModel);
			if (asahiSiteUtil.isSga() && StringUtils.isEmpty(customerData.getUid()))
			{
				customerAccountService.setCustomerToken(customerModel);
			}
			final String[] splitName = getCustomerNameStrategy().splitName(customerModel.getName());
			if (splitName != null)
			{
				customerModel.setFirstName(splitName[0]);
				customerModel.setLastName(splitName[1]);
			}
			customerModel.setAsahiWelcomeEmailStatus(Boolean.TRUE);

   		getModelService().save(customerModel);
   		getModelService().refresh(customerModel);

   		//updateAdminPrivileges(customerData, customerModel);

   		if (asahiSiteUtil.isSga() && StringUtils.isNotBlank( customerData.getSamAccess()))
   		{
   			apbB2BUnitService.updateUserSamAccess(customerModel, customerData.getUnit().getUid(), customerData.getSamAccess());
   		}
			if (asahiSiteUtil.isSga() && StringUtils.isEmpty(customerData.getUid()))
			{
				customerAccountService.sendPasswordResetEmail(customerModel);
			}

		//Send newly created user to SF
			if(asahiConfigurationService.getBoolean(SEND_CUSTOMER_TO_SF, true))
			{
				asahiUserIntegrationService.sendUsersToSalesforce(Arrays.asList(customerModel));
			}
		}
		else
		{
			super.updateCustomer(customerData);
		}
	}

	private void updateAdminPrivileges(final CustomerData customerData, final B2BCustomerModel customerModel)
	{
		LOG.info("Updating admin privileges for " + customerModel.getName());

		final UserModel adminUserModel = getUserService().getCurrentUser();
		if (adminUserModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel adminCustModel = (B2BCustomerModel) adminUserModel;

			if (adminCustModel.getDefaultB2BUnit() instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) adminCustModel.getDefaultB2BUnit();
				final List<B2BCustomerModel> adminUsers = new ArrayList<B2BCustomerModel>(b2bUnit.getAdminUsers());
				if (!adminUsers.contains(customerModel) && customerData.getRoles().contains(B2BAdminGroup))
				{
					adminUsers.add(customerModel);
					b2bUnit.setAdminUsers(adminUsers);
					getModelService().save(b2bUnit);
					getModelService().refresh(b2bUnit);
				}
				else if (adminUsers.contains(customerModel) && !customerData.getRoles().contains(B2BAdminGroup))
				{
					adminUsers.remove(customerModel);
					b2bUnit.setAdminUsers(adminUsers);
					getModelService().save(b2bUnit);
					getModelService().refresh(b2bUnit);
				}
			}

		}
	}

	@Override
	public void disableCustomer(final String customerUid)
	{
		if(!asahiSiteUtil.isCub())
		{
		final UserModel userModel = getUserService().getUser(customerUid);
		final UserModel adminUserModel = getUserService().getCurrentUser();
		if ((userModel instanceof B2BCustomerModel) && (adminUserModel instanceof B2BCustomerModel))
		{
			final B2BCustomerModel customerModel = (B2BCustomerModel) userModel;
			final B2BCustomerModel adminCustModel = (B2BCustomerModel) adminUserModel;

			if (adminCustModel.getDefaultB2BUnit() instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) adminCustModel.getDefaultB2BUnit();
				final Collection<String> disabledUsers = new ArrayList<>(b2bUnit.getDisabledUser());
				disabledUsers.add(userModel.getUid());
				b2bUnit.setDisabledUser(disabledUsers);
				getModelService().save(b2bUnit);
				getModelService().refresh(b2bUnit);
				userModel.setModifiedtime(new Date());
				getModelService().save(userModel);
				getModelService().refresh(userModel);
			}
		}
		}
		else
		{
			super.disableCustomer(customerUid);
		}
	}

	@Override
	public void enableCustomer(final String customerUid)
	{
		super.enableCustomer(customerUid);
		if(!asahiSiteUtil.isCub())
		{
   		final UserModel userModel = getUserService().getUser(customerUid);
   		final UserModel adminUserModel = getUserService().getCurrentUser();
   		if ((userModel instanceof B2BCustomerModel) && (adminUserModel instanceof B2BCustomerModel))
   		{
   			final B2BCustomerModel custModel = (B2BCustomerModel) userModel;
   			final B2BCustomerModel currentCustModel = (B2BCustomerModel) adminUserModel;
   			final AsahiB2BUnitModel asahiUnit = (AsahiB2BUnitModel) currentCustModel.getDefaultB2BUnit();
   			final Collection<String> updatedList = new ArrayList<String>(asahiUnit.getDisabledUser());
   			updatedList.remove(custModel.getUid());
   			asahiUnit.setDisabledUser(updatedList);
   			getModelService().save(asahiUnit);
   			getModelService().refresh(asahiUnit);
				userModel.setModifiedtime(new Date());
				getModelService().save(userModel);
				getModelService().refresh(userModel);
   		}
		}
	}




	/**
	 * @return the b2bCustomerReversePopulator
	 */
	protected Populator<CustomerData, B2BCustomerModel> getB2BCustomerReversePopulator()
	{
		return b2BCustomerReversePopulator;
	}

	/**
	 * @param b2BCustomerReversePopulator
	 *           the b2bCustomerReversePopulator to set
	 */
	public void setB2BCustomerReversePopulator(final Populator<CustomerData, B2BCustomerModel> b2BCustomerReversePopulator)
	{
		this.b2BCustomerReversePopulator = b2BCustomerReversePopulator;
	}

	protected <T extends CompanyB2BCommerceService> T getCompanyB2BCommerceService()
	{
		return (T) companyB2BCommerceService;
	}

	public void setCompanyB2BCommerceService(final CompanyB2BCommerceService companyB2BCommerceService)
	{
		this.companyB2BCommerceService = companyB2BCommerceService;
	}

	/**
	 * @return the customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 *           the customerNameStrategy to set
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

}
