package com.sabmiller.core.recommendation.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.sabmiller.core.model.SmartRecommendationModel;
import com.sabmiller.core.recommendation.dao.SabmRecommendationDao;
import com.sabmiller.core.util.SabmAzureStorageUtils;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static de.hybris.platform.util.CSVConstants.HYBRIS_ENCODING;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@IntegrationTest
public class DefaultSabmRecommendationService2Test extends ServicelayerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmRecommendationService2Test.class);

    private static final String RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL = "/test/smart-recommendations/smart-recommendations-typical.csv.zip";
    private static final String RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE = "/test/smart-recommendations/smart-recommendations-large.csv.zip";
    private static final String ZIPPED_FILE_RECOMMENDATIONS_CSV = "Recommendations.csv";

    private AutoCloseable closeable;

    @Mock
    private SabmAzureStorageUtils sabmAzureStorageUtils;

    @Resource
    private FlexibleSearchService flexibleSearchService;
    @Resource
    private ModelService modelService;
    @Resource
    private DefaultSabmRecommendationService recommendationService;
    @Resource(name = "recommendationDao")
    private SabmRecommendationDao sabmRecommendationDao;
    @Resource
    private UserService userService;

    @Before
    public void setUp() throws ImpExException {
        closeable = MockitoAnnotations.openMocks(this);
        setCurrentUser();
        cleanUp();
        importData("/test/smart-recommendations/b2b-units.impex", HYBRIS_ENCODING);
        importData("/test/smart-recommendations/products.impex", HYBRIS_ENCODING);
        getRecommendationService().setSabmAzureStorageUtils(sabmAzureStorageUtils);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void test_import_smartRecommendations_old_logic_typical() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());

    }

    @Test
    public void test_import_smartRecommendations_old_logic_large() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_old_logic_typical_large() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        // First run - typical file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
        // Second run - large file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_old_logic_large_typical() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        // First run - large file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
        // Second run - typical file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        getRecommendationService().retrieveAndSaveRecommendations(catalogVersion);
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_new_logic_typical() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_new_logic_large() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_new_logic_typical_large() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        // First run - typical file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
        // Second run - large file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    @Test
    public void test_import_smartRecommendations_new_logic_large_typical() throws IOException, StorageException, URISyntaxException {
        setCurrentUser();
        final CatalogVersionModel catalogVersion = getSabmProductCatalogOnlineVersion();
        // First run - large file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_LARGE);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
        // Second run - typical file.
        mockAzureBlob(RESOURCE_PATH_SMART_RECOMMENDATIONS_TYPICAL);
        Assert.assertTrue(getRecommendationService().retrieveAndSaveRecommendationsV2(catalogVersion));
        LOG.info("Smart Recommendation records after import: {}", getSabmRecommendationDao().getAllSmartRecommendations().size());
    }

    private void mockAzureBlob(final String resourcePathSmartRecommendations) throws URISyntaxException, StorageException, IOException {
        final BlobContainerClient mockContainer = mock(BlobContainerClient.class);
        final BlobClient mockBlob = mock(BlobClient.class);
        Mockito.when(getSabmAzureStorageUtils().getAzureBlobContainer("recommendationengine")).thenReturn(mockContainer);
        Mockito.when(mockContainer.getBlobClient(anyString())).thenReturn(mockBlob);
        Mockito.when(getSabmAzureStorageUtils().getInputFile("Recommendations.csv", mockContainer)).thenReturn(mockBlob);
        doAnswer(invocationOnMock -> {
            copyFileFromResourceToPath(resourcePathSmartRecommendations, invocationOnMock.getArgument(0));
            return null;
        }).when(mockBlob).downloadToFile(anyString());
        doNothing().when(mockBlob).uploadFromFile(anyString());
    }

    private void setCurrentUser() {
        final UserModel adminUser = getUserService().getUserForUID("admin");
        getUserService().setCurrentUser(adminUser);
    }

    private CatalogVersionModel getSabmProductCatalogOnlineVersion() {
        final String QUERY = String.format("SELECT {cv.%s} FROM {%s AS cv JOIN %s AS pc ON {cv.%s} = {pc.%s} } WHERE {pc.%s} = 'sabmProductCatalog' AND {cv.%s} = 'Online'",
                CatalogVersionModel.PK, CatalogVersionModel._TYPECODE, CatalogModel._TYPECODE, CatalogVersionModel.CATALOG, CatalogModel.PK, CatalogModel.ID, CatalogVersionModel.VERSION);
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(QUERY);
        return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
    }

    private void copyFileFromResourceToPath(final String resourcePath, final String targetPath) {
        try (InputStream zipInputStream = getClass().getResourceAsStream(resourcePath)) {
            assert zipInputStream != null;
            unzipFile(zipInputStream, ZIPPED_FILE_RECOMMENDATIONS_CSV).ifPresent(unzippedFile -> {
                try {
                    Files.copy(unzippedFile, Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        unzippedFile.close();
                    } catch (IOException e) {
                        Assert.fail(e.getMessage());
                    }
                }
            });
        } catch (final IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    private Optional<InputStream> unzipFile(final InputStream zipInputStream, final String fileName) throws IOException {
        final ZipInputStream zis = new ZipInputStream(zipInputStream);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(fileName)) {
                return Optional.of(zis);
            }
        }
        zis.close();
        return Optional.empty();
    }

    private void cleanUp() {
        cleanUpSmartRecommendations();
    }

    private void cleanUpSmartRecommendations() {
        final String QUERY = String.format("SELECT {%s} FROM {%s}", SmartRecommendationModel.PK, SmartRecommendationModel._TYPECODE);
        final SearchResult<SmartRecommendationModel> searchResult = getFlexibleSearchService().search(QUERY);
        if (searchResult.getCount() < 1) return;
        getModelService().removeAll(searchResult.getResult());
    }

    private SabmAzureStorageUtils getSabmAzureStorageUtils() {
        return sabmAzureStorageUtils;
    }

    private FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    private ModelService getModelService() {
        return modelService;
    }

    private DefaultSabmRecommendationService getRecommendationService() {
        return recommendationService;
    }

    private SabmRecommendationDao getSabmRecommendationDao() {
        return sabmRecommendationDao;
    }

    private UserService getUserService() {
        return userService;
    }
}
