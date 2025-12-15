/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.europe1.constants.GeneratedEurope1Constants.Enumerations.UserPriceGroup;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.customer.ProductExclusionData;
import com.sabmiller.facades.customer.ProductExclusionImportData;
import com.sabmiller.webservice.productexclusion.ProductExclusionResponse;


/**
 * Convert Customer Unit Price (CUP) response to Hybris Model and persist the same. If Price row does not exist for the
 * product, a new one is created. The Price row is linked to the {@link SABMAlcoholVariantProductEANModel}. Also, the
 * {@link UserPriceGroup} is the customer Id
 *
 * @author joshua.a.antony
 */

public class ProductExclusionWsPopulator implements Populator<ProductExclusionResponse, ProductExclusionImportData>
{
	private static final Logger LOG = LoggerFactory.getLogger(ProductExclusionWsPopulator.class);

	@Resource(name = "modelService")
	private ModelService modelService;


	@Resource(name = "unitService")
	private SabmUnitService unitService;


	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;


	/** The date format. */
	@Value(value = "${sap.service.productexclusion.dateformat:yyyy-MM-dd}")
	private String dateFormat;






	@Override
	public void populate(final ProductExclusionResponse response, final ProductExclusionImportData target)
			throws ConversionException
	{
		final StringBuilder errors = new StringBuilder();
		LOG.debug("In convert(). Product Exclusions Response : " + response);
		if (response != null && response.getProductExclusionHeader() != null
				&& response.getProductExclusionHeader().getCustomer() != null)
		{
			final String customerId = response.getProductExclusionHeader().getCustomer();
			final Set<ProductExclusionData> productExclDataList = new HashSet<ProductExclusionData>();

			for (final ProductExclusionResponse.ProductExclusion item : ListUtils.emptyIfNull(response.getProductExclusion()))
			{
				LOG.debug("Each productExclsion item : " + item);

				final String product = StringUtils.trim(item.getMaterial());
				final Date validFrom = getDate(item.getValidFrom());
				final Date validTo = getDate(item.getValidTo());

				if (StringUtils.isNotBlank(product) && validFrom != null && validTo != null)
				{
					final ProductExclusionData productExcl = new ProductExclusionData();
					productExcl.setCustomer(customerId);
					productExcl.setProduct(product);
					productExcl.setValidFrom(validFrom);
					productExcl.setValidTo(validTo);
					productExclDataList.add(productExcl);
				}
				else
				{
					errors.append("Record::CustomerId-" + customerId + ";product-" + product + ";validFrom" + validFrom + ";validTo"
							+ validTo);
				}
			}
			target.setProductExclusionDataList(productExclDataList);
		}

		else
		{
			errors.append("Product Exclusion xml response or header blank from SAP");
		}
		target.setProcessingErrors(errors.toString());

	}

	private Date getDate(final String date)
	{
		try
		{
			return SabmDateUtils.getDate(StringUtils.trim(date), dateFormat);
		}
		catch (final ParseException e)
		{
			LOG.error("Error while parsing date in product exclusions");
		}
		return null;
	}

}
