/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.facades.product.data.HeirarchyLevelData;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.product.Material.GeneralData;


/**
 * The Class ProductHeirarchyLevelsPopulator.
 */
public class ProductHeirarchyLevelsPopulator implements Populator<Material, ProductData>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ProductHeirarchyLevelsPopulator.class);

	/** The level1 end position. */
	@Value(value = "${product.level1HierarchyEndPosition:4}")
	private int level1EndPosition;

	/** The level2 end position. */
	@Value(value = "${product.level2HierarchyEndPosition:7}")
	private int level2EndPosition;

	/** The level3 end position. */
	@Value(value = "${product.level3HierarchyEndPosition:10}")
	private int level3EndPosition;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Material source, final ProductData target) throws ConversionException
	{
		if (CollectionUtils.isNotEmpty(source.getGeneralData()))
		{
			final GeneralData generalData = source.getGeneralData().get(0);
			final String productHeirarchy = generalData.getProductHierarchy();

			if (StringUtils.isNotBlank(productHeirarchy))
			{
				final String level1HierarchyCode = productHeirarchy.substring(0, level1EndPosition);
				final String level2HierarchyCode = productHeirarchy.substring(level1EndPosition, level2EndPosition);
				final String level3HierarchyCode = productHeirarchy.substring(level2EndPosition, level3EndPosition);

				final HeirarchyLevelData heirarchyLevel1 = new HeirarchyLevelData();
				heirarchyLevel1.setCode(level1HierarchyCode);
				heirarchyLevel1.setLevel(1);
				heirarchyLevel1.setName(getLevel1Name(generalData));

				final HeirarchyLevelData heirarchyLevel2 = new HeirarchyLevelData();
				heirarchyLevel2.setCode(level2HierarchyCode);
				heirarchyLevel2.setLevel(2);
				heirarchyLevel2.setName(getLevel2Name(generalData));

				final HeirarchyLevelData heirarchyLevel3 = new HeirarchyLevelData();
				heirarchyLevel3.setCode(level3HierarchyCode);
				heirarchyLevel3.setLevel(3);
				heirarchyLevel3.setName(getLevel3Name(generalData));

				final List<HeirarchyLevelData> heirarchyLevels = new ArrayList<HeirarchyLevelData>();
				heirarchyLevels.add(heirarchyLevel1);
				heirarchyLevels.add(heirarchyLevel2);
				heirarchyLevels.add(heirarchyLevel3);

				target.setHierarchy(productHeirarchy);
				target.setHeirarchyLevels(heirarchyLevels);
				target.setLevel1Hierarchy(level1HierarchyCode);
				target.setLevel2Hierarchy(level2HierarchyCode);
				target.setLevel3Hierarchy(level3HierarchyCode);

				final CategoryData topLevelCategory = new CategoryData();
				topLevelCategory.setCode(Config.getString("product.sapCategoryCode", "sap"));
				topLevelCategory.setName(Config.getString("product.sapCategoryName", "SAP"));

				final CategoryData level1Category = new CategoryData();
				level1Category.setCode(heirarchyLevel1.getCode());
				level1Category.setName(heirarchyLevel1.getName());
				level1Category.setParent(topLevelCategory);

				final CategoryData level2Category = new CategoryData();
				level2Category.setCode(heirarchyLevel2.getCode());
				level2Category.setName(heirarchyLevel2.getName());
				level2Category.setParent(level1Category);

				final CategoryData level3Category = new CategoryData();
				level3Category.setCode(heirarchyLevel3.getCode());
				level3Category.setName(heirarchyLevel3.getName());
				level3Category.setParent(level2Category);

				target.setParent(level3Category);

				LOG.debug("After setting hierarchy. Level 1 => {} : {}, Level 2 => {} : {}, Level 3 => {} : {}",
						heirarchyLevel1.getCode(), heirarchyLevel1.getName(), heirarchyLevel2.getCode(), heirarchyLevel2.getName(),
						heirarchyLevel3.getCode(), heirarchyLevel3.getName());
			}
		}
		else
		{
			LOG.warn("Product hierarchy is null or empty for product [{}]", target.getCode());
		}

	}

	/**
	 * Gets the level1 name.
	 *
	 * @param generalData
	 *           the general data
	 * @return the level1 name
	 */
	protected String getLevel1Name(final GeneralData generalData)
	{
		return getLevelName(generalData, BigInteger.ONE);
	}

	/**
	 * Gets the level2 name.
	 *
	 * @param generalData
	 *           the general data
	 * @return the level2 name
	 */
	protected String getLevel2Name(final GeneralData generalData)
	{
		return getLevelName(generalData, BigInteger.valueOf(2));
	}

	/**
	 * Gets the level3 name.
	 *
	 * @param generalData
	 *           the general data
	 * @return the level3 name
	 */
	protected String getLevel3Name(final GeneralData generalData)
	{
		return getLevelName(generalData, BigInteger.valueOf(3));
	}

	/**
	 * Gets the level name.
	 *
	 * @param generalData
	 *           the general data
	 * @param level
	 *           the level
	 * @return the level name
	 */
	protected String getLevelName(final GeneralData generalData, final BigInteger level)
	{
		if (generalData.getProductHierarchyLevels() != null)
		{
			for (final Material.GeneralData.ProductHierarchyLevels eachLevel : generalData.getProductHierarchyLevels())
			{
				if (StringUtils.isNotBlank(eachLevel.getLevel()) && level != null
						&& level.compareTo(new BigInteger(eachLevel.getLevel())) == 0)
				{
					return eachLevel.getHierarchyDescFull();
				}
			}
		}
		return null;
	}
}
