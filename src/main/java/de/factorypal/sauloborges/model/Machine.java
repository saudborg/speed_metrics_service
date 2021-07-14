package de.factorypal.sauloborges.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "machine")
@Data
public class Machine {

	@Id
	private String key;
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	private Map<String, ParametersValues> parametersMap = new HashMap<>();
}
