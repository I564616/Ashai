package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.apache.commons.lang3.BooleanUtils;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SalesForceExportCronjobModel;
import com.sabmiller.core.report.service.CustomerReportService;
import com.sabmiller.core.report.service.NotificationReportService;
import com.sabmiller.core.report.service.OrderReportService;
import com.sabmiller.core.report.service.ProductReportService;
import com.sabmiller.core.report.service.VenueReportService;
import com.sabmiller.core.report.service.WelcomeEmailSaleForceDataExportService;
import com.sabmiller.core.report.service.UserToVenueDataExportService;
import com.sabmiller.integration.salesforce.SabmCSVUtils;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class SalesForceReportExportJob extends AbstractJobPerformable<SalesForceExportCronjobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(SalesForceReportExportJob.class);
    private VenueReportService venueReportService;
    private CustomerReportService customerReportService;
    private ProductReportService productReportService;
    private OrderReportService orderReportService;
    private NotificationReportService notificationReportService;
	private WelcomeEmailSaleForceDataExportService welcomeEmailSaleForceDataExportService;
    private UserToVenueDataExportService userToVenueDataExportService;

    @Override
    public PerformResult perform(final SalesForceExportCronjobModel salesForceExportCronjobModel) {
        CronJobResult result = CronJobResult.SUCCESS;

        // one cronjob will export five files , each of them has it's own try/catch to prevent previous export fail which will abort following other export
        // customer, b2bUnit/venue, product, order, notification

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getCustomers()))
		{
        LOG.info("Start to export customer for salesforce");
        try {
            customerReportService.generateReport("salesforce");
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
                    SabmCSVUtils.removeRecursive(SabmCSVUtils.getFullPath("customers").getPath());
				}

        } catch (final IOException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchProviderException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final PGPException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final Exception e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        }
		}

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getVenues()))
		{

        LOG.info("Start to export Venue for salesforce");
        try {
            venueReportService.generateReport("salesforce");
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
                    SabmCSVUtils.removeRecursive(SabmCSVUtils.getFullPath("venue").getPath());
				}

        } catch (final IOException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchProviderException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final PGPException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final Exception e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        }
		}

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getProducts()))
		{
        LOG.info("Start to export Sku/Product for salesforce");
        try {
            productReportService.generateReport("salesforce");
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
                    SabmCSVUtils.removeRecursive(SabmCSVUtils.getFullPath("sku").getPath());
				}
        } catch (final IOException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchProviderException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final PGPException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final Exception e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        }
		}

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getOrders()))
		{

        LOG.info("Start to export Order/Transaction for salesforce");
        try {
				orderReportService.generateReport("salesforce", salesForceExportCronjobModel.getBatchSize(),
						salesForceExportCronjobModel.getDeltaHours());
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
                    SabmCSVUtils.removeRecursive(SabmCSVUtils.getFullPath("transaction").getPath());
				}

        } catch (final IOException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchProviderException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final PGPException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final Exception e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        }
		}

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getNotifications()))
		{
        LOG.info("Start to export Notification for salesforce");
        try {

            notificationReportService.generateReport("salesforce");
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
				SabmCSVUtils.purgeOldFiles(SabmCSVUtils.getFullPath("notification").getPath());
				}
        } catch (final IOException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchProviderException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final PGPException e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        } catch (final Exception e) {
            result = CronJobResult.ERROR;
            LOG.error(e.getMessage(), e);
        }
		}

		if (BooleanUtils.isTrue(salesForceExportCronjobModel.getWelcomeEmailCustomers()))
		{
			LOG.info("Start to export Notification for salesforce");
			/*try
			{

				welcomeEmailSaleForceDataExportService.generateReport("salesforce");
				if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
				{
                    SabmCSVUtils.removeRecursive(SabmCSVUtils.getFullPath("welcomeemail").getPath());
				}
			}
			catch (final IOException e)
			{
				result = CronJobResult.ERROR;
				LOG.error(e.getMessage(), e);
			}
			catch (final NoSuchProviderException e)
			{
				result = CronJobResult.ERROR;
				LOG.error(e.getMessage(), e);
			}
			catch (final PGPException e)
			{
				result = CronJobResult.ERROR;
				LOG.error(e.getMessage(), e);
			}
			catch (final Exception e)
			{
				result = CronJobResult.ERROR;
				LOG.error(e.getMessage(), e);
			}*/
		}

        if (BooleanUtils.isTrue(salesForceExportCronjobModel.getUserToVenueData()))
        {
            LOG.info("Start to export User To Venue Data for salesforce");
            try {

                userToVenueDataExportService.generateReport("salesforce");
                if (BooleanUtils.isTrue(salesForceExportCronjobModel.getPurgeOldFiles()))
                {
                    SabmCSVUtils.purgeOldFiles(SabmCSVUtils.getFullPath("userToVenue").getPath());
                }
            } catch (final IOException e) {
                result = CronJobResult.ERROR;
                LOG.error(e.getMessage(), e);
            } catch (final NoSuchProviderException e) {
                result = CronJobResult.ERROR;
                LOG.error(e.getMessage(), e);
            } catch (final PGPException e) {
                result = CronJobResult.ERROR;
                LOG.error(e.getMessage(), e);
            } catch (final Exception e) {
                result = CronJobResult.ERROR;
                LOG.error(e.getMessage(), e);
            }
        }

        return new PerformResult(result, CronJobStatus.FINISHED);
    }

    public VenueReportService getVenueReportService() {
        return venueReportService;
    }

    public void setVenueReportService(final VenueReportService venueReportService) {
        this.venueReportService = venueReportService;
    }

    public CustomerReportService getCustomerReportService() {
        return customerReportService;
    }

    public void setCustomerReportService(final CustomerReportService customerReportService) {
        this.customerReportService = customerReportService;
    }

    public ProductReportService getProductReportService() {
        return productReportService;
    }
    public void setProductReportService(final ProductReportService productReportService) {
        this.productReportService = productReportService;
    }

    public OrderReportService getOrderReportService() {
        return orderReportService;
    }
    public void setOrderReportService(final OrderReportService orderReportService) {
        this.orderReportService = orderReportService;
    }

    public NotificationReportService getNotificationReportService() {
        return notificationReportService;
    }
    public void setNotificationReportService(final NotificationReportService notificationReportService) {
        this.notificationReportService = notificationReportService;
    }

	/**
	 * @return the welcomeEmailSaleForceDataExportService
	 */
	public WelcomeEmailSaleForceDataExportService getWelcomeEmailSaleForceDataExportService()
	{
		return welcomeEmailSaleForceDataExportService;
	}

	/**
	 * @param welcomeEmailSaleForceDataExportService
	 *           the welcomeEmailSaleForceDataExportService to set
	 */
	public void setWelcomeEmailSaleForceDataExportService(
			final WelcomeEmailSaleForceDataExportService welcomeEmailSaleForceDataExportService)
	{
		this.welcomeEmailSaleForceDataExportService = welcomeEmailSaleForceDataExportService;
	}

    public void setUserToVenueDataExportService(UserToVenueDataExportService userToVenueDataExportService) {
        this.userToVenueDataExportService = userToVenueDataExportService;
    }
}
