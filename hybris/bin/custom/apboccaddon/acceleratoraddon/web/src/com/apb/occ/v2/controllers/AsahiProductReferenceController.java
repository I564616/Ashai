/**
 * 
 */
package com.apb.occ.v2.controllers;

import de.hybris.platform.apboccaddon.dto.product.AlcoholTypeListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.AlcoholTypeWsDTO;
import de.hybris.platform.apboccaddon.dto.product.BrandListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.BrandWsDTO;
import de.hybris.platform.apboccaddon.dto.product.FlavourListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.FlavourWsDTO;
import de.hybris.platform.apboccaddon.dto.product.ItemGroupListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.ItemGroupWsDTO;
import de.hybris.platform.apboccaddon.dto.product.PackageTypeListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.PackageTypeWsDTO;
import de.hybris.platform.apboccaddon.dto.product.ProductGroupListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.ProductGroupWsDTO;
import de.hybris.platform.apboccaddon.dto.product.SubProductGroupListWsDTO;
import de.hybris.platform.apboccaddon.dto.product.SubProductGroupWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.product.AsahiProductRefernceFacade;
import com.apb.facades.product.data.AlcoholTypeData;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.apb.facades.product.data.ItemGroupData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.ProductGroupData;
import com.apb.facades.product.data.SubProductGroupData;


/**
 * @author Kuldeep.Singh1
 * 
 */
@RestController
@RequestMapping(value = "/{baseSiteId}/productReference")
@ApiVersion("v2")
public class AsahiProductReferenceController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiProductReferenceController.class);

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/** The asahi product refernce facade. */
	@Resource(name = "asahiProductRefernceFacade")
	private AsahiProductRefernceFacade asahiProductRefernceFacade;

	/**
	 * Import product groups.
	 * 
	 * @param productGroup
	 *           the product group
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importProductGroup", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importProductGroups(@RequestBody final ProductGroupListWsDTO productGroup) throws WebserviceValidationException
	{
		logger.debug("Importing ProductGroups into hybris");

		if (CollectionUtils.isNotEmpty(productGroup.getProductGroup()))
		{
			for (final ProductGroupWsDTO productGroupWS : productGroup.getProductGroup())
			{

				logger.debug("Importing ProductGroup with Code: " + productGroupWS.getCode());

				this.asahiProductRefernceFacade.importProductGroup(this.dataMapper.map(productGroupWS, ProductGroupData.class));

				logger.debug("ProductGroup with code: " + productGroupWS.getCode() + " is imported");
			}
		}
	}

	/**
	 * Import alcohol types.
	 * 
	 * @param alcoholType
	 *           the alcohol type
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importAlcoholType", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importAlcoholTypes(@RequestBody final AlcoholTypeListWsDTO alcoholType) throws WebserviceValidationException
	{
		logger.debug("Importing AlcoholTypes into hybris");

		if (CollectionUtils.isNotEmpty(alcoholType.getAlcoholTypes()))
		{
			for (final AlcoholTypeWsDTO alcoholTypeWS : alcoholType.getAlcoholTypes())
			{
				logger.debug("Importing AlcoholType with Code: " + alcoholTypeWS.getCode());

				this.asahiProductRefernceFacade.importAlcoholType(this.dataMapper.map(alcoholTypeWS, AlcoholTypeData.class));

				logger.debug("AlcoholType with code: " + alcoholTypeWS.getCode() + " is imported");
			}
		}

	}

	/**
	 * Import package types.
	 * 
	 * @param packageType
	 *           the package type
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importPackageType", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importPackageTypes(@RequestBody final PackageTypeListWsDTO packageType) throws WebserviceValidationException
	{
		logger.debug("Importing PackageTypes into hybris");

		if (CollectionUtils.isNotEmpty(packageType.getPackageType()))
		{
			for (final PackageTypeWsDTO packageTypeWS : packageType.getPackageType())
			{

				logger.debug("Importing PackageType with Code: " + packageTypeWS.getCode());

				this.asahiProductRefernceFacade.importPackageType(this.dataMapper.map(packageTypeWS, PackageTypeData.class));

				logger.debug("PackageType with code: " +  packageTypeWS.getCode() + " is imported");
			}
		}

	}

	/**
	 * Import flavours.
	 * 
	 * @param flavour
	 *           the flavour
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importFlavour", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importFlavours(@RequestBody final FlavourListWsDTO flavour) throws WebserviceValidationException
	{
		logger.debug("Importing Flavour into hybris");

		if (CollectionUtils.isNotEmpty(flavour.getFlavour()))
		{
			for (final FlavourWsDTO flavourWS : flavour.getFlavour())
			{
				logger.debug("Importing Flavour with Code: " + flavourWS.getCode());

				this.asahiProductRefernceFacade.importFlavour(this.dataMapper.map(flavourWS, FlavourData.class));

				logger.debug("Flavour with code: " + flavourWS.getCode() + " is imported");
			}
		}
	}

	/**
	 * Import brands.
	 * 
	 * @param brand
	 *           the brand
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importBrand", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importBrands(@RequestBody final BrandListWsDTO brand) throws WebserviceValidationException
	{
		logger.debug("Importing Brands into hybris");

		if (CollectionUtils.isNotEmpty(brand.getBrand()))
		{
			for (final BrandWsDTO brandWS : brand.getBrand())
			{
				logger.debug("Importing Brand with Code: " + brandWS.getCode());

				this.asahiProductRefernceFacade.importBrand(this.dataMapper.map(brandWS, BrandData.class));

				logger.debug("Brand with code: " + brandWS.getCode() + " is imported");
			}
		}
	}

	/**
	 * Import item group.
	 * 
	 * @param itemGroup
	 *           the item group
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importItemGroup", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importItemGroup(@RequestBody final ItemGroupListWsDTO itemGroup) throws WebserviceValidationException
	{
		logger.debug("Importing itemGroups into hybris");

		if (CollectionUtils.isNotEmpty(itemGroup.getItemGroup()))
		{
			for (final ItemGroupWsDTO itemGroupWS : itemGroup.getItemGroup())
			{
				logger.debug("Importing itemGroup with Code: " + itemGroupWS.getCode());

				this.asahiProductRefernceFacade.importItemGroup(this.dataMapper.map(itemGroupWS, ItemGroupData.class));

				logger.debug("itemGroup with code: " + itemGroupWS.getCode() + " is imported");
			}
		}

	}

	/**
	 * Import unit volume.
	 * 
	 * @param subProductGroup
	 *           the sub product group
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importSubProductGroup", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importSubProductGroup(@RequestBody final SubProductGroupListWsDTO subProductGroup)
			throws WebserviceValidationException
	{
		logger.debug("Importing SubProductGroup into hybris");

		if (CollectionUtils.isNotEmpty(subProductGroup.getSubProductGroup()))
		{
			for (final SubProductGroupWsDTO subProductGroupWS : subProductGroup.getSubProductGroup())
			{
				logger.debug("Importing SubProductGroup with Code: " + subProductGroupWS.getCode());

				this.asahiProductRefernceFacade.importSubProductGroup(this.dataMapper.map(subProductGroupWS,
						SubProductGroupData.class));

				logger.debug("SubProductGroup with code: " +  subProductGroupWS.getCode() + " is imported");
			}
		}

	}
}
