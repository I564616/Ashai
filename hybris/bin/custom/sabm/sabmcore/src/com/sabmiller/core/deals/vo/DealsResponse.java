/**
 *
 */
package com.sabmiller.core.deals.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.sap.deals.bogof.response.PricingBOGOFDealsResponse;
import com.sabmiller.integration.sap.deals.bogof.response.PricingBOGOFDealsResponse.PricingBOGOFDealsResponseItem;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse.PricingDiscountConditionsItem;


/**
 * @author joshua.a.antony
 *
 */
public class DealsResponse
{
	private static final Logger LOG = LoggerFactory.getLogger(DealsResponse.class);

	public DealsResponse(final PricingDiscountConditionsResponse response)
	{
		this.customer = response.getPricingDiscountConditionsHeader().getCustomer();
		this.salesOrganisation = response.getPricingDiscountConditionsHeader().getSalesOrganisation();
		final List<DealItem> items = new ArrayList<DealItem>();
		for (final PricingDiscountConditionsItem eachItem : response.getPricingDiscountConditionsItem())
		{
			LOG.debug("PricingDiscountConditionsItem : {} ", eachItem);

			final DealItem item = new DealItem();
			item.setAmount(eachItem.getAmount());
			item.setCalcType(eachItem.getCalcType());
			item.setConditionType(eachItem.getConditionType());
			item.setMaterial(eachItem.getMaterial());
			item.setMinimumQuantity(deriveMinQty(eachItem.getMinimumQuantity()));
			item.setPriority(eachItem.getPriority());
			item.setSaleUnit(eachItem.getSaleUnit());
			item.setUnit(eachItem.getUnit());
			item.setUnitOfMeasure(eachItem.getUnitOfMeasure());
			item.setUnitOfMeasure2(eachItem.getUnitOfMeasure2());
			item.setValidFrom(eachItem.getValidFrom());
			item.setValidTo(eachItem.getValidTo());
			item.setMaxConditionBaseValue(SabmUtils.sapToHybrisDouble(eachItem.getMaxConditionBaseValue()));
			item.setMaxConditionValue(SabmUtils.sapToHybrisDouble(eachItem.getMaxConditionValue()));
			item.setMaxNumberOfOrders(SabmUtils.sapToHybrisDouble(eachItem.getMaxNumberOfOrders()));
			item.setUsedConditionBaseValue(SabmUtils.sapToHybrisDouble(eachItem.getUsedConditionBaseValue()));
			item.setUsedConditionValue(SabmUtils.sapToHybrisDouble(eachItem.getUsedConditionValue()));
			item.setUsedNumberOfOrders(SabmUtils.sapToHybrisDouble(eachItem.getUsedNumberOfOrders()));

			LOG.debug("DealItem : {} ", item);
			items.add(item);
		}
		this.items = items;

	}

	public DealsResponse(final CustomerUnitPricingResponse response)
	{
		this.customer = response.getCustomerUnitPricingResponseHeader().getCustomerID();
		this.salesOrganisation = response.getCustomerUnitPricingResponseHeader().getSalesOrganisation();
		final List<DealItem> items = new ArrayList<DealItem>();
		for (final CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse eachItem :
				(response.getCustomerUnitPricingDiscountResponse()))
		{
			LOG.debug("PricingDiscountConditionsItem : {} ", eachItem);
			CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse customerUnitPricingDiscountResponseObj = eachItem;
			if(customerUnitPricingDiscountResponseObj.getPricingDiscountConditionsItem() == null){
				continue;
			}
			for(final CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse.PricingDiscountConditionsItem discountItem : eachItem.getPricingDiscountConditionsItem())
			{
				final DealItem item = new DealItem();
				item.setAmount(discountItem.getAmount());
				item.setCalcType(discountItem.getCalcType());
				item.setConditionType(discountItem.getConditionType());
				item.setMaterial(eachItem.getMaterialID());
				item.setMinimumQuantity(deriveMinQty(discountItem.getMinimumQuantity()));
				item.setPriority(discountItem.getPriority());
				item.setSaleUnit(eachItem.getSaleUnit());
				item.setUnit(eachItem.getUnit());
				item.setUnitOfMeasure(eachItem.getUnitOfMeasure());
				item.setUnitOfMeasure2(eachItem.getUnitOfMeasure2());
				item.setValidFrom(discountItem.getValidFrom());
				item.setValidTo(discountItem.getValidTo());
				item.setMaxConditionBaseValue(SabmUtils.sapToHybrisDouble(discountItem.getMaxConditionBaseValue()));
				item.setMaxConditionValue(SabmUtils.sapToHybrisDouble(discountItem.getMaxConditionValue()));
				item.setMaxNumberOfOrders(SabmUtils.sapToHybrisDouble(discountItem.getMaxNumberOfOrders()));
				item.setUsedConditionBaseValue(SabmUtils.sapToHybrisDouble(discountItem.getUsedConditionBaseValue()));
				item.setUsedConditionValue(SabmUtils.sapToHybrisDouble(discountItem.getUsedConditionValue()));
				item.setUsedNumberOfOrders(SabmUtils.sapToHybrisDouble(discountItem.getUsedNumberOfOrders()));

				LOG.debug("DealItem : {} ", item);

				items.add(item);
			}
		}
		this.items = items;
	}

