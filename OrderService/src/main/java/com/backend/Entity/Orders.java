package com.backend.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data

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
	
	@Transient
	private String jwtToken;

}
