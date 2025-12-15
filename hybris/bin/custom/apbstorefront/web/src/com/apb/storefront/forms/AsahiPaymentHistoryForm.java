package com.apb.storefront.forms;

/**
 * 
 */
public class AsahiPaymentHistoryForm
{

	private String fromDate;
	
	private String toDate;
	
	private String keyword;
	
	private String sortAttribute;
	
	private int pageNo;
	
	private int totalRecords;
	
	private int fromCount;
	
	private int toCount;
	
	private int pageSize;

	/**
	 * @return date
	 */
	public String getFromDate()
	{
		return fromDate;
	}

	/**
	 * @param fromDate
	 */
	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	/**
	 * @return date
	 */
	public String getToDate()
	{
		return toDate;
	}

	/**
	 * @param toDate
	 */
	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	/**
	 * @return keyword
	 */
	public String getKeyword()
	{
		return keyword;
	}

	/**
	 * @param keyword
	 */
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	/**
	 * @return string
	 */
	public String getSortAttribute()
	{
		return sortAttribute;
	}

	/**
	 * @param sortAttribute
	 */
	public void setSortAttribute(String sortAttribute)
	{
		this.sortAttribute = sortAttribute;
	}

	/**
	 * @return string
	 */
	public int getPageNo()
	{
		return pageNo;
	}

	/**
	 * @param pageNo
	 */
	public void setPageNo(int pageNo)
	{
		this.pageNo = pageNo;
	}

	/**
	 * @return String
	 */
	public int getTotalRecords()
	{
		return totalRecords;
	}

	/**
	 * @param totalRecords
	 */
	public void setTotalRecords(int totalRecords)
	{
		this.totalRecords = totalRecords;
	}

	/**
	 * @return
	 */
	public int getFromCount()
	{
		return fromCount;
	}

	/**
	 * @param fromCount
	 */
	public void setFromCount(int fromCount)
	{
		this.fromCount = fromCount;
	}

	/**
	 * @return
	 */
	public int getToCount()
	{
		return toCount;
	}

	/**
	 * @param toCount
	 */
	public void setToCount(int toCount)
	{
		this.toCount = toCount;
	}

	/**get size
	 * @return int
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**set size
	 * @param pageSize
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
}
