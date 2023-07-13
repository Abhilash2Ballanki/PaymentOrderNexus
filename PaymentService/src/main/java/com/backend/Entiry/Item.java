package com.backend.Entiry;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Item {

	@Id

	private Integer id;

	private String name;



	private Integer amount;

}
