/**
 *
 */
package com.sabmiller.integration.imagesimport.pojo;


import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * The Class SkuVantageResponseItem Pojo to map the json response from sku vantage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "ProductCode", "EAN", "ImageStatus", "QAStatus", "Title", "ProductModifiedTime", "Original1", "Original0", "Original2",
		"Original3", "Original4", "Original5", "Original6", "Original7", "Original8", "Original9", "Original10", "Original11",
		"Original12", "Original13", "Original14", "Original15", "Original16", "Original17", "Original18", "Original19",
		"Original20" })
public class SkuVantageResponseItem
{

	/** The product code. */
	@JsonProperty("ProductCode")
	private String productCode;

	/** The ean. */
	@JsonProperty("EAN")
	private String EAN;

	/** The image status. */
	@JsonProperty("ImageStatus")
	private String imageStatus;

	/** The QA status. */
	@JsonProperty("QAStatus")
	private String QAStatus;

	/** The title. */
	@JsonProperty("Title")
	private String title;

	/** The product modified time. */
	@JsonProperty("ProductModifiedTime")
	private String productModifiedTime;

	/** The Original1. */
	@JsonProperty("Original1")
	private String Original1;

	/** The Original0. */
	@JsonProperty("Original0")
	private String Original0;

	/** The Original2. */
	@JsonProperty("Original2")
	private String Original2;

	/** The Original3. */
	@JsonProperty("Original3")
	private String Original3;

	/** The Original4. */
	@JsonProperty("Original4")
	private String Original4;

	/** The Original5. */
	@JsonProperty("Original5")
	private String Original5;

	/** The Original6. */
	@JsonProperty("Original6")
	private String Original6;

	/** The Original7. */
	@JsonProperty("Original7")
	private String Original7;

	/** The Original8. */
	@JsonProperty("Original8")
	private String Original8;

	/** The Original9. */
	@JsonProperty("Original9")
	private String Original9;

	/** The Original10. */
	@JsonProperty("Original10")
	private String Original10;

	/** The Original11. */
	@JsonProperty("Original11")
	private String Original11;

	/** The Original12. */
	@JsonProperty("Original12")
	private String Original12;

	/** The Original13. */
	@JsonProperty("Original13")
	private String Original13;

	/** The Original14. */
	@JsonProperty("Original14")
	private String Original14;

	/** The Original15. */
	@JsonProperty("Original15")
	private String Original15;

	/** The Original16. */
	@JsonProperty("Original16")
	private String Original16;

	/** The Original17. */
	@JsonProperty("Original17")
	private String Original17;

	/** The Original18. */
	@JsonProperty("Original18")
	private String Original18;

	/** The Original19. */
	@JsonProperty("Original19")
	private String Original19;

	/** The Original20. */
	@JsonProperty("Original20")
	private String Original20;

	/** The additional properties. */
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * Gets the product code.
	 *
	 * @return The ProductCode
	 */
	@JsonProperty("ProductCode")
	public String getProductCode()
	{
		return productCode;
	}

	/**
	 * Sets the product code.
	 *
	 * @param productCode
	 *           the new product code
	 */
	@JsonProperty("ProductCode")
	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

	/**
	 * Gets the ean.
	 *
	 * @return The EAN
	 */
	@JsonProperty("EAN")
	public String getEAN()
	{
		return EAN;
	}

	/**
	 * Sets the ean.
	 *
	 * @param EAN
	 *           The EAN
	 */
	@JsonProperty("EAN")
	public void setEAN(final String EAN)
	{
		this.EAN = EAN;
	}

	/**
	 * Gets the image status.
	 *
	 * @return The ImageStatus
	 */
	@JsonProperty("ImageStatus")
	public String getImageStatus()
	{
		return imageStatus;
	}

	/**
	 * Sets the image status.
	 *
	 * @param imageStatus
	 *           the new image status
	 */
	@JsonProperty("ImageStatus")
	public void setImageStatus(final String imageStatus)
	{
		this.imageStatus = imageStatus;
	}

	/**
	 * Gets the QA status.
	 *
	 * @return The QAStatus
	 */
	@JsonProperty("QAStatus")
	public String getQAStatus()
	{
		return QAStatus;
	}

	/**
	 * Sets the QA status.
	 *
	 * @param QAStatus
	 *           The QAStatus
	 */
	@JsonProperty("QAStatus")
	public void setQAStatus(final String QAStatus)
	{
		this.QAStatus = QAStatus;
	}

	/**
	 * Gets the title.
	 *
	 * @return The Title
	 */
	@JsonProperty("Title")
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *           the new title
	 */
	@JsonProperty("Title")
	public void setTitle(final String title)
	{
		this.title = title;
	}

