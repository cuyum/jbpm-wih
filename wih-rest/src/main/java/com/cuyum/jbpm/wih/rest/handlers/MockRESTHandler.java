package com.cuyum.jbpm.wih.rest.handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cuyum.jbpm.wih.rest.RESTException;
import com.cuyum.jbpm.wih.util.JSONUtil;

public class MockRESTHandler implements RESTHandler {

	private List<EntryMock> mockSorted;
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
			mockSorted = createMocks();
		} catch (Exception e) {
			System.out
					.println("WARNING:No se puede cargar archivos de mock "
							+ fileToParse + " " + e.getMessage());
			e.printStackTrace();
		}
	}

	private List<EntryMock> createMocks() throws Exception {
		List<EntryMock> ret = new LinkedList<MockRESTHandler.EntryMock>();

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
				EntryMock em = new EntryMock(tokens[0].trim(),
						tokens[1].trim(), tokens[2].trim());
				ret.add(em);
				System.out.println("Mock cargado: " + em);
			}
			Collections.sort(ret);
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
		if (mockSorted == null)
			return parameters;
		String input = JSONUtil.convertMapToJSON(parameters);
		EntryMock searchMock = new EntryMock(path, input, null);
		System.out.println("Buscando: " + searchMock);
		int idx = Collections.binarySearch(mockSorted, searchMock);

		if (idx < 0) {
			throw new RESTException("Error " + 404
					+ ": no se encuentra el mock " + searchMock);
		}

		EntryMock founded = mockSorted.get(idx);
		String output = founded.getOutput();
		System.out.println("Encontrado: " + founded);
		Map<String, Object> ret = JSONUtil.convertJsonToMap(output);
		return ret;
	}

	static class EntryMock implements Comparable<EntryMock> {
		private String serviceName;
		private String input;
		private String output;

		public EntryMock(String serviceName, String input, String output) {
			super();
			this.serviceName = serviceName;
			this.input = input;
			this.output = output;
		}

		public String getServiceName() {
			return serviceName;
		}

		public String getInput() {
			return input;
		}

		public String getOutput() {
			return output;
		}

		@Override
		public int compareTo(EntryMock o) {
			return (this.getServiceName() + this.getInput()).compareTo(o
					.getServiceName() + o.getInput());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((input == null) ? 0 : input.hashCode());
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
			if (input == null) {
				if (other.input != null)
					return false;
			} else if (!input.equals(other.input))
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
					+ (input != null ? "input=" + input + ", " : "")
					+ (output != null ? "output=" + output : "") + "]";
		}

	}

}
