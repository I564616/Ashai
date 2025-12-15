/**
 *
 */
package com.sabmiller.core.cdlvalue.dao;

import java.util.Optional;

import com.sabm.core.model.CDLValueModel;


/**
 * @author EG588BU
 *
 */
public interface SabmCDLValueDao
{

	/**
	 * @param location
	 * @param containerType
	 * @return
	 */
	Optional<CDLValueModel> getCDLValueModel(String location, String containerType);

}
