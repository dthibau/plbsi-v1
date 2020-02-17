package com.plb.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="AUT")
public class TypeContactAutre extends TypeContact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8368846731087635394L;

}
