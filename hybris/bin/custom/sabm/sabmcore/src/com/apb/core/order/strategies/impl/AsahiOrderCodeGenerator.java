package com.apb.core.order.strategies.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class AsahiOrderCodeGenerator extends PersistentKeyGenerator{
    @Autowired
    private ConfigurationService configurationService;

    private PersistentKeyGenerator orderCodeGenerator;

    public void setOrderCodeGenerator(PersistentKeyGenerator orderCodeGenerator) {
        this.orderCodeGenerator = orderCodeGenerator;
    }

    public KeyGenerator getAsahiKeyGenerator(){
        int digits = configurationService.getConfiguration().getInt("asahi.custom.keygen.order.code.digits");
        String start = configurationService.getConfiguration().getString("asahi.custom.keygen.order.code.start");

        orderCodeGenerator.setDigits(digits);
        orderCodeGenerator.setStart(start);

        return orderCodeGenerator;
    }

}
