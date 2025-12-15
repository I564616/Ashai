package com.sabmiller.commons.utils;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.model.PlantCutOffModel;

public  class SabmTimeZoneUtils {

    @Resource
    private BaseStoreService baseStoreService;

    // get plant timezone
    public String getPlantTimeZone(final B2BUnitModel b2bUnit) {

        String timeZone=null;

        if (b2bUnit.getPlant() != null && CollectionUtils.isNotEmpty(b2bUnit.getPlant().getCutOffs())) {

            for (PlantCutOffModel plantCutOffForTZ : b2bUnit.getPlant().getCutOffs()){

                if (plantCutOffForTZ != null && plantCutOffForTZ.getTimeZone() != null) {

                    TimeZone plantCutOffTimeZone = TimeZone.getTimeZone(plantCutOffForTZ.getTimeZone().getCode());

                    timeZone = plantCutOffTimeZone.getID();
                    if(timeZone!=null){
                        return timeZone;
                    }
                }
            }
        }
        if(timeZone==null){

            timeZone= getBaseStoreTimeZone();
        }
        return timeZone;
    }

    public String getBaseStoreTimeZone() {

        final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");
        TimeZone storeTimeZone = null;
        //Getting BaseStore timezone
        if (baseStore != null && baseStore.getTimeZone() != null)
        {
            storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
        }
        if(!Objects.isNull(storeTimeZone)){
            return storeTimeZone.getID();
        }

        return "Australia/Melbourne";

    }
}
