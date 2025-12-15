package com.apb.core.customer.dao;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import com.apb.core.model.AsahiEmployeeModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.model.ProdPricingTierModel;
import com.sabmiller.core.enums.AsahiEnquirySubType;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.EnquiryTypeContactMappingModel;
import com.sabmiller.core.model.PlanogramModel;
/**
 * The Interface AsahiCustomerAccountDao.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiCustomerAccountDao extends CustomerAccountDao
{

	/**
	 * Find orders by customer and store.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param pageableData
	 *           the pageable data
	 * @return the search page data
	 */
	SearchPageData<OrderModel> findOrdersByCustomerAndStore(CustomerModel customerModel, BaseStoreModel store,
			PageableData pageableData, String cofoDate) throws ParseException;

	List<B2BCustomerModel> findB2BCustomerByGroup(AsahiB2BUnitModel unit, String userGroupId);

	OrderModel findOrderByB2BUnitAndCodeAndStore(AsahiB2BUnitModel b2bUnitModel, String code, BaseStoreModel store);

	/**
	 * @param salesRepCode
	 * @param companyCode
	 * @return
	 */
	AsahiEmployeeModel findAsahiSalesRepByPurposeAndRepCode(String salesRepCode, String purposeCode);

	/**
	 * @param currentSite
	 * @return
	 */
	List<KegReturnSizeModel> getKegSizes(CMSSiteModel currentSite);

	EmailAddressModel getEmailAddressModel(String displayName, String emailAddress);

	/**
	 * @return titles
	 */
	Collection<TitleModel> getAllTitles();

	/**
	 * @param salesRepCode
	 * @return
	 */
	AsahiEmployeeModel findAsahiSalesRepById(String salesRepCode);

	/**
	 * The Method will fetch the pricing tier model from the database
	 *
	 * @param tierCode
	 * @return ProdPricingTierModel
	 */
	ProdPricingTierModel findProdPricingTierByCode(String tierCode);


	/**
	 * Gets the enquiries list.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param pageableData
	 *           the pageable data
	 * @return the order list
	 */
	public SearchPageData<CsTicketModel> getAllEnquiries(AsahiB2BUnitModel b2bunit, PageableData pageableData, String cofoDate)
			throws ParseException;

	EnquiryTypeContactMappingModel getContactByEnquiryType(final AsahiEnquiryType enquiryType, final AsahiEnquirySubType enquirySubType);
	UserModel getUserByUid(String userId);

	/**
	 * @param catalogIds
	 * @return
	 */
	List<AsahiCatalogProductMappingModel> findCatalogHierarchyData(List<String> catalogIds);

	/**
	 * @param code
	 */
	PlanogramModel fetchPlanogramByCode(String code);
}
