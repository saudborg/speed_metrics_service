package de.factorypal.sauloborges.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ParameterRequest {

	private String machineKey;
	private Map<String, Long> parameters;

	@JsonCreator
	public ParameterRequest(@JsonProperty(value = "machineKey") final String machineKey,
			@JsonProperty(value = "parameters") final Map<String, Long> parameters) {
		this.machineKey = machineKey;
		this.parameters = parameters;
	}
}
