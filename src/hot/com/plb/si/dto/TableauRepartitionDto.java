package com.plb.si.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableauRepartitionDto {

	private final List<TableauRowDto> rows;
	
	Map<Integer,Long> totalsPotentiel = new HashMap<Integer, Long>();
	
	public TableauRepartitionDto(List<TableauRowDto> rows) {
		this.rows = rows;	
		for ( TableauRowDto row : rows ) {
			totalsPotentiel.put(0, totalsPotentiel.getOrDefault(0, 0l) + row.getCount(0));
			totalsPotentiel.put(1, totalsPotentiel.getOrDefault(1, 0l) + row.getCount(1));
			totalsPotentiel.put(2, totalsPotentiel.getOrDefault(2, 0l) + row.getCount(2));
			totalsPotentiel.put(3, totalsPotentiel.getOrDefault(3, 0l) + row.getCount(3));
			totalsPotentiel.put(4, totalsPotentiel.getOrDefault(4, 0l) + row.getCount(4));
		}
		
	}

	public List<TableauRowDto> getRows() {
		return rows;
	}
	
	public Long getTotal(int potentiel) {
		return totalsPotentiel.get(potentiel);
	}
	
	public Double getPourcent(TableauRowDto row, int potentiel) {
		if ( row != null )
		return totalsPotentiel.get(potentiel) != null && totalsPotentiel.get(potentiel) != 0 ? row.getCount(potentiel).doubleValue() / totalsPotentiel.get(potentiel) : 0d;
		else 
			return 0d;
	}
}
