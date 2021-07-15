package de.factorypal.sauloborges.service;

import de.factorypal.sauloborges.exception.MachineNotFoundException;
import de.factorypal.sauloborges.model.Machine;
import de.factorypal.sauloborges.model.Parameter;
import de.factorypal.sauloborges.model.ParameterRequest;
import de.factorypal.sauloborges.model.ParametersValues;
import de.factorypal.sauloborges.model.response.MachineResponse;
import de.factorypal.sauloborges.model.response.MachineSummaryResponse;
import de.factorypal.sauloborges.repository.MachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MachineDataServiceTest {

	private MachineDataService machineDataService;

	private MachineRepository machineRepository;

	@BeforeEach
	public void before() {
		machineRepository = mock(MachineRepository.class);
		machineDataService = new MachineDataService(machineRepository);
	}

	@Test
	public void testParseAndStoreParameter() throws ExecutionException, InterruptedException {


		final Map<String, Long> parameters = createMapParameters(2);
		final Machine machine = createMachine();
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));

		final ParameterRequest request = new ParameterRequest("machineKey", parameters);
		CompletableFuture<Machine> machineCompletableFuture = machineDataService.parseAndStoreParameter(request);
		Machine machineValue = machineCompletableFuture.get();
		assertNotNull(machineValue);
		assertEquals(machineValue.getKey(), machine.getKey());
		assertEquals(machineValue.getParametersMap().size(), 2);
		assertTrue(machineValue.getParametersMap().containsKey("par1"));
		assertEquals(machineValue.getParametersMap().get("par1").getParameterList().size(), 1);
	}

	@Test
	public void testParseAndStoreParameter_emptyParameters() throws ExecutionException, InterruptedException {
		final Map<String, Long> parameters = Collections.emptyMap();
		final Machine machine = createMachine();
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));

		final ParameterRequest request = new ParameterRequest("machineKey", parameters);
		CompletableFuture<Machine> machineCompletableFuture = machineDataService.parseAndStoreParameter(request);
		Machine machineValue = machineCompletableFuture.get();
		assertNotNull(machineValue);
		assertEquals(machineValue.getKey(), machine.getKey());
		assertEquals(machineValue.getParametersMap().size(), 0);
	}

	@Test
	public void testParseAndStoreParameter_alreadyWithParameters() throws ExecutionException, InterruptedException {
		final Map<String, Long> parameters = createMapParameters(1);
		final Machine machine = createMachine();
		final ParametersValues parametersValues = new ParametersValues();
		Parameter parameter = createParameter(1, "par" + 1, 1, Calendar.getInstance().getTime());
		parametersValues.getParameterList().add(parameter);
		;

		machine.getParametersMap().put("par1", parametersValues);
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));

		final ParameterRequest request = new ParameterRequest("machineKey", parameters);
		CompletableFuture<Machine> machineCompletableFuture = machineDataService.parseAndStoreParameter(request);
		Machine machineValue = machineCompletableFuture.get();
		assertNotNull(machineValue);
		assertEquals(machineValue.getKey(), machine.getKey());
		assertEquals(machineValue.getParametersMap().size(), 1);
		assertEquals(machineValue.getParametersMap().get("par1").getParameterList().size(), 2);
	}

	@Test
	public void testParseAndStoreParameter_keyNull() throws ExecutionException, InterruptedException {
		final Map<String, Long> parameters = createMapParameters(1);
		when(machineRepository.findById(null)).thenReturn(Optional.empty());

		final ParameterRequest request = new ParameterRequest(null, parameters);
		CompletableFuture<Machine> machineCompletableFuture = machineDataService.parseAndStoreParameter(request);
		Machine machineValue = machineCompletableFuture.get();
		assertNull(machineValue);
	}

	@Test
	public void testParseAndStoreParameter_machineKeyInvalid() throws ExecutionException, InterruptedException {
		final Map<String, Long> parameters = createMapParameters(1);
		when(machineRepository.findById("machineKey")).thenReturn(Optional.empty());

		final ParameterRequest request = new ParameterRequest("machineKey", parameters);
		CompletableFuture<Machine> machineCompletableFuture = machineDataService.parseAndStoreParameter(request);
		Machine machineValue = machineCompletableFuture.get();
		assertNull(machineValue);
	}

	@Test
	public void getMachineByKey() throws MachineNotFoundException {
		final Machine machine = createMachine();
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));
		machineDataService.getMachineByKey("machineKey");
	}

	@Test
	public void getMachineByKey_invalidKey() {
		when(machineRepository.findById("machineKey")).thenReturn(Optional.empty());
		assertThrows(MachineNotFoundException.class, () -> machineDataService.getMachineByKey("machineKey"));
	}

	@Test
	public void getMachineResponseLatestParamater() throws MachineNotFoundException {
		final Machine machine = createMachine();
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		ParametersValues parametersValues = new ParametersValues();
		parametersValues.getParameterList().add(createParameter(1, "par1", 10, yesterday.getTime()));
		parametersValues.getParameterList().add(createParameter(2, "par1", 2, Calendar.getInstance().getTime()));
		machine.getParametersMap().put("par1", parametersValues);
		MachineResponse machineResponseLatestParameter =
				machineDataService.getMachineResponseLatestParamater(machine.getKey());
		assertNotNull(machineResponseLatestParameter);
		assertEquals(machineResponseLatestParameter.getName(), machine.getName());
		assertEquals(machineResponseLatestParameter.getParameters().size(), 1);
		assertEquals(machineResponseLatestParameter.getParameters().get("par1"), 2);
	}

	@Test
	public void getMachineResponseLatestParamater_multipleParameters() throws MachineNotFoundException {
		final Machine machine = createMachine();
		when(machineRepository.findById("machineKey")).thenReturn(Optional.of(machine));
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);

		ParametersValues par1Values = new ParametersValues();
		par1Values.getParameterList().add(createParameter(1, "par1", 10, yesterday.getTime()));
		par1Values.getParameterList().add(createParameter(2, "par1", 2, Calendar.getInstance().getTime()));
		machine.getParametersMap().put("par1", par1Values);

		ParametersValues par2Values = new ParametersValues();
		par2Values.getParameterList().add(createParameter(3, "par2", 10, yesterday.getTime()));
		par2Values.getParameterList().add(createParameter(4, "par2", 20, Calendar.getInstance().getTime()));
		yesterday.add(Calendar.DAY_OF_MONTH, -2);
		par2Values.getParameterList().add(createParameter(5, "par2", 24, yesterday.getTime()));
		machine.getParametersMap().put("par2", par2Values);

		MachineResponse machineResponseLatestParameter =
				machineDataService.getMachineResponseLatestParamater(machine.getKey());
		assertNotNull(machineResponseLatestParameter);
		assertEquals(machineResponseLatestParameter.getName(), machine.getName());
		assertEquals(machineResponseLatestParameter.getParameters().size(), 2);
		assertEquals(machineResponseLatestParameter.getParameters().get("par1"), 2);
		assertEquals(machineResponseLatestParameter.getParameters().get("par2"), 20);
	}

	@Test
	public void summaryAllMachinesFromTheLastMinutes() {
		final Machine machine = createMachine();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);

		ParametersValues par1Values = new ParametersValues();
		par1Values.getParameterList().add(createParameter(1, "par1", 10, yesterday.getTime()));
		par1Values.getParameterList().add(createParameter(2, "par1", 2, Calendar.getInstance().getTime()));
		machine.getParametersMap().put("par1", par1Values);

		ParametersValues par2Values = new ParametersValues();
		par2Values.getParameterList().add(createParameter(3, "par2", 10, yesterday.getTime()));
		par2Values.getParameterList().add(createParameter(4, "par2", 20, Calendar.getInstance().getTime()));
		yesterday.add(Calendar.DAY_OF_MONTH, -2);
		par2Values.getParameterList().add(createParameter(5, "par2", 24, yesterday.getTime()));
		machine.getParametersMap().put("par2", par2Values);

		when(machineRepository.findAll()).thenReturn(Arrays.asList(machine));
		List<MachineSummaryResponse> machineSummaryResponses =
				machineDataService.summaryAllMachinesFromTheLastMinutes(1);
		assertNotNull(machineSummaryResponses);
		assertEquals(machineSummaryResponses.size(), 1);
		assertEquals(machineSummaryResponses.get(0).getMachineName(), machine.getName());
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().size(), 2);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMax(), 2);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMin(), 2);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getAvg(), 2);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMedian(), 2);

		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().size(), 2);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par2").getMax(), 20);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par2").getMin(), 20);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par2").getAvg(), 20);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par2").getMedian(), 20);

	}

	@Test
	public void summaryAllMachinesFromTheLastMinutes_last10Minutes3Results() {
		final Machine machine = createMachine();
		Calendar todayMinus20 = Calendar.getInstance();
		todayMinus20.add(Calendar.MINUTE, -20);

		ParametersValues par1Values = new ParametersValues();
		par1Values.getParameterList().add(createParameter(1, "par1", 100, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 11);
		par1Values.getParameterList().add(createParameter(2, "par1", 10, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(3, "par1", 3, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(4, "par1", 6, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(5, "par1", 1, todayMinus20.getTime()));
		machine.getParametersMap().put("par1", par1Values);

		when(machineRepository.findAll()).thenReturn(Arrays.asList(machine));
		List<MachineSummaryResponse> machineSummaryResponses =
				machineDataService.summaryAllMachinesFromTheLastMinutes(10);
		assertNotNull(machineSummaryResponses);
		assertEquals(machineSummaryResponses.size(), 1);
		assertEquals(machineSummaryResponses.get(0).getMachineName(), machine.getName());
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().size(), 1);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMax(), 10);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMin(), 1);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getAvg(), 5);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMedian(), 4.5);
	}

	@Test
	public void summaryAllMachinesFromTheLastMinutes_last10Minutes3Results_empty() {
		final Machine machine = createMachine();
		Calendar todayMinus20 = Calendar.getInstance();
		todayMinus20.add(Calendar.MINUTE, -40);

		ParametersValues par1Values = new ParametersValues();
		par1Values.getParameterList().add(createParameter(1, "par1", 100, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 11);
		par1Values.getParameterList().add(createParameter(2, "par1", 10, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(3, "par1", 3, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(4, "par1", 6, todayMinus20.getTime()));
		todayMinus20.add(Calendar.MINUTE, 1);
		par1Values.getParameterList().add(createParameter(5, "par1", 1, todayMinus20.getTime()));
		machine.getParametersMap().put("par1", par1Values);

		when(machineRepository.findAll()).thenReturn(Arrays.asList(machine));
		List<MachineSummaryResponse> machineSummaryResponses =
				machineDataService.summaryAllMachinesFromTheLastMinutes(10);
		assertNotNull(machineSummaryResponses);
		assertEquals(machineSummaryResponses.size(), 1);
		assertEquals(machineSummaryResponses.get(0).getMachineName(), machine.getName());
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().size(), 1);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMax(), 0);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMin(), 0);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getAvg(), 0);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMedian(), 0);
	}

	@Test
	public void summaryAllMachinesFromTheLastMinutes_multipleMachines() {
		final Machine machine = createMachine();
		ParametersValues par1Values = new ParametersValues();
		par1Values.getParameterList().add(createParameter(1, "par1", 100, Calendar.getInstance().getTime()));
		machine.getParametersMap().put("par1", par1Values);

		final Machine machine2 = createMachine();
		machine2.setName("machine2");
		ParametersValues par2Values = new ParametersValues();
		par2Values.getParameterList().add(createParameter(2, "par1", 1, Calendar.getInstance().getTime()));
		machine2.getParametersMap().put("par1", par2Values);

		when(machineRepository.findAll()).thenReturn(Arrays.asList(machine, machine2));
		List<MachineSummaryResponse> machineSummaryResponses =
				machineDataService.summaryAllMachinesFromTheLastMinutes(1);
		assertNotNull(machineSummaryResponses);
		assertEquals(machineSummaryResponses.size(), 2);
		assertEquals(machineSummaryResponses.get(0).getMachineName(), machine.getName());
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().size(), 1);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMax(), 100);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMin(), 100);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getAvg(), 100);
		assertEquals(machineSummaryResponses.get(0).getParameterSummaryResponseList().get("par1").getMedian(), 100);

		assertEquals(machineSummaryResponses.get(1).getMachineName(), machine2.getName());
		assertEquals(machineSummaryResponses.get(1).getParameterSummaryResponseList().size(), 1);
		assertEquals(machineSummaryResponses.get(1).getParameterSummaryResponseList().get("par1").getMax(), 1);
		assertEquals(machineSummaryResponses.get(1).getParameterSummaryResponseList().get("par1").getMin(), 1);
		assertEquals(machineSummaryResponses.get(1).getParameterSummaryResponseList().get("par1").getAvg(), 1);
		assertEquals(machineSummaryResponses.get(1).getParameterSummaryResponseList().get("par1").getMedian(), 1);
	}

	@Test
	public void getMachineResponseLatestParamater_invalidKey() {
		when(machineRepository.findById("machineKey")).thenReturn(Optional.empty());
		assertThrows(MachineNotFoundException.class,
				() -> machineDataService.getMachineResponseLatestParamater("machineKey"));
	}

	private Machine createMachine() {
		final Machine machine = new Machine();
		machine.setKey("machineKey");
		machine.setName("machineName");
		return machine;
	}

	private Map<String, Long> createMapParameters(final int quantity) {
		final Map<String, Long> parameters = new HashMap<>();
		for (int i = 1; i <= quantity; i++) {
			parameters.put("par" + i, (long) i);
		}
		return parameters;
	}

	private Parameter createParameter(final long id, final String name, final long value, final Date date) {
		return new Parameter(id, name, value, date);
	}

}