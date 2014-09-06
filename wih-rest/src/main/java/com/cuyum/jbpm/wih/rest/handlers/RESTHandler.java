package com.cuyum.jbpm.wih.rest.handlers;

import java.util.Map;

import com.cuyum.jbpm.wih.rest.RESTException;

public interface RESTHandler {

	Map<String, Object> execute(String method, String urlBase, String path,
			Map<String, Object> parameters) throws RESTException;

}
