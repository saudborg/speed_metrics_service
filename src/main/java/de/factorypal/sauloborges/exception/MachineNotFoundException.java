package de.factorypal.sauloborges.exception;

public class MachineNotFoundException extends Exception {

	public MachineNotFoundException(final String key) {
		super("machineKey [" + key + "] doesn't exists");
	}
}
