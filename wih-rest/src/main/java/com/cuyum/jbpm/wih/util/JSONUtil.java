package com.cuyum.jbpm.wih.util;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.cuyum.jbpm.wih.rest.RESTException;

public class JSONUtil {
	
	public static String convertMapToJSON(Map<String, Object> map) {
		try {
			String resultado = new ObjectMapper()
					.writeValueAsString(map);
			return resultado;
		} catch (Exception e) {
			throw new RESTException("Error al convertir a JSON:"
					+ map, e);
		}
	}
	
	public static Map<String, Object> convertJsonToMap(String json) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> results = new ObjectMapper().readValue(
					json, HashMap.class);
			return results;
		} catch (Exception e) {
			throw new RESTException("Error al convertir de JSON", e);
		}
	}

}
