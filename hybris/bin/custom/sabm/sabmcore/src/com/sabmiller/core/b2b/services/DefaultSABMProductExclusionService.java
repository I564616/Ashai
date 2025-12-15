/**
 *
 */
package com.sabmiller.core.b2b.services;

import com.sabmiller.core.b2b.dao.SabmB2BUnitDao;
import com.sabmiller.core.enums.LastUpdatedEntityType;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.SABMProductExclusionDao;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.ProductExclusionModel;


/**
 * A service for ProdcutExclusion management.
 */
public class DefaultSABMProductExclusionService implements SABMProductExclusionService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMProductExclusionService.class);

	protected static final String SESSION_PRODUCT_EXCLUSIONS_LAST_UPDATE_ATTR = "session.product.exclusions.last.update";
	protected static final String SESSION_PRODUCT_EXCLUSION_DELIVERY_DATE = "session.product.exclusions.delivery.date";
	protected static final String SESSION_PRODUCT_EXCLUSION_EAN_CODES = "session.product.exclusion.ean.codes";
	protected static final String SESSION_PRODUCT_EXCLUSION_B2B_UNIT = "session.product.exclusion.b2bunit";

	/** The sabm product exclusion dao. */
	@Resource(name = "sabmProductExclusionDao")
	private SABMProductExclusionDao sabmProductExclusionDao;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "b2bUnitDao")
	private SabmB2BUnitDao sabmB2BUnitDao;

	@Resource(name = "sabmB2BUnitService")
	private SabmB2BUnitService sabmB2BUnitService;

	@Resource(name = "productService")
	private SabmProductService sabmProductService;

	@Resource(name = "sabmSearchRestrictionService")
	private SabmSearchRestrictionService sabmSearchRestrictionService;
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SABMProductExclusionService#findProductExByCustomerProductDate(de.hybris.platform.
	 * core.model.security.PrincipalModel, java.lang.String, java.util.Date)
	 */
	@Override
	public List<ProductExclusionModel> findProductExByCustomerProductDate(final PrincipalModel customer, final String product,
			final Date date)
	{
		LOG.debug("calling productExclusionDao with parameters: [customer: {}, product: {}, date: {}]", customer, product, date);

		return sabmProductExclusionDao.find(customer, product, date);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMProductExclusionService#findProductExByDate(java.util.Date)
	 */
	@Override
	public List<ProductExclusionModel> findProductExByDate(final Date date)
	{
		//Retrieving the B2BUnitModel from the session customer.
		final B2BUnitModel parentUnit = b2bCommerceUnitService.getParentUnit();

		LOG.debug("calling productExclusionDao with parameters: " + "[parentUnit: {}, date: {}]", parentUnit, date);

		return sabmProductExclusionDao.find(parentUnit, date);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMProductExclusionService#findProductEx()
	 */
	@Override
	public List<ProductExclusionModel> findProductEx()
	{
		//Retrieving the B2BUnitModel from the session customer.
		final UserModel userModel = userService.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BUnitModel parentUnit = b2bCommerceUnitService.getParentUnit();

			Date deliveryDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

			//Defaulting delivery date to now in case it is missing in session.
			if (deliveryDate == null)
			{
				deliveryDate = new Date();
			}

			LOG.debug("calling productExclusionDao with parameters: " + "[parentUnit: {}, date: {}]", parentUnit, deliveryDate);

			return sabmProductExclusionDao.find(parentUnit, deliveryDate);
		}
		else
		{
			LOG.warn("ignoring product exclusions for a regular customer");
		}
		return Collections.emptyList();
	}

	@Override
	public List<ProductExclusionModel> getCustomerProductExclusions(final B2BUnitModel parentUnit)
	{
		//Retrieving the B2BUnitModel from the session customer.

		LOG.debug("calling productExclusionDao with parameters: " + "[parentUnit: {}]", parentUnit);

		return sabmProductExclusionDao.findCustomerProductExcl(parentUnit);

	}

	@Override
	public Set<String> getSessionProductExclusionEanCodes() {
		final Set<String> exclusionEanCodes = sessionService.getAttribute(SESSION_PRODUCT_EXCLUSION_EAN_CODES);
		if(exclusionEanCodes == null){ // just to be safe, a fallback option,
			return getAndSetSessionEanProductExclusion();
		}
		return exclusionEanCodes;
	}

	@Override
	public <T, R> R executeWithoutProductExclusionSearchRestriction(Function<T, R> function) {
		return sessionService.executeInLocalView(new SessionExecutionBody() {
			@Override
			public Object execute() {
				sabmSearchRestrictionService.simulateSearchRestrictionDisabledInSession(Collections.singleton("Frontend_ProductExclusionRestriction"));
				return function.apply(null);
			}
		});
	}


	protected Set<ProductModel> getProductExclusionBaseProducts() {

		final List<ProductExclusionModel> productExclusions = findProductEx();
		// convert to set avoiding a query to db of the same product code
		final Set<String> productExclusionCodes = productExclusions.stream().map(ProductExclusionModel::getProduct).filter(Objects::nonNull).collect(Collectors.toSet());

		return executeWithoutProductExclusionSearchRestriction((t)->{
			final Set<ProductModel> excludedEanCodes = new HashSet<>();
			for (String productCode : productExclusionCodes) {
				final Optional<ProductModel> baseProductCode = getBaseProductCode(productCode);
				baseProductCode.ifPresent(excludedEanCodes::add);
			}
			return excludedEanCodes;
		});
	}

	@Override
	public Set<String> getAndSetSessionEanProductExclusion() {
		final Date sessionExclusionDeliveryDate = sessionService.getAttribute(SESSION_PRODUCT_EXCLUSION_DELIVERY_DATE);
		final String sessionExclusionB2bUnit = sessionService.getAttribute(SESSION_PRODUCT_EXCLUSION_B2B_UNIT);
		final B2BUnitModel b2BUnit = b2bCommerceUnitService.getParentUnit();
		final Date sessionDeliveryDate = sessionService.getOrLoadAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, Calendar.getInstance()::getTime);
		final Set<String> sessionProductEanExclusion = sessionService.getAttribute(SESSION_PRODUCT_EXCLUSION_EAN_CODES);
		if(sessionExclusionDeliveryDate == null || !Objects.equals(sessionDeliveryDate,sessionExclusionDeliveryDate) || sessionProductEanExclusion == null){ // this just means that we don't have exclusion in session yet.
			return getProductExclusionsInternal(b2BUnit,sessionDeliveryDate);
		}

		if(!Objects.equals(sessionExclusionB2bUnit,b2BUnit.getUid())){ // b2bUnitSwitch occurred
			return getProductExclusionsInternal(b2BUnit,sessionDeliveryDate);
		}

		final Date lastUpdateTimeFromSession = sessionService.getAttribute(SESSION_PRODUCT_EXCLUSIONS_LAST_UPDATE_ATTR);
		final Optional<Date> lastUpdateTime = getLastUpdateEntityDateInternal(b2BUnit,DateUtils.truncate(sabmB2BUnitService.getStoreDate(),Calendar.DATE));

		if(!lastUpdateTime.isPresent() || lastUpdateTimeFromSession == null){ // means no update record. consider the session exclusion still valid
			return sessionProductEanExclusion;
		}

		if(lastUpdateTimeFromSession == null){
			//this shouldn't occur. but just to be sure, just re-fetch from db
			return getProductExclusionsInternal(b2BUnit,sessionDeliveryDate);
		}

		// if the update time from db is after the lastUpdateTimeFromSession means, there are changes or update happened. re-fetch
		return lastUpdateTime.get().after(lastUpdateTimeFromSession)?getProductExclusionsInternal(b2BUnit,sessionDeliveryDate):sessionProductEanExclusion;

	}



	/**
	 * Helper method to get exclusions from internal
	 * @return
	 */
	protected Set<String> getProductExclusionsInternal(final B2BUnitModel b2BUnit, final Date deliveryDate) {

		final Set<String> exclusions = getProductExclusionBaseProducts().stream().map(ProductModel::getCode).collect(Collectors.toSet());

		//update the session settings
		sessionService.setAttribute(SESSION_PRODUCT_EXCLUSION_EAN_CODES,exclusions);
		sessionService.setAttribute(SESSION_PRODUCT_EXCLUSION_DELIVERY_DATE,deliveryDate);
		sessionService.setAttribute(SESSION_PRODUCT_EXCLUSION_B2B_UNIT,b2BUnit.getUid());
		final Optional<Date> lastUpdateTime = getLastUpdateEntityDateInternal(b2BUnit, DateUtils.truncate(sabmB2BUnitService.getStoreDate(),Calendar.DATE));
		lastUpdateTime.ifPresent((d)->sessionService.setAttribute(SESSION_PRODUCT_EXCLUSIONS_LAST_UPDATE_ATTR,d));

		return exclusions;
	}

	protected Optional<Date> getLastUpdateEntityDateInternal(final B2BUnitModel b2BUnit, final Date deliveryDate){
		final LastUpdateTimeEntityModel lastUpdateTimeEntity = sabmB2BUnitDao.findLastUpdateTimeEntities(deliveryDate, LastUpdatedEntityType.PRODUCT_EXCLUSION, b2BUnit);

		if(lastUpdateTimeEntity == null){
			return Optional.empty();
		}

		return Optional.ofNullable(lastUpdateTimeEntity.getLastUpdateTime());
	}

	protected Optional<ProductModel> getBaseProductCode(@Nonnull final String productCode) {
		if (StringUtils.isEmpty(productCode)) {
			return Optional.empty();
		}

		try {
			final SABMAlcoholVariantProductMaterialModel material = sabmProductService.getMaterialByCode(productCode);
			return Optional.ofNullable(material.getBaseProduct());

		} catch (UnknownIdentifierException | AmbiguousIdentifierException ie) {
			LOG.debug("Unable to get EAN code for variant code [{}]", productCode);
		}

		return Optional.empty();
	}

	protected ProductService getProductService() {
		return productService;
	}

}
