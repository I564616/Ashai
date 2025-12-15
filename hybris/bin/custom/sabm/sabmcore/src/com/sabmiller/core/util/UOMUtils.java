package com.sabmiller.core.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.product.data.UomData;

import de.hybris.platform.core.model.product.UnitModel;


/**
 * Created by evariz.p.papellero on 8/17/17.
 */
public class UOMUtils
{
	/**
	 * This will convert the unitModel into a unit data
	 *
	 * @param unitModel
	 *           the unit model to be converted
	 * @return UomData the created unit data
	 */
	public static UomData convertUom(final UnitModel unitModel)
	{
		final UomData uomData = new UomData();
		uomData.setCode(unitModel.getCode());
		uomData.setName(unitModel.getName());
		uomData.setPluralName(unitModel.getPluralName());
		return uomData;
	}


	public static List<UomData> getUomList(final Set<UnitModel> unitModels)
	{
		final List<UomData> unitLists = new LinkedList<>();

		//SABMC-1902 - if product have EA as UOM, then don't show other UOMs.
		final UnitModel drumUom = checkDrumUom(unitModels);
		if (drumUom != null)
		{
			final UomData uomData = UOMUtils.convertUom(drumUom);
			unitLists.add(uomData);
		}
		else
		{
			for (final UnitModel unitModel : unitModels)
			{
				if (!StringUtils.isBlank(unitModel.getName()) && !StringUtils.isBlank(unitModel.getCode()))
				{
					final UomData uomData = UOMUtils.convertUom(unitModel);


					unitLists.add(uomData);
				}
			}
		}
		return unitLists;
	}


	/**
	 * @param units
	 * @return
	 */
	private static UnitModel checkDrumUom(final Set<UnitModel> units)
	{
		for (final UnitModel uom : units)
		{
			if (StringUtils.equalsIgnoreCase(uom.getCode(), "EA"))
			{
				return uom;
			}
		}
		return null;
	}
}
