package com.cuyum.jbpm.wih.rest.handlers;

import java.util.HashMap;
import java.util.Map;

import com.cuyum.jbpm.wih.rest.RESTClient;
import com.cuyum.jbpm.wih.rest.RESTException;

public class DefaultRESTHandler implements RESTHandler {

	@Override
	public Map<String, Object> execute(String method, String urlBase, String path,
			Map<String, Object> parameters) throws RESTException {
		String uri = urlBase+path;
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		RESTClient restClient = new RESTClient(method, uri,
				parameters);

		restClient.executeService();

		if (restClient.getResponseCode() != 200) {
			System.err.println("Error ejecutando RESTWorkItemHandler " + uri);
			System.err.println("HTTP STATUS CODE: "
					+ restClient.getResponseCode());
			System.err.println("HTTP STATUS MESSAGE: " + restClient.getResponseMessage());
			throw new RESTException("Error " + restClient.getResponseCode()
					+ ": " + restClient.getResponseMessage());
		}
		
		return restClient.getResults();
		
	}

}
