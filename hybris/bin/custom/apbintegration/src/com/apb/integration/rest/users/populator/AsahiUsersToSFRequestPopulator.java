package com.apb.integration.rest.users.populator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.integration.data.AsahiAccountInfo;
import com.apb.integration.data.AsahiUsersRequestData;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.apb.integration.util.AsahiModelsUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AsahiUsersToSFRequestPopulator implements Populator<B2BCustomerModel, AsahiUsersRequestData> {


	private final DateFormat dataDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final String ORDER_ONLY_ROLE = "orderOnly";

	private static final String PAY_ONLY_ROLE = "payOnly";

	private static final String ORDER_PAY_ROLE = "orderAndPay";

	private static final String SGA_COMPANY_CODE = "sga";

	@Resource(name = "asahiUserIntegrationService")
	private AsahiUserIntegrationService asahiUserIntegrationService;

	private CustomerNameStrategy customerNameStrategy;

	@Override
	public void populate(B2BCustomerModel source, AsahiUsersRequestData target) throws ConversionException {

		final String[] names = getCustomerNameStrategy().splitName(source.getName());
		if (names != null)
		{
			target.setFirstName(names[0]);
			target.setLastName(names[1]);
		}
		target.setMobile(StringUtils.isBlank(source.getContactNumber())?"":source.getContactNumber()); // Confirm for exact field where mobile number updates for b2bcustomer in ALB
		target.setEmail(source.getUid());
		target.setPk(source.getPk().getLong());
		target.setIsPasswordSet(StringUtils.isNotBlank(source.getEncodedPassword()));
		target.setLastLoginDate(getALBLastLoginDate(source));
		target.setRegistrationDate(source.getAsahiCreationTime()!=null?dataDateFormat.format(source.getAsahiCreationTime())
				:dataDateFormat.format(source.getCreationtime()));
		target.setFirstOnlineOrderDate(getALBFirstOnlineOrder(source));
		target.setLastOnlineOrderDate(getALBLastOnlineOrder(source));
		target.setLastOrderDate(getALBLastOrder(source));
		target.setActive(AsahiModelsUtil.isCustomerActiveForAsahiAccount(source));
		//linkedAccounts
		target.setLinkedAccounts(getALBLinkedAccountsToUser(source));

	}

	private String getALBLastLoginDate(final B2BCustomerModel customer){
		String returnDate = "";
		if (null != customer.getLastLogin())
		{
			returnDate = dataDateFormat.format(customer.getLastLogin());
		}
		return returnDate;
	}

	private String getALBFirstOnlineOrder(final B2BCustomerModel customer){
		Date first = new Date(0L);
		final OrderModel firstOrder =asahiUserIntegrationService.getALBFirstWebOrder(customer);
		if (firstOrder != null){
			first = firstOrder.getDate();
		}
		if (!first.equals(new Date(0L))){
			return dataDateFormat.format(first);
		}else{
			return StringUtils.EMPTY;
		}
	}

	private String getALBLastOrder(final B2BCustomerModel customer){
		Date last = new Date(0L);
		final OrderModel lastOrder =asahiUserIntegrationService.getALBLastOrder(customer);
		if (lastOrder != null){
			last = lastOrder.getDate();
		}
		if (!last.equals(new Date(0L))){
			return dataDateFormat.format(last);
		}else{
			return StringUtils.EMPTY;
		}
	}

	private String getALBLastOnlineOrder(final B2BCustomerModel customer){
		Date first = new Date(0L);
		final OrderModel firstOrder =asahiUserIntegrationService.getALBLastWebOrder(customer);
		if (firstOrder != null){
			first = firstOrder.getDate();
		}
		if (!first.equals(new Date(0L))){
			return dataDateFormat.format(first);
		}else{
			return StringUtils.EMPTY;
		}
	}

	private List<AsahiAccountInfo> getALBLinkedAccountsToUser(B2BCustomerModel customer){
		List<AsahiAccountInfo> linkedAccounts = new ArrayList<AsahiAccountInfo>();
		for(PrincipalGroupModel b2bUnit:customer.getGroups())
		{
			if(b2bUnit instanceof AsahiB2BUnitModel && StringUtils.isNotBlank(((AsahiB2BUnitModel) b2bUnit).getCompanyCode())
					&& ((AsahiB2BUnitModel) b2bUnit).getCompanyCode().equals(SGA_COMPANY_CODE)){
				AsahiAccountInfo account= new AsahiAccountInfo();
				account.setAccountNumber(b2bUnit.getUid());
				account.setTradingName(b2bUnit.getLocName());
				if(CollectionUtils.isNotEmpty(customer.getSamAccess())){
					for(AsahiSAMAccessModel samAccess:customer.getSamAccess())
					{
						//Set Role
						if(b2bUnit.equals(samAccess.getPayer())){
							account.setRole(getRoleForUnit(samAccess));
							break;
						}
						else {
							account.setRole(ORDER_ONLY_ROLE);
						}
					}
				}
				else {
					account.setRole(ORDER_ONLY_ROLE);
				}
				// Check for customer disabled for unit
				account.setDisabled(checkUserDisabledForUnit((AsahiB2BUnitModel) b2bUnit, customer.getUid()));
				linkedAccounts.add(account);
			}
		}
		return linkedAccounts;
	}

	private String getRoleForUnit(AsahiSAMAccessModel samAccess) {
		if(!samAccess.isApprovalDenied() && !samAccess.isPendingApproval() && samAccess.isOrderAccess() && !samAccess.isPayAccess()) {
			return ORDER_ONLY_ROLE;
		}
		else if(!samAccess.isApprovalDenied() && !samAccess.isPendingApproval() && !samAccess.isOrderAccess() && samAccess.isPayAccess()) {
			return PAY_ONLY_ROLE;
		}
		else if(!samAccess.isApprovalDenied() && !samAccess.isPendingApproval() && samAccess.isOrderAccess() && samAccess.isPayAccess()) {
			return ORDER_PAY_ROLE;
		}
		else {
			return ORDER_ONLY_ROLE;
		}
	}

	private boolean checkUserDisabledForUnit(AsahiB2BUnitModel unit,String uid) {
		if(unit.getDisabledUser().contains(uid)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public CustomerNameStrategy getCustomerNameStrategy() {
		return customerNameStrategy;
	}

	public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
		this.customerNameStrategy = customerNameStrategy;
	}


}