	/**
	 * Gets the product modified time.
	 *
	 * @return The ProductModifiedTime
	 */
	@JsonProperty("ProductModifiedTime")
	public String getProductModifiedTime()
	{
		return productModifiedTime;
	}

	/**
	 * Sets the product modified time.
	 *
	 * @param productModifiedTime
	 *           the new product modified time
	 */
	@JsonProperty("ProductModifiedTime")
	public void setProductModifiedTime(final String productModifiedTime)
	{
		this.productModifiedTime = productModifiedTime;
	}

	/**
	 * Gets the original1.
	 *
	 * @return The Original1
	 */
	@JsonProperty("Original1")
	public String getOriginal1()
	{
		return Original1;
	}

	/**
	 * Sets the original1.
	 *
	 * @param Original1
	 *           The Original1
	 */
	@JsonProperty("Original1")
	public void setOriginal1(final String Original1)
	{
		this.Original1 = Original1;
	}

	/**
	 * Gets the original0.
	 *
	 * @return The Original0
	 */
	@JsonProperty("Original0")
	public String getOriginal0()
	{
		return Original0;
	}

	/**
	 * Sets the original0.
	 *
	 * @param Original0
	 *           The Original0
	 */
	@JsonProperty("Original0")
	public void setOriginal0(final String Original0)
	{
		this.Original0 = Original0;
	}

	/**
	 * Gets the original2.
	 *
	 * @return The Original2
	 */
	@JsonProperty("Original2")
	public String getOriginal2()
	{
		return Original2;
	}

	/**
	 * Sets the original2.
	 *
	 * @param Original2
	 *           The Original2
	 */
	@JsonProperty("Original2")
	public void setOriginal2(final String Original2)
	{
		this.Original2 = Original2;
	}

	/**
	 * Gets the original3.
	 *
	 * @return The Original3
	 */
	@JsonProperty("Original3")
	public String getOriginal3()
	{
		return Original3;
	}

	/**
	 * Sets the original3.
	 *
	 * @param Original3
	 *           The Original3
	 */
	@JsonProperty("Original3")
	public void setOriginal3(final String Original3)
	{
		this.Original3 = Original3;
	}

	/**
	 * Gets the original4.
	 *
	 * @return The Original4
	 */
	@JsonProperty("Original4")
	public String getOriginal4()
	{
		return Original4;
	}

	/**
	 * Sets the original4.
	 *
	 * @param Original4
	 *           The Original4
	 */
	@JsonProperty("Original4")
	public void setOriginal4(final String Original4)
	{
		this.Original4 = Original4;
	}

	/**
	 * Gets the original5.
	 *
	 * @return The Original5
	 */
	@JsonProperty("Original5")
	public String getOriginal5()
	{
		return Original5;
	}

	/**
	 * Sets the original5.
	 *
	 * @param Original5
	 *           The Original5
	 */
	@JsonProperty("Original5")
	public void setOriginal5(final String Original5)
	{
		this.Original5 = Original5;
	}

	/**
	 * Gets the original6.
	 *
	 * @return The Original6
	 */
	@JsonProperty("Original6")
	public String getOriginal6()
	{
		return Original6;
	}

	/**
	 * Sets the original6.
	 *
	 * @param Original6
	 *           The Original6
	 */
	@JsonProperty("Original6")
	public void setOriginal6(final String Original6)
	{
		this.Original6 = Original6;
	}

	/**
	 * Gets the original7.
	 *
	 * @return The Original7
	 */
	@JsonProperty("Original7")
	public String getOriginal7()
	{
		return Original7;
	}

	/**
	 * Sets the original7.
	 *
	 * @param Original7
	 *           The Original7
	 */
	@JsonProperty("Original7")
	public void setOriginal7(final String Original7)
	{
		this.Original7 = Original7;
	}

	/**
	 * Gets the original8.
	 *
	 * @return The Original8
	 */
	@JsonProperty("Original8")
	public String getOriginal8()
	{
		return Original8;
	}

	/**
	 * Sets the original8.
	 *
	 * @param Original8
	 *           The Original8
	 */
	@JsonProperty("Original8")
	public void setOriginal8(final String Original8)
	{
		this.Original8 = Original8;
	}

	/**
	 * Gets the original9.
	 *
	 * @return The Original9
	 */
	@JsonProperty("Original9")
	public String getOriginal9()
	{
		return Original9;
	}

	/**
	 * Sets the original9.
	 *
	 * @param Original9
	 *           The Original9
	 */
	@JsonProperty("Original9")
	public void setOriginal9(final String Original9)
	{
		this.Original9 = Original9;
	}

