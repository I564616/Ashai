/**
 *
 */
package com.sabmiller.webservice.product.util;

import java.util.Map;


/**
 * @author joshua.a.antony
 *
 */
public class SapHybrisUnitOfMeasureMapper
{

	private Map<String, String> map;


	public String getHybrisUomCode(final String sapUomCode)
	{
		final String value = map.get(sapUomCode);
		return value != null ? value : sapUomCode;
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(final Map<String, String> map)
	{
		this.map = map;
	}
}
