/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.persistence.PersistenceUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.apb.core.util.AsahiSiteUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.dao.SabmB2BCustomerDao;
import com.sabmiller.core.b2b.dao.SabmB2BUnitDao;
import com.sabmiller.core.b2b.dao.SearchB2BUnitQueryParam;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.B2BUnitStatus;
import com.sabmiller.core.enums.LastUpdatedEntityType;
import com.sabmiller.core.enums.SapServiceCallStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.util.SabmConverter;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.integration.sap.cup.request.CustomerUnitPricingRequest;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;
import com.sabmiller.integration.sap.price.CupRequestHandler;
import com.sabmiller.integration.sap.productexclusion.ProductExclusionRequestHandler;
import com.sabmiller.integration.sap.productexclusion.request.ProductExclusionRequest;


/**
 * Extension to the OOTB B2BUnitService.
 *
 * @author joshua.a.antony
 */
@SuppressWarnings(
{ "SE_NO_SERIALVERSIONID", "SE_BAD_FIELD" })
public class DefaultSabmB2BUnitService extends DefaultB2BUnitService implements SabmB2BUnitService
{

	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BUnitService.class);

	private static final String CUP_DISCOUNTS = "CUP_DISCOUNTS";

	private static final String CUP_PERSISTENCE_LEGACY_MODE = "cup.persistence.legacy.mode";
	private static final String CUP_PERSISTENCE_BATCH_MODE = "cup.persistence.batch.mode";
	private static final String CUP_STUB_AVAILABLE_CHECK = "cub.cup.stub.available.check";
	public static final String CUP_STUB_RESPONSE = "stub/cupResponse.xml";
	public static final String CUP_STUB_MEDIA_RESPONSE = "cub.stub.unit.price";
	private static final String SGA_SITE_NAME = "sga";
	private static final String APB_SITE_NAME = "apb";
	private static final String CUB_SITE_NAME = "sabmStore";
	private static final String ASAHI_ORDER_TYPE_ONLINE = "ONLINE";
	private static final String ASAHI_ORDER_TYPE_CALLCENTERORDER = "CALLCENTERORDER";

	/**
	 * The Constant PERVIOUS_B2BUNIT.
	 */
	private static final String PERVIOUS_B2BUNIT = "previous";

	@Inject
    @Named("sabmB2BCustomerDao")
	private SabmB2BCustomerDao sabmB2BCustomerDao;

    @Inject
    @Named("sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	/**
	 * The b2b unit dao.
	 */
    @Inject
    @Named( "b2bUnitDao")
	private transient SabmB2BUnitDao b2bUnitDao;

	/**
	 * The b2b unit reverse populator.
	 */
    @Inject
    @Named("sabmB2BUnitReversePopulator")
	private transient Populator<B2BUnitData, B2BUnitModel> b2bUnitReversePopulator;

	/**
	 * The cup request handler.
	 */
    @Inject
    @Named("cupRequestHandler")
	private CupRequestHandler cupRequestHandler;

	/**
	 * The product exclusion request handler.
	 */
    @Inject
    @Named("productExclusionRequestHandler")
	private ProductExclusionRequestHandler productExclRequestHandler;

	/**
	 * The cup request converter.
	 */
    @Inject
    @Named("cupRequestConverter")
	private SabmConverter<B2BUnitModel, CustomerUnitPricingRequest, Date> cupRequestConverter;

	/**
	 * The cup reverse converter.
	 */
    @Inject
    @Named("cupReverseConverter")
	private Converter<CustomerUnitPricingResponse, List<PriceRowModel>> cupReverseConverter;

	/**
	 * The cup request converter.
	 */
    @Inject
    @Named("productExclRequestConverter")
	private SabmConverter<B2BUnitModel, ProductExclusionRequest, Date> productExclRequestConverter;

    @Inject
    @Named("sabmProductExclusionService")
	private SABMProductExclusionService productExclusionService;

	/**
	 * The catalog version service.
	 */
    @Inject
    @Named("catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/**
	 * The catalog version determination strategy.
	 */
    @Inject
    @Named("catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

    @Inject
    @Named("sessionService")
	private SessionService sessionService;

    @Inject
    @Named("b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

    @Inject
    @Named("sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;

    @Inject
    @Named("baseStoreService")
	private BaseStoreService baseStoreService;

    @Inject
    @Named("dealsService")
	private DealsService dealsService;

    @Inject
    @Named("cmsSiteService")
	private CMSSiteService cmsSiteService;

    @Inject
	private AsahiSiteUtil asahiSiteUtil;

    @Inject
	private B2BCustomerService b2bCustomerService;

    @Inject
	private MediaService mediaService;
	/**
	 * The expiry discount.
	 */
	@Value(value = "${deals.discount.service.expiry:12}")
	private int expiryDiscount;

	/**
	 * The expiry onceoff.
	 */
	@Value(value = "${deals.onceoff.service.expiry:12}")
	private int expiryOnceoff;

	/**
	 * The expiry bogof.
	 */
	@Value(value = "${deals.bogof.service.expiry:12}")
	private int expiryBogof;

	/**
	 * The expiry cup.
	 */
	@Value(value = "${cup.service.expiry:12}")
	private int expiryCup;

	/**
	 * The expiry productExclusions.
	 */
	@Value(value = "${productexclusions.service.expiry:12}")
	private int expiryProductExclusion;

	/**
	 * The session attr user id.
	 */
	@Value(value = "${session.attr.user.invoking.sap.service:CURRENT_USER_SAP_INVOCATION}")
	private String sessionAttrUserId;

	/**
	 * Find the ZADP B2BUnit based on the payer Id. Note that ZADP is the top level B2BUnit and ZALB are the regional
	 * units (Child) sharing the same Payer Id. This method will return the Main (Parent) B2BUnit.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the b2 b unit model
	 */
	@Override
	public B2BUnitModel findTopLevelB2BUnit(final String payerId)
	{
		return b2bUnitDao.findTopLevelB2BUnit(payerId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findBranch(java.lang.String)
	 */
	@Override
	public B2BUnitModel findBranch(final String payerId)
	{
		return b2bUnitDao.findBranch(payerId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findBranches(java.lang.String)
	 */
	@Override
	public List<B2BUnitModel> findBranches(final String payerId)
	{
		return b2bUnitDao.findBranches(payerId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#persist(de.hybris.platform.b2b.model.B2BUnitModel,
	 * de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData)
	 */
	@Override
	public B2BUnitModel persist(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		final boolean isNewRecord = getModelService().isNew(b2bUnitModel);

		b2bUnitReversePopulator.populate(b2bUnitData, b2bUnitModel);

		if (isTopLevelB2BUnit(b2bUnitModel))
		{
			LOG.debug("This [{}] is a top level (ZADP) B2B Unit. Setting primary admin user for the same", b2bUnitModel);
			setPrimaryAdmin(b2bUnitModel, b2bUnitData);

			//If branches have been imported prior to ZADP, ensure that they have the correct parent
			if (isNewRecord)
			{
				for (final B2BUnitModel eachBranch : findBranches(b2bUnitModel.getPayerId()))
				{
					updateParentB2BUnit(b2bUnitModel, eachBranch);
					eachBranch.setActive(Boolean.TRUE);
					getModelService().save(eachBranch);
				}
			}
		}
		else if (isBranch(b2bUnitModel))
		{
			LOG.debug("The b2b unit is a branch : Looking for ZADP with the same Payer Id [{}]", b2bUnitModel.getPayerId());
			final B2BUnitModel parentB2BUnitModel = findTopLevelB2BUnit(b2bUnitModel.getPayerId());
			if (parentB2BUnitModel != null)
			{
				LOG.debug("Found ZADP unit ::::  Unit Id => {}", parentB2BUnitModel.getUid());
				updateParentB2BUnit(parentB2BUnitModel, b2bUnitModel);

				//If Primary Admin does not have a default B2BUnit as a branch, set one
				final B2BCustomerModel primaryAdmin = (B2BCustomerModel) findPrimaryAdmin(parentB2BUnitModel);
				if (primaryAdmin != null && !isBranch(primaryAdmin.getDefaultB2BUnit()))
				{
					LOG.debug("Setting the Default B2BUnit of {} to {} ", primaryAdmin, b2bUnitModel);
					primaryAdmin.setDefaultB2BUnit(b2bUnitModel);
					getModelService().save(primaryAdmin);
				}
			}
			else
			{
				LOG.error("Unable to locate parent for this b2bunit using Payer : {}. This unit will be created without a parent!",
						b2bUnitModel.getPayerId());
			}
		}
		LOG.debug("All set :::  Finally persisting the b2bUnitModel {}", b2bUnitModel.getUid());
		getModelService().save(b2bUnitModel);

		return b2bUnitModel;
	}

	/**
	 * Sets the defaults.
	 *
	 * @param uId
	 *           the u id
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	protected void setDefaults(final String uId, final B2BUnitData b2bUnitData)
	{
		try
		{
			final B2BUnitModel b2bUnitModel = getUnitForUid(uId);
			setDefaultCarrier(b2bUnitModel, b2bUnitData);
			setDefaultUnloadingPoint(b2bUnitModel, b2bUnitData);
			getModelService().save(b2bUnitModel);
		}
		catch (final Exception e)
		{
			LOG.error("Unable to set the defaults ", e);
		}
	}

	/**
	 * Sets the primary admin.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	private void setPrimaryAdmin(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (b2bUnitData.getPrimaryAdmin() != null && !StringUtils.isBlank(b2bUnitData.getPrimaryAdmin().getEmail()))
		{
			final B2BCustomerModel primaryAdmin = findOrCreatePrimaryAdmin(b2bUnitData);

			if (getModelService().isNew(primaryAdmin))
			{
				updatePrimaryAdminParentAndGroups(b2bUnitModel, primaryAdmin);
			}
			if (primaryAdmin != null && !isBranch(primaryAdmin.getDefaultB2BUnit()))
			{
				LOG.debug("Setting the Default B2BUnit of {} to {} ", primaryAdmin, b2bUnitModel);
				primaryAdmin.setDefaultB2BUnit(findBranch(b2bUnitModel.getPayerId()));
				getModelService().save(primaryAdmin);
			}

			getModelService().save(primaryAdmin);
		}
	}

	/**
	 * Find user with email address. If user does not exist, create a new user and persist it.
	 *
	 * @param b2bUnitData
	 *           the b2b unit data
	 * @return the b2 b customer model
	 */
	protected B2BCustomerModel findOrCreatePrimaryAdmin(final B2BUnitData b2bUnitData)
	{
		final CustomerData primaryAdmin = b2bUnitData.getPrimaryAdmin();
		final String email = primaryAdmin.getEmail();
		B2BCustomerModel userModel = null;
		if (!getUserService().isUserExisting(email.toLowerCase()))
		{
			LOG.debug("Primary Admin does not exist in the system. Creating one with Id {}", email);
			userModel = getModelService().create(B2BCustomerModel.class);
			userModel.setEmail(email);
			userModel.setName(primaryAdmin.getName());
			userModel.setFirstName(primaryAdmin.getFirstName());
			userModel.setLastName(primaryAdmin.getLastName());
			userModel.setUid(email);
			userModel.setPrimaryAdmin(Boolean.TRUE);
			return userModel;
		}
		else
		{
			LOG.debug("Primary Admin {} exist in the system. Updating the Parent B2BUnit", email);
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getUserForUID(email.toLowerCase());
			b2bCustomerModel.setName(primaryAdmin.getName());
			b2bCustomerModel.setFirstName(primaryAdmin.getFirstName());
			b2bCustomerModel.setLastName(primaryAdmin.getLastName());
			return b2bCustomerModel;
		}
	}

	/**
	 * Update primary admin parent and groups.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param userModel
	 *           the user model
	 */
	protected void updatePrimaryAdminParentAndGroups(final B2BUnitModel b2bUnitModel, final UserModel userModel)
	{
		final UserGroupModel b2bAdminGroup = getUserService().getUserGroupForUID("b2badmingroup");
		final Set<PrincipalGroupModel> groups = new HashSet(
				userModel.getGroups() != null ? userModel.getGroups() : Collections.emptySet());
		groups.add(b2bAdminGroup);
		userModel.setGroups(groups);

		updateParentB2BUnit(b2bUnitModel, userModel);
	}

	/**
	 * Checks if is top level b2 b unit.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is top level b2 b unit
	 */
	protected boolean isTopLevelB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		return b2bUnitModel != null && SabmCoreConstants.ZADP.equals(b2bUnitModel.getAccountGroup());
	}

	/**
	 * Checks if is branch.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is branch
	 */
	protected boolean isBranch(final B2BUnitModel b2bUnitModel)
	{
		return b2bUnitModel != null && SabmCoreConstants.ZALB.equals(b2bUnitModel.getAccountGroup());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#isDiscountDealsObsolete(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.util.Date)
	 */
	@Override
	public boolean isDiscountDealsObsolete(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		return isEntityObsolete(b2bUnitModel, LastUpdatedEntityType.PRICING_DISCOUNT, expiryDiscount, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#isOnceOffDealsObsolete(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.util.Date)
	 */
	@Override
	public boolean isOnceOffDealsObsolete(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		return isEntityObsolete(b2bUnitModel, LastUpdatedEntityType.LIMITED_DEAL, expiryOnceoff, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmB2BUnitService#isBOGOFDealsObsolete(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.util.Date)
	 */
	@Override
	public boolean isBOGOFDealsObsolete(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		return isEntityObsolete(b2bUnitModel, LastUpdatedEntityType.BOGOF, expiryBogof, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#isCUPObsolete(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.util.Date)
	 */
	@Override
	public boolean isCUPObsolete(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		return isEntityObsolete(b2bUnitModel, LastUpdatedEntityType.CUP, expiryCup, deliveryDate);
	}

	@Override
	public boolean isProductExclObsolete(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		return isEntityObsolete(b2bUnitModel, LastUpdatedEntityType.PRODUCT_EXCLUSION, expiryProductExclusion, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#markDiscountDealsAsRefreshed(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.util.Date)
	 */
	@Override
	public void markDiscountDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		markEntitesAsRefreshed(b2bUnitModel, LastUpdatedEntityType.PRICING_DISCOUNT, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#markOnceOffDealsAsRefreshed(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.util.Date)
	 */
	@Override
	public void markOnceOffDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		markEntitesAsRefreshed(b2bUnitModel, LastUpdatedEntityType.LIMITED_DEAL, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#markBOGOFDealsAsRefreshed(de.hybris.platform.b2b.model.
	 * B2BUnitModel, java.util.Date)
	 */
	@Override
	public void markBOGOFDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		markEntitesAsRefreshed(b2bUnitModel, LastUpdatedEntityType.BOGOF, deliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmB2BUnitService#markCUPAsRefreshed(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.util.Date)
	 */
	@Override
	public void markCUPAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		markEntitesAsRefreshed(b2bUnitModel, LastUpdatedEntityType.CUP, deliveryDate);
	}

	@Override
	public void markProductExclAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		markEntitesAsRefreshed(b2bUnitModel, LastUpdatedEntityType.PRODUCT_EXCLUSION, deliveryDate);
	}

	/**
	 * Update the timestamp of the requested entity to current time. This way, subsequent request to update these
	 * entities (Example : CUP, BOGOF) will not trigger a SAP call
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param entityType
	 *           the entity type
	 * @param deliveryDate
	 *           the delivery date
	 */
	private void markEntitesAsRefreshed(final B2BUnitModel b2bUnitModel, final LastUpdatedEntityType entityType,
			final Date deliveryDate)
	{
		try
		{
			LOG.debug("Updating the last udpated time for entity {}", entityType);

			getModelService().refresh(b2bUnitModel);
			b2bUnitModel.setRefreshEntitiesLastUpdatedTime(new Date());
			final LastUpdateTimeEntityModel lastUpdateTimeEntityModel = findLastUpdatedEntity(b2bUnitModel, entityType,
					deliveryDate);
			if (lastUpdateTimeEntityModel != null)
			{
				lastUpdateTimeEntityModel.setLastUpdateTime(new Date());
				lastUpdateTimeEntityModel.setDeliveryDate(deliveryDate);
				getModelService().save(lastUpdateTimeEntityModel);
			}
			else
			{
				final LastUpdateTimeEntityModel newModel = getModelService().create(LastUpdateTimeEntityModel.class);
				newModel.setEntityType(entityType);
				newModel.setLastUpdateTime(new Date());
				newModel.setDeliveryDate(deliveryDate);
				getModelService().save(newModel);
				final List<LastUpdateTimeEntityModel> existingEntities = b2bUnitModel.getLastUpdateTimeEntities();

				final List<LastUpdateTimeEntityModel> lastUpdatedEntityList = new ArrayList<LastUpdateTimeEntityModel>();
				for (final LastUpdateTimeEntityModel model : ListUtils.emptyIfNull(existingEntities))
				{
					lastUpdatedEntityList.add(model);
				}

				lastUpdatedEntityList.add(newModel);
				b2bUnitModel.setLastUpdateTimeEntities(lastUpdatedEntityList);
				getModelService().save(b2bUnitModel);
			}

		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred while refreshing the last update entity. Entity Type : " + entityType
					+ " , B2BUnitModel : " + b2bUnitModel, e);
		}
	}

	/**
	 * Find last updated entity.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param lastUpdatedEntityType
	 *           the last updated entity type
	 * @param deliveryDate
	 *           the delivery date
	 * @return the last update time entity model
	 */
	private LastUpdateTimeEntityModel findLastUpdatedEntity(final B2BUnitModel b2bUnitModel,
			final LastUpdatedEntityType lastUpdatedEntityType, final Date deliveryDate)
	{
		final LastUpdateTimeEntityModel lastUpdateTimeEntity = b2bUnitDao.findLastUpdateTimeEntities(deliveryDate,
				lastUpdatedEntityType, b2bUnitModel);

		return lastUpdateTimeEntity;
	}

	/**
	 * Check if the last time the entity updated was before the 'hours' specified. Example : if BOGOF Deal for a b2b unit
	 * was updated 48 hours ago and the 'hours' passed to this method is 24, then this implies that the data is old
	 * (>24hrs) and needs update, and returns true.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param lastUpdatedEntityType
	 *           the last updated entity type
	 * @param hours
	 *           the hours
	 * @param deliveryDate
	 *           the delivery date
	 * @return true if the entry is old and needs update, false if the entry was udpated recently (before the 'hours')
	 */
	private boolean isEntityObsolete(final B2BUnitModel b2bUnitModel, final LastUpdatedEntityType lastUpdatedEntityType,
			final int hours, final Date deliveryDate)
	{
		LOG.debug("In isEntityObsolete(). b2bUnitModel: [{}], lastUpdatedEntityType: [{}]", b2bUnitModel.getUid(),
				lastUpdatedEntityType);
		final LastUpdateTimeEntityModel lastUpdateTimeEntityModel = b2bUnitDao.findLastUpdateTimeEntities(deliveryDate,
				lastUpdatedEntityType, b2bUnitModel);

		if (null != lastUpdateTimeEntityModel)
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, hours * -1);
			final boolean isObsolete = cal.getTimeInMillis() > lastUpdateTimeEntityModel.getLastUpdateTime().getTime();
			LOG.debug("Is the requested entity obsolete? [{}]", isObsolete);
			return isObsolete;
		}
		return true;
	}

	@Override
	public void refreshCUP(final B2BUnitModel b2bUnitModel)
	{
		updateCUP(b2bUnitModel, getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE));
	}


	/**
	 * Helper method to perform Import, assumption is that requestType will never be
	 * {@link com.sabmiller.core.b2b.services.SabmB2BUnitService.CustomerUnitPricingRequestType#NONE}
	 *
	 * @param b2bUnitModel
	 * @param deliveryDate
	 */
	protected void importFromCUPResponse(final B2BUnitModel b2bUnitModel, final Date deliveryDate, final String userId)
	{


		final CustomerUnitPricingRequest customerUnitPricingRequest = new CustomerUnitPricingRequest();
		customerUnitPricingRequest.setRequestType(CUP_DISCOUNTS);

		CustomerUnitPricingResponse cupResponse = null;
		try
		{
			if (getConfigurationService().getConfiguration().getBoolean(CUP_STUB_AVAILABLE_CHECK, false))
			{

				final MediaModel stubMedia = mediaService.getMedia(CUP_STUB_MEDIA_RESPONSE);
				final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
				//final ClassLoader classLoader = getClass().getClassLoader();
				//media service -
				// check material/EAN product
				//final File stubFile = new File(classLoader.getResource(CUP_STUB_RESPONSE).getFile());
				//final InputStream targetStream = new FileInputStream(stubFile);
				final JAXBContext jaxbContext = JAXBContext.newInstance(CustomerUnitPricingResponse.class);
				final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				final XMLInputFactory factory = XMLInputFactory.newInstance();
				final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
				final JAXBElement<CustomerUnitPricingResponse> userElement = unmarshaller.unmarshal(fileSource,
						CustomerUnitPricingResponse.class);
				cupResponse = userElement.getValue();
			}
			else
			{
				cupResponse = cupRequestHandler
						.sendPostRequest(cupRequestConverter.convert(b2bUnitModel, customerUnitPricingRequest, deliveryDate));
			}

		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred while trying to refresh CUP ", e);
		}

		if (cupResponse == null)
		{
			b2bUnitModel.setDiscountCallStatus(SapServiceCallStatus.ERROR);
			b2bUnitModel.setCupCallStatus(SapServiceCallStatus.ERROR);
			getModelService().save(b2bUnitModel);
			return;
		}

		final ExecutorService executorService = newFixedThreadPoolExecutorWithLocalSession(2, userId, deliveryDate);

		try
		{

			final CustomerUnitPricingResponse finalCupResponse = cupResponse;
			final boolean isLegacy = getConfigurationService().getConfiguration().getBoolean(CUP_PERSISTENCE_LEGACY_MODE, false);

			// calls price import, separate thread each
			executorService.submit(() -> PersistenceUtils.doWithChangedPersistenceLegacyMode(isLegacy, () -> {
				final Stopwatch stopwatch = Stopwatch.createStarted();

				importPriceRowsFromCUPResponse(b2bUnitModel, finalCupResponse, deliveryDate);

				LOG.debug("It took [{}] to import price using [{}] mode", stopwatch.toString(), isLegacy ? "legacy" : "direct");
				return null;
			}));

			//calls deals import
			executorService.submit(() -> PersistenceUtils.doWithChangedPersistenceLegacyMode(isLegacy, () -> {
				final Stopwatch stopwatch = Stopwatch.createStarted();

				dealsService.importDeals(b2bUnitModel, deliveryDate, finalCupResponse);

				LOG.debug("It took [{}] to import deals using [{}] mode", stopwatch.toString(), isLegacy ? "legacy" : "direct");
				return null;
			}));

		}
		finally
		{
			executorService.shutdown();
		}


	}


	/**
	 * Helper method to create fixed thread executor and executes each thread on a local session
	 *
	 * @param threads
	 * @param userId
	 * @param deliveryDate
	 * @return
	 */
	private ExecutorService newFixedThreadPoolExecutorWithLocalSession(final int threads, final String userId,
			final Date deliveryDate)
	{
		return Executors.newFixedThreadPool(2, (threadRunnable) -> new Thread(() -> {
			try
			{
				onThreadExecution(userId, deliveryDate);
				threadRunnable.run();
			}
			finally
			{
				afterThreadExecution();
			}
		}));
	}

	/**
	 * Helper method to import price rows from CUP Respone
	 *
	 * @param b2bUnit
	 * @param customerUnitPricingResponse
	 * @param deliveryDate
	 */
	private void importPriceRowsFromCUPResponse(final B2BUnitModel b2bUnit,
			final CustomerUnitPricingResponse customerUnitPricingResponse, final Date deliveryDate)
	{

		boolean success = true;
		try
		{
			//Convert the response to PriceModels and persist
			final List<PriceRowModel> priceRows = cupReverseConverter.convert(customerUnitPricingResponse);

			if (getConfigurationService().getConfiguration().getBoolean(CUP_PERSISTENCE_BATCH_MODE, true))
			{
				if (CollectionUtils.isNotEmpty(priceRows))
				{
					getModelService().saveAll(priceRows);
				}
			}
			else
			{
				for (final PriceRowModel priceRow : ListUtils.emptyIfNull(priceRows))
				{
					try
					{
						getModelService().save(priceRow);
					}
					catch (final Exception e)
					{
						LOG.error("Exception occured while trying to save price for product:" + priceRow.getProductId(), e);
					}
				}
			}

			//Finally put an entry on B2BUnit denoting when that the cup was refreshed
			markCUPAsRefreshed(b2bUnit, deliveryDate);

			//Set the status to done
			b2bUnit.setCupCallStatus(SapServiceCallStatus.DONE);
			getModelService().save(b2bUnit);

		}
		catch (final Exception e)
		{
			LOG.error("Exception occured while trying to refresh CUP ", e);
			success = false;
		}
		finally
		{
			if (!success)
			{
				LOG.error("CUP prices not updated due exception");
				getModelService().detachAll();
				getModelService().refresh(b2bUnit);
				b2bUnit.setCupCallStatus(SapServiceCallStatus.ERROR);
				getModelService().save(b2bUnit);
			}
		}
		resetCupRefreshStatus(b2bUnit);
	}

	@Override
	public void requestProductExclusions(final B2BUnitModel b2bUnitModel)
	{
		if (Config.getBoolean("invoke.sap.product.exclusion.async.call", true))
		{
			LOG.debug("Invoking the SAP service to refresh the CUP. b2bUnit {}", b2bUnitModel);

			//final Date today = SabmDateUtils.getOnlyDate(new Date());
			final Date today = SabmDateUtils.getOnlyDate(getStoreDate());
			final String userId = getUserService().getCurrentUser().getUid();

			//if (shouldCallSapService(b2bUnitModel.getProductExclCallStatus()) && isProductExclObsolete(b2bUnitModel, today)) {
			if (isProductExclObsolete(b2bUnitModel, today))
			{
				Executors.newCachedThreadPool().execute(() -> {

					onThreadExecution(userId, today);

					boolean success = true;
					try
					{

						markProductExclAsRefreshed(b2bUnitModel, today);
						b2bUnitModel.setProductExclCallStatus(SapServiceCallStatus.IN_PROGRESS);
						getModelService().save(b2bUnitModel);
						productExclRequestHandler
								.sendPostRequest(productExclRequestConverter.convert(b2bUnitModel, new ProductExclusionRequest(), today));

					}
					catch (final Exception e)
					{
						LOG.error("Exception occured while trying to call Product Exclusion sync call ", e);
						success = false;
					}
					finally
					{
						if (!success)
						{
							getModelService().refresh(b2bUnitModel);
							b2bUnitModel.setProductExclCallStatus(SapServiceCallStatus.ERROR);
							getModelService().save(b2bUnitModel);
						}
					}
				});
			}
		}
	}

	@Override
	public Date getStoreDate()
	{

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


		TimeZone storeTimeZone = null;

		//Getting BaseStore timezone
		if (baseStore != null && baseStore.getTimeZone() != null)
		{
			storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
		}

		final Date now = new Date();
		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();
		Date storeTime = null;
		if (storeTimeZone != null)
		{
			storeTime = new Date(now.getTime() - serverTimeZone.getOffset(now.getTime()) + storeTimeZone.getOffset(now.getTime()));
		}
		else
		{
			storeTime = now;
		}

		final Calendar storeDateTime = Calendar.getInstance();
		storeDateTime.setTime(storeTime);

		return storeDateTime.getTime();

	}

	/**
	 * Mark cup refresh in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void markCupRefreshInProgress(final B2BUnitModel b2bUnitModel)
	{
		try
		{
			b2bUnitModel.setCupCallStatus(SapServiceCallStatus.IN_PROGRESS);
			getModelService().save(b2bUnitModel);
		}
		catch (final Exception e)
		{
			LOG.error(
					"Exception occured while trying to set CUP status IN_PROGRESS. Swallowing this as the side effects are minimal",
					e.getMessage());
		}

	}

	/**
	 * Reset cup refresh status.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void resetCupRefreshStatus(final B2BUnitModel b2bUnitModel)
	{
		getModelService().refresh(b2bUnitModel);
		if (SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getCupCallStatus()))
		{
			b2bUnitModel.setCupCallStatus(SapServiceCallStatus.ERROR);
		}
		getModelService().save(b2bUnitModel);
	}

	/**
	 * On thread execution.
	 *
	 * @param userId
	 *           the user id
	 */
	private void onThreadExecution(final String userId, final Date deliveryDate)
	{
		Registry.activateMasterTenant();
		prepareLocalSession(userId, deliveryDate);
	}

	private void afterThreadExecution()
	{
		Registry.unsetCurrentTenant();
	}

	/**
	 * Helper to prepare the session for local context with custom session user,catalogs,deliveryDates
	 *
	 * @param userId
	 * @param deliveryDate
	 */
	private void prepareLocalSession(final String userId, final Date deliveryDate)
	{
		getUserService().setCurrentUser(getUserService().getUserForUID("integrationAdmin"));
		catalogVersionService.setSessionCatalogVersion(catalogVersionDeterminationStrategy.getCatalogId(),
				CatalogManager.ONLINE_VERSION);

		getSessionService().setAttribute(sessionAttrUserId, userId);
		getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, deliveryDate);
	}

	/**
	 * Sets the default unloading point.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	protected void setDefaultUnloadingPoint(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		LOG.debug("Setting the default unloading point for b2bUnit: [{}]", b2bUnitModel);
		if (b2bUnitData.getDefaultUnloadingPoint() != null)
		{
			for (final UnloadingPointModel model : ListUtils.emptyIfNull(b2bUnitModel.getUnloadingPoints()))
			{
				LOG.debug("Comparing [{}] with [{}]", model.getCode(), b2bUnitData.getDefaultUnloadingPoint().getCode());
				if (model.getCode().equals(b2bUnitData.getDefaultUnloadingPoint().getCode()))
				{
					LOG.debug("Found default unloading point for b2bUnit: [{}]", b2bUnitModel);
					b2bUnitModel.setDefaultUnloadingPoint(model);
					break;
				}
			}
		}
	}

	/**
	 * Sets the default carrier.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	protected void setDefaultCarrier(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		LOG.debug("Setting the default shipping carrier for b2bUnit: [{}]", b2bUnitModel);
		if (b2bUnitData.getDefaultCarrier() != null)
		{
			for (final ShippingCarrierModel model : ListUtils.emptyIfNull(b2bUnitModel.getShippingCarriers()))
			{
				LOG.debug("Comparing [{}] with [{}]", model.getCarrierCode(), b2bUnitData.getDefaultCarrier().getCode());
				if (model.getCarrierCode().equals(b2bUnitData.getDefaultCarrier().getCode()))
				{
					LOG.debug("Found default carrier for b2bUnit: [{}]", b2bUnitModel);
					b2bUnitModel.setDefaultCarrier(model);
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findPrimaryAdmin(java.lang.String)
	 */
	@Override
	public UserModel findPrimaryAdmin(final String payerId)
	{
		return findPrimaryAdmin(findTopLevelB2BUnit(payerId));
	}

	/**
	 * get the PrimaryAdmin user from B2BUnitModel.
	 *
	 * @param topLevelCompany
	 *           the top level company
	 * @return UserModel
	 */
	private UserModel findPrimaryAdmin(final B2BUnitModel topLevelCompany)
	{
		if (null != topLevelCompany)
		{
			for (final PrincipalModel pModel : SetUtils.emptyIfNull(topLevelCompany.getMembers()))
			{
				if (pModel instanceof UserModel)
				{
					final UserModel userModel = (UserModel) pModel;
					if (userModel.getPrimaryAdmin() != null && userModel.getPrimaryAdmin())
					{
						LOG.debug("Found Primary Admin : {}", userModel.getUid());
						return userModel;
					}
				}
			}
		}
		return null;
	}

	/**
	 * When the user can 'changes' the current b2bunit from the header, the selected B2BUnit is set in the session.
	 * Hence, this method first introspects the session. If B2BUnit is available in the session, it is returned - else
	 * falls back to the OOTB implementation.
	 *
	 * @param employee
	 *           the employee
	 * @return the parent
	 */
	@Override
	public B2BUnitModel getParent(final B2BCustomerModel employee)
	{

		final CMSSiteModel cmssite = cmsSiteService.getCurrentSite();

		if (employee == null)
		{
			return null;
		}

		// HC-316 For Asahi customers bypassing sabm flow
		if (cmssite != null)
		{
			if (!CUB_SITE_NAME.equalsIgnoreCase(cmssite.getUid()))
			{
				return super.getParent(employee);
			}
			else
			{
				B2BUnitModel b2bUnitModel = (B2BUnitModel) getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
				if (b2bUnitModel != null && !(b2bUnitModel instanceof AsahiB2BUnitModel))
				{
					LOG.debug("Found B2B Unit {} in session. ", b2bUnitModel.getUid());
					return b2bUnitModel;
				}

				if (employee.getDefaultB2BUnit() != null && !(employee.getDefaultB2BUnit() instanceof AsahiB2BUnitModel))
				{
					return employee.getDefaultB2BUnit();
				}

				for (final PrincipalGroupModel group : employee.getGroups())
				{
					if (group instanceof AsahiB2BUnitModel)
					{
						continue;
					}
					else if (group instanceof B2BUnitModel && ((B2BUnitModel) group).getAccountGroup().equals(SabmCoreConstants.ZALB)
							&& !SabmUtils.isUserDisabledForCUBAccount((B2BUnitModel) group, employee))
					{
						b2bUnitModel = (B2BUnitModel) group;
						break;
					}
				}

				if (b2bUnitModel != null)
				{
					getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, b2bUnitModel);

					employee.setDefaultB2BUnit(b2bUnitModel);
					getModelService().save(employee);
					getModelService().refresh(employee);
				}
				return b2bUnitModel;
			}
		}

		// Need to handle case when defaultB2BUnit is null for Asahi customers
		if (null != employee.getDefaultB2BUnit())
		{
			if (employee.getDefaultB2BUnit() instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnit = (AsahiB2BUnitModel) employee.getDefaultB2BUnit();
				if (asahiB2BUnit.getCompanyCode().equalsIgnoreCase("apb") | asahiB2BUnit.getCompanyCode().equalsIgnoreCase("sga"))
				{
					return super.getParent(employee);
				}
			}

		}

		LOG.debug("In getParent(). Looking up for B2BUnit in the session. If not found, fall back to the OOTB implemenation");


		return null;
	}

	/**
	 * Gets the B2BUnit in the current session.
	 *
	 * @return the B2BUnit model if it is in session, null otherwise.
	 */
	@Override
	public B2BUnitModel getB2BUnitInCurrentSession()
	{
		final B2BUnitModel b2bUnitModel = getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);

		return b2bUnitModel;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#isDealRefreshInProgress(de.hybris.platform.b2b.model.
	 * B2BUnitModel )
	 */
	@Override
	public boolean isDealRefreshInProgress(final B2BUnitModel b2bUnitModel)
	{
		return SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getDiscountCallStatus())
				|| SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getBogofCallStatus());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#isCupRefreshInProgress(de.hybris.platform.b2b.model.
	 * B2BUnitModel )
	 */
	@Override
	public boolean isCupRefreshInProgress(final B2BUnitModel b2bUnitModel)
	{
		return SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getCupCallStatus());
	}

	@Override
	public boolean isProductExclRefreshInProgress(final B2BUnitModel b2bUnitModel)
	{
		return SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getProductExclCallStatus());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findTopLevelB2BUnit(de.hybris.platform.b2b.model.
	 * B2BCustomerModel )
	 */
	@Override
	public B2BUnitModel findTopLevelB2BUnit(final B2BCustomerModel customerModel)
	{
		for (final PrincipalGroupModel model : customerModel.getGroups())
		{
			if (model instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			if (model instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnitModel = (B2BUnitModel) model;
				if (isTopLevelB2BUnit(b2bUnitModel))
				{
					LOG.debug("Found ZADP B2BUnit for Customer {}  ", customerModel);
					return b2bUnitModel;
				}
			}
		}
		LOG.debug("No Top level organization found for customer {} , returning null ", customerModel);

		return null;
	}

	/**
	 * By default, ZADP users will have a ZALB as default B2BUnit and all the functionality are seen through the eyes of
	 * the ZALB B2BUnit. However, for ZADP specific use cases, there needs to be a way to flip from ZALB to ZADP view.
	 * This method will temporarily make the ZADP as the default B2BUnit. It is the responsibility of the client to
	 * invoke {@link #turnBackImpersonation()} method as and when required to switch the application back to ZALB mode
	 *
	 * @param customerModel
	 *           the customer model
	 */
	@Override
	public void turnOffImpersonation(final B2BCustomerModel customerModel)
	{
		final B2BUnitModel b2bUnit = findTopLevelB2BUnit(customerModel);
		if (b2bUnit != null)
		{
			final B2BUnitModel impersonatedB2BUnit = (B2BUnitModel) getSessionService()
					.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
			getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATED_B2B_UNIT,
					null == impersonatedB2BUnit ? customerModel.getDefaultB2BUnit() : impersonatedB2BUnit);

			getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, b2bUnit);
		}
	}

	/**
	 * This method needs to be invoked in conjunction with {@link #turnOffImpersonation(B2BCustomerModel)}. This
	 * implementation puts the original ZALB back in the session
	 */
	@Override
	public void turnBackImpersonation()
	{
		final B2BUnitModel impersonatedB2BUnit = (B2BUnitModel) getSessionService()
				.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATED_B2B_UNIT);
		if (impersonatedB2BUnit != null)
		{
			getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, impersonatedB2BUnit);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#searchB2BUnit(com.sabmiller.core.b2b.dao.
	 * SearchB2BUnitQueryParam)
	 */
	@Override
	public List<B2BUnitModel> searchB2BUnit(final SearchB2BUnitQueryParam aueryParam)
	{
		return b2bUnitDao.searchB2BUnit(aueryParam);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#updateDefaultCustomerUnit(java.lang.String)
	 */
	@Override
	public boolean updateDefaultCustomerUnit(final String unitId, final B2BCustomerModel customer)
	{
		if (BooleanUtils.isTrue(customer.getPreviousB2bUnit()) || isTopLevelB2BUnit(customer.getDefaultB2BUnit()))
		{
			final B2BUnitModel defaultUnit = findAvaliableDefaultUnit(unitId, customer);
			if (defaultUnit == null)
			{
				return false;
			}
			customer.setDefaultB2BUnit(defaultUnit);
			getModelService().save(customer);

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#updateDefaultCustomerUnit(java.lang.String)
	 */
	public void updateDefaultCustomerUnit(final String unitId)
	{
		if (unitId == null)
		{
			return;
		}
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) userModel;
			if (unitId.equals(PERVIOUS_B2BUNIT))
			{
				updateRemenberUnitFlag(true, customer);
			}
			else
			{
				//current unit will not be change if only reset default unit.
				if (getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT) == null)
				{
					getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, customer.getDefaultB2BUnit());
				}
				setSelectedUnitAsDefault(unitId, customer);
			}
		}

	}

	/**
	 * Find avaliable default unit.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customer
	 *           the customer
	 * @return the b2 b unit model
	 */
	private B2BUnitModel findAvaliableDefaultUnit(final String unitId, final B2BCustomerModel customer)
	{
		B2BUnitModel defaultUnit = null;
		//set selected b2b unit to default unit

		final B2BUnitModel zadpB2BUnit = findTopLevelB2BUnit(customer);
		final boolean customerBelongsToZADP = (zadpB2BUnit != null);
		if (customerBelongsToZADP)
		{
			for (final PrincipalModel principalModel : SetUtils.emptyIfNull(zadpB2BUnit.getMembers()))
			{
				if (principalModel instanceof B2BUnitModel)
				{
					if (principalModel.getUid() != null && unitId != null && unitId.equals(principalModel.getUid())
							&& !SabmUtils.isUserDisabledForCUBAccount((B2BUnitModel) principalModel, customer))
					{
						defaultUnit = (B2BUnitModel) principalModel;
						break;
					}
				}
			}
		}
		else
		{
			final Set<PrincipalGroupModel> groups = customer.getGroups();
			for (final PrincipalGroupModel principalGroupModel : groups)
			{
				if (principalGroupModel instanceof AsahiB2BUnitModel)
				{
					continue;
				}


				if (principalGroupModel instanceof B2BUnitModel)
				{
					//set default unit
					if (principalGroupModel.getUid() != null && unitId != null && unitId.equals(principalGroupModel.getUid())
							&& !SabmUtils.isUserDisabledForCUBAccount((B2BUnitModel) principalGroupModel, customer))
					{
						defaultUnit = (B2BUnitModel) principalGroupModel;
						break;
					}
				}
			}
		}
		return defaultUnit;
	}

	/**
	 * Sets the selected unit as default.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customer
	 *           the customer
	 */
	private void setSelectedUnitAsDefault(final String unitId, final B2BCustomerModel customer)
	{

		final B2BUnitModel defaultUnit = findAvaliableDefaultUnit(unitId, customer);
		if (defaultUnit != null)
		{
			customer.setPreviousB2bUnit(false);
			customer.setDefaultB2BUnit(defaultUnit);
			getModelService().save(customer);
		}

	}

	/**
	 * Update remenber unit flag.
	 *
	 * @param flag
	 *           the flag
	 * @param customer
	 *           the customer
	 */
	public void updateRemenberUnitFlag(final boolean flag, final B2BCustomerModel customer)
	{
		customer.setPreviousB2bUnit(flag);
		getModelService().save(customer);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#updateDefaultCustomerUnit(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean updateDefaultCustomerUnit(final String unitId, final String customerId)
	{
		//set default b2bunit when changed b2bunit if remember previous business unit flag is true
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			return updateDefaultCustomerUnit(unitId, (B2BCustomerModel) userModel);
		}
		return false;
	}

	/**
	 * Gets the custmoers except zadp.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the custmoers except zadp
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#getCustmoersExceptZADP(de.hybris.platform.b2b.model.
	 *      B2BUnitModel)
	 */
	@Override
	public List<B2BCustomerModel> getCustmoersExceptZADP(final B2BUnitModel b2bUnitModel)
	{
		final List<B2BCustomerModel> customerListExceptZADP = new ArrayList<B2BCustomerModel>();
		//find ZADP unit for current b2bUnit
		B2BUnitModel zadpUnit = null;
		if (isTopLevelB2BUnit(b2bUnitModel))
		{
			zadpUnit = b2bUnitModel;
		}
		else if (b2bUnitModel.getGroups() != null)
		{
			for (final PrincipalGroupModel principalGroup : b2bUnitModel.getGroups())
			{
				if (principalGroup instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				if (principalGroup instanceof B2BUnitModel)
				{
					final B2BUnitModel unit = (B2BUnitModel) principalGroup;
					if (isTopLevelB2BUnit(unit))
					{
						zadpUnit = unit;
						break;
					}
				}
			}
		}

		//List all users except ZADP user for current b2b unit
		if (zadpUnit != null)
		{
			for (final PrincipalModel principalGroup : b2bUnitModel.getMembers())
			{
				if (principalGroup != null && principalGroup.getClass().getName().equals(B2BCustomerModel.class.getName()))
				{
					final B2BCustomerModel customer = (B2BCustomerModel) principalGroup;
					final B2BUnitModel userZADPUnit = findTopLevelB2BUnit(customer);
					if (userZADPUnit == null)
					{
						customerListExceptZADP.add(customer);
					}
					else if (userZADPUnit.getUid().equals(zadpUnit.getUid()))
					{
						customerListExceptZADP.add(customer);

					}
				}
			}
		}

		return customerListExceptZADP;
	}

	/*
	 * method to filter our user who should have access to invoice discrepancy functions.
	 */
	@Override
	public List<B2BCustomerModel> getCustomersWithInvoicePermission(final B2BUnitModel b2bUnitModel)
	{
		final List<B2BCustomerModel> customerListWithInvoicePermission = new ArrayList<>();

		for (final PrincipalModel principalGroup : b2bUnitModel.getMembers())
		{

			if (principalGroup instanceof B2BCustomerModel)
			{

				final B2BCustomerModel customer = (B2BCustomerModel) principalGroup;
				if (customer.getActive())
				{
					if (customer.getPrimaryAdmin())
					{
						customerListWithInvoicePermission.add(customer);

					}
					for (final PrincipalModel group : customer.getGroups())
					{

						final UserGroupModel userGroup = (UserGroupModel) group;
						if (userGroup.getUid().equals("b2binvoicecustomer"))
						{

							customerListWithInvoicePermission.add(customer);

						}
					}
				}
			}
		}

		return customerListWithInvoicePermission;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#removeCustomerFromUnit(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean removeCustomerFromUnit(final String unitId, final String customerId)
	{
		try
		{
			Assert.notNull(unitId, "unitId cannot be null");
			Assert.notNull(customerId, "customerId cannot be null");
			final UserModel user = getUserService().getUserForUID(customerId);
			final B2BUnitModel group = getUnitForUid(unitId);

			final ArrayList<String> disabledMembers = new ArrayList<String>(group.getCubDisabledUsers());
			if (!disabledMembers.contains(customerId))
			{

				disabledMembers.add(customerId);
				group.setCubDisabledUsers(disabledMembers);
				getModelService().save(group);
				getModelService().refresh(group);

				user.setModifiedtime(new Date());
				getModelService().save(user);
				getModelService().refresh(user);

			}
			return true;
		}
		catch (final Exception e)
		{
			throw e;
		}

	}

	@Override
	public Collection<String> getCUBDisabledList(final String b2bUnitId)
	{
		final B2BUnitModel b2bUnitModel = getUnitForUid(b2bUnitId);
		return CollectionUtils.isEmpty(b2bUnitModel.getCubDisabledUsers()) ? null : b2bUnitModel.getCubDisabledUsers();
	}

	@Override
	public List<B2BUnitModel> getActiveB2BUnitModelsByCustomer(final String uid)
	{

		if (StringUtils.isBlank(uid))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) b2bCustomerService.getUserForUID(uid);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		final List<B2BUnitModel> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{

			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnit.getActive()) && (CollectionUtils.isEmpty(b2bUnit.getCubDisabledUsers())
						|| !(b2bUnit.getCubDisabledUsers().contains(customer.getUid()))))
				{
					b2bUnits.add(b2bUnit);
				}
				else if (BooleanUtils.isFalse(b2bUnit.getActive()))
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnit);
				}
			}
		}
		return b2bUnits;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmB2BUnitService#getNoneZADPUsersWithSpecifiedBusinessUnit(de.hybris.platform.
	 * b2b.model.B2BUnitModel, de.hybris.platform.core.model.user.UserModel)
	 */
	@Override
	public List<B2BCustomerModel> getNoneZADPUsersWithSpecifiedBusinessUnit(final B2BUnitModel businessUnit,
			final UserModel excludeUser)
	{
		final HashMap<String, B2BCustomerModel> customerHash = new HashMap<String, B2BCustomerModel>();

		/*
		 * Find zalb business unit(s)
		 */
		final List<B2BUnitModel> zalbBusinessUnits = new ArrayList<B2BUnitModel>();
		if (SabmCoreConstants.ZADP.equals(businessUnit.getAccountGroup()))
		{
			final Set<PrincipalModel> members = businessUnit.getMembers();
			for (final PrincipalModel member : members)
			{
				if (member instanceof B2BUnitModel)
				{
					final B2BUnitModel zalbBusinessUnit = (B2BUnitModel) member;
					zalbBusinessUnits.add(zalbBusinessUnit);
				}
			}
		}
		else
		{
			zalbBusinessUnits.add(businessUnit);
		}

		/*
		 * Iterate all the zalb business unit and find the belonging user which isn't zadp user.
		 */
		for (final B2BUnitModel zalbBusinessUnit : zalbBusinessUnits)
		{
			final Set<PrincipalModel> members = zalbBusinessUnit.getMembers();
			for (final PrincipalModel member : members)
			{
				if (member != null && member.getClass().getName().equals(B2BCustomerModel.class.getName()))
				{
					final B2BCustomerModel customer = (B2BCustomerModel) member;

					if (!customerHash.containsKey(customer.getUid()))
					{
						// Check whether the user belong to a ZADP unit.
						final Set<PrincipalGroupModel> groups = customer.getGroups();

						boolean zadpUser = false;
						for (final PrincipalGroupModel group : groups)
						{
							if (group instanceof AsahiB2BUnitModel)
							{
								continue;
							}
							if (group instanceof B2BUnitModel)
							{
								final B2BUnitModel assignedBusinessUnit = (B2BUnitModel) group;
								if (SabmCoreConstants.ZADP.equals(assignedBusinessUnit.getAccountGroup()))
								{
									zadpUser = true;
									break;
								}
							}
						}

						if (!zadpUser)
						{
							customerHash.put(customer.getUid(), customer);
						}
					}
				}
			}
		}

		final List<B2BCustomerModel> returnValue = new ArrayList<B2BCustomerModel>();
		final Iterator<Entry<String, B2BCustomerModel>> iterator = customerHash.entrySet().iterator();
		while (iterator.hasNext())
		{
			returnValue.add(iterator.next().getValue());
		}

		return returnValue;
	}

	/**
	 * Find the ZADP Unit for the customer base on the customer group.
	 *
	 * @param customerModel
	 *           the customer model
	 * @return the list
	 */
	public List<B2BUnitModel> findCustomerTopLevelUnit(final B2BCustomerModel customerModel)
	{
		final List<B2BUnitModel> zadpUnitList = new ArrayList<B2BUnitModel>();
		final Set<PrincipalGroupModel> groups = customerModel.getGroups();
		final Map<String, B2BUnitModel> unitMap = new HashMap<String, B2BUnitModel>();

		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel assignedBusinessUnit = (B2BUnitModel) group;
				if (SabmCoreConstants.ZADP.equals(assignedBusinessUnit.getAccountGroup()))
				{
					unitMap.put(assignedBusinessUnit.getUid(), assignedBusinessUnit);
				}
				else if (SabmCoreConstants.ZALB.equals(assignedBusinessUnit.getAccountGroup()))
				{
					final Set<PrincipalGroupModel> unitGroups = assignedBusinessUnit.getGroups();
					for (final PrincipalGroupModel tempGroup : unitGroups)
					{
						if (SabmCoreConstants.ZADP.equals(((B2BUnitModel) tempGroup).getAccountGroup()))
						{
							unitMap.put(tempGroup.getUid(), ((B2BUnitModel) tempGroup));
						}
					}
				}
			}
		}
		if (MapUtils.isNotEmpty(unitMap))
		{
			for (final Map.Entry<String, B2BUnitModel> entry : unitMap.entrySet())
			{
				zadpUnitList.add(entry.getValue());
			}
		}
		return zadpUnitList;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#getContactAddressFormB2BUnit(de.hybris.platform.b2b.model.
	 * B2BUnitModel)
	 */
	@Override
	public AddressModel getContactAddressFormB2BUnit(final B2BUnitModel b2bUnit)
	{
		final AddressModel address = b2bUnit.getContactAddress();
		if (null == address)
		{
			final Collection<AddressModel> addresses = b2bUnit.getAddresses();
			if (CollectionUtils.isNotEmpty(addresses))
			{
				for (final AddressModel addressModel : addresses)
				{
					if (BooleanUtils.isTrue(addressModel.getContactAddress()))
					{
						return addressModel;
					}
				}
			}
			else
			{
				LOG.warn("This B2BUnit[{}] not set the contact address information.", b2bUnit);
				return null;
			}
		}
		return address;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findPrimaryAdminStatus(java.lang.String)
	 */
	@Override
	public String findPrimaryAdminStatus(final String b2bUnitId)
	{
		final B2BUnitModel b2bUnit = this.getUnitForUid(b2bUnitId);
		if (null != b2bUnit)
		{
			//get the PrimaryAdmin user from B2BUnitModel
			final B2BCustomerModel primaryAdmin = findPrimaryAdminbyB2BUnit(b2bUnit);
			if (null != primaryAdmin)
			{
				// return the status for the customer
				return getPrimaryAdminStatus(primaryAdmin);
			}
		}
		LOG.debug("The b2bUnitId [{}] and the b2bUnit [{}]  not found Primary Admin, then return INActive", b2bUnitId, b2bUnit);
		return SabmCoreConstants.SEARCH_B2BUNIT_STATUS_INACTIVE;
	}

	/**
	 * get the PrimaryAdmin user from B2BUnitModel.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return B2BCustomerModel
	 */
	private B2BCustomerModel findPrimaryAdminbyB2BUnit(final B2BUnitModel b2bUnit)
	{
		for (final PrincipalModel pModel : SetUtils.emptyIfNull(b2bUnit.getMembers()))
		{
			if (pModel != null && pModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
			{
				final B2BCustomerModel userModel = (B2BCustomerModel) pModel;
				if (userModel.getPrimaryAdmin() != null && userModel.getPrimaryAdmin())
				{
					LOG.debug("Found Primary Admin :{} ", userModel.getUid());
					return userModel;
				}
			}
		}
		return null;
	}

	/**
	 * get the ZADP users from B2BUnitModel.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return B2BCustomerModel
	 */
	@Override
	public List<B2BCustomerModel> getZADPUsersByB2BUnit(final B2BUnitModel b2bUnit)
	{
		final List<B2BCustomerModel> customers = new ArrayList<>();
		if (null != b2bUnit)
		{
			for (final PrincipalModel pModel : SetUtils.emptyIfNull(b2bUnit.getMembers()))
			{
				if (pModel != null && pModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
				{
					final B2BCustomerModel userModel = (B2BCustomerModel) pModel;
					customers.add(userModel);
				}
			}
		}
		return customers;

	}

	/**
	 * get primaryAdmin status from B2BCustomerModel.
	 *
	 * @param primaryAdmin
	 *           the primary admin
	 * @return String
	 */
	public String getPrimaryAdminStatus(final B2BCustomerModel primaryAdmin)
	{
		//if the user's active is false then return inactive
		if (primaryAdmin.getActive())
		{
			// if the user's password not set then return invited
			if (StringUtils.isNotEmpty(primaryAdmin.getEncodedPassword()))
			{
				return SabmCoreConstants.SEARCH_B2BUNIT_STATUS_ACTIVE;
			}
			return SabmCoreConstants.SEARCH_B2BUNIT_STATUS_INVITED;
		}
		return SabmCoreConstants.SEARCH_B2BUNIT_STATUS_INACTIVE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#updateB2BUnitStatus(de.hybris.platform.b2b.model.
	 * B2BCustomerModel)
	 */
	@Override
	public void updateB2BUnitStatus(final CustomerModel customerModel, final boolean sendMail, final boolean setPassword)
	{
		for (final PrincipalGroupModel pModel : SetUtils.emptyIfNull(customerModel.getAllGroups()))
		{
			if (pModel instanceof B2BUnitModel && SabmCoreConstants.ZALB.equals(((B2BUnitModel) pModel).getAccountGroup()))
			{
				updateB2BUnitZALBStatus((B2BUnitModel) pModel, sendMail, setPassword);
			}
		}
	}

	/**
	 * when send mail or set password then should change the b2bunit's status.
	 *
	 * @param b2BUnitModel
	 *           the b2 b unit model
	 * @param sendMail
	 *           the send mail
	 * @param setPassword
	 *           the set password
	 */
	private void updateB2BUnitZALBStatus(final B2BUnitModel b2BUnitModel, final boolean sendMail, final boolean setPassword)
	{
		// if the status is null and send mail is true then  save the status is invited
		if (sendMail)
		{
			if (null == b2BUnitModel.getB2BUnitStatus() || B2BUnitStatus.INACTIVE.equals(b2BUnitModel.getB2BUnitStatus()))
			{
				b2BUnitModel.setB2BUnitStatus(B2BUnitStatus.INVITED);
				//save the status to model
				getModelService().save(b2BUnitModel);
			}
			else if (B2BUnitStatus.INVITED.equals(b2BUnitModel.getB2BUnitStatus())
					|| B2BUnitStatus.ACTIVE.equals(b2BUnitModel.getB2BUnitStatus()))
			{
				return;
			}
		}
		// if the status is invited  and set password is true then change the status is active
		else if (setPassword)
		{
			if (B2BUnitStatus.ACTIVE.equals(b2BUnitModel.getB2BUnitStatus()))
			{
				return;
			}
			else
			{
				b2BUnitModel.setB2BUnitStatus(B2BUnitStatus.ACTIVE);
				//save the status to model
				getModelService().save(b2BUnitModel);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#findB2BUnitStatus(java.lang.String)
	 */
	@Override
	public String findB2BUnitStatus(final String b2bUnitId)
	{
		final B2BUnitModel b2bUnit = this.getUnitForUid(b2bUnitId);

		if (null != b2bUnit)
		{
			// find the status from b2bunit
			return findStatusByB2bUnit(b2bUnit);
		}
		LOG.debug("The b2bUnitId [{}]  not found b2bunit, then return INActive", b2bUnitId);
		return B2BUnitStatus.INACTIVE.getCode().toLowerCase();
	}

	/**
	 * get the status from B2BUnitModel.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return String
	 */
	private String findStatusByB2bUnit(final B2BUnitModel b2bUnit)
	{
		//  if the status is active not go back
		if (null == b2bUnit.getB2BUnitStatus())
		{
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				return updateStatusByAsahiB2bUnit((AsahiB2BUnitModel) b2bUnit);
			}
			return updateStatusByB2bUnit(b2bUnit);
		}
		else if (b2bUnit.getB2BUnitStatus().equals(B2BUnitStatus.ACTIVE))
		{
			return B2BUnitStatus.ACTIVE.getCode().toLowerCase();
		}
		// if the status is null,invited or inactive then can change the status when search the b2bunit again
		else
		{
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				return updateStatusByAsahiB2bUnit((AsahiB2BUnitModel) b2bUnit);
			}
			return updateStatusByB2bUnit(b2bUnit);
		}
	}

	/**
	 * validate the status from b2bunit.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return String
	 */
	private String updateStatusByB2bUnit(final B2BUnitModel b2bUnit)
	{
		boolean onlyEmail = Boolean.FALSE;
		boolean emailAndPassword = Boolean.FALSE;
		// get the member from b2bunit
		for (final PrincipalModel pModel : SetUtils.emptyIfNull(b2bUnit.getMembers()))
		{
			// the model should be b2bcustumer
			if (pModel != null && pModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
			{
				final B2BCustomerModel userModel = (B2BCustomerModel) pModel;
				//the user active is true and the email is send  but the password not set
				if (userModel.getActive() && userModel.getWelcomeEmailStatus()
						&& !StringUtils.isNotEmpty(userModel.getEncodedPassword()))
				{
					onlyEmail = Boolean.TRUE;
				}
				//the user active is true and the email is send  but the password is set
				if (userModel.getActive() && userModel.getWelcomeEmailStatus()
						&& StringUtils.isNotEmpty(userModel.getEncodedPassword()))
				{
					emailAndPassword = Boolean.TRUE;
				}
			}
		}
		// update the status
		return updateStatus(b2bUnit, onlyEmail, emailAndPassword);

	}

	/**
	 * validate the status from AsahiB2bunit.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return String
	 */
	private String updateStatusByAsahiB2bUnit(final AsahiB2BUnitModel b2bUnit)
	{
		boolean onlyEmail = Boolean.FALSE;
		boolean emailAndPassword = Boolean.FALSE;
		// get the member from b2bunit
		for (final PrincipalModel pModel : SetUtils.emptyIfNull(b2bUnit.getMembers()))
		{
			// the model should be b2bcustumer
			if (pModel != null && pModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
			{
				final B2BCustomerModel userModel = (B2BCustomerModel) pModel;
				//the user active is true and the email is send  but the password not set
				final boolean welcomeEmail = userModel.getAsahiWelcomeEmailStatus() == null ? Boolean.TRUE
						: userModel.getAsahiWelcomeEmailStatus();
				if (userModel.getActive() && welcomeEmail && !StringUtils.isNotEmpty(userModel.getEncodedPassword()))
				{
					onlyEmail = Boolean.TRUE;
				}
				//the user active is true and the email is send  but the password is set
				if (userModel.getActive() && welcomeEmail && StringUtils.isNotEmpty(userModel.getEncodedPassword()))
				{
					emailAndPassword = Boolean.TRUE;
				}
			}
		}
		// update the status
		return updateStatus(b2bUnit, onlyEmail, emailAndPassword);

	}

	/**
	 * when the status is invited if the condition establishment then is only change to active
	 * <p>
	 * other status can change corresponding state.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param onlyEmail
	 *           the only email
	 * @param emailAndPassword
	 *           the email and password
	 * @return String
	 */
	private String updateStatus(final B2BUnitModel b2bUnit, final boolean onlyEmail, final boolean emailAndPassword)
	{
		boolean saveStatusFlag = false;
		// the status is null or inactive can change corresponding state
		if (null == b2bUnit.getB2BUnitStatus())
		{
			if (emailAndPassword)
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.ACTIVE);
				saveStatusFlag = Boolean.TRUE;
			}
			else if (onlyEmail && !emailAndPassword)
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.INVITED);
				saveStatusFlag = Boolean.TRUE;
			}
			else
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.INACTIVE);
				saveStatusFlag = Boolean.TRUE;
			}
		}
		else if (B2BUnitStatus.INACTIVE.equals(b2bUnit.getB2BUnitStatus()))
		{
			if (emailAndPassword)
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.ACTIVE);
				saveStatusFlag = Boolean.TRUE;
			}
			else if (onlyEmail && !emailAndPassword)
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.INVITED);
				saveStatusFlag = Boolean.TRUE;
			}
		}
		// the status is invited if the condition establishment then is only change to active
		else if (B2BUnitStatus.INVITED.equals(b2bUnit.getB2BUnitStatus()))
		{
			if (emailAndPassword)
			{
				b2bUnit.setB2BUnitStatus(B2BUnitStatus.ACTIVE);
				saveStatusFlag = Boolean.TRUE;
			}
		}
		if (saveStatusFlag)
		{
			// save the status to b2bunit
			getModelService().save(b2bUnit);
		}

		return b2bUnit.getB2BUnitStatus().getCode().toLowerCase();
	}

	@Override
	public List<B2BUnitModel> searchB2BUnitByAccount(final String accountNumber)
	{
		return b2bUnitDao.searchB2BUnitByAccount(accountNumber);
	}

	@Override
	public List<B2BUnitModel> searchB2BUnitByCustomer(final String customerNumber, final String customerName)
	{
		return b2bUnitDao.searchB2BUnitByCustomer(customerNumber, customerName);
	}

	/**
	 * Fetches the sub channel from the given B2B unit. Ensures that the supplied B2BUnit is a ZALB type unit before
	 * retrieving the sub-channel.
	 *
	 * @param b2BUnitModel
	 *           the B2B unit to retrieve the sub-channel from.
	 * @return the B2B unit's sub-channel as a string.
	 */
	public String getSubChannelByB2BUnit(final B2BUnitModel b2BUnitModel)
	{
		if (!SabmCoreConstants.ZALB.equals(b2BUnitModel.getAccountGroup()))
		{
			LOG.error("Unable to retrieve sub-channel, B2BUnit should be a ZALB.");
			return StringUtils.EMPTY;
		}

		final B2BUnitGroupModel b2BUnitGroupModel = b2BUnitModel.getSapGroup();

		if (b2BUnitGroupModel == null)
		{
			LOG.error("Unable to retrieve sub-channel, B2BUnit has no SAP Group.");
			return StringUtils.EMPTY;
		}

		return b2BUnitGroupModel.getSubChannel();
	}

	/**
	 * @param b2bUnitModel
	 * @param currentDeliveryDate
	 */
	public void updateCUP(final B2BUnitModel b2bUnitModel, final Date currentDeliveryDate)
	{

		LOG.debug("Invoking the SAP service to refresh the CUP. b2bUnit {}", b2bUnitModel);

		LOG.debug("importFromCUPResponse currentDeliveryDate::", currentDeliveryDate);

		final String userId = getUserService().getCurrentUser().getUid();
		final boolean isCUPObsolete = isCUPObsolete(b2bUnitModel, currentDeliveryDate);

		LOG.debug("isCUPObsolete: {} for unit -  " + b2bUnitModel.getUid() + " --- currentDeliveryDate : " + currentDeliveryDate,
				isCUPObsolete);


		if (!isCUPObsolete || !shouldCallSapService(b2bUnitModel.getCupCallStatus()))
		{
			return;
		}


		b2bUnitModel.setCupCallStatus(SapServiceCallStatus.IN_PROGRESS);
		b2bUnitModel.setDiscountCallStatus(SapServiceCallStatus.IN_PROGRESS);
		getModelService().save(b2bUnitModel);

		final ExecutorService executorService = Executors.newCachedThreadPool();
		try
		{
			executorService.execute(() -> {
				onThreadExecution(userId, currentDeliveryDate);
				try
				{
					importFromCUPResponse(b2bUnitModel, currentDeliveryDate, userId);
				}
				finally
				{
					afterThreadExecution();
				}
			});

		}
		finally
		{
			executorService.shutdown();
		}

	}

	/*
	 * * Find old LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate < deliveryBefore.
	 *
	 * @param deliveryBefore the delivery before
	 *
	 * @param batchSize the batch size
	 *
	 * @return list of @LastUpdateTimeEntityModel
	 */
	@Override
	public List<LastUpdateTimeEntityModel> findOldLastUpdateTimeEntities(final Date deliveryBefore, final int batchSize)
	{
		return b2bUnitDao.findOldLastUpdateTimeEntities(deliveryBefore, batchSize);
	}

	@Override
	public String getOrderingStatus(final B2BUnitModel b2bUnitModel, final Date ordersAfterDate)
	{

		final List<OrderModel> orders = b2bUnitDao.findB2BunitOrders(b2bUnitModel, ordersAfterDate);

		String webOrderingStatus = SabmCoreConstants.B2BUNIT_ORDERING_STATUS_NONE;
		if (CollectionUtils.isNotEmpty(orders))
		{
			webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_NONE;
			int webOrdersCount = 0;
			for (final OrderModel order : orders)
			{
				if (SalesApplication.WEB.equals(order.getSalesApplication()))
				{
					webOrdersCount++;
				}
			}
			if (webOrdersCount > 0)
			{
				webOrderingStatus = getWebOrderingStatus(webOrdersCount, orders.size());
			}

		}
		return webOrderingStatus;

	}

	private String getWebOrderingStatus(final int webOrdersCount, final int totalOrders)
	{

		final int webOrdersPercentage = (webOrdersCount * 100) / totalOrders;

		String webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_RED;

		if (webOrdersPercentage >= SabmCoreConstants.B2BUNIT_WEB_ORDERING_PERCENTAGE_GREEN)
		{

			webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_GREEN;

		}
		else if (webOrdersPercentage >= SabmCoreConstants.B2BUNIT_WEB_ORDERING_PERCENTAGE_YELLOW)
		{
			webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_YELLOW;

		}

		return webOrderingStatus;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#refreshCup(de.hybris.platform.b2b.model.B2BUnitModel) Get
	 * users who active since a week. Get the prices for default B2Bunit for the active user from SAP Price update in
	 * price row.
	 */
	@Override
	public void importLastWeekCustomersCUP()
	{

		LOG.debug("Invoking the SAP service to refresh the CUP By Cron Job");
		final Set<B2BCustomerModel> b2bCustomers = sabmB2BCustomerDao.getCustomerByLastLogIn();
		final Set<B2BUnitModel> b2bUnits = new HashSet<>();

		LOG.info("Processing customer CUP data from cron job for " + b2bCustomers.size());
		for (final B2BCustomerModel b2bCustomer : b2bCustomers)
		{
			try
			{
				final B2BUnitModel b2bUnitModel = b2bCustomer.getDefaultB2BUnit();

				//if no default b2bunit or if cup prices already fetched for b2bunit
				if (null == b2bUnitModel || b2bUnits.contains(b2bUnitModel))
				{
					continue;
				}
				b2bUnits.add(b2bUnitModel);

				LOG.info("Invoking the SAP service to refresh the CUP. b2bUnit {}", b2bUnitModel);

				//final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
				final Set<Date> enabledCalendarDates = deliveryDateCutOffService.enabledCalendarDates(b2bUnitModel);
				Date currentDeliveryDate = null;
				if (CollectionUtils.isNotEmpty(enabledCalendarDates))
				{
					final List<Date> sortedList = new ArrayList<>(enabledCalendarDates);
					Collections.sort(sortedList);

					currentDeliveryDate = sortedList.get(0);
				}
				else
				{
					final Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, 1);

					currentDeliveryDate = cal.getTime();
				}

				LOG.debug("importCUP currentDeliveryDate::", currentDeliveryDate);

				final String userId = b2bCustomer.getUid();

				if (isCUPObsolete(b2bUnitModel, currentDeliveryDate))
				{
					//onThreadExecution(userId, currentDeliveryDate);
					getSessionService().setAttribute(sessionAttrUserId, userId);
					getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, currentDeliveryDate);
					//markCupRefreshInProgress(b2bUnitModel);

					boolean success = true;
					try
					{
						LOG.info("Trigger from Cron job for fetching CUP data to avoid the load on peak hours");
						//Invoke the SAP Web service to fetch CUP for all the products associated with the customer (B2BUnit)
						final CustomerUnitPricingResponse cupResponse = cupRequestHandler.sendPostRequest(
								cupRequestConverter.convert(b2bUnitModel, new CustomerUnitPricingRequest(), currentDeliveryDate));
						//Convert the response to PriceModels and persist
						final List<PriceRowModel> priceRows = cupReverseConverter.convert(cupResponse);
						for (final PriceRowModel priceRow : ListUtils.emptyIfNull(priceRows))
						{
							try
							{
								getModelService().save(priceRow);
							}
							catch (final Exception e)
							{
								LOG.error("Exception occured while trying to save price for product:", priceRow.getProductId(), e);
							}
						}

						//Finally put an entry on B2BUnit denoting when that the deal was refreshed
						markCUPAsRefreshed(b2bUnitModel, currentDeliveryDate);

						//Set the status to done
						b2bUnitModel.setCupCallStatus(SapServiceCallStatus.DONE);
						getModelService().save(b2bUnitModel);

					}
					catch (final Exception e)
					{
						LOG.error("Exception occured while trying to refresh CUP ", e);
						success = false;
					}
					finally
					{
						if (!success)
						{
							LOG.error("CUP prices not updated due exception");
							getModelService().detachAll();
							getModelService().refresh(b2bUnitModel);
							b2bUnitModel.setCupCallStatus(SapServiceCallStatus.ERROR);
							getModelService().save(b2bUnitModel);
						}
					}
				}
			}
			catch (final Exception e)
			{

				LOG.error("Cron Job Trigger : CUP prices not updated due exception for b2bunit",
						b2bCustomer.getDefaultB2BUnit().getId(), e);
			}
		}

	}

	/**
	 * Helper method either to call sap, based only on status
	 *
	 * @param sapServiceCallStatus
	 * @return
	 */
	private boolean shouldCallSapService(final SapServiceCallStatus sapServiceCallStatus)
	{
		return !SapServiceCallStatus.IN_PROGRESS.equals(sapServiceCallStatus);
	}

	@Override
	public String getAsahiOrderingStatus(final AsahiB2BUnitModel b2bUnitModel, final Date ordersAfterDate)
	{
		final List<OrderModel> orders = b2bUnitDao.findB2BunitOrders(b2bUnitModel, ordersAfterDate);

		String webOrderingStatus = SabmCoreConstants.B2BUNIT_ORDERING_STATUS_NONE;
		if (CollectionUtils.isNotEmpty(orders))
		{
			int webOrdersCount = 0;
			for (final OrderModel order : orders)
			{
				if (order.getOrderType().toString().equals(ASAHI_ORDER_TYPE_ONLINE))
				{
					webOrdersCount++;
				}
			}
			if (webOrdersCount > 0)
			{
				webOrderingStatus = getAsahiWebOrderingStatus(webOrdersCount, orders.size());
			}

		}
		return webOrderingStatus;
	}

	private String getAsahiWebOrderingStatus(final int webOrdersCount, final int totalOrders)
	{

		final int webOrdersPercentage = (webOrdersCount * 100) / totalOrders;

		String webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_RED;

		if (webOrdersPercentage >= SabmCoreConstants.ASAHI_B2BUNIT_WEB_ORDERING_PERCENTAGE_GREEN)
		{

			webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_GREEN;

		}
		else if (webOrdersPercentage < SabmCoreConstants.ASAHI_B2BUNIT_WEB_ORDERING_PERCENTAGE_GREEN && webOrdersPercentage > 0)
		{
			webOrderingStatus = SabmCoreConstants.B2BUNIT_WEB_ORDERING_STATUS_YELLOW;

		}

		return webOrderingStatus;

	}

	public DealsService getDealsService()
	{
		return dealsService;
	}

	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	/**
	 * @return the b2bCustomerService
	 */
	public B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	/**
	 * @param b2bCustomerService
	 *           the b2bCustomerService to set
	 */
	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	/**
	 *
	 *
	 */
	@Override
	public List<ShippingCarrierModel> getAllowedCarries(final List<ShippingCarrierModel> carriers)
	{


		List<ShippingCarrierModel> finalCarriers = carriers;
		final String commaSeparatedCarriers = getConfigurationService().getConfiguration().getString("cub.restricted.carriers.list",
				"0006000650");
		final List<String> restrictedCarriers = List.of(commaSeparatedCarriers.split(","));
		finalCarriers = carriers.stream().filter(
						carrier -> !restrictedCarriers.contains(carrier.getCarrierCode()))
				.collect(Collectors.toList());
		LOG.debug("finalCarriers Carriers List {}", finalCarriers);
		return finalCarriers;
	}


}
