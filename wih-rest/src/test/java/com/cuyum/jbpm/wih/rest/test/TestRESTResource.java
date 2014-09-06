package com.cuyum.jbpm.wih.rest.test;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.cuyum.jbpm.wih.rest.handlers.MockRESTHandler;

@Path("/test")
public class TestRESTResource {

	/*
	path1;{"dato1":"val1","dato2":"val2"};{"resultado":"ok"}
	path1;{"dato1":"val1","dato2":"val3"};{"resultado":"fail","mensaje":"val3 no es correcto"}
	path2;{"dato1":"val1"};{}
	*/
	private MockRESTHandler mockHandler;
	
	public TestRESTResource(String fileMock) {
		mockHandler = new MockRESTHandler(fileMock);
	}
	
	@POST
	@Path("/path1")
	@Consumes("application/json")
	@Produces("application/json")
	public Map<String, Object> path1Post(Map<String, Object> parametros) {
		return mockHandler.execute("POST", "", "/path1", parametros);		
	}
	
	@POST
	@Path("/path2")
	@Consumes("application/json")
	@Produces("application/json")
	public Map<String, Object> path2Post(Map<String, Object> parametros) {
		return mockHandler.execute("POST", "", "/path2", parametros);		
	}
	
	@GET
	@Path("/path2")
	@Produces("application/json")
	public Map<String, Object> path2Get(@QueryParam("dato1")String dato1) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dato1", dato1);		
		return mockHandler.execute("GET", "", "/path2", params);
	}
	

}
