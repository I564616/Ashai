/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.model.VideoMediaModel;
import com.sabmiller.core.b2b.dao.CUBMaxOrderQuantityDao;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.dao.SabmProductDao;
import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.stock.populators.CUBStockInformationPopulator;


/**
 * The SABMAlcoholVariantProductEANPopulator to populate the ProductData attributes from
 * SABMAlcoholVariantProductEANModel
 */
public class SABMAlcoholVariantProductEANPopulator implements Populator<ProductModel, ProductData>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAlcoholVariantProductEANPopulator.class);
	private static final String VIDEO = "video";

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils sabFormatterUtil;

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource
	private CUBStockInformationPopulator cubStockInformationPopulator;

	@Resource
	private SabmProductDao productDao;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;
	@Resource
	private UserService userService;
	@Resource
	private CUBMaxOrderQuantityDao cubMaxOrderQuantityDao;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * Populate the target instance from the source instance.
	 *
	 * @param source
	 * @param target
	 * @throws ConversionException
	 *
	 */
	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		if(asahiSiteUtil.isCub())
		{
   		if (source != null && target != null)
   		{
   			ProductModel variant = source;
   			SABMAlcoholVariantProductEANModel eanProduct = null;
   			//Checking if the source product is instance of SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
   			while (variant instanceof VariantProductModel)
   			{
   				if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
   				{
   					eanProduct = (SABMAlcoholVariantProductEANModel) variant;
   					break;
   				}

   				variant = ((VariantProductModel) variant).getBaseProduct();
   			}

   			if (eanProduct != null)
   			{
   				target.setContainer(eanProduct.getContainer());
   				target.setCapacity(eanProduct.getCapacity());
   				target.setPresentation(eanProduct.getPresentation());
   				target.setWeight(eanProduct.getWeight());
   				target.setWidth(eanProduct.getWidth());
   				target.setHeight(eanProduct.getHeight());
   				target.setLength(eanProduct.getLength());
   				target.setSizeUnit(eanProduct.getSizeUnit());
   				target.setEan(eanProduct.getEan());
   				target.setNewProductFlag(BooleanUtils.isTrue(eanProduct.getIsNewProduct()));
					target.setWetEligible(eanProduct.getWetEligible() != null ? eanProduct.getWetEligible() : false);
					target.setLevel4(eanProduct.getLevel4());
   				String productSku = null;

   				try
   				{
   					//productSku = productService.getMaterialCodeFromEan(eanProduct.getCode());

   					SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = null;
   					if (eanProduct.getLeadSku() != null)
   					{
   						sabmAlcoholVariantProductMaterialModel = eanProduct.getLeadSku();
   					}
   					else
   					{
   						//Lead Sku not avaialble,query for material whose base product is this ean
   						sabmAlcoholVariantProductMaterialModel = productDao.findMaterialProductByEan(eanProduct);
   					}

   					if (sabmAlcoholVariantProductMaterialModel != null)
   					{
   						productSku = sabmAlcoholVariantProductMaterialModel.getCode();
   						target.setLeadSkuId(sabFormatterUtil.formatSKU(productSku));
   						//Set CUB Stock Status
   						cubStockInformationPopulator.populate(productSku, target);
							//target.setMaxOrderQuantity(sabmAlcoholVariantProductMaterialModel.getMaxOrderQuantity());
   					}
						this.populateMaxOrderQty(eanProduct, target);
   				}
   				catch (final Exception e)
   				{
   					LOG.info("Attribute sku in not set: {}" + source);
   				}

   				if (eanProduct.getUnit() != null)
   				{
   					target.setUnit(eanProduct.getUnit().getName());
   				}
   				else
   				{
   					LOG.info("Attribute unit is null in ProductModel: {}" + source);
   				}

   				if (Config.getBoolean("show.deal.titles", true) && !asahiCoreUtil.isNAPUser())
   				{
   					try
   					{
   						target.setDealsFlag(false);
   						if (productSku != null)
   						{
   							final List<String> deals = sabmDealsSearchFacade.getDealsForProduct(productSku);
   						if (CollectionUtils.isNotEmpty(deals))
   						{
   								target.setDealsTitle(deals);
   							SabmStringUtils.getSortedDealTitles(deals);
   							target.setDealsFlag(true);
   						}
   						}
   					}
   					catch (final Exception e)
   					{
   						LOG.warn("exception while fetching deal info.");
   					}
   				}

   			}

   			final List<ImageData> videos = new ArrayList<>();
   			if (eanProduct != null && CollectionUtils.isNotEmpty(eanProduct.getOthers()))
   			{
   				for (final MediaModel video : eanProduct.getOthers())
   				{
   					if (video instanceof VideoMediaModel)
   					{

   						if (video.getMediaFormat() != null && video.getMediaFormat().getQualifier().equalsIgnoreCase(VIDEO))
   						{
   							final ImageData videodata = new ImageData();
   							videodata.setAltText(video.getAltText());
   							videodata.setUrl(video.getURL());
   							videodata.setGalleryIndex(((VideoMediaModel) video).getIndexLocation());
   							videodata.setFormat(VIDEO);
   							videos.add(videodata);
   						}
   					}
   				}
   				target.setVideos(videos);
   			}
   		}
   		else
   		{
   			LOG.error("Unable to populate a null source or null target");
   		}
		}
	}

	/**
	 * @param target
	 * @param source
	 *
	 */
	private void populateMaxOrderQty(final ProductModel source, final ProductData target)
	{
		final List<MaxOrderQtyModel> maxOrderQtyModels = this.cubMaxOrderQuantityDao
				.getCUBMaxOrderQuantityForProductCode(source.getCode());
		if (CollectionUtils.isNotEmpty(maxOrderQtyModels))
		{
			final MaxOrderQtyModel maxOrderQtyModel = this.productService.getMaxOrderQuantity(source);
			if (null != maxOrderQtyModel)
			{
				if (maxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled())
				{
					if (0 != ObjectUtils.defaultIfNull(maxOrderQtyModel.getDefaultAvgMaxOrderQty(), 0))
					{
						target.setMaxOrderQuantity(maxOrderQtyModel.getDefaultAvgMaxOrderQty());
					}
				}
				else
				{
					target.setMaxOrderQuantity(maxOrderQtyModel.getMaxOrderQty());
				}
				final Integer maxOrderRuleDays = Integer.parseInt(asahiConfigurationService
						.getString(ApbCoreConstants.CUB_MAX_ORDER_QTY_RULE_DAYS, ApbCoreConstants.DEFAULT_MAX_ORDER_QTY_RULE_DAYS));
				target.setMaxOrderQuantityDays(maxOrderRuleDays);
			}

		}
	}
}
