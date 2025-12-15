package com.sabmiller.integration.service.impl;

import com.sabmiller.integration.dao.ErrorEventDao;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.model.ErrorEventModel;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.integration.restclient.commons.SabmRestIoException;
import com.sabmiller.integration.service.ErrorEventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

public class ErrorEventServiceImpl implements ErrorEventService {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorEventServiceImpl.class.getName());

    private ModelService modelService;
    private KeyGenerator keyGenerator;
    private ErrorEventDao errorEventDao;


    @Override
    public String createErrorEntry(final Throwable cause, final String integration,
                                   final WebServiceLogModel webServiceLogModel,
                                   final ErrorEventType errorEventType,
                                   final String additionalDetails) {

        try {
            if (cause != null) {
                final ErrorEventModel eventModel = modelService.create(ErrorEventModel._TYPECODE);

                eventModel.setCode(keyGenerator.generate().toString());
                eventModel.setErrorType(errorEventType);
                eventModel.setRequestData(webServiceLogModel != null ? webServiceLogModel : findBurriedLogError(cause));
                eventModel.setAdditionalDetails(additionalDetails);
                eventModel.setRootCause(ExceptionUtils.getStackTrace(cause));

                if (eventModel.getErrorType() == null) {
                    eventModel.setErrorType(ErrorEventType.NOTSPECIFIED);
                }

                modelService.save(eventModel);
                return eventModel.getCode();
            } else {
                LOG.error("Unable to log error with no exception");
            }
        }catch (IllegalArgumentException | ModelSavingException e){
            LOG.error("Error saving error event ", e);
        }
        return null;
    }

    protected WebServiceLogModel findBurriedLogError(final Throwable e){
        if(e.getClass() == SabmRestIoException.class){
            final WebServiceLogModel log = ((SabmRestIoException)e).getErrorLog();
            modelService.refresh(log);
            return log;
        }

        if(e.getCause() != null){
            return findBurriedLogError(e.getCause());
        }
        return null;
    }

    @Override
    public void cleanupOldEntries() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -Config.getInt("sabm.errorevent.cleanup.days", 30));

        final List<ErrorEventModel> errors = errorEventDao.findByDate(cal.getTime());
        LOG.info("Deleting {} error entries", errors.size());
        if(CollectionUtils.isNotEmpty(errors)){
            modelService.removeAll(errors);
        }
    }


    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public void setErrorEventDao(final ErrorEventDao errorEventDao) {
        this.errorEventDao = errorEventDao;
    }
}
