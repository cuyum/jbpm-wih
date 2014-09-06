package com.cuyum.jbpm.wih.rest;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.cuyum.jbpm.wih.rest.handlers.DefaultRESTHandler;
import com.cuyum.jbpm.wih.rest.handlers.MockRESTHandler;
import com.cuyum.jbpm.wih.rest.handlers.RESTHandler;



public class RESTWorkItemHandler extends AbstractLogOrThrowWorkItemHandler
{
	
	//public static final String IN_URL_BASE="urlBase";
	public static final String IN_METHOD="method";
	public static final String IN_PATH="path";
	
	public static final String SYSTEM_URL_BASE_PROPERTY="cuyum.jbpm.wih.rest.url.base";
	public static final String SYSTEM_URL_BASE_DEFAULT="http://localhost:8080/rest";
	
	public static final String USE_MOCK_PROPERTY="cuyum.jbpm.wih.rest.mock.use";
	public static final String USE_MOCK_DEFAULT="false";
	
	public static final String FILENAME_MOCK_PROPERTY="cuyum.jbpm.wih.rest.mock.filename";
	//public static final String FILENAME_MOCK_DEFAULT="mocks.csv";
	
	
	protected String taskName;
	
	protected String urlBase;
		
	protected String path;
	
	protected String method = "POST";
	
	protected String useMock;
	
	protected Map<String, Object> parametersBody;
	
	
	private void resolveParameters(Map<String, Object> parameters) {
		Map<String, Object> ret = new HashMap<String, Object>(parameters);
		urlBase = System.getProperty(SYSTEM_URL_BASE_PROPERTY, SYSTEM_URL_BASE_DEFAULT);
		useMock = System.getProperty(USE_MOCK_PROPERTY, USE_MOCK_DEFAULT);
		
		if (parameters.containsKey(IN_PATH)) {
			path = (String) parameters.get(IN_PATH);
			ret.remove(IN_PATH);		
		}
		
		if (parameters.containsKey(IN_METHOD)) {
			method = (String) parameters.get(IN_METHOD);
			ret.remove(IN_METHOD);		
		}
		
		taskName = (String)ret.get("TaskName");
		ret.remove("TaskName");
		
		parametersBody = ret;
	}

	protected RESTHandler resolveHandler(String path) {
		if (useMock.equals("true")) {
			String filename = System.getProperty(FILENAME_MOCK_PROPERTY);
			return new MockRESTHandler(filename);
		}
		else	
			return new DefaultRESTHandler();
	}
	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
	
		try {
			resolveParameters(workItem.getParameters());
			RESTHandler rh = resolveHandler(path);
			System.out.println("Ejecutando WIH "+this.toString());
			Map<String,Object> results = rh.execute(method, urlBase, path, parametersBody);
			System.out.println("Resultados WIH "+results.toString());
			manager.completeWorkItem(workItem.getId(), results);
		} catch (Exception e) {
			handleException(e);
		}
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		System.out.println("Abortando ejecución de RESTWorkItemHandler: "+this);
	}

	public String getUrlBase() {
		return urlBase;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, Object> getParametersBody() {
		return parametersBody;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "RESTWorkItemHandler ["
				+ (taskName != null ? "taskName=" + taskName + ", " : "")
				+ (urlBase != null ? "urlBase=" + urlBase + ", " : "")
				+ (path != null ? "path=" + path + ", " : "")
				+ (method != null ? "method=" + method + ", " : "")
				+ (useMock != null ? "useMock=" + useMock + ", " : "")
				+ (parametersBody != null ? "parametersBody=" + parametersBody
						: "") + "]";
	}

	
}
