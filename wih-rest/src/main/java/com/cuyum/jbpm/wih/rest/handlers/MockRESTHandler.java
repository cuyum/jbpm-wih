package com.cuyum.jbpm.wih.rest.handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.cuyum.jbpm.wih.rest.RESTException;
import com.cuyum.jbpm.wih.util.JSONUtil;

public class MockRESTHandler implements RESTHandler {

	private Map<EntryMock, String> mocksEntry;
	private String fileToParse;

	public MockRESTHandler(String fileToParse) {
		this.fileToParse = fileToParse;
		loadMocks();
	}

	public String getFileToParse() {
		return fileToParse;
	}

	public void loadMocks() {
		try {
			mocksEntry = createMocks();
		} catch (Exception e) {
			System.out.println("WARNING:No se puede cargar archivos de mock "
					+ fileToParse + " " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Map<EntryMock, String> createMocks() throws Exception {
		Map<EntryMock, String> ret = new HashMap<MockRESTHandler.EntryMock, String>();

		System.out.println("Cargando mocks de " + fileToParse);
		BufferedReader fileReader = null;
		// Delimiter used in CSV file
		final String DELIMITER = ";";
		try {
			String line = "";
			// Create the file reader
			fileReader = new BufferedReader(new FileReader(fileToParse));

			// Read the file line by line
			while ((line = fileReader.readLine()) != null) {
				// Get all tokens available in line
				if (line.startsWith("#"))
					continue;
				String[] tokens = line.split(DELIMITER);
				EntryMock em = new EntryMock(tokens[0], tokens[1]);
				ret.put(em, tokens[2]);
				System.out.println("Mock cargado: " + em);
			}
			System.out.println("Secargarom mocks correctamente de "
					+ fileToParse);
			return ret;
		} catch (Exception e) {
			throw new Exception("Archivo de mocks invalido " + fileToParse, e);
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Map<String, Object> execute(String method, String urlBase,
			String path, Map<String, Object> parameters) throws RESTException {
		// Si no hay mocks devuelve los mismos resultados
		
		System.out.println("Ejecutando mock");
		if (mocksEntry == null)
			return parameters;
		//String input = JSONUtil.convertMapToJSON(parameters);
		EntryMock searchMock = new EntryMock(path, parameters);
		System.out.println("Buscando: " + searchMock);
		String output = mocksEntry.get(searchMock);

		if (output ==null) {
			throw new RESTException("Error " + 404
					+ ": no se encuentra el mock " + searchMock);
		}

		System.out.println("Encontrado: " + output);
		Map<String, Object> ret = JSONUtil.convertJsonToMap(output);
		return ret;
	}

	static class EntryMock {
		private String serviceName;
		private String input;
		private Map<String, Object> inputMap;

		public EntryMock(String serviceName, String input) {
			super();
			this.serviceName = serviceName.trim();
			this.input = input.trim();
			this.inputMap = JSONUtil.convertJsonToMap(this.input);
		}
		
		public EntryMock(String serviceName, Map<String, Object> inputMap) {
			super();
			this.serviceName = serviceName.trim();
			this.inputMap = inputMap;
			this.input = JSONUtil.convertMapToJSON(this.inputMap);
		}

		public String getServiceName() {
			return serviceName;
		}

		public String getInput() {
			return input;
		}

		private boolean equalMaps(Map<String, Object> m1,
				Map<String, Object> m2) {
			if (m1.size() != m2.size())
				return false;
			for (String key : m1.keySet())
				if (!m1.get(key).equals(m2.get(key)))
					return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((inputMap == null) ? 0 : inputMap.hashCode());
			result = prime * result
					+ ((serviceName == null) ? 0 : serviceName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EntryMock other = (EntryMock) obj;
			if (inputMap == null) {
				if (other.inputMap != null)
					return false;
			} else if (!equalMaps(this.inputMap, other.inputMap))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "EntryMock ["
					+ (serviceName != null ? "serviceName=" + serviceName
							+ ", " : "")
					+ (input != null ? "input=" + input : "") + "]";
		}

		
	}

}
