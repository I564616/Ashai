/**
 * 
 */
package com.apb.occ.v2.controllers;

import de.hybris.platform.apboccaddon.dto.category.ApbCategoryListWsDTO;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.product.CategoryWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.category.ApbCategoryFacade;
import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.apb.occ.v2.validators.ApbCategoryWsDTOValidator;


/**
 * The Class AsahiCategoryController.
 * 
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/category")
@ApiVersion("v2")
public class AsahiCategoryController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiCategoryController.class);

	/** The apb category facade. */
	@Resource(name = "apbCategoryFacade")
	private ApbCategoryFacade apbCategoryFacade;

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "apbCategoryWsDTOValidator")
	private ApbCategoryWsDTOValidator apbCategoryWsDTOValidator;

	/**
	 * @return the apbCategoryWsDTOValidator
	 */
	public ApbCategoryWsDTOValidator getApbCategoryWsDTOValidator()
	{
		return apbCategoryWsDTOValidator;
	}

	/**
	 * @param apbCategoryWsDTOValidator
	 *           the apbCategoryWsDTOValidator to set
	 */
	public void setApbCategoryWsDTOValidator(final ApbCategoryWsDTOValidator apbCategoryWsDTOValidator)
	{
		this.apbCategoryWsDTOValidator = apbCategoryWsDTOValidator;
	}

	/**
	 * Import categories.
	 * 
	 * @param category
	 *           the category
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importCategories", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importCategories(@RequestBody final ApbCategoryListWsDTO category) throws WebserviceValidationException
	{
		logger.debug("Importing Categories into hybris");

		if (CollectionUtils.isNotEmpty(category.getCategory()))
		{
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final CategoryWsDTO categoryWS : category.getCategory())
			{
				logger.debug("Importing Category with Code: " + categoryWS.getCode());

				final Errors errors = new BeanPropertyBindingResult(categoryWS, "categoryWS");
				this.getApbCategoryWsDTOValidator().validate(categoryWS, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
					this.apbCategoryFacade.importCategory(this.dataMapper.map(categoryWS, CategoryData.class));
				}

				logger.debug("Category with code: " + categoryWS.getCode() + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				throw new AsahiWebServiceException(errorList);
			}
		}
	}
}
