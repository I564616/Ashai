package com.sabmiller.core.jobs;

import com.sabmiller.core.model.SmartRecommendationsCronJobModel;
import com.sabmiller.core.product.SabmProductService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductHierarchyExportJobPerformable extends AbstractJobPerformable<SmartRecommendationsCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RetrieveRecommendationJobPerformable.class);

    private SabmCronJobStatus sabmCronJobStatus;
    private ProductService productService;

    @Override
    public PerformResult perform(SmartRecommendationsCronJobModel smartRecommendationsCronJobModel) {
        ((SabmProductService) productService).exportProductHierarchy(smartRecommendationsCronJobModel.getCatalogVersion());
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public SabmCronJobStatus getSabmCronJobStatus() {
        return sabmCronJobStatus;
    }

    public void setSabmCronJobStatus(SabmCronJobStatus sabmCronJobStatus) {
        this.sabmCronJobStatus = sabmCronJobStatus;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
