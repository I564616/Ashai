/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealResultFreeGoodData;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.complexdeals.DealCondition.FreeGoods;
import com.sabmiller.webservice.product.util.SapHybrisUnitOfMeasureMapper;


/**
 * @author joshua.a.antony
 *
 */
public class ResultFreeGoodPopulator implements Populator<DealCondition, ComplexDealData>
{

	private static final Logger LOG = LoggerFactory.getLogger(ResultFreeGoodPopulator.class);

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Resource(name = "sapHybrisUnitOfMeasureMapper")
	private SapHybrisUnitOfMeasureMapper mapper;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final DealCondition source, final ComplexDealData target) throws ConversionException
	{
		final List<ComplexDealResultFreeGoodData> freeGoodResults = new ArrayList<ComplexDealResultFreeGoodData>();
		for (final FreeGoods eachFreeGoods : CollectionUtils.emptyIfNull(source.getFreeGoods()))
		{
			final ComplexDealResultFreeGoodData freeGoodResult = new ComplexDealResultFreeGoodData();
			freeGoodResult.setAmounts(BooleanUtils.toBoolean(eachFreeGoods.isAmounts()));
			freeGoodResult.setFreeGoodsFlag(BooleanUtils.toBoolean(eachFreeGoods.isFreeGoodsFlag()));
			freeGoodResult.setMaterial(eachFreeGoods.getMaterial());
			freeGoodResult.setQuantity(eachFreeGoods.getTargetQty());
			freeGoodResult.setScale(eachFreeGoods.getScale());
			freeGoodResult.setSequenceNumber(eachFreeGoods.getSequenceNumber());
			freeGoodResult.setUom(mapper.getHybrisUomCode(eachFreeGoods.getUnitOfMeasure()));

			freeGoodResults.add(freeGoodResult);

			//LOG.info("In populate(). Added Free Good : [{}] to the list ", ReflectionToStringBuilder.toString(freeGoodResult));
		}
		target.setResultFreeGood(freeGoodResults);
	}
}
