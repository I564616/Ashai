package com.apb.occ.v2.controllers;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.facades.product.ApbProductFacade;
import com.apb.occ.v2.exception.AsahiWebServiceException;


/**
 * The Class AsahiProductsController.
 *
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/product")
@ApiVersion("v2")
public class AsahiProductsController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiProductsController.class);

	/** The product facade. */
	@Resource(name = "apbProductFacade")
	private ApbProductFacade productFacade;

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "apbProductWsDTOValidator")
	private Validator apbProductWsDTOValidator;

	/**
	 * Update products.
	 *
	 * @param product
	 *           the product
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@RequestMapping(value = "/importProducts", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public ResponseEntity<WebserviceException> importProducts(@RequestBody final ProductListWsDTO product, @PathVariable("baseSiteId") final String siteUid)
	{
	    showIncomingData(product, "product");
		logger.debug("Importing Products into hybris");

		if (CollectionUtils.isNotEmpty(product.getProducts()))
		{
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final ProductWsDTO productWS : product.getProducts())
			{
				logger.debug("Importing Product with code: " + ApbXSSEncoderUtil.encodeValue(productWS.getCode()));

				final Errors errors = new BeanPropertyBindingResult(productWS, "productWS");
				this.getApbProductWsDTOValidator().validate(productWS, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
					this.productFacade.importProducts(this.dataMapper.map(productWS, ProductData.class), siteUid);
				}

				logger.debug("Product with code:: " + ApbXSSEncoderUtil.encodeValue(productWS.getCode()) + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				return new ResponseEntity<>(new AsahiWebServiceException(errorList), HttpStatus.BAD_REQUEST);
			}
		}
		else
		{
			return new ResponseEntity<>(new RequestParameterException("Request is Not Valid", RequestParameterException.INVALID),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @return the apbProductWsDTOValidator
	 */
	public Validator getApbProductWsDTOValidator()
	{
		return apbProductWsDTOValidator;
	}

	/**
	 * @param apbProductWsDTOValidator
	 *           the apbProductWsDTOValidator to set
	 */
	public void setApbProductWsDTOValidator(final Validator apbProductWsDTOValidator)
	{
		this.apbProductWsDTOValidator = apbProductWsDTOValidator;
	}
}
