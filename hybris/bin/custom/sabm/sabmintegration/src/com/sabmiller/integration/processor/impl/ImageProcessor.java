package com.sabmiller.integration.processor.impl;


import com.sabmiller.integration.imagesimport.SABMImageImportRequestHandler;
import com.sabmiller.integration.imagesimport.pojo.SkuVantageResponseItem;
import com.sabmiller.integration.media.strategies.MasterMediaCreationStrategy;
import com.sabmiller.integration.processor.AbstractThreadProcessor;
import com.sabmiller.integration.processor.exception.ValidatorException;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.strategy.CatalogVersionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The Class ImageProcessor.
 */
public class ImageProcessor extends AbstractThreadProcessor<ProductModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);

	/** The master media creation strategy. */
	@Resource(name = "masterMediaCreationStrategy")
	MasterMediaCreationStrategy masterMediaCreationStrategy;

	/** The sabm image import request handler. */
	@Resource
	private SABMImageImportRequestHandler sabmImageImportRequestHandler;

	/** The catalog version strategy. */
	private CatalogVersionStrategy catalogVersionStrategy;


	/**
	 * Import media.
	 *
	 * @param productModel
	 *           the product model
	 */
	@Override
	protected void executeProcessor(final ProductModel productModel)
	{
		if (productModel != null)
		{
			try
			{
				catalogVersionStrategy.setSessionCatalogVersion();

				LOG.debug("Running importMedia for product: {}", productModel.getCode());

				final SkuVantageResponseItem[] responseItems = sabmImageImportRequestHandler
						.sendGetRequestSingleProduct(productModel.getEan());

				if (ArrayUtils.isNotEmpty(responseItems))
				{
					//The result will always contains max one item
					processImageItem(responseItems[0], productModel);
				}
			}
			catch (final SABMIntegrationException e)
			{
				LOG.error("Error calling external provider for image's URL" + e, e);
			}
		}

	}

	/**
	 * Process image item.
	 *
	 * @param imageItem
	 *           the image item
	 * @param productModel
	 *           the product model
	 */
	protected void processImageItem(final SkuVantageResponseItem imageItem, final ProductModel productModel)
	{
		if (imageItem != null && productModel != null)
		{
			final Map<String, String> imageUrls = retrieveImageUrls(imageItem, productModel.getEan());

			if (MapUtils.isNotEmpty(imageUrls))
			{
				for(final Map.Entry<String, String> entry : imageUrls.entrySet()){
                    masterMediaCreationStrategy.createMasterMediaProduct(productModel, catalogVersionStrategy.getCatalogVersion(),
                            entry.getKey(), entry.getValue());
				}

			}
		}
	}

	/**
	 * Retrieve image urls.
	 *
	 * @param imageItem
	 *           the image item
	 * @param code
	 *           the code
	 * @return the map
	 */
	protected Map<String, String> retrieveImageUrls(final SkuVantageResponseItem imageItem, final String code)
	{
		final Map<String, String> map = new LinkedHashMap<>();

		if (imageItem != null)
		{
			for (int i = 0; i <= 20; i++)
			{
				try
				{
					final Object property = PropertyUtils.getProperty(imageItem, "original" + i);

					if (property instanceof String && StringUtils.isNotEmpty((String) property))
					{
						map.put(code + "-" + i, (String) property);
					}

				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					LOG.warn("Unable to find property: " + "original" + i + " in object: " + imageItem + " - " + e);
				}
			}
		}

		return map;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.processor.AbstractProcessor#validate(java.lang.Object)
	 */
	@Override
	protected void validate(final ProductModel imageItem) throws ValidatorException
	{
		validateAttribute(imageItem.getEan(), "Missing 'product ean'");
	}

	/**
	 * Sets the catalog version strategy.
	 *
	 * @param catalogVersionStrategy
	 *           the catalogVersionStrategy to set
	 */
	public void setCatalogVersionStrategy(final CatalogVersionStrategy catalogVersionStrategy)
	{
		this.catalogVersionStrategy = catalogVersionStrategy;
	}

}
