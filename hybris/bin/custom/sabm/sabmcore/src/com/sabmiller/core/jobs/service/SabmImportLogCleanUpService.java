/**
 *
 */
package com.sabmiller.core.jobs.service;

import java.util.List;

import com.sabmiller.webservice.model.MasterImportModel;


/**
 * @author iqbal.javed
 *
 */
public interface SabmImportLogCleanUpService
{
	public List<MasterImportModel> getMasterImportLogs();

	public String getCurrentSystemDate();
}
