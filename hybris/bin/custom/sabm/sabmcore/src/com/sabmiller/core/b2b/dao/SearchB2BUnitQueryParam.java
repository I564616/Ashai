/**
 *
 */
package com.sabmiller.core.b2b.dao;


import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.util.Objects;

/**
 * @author joshua.a.antony
 *
 */
public class SearchB2BUnitQueryParam implements java.io.Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	final String customer;
	final String salesOrgId;
	final String salesGroup;
	final String salesOffice;
	final String customerGroup;
	final String priceGroup;
	final String banner;
	final String primaryBanner;
	final String subBanner;
	final String division;
	final String distributionChannel;
	final String plant;
	final String subChannel;


	private SearchB2BUnitQueryParam(final Builder builder)
	{
		this.customer = builder.customer;
		this.salesOrgId = builder.salesOrgId;
		this.salesGroup = builder.salesGroup;
		this.salesOffice = builder.salesOffice;
		this.customerGroup = builder.customerGroup;
		this.priceGroup = builder.priceGroup;
		this.banner = builder.banner;
		this.primaryBanner = builder.primaryBanner;
		this.subBanner = builder.subBanner;
		this.division = builder.division;
		this.distributionChannel = builder.distributionChannel;
		this.plant = builder.plant;
		this.subChannel = builder.subChannel;
	}

	public static class Builder
	{
		private String customer;
		private String salesOrgId;
		private String salesGroup;
		private String salesOffice;
		private String customerGroup;
		private String priceGroup;
		private String banner;
		private String primaryBanner;
		private String subBanner;
		private String subChannel;
		private String division;
		private String distributionChannel;
		private String plant;

		public Builder customer(final String customer)
		{
			this.customer = customer;
			return this;
		}

		public Builder salesOrgId(final String salesOrgId)
		{
			this.salesOrgId = salesOrgId;
			return this;
		}

		public Builder salesGroup(final String salesGroup)
		{
			this.salesGroup = salesGroup;
			return this;
		}

		public Builder salesOffice(final String salesOffice)
		{
			this.salesOffice = salesOffice;
			return this;
		}

		public Builder customerGroup(final String customerGroup)
		{
			this.customerGroup = customerGroup;
			return this;
		}

		public Builder priceGroup(final String priceGroup)
		{
			this.priceGroup = priceGroup;
			return this;
		}

		public Builder banner(final String banner)
		{
			this.banner = banner;
			return this;
		}

		public Builder primaryBanner(final String primaryBanner)
		{
			this.primaryBanner = primaryBanner;
			return this;
		}

		public Builder subBanner(final String subBanner)
		{
			this.subBanner = subBanner;
			return this;
		}

		public Builder subChannel(final String subChannel)
		{
			this.subChannel = subChannel;
			return this;
		}

		public Builder division(final String division)
		{
			this.division = division;
			return this;
		}

		public Builder distributionChannel(final String distributionChannel)
		{
			this.distributionChannel = distributionChannel;
			return this;
		}

		public Builder plant(final String plant)
		{
			this.plant = plant;
			return this;
		}

		public SearchB2BUnitQueryParam build()
		{
			return new SearchB2BUnitQueryParam(this);
		}
	}

	/**
	 * Overridden so than, objects are the same if it generates the same keys
	 * @param other
	 * @return
	 */
	public boolean equals(final Object other){
		if(this == other){
			return true;
		}
		if(!(other instanceof SearchB2BUnitQueryParam)){
			return false;
		}

		final SearchB2BUnitQueryParam otherParam = (SearchB2BUnitQueryParam) other;

		return otherParam.toKey().equals(toKey());
	}

	/**
	 * Overridden so same keys have same hashCode
	 * @return
	 */
	@Override
	public int hashCode(){
		return Objects.hashCode(toKey());
	}

	/**
	 * @return the customer
	 */
	public String getCustomer()
	{
		return customer;
	}

	/**
	 * @return the salesOrgId
	 */
	public String getSalesOrgId()
	{
		return salesOrgId;
	}

	/**
	 * @return the salesGroup
	 */
	public String getSalesGroup()
	{
		return salesGroup;
	}

	/**
	 * @return the salesOffice
	 */
	public String getSalesOffice()
	{
		return salesOffice;
	}

	/**
	 * @return the customerGroup
	 */
	public String getCustomerGroup()
	{
		return customerGroup;
	}

	/**
	 * @return the priceGroup
	 */
	public String getPriceGroup()
	{
		return priceGroup;
	}

	/**
	 * @return the banner
	 */
	public String getBanner()
	{
		return banner;
	}

	/**
	 * @return the primaryBanner
	 */
	public String getPrimaryBanner()
	{
		return primaryBanner;
	}

	/**
	 * @return the subBanner
	 */
	public String getSubBanner()
	{
		return subBanner;
	}

	/**
	 * @return the division
	 */
	public String getDivision()
	{
		return division;
	}

	/**
	 * @return the distributionChannel
	 */
	public String getDistributionChannel()
	{
		return distributionChannel;
	}

	/**
	 * @return the plant
	 */
	public String getPlant()
	{
		return plant;
	}

	/**
	 * @return the subChannel
	 */
	public String getSubChannel()
	{
		return subChannel;
	}

	public String toKey() {
		return new StringBuilder(emptyIfNull(customer)).append('-')
				.append(emptyIfNull(salesOrgId)).append('-')
				.append(emptyIfNull(salesGroup)).append('-')
				.append(emptyIfNull(salesOffice)).append('-')
				.append(emptyIfNull(customerGroup)).append('-')
				.append(emptyIfNull(priceGroup)).append('-')
				.append(emptyIfNull(banner)).append('-')
				.append(emptyIfNull(primaryBanner)).append('-')
				.append(emptyIfNull(subBanner)).append('-')
				.append(emptyIfNull(division)).append('-')
				.append(emptyIfNull(distributionChannel)).append('-')
				.append(emptyIfNull(plant)).append('-')
				.append(emptyIfNull(subChannel)).toString();

	}

	private static String emptyIfNull(final String string){
		return string == null? StringUtils.EMPTY:string;
	}

}
