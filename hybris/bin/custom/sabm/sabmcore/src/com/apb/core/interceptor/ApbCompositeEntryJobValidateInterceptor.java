package com.apb.core.interceptor;

import de.hybris.platform.cronjob.model.CompositeEntryModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


public class ApbCompositeEntryJobValidateInterceptor implements ValidateInterceptor<CompositeEntryModel> {

    @Override
    public void onValidate(CompositeEntryModel model, InterceptorContext ctx) throws InterceptorException {
    	//removing the restriction for compositeEntry as it's comparing the Jalo TriggerableJob interface with the servicelayer JobModel class which will never be
    }
}