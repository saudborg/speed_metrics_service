package de.factorypal.sauloborges.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "parameter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter implements Comparable<Parameter> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private Long value;
	private Date creationDate;

	@Override
	public int compareTo(final Parameter o) {
		return this.creationDate.compareTo(o.getCreationDate());
	}
}
