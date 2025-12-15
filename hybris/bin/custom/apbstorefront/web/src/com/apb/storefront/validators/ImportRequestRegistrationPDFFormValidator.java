/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.validators;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.forms.ApbContactUsForm;
import com.apb.storefront.forms.ApbRequestRegisterForm;
import com.apb.storefront.forms.PlanogramUpdateForm;


/**
 * @author C5252631
 *
 *         Implementation of ImportRequestRegistrationPDFFormValidator {@link Validator}
 *
 *         Handle Fields Mandatory and file type validation
 */
@Component("importRequestRegistrationPDFFormValidator")
public class ImportRequestRegistrationPDFFormValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(ImportRequestRegistrationPDFFormValidator.class);
	public static final String PDF_FILE_FIELD = "pdfFile";
	public static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";
	public static final String APP_IMAGE_JPEG_CONTENT_TYPE = "image/jpeg";
	public static final String APP_IMAGE_GIF_CONTENT_TYPE = "image/gif";
	public static final String APP_IMAGE_PNG_CONTENT_TYPE = "image/png";
	public static final String PDF_FILE_EXTENSION = ".pdf";
	public static final String JPEG_FILE_EXTENSION = ".jpeg";
	public static final String GIF_FILE_EXTENSION = ".gif";
	public static final String PNG_FILE_EXTENSION = ".png";
	public static final String JPG_FILE_EXTENSION = ".jpg";
	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	public void validate(final Object target, final Errors errors)
	{
		MultipartFile pdfFile = null;
		if (target instanceof ApbRequestRegisterForm)
		{
			final ApbRequestRegisterForm apbRequestRegisterForm = (ApbRequestRegisterForm) target;
			pdfFile = apbRequestRegisterForm.getPdfFile();
			if (apbRequestRegisterForm.isApplicantCarry())
			{
				validateFiles(pdfFile, errors);
			}
		}
		if (target instanceof ApbContactUsForm)
		{
			final ApbContactUsForm apbContactUsForm = (ApbContactUsForm) target;
			pdfFile = apbContactUsForm.getPdfFile();
			if (pdfFile != null && pdfFile.getSize() > 0)
			{
				validateFiles(pdfFile, errors);
			}
		}
		
		if (target instanceof PlanogramUpdateForm)
		{
			final PlanogramUpdateForm planogramUpdateForm = (PlanogramUpdateForm) target;
			pdfFile = planogramUpdateForm.getFile();
			if (pdfFile != null && pdfFile.getSize() > 0)
			{
				validateFiles(pdfFile, errors);
			}
		}
	}

	private Errors validateFiles(final MultipartFile pdfFile, final Errors errors)
	{
		if (pdfFile == null || pdfFile.isEmpty())
		{
			errors.rejectValue(PDF_FILE_FIELD, "import.pdf.file.fileRequired");
			return errors;
		}
		final String fileContentType = pdfFile.getContentType();
		final String fileName = pdfFile.getOriginalFilename();

		if ( null!= "fileName" &&(APPLICATION_PDF_CONTENT_TYPE.equalsIgnoreCase(fileContentType) && fileName.toLowerCase().endsWith(PDF_FILE_EXTENSION))
				|| (APP_IMAGE_JPEG_CONTENT_TYPE.equalsIgnoreCase(fileContentType)
						&& fileName.toLowerCase().endsWith(JPEG_FILE_EXTENSION))
				|| (APP_IMAGE_GIF_CONTENT_TYPE.equalsIgnoreCase(fileContentType)
						&& fileName.toLowerCase().endsWith(GIF_FILE_EXTENSION))
				|| (APP_IMAGE_PNG_CONTENT_TYPE.equalsIgnoreCase(fileContentType)
						&& fileName.toLowerCase().endsWith(PNG_FILE_EXTENSION))
				|| (APP_IMAGE_JPEG_CONTENT_TYPE.equalsIgnoreCase(fileContentType)
						&& fileName.toLowerCase().endsWith(JPG_FILE_EXTENSION)))
		{
			LOG.debug("File format found!");
		}
		else
		{
			errors.rejectValue(PDF_FILE_FIELD, "import.pdf.file.not.matched");
			return errors;
		}
		if (pdfFile.getSize() > getFileMaxSize())
		{
			errors.rejectValue(PDF_FILE_FIELD, "import.pdf.file.fileMaxSizeExceeded");
			return errors;
		}
		return errors;
	}

	protected long getFileMaxSize()
	{
		final String pdfMaxSize = asahiConfigurationService
				.getString(ApbStoreFrontContants.IMPORT_PDF_FILE_MAX_SIZE_BYTES_KEY + cmsSiteService.getCurrentSite().getUid(), "0");
		return Long.parseLong(pdfMaxSize);
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return ApbRequestRegisterForm.class.equals(aClass);
	}
}
