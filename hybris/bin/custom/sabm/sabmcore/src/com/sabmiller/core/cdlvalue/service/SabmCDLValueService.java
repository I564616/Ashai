/**
 *
 */
package com.sabmiller.core.cdlvalue.service;

import java.math.BigDecimal;


/**
 * @author EG588BU
 *
 */
public interface SabmCDLValueService
{

	/**
	 * @param cdlContainerType
	 * @param presentation
	 * @return
	 */
	BigDecimal getCDLPrice(String cdlContainerType, String presentation);

}