	/**
	 * Gets the original10.
	 *
	 * @return The Original10
	 */
	@JsonProperty("Original10")
	public String getOriginal10()
	{
		return Original10;
	}

	/**
	 * Sets the original10.
	 *
	 * @param Original10
	 *           The Original10
	 */
	@JsonProperty("Original10")
	public void setOriginal10(final String Original10)
	{
		this.Original10 = Original10;
	}

	/**
	 * Gets the original11.
	 *
	 * @return The Original11
	 */
	@JsonProperty("Original11")
	public String getOriginal11()
	{
		return Original11;
	}

	/**
	 * Sets the original11.
	 *
	 * @param Original11
	 *           The Original11
	 */
	@JsonProperty("Original11")
	public void setOriginal11(final String Original11)
	{
		this.Original11 = Original11;
	}

	/**
	 * Gets the original12.
	 *
	 * @return The Original12
	 */
	@JsonProperty("Original12")
	public String getOriginal12()
	{
		return Original12;
	}

	/**
	 * Sets the original12.
	 *
	 * @param Original12
	 *           The Original12
	 */
	@JsonProperty("Original12")
	public void setOriginal12(final String Original12)
	{
		this.Original12 = Original12;
	}

	/**
	 * Gets the original13.
	 *
	 * @return The Original13
	 */
	@JsonProperty("Original13")
	public String getOriginal13()
	{
		return Original13;
	}

	/**
	 * Sets the original13.
	 *
	 * @param Original13
	 *           The Original13
	 */
	@JsonProperty("Original13")
	public void setOriginal13(final String Original13)
	{
		this.Original13 = Original13;
	}

	/**
	 * Gets the original14.
	 *
	 * @return The Original14
	 */
	@JsonProperty("Original14")
	public String getOriginal14()
	{
		return Original14;
	}

	/**
	 * Sets the original14.
	 *
	 * @param Original14
	 *           The Original14
	 */
	@JsonProperty("Original14")
	public void setOriginal14(final String Original14)
	{
		this.Original14 = Original14;
	}

	/**
	 * Gets the original15.
	 *
	 * @return The Original15
	 */
	@JsonProperty("Original15")
	public String getOriginal15()
	{
		return Original15;
	}

	/**
	 * Sets the original15.
	 *
	 * @param Original15
	 *           The Original15
	 */
	@JsonProperty("Original15")
	public void setOriginal15(final String Original15)
	{
		this.Original15 = Original15;
	}

	/**
	 * Gets the original16.
	 *
	 * @return The Original16
	 */
	@JsonProperty("Original16")
	public String getOriginal16()
	{
		return Original16;
	}

	/**
	 * Sets the original16.
	 *
	 * @param Original16
	 *           The Original16
	 */
	@JsonProperty("Original16")
	public void setOriginal16(final String Original16)
	{
		this.Original16 = Original16;
	}

	/**
	 * Gets the original17.
	 *
	 * @return The Original17
	 */
	@JsonProperty("Original17")
	public String getOriginal17()
	{
		return Original17;
	}

	/**
	 * Sets the original17.
	 *
	 * @param Original17
	 *           The Original17
	 */
	@JsonProperty("Original17")
	public void setOriginal17(final String Original17)
	{
		this.Original17 = Original17;
	}

	/**
	 * Gets the original18.
	 *
	 * @return The Original18
	 */
	@JsonProperty("Original18")
	public String getOriginal18()
	{
		return Original18;
	}

	/**
	 * Sets the original18.
	 *
	 * @param Original18
	 *           The Original18
	 */
	@JsonProperty("Original18")
	public void setOriginal18(final String Original18)
	{
		this.Original18 = Original18;
	}

	/**
	 * Gets the original19.
	 *
	 * @return The Original19
	 */
	@JsonProperty("Original19")
	public String getOriginal19()
	{
		return Original19;
	}

	/**
	 * Sets the original19.
	 *
	 * @param Original19
	 *           The Original19
	 */
	@JsonProperty("Original19")
	public void setOriginal19(final String Original19)
	{
		this.Original19 = Original19;
	}

	/**
	 * Gets the original20.
	 *
	 * @return The Original20
	 */
	@JsonProperty("Original20")
	public String getOriginal20()
	{
		return Original20;
	}

	/**
	 * Sets the original20.
	 *
	 * @param Original20
	 *           The Original20
	 */
	@JsonProperty("Original20")
	public void setOriginal20(final String Original20)
	{
		this.Original20 = Original20;
	}

	/**
	 * Gets the additional properties.
	 *
	 * @return the additional properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	/**
	 * Sets the additional property.
	 *
	 * @param name
	 *           the name
	 * @param value
	 *           the value
	 */
	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value)
	{
		this.additionalProperties.put(name, value);
	}

}