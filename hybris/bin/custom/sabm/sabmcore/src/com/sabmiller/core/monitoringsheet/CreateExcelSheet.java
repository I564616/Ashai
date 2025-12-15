/**
 *
 */
package com.sabmiller.core.monitoringsheet;

import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.List;


/**
 * @author praveenkumar.k.reddy
 *
 */


public interface CreateExcelSheet
{

	boolean createExcelSheet(List<CronJobModel> cronJobModel, List<List<?>> interfacedetails);

}
