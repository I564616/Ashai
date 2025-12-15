package com.apb.core.listener;

import com.apb.core.interceptor.AsahiDirectDebitValidatorInterceptor;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.util.Collection;

public class AfterDirectDebitSaveEventListener implements AfterSaveListener {

    @Autowired
    private ModelService modelService;

    @Resource
    private AsahiDirectDebitValidatorInterceptor asahiDirectDebitValidatorInterceptor;

    @Override
    public void afterSave(Collection<AfterSaveEvent> collection) {
        for (AfterSaveEvent event : collection) {
            final PK pk = event.getPk();
            final int type = event.getType();
            if (pk.getTypeCode() == 12015 && type == AfterSaveEvent.CREATE) {
                final AsahiSAMDirectDebitModel asahiSAMDirectDebitModel = modelService.get(pk);
                asahiDirectDebitValidatorInterceptor.isSendDirectDebitSuccess(asahiSAMDirectDebitModel, false, "update");
            }
        }
    }
}
