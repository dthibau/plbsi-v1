package com.plb.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="SI")
public class TypeContactSI extends TypeContact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6050321760534267588L;



}
