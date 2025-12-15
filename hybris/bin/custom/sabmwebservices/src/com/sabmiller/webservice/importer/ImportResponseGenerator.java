/**
 *
 */
package com.sabmiller.webservice.importer;

import com.sabmiller.webservice.response.ImportResponse;


/**
 * @author joshua.a.antony
 *
 */
public interface ImportResponseGenerator<R extends ImportResponse, Entity>
{
	R generateResponse(final Entity entity, final Exception e, final Boolean entityExist);
}
