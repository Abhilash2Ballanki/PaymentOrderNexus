package com.backend.Entiry;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;


import lombok.Data;

@Data
@Entity
public class Orders {
	@Id

	private int id;
	private String item;
	private int quantity;

	@Transient
	private String email;

	@Transient
	private String bankname;

	@Transient
	private String corelation_id;

}
