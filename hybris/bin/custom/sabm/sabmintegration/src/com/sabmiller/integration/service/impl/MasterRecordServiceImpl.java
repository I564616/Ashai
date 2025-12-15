package com.sabmiller.integration.service.impl;

import com.sabmiller.integration.service.MasterRecordService;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.model.MasterImportModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class MasterRecordServiceImpl implements MasterRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(MasterRecordServiceImpl.class.getName());

    private ModelService modelService;

    @Override
    public PerformResult processRecords(final List<MasterImportModel> masterRecords, final String serviceUrl){
        boolean hadErrors = false;
        if (CollectionUtils.isNotEmpty(masterRecords))
        {
            final RestTemplate restTemplate = new RestTemplate();

            for (final MasterImportModel model : masterRecords)
            {
                LOG.info("Retrying the Import for payload : " + model.getPayloadId() + ". Invoking service at " + serviceUrl);
                final String payload = model.getPayload();

                final HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", Config.getString("services.authorization", null));

                final HttpEntity<String> request = new HttpEntity<>(payload, headers);
                try {
                    restTemplate.exchange(serviceUrl, HttpMethod.POST, request, String.class);
                    model.setStatus(DataImportStatusEnum.SUCCESS);
                    modelService.save(model);

                }catch (final RestClientException e){
                    LOG.error("Error importing record with ref. ID [{}]", model.getReferenceId(), e);
                    hadErrors = true;
                }
            }
        }

        return new PerformResult(hadErrors ? CronJobResult.ERROR : CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
