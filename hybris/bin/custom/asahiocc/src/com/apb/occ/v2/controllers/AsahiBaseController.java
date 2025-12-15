/**
 * 
 */
package com.apb.occ.v2.controllers;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.google.gson.Gson;

import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Asahi Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add
 * or overwrite the exception handler if needed.
 * 
 * @author Kuldeep.Singh1
 */
@Controller
public class AsahiBaseController
{
	private static final String TYPE = "ValidationError";
	private static final String SUBJECT_TYPE = "parameter";
	private static final String REASON_INVALID = "invalid";
	private static final String REASON_MISSING = "missing";

	@Autowired
    private AsahiConfigurationService asahiConfigurationService;

	private static final Logger LOGGER = LoggerFactory.getLogger("ShowIncomingData");

	/**
	 * Handle apb web service exception.
	 * 
	 * @param ex
	 *           the ex
	 * @return the error list ws DTO
	 */
	@ExceptionHandler(
	{ AsahiWebServiceException.class })
	public ErrorListWsDTO handleAsahiWebServiceException(final AsahiWebServiceException ex)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final List<ErrorWsDTO> errorList = new ArrayList<ErrorWsDTO>();

		if (CollectionUtils.isNotEmpty(ex.getValidationObject()))
		{
			for (final Object obj : ex.getValidationObject())
			{
				final Errors error = (Errors) obj;
				if (CollectionUtils.isNotEmpty(error.getFieldErrors()))
				{
					for (final FieldError fieldError : error.getFieldErrors())
					{
						final ErrorWsDTO errorWSDTO = new ErrorWsDTO();

						errorWSDTO.setMessage(fieldError.getDefaultMessage());
						errorWSDTO.setSubject(fieldError.getField());
						errorWSDTO.setType(TYPE);
						errorWSDTO.setSubjectType(SUBJECT_TYPE);
						errorWSDTO.setReason(fieldError.getRejectedValue() == null ? REASON_MISSING : REASON_INVALID);
						errorList.add(errorWSDTO);
					}
				}
			}
			errorListDto.setErrors(errorList);
		}
		return errorListDto;
	}

	protected <E> void showIncomingData(E t, String type){
        if(asahiConfigurationService.getBoolean("log.incoming.payload." + type, false)) {
            final Gson gson = new Gson();
            LOGGER.info(String.format("Interface type:: %s, incoming data:: %s", type, gson.toJson(t)));
        }
    }
}