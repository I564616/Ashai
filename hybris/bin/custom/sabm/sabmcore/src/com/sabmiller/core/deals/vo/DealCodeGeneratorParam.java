/**
 *
 */
package com.sabmiller.core.deals.vo;

import java.util.Date;

import com.sabmiller.core.enums.DealTypeEnum;


/**
 * @author joshua.a.antony
 *
 */
public class DealCodeGeneratorParam
{
	private final String salesOrg;
	private final String customerId;
	private final String material;
	private final Date validFrom;
	private final Date validTo;
	private final int minQty;
	private final String uom;
	private final DealTypeEnum dealType;

	public DealCodeGeneratorParam(final Builder builder)
	{
		this.salesOrg = builder.salesOrg;
		this.customerId = builder.customerId;
		this.material = builder.material;
		this.validFrom = builder.validFrom;
		this.validTo = builder.validTo;
		this.minQty = builder.minQty;
		this.uom = builder.uom;
		this.dealType = builder.dealType;
	}

	public static class Builder
	{
		String salesOrg;
		String customerId;
		String material;
		Date validFrom;
		Date validTo;
		int minQty;
		String uom;
		DealTypeEnum dealType;

		public Builder(final String customerId, final String salesOrg)
		{
			this.customerId = customerId;
			this.salesOrg = salesOrg;
		}


		public Builder material(final String material)
		{
			this.material = material;
			return this;
		}

		public Builder validFrom(final Date validFrom)
		{
			this.validFrom = validFrom;
			return this;
		}

		public Builder validTo(final Date validTo)
		{
			this.validTo = validTo;
			return this;
		}

		public Builder minQty(final int minQty)
		{
			this.minQty = minQty;
			return this;
		}

		public Builder uom(final String uom)
		{
			this.uom = uom;
			return this;
		}

		public Builder dealType(final DealTypeEnum dealType)
		{
			this.dealType = dealType;
			return this;
		}

		public DealCodeGeneratorParam build()
		{
			return new DealCodeGeneratorParam(this);
		}

	}

	public String getSalesOrg()
	{
		return salesOrg;
	}

	public String getCustomerId()
	{
		return customerId;
	}

	public String getMaterial()
	{
		return material;
	}

	public Date getValidFrom()
	{
		return validFrom;
	}

	public Date getValidTo()
	{
		return validTo;
	}

	public int getMinQty()
	{
		return minQty;
	}

	public String getUom()
	{
		return uom;
	}

	public DealTypeEnum getDealType()
	{
		return dealType;
	}


}
