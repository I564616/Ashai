/**
 *
 */
package com.sabmiller.webservice.importer;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.webservice.customer.handler.CustomerImportHandler;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.model.ImportRecordModel;
import com.sabmiller.webservice.model.MasterImportModel;
import com.sabmiller.webservice.product.handler.ProductImportHandler;
import com.sabmiller.webservice.response.ImportResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;


/**
 * Abstract template to handle implementation of data import into Hybris system. The class is the entry point for all
 * the import from the external system to Hybris - this has been hooked up using Spring Integration module (see
 * sabmwebservices-spring.xml for configuration). Each concrete implementation needs to extend this handler and provide
 * its own implementation of importEntity(). See {@link CustomerImportHandler} for an example implementation.
 *
 * This class is also responsible for persisting the payload request, sending error email (in case of failures) and
 * persisting detailed log information in the {@link MasterImportModel} and {@link ImportRecordModel} thereby avoiding
 * the need to look into log files to debug issues. All the information is available in HMC
 *
 * @author joshua.a.antony
 *
 */
public abstract class AbstractImportHandler<RequestEntity, R extends ImportResponse, IRecordModel extends ImportRecordModel>
		implements ImportHandler<RequestEntity, R>, ImportResponseGenerator<R, RequestEntity>
{
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "emailService")
	private SystemEmailService emailService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	public abstract Converter<R, IRecordModel> getImportRecordReverseConverter();

	public abstract EntityTypeEnum getEntityType();

	public abstract R importEntity(final RequestEntity entity);



	/**
	 * Save the incoming payload request to the "MasterImport" item.It provides insight on the actual request received
	 * from the external system( SAP).Also, the payload can be used to re-run the import if required at a later stage.
	 */
	@Override
	public Message savePayload(final Message message)
	{
		final String paylaodId = getPaylaodFromRequest(message);
		final String referenceId = getReferenceIdFromRequest(message);
		final MasterImportModel existingModel = fetchMasterImportModel(paylaodId);

		final MasterImportModel model = existingModel == null ? (MasterImportModel) modelService.create(MasterImportModel.class)
				: existingModel;
		model.setPayloadId(paylaodId);
		model.setEntity(getEntityType());
		model.setStatus(DataImportStatusEnum.NEW);
		model.setPayload(message.getPayload().toString());
		model.setReferenceId(referenceId);
		modelService.save(model);

		LOG.debug("End persistRequest(). Item imported is : {}", model.getPk().getLong());

		return message;
	}

	protected String getPaylaodFromRequest(final Message message)
	{
		final Object payloadObj = message.getHeaders().get("payloadId");
		return payloadObj != null ? payloadObj.toString() : null;
	}

	protected String getReferenceIdFromRequest(final Message message)
	{
		final Object payloadObj = message.getHeaders().get("referenceId");
		return payloadObj != null ? payloadObj.toString() : null;
	}

	/**
	 * In case of XSD validation error, send email with the payload information. This method needs to be invoked from the
	 * Spring Integration framework(see sabmwebservices-spring.xml for configuration) on XSD failure
	 */
	@Override
	public String handleXSDValidationError(final String message, final String payloadId)
	{
		LOG.error("XSD Valdiation Error occurred for Payload Id {}. Sending email to support team with the actual payload... ",
				payloadId);


		updatePayloadWithError(payloadId, message, DataImportStatusEnum.XSD_VALIDATION_ERROR);

		sendEmail(message, payloadId);
		return "XSD validation occured. An email has been sent with detailed error to support team";
	}

	public String handleXSDValidationError(final @Payload Message message, @Header("payloadId") final String payloadId)
	{
		return handleXSDValidationError(message.getPayload().toString(), payloadId);
	}


	/**
	 * Update the main {@link MasterImportModel} record with the error information.The error information also includes
	 * the exception stack trace (if any).
	 */
	public void updatePayloadWithError(final String payloadId, final String error, final DataImportStatusEnum dataImportStatus)
	{
		final MasterImportModel model = fetchMasterImportModel(payloadId);
		model.setError(error);
		model.setStatus(dataImportStatus);
		modelService.save(model);
	}


	/**
	 * Import individual entity. The Payload might have many entities (example : many product), this method is
	 * responsible for importing one such entity (example : product). All the import is performed in transaction, any
	 * failure during the transaction will result in roll back.
	 *
	 * This template relies heavily on the actual implementor to perform most of the operations. See
	 * {@link CustomerImportHandler}, {@link ProductImportHandler} for the implementations
	 */
	@Override
	public <IC> R executeImport(final @Payload RequestEntity requestEntity, @Header("payloadId") final String payloadId, @Header(name = "importContext",required = false) IC context)
	{
		LOG.debug("In executeImport(). Payload Id is {} ", payloadId);

		preExecuteImport();

		final Transaction tx = Transaction.current();
		tx.begin();
		boolean success = false;
		try
		{
			final R importEntity = this instanceof ContextSupportImportHandler?((ContextSupportImportHandler<RequestEntity, R,IC>) this).importEntity(requestEntity,context):importEntity(requestEntity);
			success = DataImportStatusEnum.SUCCESS.equals(importEntity.getStatus());
			importEntity.setPayloadId(payloadId);
			if (success == false)
			{
				sendEmail(importEntity.getError(), payloadId);
			}
			return importEntity;
		}
		catch (final Exception e)
		{
			LOG.error("Error occured while performig import. Generating error response and Rolling back!!!", e);
			sendEmail(ExceptionUtils.getStackTrace(e), payloadId);
			final R importEntity = generateResponse(requestEntity, e, null);
			importEntity.setPayloadId(payloadId);
			return importEntity;
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}

	}

	public R handleRequest(final @Payload RequestEntity requestEntity)
	{
		LOG.debug("In handleRequest().");

		preExecuteImport();

		try {
			final R response = importEntity(requestEntity);

			return response;
		}

		catch (Exception e){

			e.printStackTrace();
		}
		return null;
	}

	protected void preExecuteImport()
	{
		executeAsAdmin();
	}


	/**
	 * Tie each record that was imported to the Master record. This makes it easier to navigate back to the Master Record
	 * from the Individual record (ex: Customer,Product) in HMC and lookup for the payload that was actually sent from
	 * SAP
	 */
	protected void associateRecordToMaster(final String payloadId, final IRecordModel recordModel)
	{
		try
		{
			LOG.debug("Associating record {}  to Master having payload : {}. Record status is {}", recordModel.getPk(), payloadId,
					recordModel.getStatus());

			final MasterImportModel masterImportModel = fetchMasterImportModel(payloadId);
			masterImportModel.setStatus(recordModel.getStatus());
			modelService.save(masterImportModel);

			recordModel.setMasterRecord(masterImportModel);
		}
		catch (final Exception e)
		{
			LOG.error("Exception associating the record " + recordModel.getPk() + " to Master with Payload Id " + payloadId, e);
		}
	}

	/**
	 * The {@link MasterImportModel} holds the actual payload that was sent from SAP. This method does a lookup of the
	 * model from the payload id
	 */
	protected MasterImportModel fetchMasterImportModel(final String payloadId)
	{
		if (!StringUtils.isBlank(payloadId))
		{
			final String query = "SELECT {" + MasterImportModel.PK + "} " + "FROM {" + MasterImportModel._TYPECODE + "} WHERE {"
					+ MasterImportModel.PAYLOADID + "}=?payloadId";
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("payloadId", payloadId);

			final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
			final SearchResult<MasterImportModel> result = flexibleSearchService.search(fsq);
			return result.getCount() > 0 ? result.getResult().get(0) : null;
		}
		return null;
	}

	/** Persist import status for each entity (Customer/Product etc...) to the database **/
	public boolean logImportStatus(final R response)
	{
		LOG.debug("Logging the import status for record {}", response);

		final IRecordModel model = getImportRecordReverseConverter().convert(response);
		associateRecordToMaster(response.getPayloadId(), model);
		getModelService().save(model);

		return response.getError() != null;
	}

	private String trimEmailBody(final String payload)
	{
		return payload;
		//return payload.length() > 3000 ? payload.substring(0, 3000) : payload;
	}

	/**
	 * This is required in order to create some entities like {@link B2BUnitModel} , {@link ProductModel} etc. The
	 * 'integrationAdmin' is basically a user with Admin group and hence can perform operation required during the data
	 * migration/creation. This also ensures that any restrictions applied on the B2BCustomerGroup, B2BGroup etc... do
	 * not apply to this user.
	 */
	protected void executeAsAdmin()
	{
		userService.setCurrentUser(userService.getUserForUID("integrationAdmin"));
	}

	private void sendEmail(final String message, final String payloadId)
	{
		if (Config.getBoolean("services.import.support.email.enable", true))
		{
			setCatalogVersions();

			final String supportEmail = Config.getString("services.import.support.toemail", null);
			final String supportEmailName = Config.getString("services.import.support.toemail.name", null);
			final String fromEmail = Config.getString("services.import.support.fromemail", null);

			final String subject = getEntityType() + " Import Failed. Payload " + payloadId;

			final SystemEmailMessageModel systemEmailMessageModel = emailService.constructSystemEmailForMultipleRecepients(fromEmail,
					supportEmail, supportEmailName, subject, Collections.singletonList(trimEmailBody(message)), null);
			emailService.send(systemEmailMessageModel);
		}
	}

	private void setCatalogVersions()
	{
		final List<CatalogVersionModel> catalogs = new ArrayList<CatalogVersionModel>();
		catalogs.add(catalogVersionDeterminationStrategy.offlineCatalogVersion());
		catalogs.add(catalogVersionDeterminationStrategy.offlineContentCatalogVersion());
		catalogVersionService.setSessionCatalogVersions(catalogs);
	}


	public ModelService getModelService()
	{
		return modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

}
