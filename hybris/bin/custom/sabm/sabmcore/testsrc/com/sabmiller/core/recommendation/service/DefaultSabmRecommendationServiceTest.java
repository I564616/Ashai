package com.sabmiller.core.recommendation.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.azure.storage.blob.BlobContainerClient;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.RecommendationGroupType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.SmartRecommendationModel;
import com.sabmiller.core.recommendation.dao.SabmRecommendationDao;
import com.sabmiller.core.util.SabmAzureStorageUtils;

@UnitTest
public class DefaultSabmRecommendationServiceTest {

    @Mock
    private SabmB2BUnitService b2bUnitService;

    @Mock
    private SabmRecommendationDao recommendationDao;

    @Mock
    private ModelService modelService;

    @Mock
    private ProductService productService;

    @Mock
    private SabmAzureStorageUtils sabmAzureStorageUtils;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    private final DefaultSabmRecommendationService recommendationService = new DefaultSabmRecommendationService();

    private final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
    private final CatalogModel catalogModel = new CatalogModel();

    private final String[] record = {"851243", "92305", "87591", "88120"};
    private final String[] recordGroupNull = {"851243"};
    private final String[] recordGroupNotNull = {"851243", "A"};
    private final B2BUnitModel b2bUnit = new B2BUnitModel();

    private final ProductModel product1 = new ProductModel();
    private final ProductModel product2 = new ProductModel();
    private final ProductModel product3 = new ProductModel();

    private final SmartRecommendationModel recommendationModel1 = new SmartRecommendationModel();
    private final SmartRecommendationModel recommendationModel2 = new SmartRecommendationModel();
    private final SmartRecommendationModel recommendationModel3 = new SmartRecommendationModel();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        recommendationService.setSabmAzureStorageUtils(sabmAzureStorageUtils);

        given(configurationService.getConfiguration()).willReturn(configuration);
        given(configuration.getString(SabmCoreConstants.RECOMMENDATION_CONTAINER_REFERENCE, "recommendationengine")).willReturn("recommendationengine");
        given(configuration.getString(SabmCoreConstants.RECOMMENDATION_FILE_REFERENCE, "Recommendations.csv")).willReturn("Recommendations.csv");
        recommendationService.setConfigurationService(configurationService);
        recommendationService.setB2bUnitService(b2bUnitService);
        recommendationService.setProductService(productService);
        recommendationService.setRecommendationDao(recommendationDao);
        recommendationService.setModelService(modelService);

        b2bUnit.setUid("0000851243");
        product1.setCode("000000000000092305");
        product2.setCode("000000000000087591");
        product3.setCode("000000000000088120");

