package de.factorypal.sauloborges.controller;

import de.factorypal.sauloborges.exception.MachineNotFoundException;
import de.factorypal.sauloborges.model.Machine;
import de.factorypal.sauloborges.model.ParameterRequest;
import de.factorypal.sauloborges.model.response.MachineResponse;
import de.factorypal.sauloborges.model.response.MachineSummaryResponse;
import de.factorypal.sauloborges.service.MachineDataService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Main controller which contain the endpoint for fetch the data async
 */
@RestController
public class MachineDataController {

	private final MachineDataService machineDataService;

	@Autowired
	public MachineDataController(final MachineDataService machineDataService) {
		this.machineDataService = machineDataService;
	}

	@ApiOperation(value = "Given a list of key and parameters, add this info into respective machine")
	@ApiResponses(
			value = { @ApiResponse(code = 200, message = "Successful retrieval", response = MachineResponse.class) })
	@RequestMapping(value = "/machine/parameters", method = RequestMethod.POST)
	public ResponseEntity<List<String>> postParameters(@RequestBody final List<ParameterRequest> parameterRequest)
			throws ExecutionException, InterruptedException {

		List<CompletableFuture<Machine>> futureList = new ArrayList<>();
		for (ParameterRequest request : parameterRequest) {
			futureList.add(machineDataService.parseAndStoreParameter(request));
		}

		List<String> list = new ArrayList<>();
		for (CompletableFuture<Machine> machineCompletableFuture : futureList) {
			Machine response = machineCompletableFuture.get();
			if (response != null)
				list.add("parameters for machineKey " + response.getName() + " added");
		}

		return ResponseEntity.ok(list);
	}

	@ApiOperation(value = "Given a machine key return the machine information")
	@ApiResponses(
			value = { @ApiResponse(code = 200, message = "Successful retrieval", response = MachineResponse.class) })
	@RequestMapping(value = "/machine/{machineKey}", method = RequestMethod.GET)
	public ResponseEntity<Machine> getMachine(@PathVariable final String machineKey) throws MachineNotFoundException {
		return ResponseEntity.ok(machineDataService.getMachineByKey(machineKey));
	}

	@ApiOperation(value = "Given a machine key return the last information from each parameter")
	@ApiResponses(
			value = { @ApiResponse(code = 200, message = "Successful retrieval", response = MachineResponse.class) })
	@RequestMapping(value = "/machine/{machineKey}/lastestParameters", method = RequestMethod.GET)
	public ResponseEntity<MachineResponse> getLastParametersPerMachine(@PathVariable final String machineKey)
			throws MachineNotFoundException {
		final MachineResponse response = machineDataService.getMachineResponseLatestParamater(machineKey);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Return a detailed information from each parameter from each machine")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval", response = MachineSummaryResponse.class) })
	@RequestMapping(value = "/machine/summary/{minutes}", method = RequestMethod.GET)
	public ResponseEntity<List<MachineSummaryResponse>> getLastParametersPerMachine(@PathVariable final int minutes) {

		List<MachineSummaryResponse> machineSummaryResponses =
				machineDataService.summaryAllMachinesFromTheLastMinutes(minutes);
		return ResponseEntity.ok(machineSummaryResponses);
	}

}
