package com.cuyum.jbpm.wih.rest;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cuyum.jbpm.wih.util.JSONUtil;

public class RESTClient {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	private String path;

	private String method;

	private Map<String, Object> parameters;

	private Integer responseCode;
	private String responseMessage;

	private Map<String, Object> results;

	public RESTClient(String method, String path, Map<String, Object> parameters) {
		super();
		this.path = path;
		this.method = method;
		this.parameters = parameters;
	}

	public RESTClient(String path, Map<String, Object> parameters) {
		this(METHOD_POST, path, parameters);
	}

	public RESTClient(String path) {
		this(METHOD_GET, path, null);
	}

	private String convertMapToParameters(Map<String, Object> params) {
		if (params == null || params.entrySet().size() == 0)
			return "";
		StringBuilder sb = new StringBuilder("?");

		Iterator<Entry<String, Object>> iter = params.entrySet().iterator();
		Entry<String, Object> first = iter.next();
		sb.append(first.getKey() + "=" + first.getValue());

		while (iter.hasNext()) {
			Entry<String, Object> resto = iter.next();
			sb.append("&" + resto.getKey() + "=" + resto.getValue());
		}
		
		return sb.toString();
	}

	public int executeService() throws RESTException {

		HttpClient httpClient = null;
		HttpRequestBase request = null;
		HttpResponse response = null;
		System.out.println("REST Client:" + method + " " + path);

		try {
			URI uri = new URI(path);
			if (method.toUpperCase().equals(METHOD_GET)) {
				String sparams = convertMapToParameters(parameters);
				request = new HttpGet(uri + sparams);
			} else {
				String body = JSONUtil.convertMapToJSON(parameters);
				System.out.println(body);
				StringEntity entity = new StringEntity(body);
				entity.setContentType("application/json");
				HttpPost hp = new HttpPost(uri);
				hp.setEntity(entity);
				request = hp;
			}

			httpClient = new DefaultHttpClient();
			response = httpClient.execute(request);

			responseCode = response.getStatusLine().getStatusCode();
			responseMessage = response.getStatusLine().getReasonPhrase();
			if (responseCode == 200) {
				String ret = EntityUtils.toString(response.getEntity());
				System.out.println("REST Client RESULT:" + ret);
				results = JSONUtil.convertJsonToMap(ret);
			}
			return responseCode;

		} catch (Exception e) {
			throw new RESTException("Error al ejecutar RESClient", e);

		} finally {

			// request.
		}

	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public Map<String, Object> getResults() {
		return results;
	}

}
