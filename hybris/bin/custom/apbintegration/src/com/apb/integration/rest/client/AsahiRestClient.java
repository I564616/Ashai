package com.apb.integration.rest.client;

import org.springframework.http.ResponseEntity;

public interface AsahiRestClient {

	/**
	 * To execute the Post RestRequest
	 *
	 * @param urlPath
	 * @param requestEntity
	 * @param responseEntity
	 */

	public ResponseEntity<String> executeOrderAXRestRequest(final String urlPath, Object requestEntity, Class responseEntity,
			String RequestType);

	/**
	 * To execute the Get RestRequest
	 *
	 * @param urlPath
	 * @param responseEntity
	 */
	public Object executePOSTRestRequest(final String urlPath, Object requestEntity, Class responseEntity,
			String RequestType);

}
