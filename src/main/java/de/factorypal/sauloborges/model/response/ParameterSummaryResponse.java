package de.factorypal.sauloborges.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterSummaryResponse {

	private double avg;
	private double median;
	private double min;
	private double max;

}
