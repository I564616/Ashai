package com.apb.core.services.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryEntryData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import com.opencsv.CSVWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.order.dao.impl.ApbOrderStatusMappingDaoImpl;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.core.util.AsahiCoreUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.order.dao.DefaultSabmOrderDao;

/**
 * The Class AsahiOrderServiceImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiOrderServiceImpl extends DefaultOrderService implements AsahiOrderService
{

	private static final String QUICKORDER_NUMBER_ORDERS_SHOW = "quickorder.number.order.show.sga";

	private static final Logger LOG = LoggerFactory.getLogger(AsahiOrderServiceImpl.class);

	/** The order dao. */
	@Resource(name = "orderDao")
	DefaultSabmOrderDao orderDao;

	/** The order dao. */
	@Resource(name = "orderStatusMappingDao")
	ApbOrderStatusMappingDaoImpl orderStatusMappingDao;


	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The model service. */
	@Resource
	private ModelService modelService;
	@Resource
	private MediaService mediaService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/**
	 * Gets the order list.
	 *
	 * @param status
	 *           the status
	 * @return the order list
	 */
	@Override
	public List<OrderModel> getOrderList(final String status, final CMSSiteModel cmsSiteModel)
	{
		return this.orderDao.findOrdersByOrderStatus(status, cmsSiteModel);
	}

	/**
	 * Gets the order for code.
	 *
	 * @param code
	 *           the code
	 * @return the order for code
	 */
	@Override
	public OrderModel getOrderForCode(final String code)
	{
		return this.orderDao.getOrderForCode(code);
	}

	/**
	 * Gets the order entry by backend uid.
	 *
	 * @param backendUid
	 *           the backend uid
	 * @return the order entry by backend uid
	 */
	@Override
	public OrderEntryModel getOrderEntryByBackendUid(final String backendUid)
	{
		return this.orderDao.getOrderEntryByBackendUid(backendUid);
	}

	/**
	 * Gets the base site by uid.
	 *
	 * @param siteUid
	 *           the site uid
	 * @return the base site by uid
	 */
	@Override
	public BaseSiteModel getBaseSiteByUid(final String siteUid)
	{
		return this.orderDao.getBaseSiteByUid(siteUid);

	}

	/**
	 * Gets the base store by uid.
	 *
	 * @param storeUid
	 *           the store uid
	 * @return the base store by uid
	 */
	@Override
	public BaseStoreModel getBaseStoreByUid(final String storeUid)
	{
		return this.orderDao.getBaseStoreByUid(storeUid);
	}

	@Override
	public String getOrderMapping(final String backendStatusCode)
	{
		return orderStatusMappingDao.getOrderMapping(backendStatusCode);
	}

	@Override
	public String getDisplayOrderStatus(final String statusCode, final String companyCode)
	{
		return orderStatusMappingDao.getDisplayOrderStatus(statusCode,companyCode);
	}

	@Override
	public List<OrderModel> getOrderEntriesForUser(final AsahiB2BUnitModel unit)
	{
		List<OrderModel> listOrders = this.orderDao.getOrderEntriesForUser(unit);
		final int noOfOrders = Integer.parseInt(this.asahiConfigurationService.getString(QUICKORDER_NUMBER_ORDERS_SHOW, "6"));
		if (CollectionUtils.isNotEmpty(listOrders))
		{
			listOrders = listOrders.stream().limit(noOfOrders).collect(Collectors.toList());
		}
		return listOrders;
	}

	/**
	 * Gets the orders based on date and site.
	 *
	 * @param siteId the site id
	 * @param previousYear the previous year
	 * @param currentDate the current date
	 * @return the base store by uid
	 */
	@Override
	public void removeOrdersBasedOnDateAndSite(
			final String siteId, final Date previousYear, final Date currentDate) {
		final List<OrderModel> orders = this.orderDao.getOrdersBasedOnDateAndSite(siteId,previousYear,currentDate);
		if(CollectionUtils.isNotEmpty(orders)){
			modelService.removeAll(orders);
		}
	}
	@Override
	public String exportOrderCSV(final List<OrderHistoryData> orders)
	{
		CSVWriter csvWriter = null;
		Writer writer = null;
		try {
			final File outputFile = populateFileStructure();
			writer = Files.newBufferedWriter(Path.of(outputFile.getPath()));

			csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, '\0', '\0', CSVWriter.DEFAULT_LINE_END);

			final boolean isNapUser = asahiCoreUtil.isNAPUser();
			final String orderHeader = isNapUser?asahiConfigurationService.getString("sga.report.order.nap.header","Order No.,Data Placed,User,Product,Package Size,Portal Unit Volume,Ordered Quantity"):
				asahiConfigurationService.getString("sga.report.order.header","Order No.,Data Placed,User,Order Net Price,Order GST,OrderCDL,Subtotal,Product,Package Size,Portal Unit Volume,Ordered Quantity,Base Product Price,Total Price");

			
			
			final String[] headerRecord = orderHeader.split(",");
			csvWriter.writeNext(headerRecord);

			if(!isNapUser)
			{
			
			for (final OrderHistoryData order: orders) {

   			if(CollectionUtils.isNotEmpty(order.getEntries()))
   				{
      				for(final OrderHistoryEntryData entry: order.getEntries())
         				{
         				final String[] line = new String[13];
         					line[0] = order.getCode();
         					line[1] = "\""+order.getOrderPlacedDate()+"\"";
         				   line[2] = order.getFirstName();
         					line[3] = order.getOrderNetPrice()!=null?order.getOrderNetPrice().getFormattedValue():"";
         					line[4] = order.getOrderGST()!=null?order.getOrderGST().getFormattedValue():"";
         					line[5] = order.getOrderCDL()!=null?order.getOrderCDL().getFormattedValue():"";
         					line[6] = order.getSubTotal()!=null?order.getSubTotal().getFormattedValue():"";
         					line[7] = entry.getProductName();
         					line[8] = entry.getProductPackageSize();
         					line[9] = entry.getProductUnitVolume();
         					line[10] = entry.getOrderQuantity();
         					line[11] = entry.getProductBasePrice()!=null?entry.getProductBasePrice().getFormattedValue():"";
         					line[12] = order.getTotal()!=null?order.getTotal().getFormattedValue():"";

         					csvWriter.writeNext(line);
         				}
   			  }
   			else
   			{
   				final String[] line = new String[13];
   				line[0] = order.getCode();
   				line[1] = "\""+order.getOrderPlacedDate()+"\"";
   				line[2] = order.getFirstName();
   				line[3] = order.getOrderNetPrice()!=null?order.getOrderNetPrice().getFormattedValue():"";
   				line[4] = order.getOrderGST()!=null?order.getOrderGST().getFormattedValue():"";
   				line[5] = order.getOrderCDL()!=null?order.getOrderCDL().getFormattedValue():"";
   				line[6] = order.getSubTotal()!=null?order.getSubTotal().getFormattedValue():"";
   				line[7] = "";
   				line[8] = "";
   				line[9] = "";
   				line[10] = "";
   				line[11] = "";
   				line[12] = order.getTotal()!=null?order.getTotal().getFormattedValue():"";
   				csvWriter.writeNext(line,false);
   			}
   			}
			}
			else
			{
				for (final OrderHistoryData order: orders) {
				   
      			if(CollectionUtils.isNotEmpty(order.getEntries()))
      				{
         				for(final OrderHistoryEntryData entry: order.getEntries())
            				{
            					final String[] line = new String[13];
            					line[0] = order.getCode();
            					line[1] = "\""+order.getOrderPlacedDate()+"\"";
            				   line[2] = order.getFirstName();
            					line[3] = entry.getProductName();
            					line[4] = entry.getProductPackageSize();
            					line[5] = entry.getProductUnitVolume();
            					line[6] = entry.getOrderQuantity();
   
            					csvWriter.writeNext(line);
            				}
      			  }
      			else
      			{
      				final String[] line = new String[13];
      				line[0] = order.getCode();
      				line[1] = "\""+order.getOrderPlacedDate()+"\"";
      				line[2] = order.getFirstName();
      				line[3] = "";
      				line[4] = "";
      				line[5] = "";
      				line[6] = "";
      				csvWriter.writeNext(line,false);
      			}

			}
			}

			writer.close();

			 final CatalogUnawareMediaModel mediaModel = getModelService().create(CatalogUnawareMediaModel.class);
	        if(Objects.nonNull(outputFile)) {
	            mediaModel.setCode( outputFile.getName()+ "_" + System.currentTimeMillis());
	            mediaModel.setMime("text/xls");
	            mediaModel.setRealFileName(outputFile.getName());
	            modelService.save(mediaModel);
	            try {
	            	mediaService.setStreamForMedia(mediaModel, new FileInputStream(outputFile),outputFile.getName(),mediaModel.getMime());
	            } catch (final FileNotFoundException e) {
	                LOG.info("Error generating MediaFile" + e.getStackTrace());
	            }
	            FileUtils.forceDelete(outputFile);
	        }
	        return mediaModel.getDownloadURL();

		} catch (final IOException e) {
			LOG.error("Error encountered writing to file. ", e);
		}
		finally {

			try
			{
				if (writer != null)
				{
					writer.close();
				}
				if (csvWriter != null)
				{
					csvWriter.close();
				}
			}
			catch (final IOException e)
			{
				// YTODO Auto-generated catch block
				LOG.error("Error encountered writing to file. ", e);
			}
		}
		return null;
	}

	 private File populateFileStructure()
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        final String pathDirectory = asahiConfigurationService.getString("sga.report.order.fileDir","order");
        return new File(pathDirectory+"_"+dateFormat.format(new Date())+".csv");
    }
}
