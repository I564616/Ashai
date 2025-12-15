package com.apb.facades.contactus.impl;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.model.ContactUsQueryTypeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbContactUsService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.contactus.ApbContactUsFacade;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.ContactUsQueryTypeData;
import com.apb.facades.contactust.data.DeliveryDiscrepancyData;
import com.apb.facades.contactust.data.PriceDiscrepancyData;
import com.google.common.base.Preconditions;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.model.DiscrepancyDetailsModel;

import com.apb.core.util.AsahiSiteUtil;

/**
 *
 */
public class ApbContactUsFacadeImpl implements ApbContactUsFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbContactUsFacadeImpl.class);

	@Autowired
	private ApbContactUsService apbContactUsService;

	/** The apb contact us converter. */
	private Converter<ContactUsQueryTypeModel, ContactUsQueryTypeData> apbContactUsQueryTypeConverter;

	@Autowired
	private ApbCustomerAccountService apbCustomerAccountService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private MediaService mediaService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private static final String DEFAULT_NEW_USER_REGISTER_SUBJECT = "4";

	/**
	 * get List of subject
	 */
	@Override
	public List<ContactUsQueryTypeData> getSubject(final CMSSiteModel cmsSite)
	{
		final List<ContactUsQueryTypeData> contactUsQueryTypeDataList = new LinkedList<>();
		final List<ContactUsQueryTypeModel> contactUsQueryTypeModelList = apbContactUsService.getSubject(cmsSite);
		if (CollectionUtils.isNotEmpty(contactUsQueryTypeModelList))
		{
			for (final ContactUsQueryTypeModel contactUsQueryTypeModel : contactUsQueryTypeModelList)
			{
				final ContactUsQueryTypeData contactUsQueryTypeData = new ContactUsQueryTypeData();
				apbContactUsQueryTypeConverter.convert(contactUsQueryTypeModel, contactUsQueryTypeData);
				contactUsQueryTypeDataList.add(contactUsQueryTypeData);
			}
		}
		LOG.warn("Contact Us Query Type not found!");
		return contactUsQueryTypeDataList;
	}

	/**
	 * @return the apbContactUsService
	 */
	public ApbContactUsService getApbContactUsService()
	{
		return apbContactUsService;
	}

	/**
	 * @param apbContactUsService
	 *           the apbContactUsService to set
	 */
	public void setApbContactUsService(final ApbContactUsService apbContactUsService)
	{
		this.apbContactUsService = apbContactUsService;
	}

	/**
	 * @return the apbContactUsQueryTypeConverter
	 */
	public Converter<ContactUsQueryTypeModel, ContactUsQueryTypeData> getApbContactUsQueryTypeConverter()
	{
		return apbContactUsQueryTypeConverter;
	}

	/**
	 * @param apbContactUsQueryTypeConverter
	 *           the apbContactUsQueryTypeConverter to set
	 */
	public void setApbContactUsQueryTypeConverter(
			final Converter<ContactUsQueryTypeModel, ContactUsQueryTypeData> apbContactUsQueryTypeConverter)
	{
		this.apbContactUsQueryTypeConverter = apbContactUsQueryTypeConverter;
	}

	@Override
	public String sendContactUsQueryEmail(final ApbContactUsData apbContactUsData)
			throws MediaIOException, IllegalArgumentException, IOException
	{
		validateParameterNotNull(apbContactUsData, "ApbContactUsData can not be null");
		return apbCustomerAccountService.sendContactUsQueryEmail(setContactUsModel(apbContactUsData));
	}

	private ContactUsQueryEmailModel setContactUsModel(final ApbContactUsData apbContactUsData)
			throws MediaIOException, IllegalArgumentException, IOException {

		final Boolean isUpdatedContactus = this.asahiConfigurationService.getBoolean("sga.contactus.update.available", false);

		final ContactUsQueryEmailModel contactUsQueryEmailModel = modelService.create(ContactUsQueryEmailModel.class);
		contactUsQueryEmailModel.setCode(UUID.randomUUID().toString());
		contactUsQueryEmailModel.setAccountNumber(apbContactUsData.getAccountNumber());
		contactUsQueryEmailModel.setCompanyName(apbContactUsData.getCompanyName());
		contactUsQueryEmailModel.setContactNumber(apbContactUsData.getContactNumber());
		contactUsQueryEmailModel.setEmailAddress(apbContactUsData.getEmailAddress());
		contactUsQueryEmailModel.setName(apbContactUsData.getName());
		if(!isUpdatedContactus || asahiSiteUtil.isApb()){
			final ContactUsQueryTypeModel contactUsQueryTypeModel = new ContactUsQueryTypeModel();
			try {
				contactUsQueryTypeModel.setCode(apbContactUsData.getSubject());
				final List<ContactUsQueryTypeModel> contactUsQueryTypeModelList = flexibleSearchService
						.getModelsByExample(contactUsQueryTypeModel);
				if (CollectionUtils.isNotEmpty(contactUsQueryTypeModelList)) {
					contactUsQueryEmailModel.setSubject(contactUsQueryTypeModelList.get(0).getContactUsQueryType());
				}
			} catch (final ModelLoadingException mle) {
				LOG.error("Model Not loaded" + mle.getMessage());
			}
			if (StringUtils.isEmpty(apbContactUsData.getSubjectOther())) {
				contactUsQueryEmailModel.setOtherSubject(ApbFacadesConstants.NA);
			} else {
				contactUsQueryEmailModel.setOtherSubject(apbContactUsData.getSubjectOther());
			}
		}
		contactUsQueryEmailModel.setFurtherDetail(apbContactUsData.getFurtherDetail());
		final MultipartFile pdfile = apbContactUsData.getPdfFile();
		contactUsQueryEmailModel.setUploadFile(getMediasFromFiles(pdfile));
		if (apbContactUsData.getAsahiContactUsSaleRepData() != null)
		{
			contactUsQueryEmailModel.setSalesRepName(apbContactUsData.getAsahiContactUsSaleRepData().getName() != null
					? apbContactUsData.getAsahiContactUsSaleRepData().getName() : "");
			contactUsQueryEmailModel.setSalesRepEmail(apbContactUsData.getAsahiContactUsSaleRepData().getEmailAddress() != null
					? apbContactUsData.getAsahiContactUsSaleRepData().getEmailAddress() : "");
		}

		// New enquiry attributes added here


		contactUsQueryEmailModel.setMessage(apbContactUsData.getMessage());
		contactUsQueryEmailModel.setDeliveryNumber(apbContactUsData.getDeliveryNumber());
		contactUsQueryEmailModel.setEnquiryType(apbContactUsData.getEnquiryType());
		contactUsQueryEmailModel.setEnquirySubType(apbContactUsData.getEnquirySubType());
		contactUsQueryEmailModel.setAddInfo(apbContactUsData.getAddInfo());

		final List<DiscrepancyDetailsModel> discrepancylist = new ArrayList<DiscrepancyDetailsModel>();



		if(null != apbContactUsData.getEnquiryType() && apbContactUsData.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.INCORRECT_CHARGE.getCode()))
		{

			for(final PriceDiscrepancyData pricediscrepancy : apbContactUsData.getPriceDiscrepancyDTOs())
			{
				final DiscrepancyDetailsModel discrepancy = modelService.create(DiscrepancyDetailsModel.class);
				discrepancy.setMaterialNumber(pricediscrepancy.getMaterialNumber());
				discrepancy.setExpectedTotalPay(pricediscrepancy.getExpectedTotalPay());
				discrepancy.setAmtCharged(pricediscrepancy.getAmtCharged());
				modelService.save(discrepancy);

				discrepancylist.add(discrepancy);
			}

		}

		if(null != apbContactUsData.getEnquiryType() && apbContactUsData.getEnquiryType().equalsIgnoreCase(AsahiEnquiryType.REPORT_DEL_ISSUE.getCode()))
		{

			for(final DeliveryDiscrepancyData deliverydiscrepancy : apbContactUsData.getDeliveryDiscrepancyDTOs())
			{
				final DiscrepancyDetailsModel discrepancy = modelService.create(DiscrepancyDetailsModel.class);

				discrepancy.setMaterialNumber(deliverydiscrepancy.getMaterialNumber());
				discrepancy.setQtyWithDelIssue(deliverydiscrepancy.getQtyWithDelIssue());
				discrepancy.setQtyReceived(deliverydiscrepancy.getQtyReceived());
				discrepancy.setExpectedQty(deliverydiscrepancy.getExpectedQty());
				modelService.save(discrepancy);

				discrepancylist.add(discrepancy);
			}

		}

		contactUsQueryEmailModel.setDiscrepancies(discrepancylist);

		return contactUsQueryEmailModel;
	}

	/**
	 * @param file
	 * @return media
	 * @throws MediaIOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public MediaModel getMediasFromFiles(final MultipartFile file)
			throws MediaIOException, IllegalArgumentException, IOException
	{
		if (null != file && file.getSize() > 0)
		{
			final String fileName = file.getOriginalFilename();
			final CatalogUnawareMediaModel mediaModel = modelService.create(CatalogUnawareMediaModel.class);
			if (null != fileName && !"".equals(fileName))
			{
				mediaModel.setCode(UUID.randomUUID().toString());
				modelService.save(mediaModel);
				final InputStream inputStream = file.getInputStream();
				try
				{
					mediaService.setStreamForMedia(mediaModel, inputStream, fileName, file.getContentType());
					if (null != inputStream)
					{
						inputStream.close();
					}
				}
				catch (final FileNotFoundException fne)
				{
					if (null != inputStream)
					{
						inputStream.close();
					}
					LOG.error("Error in uploaded file: " + fne.getMessage(), fne);
				}
				modelService.refresh(mediaModel);
			}
			return mediaModel;
		}
		return null;
	}

	/**
	 * @param parameter
	 * @param nullMessage
	 */
	public static void validateParameterNotNull(final Object parameter, final String nullMessage)
	{
		Preconditions.checkArgument(parameter != null, nullMessage);
	}

	/**
	 * @param parameter
	 * @param parameterValue
	 */
	public static void validateParameterNotNullStandardMessage(final String parameter, final Object parameterValue)
	{
		validateParameterNotNull(parameterValue, "Parameter " + parameter + " can not be null");
	}

	@Override
	public String getDefaultContactUsSubjectCode(final CMSSiteModel cmsSite)
	{
		final List<ContactUsQueryTypeData> subjectList = this.getSubject(cmsSite);
		ContactUsQueryTypeData defaultContactUsSubject = null;

		if (CollectionUtils.isNotEmpty(subjectList))
		{
			defaultContactUsSubject = subjectList.stream().filter(x -> DEFAULT_NEW_USER_REGISTER_SUBJECT.equals(x.getCode()))
					.findAny().orElse(null);
		}

		return (null != defaultContactUsSubject) ? defaultContactUsSubject.getCode() : "";
	}

}