	public DealsResponse(final PricingBOGOFDealsResponse response)
	{
		this.customer = response.getPricingBOGOFDealsResponseHeader().getCustomer();
		this.salesOrganisation = response.getPricingBOGOFDealsResponseHeader().getSalesOrganisation();
		final List<DealItem> items = new ArrayList<DealItem>();
		for (final PricingBOGOFDealsResponseItem eachItem : response.getPricingBOGOFDealsResponseItem())
		{
			if (isInvalidBogofItem(eachItem))
			{
				continue;
			}
			final DealItem item = new DealItem();
			item.setConditionType(eachItem.getConditionType());
			item.setMaterial(eachItem.getMaterial());
			item.setMinimumQuantity(deriveMinQty(eachItem.getMinimumQuantity()));
			item.setFreeGoodsQty(eachItem.getFreeGoodsQty());
			item.setUnitOfMeasure(eachItem.getUnitOfMeasure());
			item.setAdditionalQtyFreeGoods(eachItem.getAdditionalQtyFreeGoods());
			item.setAdditionalUnitOfMeasure(eachItem.getAdditionalUnitOfMeasure());
			item.setRule(eachItem.getRule());
			item.setAdditionalMaterial(eachItem.getAdditionalMaterial());
			item.setValidFrom(eachItem.getValidFrom());
			item.setValidTo(eachItem.getValidTo());
			item.setPriority(eachItem.getPriority());

			items.add(item);
		}
		this.items = items;
	}

