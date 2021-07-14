package de.factorypal.sauloborges.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineSummaryResponse {

	private String machineName;
	private Map<String, ParameterSummaryResponse> parameterSummaryResponseList = new HashMap<>();

}
