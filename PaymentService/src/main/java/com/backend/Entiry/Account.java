package com.backend.Entiry;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Account {
	@Id
	private Integer id;
	private String accountId;
	private Integer amount;

}
