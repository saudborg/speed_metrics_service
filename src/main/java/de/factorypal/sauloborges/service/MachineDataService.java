package de.factorypal.sauloborges.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import de.factorypal.sauloborges.exception.MachineNotFoundException;
import de.factorypal.sauloborges.model.Machine;
import de.factorypal.sauloborges.model.Parameter;
import de.factorypal.sauloborges.model.ParameterRequest;
import de.factorypal.sauloborges.model.ParametersValues;
import de.factorypal.sauloborges.model.response.MachineResponse;
import de.factorypal.sauloborges.model.response.MachineSummaryResponse;
import de.factorypal.sauloborges.model.response.ParameterSummaryResponse;
import de.factorypal.sauloborges.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class MachineDataService {

	private MachineRepository machineRepository;

	@Autowired
	public MachineDataService(final MachineRepository machineRepository) {
		this.machineRepository = machineRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void readMachineDataFromLocalCsv() throws IOException, CsvException {
		readMachineDataFromCSV("/machines.csv");
	}

	private void readMachineDataFromCSV(final String csvPath) throws IOException, CsvException {
		InputStream inputStream = this.getClass().getResourceAsStream(csvPath);

		try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
			String[] header = reader.readNext();
			List<String[]> machineEntry = reader.readAll();
			machineEntry.forEach(entry -> {
				String key = entry[0];
				String name = entry[1];
				Machine machine = new Machine();
				machine.setKey(key);
				machine.setName(name);
				machineRepository.save(machine);
			});
		}
	}

	@Async
	public CompletableFuture<Machine> parseAndStoreParameter(final ParameterRequest request) {
		final String key = request.getMachineKey();
		Machine machine = machineRepository.findById(key).orElse(null);
		if (machine == null) {
			return CompletableFuture.completedFuture(null);
		}

		for (Map.Entry<String, Long> entry : request.getParameters().entrySet()) {
			Parameter parameter = Parameter.builder().name(entry.getKey()).value(entry.getValue())
					.creationDate(Calendar.getInstance().getTime()).build();

			Optional<ParametersValues> parametersValues1 =
					Optional.ofNullable(machine.getParametersMap().get(parameter.getName()));
			final ParametersValues parametersValues = parametersValues1.orElse(new ParametersValues());

			parametersValues.getParameterList().add(parameter);

			machine.getParametersMap().put(parameter.getName(), parametersValues);

		}
		machineRepository.save(machine);
		return CompletableFuture.completedFuture(machine);
	}

	public Machine getMachineByKey(final String machineKey) throws MachineNotFoundException {
		Optional<Machine> machine = machineRepository.findById(machineKey);
		return machine.orElseThrow(() -> new MachineNotFoundException(machineKey));
	}

	public MachineResponse getMachineResponseLatestParamater(final String machineKey) throws MachineNotFoundException {
		Machine machine =
				machineRepository.findById(machineKey).orElseThrow(() -> new MachineNotFoundException(machineKey));

		MachineResponse response = new MachineResponse();
		response.setName(machine.getName());

		machine.getParametersMap().forEach((key, value) -> {
			List<Parameter> parameterList = value.getParameterList();
			parameterList.stream().max(Parameter::compareTo)
					.ifPresent(parameter -> response.getParameters().put(key, parameter.getValue()));
		});
		return response;
	}

	public List<MachineSummaryResponse> summaryAllMachinesFromTheLastMinutes(final int minutes) {
		List<MachineSummaryResponse> listResponse = new ArrayList<>();
		List<Machine> machineList = machineRepository.findAll();
		for (Machine machine : machineList) {
			MachineSummaryResponse machineSummaryResponse = new MachineSummaryResponse();
			machineSummaryResponse.setMachineName(machine.getName());

			machine.getParametersMap().forEach((key, value) -> {
				List<Parameter> parametersInInterval =
						value.getParameterList().stream().filter(p -> isInTheLastMinutes(p, minutes))
								.collect(Collectors.toList());
				if (!parametersInInterval.isEmpty()) {
					List<Long> valuesList =
							parametersInInterval.stream().map(Parameter::getValue).collect(Collectors.toList());
					double avg = calculateAverage(valuesList);
					double median = calculateMedian(valuesList);
					double min = calculateMin(valuesList);
					double max = calculateMax(valuesList);
					machineSummaryResponse.getParameterSummaryResponseList().put(key,
							ParameterSummaryResponse.builder().max(max).min(min).avg(avg).median(median).build());
				} else {
					// empty values
					machineSummaryResponse.getParameterSummaryResponseList()
							.put(key, ParameterSummaryResponse.builder().max(0).min(0).avg(0).median(0).build());
				}
			});
			listResponse.add(machineSummaryResponse);
		}
		return listResponse;
	}

	private static boolean isInTheLastMinutes(final Parameter parameter, int minutes) {
		Calendar threshold = Calendar.getInstance();
		threshold.add(Calendar.MINUTE, -minutes);
		return parameter.getCreationDate().compareTo(threshold.getTime()) > 0;
	}

	private double calculateAverage(List<Long> marks) {
		return marks.stream().mapToDouble(d -> d).average().orElse(0.0);
	}

	private double calculateMedian(List<Long> marks) {
		DoubleStream sortedAges = marks.stream().mapToDouble(Long::doubleValue).sorted();
		double median = marks.size() % 2 == 0 ?
				sortedAges.skip(marks.size() / 2 - 1).limit(2).average().getAsDouble() :
				sortedAges.skip(marks.size() / 2).findFirst().getAsDouble();
		return median;
	}

	private double calculateMin(List<Long> marks) {
		return marks.stream().min(Long::compareTo).get();
	}

	private double calculateMax(List<Long> marks) {
		return marks.stream().max(Long::compareTo).get();
	}
}
