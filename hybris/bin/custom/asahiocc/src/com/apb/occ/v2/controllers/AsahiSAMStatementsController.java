/**
 * 
 */
package com.apb.occ.v2.controllers;

import java.util.ArrayList;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.sam.data.AsahiSAMStatementData;
import com.apb.facades.sam.statement.AsahiSAMStatementFacade;
import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.apb.occ.v2.validators.AsahiSAMStatementWsDTOValidator;

import de.hybris.platform.asahiocc.dto.sam.AsahiSAMStatementWsDTO;
import de.hybris.platform.asahiocc.dto.sam.AsahiSAMStatementsWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;


/**
 * The Class AsahiSAMInvoiceController.
 * 
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/statement")
@ApiVersion("v2")
public class AsahiSAMStatementsController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMStatementsController.class);

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/** The asahi SAM statement ws DTO validator. */
	@Resource(name = "asahiSAMStatementWsDTOValidator")
	private AsahiSAMStatementWsDTOValidator asahiSAMStatementWsDTOValidator;
	
	/** The asahi SAM statement facade. */
	@Resource
	private AsahiSAMStatementFacade asahiSAMStatementFacade;

	/**
	 * Gets the asahi SAM statement ws DTO validator.
	 *
	 * @param statements the statements
	 * @return the asahi SAM statement ws DTO validator
	 * @throws WebserviceValidationException the webservice validation exception
	 */
	/*public AsahiSAMStatementWsDTOValidator getAsahiSAMStatementWsDTOValidator() {
		return asahiSAMStatementWsDTOValidator;
	}*/

	/**
	 * Sets the asahi SAM statement ws DTO validator.
	 *
	 * @param asahiSAMStatementWsDTOValidator the new asahi SAM statement ws DTO validator
	 */
	/*public void setAsahiSAMStatementWsDTOValidator(
			AsahiSAMStatementWsDTOValidator asahiSAMStatementWsDTOValidator) {
		this.asahiSAMStatementWsDTOValidator = asahiSAMStatementWsDTOValidator;
	}*/


	/**
	 * Import statements
	 * 
	 * @param category
	 *           the category
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@RequestMapping(value = "/importStatements", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importStatements(@RequestBody final AsahiSAMStatementsWsDTO statements) throws WebserviceValidationException
	{
		logger.info("Importing statements into hybris");

		if (CollectionUtils.isNotEmpty(statements.getStatements()))
		{
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final AsahiSAMStatementWsDTO statement : statements.getStatements())
			{
				logger.info("Importing statement with number: " + statement.getStatementNumber());
				
				final Errors errors = new BeanPropertyBindingResult(statement, "statement");
				this.asahiSAMStatementWsDTOValidator.validate(statement, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
					this.asahiSAMStatementFacade.importStatements(this.dataMapper.map(statement, AsahiSAMStatementData.class));
				}

				logger.info("Statement with number: " + statement.getStatementNumber() + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				throw new AsahiWebServiceException(errorList);
			}
		}
	}
}
