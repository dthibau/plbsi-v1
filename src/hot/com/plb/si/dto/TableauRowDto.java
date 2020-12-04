package com.plb.si.dto;

import java.util.HashMap;
import java.util.Map;

import com.plb.model.directory.Account;

/**
 * Une ligne du tableau de répartition.
 * @author dthibau
 *
 */
public class TableauRowDto {

	private String commercial;
	private Map<Integer,Long> counts = new HashMap<Integer,Long>();
	
	
	public String getCommercial() {
		return commercial;
	}
	public void setCommercial(String commercial) {
		this.commercial = commercial;
	}
	public Map<Integer, Long> getCounts() {
		return counts;
	}
	public void setCounts(Map<Integer, Long> counts) {
		this.counts = counts;
	}
	public void addCount(int key, long count) {
		counts.put(key,count);
	}
	
	public Long getCount(int potentiel) {
		return counts.get(potentiel) != null ? counts.get(potentiel) : 0l;
	}
	
}
