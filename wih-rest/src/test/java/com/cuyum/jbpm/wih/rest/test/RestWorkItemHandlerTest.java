package com.cuyum.jbpm.wih.rest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.cuyum.jbpm.wih.rest.RESTWorkItemHandler;

public class RestWorkItemHandlerTest {

	private final static String serverURL = "http://localhost:9998/test";
	private static Server server;

	private RESTWorkItemHandler handler;

	@Before
	public void init() throws Exception {
		handler = new RESTWorkItemHandler();
	}

	@SuppressWarnings({ "rawtypes" })
	@BeforeClass
	public static void initialize() throws Exception {
		System.setProperty(RESTWorkItemHandler.SYSTEM_URL_BASE_PROPERTY, serverURL);
		SimpleRESTApplication application = new SimpleRESTApplication();
		RuntimeDelegate delegate = RuntimeDelegate.getInstance();

		JAXRSServerFactoryBean bean = delegate.createEndpoint(application,
				JAXRSServerFactoryBean.class);
		bean.setProvider(new JacksonJsonProvider());

		bean.setAddress("http://localhost:9998" + bean.getAddress());
		server = bean.create();
		server.start();

	}

	@AfterClass
	public static void destroy() throws Exception {
		server.stop();
		server.destroy();
	}

	@Test
	public void testPOSTOperation1() {

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("path", "/path1");
		workItem.setParameter("dato1", "val1");
		workItem.setParameter("dato2", "val2");
		System.out.println("Parametros: " + workItem.getParameters());

		WorkItemManager manager = new TestWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);

		Map<String, Object> result = (Map<String, Object>) workItem
				.getResults();
		System.out.println("Resultado: " + result);

		assertNotNull("result cannot be null", result);
		assertEquals(result.get("resultado"), "ok");

	}

	@Test
	public void testPOSTOperation2() {

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("path", "/path1");
		workItem.setParameter("dato1", "val1");
		workItem.setParameter("dato2", "val3");
		System.out.println("Parametros: " + workItem.getParameters());

		WorkItemManager manager = new TestWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);

		Map<String, Object> result = (Map<String, Object>) workItem
				.getResults();
		System.out.println("Resultado: " + result);

		assertNotNull("result cannot be null", result);
		assertEquals(result.get("resultado"), "fail");
		assertEquals(result.get("mensaje"), "val3 no es correcto");
	}

	@Test
	public void testPOSTOperation3() {

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("path", "/path2");
		workItem.setParameter("dato1", "val1");
		System.out.println("Parametros: " + workItem.getParameters());

		WorkItemManager manager = new TestWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);

		Map<String, Object> result = (Map<String, Object>) workItem
				.getResults();
		System.out.println("Resultado: " + result);
		assertNotNull("result cannot be null", result);
	}

	@Test
	public void testGETOperation3() {

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("method", "GET");
		workItem.setParameter("path", "/path2");
		workItem.setParameter("dato1", "val1");
		System.out.println("Parametros: " + workItem.getParameters());

		WorkItemManager manager = new TestWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);

		Map<String, Object> result = (Map<String, Object>) workItem
				.getResults();
		System.out.println("Resultado: " + result);
		assertNotNull("result cannot be null", result);
	}

	//@Test(expected = WorkItemHandlerRuntimeException.class)
	public void testPOSTOperationNotFound() {

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("path", "/path2");
		workItem.setParameter("dato2", "val1");
		System.out.println("Parametros: " + workItem.getParameters());

		WorkItemManager manager = new TestWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);
		
	}

	private class TestWorkItemManager implements WorkItemManager {

		private WorkItem workItem;

		TestWorkItemManager(WorkItem workItem) {
			this.workItem = workItem;
		}

		@Override
		public void completeWorkItem(long id, Map<String, Object> results) {
			((WorkItemImpl) workItem).setResults(results);

		}

		@Override
		public void abortWorkItem(long id) {

		}

		@Override
		public void registerWorkItemHandler(String workItemName,
				WorkItemHandler handler) {

		}

	}
}