	/**
	 * For some reason, SAP sends these shitty data across and the inability of SAP to fix this leads to no option than
	 * to put this buggy implementation
	 */
	private boolean isInvalidBogofItem(final PricingBOGOFDealsResponseItem eachItem)
	{
		try
		{
			if ("YTPM".equals(eachItem.getConditionType()) && StringUtils.isNotBlank(eachItem.getPriority()))
			{
				final int priority = Integer.parseInt(SabmStringUtils.stripLeadingZeroes(eachItem.getPriority()));
				if (priority >= 15 && priority <= 50)
				{
					return true;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error determining if the deal is invalid ", e);
		}

		return false;
	}

	private int deriveMinQty(final String minimumQuantity)
	{
		final int minQty = Double.valueOf(minimumQuantity.trim()).intValue();
		return minQty == 0 ? 1 : minQty;
	}

	protected String customer;
	protected String salesOrganisation;
	private List<DealItem> items;

	public static class DealItem
	{
		protected String material;
		protected String conditionType;
		protected String amount;
		protected String unit;
		protected String saleUnit;
		protected String unitOfMeasure;
		protected int minimumQuantity;
		protected String unitOfMeasure2;
		protected XMLGregorianCalendar validFrom;
		protected XMLGregorianCalendar validTo;
		protected String calcType;
		protected String priority;
		protected String freeGoodsQty;
		protected String additionalQtyFreeGoods;
		protected String additionalUnitOfMeasure;
		protected String rule;
		protected String additionalMaterial;
		protected Double maxConditionValue;
		protected Double maxConditionBaseValue;
		protected Double maxNumberOfOrders;
		protected Double usedConditionValue;
		protected Double usedConditionBaseValue;
		protected Double usedNumberOfOrders;

		@Override
		public String toString()
		{
			return ReflectionToStringBuilder.toString(this);
		}


		public String getMaterial()
		{
			return material;
		}

		public void setMaterial(final String material)
		{
			this.material = material;
		}

		public String getConditionType()
		{
			return conditionType;
		}

		public void setConditionType(final String conditionType)
		{
			this.conditionType = conditionType;
		}

		public String getAmount()
		{
			return amount;
		}

		public void setAmount(final String amount)
		{
			this.amount = amount;
		}

		public String getUnit()
		{
			return unit;
		}

		public void setUnit(final String unit)
		{
			this.unit = unit;
		}

		public String getSaleUnit()
		{
			return saleUnit;
		}

		public void setSaleUnit(final String saleUnit)
		{
			this.saleUnit = saleUnit;
		}

		public String getUnitOfMeasure()
		{
			return unitOfMeasure;
		}

		public void setUnitOfMeasure(final String unitOfMeasure)
		{
			this.unitOfMeasure = unitOfMeasure;
		}

		public int getMinimumQuantity()
		{
			return minimumQuantity;
		}

		public void setMinimumQuantity(final int minimumQuantity)
		{
			this.minimumQuantity = minimumQuantity;
		}

		public String getUnitOfMeasure2()
		{
			return unitOfMeasure2;
		}

		public void setUnitOfMeasure2(final String unitOfMeasure2)
		{
			this.unitOfMeasure2 = unitOfMeasure2;
		}

		public XMLGregorianCalendar getValidFrom()
		{
			return validFrom;
		}

		public void setValidFrom(final XMLGregorianCalendar validFrom)
		{
			this.validFrom = validFrom;
		}

		public XMLGregorianCalendar getValidTo()
		{
			return validTo;
		}

		public void setValidTo(final XMLGregorianCalendar validTo)
		{
			this.validTo = validTo;
		}

		public String getCalcType()
		{
			return calcType;
		}

		public void setCalcType(final String calcType)
		{
			this.calcType = calcType;
		}

		public String getPriority()
		{
			return priority;
		}

		public void setPriority(final String priority)
		{
			this.priority = priority;
		}

		public String getFreeGoodsQty()
		{
			return freeGoodsQty;
		}

		public void setFreeGoodsQty(final String freeGoodsQty)
		{
			this.freeGoodsQty = freeGoodsQty;
		}

		public String getAdditionalQtyFreeGoods()
		{
			return additionalQtyFreeGoods;
		}

		public void setAdditionalQtyFreeGoods(final String additionalQtyFreeGoods)
		{
			this.additionalQtyFreeGoods = additionalQtyFreeGoods;
		}

		public String getAdditionalUnitOfMeasure()
		{
			return additionalUnitOfMeasure;
		}

		public void setAdditionalUnitOfMeasure(final String additionalUnitOfMeasure)
		{
			this.additionalUnitOfMeasure = additionalUnitOfMeasure;
		}

		public String getRule()
		{
			return rule;
		}

		public void setRule(final String rule)
		{
			this.rule = rule;
		}

		public String getAdditionalMaterial()
		{
			return additionalMaterial;
		}

		public void setAdditionalMaterial(final String additionalMaterial)
		{
			this.additionalMaterial = additionalMaterial;
		}

		public Double getMaxConditionValue()
		{
			return maxConditionValue;
		}

		public void setMaxConditionValue(final Double maxConditionValue)
		{
			this.maxConditionValue = maxConditionValue;
		}

		public Double getMaxConditionBaseValue()
		{
			return maxConditionBaseValue;
		}

		public void setMaxConditionBaseValue(final Double maxConditionBaseValue)
		{
			this.maxConditionBaseValue = maxConditionBaseValue;
		}

		public Double getMaxNumberOfOrders()
		{
			return maxNumberOfOrders;
		}

		public void setMaxNumberOfOrders(final Double maxNumberOfOrders)
		{
			this.maxNumberOfOrders = maxNumberOfOrders;
		}

		public Double getUsedConditionValue()
		{
			return usedConditionValue;
		}

		public void setUsedConditionValue(final Double usedConditionValue)
		{
			this.usedConditionValue = usedConditionValue;
		}

		public Double getUsedConditionBaseValue()
		{
			return usedConditionBaseValue;
		}

		public void setUsedConditionBaseValue(final Double usedConditionBaseValue)
		{
			this.usedConditionBaseValue = usedConditionBaseValue;
		}

		public Double getUsedNumberOfOrders()
		{
			return usedNumberOfOrders;
		}

		public void setUsedNumberOfOrders(final Double usedNumberOfOrders)
		{
			this.usedNumberOfOrders = usedNumberOfOrders;
		}

	}

	public String getCustomer()
	{
		return customer;
	}

	public void setCustomer(final String customer)
	{
		this.customer = customer;
	}

	public String getSalesOrganisation()
	{
		return salesOrganisation;
	}

	public void setSalesOrganisation(final String salesOrganisation)
	{
		this.salesOrganisation = salesOrganisation;
	}

	public List<DealItem> getItems()
	{
		return items;
	}

	public void setItems(final List<DealItem> items)
	{
		this.items = items;
	}


}
