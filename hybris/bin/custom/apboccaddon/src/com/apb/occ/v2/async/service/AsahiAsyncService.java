package com.apb.occ.v2.async.service;


import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import de.hybris.platform.apboccaddon.dto.b2bunit.AbpB2BUnitWsDTO;
import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsahiAsyncService{

    private static final Logger LOGGER = Logger.getLogger(AsahiAsyncService.class);


    /**
     * The apb B 2 B unit ws DTO validator.
     */
    @Resource(name = "apbB2BUnitWsDTOValidator")
    private Validator apbB2BUnitWsDTOValidator;

    /**
     * The product facade.
     */
    @Resource(name = "apbB2BUnitFacade")
    private ApbB2BUnitFacade apbB2BUnitFacade;

    @Autowired
    private BaseSiteService baseSiteService;

    @Resource(name = "asyncExecutor")
    private ThreadPoolTaskExecutor asyncExecutor;

    @Autowired
    private ImpersonationService impersonationService;

    @Autowired
    private AsahiConfigurationService asahiConfigurationService;

    private DataMapper dataMapper = null;

    private Tenant tenant;

    public void setDataMapper(final DataMapper dataMapper) {
        if (null == this.dataMapper) {
            this.dataMapper = dataMapper;
        }
    }

    public void setTenant(final Tenant tenant){
        this.tenant = tenant;
    }

    public void importCustomers(final List<AbpB2BUnitWsDTO> customers, final String siteUid) {
        LOGGER.info("Total customer data sent - " + customers.size());
        CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture> lsFutures = new ArrayList<>(customers.size());
            Registry.setCurrentTenant(this.tenant);
            for (final AbpB2BUnitWsDTO b2bUnit : customers) {
                lsFutures.add(importCustomerAsync(b2bUnit, siteUid));
            }
            return CompletableFuture.completedFuture(lsFutures);
        }, asyncExecutor).thenAccept(cf -> {
            if (cf.getNow(new ArrayList<>()).size() > 0) {
                try {
                    LOGGER.info("Records imported successfully, number of records imported - " + cf.get().size());
                } catch (Exception e) {
                    LOGGER.info("Records not imported successfully");
                }
            }
        });
    }

    public CompletableFuture importCustomerAsync(final AbpB2BUnitWsDTO b2bUnit, final String siteUid) {
        final ImpersonationContext context = new ImpersonationContext();
        context.setSite(baseSiteService.getBaseSiteForUID(siteUid));
        return impersonationService.executeInContext(context, () -> {
            boolean imported = false;
            try {
                LOGGER.info("Customer Account Number: " + b2bUnit.getAccountNum() + ", Uid: " + b2bUnit.getUid() + ", Name " + b2bUnit.getName());
                final Errors errors = new BeanPropertyBindingResult(b2bUnit, "customer");
                apbB2BUnitWsDTOValidator.validate(b2bUnit, errors);
                if (errors.hasErrors() && asahiConfigurationService.getBoolean("dump.validation.failed.customers.sga", false)) {
                    imported = apbB2BUnitFacade.custImportFailed(this.dataMapper.map(b2bUnit, AsahiB2BUnitData.class));
                } else {
                    imported = apbB2BUnitFacade.importApbB2BUnit(this.dataMapper.map(b2bUnit, AsahiB2BUnitData.class));
                }
                LOGGER.info(imported ? String.format("Uid : %s is saved successfully in DB", b2bUnit.getUid()) : String.format("Uid : %s is not saved in b2bUnit, hence saving in custom table", (null != b2bUnit.getUid() && !b2bUnit.getUid().isEmpty()) ? b2bUnit.getUid() : "(abn number) - " + b2bUnit.getAbnNumber()));
            } catch (Exception e) {
                LOGGER.error("Exception occurred in importCustomerAsync -> ", e);

            }
            return CompletableFuture.completedFuture(imported);
        });
    }
}