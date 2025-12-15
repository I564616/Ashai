package com.sabmiller.core.recommendation.service;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

import com.apb.core.model.ApbProductModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.base.Stopwatch;
import com.opencsv.CSVReader;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.cache.impl.SABMLRUCache;
import com.sabmiller.core.b2b.dao.SabmB2BUnitDao;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.b2b.services.SABMProductExclusionService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.enums.RecommendationGroupType;
import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.ProductExclusionModel;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.SABMRecommendationDPModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.model.SmartRecommendationModel;
import com.sabmiller.core.model.StagingSABMRecommendationModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.recommendation.dao.SabmRecommendationDao;
import com.sabmiller.core.stock.DefaultSabmCommerceStockService;
import com.sabmiller.core.util.SabmAzureStorageUtils;
import com.sabmiller.facades.deal.data.DealBaseProductJson;


/**
 * Created by raul.b.abatol.jr on 06/06/2017.
 */
public class DefaultSabmRecommendationService implements RecommendationService {


    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmRecommendationService.class);

    protected static final String SESSION_SMART_RECOMMENDATIONS = "session.smart.recommendation";


    private UserService userService;

    private SabmB2BUnitService b2bUnitService;

    private SabmRecommendationDao recommendationDao;

    private ModelService modelService;

    private ProductService productService;

    private SabmProductService sabmProductService;

    @Resource
    private SabmB2BUnitDao b2bUnitDao;

    @Resource(name = "catalogVersionDeterminationStrategy")
    private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

    private SabmAzureStorageUtils sabmAzureStorageUtils;
    private ConfigurationService configurationService;
    private SABMProductExclusionService sabmProductExclusionService;
    private CartService cartService;
    private SessionService sessionService;
    private CUBStockInformationService cubStockInformationService;
    private SabmConfigurationService sabmConfigurationService;
    private DealsService dealsService;
	 @Resource
	 private AsahiCoreUtil asahiCoreUtil;
    private B2BCommerceUnitService b2bCommerceUnitService;
 	 @Resource(name = "inclusionExclusionProductStrategy")
 	 private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;
 	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;
	@Resource
	private DefaultSabmCommerceStockService defaultSabmCommerceStockService;
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

    final static private String BASEPRODUCTDELIMITER = ";";
    final static private String PRODUCTINFODELIMITER = "-";

    private static final String DATE_ARCHIVE_FORMAT  = "dd-MM-yyyy_HH-mm-ss";
    protected static final List<ProductOption> ASAHI_RECOMMENDATIONS_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES);

    @Override
    public List<SABMRecommendationModel> getRecommendations() {
        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendations(selectedB2BUnit);
        return recommendations != null ? recommendations : Collections.emptyList();
    }

    @Override
    public List<SABMRecommendationModel> getDisplayableRecommendations() {
        final List<SABMRecommendationModel> recommendations = getRecommendations();
        if(recommendations.isEmpty()){
            return Collections.emptyList();
        }
        final List<ProductExclusionModel> exclusions = getSabmProductExclusionService().findProductEx();
        final B2BUnitModel b2BUnit = getB2bCommerceUnitService().getParentUnit();
        final Date sessionDeliveryDate = getSessionService().getOrLoadAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, Calendar.getInstance()::getTime);
        final PlantModel plant = b2BUnit.getPlant();
        return recommendations.stream().filter(r->this.shouldDisplayRecommendation(r,exclusions,plant,sessionDeliveryDate)).collect(Collectors.toList());
    }

    @Override
    public List<SABMRecommendationModel> getRecommendationsByID(final String id) {
        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendationsByID(selectedB2BUnit, id);
        return recommendations != null ? recommendations : Collections.emptyList();
    }

    @Override
    public List<SABMRecommendationModel> getRecommendationsByProductID(final String productID) {
        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendationsByProductID(selectedB2BUnit,
                productID);
        return recommendations != null ? recommendations : Collections.emptyList();
    }


    @Override
    public SABMRecommendationModel getRecommendationsByDealID(final String dealID) {
        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendationsByDealID(selectedB2BUnit, dealID);
        if (recommendations != null && CollectionUtils.isNotEmpty(recommendations)) {
            return recommendations.get(0);
        }
        return null;
    }

    private SABMRecommendationModel getRecommendationsByDealIDAndB2BUnit(final String dealID, final B2BUnitModel selectedB2BUnit) {
        final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendationsByDealID(selectedB2BUnit, dealID);
        if (recommendations != null && CollectionUtils.isNotEmpty(recommendations)) {
            return recommendations.get(0);
        }
        return null;
    }

    @Override
    public void saveProductAsRecommendation(final String productID, final Integer quantity, final UnitModel uom) {
        final List<SABMRecommendationModel> productRecommendations = getRecommendationsByProductID(productID);
        SABMRecommendationModel productRecommendation;
        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        if (CollectionUtils.isNotEmpty(productRecommendations)) {
            productRecommendation = productRecommendations.get(0);
        } else {
            productRecommendation = modelService.create(SABMRecommendationModel.class);
            productRecommendation.setB2bUnit(selectedB2BUnit);
            productRecommendation.setProductCode(productID);
            final String fName = ((B2BCustomerModel) currentUser).getFirstName();
            productRecommendation.setRecommendedBy(fName);

            productRecommendation.setRecommendedDate(new Date());
            productRecommendation.setRecommendationType(RecommendationType.PRODUCT);
            productRecommendation.setStatus(RecommendationStatus.RECOMMENDED);
            productRecommendation.setQty(0);
        }
        final Map<String, Object> qtyMap = getRecommendedQuantity(productID, productRecommendation.getUnit(),
                productRecommendation.getQty());
        productRecommendation.setQty((Integer) qtyMap.get("QTY") + quantity);
        productRecommendation.setUnit((UnitModel) qtyMap.get("UNIT"));
        modelService.save(productRecommendation);
    }

    private void saveProductAsRecommendationForBulkUpload(final StagingSABMRecommendationModel stagingRecommendationModel,
                                                          final B2BUnitModel b2bUnit) {
        final List<SABMRecommendationModel> productRecommendations = recommendationDao.getRecommendationsByProductID(b2bUnit,
                stagingRecommendationModel.getProductCode());
        SABMRecommendationModel productRecommendation;
        if (CollectionUtils.isNotEmpty(productRecommendations)) {
            productRecommendation = productRecommendations.get(0);
        } else {
            productRecommendation = modelService.create(SABMRecommendationModel.class);
            productRecommendation.setB2bUnit(b2bUnit);
            productRecommendation.setProductCode(stagingRecommendationModel.getProductCode());
            productRecommendation.setRecommendedDate(new Date());
            productRecommendation.setRecommendationType(RecommendationType.PRODUCT);
            productRecommendation.setStatus(RecommendationStatus.RECOMMENDED);
            productRecommendation.setQty(0);
        }
        final Map<String, Object> existingQtyMap = getRecommendedQuantity(stagingRecommendationModel.getProductCode(),
                productRecommendation.getUnit(), productRecommendation.getQty());
        final Map<String, Object> recommendationQtyMap = getRecommendedQuantity(stagingRecommendationModel.getProductCode(),
                stagingRecommendationModel.getProductUOM(), stagingRecommendationModel.getQty());
        productRecommendation.setQty((Integer) existingQtyMap.get("QTY") + (Integer) recommendationQtyMap.get("QTY"));
        productRecommendation.setUnit((UnitModel) existingQtyMap.get("UNIT"));
        productRecommendation.setRecommendedBy(stagingRecommendationModel.getRecommendedBy());
        modelService.save(productRecommendation);
    }


    @Override
    public SABMRecommendationModel saveDealAsRecommendation(final String dealID, final List<DealBaseProductJson> dealProductsList,
                                                            final boolean isUpdateRecommendation) {
        SABMRecommendationModel dealRecommendation = getRecommendationsByDealID(dealID);

        boolean recommendationExists = true;

        if (dealRecommendation == null) {
            recommendationExists = false;
            dealRecommendation = modelService.create(SABMRecommendationModel.class);
        }

        final UserModel currentUser = userService.getCurrentUser();
        final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
        final String fName = ((B2BCustomerModel) currentUser).getFirstName();

        dealRecommendation.setRecommendedDate(new Date());
        dealRecommendation.setRecommendedBy(fName);


        if (recommendationExists) {
            updateProductsInDealRecommendation(dealRecommendation, dealProductsList, isUpdateRecommendation);
        } else {
            dealRecommendation.setB2bUnit(selectedB2BUnit);
            dealRecommendation.setDealCode(dealID);
            dealRecommendation.setRecommendationType(RecommendationType.DEAL);
            dealRecommendation.setStatus(RecommendationStatus.RECOMMENDED);
            addProductsInDealRecommendation(dealRecommendation, dealProductsList);
        }


        modelService.save(dealRecommendation);
        return dealRecommendation;
    }

    private void saveDealAsRecommendationForBulkUpload(final StagingSABMRecommendationModel stagingRecommendationModel,
                                                       final B2BUnitModel b2bUnit) {
        SABMRecommendationModel dealRecommendation = getRecommendationsByDealIDAndB2BUnit(stagingRecommendationModel.getDealCode(),
                b2bUnit);

        boolean recommendationExists = true;

        if (dealRecommendation == null) {
            recommendationExists = false;
            dealRecommendation = modelService.create(SABMRecommendationModel.class);
        }

        final List<DealBaseProductJson> dealProductsList = new ArrayList<DealBaseProductJson>();
        getDealProductJsonList(stagingRecommendationModel.getDealProducts(), dealProductsList);

        dealRecommendation.setRecommendedDate(new Date());
        dealRecommendation.setRecommendedBy(stagingRecommendationModel.getRecommendedBy());

        if (recommendationExists) {
            updateProductsInDealRecommendation(dealRecommendation, dealProductsList, false);
        } else {
            dealRecommendation.setB2bUnit(b2bUnit);
            dealRecommendation.setDealCode(stagingRecommendationModel.getDealCode());
            dealRecommendation.setRecommendationType(RecommendationType.DEAL);
            dealRecommendation.setStatus(RecommendationStatus.RECOMMENDED);
            addProductsInDealRecommendation(dealRecommendation, dealProductsList);
        }
        modelService.save(dealRecommendation);
    }


    private void updateProductsInDealRecommendation(final SABMRecommendationModel dealRecommendation,
                                                    final List<DealBaseProductJson> dealProductsList, final boolean isUpdateRecommendation) {

        final List<SABMRecommendationDPModel> existingdealProducts = dealRecommendation.getDealProducts();
        final List<SABMRecommendationDPModel> updatedDealProducts = new ArrayList<SABMRecommendationDPModel>();
        updatedDealProducts.addAll(existingdealProducts);
        for (final DealBaseProductJson dealBaseProduct : dealProductsList) {

            SABMRecommendationDPModel dealProductModel = getExistingDealProduct(updatedDealProducts, dealBaseProduct);
            if (dealProductModel == null) {
                dealProductModel = modelService.create(SABMRecommendationDPModel.class);
                dealProductModel.setQty(dealBaseProduct.getQty());
                dealProductModel.setProductCode(dealBaseProduct.getProductCode());
                //final ProductModel product = productService.getProductForCode(dealBaseProduct.getProductCode());
                final ProductModel product = getProductForCode(dealBaseProduct.getProductCode());
                dealProductModel.setUnit(product.getUnit());
                modelService.save(dealProductModel);
                updatedDealProducts.add(dealProductModel);
            } else {
                if (isUpdateRecommendation) {
                    dealProductModel.setQty(dealBaseProduct.getQty());
                } else {
                    dealProductModel.setQty(dealProductModel.getQty() + dealBaseProduct.getQty());

                }

                modelService.save(dealProductModel);
            }
        }
        dealRecommendation.setDealProducts(updatedDealProducts);
        modelService.save(dealRecommendation);

        modelService.refresh(dealRecommendation);

    }

    private void addProductsInDealRecommendation(final SABMRecommendationModel dealRecommendation,
                                                 final List<DealBaseProductJson> dealProductsList) {

        final List<SABMRecommendationDPModel> dealProductsToSave = new ArrayList<SABMRecommendationDPModel>();

        for (final DealBaseProductJson dealBaseProduct : dealProductsList) {
            final SABMRecommendationDPModel dealProductModel = modelService.create(SABMRecommendationDPModel.class);
            dealProductModel.setProductCode(dealBaseProduct.getProductCode());
            dealProductModel.setQty(dealBaseProduct.getQty());
            //final ProductModel product = productService.getProductForCode(dealBaseProduct.getProductCode());
            final ProductModel product = getProductForCode(dealBaseProduct.getProductCode());
            dealProductModel.setUnit(product.getUnit());
            modelService.save(dealProductModel);
            dealProductsToSave.add(dealProductModel);
        }

        dealRecommendation.setDealProducts(dealProductsToSave);
        modelService.save(dealRecommendation);
        modelService.refresh(dealRecommendation);

    }

    private SABMRecommendationDPModel getExistingDealProduct(final List<SABMRecommendationDPModel> existingDealProducts,
                                                             final DealBaseProductJson dealBaseProduct) {

        for (final SABMRecommendationDPModel dpModel : existingDealProducts) {
            if (dpModel.getProductCode().equals(dealBaseProduct.getProductCode())) {
                return dpModel;
            }
        }
        return null;
    }


    @Override
    public void updateProductRecommendation(final String recommendationID, final Integer quantity, final UnitModel uom) {
        final List<SABMRecommendationModel> recommendations = this.getRecommendationsByID(recommendationID);
        if (CollectionUtils.isNotEmpty(recommendations)) {
            final SABMRecommendationModel productRecommendation = recommendations.get(0);
            productRecommendation.setQty(quantity);
            productRecommendation.setUnit(uom);
            modelService.save(productRecommendation);
        }
    }

    @Override
    public void updateDealProductRecommendation(final SABMRecommendationModel dealRecommendation, final String productID,
                                                final Integer quantity) {
        final List<SABMRecommendationDPModel> recommendationDealProducts = dealRecommendation.getDealProducts();
        SABMRecommendationDPModel dealProduct = modelService.create(SABMRecommendationDPModel.class);
        Boolean isExisting = false;

        for (final SABMRecommendationDPModel dpModel : recommendationDealProducts) {
            if (dpModel.getProductCode().equals(productID)) {
                dealProduct = dpModel;
                isExisting = true;
                break;
            }
        }

        if (isExisting) {
            dealProduct.setQty(quantity);
            modelService.save(dealProduct);
            modelService.refresh(dealRecommendation);
        }
    }

    @Override
    public void updateRecommendation(final String recommendationID, final RecommendationStatus status) {
        final List<SABMRecommendationModel> recommendations = this.getRecommendationsByID(recommendationID);
        if (CollectionUtils.isNotEmpty(recommendations)) {
            final SABMRecommendationModel recommendation = recommendations.get(0);
            recommendation.setStatus(status);
            recommendation.setEntryActedBy(userService.getCurrentUser());
            recommendation.setCustomerActionDate(new Date());
            modelService.save(recommendation);
        }
    }

    @Override
    public void deleteRecommendationByID(final String recommendationID) {
        final List<SABMRecommendationModel> recommendations = this.getRecommendationsByID(recommendationID);
        if (CollectionUtils.isNotEmpty(recommendations)) {
            final SABMRecommendationModel recommendation = recommendations.get(0);
            final List<SABMRecommendationDPModel> recommendationDealProducts = recommendation.getDealProducts();

            if (CollectionUtils.isNotEmpty(recommendationDealProducts)) {
                modelService.removeAll(recommendationDealProducts);
            }

            modelService.remove(recommendation);
        }
    }

    @Override
    public Map<String, Object> getRecommendedQuantity(final String productID, UnitModel unit, final Integer quantity) {
        Long newQuantity = quantity.longValue();
        //final ProductModel product = productService.getProductForCode(productID);
        final ProductModel product = getProductForCode(productID);
        if (product == null) {
            return null;
        }
        if (unit == null) {
            unit = product.getUnit();
        }
        if (product instanceof SABMAlcoholVariantProductEANModel && unit != null && !unit.equals(product.getUnit())) {
            final List<ProductUOMMappingModel> uomMappings = ((SABMAlcoholVariantProductEANModel) product).getUomMappings();

            if (CollectionUtils.isNotEmpty(uomMappings)) {
                boolean isConversion = false;
                double calculatedBase = 0d;
                for (final ProductUOMMappingModel productUOM : uomMappings) {
                    //Find the corresponding base information where  the one selected from the dropdown be equal to  the CommerceCartParameter.unit
                    if (unit.equals(productUOM.getFromUnit()) && productUOM.getToUnit() != null
                            && productUOM.getToUnit().equals(product.getUnit()) && productUOM.getQtyConversion() != null) {
                        calculatedBase = productUOM.getQtyConversion().doubleValue();
                        isConversion = true;
                        break;
                    }
                }
                if (isConversion) {
                    // Calculated new value,update the quantity of the CommerceCartParameter.quantity with the new  value
                    newQuantity = BigDecimal.valueOf(calculatedBase).multiply(BigDecimal.valueOf(quantity)).longValue();
                } else {
                    LOG.warn("Unable to find conversion mapping for product:" + product.getCode() + " and units: " + unit.getCode()
                            + " - " + product.getUnit().getCode());
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ProductData.UomMappingList is empty for the product: " + product.getCode() + ", return old quantity");
                }

            }
        }
        final Map<String, Object> mapObj = new HashMap<String, Object>();
        unit = product.getUnit();
        mapObj.put(SabmCoreConstants.RECOMMENDATION_QTY, newQuantity.intValue());
        mapObj.put(SabmCoreConstants.RECOMMENDATION_UNIT, unit);
        return mapObj;
    }

    @Override
    @CacheEvict(value = "recommendationCache")

    public void updateRecommendationStatus(final SABMRecommendationModel recommendationModel, final RecommendationStatus status) {
        recommendationModel.setStatus(status);
        modelService.save(recommendationModel);
    }

    @Override
    public void updateRecommendationsForBulkUpload(final StagingSABMRecommendationModel stagingRecommendationModel,
                                                   final boolean isBannerInfoAvailable) throws Exception {
        try {
            if (isBannerInfoAvailable) {
                //fetch b2bunits based on banner info
                final List<B2BUnitModel> b2bUnitModelList = b2bUnitDao.findB2BUnitsByBannerAndPriceGroup(
                        stagingRecommendationModel.getPrimaryBanner(), stagingRecommendationModel.getSubBanner(),
                        stagingRecommendationModel.getPriceGroup());

                if (b2bUnitModelList.isEmpty()) {
                    throw new Exception("Invalid Banner information");
                } else {
                    for (final B2BUnitModel b2bUnit : b2bUnitModelList) {
                        routeRecommendationUpdatebyType(stagingRecommendationModel, b2bUnit);
                    }
                }

            } else {
                routeRecommendationUpdatebyType(stagingRecommendationModel, stagingRecommendationModel.getB2bUnit());
            }
        } catch (final Exception e) {
            throw new Exception(e);
        }
    }

    private void routeRecommendationUpdatebyType(final StagingSABMRecommendationModel stagingRecommendationModel,
                                                 final B2BUnitModel b2bUnit) {
        switch (stagingRecommendationModel.getRecommendationType()) {
            case PRODUCT:
                saveProductAsRecommendationForBulkUpload(stagingRecommendationModel, b2bUnit);
                break;

            case DEAL:
                saveDealAsRecommendationForBulkUpload(stagingRecommendationModel, b2bUnit);
                break;
        }
    }

    private ProductModel getProductForCode(final String productID) {
        final ProductModel product = productService.getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(),
                productID);
        return product;
    }

    private void getDealProductJsonList(final String dealProductsString, final List<DealBaseProductJson> dealProductsList) {
        final String[] dealProductsSplitted = dealProductsString.split(BASEPRODUCTDELIMITER);
        if (dealProductsSplitted.length > 0) {
            for (final String dealProduct : dealProductsSplitted) {
                final String[] ProductInfo = dealProduct.split(PRODUCTINFODELIMITER);
                if (ProductInfo.length == 3) {
                    final DealBaseProductJson obj = new DealBaseProductJson();
                    obj.setProductCode(ProductInfo[0]);
                    obj.setQty(Integer.parseInt((ProductInfo[1])));
                    obj.setUomCode(ProductInfo[2]);
                    dealProductsList.add(obj);
                }
            }
        }
    }

    /**
     * Retrieves file from Azure storage containing recommendations and saves it to Hybris
     */
    @Override
    public boolean retrieveAndSaveRecommendations(final CatalogVersionModel catalogVersion) throws NoSuchFileException {
        boolean result = true;

		final String containerReference = getConfigurationService().getConfiguration()
				.getString(SabmCoreConstants.RECOMMENDATION_CONTAINER_REFERENCE, "recommendationengine");
        final BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer(containerReference);
        if (null == container) {
            LOG.error("Container not found.");
            return false;
        }

        CSVReader csvReader = null;

        try {
            final String inputFileName = getConfigurationService().getConfiguration().getString(SabmCoreConstants.RECOMMENDATION_FILE_REFERENCE, "Recommendations.csv");
            final BlobClient blob = sabmAzureStorageUtils.getInputFile(inputFileName, container);

            if (null == blob) {
                throw new NoSuchFileException("Input file not found.");
            }

            final File inputFile = File.createTempFile(inputFileName, null);
            blob.downloadToFile(inputFile.getPath(), true);

            final Map<String,B2BUnitModel> b2BUnitCache = new SABMLRUCache<>(10000); // can be tweaked

            final Stopwatch stopwatch = Stopwatch.createStarted();

            LOG.info("Clearing B2bUnit previous recommendations.");

            if(!clearSmartRecommendationsFromCSV(inputFile,b2BUnitCache)){
                stopwatch.stop();
                LOG.error("Was not able to clear recommendations.");
                return false;
            }

            stopwatch.stop();

            LOG.info("Clearing B2bUnit previous recommendations finished in [{}]", stopwatch.toString());

            final Reader reader = Files.newBufferedReader(inputFile.toPath());
            csvReader = new CSVReader(reader);
            csvReader.skip(1);

            String[] nextRecord;

            final Map<String,ProductModel> productCache = new SABMLRUCache<>(4000); // Can be tweaked
            final Map<String,SmartRecommendationModel> sabmRecommendations = new HashMap<>(); // Unlimited power!!!!!
            while (null != (nextRecord = csvReader.readNext())) {

                if (!saveRecommendation(catalogVersion, nextRecord,b2BUnitCache,productCache,sabmRecommendations) && result) {
                    result = false;
                }
            }

            // save batch recommendation for less db calls
            saveRecommendations(sabmRecommendations.values());

            if (compressAndArchiveFile(inputFile, inputFileName, container)) {
                LOG.debug("Successfully compressed and archived file");
                inputFile.delete();
                blob.deleteIfExists();
            }
        } catch (final BlobStorageException e) {
            LOG.error(String.format("Error returned from the service. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
            return false;
        } catch (final NoSuchFileException e) {
            throw new NoSuchFileException("Input file not found.");
        } catch (final IOException e) {
            LOG.error("Error writing to file.");
            return false;
        }finally {
            IOUtils.closeQuietly(csvReader);
        }

        return result;
    }

    @Override
    public boolean retrieveAndSaveRecommendationsV2(final CatalogVersionModel catalogVersion) {
        LOG.debug("Before getting Azure Blob container.");
        final String containerReference = getConfigurationService().getConfiguration()
                .getString(SabmCoreConstants.RECOMMENDATION_CONTAINER_REFERENCE, "recommendationengine");
        final BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer(containerReference);
        if (null == container) {
            LOG.error("Unable to process smart recommendations because Azure Blob container not found.");
            return false;
        }
        LOG.debug("After getting Azure Blob container.");
        try {
            final String inputFileName = getConfigurationService().getConfiguration().getString(SabmCoreConstants.RECOMMENDATION_FILE_REFERENCE, "Recommendations.csv");
            final BlobClient blob = sabmAzureStorageUtils.getInputFile(inputFileName, container);
            if (null == blob) {
					throw new NoSuchFileException("Input file not found.");
				}
            LOG.debug("After getting input file {} from Azure Blob container.", inputFileName);
            final File inputFile = File.createTempFile(inputFileName, null);
            blob.downloadToFile(inputFile.getPath(),true);
            LOG.debug("After downloading input file {} to the temporary file {}.", inputFileName, inputFile.getParentFile());
            // Pre-process smart recommendations from incoming CSV file.
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final Map<String, Set<String>> productToB2BUnitM1 = new HashMap<>();
            final Map<String, Set<String>> productToB2BUnitM2 = new HashMap<>();
            final Map<String, Set<String>> productToB2BUnitM3 = new HashMap<>();
            loadSmartRecommendationsFromCSV(inputFile, productToB2BUnitM1, productToB2BUnitM2, productToB2BUnitM3);
            stopwatch.stop();
            LOG.debug("After loading smart recommendations from CSV. Time taken: {}. M1 products: {}, M2 products: {}, M3 products: {}.", stopwatch, productToB2BUnitM1.size(), productToB2BUnitM2.size(), productToB2BUnitM3.size());
            // Process existing smart recommendations. This includes update / delete use cases.
            stopwatch.reset().start();
            final List<SmartRecommendationModel> allSmartRecommendationsFromDB = getRecommendationDao().getAllSmartRecommendations();
            stopwatch.stop();
            LOG.debug("After reading existing smart recommendations from DB. Time taken: {}, Records: {}.", stopwatch, allSmartRecommendationsFromDB.size());
            final Map<String, B2BUnitModel> b2bUnitToMapCache = new HashMap<>();
            stopwatch.reset().start();
            final List<SmartRecommendationModel> smartRecommendationsForDeletion = new ArrayList<>();
            final List<SmartRecommendationModel> smartRecommendationsForUpdate = new ArrayList<>();
            for (final SmartRecommendationModel smartRecommendation : allSmartRecommendationsFromDB) {
                // Determine whether this is an update or delete.
                final Set<String> b2bUnits = new HashSet<>();
                switch (smartRecommendation.getType()) {
                    case MODEL1 ->
                            b2bUnits.addAll(productToB2BUnitM1.getOrDefault(smartRecommendation.getProduct().getCode(), new HashSet<>()));
                    case MODEL2 ->
                            b2bUnits.addAll(productToB2BUnitM2.getOrDefault(smartRecommendation.getProduct().getCode(), new HashSet<>()));
                    case MODEL3 ->
                            b2bUnits.addAll(productToB2BUnitM3.getOrDefault(smartRecommendation.getProduct().getCode(), new HashSet<>()));
                }
                if (b2bUnits.isEmpty()) {
                    // This is a delete case.
                    smartRecommendationsForDeletion.add(smartRecommendation);
                    continue;
                }
                // Handle the update case.
                final Optional<SmartRecommendationModel> updatedSmartRecommendation = updateSmartRecommendation(smartRecommendation, b2bUnits, b2bUnitToMapCache);
                if (updatedSmartRecommendation.isPresent()) {
                    smartRecommendationsForUpdate.add(smartRecommendation);
                } else {
                    smartRecommendationsForDeletion.add(smartRecommendation);
                }
                // Cleanup.
                switch (smartRecommendation.getType()) {
                    case MODEL1 -> productToB2BUnitM1.remove(smartRecommendation.getProduct().getCode());
                    case MODEL2 -> productToB2BUnitM2.remove(smartRecommendation.getProduct().getCode());
                    case MODEL3 -> productToB2BUnitM3.remove(smartRecommendation.getProduct().getCode());
                }
            }
            stopwatch.stop();
            LOG.debug("After processing update and delete cases. Time taken: {}. Records for deletion: {}. Records for update: {}.", stopwatch, smartRecommendationsForDeletion.size(), smartRecommendationsForUpdate.size());
            // Handle the insert case.
            final Map<String, ProductModel> productToProductCache = new HashMap<>();
            stopwatch.reset().start();
            final List<SmartRecommendationModel> smartRecommendationsForInsertion = new ArrayList<>();
            productToB2BUnitM1.forEach((product, b2bUnits) -> {
                createSmartRecommendation(product, b2bUnits, SmartRecommendationType.MODEL1, b2bUnitToMapCache, productToProductCache, catalogVersion).ifPresent(smartRecommendationsForInsertion::add);
            });
            productToB2BUnitM2.forEach((product, b2bUnits) -> {
                createSmartRecommendation(product, b2bUnits, SmartRecommendationType.MODEL2, b2bUnitToMapCache, productToProductCache, catalogVersion).ifPresent(smartRecommendationsForInsertion::add);
            });
            productToB2BUnitM3.forEach((product, b2bUnits) -> {
                createSmartRecommendation(product, b2bUnits, SmartRecommendationType.MODEL3, b2bUnitToMapCache, productToProductCache, catalogVersion).ifPresent(smartRecommendationsForInsertion::add);
            });
            // Cleanup.
            productToB2BUnitM1.clear();
            productToB2BUnitM2.clear();
            productToB2BUnitM3.clear();
            stopwatch.stop();
            LOG.debug("After processing create case. Time taken: {}. Records for creation: {}.", stopwatch, smartRecommendationsForInsertion.size());
            // Persist changes.
            stopwatch.reset().start();
            if (!smartRecommendationsForDeletion.isEmpty()) {
                getModelService().removeAll(smartRecommendationsForDeletion);
            }
            stopwatch.stop();
            LOG.debug("After persisting deletions. Time taken {}.", stopwatch);
            stopwatch.reset().start();
            if (!smartRecommendationsForUpdate.isEmpty()) {
                getModelService().saveAll(smartRecommendationsForUpdate);
            }
            stopwatch.stop();
            LOG.debug("After processing update case. Time taken {}.", stopwatch);
            stopwatch.reset().start();
            if (!smartRecommendationsForInsertion.isEmpty()) {
                getModelService().saveAll(smartRecommendationsForInsertion);
            }
            stopwatch.stop();
            LOG.debug("After processing create case. Time taken {}.", stopwatch);
            // Post-process the incoming file.
            stopwatch.reset().start();
            if (compressAndArchiveFile(inputFile, inputFileName, container)) {
                LOG.debug("Successfully compressed and archived file.");
                inputFile.delete();
                blob.deleteIfExists();
            }
            stopwatch.stop();
            LOG.debug("After post-processing the incoming file. Time taken {}.", stopwatch);
        } catch (final BlobStorageException e) {
            LOG.error("Error communicating with Azure Storage. Details: {}.", e.getMessage());
            return false;
        } catch (final NoSuchFileException e) {
            LOG.error("Error accessing CSV file. Details: {}.", e.getMessage());
        } catch (final IOException e) {
            LOG.error("Error processing smart recommendations. Details: {}.", e.getMessage());
            return false;
        }
        return true;
    }

    protected Optional<SmartRecommendationModel> updateSmartRecommendation(final SmartRecommendationModel smartRecommendation,
                                                final Set<String> newB2BUnitUids,
                                                final Map<String, B2BUnitModel> b2bUnitUidToMapCache) {
        final List<B2BUnitModel> newB2BUnits = new ArrayList<>();
        // Determine which existing b2b units should be kept.
        smartRecommendation.getB2bUnits().forEach((b2bUnit) -> {
            if (newB2BUnitUids.contains(b2bUnit.getUid())) {
                newB2BUnits.add(b2bUnit);
                newB2BUnitUids.remove(b2bUnit.getUid());
            }
        });
        // Process remaining net new b2b units.
        if (!newB2BUnitUids.isEmpty()) {
            final List<B2BUnitModel> b2BUnits = getB2BUnitsForUids(newB2BUnitUids, b2bUnitUidToMapCache);
            if (b2BUnits.isEmpty()) {
                LOG.warn("No B2BUnitModels found for uids: {}.", newB2BUnitUids);
            } else {
                newB2BUnits.addAll(b2BUnits);
            }
        }
        // If the list of new b2b units is empty, then the recommendation should be deleted rather than updated.
        if (newB2BUnits.isEmpty()) {
            LOG.warn("Smart recommendation for product {} and type {} cannot be updated because the new list of b2b units is empty. Therefore, it will be removed.", smartRecommendation.getProduct().getCode(), smartRecommendation.getType());
            return Optional.empty();
        }
        smartRecommendation.setB2bUnits(newB2BUnits);
        return Optional.of(smartRecommendation);
    }

    protected Optional<SmartRecommendationModel> createSmartRecommendation(final String productCode,
                                                                           final Set<String> b2bUnitUids,
                                                                           final SmartRecommendationType smartRecommendationType,
                                                                           final Map<String, B2BUnitModel> b2bUnitToMapCache,
                                                                           final Map<String, ProductModel> productToProductCache,
                                                                           final CatalogVersionModel catalogVersion) {
        // Get b2b units.
        final List<B2BUnitModel> b2BUnits = getB2BUnitsForUids(b2bUnitUids, b2bUnitToMapCache);
        if (b2BUnits.isEmpty()) {
            LOG.warn("No B2BUnitModels found for uids: {}. Skipping creating of a smart recommendation for product {} and type {}.", b2bUnitUids, productCode, smartRecommendationType);
            return Optional.empty();
        }
        // Get product.
        final Optional<ProductModel> product = getProductForCode(productCode, productToProductCache, catalogVersion);
        if (product.isEmpty()) {
            LOG.warn("No product found for code {}. Skipping creation of a smart recommendation with type {}.", product, smartRecommendationType);
            return Optional.empty();
        }
        // Create recommendation.
        final SmartRecommendationModel smartRecommendation = getModelService().create(SmartRecommendationModel.class);
        smartRecommendation.setB2bUnits(b2BUnits);
        smartRecommendation.setProduct(product.get());
        smartRecommendation.setType(smartRecommendationType);
        return Optional.of(smartRecommendation);
    }

    protected Optional<ProductModel> getProductForCode(final String productCode,
                                                       final Map<String, ProductModel> productCodeToProductCache,
                                                       final CatalogVersionModel catalogVersion) {
        final ProductModel product = getProductForCode(catalogVersion, productCode, productCodeToProductCache);
        if (product instanceof final SABMAlcoholVariantProductMaterialModel materialProduct) {
            return Optional.of(materialProduct.getBaseProduct());
        }
        return Optional.ofNullable(product);
    }



    protected List<B2BUnitModel> getB2BUnitsForUids(final Set<String> codes, final Map<String, B2BUnitModel> b2bUnitToMapCache) {
        final List<B2BUnitModel> b2bUnitModels = new ArrayList<>();
        codes.forEach((code) -> getB2bUnitForUid(code, b2bUnitToMapCache).ifPresent(b2bUnitModels::add));
        return b2bUnitModels;
    }

    protected void loadSmartRecommendationsFromCSV(final File csvFile,
                                                   final Map<String, Set<String>> productToB2BUnitM1,
                                                   final Map<String, Set<String>> productToB2BUnitM2,
                                                   final Map<String, Set<String>> productToB2BUnitM3)
            throws IOException {
        final CSVReader csvReader = new CSVReader(new FileReader(csvFile));
        csvReader.skip(1);
        for (final String[] currentCsvRow : csvReader) {
            // Extract b2b unit code.
            final String b2bUnitCode = currentCsvRow[0];
            if (StringUtils.isEmpty(b2bUnitCode)) {
                continue;
            }
            final String fixedB2bUnitCode = fixB2bUnitCode(b2bUnitCode);
            // Extract product codes.
            // M1.
            if (currentCsvRow.length > 1) {
                loadProductCodeIfAvailable(currentCsvRow[1], productToB2BUnitM1, fixedB2bUnitCode);
            }
            // M2.
            if (currentCsvRow.length > 2) {
                loadProductCodeIfAvailable(currentCsvRow[2], productToB2BUnitM2, fixedB2bUnitCode);
            }
            // M3.
            if (currentCsvRow.length > 3) {
                loadProductCodeIfAvailable(currentCsvRow[3], productToB2BUnitM3, fixedB2bUnitCode);
            }
        }
        csvReader.close();
    }

    protected void loadProductCodeIfAvailable(final String currentCsvCell,
                                              final Map<String, Set<String>> productToB2BUnitM,
                                              final String b2bUnitCode) {
        if (StringUtils.isEmpty(currentCsvCell)) {
            return;
        }
        final String productCode = StringUtils.leftPad(currentCsvCell, 18, '0');
        if (!productToB2BUnitM.containsKey(productCode)) {
            productToB2BUnitM.put(productCode, new HashSet<>());
        }
        productToB2BUnitM.get(productCode).add(b2bUnitCode);
    }

    protected boolean clearSmartRecommendationsFromCSV(@Nonnull final File input, final Map<String,B2BUnitModel> cacheMap){
        final Set<String> clearedB2bUnits = new HashSet<>();
        try(final Reader reader = Files.newBufferedReader(input.toPath());final CSVReader csvReader = new CSVReader(reader)){
            csvReader.skip(1);
				for (final String[] data : csvReader)
				{

                final String b2bUnitCode = data[0];

                if(StringUtils.isEmpty(b2bUnitCode)){
                    continue;
                }

                final String fixedB2bUnitCode = fixB2bUnitCode(b2bUnitCode);

                if(clearedB2bUnits.contains(fixedB2bUnitCode)){
                    continue;
                }

                clearedB2bUnits.add(fixedB2bUnitCode);

                final Optional<B2BUnitModel> b2bUnit = getB2bUnitForUid(fixedB2bUnitCode,cacheMap);
                b2bUnit.ifPresent((unit) -> {
                    if(CollectionUtils.isNotEmpty(unit.getSmartRecommendations())){
                        LOG.debug("Clearing Smart Recommendations for b2bUnit [{}]",fixedB2bUnitCode);
                        unit.setSmartRecommendations(Collections.emptyList());
                        getModelService().save(unit);
                    }
                });
            }
            return true;
			}
			catch (final IOException e)
			{
           LOG.error("IOException occured reading recommendation file.",e);
        }

        return false;
    }

    protected void saveRecommendations(final Collection<SmartRecommendationModel> smartRecommendations){
        LOG.info("Saving [{}] recommendations.",smartRecommendations.size());
        for (final SmartRecommendationModel smartRecommendation: smartRecommendations){
            getModelService().save(smartRecommendation);
        }
    }

    /**
     * Retrieves file from Azure storage containing recommendation groupings and saves it to Hybris B2BUnit
     */
    @Override
    public boolean retrieveAndSaveRecommendationGroup() throws NoSuchFileException {
        boolean result = true;

        final String containerReference = getConfigurationService().getConfiguration()
                .getString(SabmCoreConstants.RECOMMENDATION_CONTAINER_REFERENCE, "recommendationengine");
        final BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer(containerReference);
        if (null == container) {
            LOG.error("Container not found.");
            return false;
        }

        CSVReader csvReader = null;

        try {
            final String inputFileName = getConfigurationService().getConfiguration().getString(SabmCoreConstants.RECOMMENDATION_GROUP_FILE_REFERENCE, "Recommendations.csv");
            final BlobClient blob = sabmAzureStorageUtils.getInputFile(inputFileName, container);

            if (null == blob) {
                throw new NoSuchFileException("Input file not found.");
            }

            final File inputFile = File.createTempFile(inputFileName, null);
            blob.downloadToFile(inputFile.getPath(), true);

            final Reader reader = Files.newBufferedReader(inputFile.toPath());
            csvReader = new CSVReader(reader);
            csvReader.skip(1);
            String[] nextRecord;
            while (null != (nextRecord = csvReader.readNext())) {
                if (!saveRecommendationGroup(nextRecord) && result) {
                    result = false;
                }
            }

            if (compressAndArchiveFile(inputFile, inputFileName, container)) {
                LOG.debug("Successfully compressed and archived file");
                inputFile.delete();
                blob.deleteIfExists();
            }
        } catch (final BlobStorageException e) {
            LOG.error(String.format("Error returned from the service. Http code : %d and error code: %s", e.getStatusCode(), e.getErrorCode()));
            return false;
        } catch (final NoSuchFileException e) {
            throw new NoSuchFileException("Input file not found.");
        } catch (final IOException e) {
            LOG.error("Error writing to file.", e);
            return false;
        }finally {
            IOUtils.closeQuietly(csvReader);
        }

        return result;
    }

    @Override
    public Map<SmartRecommendationType, Optional<ProductModel>> calculateSmartRecommendations() {

        final B2BUnitModel b2BUnit = getB2bCommerceUnitService().getParentUnit();
        final Date sessionDeliveryDate = getSessionService().getOrLoadAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, Calendar.getInstance()::getTime);
        final PlantModel plant = b2BUnit.getPlant();
        final Collection<SmartRecommendationModel> smartRecommendations = b2BUnit.getSmartRecommendations();

        final Map<SmartRecommendationType, String> sessionSmartRecommendations = new HashMap<>(getSmartRecommendationFromSession());
        final Set<String> exclusions = retrieveProductExclusions(b2BUnit, sessionDeliveryDate);

        //this will set the invalid recommendations to null;
        final Map<SmartRecommendationType, Optional<ProductModel>> smartRecommendationsMapResult = verifySessionSmartRecommendations(smartRecommendations,sessionSmartRecommendations, exclusions, plant);

        final Set<Map.Entry<SmartRecommendationType,Optional<ProductModel>>>  emptyRecommendationModels = smartRecommendationsMapResult.entrySet().stream().filter((e)->!e.getValue().isPresent()).collect(Collectors.toSet());

        if (emptyRecommendationModels.isEmpty()) {
            return smartRecommendationsMapResult;
        }

        Optional<ProductModel> model1Recommendation = smartRecommendationsMapResult.get(SmartRecommendationType.MODEL1);
        if (!model1Recommendation.isPresent()){
            final Optional<ProductModel> bdeRecommendedProduct = getBDERecommendation(exclusions, b2BUnit, plant,sessionDeliveryDate);
            if(bdeRecommendedProduct.isPresent()){
                model1Recommendation = bdeRecommendedProduct;
                smartRecommendationsMapResult.put(SmartRecommendationType.MODEL1,bdeRecommendedProduct);
                exclusions.add(bdeRecommendedProduct.get().getCode()); // add in exclusion list
            }
        }

        final Optional<ProductModel> model2Recommendation = smartRecommendationsMapResult.get(SmartRecommendationType.MODEL2);
        final Optional<ProductModel> model3Recommendation = smartRecommendationsMapResult.get(SmartRecommendationType.MODEL3);


        Stream<SmartRecommendationModel> smartRecommendationModelStream = smartRecommendations.stream();

        if(model1Recommendation.isPresent()) {
            smartRecommendationModelStream = smartRecommendationModelStream.filter((s)->!SmartRecommendationType.MODEL1.equals(s.getType()));
        }

        if(model2Recommendation.isPresent()) {
            smartRecommendationModelStream = smartRecommendationModelStream.filter((s)->!SmartRecommendationType.MODEL2.equals(s.getType()));
        }

        if(model3Recommendation.isPresent()) {
            smartRecommendationModelStream = smartRecommendationModelStream.filter((s) ->!SmartRecommendationType.MODEL3.equals(s.getType()));
        }

        //the stream now only contains what grouping we need. so what now?

        final Map<SmartRecommendationType,List<SmartRecommendationModel>> smartRecommendationTypeListMap = smartRecommendationModelStream.collect(Collectors.groupingBy(SmartRecommendationModel::getType,Collectors.toCollection(ArrayList::new)));
       // Yeah, kinda sucks but this one works.
        if(!model1Recommendation.isPresent()){
            calculateRecommendationForType(SmartRecommendationType.MODEL1,smartRecommendationTypeListMap,exclusions,plant,smartRecommendationsMapResult);
        }
        if(!model2Recommendation.isPresent()){
            calculateRecommendationForType(SmartRecommendationType.MODEL2,smartRecommendationTypeListMap,exclusions,plant,smartRecommendationsMapResult);
        }

        if(!model3Recommendation.isPresent()){
            calculateRecommendationForType(SmartRecommendationType.MODEL3,smartRecommendationTypeListMap,exclusions,plant,smartRecommendationsMapResult);
        }

        updateSmartRecommendationSession(smartRecommendationsMapResult);

        return smartRecommendationsMapResult;
    }

    protected void updateSmartRecommendationSession(final Map<SmartRecommendationType, Optional<ProductModel>> result) {
        final Map<SmartRecommendationType, String> newSessionSmartRecommendation = new HashMap<>();
        result.entrySet().stream()
                .filter((e) -> e.getValue().isPresent())
                .forEach((e) -> {
                    newSessionSmartRecommendation.put(e.getKey(), e.getValue().get().getCode());
                });

        getSessionService().setAttribute(SESSION_SMART_RECOMMENDATIONS,newSessionSmartRecommendation);
    }

    /**
     * Verifies session if it's still valid
     * @param smartRecommendationTypeStringMap
     * @param exclusions
     * @param plant
     * @return
     */
    protected final Map<SmartRecommendationType,Optional<ProductModel>> verifySessionSmartRecommendations(final Collection<SmartRecommendationModel> smartRecommendations, final Map<SmartRecommendationType,String> smartRecommendationTypeStringMap, final Set<String> exclusions, final PlantModel plant){

        final Map<SmartRecommendationType,Optional<ProductModel>> smartRecommendationsMapResult = new HashMap<>();

		  for (final SmartRecommendationType smartRecommendationType : SmartRecommendationType.values())
		  {
            smartRecommendationsMapResult.put(smartRecommendationType,Optional.empty());
        }

        final Map<SmartRecommendationType,List<SmartRecommendationModel>> smartRecommendationTypeListMap = smartRecommendations.stream().collect(Collectors.groupingBy(SmartRecommendationModel::getType,Collectors.toCollection(ArrayList::new)));

		  for (final Map.Entry<SmartRecommendationType, String> entry : smartRecommendationTypeStringMap.entrySet())
		  {
            final String productCode = entry.getValue();
            if(productCode == null){
                continue;
            }

            //checks if the recommendation still exist. handles newly updated recommendation + b2b unit changes
            if(!existInSmartRecommendations(productCode,smartRecommendationTypeListMap.get(entry.getKey()))){
                continue;
            }

            //checks if the productCode is in the exclusions,carts,product page
            if(!isNotExcludedProductCodeForRecommendation(productCode,exclusions)){ // needs to change isOutOfStock
                continue;
            }

            //deflate productCode into ProductModel
            final Optional<ProductModel> optProduct = getOptionalProductForCode(productCode);
            if(!optProduct.isPresent()){
                continue;
            }

            // Yeah yeah I know, so many checks purchasable eh?
            if(BooleanUtils.isNotTrue(optProduct.get().getPurchasable())){
                continue;
            }

            final Optional<SABMAlcoholVariantProductMaterialModel> material = getLeadSkuForProduct(optProduct.get());

            //checks if material is not available and is out of stock then just skip...
            if(!material.isPresent() || isOutOfStock(material.get().getCode(),plant)){
                continue;
            }


            //it passed all the requirements. so it's still valid
            smartRecommendationsMapResult.put(entry.getKey(),optProduct);
            exclusions.add(optProduct.get().getCode());
        }

        return smartRecommendationsMapResult;
    }


    /**
     * Verifies if productCode exist in smartRecommendations
     * @param productCode
     * @param smartRecommendations
     * @return
     */
    protected boolean existInSmartRecommendations(final @Nonnull String productCode, final @Nonnull List<SmartRecommendationModel> smartRecommendations){
        if(CollectionUtils.isEmpty(smartRecommendations)){
            return false;
        }

        return smartRecommendations.stream().filter((s)->productCode.equals(s.getProduct().getCode())).findAny().isPresent();
    }

    protected Optional<SABMAlcoholVariantProductMaterialModel> getLeadSkuForProduct(ProductModel product){
        while(product instanceof SABMAlcoholVariantProductEANModel){
            final SABMAlcoholVariantProductEANModel productEAN = (SABMAlcoholVariantProductEANModel) product;
            final SABMAlcoholVariantProductMaterialModel leadSku = productEAN.getLeadSku();
            if(leadSku != null){
                return Optional.of(leadSku);
            }
            product = productEAN.getBaseProduct();
        }

        return Optional.empty();
    }

    /**
     * Retrieves the leadsku if available or the first variant if non
     * @param product
     * @return
     */
    protected Optional<SABMAlcoholVariantProductMaterialModel> getPrimaryMaterial(ProductModel product){

        final Optional<SABMAlcoholVariantProductMaterialModel> leadSku = getLeadSkuForProduct(product);
        if(leadSku.isPresent()){
            return leadSku;
        }

        SABMAlcoholVariantProductEANModel eanProduct = null;
        //just trying to simulate how they do stuffs to avoid different logics
        while (product instanceof VariantProductModel)
        {
            if (SABMAlcoholVariantProductEANModel.class.equals(product.getClass()))
            {
                eanProduct = (SABMAlcoholVariantProductEANModel) product;
                break;
            }

            product = ((VariantProductModel) product).getBaseProduct();
        }

        if(eanProduct == null){
            return Optional.empty();
        }

        final Collection<VariantProductModel> variants = eanProduct.getVariants();
        return variants.stream().filter(SABMAlcoholVariantProductMaterialModel.class::isInstance).map(SABMAlcoholVariantProductMaterialModel.class::cast).findFirst();
    }

    /**
     * Returns smart recommendations store from session or empty;
     * @return
     */
    protected Map<SmartRecommendationType,String> getSmartRecommendationFromSession(){
        return getSessionService().getOrLoadAttribute(SESSION_SMART_RECOMMENDATIONS,()->new HashMap<>());
    }

    protected Set<String> retrieveProductExclusions(final B2BUnitModel b2BUnit, final Date sessionDeliveryDate){
        final Set<String> exclusions = new HashSet<>();
        exclusions.addAll(sabmProductExclusionService.getAndSetSessionEanProductExclusion());
        exclusions.addAll(getCartProductsEan());

        return exclusions;
    }

    protected String getProductBaseCodeForProduct(@Nonnull final ProductModel product){
        if(product instanceof SABMAlcoholVariantProductMaterialModel && ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct() != null){
            return ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct().getCode();
        }

        return product.getCode();
    }

    /**
     * Helper method which tries to retrieve a valid product for recommendation based on @{@link B2BUnitModel} @{@link SABMRecommendationModel#getStatus()} RECOMMENDED
     * @param exclusions
     * @param b2BUnit
     * @param plant
     * @return
     */
    protected Optional<ProductModel> getBDERecommendation(final Set<String> exclusions, final B2BUnitModel b2BUnit, final PlantModel plant, final Date sessionDeliveryDate){
        final List<SABMRecommendationModel> sabmRecommendations = getRecommendationDao().getRecommendations(b2BUnit);

		  for (final SABMRecommendationModel sabmRecommendation : sabmRecommendations)
		  {

            Optional<ProductModel> product = Optional.empty();

            if(RecommendationType.PRODUCT.equals(sabmRecommendation.getRecommendationType())){
                product = getValidProductForRecommendation(sabmRecommendation,exclusions,plant);
            }
            else if(RecommendationType.DEAL.equals(sabmRecommendation.getRecommendationType()) && isValidDealRecommendation(sessionDeliveryDate,sabmRecommendation)){
                product = getValidProductForRecommendationFromDeal(sabmRecommendation.getDealProducts(),exclusions,plant);
            }

            if(product.isPresent()){
                return product;
            }

        }

        return Optional.empty();
    }

    /**
     * Helper method to verify if this deal is valid.
     * @param sessionDeliveryDate
     * @param sabmRecommendation
     * @return
     */
    protected boolean isValidDealRecommendation(final @Nonnull Date sessionDeliveryDate,@Nonnull final SABMRecommendationModel sabmRecommendation){
        final String dealCode = sabmRecommendation.getDealCode();
        if(StringUtil.isEmpty(dealCode)){
            return false;
        }

        final DealModel dealModel = getDealsService().getDeal(dealCode);
        if(dealModel == null){
            return false;
        }

        return getDealsService().isValidityPeriod(sessionDeliveryDate,dealModel,true) && getDealsService().isValidDeal(dealModel);
    }

    /**
     * Checks the the #productCode is not in the exclusion list
     * @param productCode
     * @param exclusions
     * @return
     */
    protected boolean isNotExcludedProductCodeForRecommendation(final String productCode, final Set<String> exclusions){
        return StringUtils.isNotEmpty(productCode) && !exclusions.contains(productCode);
    }

    /**
     * Tries to retrieve a product valid for recommendation based on @{@link SABMRecommendationModel#getProductCode()}
     * @param sabmRecommendation
     * @param exclusions
     * @param plant
     * @return
     */
    protected Optional<ProductModel> getValidProductForRecommendation(final SABMRecommendationModel sabmRecommendation, final Set<String> exclusions, final PlantModel plant){

        if (isNotExcludedProductCodeForRecommendation(sabmRecommendation.getProductCode(), exclusions)) {
            //converts product ean to lead sku for stock checking. damn, that stock check is based on material
				final Optional<ProductModel> optProductEan = getOptionalProductForCode(sabmRecommendation.getProductCode());
            final ProductModel productEan = optProductEan.orElse(null);

            if(!(productEan instanceof SABMAlcoholVariantProductEANModel)){
                return Optional.empty();
            }

            final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEAN = (SABMAlcoholVariantProductEANModel) productEan;

            if (sabmAlcoholVariantProductEAN.getLeadSku() != null && BooleanUtils.isTrue(sabmAlcoholVariantProductEAN.getPurchasable()) &&  !isOutOfStock(((SABMAlcoholVariantProductEANModel) productEan).getLeadSku().getCode(), plant)) {
                return optProductEan;
            }
        }

        return Optional.empty();
    }

    /**
     * Tries to get a valid product for recommendation based on @{@link SABMRecommendationModel#getDealProducts()}
     * @param sabmRecommendationDps
     * @param exclusions
     * @param plant
     * @return
     */
    protected Optional<ProductModel> getValidProductForRecommendationFromDeal(@Nonnull final List<SABMRecommendationDPModel> sabmRecommendationDps,@Nonnull final Set<String> exclusions,@Nonnull final PlantModel plant){

		 for (final SABMRecommendationDPModel sabmRecommendationDP : sabmRecommendationDps)
		 {

            final Optional<ProductModel> product = getOptionalProductForCode(sabmRecommendationDP.getProductCode());
            if(!product.isPresent()){
                continue;
            }

            final ProductModel baseProduct;
            if(product.get() instanceof SABMAlcoholVariantProductMaterialModel){
                baseProduct = ((SABMAlcoholVariantProductMaterialModel) product.get()).getBaseProduct();
                if(baseProduct == null){
                    continue;
                }
            }else{
                baseProduct = product.get();
            }

            if(BooleanUtils.isNotTrue(baseProduct.getPurchasable())){ // verify if purchasable, dumb..
                continue;
            }

            // skip if product is in the exclusion or out of stock
            if(!isNotExcludedProductCodeForRecommendation(baseProduct.getCode(),exclusions) || isOutOfStock(sabmRecommendationDP.getProductCode(),plant)){
                continue;
            }

            return Optional.of(baseProduct);
        }

        return Optional.empty();
    }


    /**
     * Just  a helper method to get product code, optional, so this won't throw an Exception but will return empty
     * @param code
     * @return
     */
    protected Optional<ProductModel> getOptionalProductForCode(@Nonnull final String code){
        try {
            return Optional.of(getProductService().getProductForCode(code));
			}
			catch (final Exception e)
			{
            LOG.warn(String.format("Unable to retrieve product for code [%s]",code),e);
        }

        return Optional.empty();
    }



    /**
     * Returns a recommendation type based on SmartRecommendationType. this thus the filter, e.g stock checks. not in exclusions/cart
     * @param smartRecommendationType
     * @param smartRecommendationsMap
     * @param exclusionsProductCode
     * @param plant
     * @param smartRecommendationResult
     */
    protected void calculateRecommendationForType(final SmartRecommendationType smartRecommendationType, final Map<SmartRecommendationType,List<SmartRecommendationModel>> smartRecommendationsMap, final Set<String> exclusionsProductCode,final PlantModel plant, final Map<SmartRecommendationType,Optional<ProductModel>> smartRecommendationResult){
        final List<SmartRecommendationModel> unfilteredRecommendations =  smartRecommendationsMap.get(smartRecommendationType);

        if(CollectionUtils.isEmpty(unfilteredRecommendations)){
            smartRecommendationResult.put(smartRecommendationType,Optional.empty());
            return;
        }

        Collections.shuffle(unfilteredRecommendations); //shuffle first, this will do the randomization.

        final Optional<SmartRecommendationModel> smartRecommendation  = filterSmartRecommendations(unfilteredRecommendations, exclusionsProductCode,plant);
        final ProductModel product = smartRecommendation.isPresent()?smartRecommendation.get().getProduct():null;
        smartRecommendationResult.put(smartRecommendationType,Optional.ofNullable(product));
        smartRecommendation.ifPresent((s)->exclusionsProductCode.add(product.getCode()));
    }

    /**
     * Return a subset of the smartRecommendations which suffice the criteria which has stock, and not in cart and not in exclusions
     * @param smartRecommendations
     * @param exclusionsProductCode
     * @param plant
     * @return
     */
    protected Optional<SmartRecommendationModel> filterSmartRecommendations(final List<SmartRecommendationModel> smartRecommendations, final Set<String> exclusionsProductCode, final PlantModel plant){
        Stream<SmartRecommendationModel> filteredSmartRecommendationStream = smartRecommendations.stream().filter((s)->!exclusionsProductCode.contains(s.getProduct().getCode()))
                .filter((s)->s.getProduct() instanceof SABMAlcoholVariantProductEANModel)
                .filter((s)->((SABMAlcoholVariantProductEANModel) s.getProduct()).getLeadSku() != null) // has leadsku?
                .filter((s)->BooleanUtils.isTrue(s.getProduct().getPurchasable())); // purchasable?


            filteredSmartRecommendationStream = filteredSmartRecommendationStream
                    .filter((s)->!isOutOfStock(((SABMAlcoholVariantProductEANModel) s.getProduct()).getLeadSku().getCode(),plant)); // not out of stock?


        return filteredSmartRecommendationStream.findFirst();
    }

    protected boolean isOutOfStock(final String productCode,final PlantModel plant){
        final CUBStockInformationModel cubStockInformation = getCubStockInformationService().getCUBStockInformationForProductAndPlant(productCode,plant);
        if(cubStockInformation == null){
            return false;
        }

        return CUBStockStatus.OUTOFSTOCK.equals(cubStockInformation.getStockStatus()) ||  CUBStockStatus.LOWSTOCK.equals(cubStockInformation.getStockStatus());
    }

    protected Set<String> getCartProductsEan(){
        if(!getCartService().hasSessionCart()){
            return Collections.emptySet();
        }
       return getCartService().getSessionCart().getEntries().stream()
                .map(AbstractOrderEntryModel::getProduct) // convert to product
                .filter(VariantProductModel.class::isInstance) // filter nonnull. just to be sure ? probably not required. but just to avoid the fault. :)
                .map((v)->((VariantProductModel) v).getBaseProduct())
                .filter(Objects::nonNull)
                .map(ProductModel::getCode)
                .collect(Collectors.toSet());
    }

    protected Optional<String> getBaseProductCode(@Nonnull final String productCode) {
        if (StringUtils.isEmpty(productCode)) {
            return Optional.empty();
        }

        try {
            final ProductModel material = getProductService().getProductForCode(productCode);
            if (material instanceof VariantProductModel) {
                final ProductModel base = ((VariantProductModel) material).getBaseProduct();
                if (base == null) {
                    return Optional.empty();
                }

                return Optional.ofNullable(base.getCode());
            }

        } catch (UnknownIdentifierException | AmbiguousIdentifierException ie) {
            LOG.debug("Unable to get EAN code for variant code [{}]", productCode);
        }

        return Optional.empty();
    }

	/*protected boolean isAPotentialSmartRecommmendation(final ProductModel product, final ){

	}*/

	private String fixB2bUnitCode(final String b2bUnitCode){
        return StringUtils.leftPad(b2bUnitCode, 10, '0');
    }

    protected boolean saveRecommendation(final CatalogVersionModel catalogVersion, final String[] recommendation, final Map<String,B2BUnitModel> b2bCache,final Map<String,ProductModel> productCache,final Map<String,SmartRecommendationModel> smartRecommendationCache) {

        final String rawB2bUnitCode = recommendation[0];
        if(StringUtils.isEmpty(rawB2bUnitCode)){
            LOG.warn("Row with empty B2bUnit found! Skipping..."); // can be enhanced with specifying which row.
            return false;
        }

        boolean saveResult = true;

        final String customerNo = fixB2bUnitCode(recommendation[0]);
        final Optional<B2BUnitModel> optB2bUnit = getB2bUnitForUid(customerNo,b2bCache);
        if (!optB2bUnit.isPresent()) {
            return false;
        }

        final B2BUnitModel b2BUnit = optB2bUnit.get();

        for (int i = 1; i < recommendation.length; i++) {
            if (StringUtils.isNotBlank(recommendation[i])) {
                final String productCode = StringUtils.leftPad(recommendation[i], 18, '0');
                try {

                    ProductModel product = getProductForCode(catalogVersion,productCode,productCache);
                    if(product instanceof SABMAlcoholVariantProductMaterialModel){
                        product = ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct();
                        if(product == null){
                            saveResult = false;
                            LOG.error("No Base Product: " + productCode);
                            continue;
                        }
                    }else if(product == null){
                        saveResult = false;
                        continue;
                    }

                    final SmartRecommendationType recommendationType = SmartRecommendationType.valueOf(SabmCoreConstants.SMART_RECOMMENDATION_TYPE + i);
                    final Set<B2BUnitModel> b2bUnits = new HashSet<>();

                    SmartRecommendationModel smartRecommendation;
                    try {
                        smartRecommendation = getSmartRecommendation(product,recommendationType,smartRecommendationCache);
							}
							catch (final ModelNotFoundException e)
							{
                        smartRecommendation = modelService.create(SmartRecommendationModel.class);
                        smartRecommendation.setProduct(product);
                        smartRecommendation.setType(recommendationType);
                        smartRecommendationCache.put(toSmartRecommendationCacheKey(product,recommendationType),smartRecommendation);
                    }
                    if (CollectionUtils.isNotEmpty(smartRecommendation.getB2bUnits())) {
                        b2bUnits.addAll(smartRecommendation.getB2bUnits());
                    }
                    if (!b2bUnits.contains(b2BUnit)) {
                        b2bUnits.add(b2BUnit);
                        smartRecommendation.setB2bUnits(b2bUnits);
                    }
					  }
					  catch (final UnknownIdentifierException e)
					  {
                    //LOG.error("No product record found for code: " + productCode);
                    saveResult = false;
                }
            }
        }
        return saveResult;
    }

    private String toSmartRecommendationCacheKey(final ProductModel product, final SmartRecommendationType smartRecommendationType){
        return product.getCode()+":"+smartRecommendationType.getCode();
    }

    protected Optional<B2BUnitModel> getB2bUnitForUid(final String uid,final Map<String,B2BUnitModel> cacheMap){

        if(cacheMap.containsKey(uid)){
            return Optional.ofNullable(cacheMap.get(uid));
        }

		  final B2BUnitModel b2BUnit = b2bUnitService.getUnitForUid(uid);

        cacheMap.put(uid,b2BUnit);

        return Optional.ofNullable(b2BUnit);
    }

    protected SmartRecommendationModel getSmartRecommendation(final ProductModel product, final SmartRecommendationType smartRecommendationType, final Map<String,SmartRecommendationModel> cacheMap){

        final String key = toSmartRecommendationCacheKey(product,smartRecommendationType);
        if(cacheMap.containsKey(key)){
            return cacheMap.get(key);
        }

        SmartRecommendationModel smartRecommendation = null;
        try {
            smartRecommendation = recommendationDao.getSmartRecommendation(product, smartRecommendationType);
        }finally {
            cacheMap.put(key,smartRecommendation);
        }

        return smartRecommendation;
    }

    protected ProductModel getProductForCode(final CatalogVersionModel catalogVersion,final String productCode, final Map<String,ProductModel> cacheMap){
        if(StringUtils.isEmpty(productCode)){
            return null;
        }

        if(cacheMap.containsKey(productCode)){
            return cacheMap.get(productCode);
        }

        ProductModel product = null;
        try {
            product = productService.getProductForCode(catalogVersion, productCode);
        }finally {
            if(product == null){
                LOG.error("Unable to get product [{}] for catalogVersion [{}:{}] : ",productCode,catalogVersion.getCatalog().getId(),catalogVersion.getVersion());
            }
            cacheMap.put(productCode,product);
        }

        return product;
    }


    protected boolean saveRecommendationGroup(final String[] recommendationGroup) {
        final String customerNo = StringUtils.leftPad(recommendationGroup[0], 10, '0');
        final B2BUnitModel b2BUnit = b2bUnitService.getUnitForUid(customerNo);
        if (null == b2BUnit) {
            return false;
        }

        if (recommendationGroup.length > 1 && StringUtils.isNotBlank(recommendationGroup[1])) {
            try {
                b2BUnit.setRecommendationGroup(RecommendationGroupType.valueOf(recommendationGroup[1]));
				 }
				 catch (final IllegalArgumentException e)
				 {
                LOG.error("Recommendation Group value not known: " + recommendationGroup[1] + ". Setting value as null.");
                b2BUnit.setRecommendationGroup(null);
            }
        } else {
            b2BUnit.setRecommendationGroup(null);
        }

        try {
            modelService.save(b2BUnit);
			}
			catch (final ModelSavingException e)
			{
            LOG.error("Error saving recommendation group for B2BUnit: " + b2BUnit.getUid());
            return false;
        }

        return true;
    }

    private boolean compressAndArchiveFile(final File inputFile, final String filename, final BlobContainerClient container) {
        final String zipFileName = filename + DateFormatUtils.format(new Date(),DATE_ARCHIVE_FORMAT) + ".zip";
        final File outputFile = new File(zipFileName);

        try(final FileOutputStream fos = new FileOutputStream(outputFile);final ZipOutputStream zipOut = new ZipOutputStream(fos);final FileInputStream fis = new FileInputStream(inputFile)) {

            final ZipEntry zipEntry = new ZipEntry(filename);

            zipOut.putNextEntry(zipEntry);

            IOUtils.copy(fis,zipOut);

            zipOut.closeEntry();

			}
			catch (final FileNotFoundException e)
			{
            LOG.error("Output file not found");
			}
			catch (final IOException e)
			{
            LOG.error("Error encountered while compressing the file.");
        }

        if (sabmAzureStorageUtils.archiveFile(container, outputFile)) {
            LOG.debug("Successfully archived file");
            outputFile.delete();
            return true;
        }

        return false;
    }

    private void selectProductFromList(final Collection<SmartRecommendationModel> recommendations, final SmartRecommendationType type,
			 final List<SmartRecommendationModel> smartRecommendations, final List<String> recommendedProducts)
	 {
        final Random rand = new Random();
		 final List<SmartRecommendationModel> validRecommendations = recommendations.stream()
                .filter(r -> r.getType().equals(type))
                .filter(r -> !recommendedProducts.contains(r.getProduct().getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(validRecommendations)) {
            smartRecommendations.add(validRecommendations.get(rand.nextInt(validRecommendations.size())));
        }
    }

    @Override
    public Map<String, String> getAllProductRecommendationsInCart() {
        if(!getCartService().hasSessionCart()){
            return Collections.emptyMap();
        }

        final Map<String,String> recommendationToTypeMap = new HashMap<>();

        final CartModel cart = getCartService().getSessionCart();

		  for (final AbstractOrderEntryModel orderEntry : cart.getEntries())
		  {
            if (null != orderEntry.getSmartRecommendationModel()) {
                final ProductModel product = orderEntry.getProduct();
                //just in case something bad happened, just add security checks. damn it.. I don't wanna..
                if (product == null) {
                    continue;
                }

                final String base = getProductBaseCodeForProduct(product);
                recommendationToTypeMap.put(base, orderEntry.getSmartRecommendationModel().getCode());
            }
        }

        return recommendationToTypeMap;
    }

    protected boolean shouldDisplayRecommendation(final SABMRecommendationModel recommendation, final List<ProductExclusionModel> productExclusions, final PlantModel plant, final Date deliveryDate){
        if(recommendation == null){
            return false;
        }

	    final RecommendationType recommendationType = recommendation.getRecommendationType();
        if(RecommendationType.PRODUCT.equals(recommendationType)){
            final Optional<ProductModel> optProduct = getOptionalProductForCode(recommendation.getProductCode());
            if(!optProduct.isPresent()) {
                return false;
            }

            final Optional<ProductExclusionModel> exclusion = productExclusions.stream().filter((e)->optProduct.get().getCode().equals(e.getProduct())).findAny();
            if(exclusion.isPresent()){
                return false;
            }

            final Optional<SABMAlcoholVariantProductMaterialModel> material = getPrimaryMaterial(optProduct.get());
            if(!material.isPresent()){
                LOG.warn("Shouldn't happen but it did happen anyway, so logging it. Primary Material not found for product{}",optProduct.get().getCode());
            }
            if(material.isPresent() && isOutOfStock(material.get().getCode(),plant)){
                return false;
            }
        }else{
            //this is a deal probably handling it the deal way.
            final String dealCode = recommendation.getDealCode();

            if(StringUtils.isEmpty(dealCode)){
                return false;
            }


            final DealModel deal = dealsService.getDeal(dealCode);
            if(deal == null){
                return false;
            }

            if(!dealsService.isValidityPeriod(deliveryDate,deal,true) || !dealsService.isValidDeal(deal)){
                return false;
            }

            if(!(userService.getCurrentUser() instanceof BDECustomerModel) && !shouldDisplayRecommendationWithDeal(deal,plant)){
                return false;
            }

        }

        return true;
    }

    /**
     * Returns true if the product is not purchasable and has valid ean setup
     * @param product
     * @return
     */
    private boolean verifySkuForRecommendation(final ProductModel product){
        SABMAlcoholVariantProductEANModel eanProduct = null;

        if (product instanceof SABMAlcoholVariantProductMaterialModel && ((SABMAlcoholVariantProductMaterialModel) product)
                .getBaseProduct() instanceof SABMAlcoholVariantProductEANModel) {
            eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
                    .getBaseProduct();
        }
        if (eanProduct == null || BooleanUtils.isNotTrue(eanProduct.getPurchasable())) {
            return false;
        }
        return true;
    }

    protected boolean preVerifyRecommendedProductDeals(final List<AbstractDealConditionModel> productDeals, final List<ProductModel> materialsHolder){

		 for (final AbstractDealConditionModel abstractDealCondition : productDeals)
		 {
			 final ProductDealConditionModel productDealCondition = (ProductDealConditionModel) abstractDealCondition;

            final ProductModel product = getSabmProductService().getProductForCodeSafe(productDealCondition.getProductCode());

            if (product == null) {
                return false;
            }

            if(!verifySkuForRecommendation(product)){
                return false;
            }

            materialsHolder.add(product);
        }

        return true;
    }

    private boolean preVerifyRecommendedComplexDeals(final List<AbstractDealConditionModel> complexDeals,final List<ProductModel> productMaterials,final PlantModel plant,final List<ProductModel> excluded, final boolean multiRange,final  boolean isAcross){
		 for (final AbstractDealConditionModel abstractDealCondition : complexDeals)
		 {
            final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) abstractDealCondition;

            final List<? extends ProductModel> materials = getSabmProductService().getProductByHierarchy(complexCondition.getLine(),
                    complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
                    complexCondition.getEmptyType(), complexCondition.getPresentation());

            if (CollectionUtils.isEmpty(materials)) {
                return false;
            }

            //Filtering the material that have the same EAN
            final Collection<ProductModel> filteredMaterial = CollectionUtils.subtract(materials, excluded);

            final Map<ProductModel,ProductModel> materialMap = getByEanToLeadSku(filteredMaterial);
            final Collection<ProductModel> primarySkus = materialMap.values();

            if (CollectionUtils.isEmpty(primarySkus))
            {
                return false;
            }


            List<ProductModel> productMaterialLocal = null;

            if(isAcross || (!multiRange && primarySkus.size() == 1)){
                productMaterialLocal = productMaterials;
            }else{
                productMaterialLocal = new ArrayList<>();
            }

            for (final ProductModel material : primarySkus)
            {
                if(!verifySkuForRecommendation(material)){
                    return false;
                }
                productMaterialLocal.add(material);
            }

            if(!(productMaterialLocal == productMaterials)){ // if identities are not the same means that it created a branch range, check right away if the stocks are cool
                if(productMaterialLocal.size() == 1 && isOutOfStock(productMaterialLocal.get(0).getCode(),plant)){
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean shouldDisplayRecommendationWithDeal(final DealModel deal, final PlantModel plant) {
        final List<AbstractDealConditionModel> dealConditions = deal.getConditionGroup().getDealConditions();

        final boolean multiRange = dealsService.isMultiRange(dealConditions);
        final boolean isAcross = dealsService.isAcross(deal.getConditionGroup());
        final Map<Class<? extends AbstractDealConditionModel>, List<AbstractDealConditionModel>> dealsByType = dealConditions.stream().filter((d)->BooleanUtils.isNotTrue(d.getExclude())).collect(Collectors.groupingBy((d) -> d instanceof ProductDealConditionModel ? ProductDealConditionModel.class : d instanceof ComplexDealConditionModel ? ComplexDealConditionModel.class : AbstractDealConditionModel.class));

		  final List<ProductModel> productMaterials = new ArrayList<>();
        if (!multiRange || isAcross) { // means we need to check base from products
			  final boolean preVerify = preVerifyRecommendedProductDeals(
					  dealsByType.getOrDefault(ProductDealConditionModel.class, Collections.emptyList()), productMaterials);
            if(!preVerify){
                return false;
            }
        }

        final List<AbstractDealConditionModel> complexDeals = dealsByType.getOrDefault(ComplexDealConditionModel.class, Collections.emptyList());

        if (!complexDeals.isEmpty()) {

            final List<ProductModel> excluded = getSabmProductService().findExcludedProduct(dealConditions);

				final boolean preVerify = preVerifyRecommendedComplexDeals(complexDeals, productMaterials, plant, excluded,
						multiRange, isAcross);
            if(!preVerify){
                return false;
            }
        }

        //verify
        if(productMaterials.size() == 1 && isOutOfStock(productMaterials.get(0).getCode(),plant)){
            return false;
        }

        return true;
    }

    /**
     * Helper method to group products by lead sku or first variant it lead sku is empty
     * @param products
     * @return
     */
	 private Map<ProductModel, ProductModel> getByEanToLeadSku(final Collection<ProductModel> products)
	 {
        final Map<ProductModel, ProductModel> mapMaterial = new HashMap<>();
        for (final ProductModel product : products) {
            if (product instanceof SABMAlcoholVariantProductMaterialModel) {
                final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
                        .getBaseProduct();
                /*
                 * If there's lead sku, then the approved MAT product must be the lead sku. Otherwise use the first
                 * MAT product of an EAN product as the priority
                 */
                if (eanProduct.getLeadSku() != null) {
                    if (eanProduct.getLeadSku() == product) {
                        mapMaterial.put(eanProduct, product);
                    }
                } else {
                    // It'll add the first MAT product only.
                    if (mapMaterial.get(eanProduct) == null) {
							  mapMaterial.put(eanProduct, product);
                    }
                }
            }
			}

        return mapMaterial;
    }

	 @Override
	 public SearchPageData<SABMRecommendationModel> getPageableRecommendations(final SearchPageData searchData, final String sortCode)
	 {
		 final UserModel currentUser = userService.getCurrentUser();
		 final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		 final SearchPageData<SABMRecommendationModel> recommendations = recommendationDao
				 .getPagedRecommendationsByB2BUnit(selectedB2BUnit, searchData, sortCode);
		 return recommendations;
	 }

	 @Override
	 public void saveRepRecommendedProducts(final String productCode, final Integer quantity)
	 {
		 final List<SABMRecommendationModel> productRecommendations = getRecommendationsByProductID(productCode);
		 SABMRecommendationModel productRecommendation;
		 final UserModel currentUser = userService.getCurrentUser();
		 final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		 if (CollectionUtils.isNotEmpty(productRecommendations))
		 {
			 productRecommendation = productRecommendations.get(0);
			 productRecommendation.setQty(productRecommendation.getQty() + quantity);
		 }
		 else
		 {
			 productRecommendation = modelService.create(SABMRecommendationModel.class);
			 productRecommendation.setB2bUnit(selectedB2BUnit);
			 productRecommendation.setProductCode(productCode);
			 final String firstName = ((B2BCustomerModel) currentUser).getFirstName();
			 productRecommendation.setRecommendedBy(firstName);

			 productRecommendation.setRecommendedDate(new Date());
			 productRecommendation.setRecommendationType(RecommendationType.PRODUCT);
			 productRecommendation.setStatus(RecommendationStatus.RECOMMENDED);
			 productRecommendation.setQty(quantity);
			 productRecommendation.setIsAsahiRecommendation(Boolean.TRUE);
			 try
			 {
				 final ProductModel product = productService.getProductForCode(productCode);
				 if (product instanceof ApbProductModel && null != ((ApbProductModel) product).getBrand())
				 {
					 productRecommendation.setApbProductbrandName(((ApbProductModel) product).getBrand().getName());
				 }
			 }
			 catch (final UnknownIdentifierException ex)
			 {
				 LOG.error("Could not fetch APBProduct for code :" + productCode);
			 }
		 }

		 modelService.save(productRecommendation);

	 }

	 @Override
	 public void updateProductRecommendation(final String productCode, final Integer quantity)
	 {
		 final List<SABMRecommendationModel> recommendations = this.getRecommendationsByProductID(productCode);
		 if (CollectionUtils.isNotEmpty(recommendations))
		 {
			 final SABMRecommendationModel productRecommendation = recommendations.get(0);
			 productRecommendation.setQty(quantity);
			 modelService.save(productRecommendation);
		 }
	 }

	 @Override
	 public void deleteRecommendationByProductId(final String productCode)
	 {
		 final List<SABMRecommendationModel> recommendations = this.getRecommendationsByProductID(productCode);
		 if (CollectionUtils.isNotEmpty(recommendations))
		 {
			 final SABMRecommendationModel recommendation = recommendations.get(0);
			 modelService.remove(recommendation);
		 }
	 }

	 @Override
	 public void deleteAllRecommendations()
	 {
		 final UserModel currentUser = userService.getCurrentUser();
		 final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		 final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendations(selectedB2BUnit);
		 modelService.removeAll(recommendations);

	 }


    protected SabmAzureStorageUtils getSabmAzureStorageUtils() {
        return sabmAzureStorageUtils;
    }

	 public Map<SmartRecommendationType, ProductData> getSgaProductRecommendations()
	 {
		 final Map<SmartRecommendationType, ProductData> sgaProductRecommendations = new HashMap<SmartRecommendationType, ProductData>();
		 final UserModel currentUser = userService.getCurrentUser();
		 final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		 final List<SABMRecommendationModel> recommendations = recommendationDao.getRecommendations(selectedB2BUnit);
		 ProductData productData = null;
		 if(CollectionUtils.isNotEmpty(recommendations)) {
			 final List<SABMRecommendationModel> validRecommendations =  recommendations.stream().filter(recommendation-> isValidProductForAsahiRecommendation(recommendation.getProductCode())).collect(Collectors.toList());
			 if(CollectionUtils.isNotEmpty(validRecommendations)) {
				 Collections.shuffle(validRecommendations);
				 final SABMRecommendationModel selectedRecommendationModel = validRecommendations.get(0);
				 productData = productFacade.getProductForCodeAndOptions(selectedRecommendationModel.getProductCode(), ASAHI_RECOMMENDATIONS_PRODUCT_OPTIONS);
				 productData.setRecommendedQuantity(selectedRecommendationModel.getQty());

			 } else {
				 productData = getDefaultRepRecommendedProduct();
			 }
		 } else {
			 productData = getDefaultRepRecommendedProduct();
		 }
		 if(null != productData) {
			 sgaProductRecommendations.put(SmartRecommendationType.MODEL3, productData);
		 }
		 //Set Model1 Model2 recommendations for ALB
		 addSectionOneSectionTwoRecommendations(sgaProductRecommendations);

		 return sgaProductRecommendations;
	 }

	 protected ProductData getDefaultRepRecommendedProduct() {
			final BaseStoreModel baseStoreModel= baseStoreService.getCurrentBaseStore();
			if( null != baseStoreModel && CollectionUtils.isNotEmpty(baseStoreModel.getRepRecommendedDefaultProductList())) {
				final List<ApbProductModel> validProductList = baseStoreModel.getRepRecommendedDefaultProductList().stream().filter(product-> isValidProductForAsahiRecommendation(product.getCode())).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(validProductList)) {
					Collections.shuffle(validProductList);
					return productFacade.getProductForCodeAndOptions(validProductList.get(0).getCode(), ASAHI_RECOMMENDATIONS_PRODUCT_OPTIONS);
				}
			}
			return null;
	 }

	 /**
	  * Tries to retrieve a product valid for recommendation based on @{@link SABMRecommendationModel#getProductCode()}
	  *
	  * @param productCode
	  * @return
	  */
	 protected boolean isValidProductForAsahiRecommendation(final String productCode)
	 {
		 final Optional<ProductModel> optProduct = getOptionalProductForCode(productCode);
		 final ProductModel product = optProduct.orElse(null);
		 if (!(product instanceof ApbProductModel))
		 {
			 return false;
		 }
		 final ApbProductModel apbProductModel = (ApbProductModel) product;

		 if (null != apbProductModel && (this.inclusionExclusionProductStrategy.isProductIncluded(apbProductModel.getCode())
				 && apbProductModel.isActive() && apbProductModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED))
		  && !isOutOfStock(apbProductModel) )
		 {
			 return true;
		 }
		 return false;
	 }

    /**
	 * @param apbProductModel
	 * @return
	 */
	private boolean isOutOfStock(final ApbProductModel apbProductModel)
	{
		final StockLevelStatus status = defaultSabmCommerceStockService.getStockLevelForSGA(apbProductModel);
		return StockLevelStatus.OUTOFSTOCK.equals(status);
	}

	protected void addSectionOneSectionTwoRecommendations(final Map<SmartRecommendationType, ProductData> sgaProductRecommendations)
	{
		final UserModel currentUser=userService.getCurrentUser();
		ProductData sectionOneProductData = null;
		ProductData sectionTwoProductData = null;
		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);
		Collection<String> catalogIds = null;

		if(selectedB2BUnit instanceof AsahiB2BUnitModel) {
			catalogIds = ((AsahiB2BUnitModel) selectedB2BUnit).getCatalogHierarchy();
		}

		final List<AsahiCatalogProductMappingModel> productMappingList = recommendationDao.getProductMappingBasedOnCatalogId((List<String>) catalogIds);

		final List<ApbProductModel> sectionOneProducts = productMappingList.stream().flatMap(mappingRow -> mappingRow.getSectionOneProducts().stream()
				.filter(product->isValidProductForAsahiRecommendation(product.getCode())))
				.collect(Collectors.toList());

		final List<ApbProductModel> sectionTwoProducts = productMappingList.stream().flatMap(mappingRow -> mappingRow.getSectionTwoProducts().stream()
				.filter(product->isValidProductForAsahiRecommendation(product.getCode())))
				.collect(Collectors.toList());

		if(CollectionUtils.isNotEmpty(sectionOneProducts)) {
			Collections.shuffle(sectionOneProducts);
			sectionOneProductData = productFacade.getProductForCodeAndOptions(sectionOneProducts.get(0).getCode(), ASAHI_RECOMMENDATIONS_PRODUCT_OPTIONS);
			sgaProductRecommendations.put(SmartRecommendationType.MODEL1, sectionOneProductData);
		}

		if(CollectionUtils.isNotEmpty(sectionTwoProducts)) {
			Collections.shuffle(sectionTwoProducts);
			sectionTwoProductData = productFacade.getProductForCodeAndOptions(sectionTwoProducts.get(0).getCode(), ASAHI_RECOMMENDATIONS_PRODUCT_OPTIONS);
			sgaProductRecommendations.put(SmartRecommendationType.MODEL2, sectionTwoProductData);
		}
	}

	public void setSabmAzureStorageUtils(final SabmAzureStorageUtils sabmAzureStorageUtils)
	 {
        this.sabmAzureStorageUtils = sabmAzureStorageUtils;
    }

    protected SabmProductService getSabmProductService() {
        return sabmProductService;
    }

    public void setSabmProductService(final SabmProductService sabmProductService)
	 {
        this.sabmProductService = sabmProductService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
	 {
        this.configurationService = configurationService;
    }

    protected SabmB2BUnitService getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	 {
        this.b2bUnitService = b2bUnitService;
    }

    protected ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService)
	 {
        this.productService = productService;
    }

    protected SabmRecommendationDao getRecommendationDao() {
        return recommendationDao;
    }

    public void setRecommendationDao(final SabmRecommendationDao recommendationDao)
	 {
        this.recommendationDao = recommendationDao;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
	 {
        this.modelService = modelService;
    }

    protected UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService)
	 {
        this.userService = userService;
    }

    protected SABMProductExclusionService getSabmProductExclusionService() {
        return sabmProductExclusionService;
    }

    public void setSabmProductExclusionService(final SABMProductExclusionService sabmProductExclusionService)
	 {
        this.sabmProductExclusionService = sabmProductExclusionService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService)
	 {
        this.cartService = cartService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService)
	 {
        this.sessionService = sessionService;
    }

    protected CUBStockInformationService getCubStockInformationService() {
        return cubStockInformationService;
    }

    public void setCubStockInformationService(final CUBStockInformationService cubStockInformationService)
	 {
        this.cubStockInformationService = cubStockInformationService;
    }

    protected SabmConfigurationService getSabmConfigurationService() {
        return sabmConfigurationService;
    }

    public void setSabmConfigurationService(final SabmConfigurationService sabmConfigurationService)
	 {
        this.sabmConfigurationService = sabmConfigurationService;
    }

    protected B2BCommerceUnitService getB2bCommerceUnitService() {
        return b2bCommerceUnitService;
    }

    public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	 {
        this.b2bCommerceUnitService = b2bCommerceUnitService;
    }

    protected DealsService getDealsService() {
        return dealsService;
    }

    public void setDealsService(final DealsService dealsService)
	 {
        this.dealsService = dealsService;
    }
}
