/**
 *
 */
package com.sabmiller.core.util;

import java.util.List;


/**
 * @author joshua.a.antony
 *
 */
public interface IWorkLog
{

	public void add(String work);

	public List<String> getLogs();

}