        final List<B2BUnitModel> b2BUnitModels = new ArrayList<>();
        b2BUnitModels.add(b2bUnit);
        recommendationModel1.setB2bUnits(b2BUnitModels);
        recommendationModel1.setProduct(product1);
        recommendationModel1.setType(SmartRecommendationType.MODEL1);
        recommendationModel2.setProduct(product2);
        recommendationModel2.setType(SmartRecommendationType.MODEL2);
        recommendationModel3.setProduct(product3);
        recommendationModel3.setType(SmartRecommendationType.MODEL3);
        catalogVersionModel.setCatalog(catalogModel);
        catalogVersionModel.setVersion("Online");
        catalogModel.setId("dummy");
    }

    @Test
    public void retrieveAndSaveRecommendationsNoContainer() throws NoSuchFileException {
        Mockito.when(sabmAzureStorageUtils.getAzureBlobContainer("recommendationengine")).thenReturn(null);
        Assert.assertFalse(recommendationService.retrieveAndSaveRecommendations(catalogVersionModel));
    }

    @Test(expected = NoSuchFileException.class)
    public void retrieveAndSaveRecommendationsNoInputFile() throws NoSuchFileException {
        final BlobContainerClient mockContainer = mock(BlobContainerClient.class);
        Mockito.when(sabmAzureStorageUtils.getAzureBlobContainer("recommendationengine")).thenReturn(mockContainer);
        Mockito.when(sabmAzureStorageUtils.getInputFile("Recommendations.csv", mockContainer)).thenReturn(null);
        recommendationService.retrieveAndSaveRecommendations(catalogVersionModel);
    }

    @Test
    public void saveRecommendationB2BUnitNotExisting() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(null);
        Assert.assertFalse(recommendationService.saveRecommendation(catalogVersionModel, record, new HashMap<>(),new HashMap<>(),new HashMap<>()));
    }

    @Test
    public void saveRecommendationProductNotExisting() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(b2bUnit);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000092305")).thenThrow(UnknownIdentifierException.class);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000087591")).thenReturn(product2);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000088120")).thenReturn(product3);

        Mockito.when(recommendationDao.getSmartRecommendation(product2, SmartRecommendationType.MODEL2)).thenThrow(ModelNotFoundException.class);
        Mockito.when(recommendationDao.getSmartRecommendation(product3, SmartRecommendationType.MODEL3)).thenReturn(recommendationModel3);

        Mockito.when(modelService.create(SmartRecommendationModel.class)).thenReturn(recommendationModel2);

        Assert.assertFalse(recommendationService.saveRecommendation(catalogVersionModel, record, new HashMap<>(),new HashMap<>(),new HashMap<>()));
        //Mockito.verify(modelService, times(1)).save(b2bUnit);
        Mockito.verify(modelService, times(1)).create(SmartRecommendationModel.class);
//        Mockito.verify(modelService, times(1)).save(recommendationModel2);
//        Mockito.verify(modelService, times(1)).save(recommendationModel3);
    }

    @Test
    public void saveRecommendationB2BAndAllProductsExisting() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(b2bUnit);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000092305")).thenReturn(product1);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000087591")).thenReturn(product2);
        Mockito.when(productService.getProductForCode(catalogVersionModel, "000000000000088120")).thenReturn(product3);

        Mockito.when(recommendationDao.getSmartRecommendation(product1, SmartRecommendationType.MODEL1)).thenReturn(recommendationModel1);
        Mockito.when(recommendationDao.getSmartRecommendation(product2, SmartRecommendationType.MODEL2)).thenReturn(recommendationModel2);
        Mockito.when(recommendationDao.getSmartRecommendation(product3, SmartRecommendationType.MODEL3)).thenReturn(recommendationModel3);

        Assert.assertTrue(recommendationService.saveRecommendation(catalogVersionModel, record, new HashMap<>(),new HashMap<>(), new HashMap<>()));
        Mockito.verify(modelService, times(0)).save(b2bUnit);
        Mockito.verify(modelService, times(0)).save(recommendationModel1);
       // Mockito.verify(modelService, times(1)).save(recommendationModel2);
       // Mockito.verify(modelService, times(1)).save(recommendationModel3);
    }

    @Test
    public void saveRecommendationGroupB2BUnitNotExisting() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(null);
        Assert.assertFalse(recommendationService.saveRecommendationGroup(recordGroupNotNull));
    }

    @Test
    public void saveRecommendationGroupNoGroup() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(b2bUnit);
        Assert.assertTrue(recommendationService.saveRecommendationGroup(recordGroupNull));
        Mockito.verify(modelService, times(1)).save(b2bUnit);
        Assert.assertEquals(b2bUnit.getRecommendationGroup(), null);
    }

    @Test
    public void saveRecommendationGroupWithGroup() {
        Mockito.when(b2bUnitService.getUnitForUid("0000851243")).thenReturn(b2bUnit);
        Assert.assertTrue(recommendationService.saveRecommendationGroup(recordGroupNotNull));
        Mockito.verify(modelService, times(1)).save(b2bUnit);
        Assert.assertEquals(b2bUnit.getRecommendationGroup(), RecommendationGroupType.A);
    }

}
