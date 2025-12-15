package com.apb.facades.assistedservice.event;

import de.hybris.platform.assistedservicefacades.event.AssistedServiceCartRecalculator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.ticketsystem.events.SessionEvent;

import jakarta.annotation.Resource;


import com.apb.core.util.AsahiSiteUtil;

public class AsahiAssistedServiceCartRecalculator extends AssistedServiceCartRecalculator {

    private static final String DB_CART_RECALCULATION_DISABLED = "db.cart.recalculation.disabled";

    private ConfigurationService configurationService;
    
    @Resource
 	 private AsahiSiteUtil asahiSiteUtil;

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    @Override
    protected void onEvent(final SessionEvent event){
   	 
   	 if(!asahiSiteUtil.isCub())
   	 {
   	 
        if(getConfigurationService().getConfiguration().getBoolean(DB_CART_RECALCULATION_DISABLED)){
            //do nothing
        }
        else{
            super.onEvent(event);
        }
   	 }
   	 else
   	 {
   		 super.onEvent(event);
   	 }
    }
}
