/**
 *
 */
package com.sabmiller.core.product.strategy;

import de.hybris.platform.util.Config;

import java.util.Map;


/**
 * @author joshua.a.antony
 *
 */
public class DefaultHybrisCategoryInfoDeterminationStrategy implements HybrisCategoryInfoDeterminationStrategy
{
	private Map<String, String> categoryAttributeCodeMap;
	private Map<String, String> categoryAttributeNameMap;

	@Override
	public String deriveCategoryName(final String categoryAttribute)
	{
		final String name = get(categoryAttributeNameMap, categoryAttribute);

		return name != null ? name : getUnassignedHybrisCategoryName();
	}

	@Override
	public String deriveCategoryCode(final String categoryAttribute)
	{
		final String code = get(categoryAttributeCodeMap, categoryAttribute);

		return code != null ? code : getUnassignedHybrisCategoryCode();
	}

	private String get(final Map<String, String> map, final String toFind)
	{
		if (map != null)
		{
			for (final Map.Entry<String, String> entry : map.entrySet())
			{
				if (entry.getKey().equalsIgnoreCase(toFind))
				{
					return entry.getValue();

				}
			}
		}
		return null;
	}

	private String getUnassignedHybrisCategoryCode()
	{
		return Config.getString("unassigned.category.code", "unassigned");
	}

	private String getUnassignedHybrisCategoryName()
	{
		return Config.getString("unassigned.category.name", "UNASSIGNED");
	}

	public Map<String, String> getCategoryAttributeCodeMap()
	{
		return categoryAttributeCodeMap;
	}

	public void setCategoryAttributeCodeMap(final Map<String, String> categoryAttributeCodeMap)
	{
		this.categoryAttributeCodeMap = categoryAttributeCodeMap;
	}

	public Map<String, String> getCategoryAttributeNameMap()
	{
		return categoryAttributeNameMap;
	}

	public void setCategoryAttributeNameMap(final Map<String, String> categoryAttributeNameMap)
	{
		this.categoryAttributeNameMap = categoryAttributeNameMap;
	}

}
