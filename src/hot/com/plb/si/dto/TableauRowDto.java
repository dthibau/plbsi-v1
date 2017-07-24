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
	
	public String getNC() {
		return counts.get(0) != null ? ""+counts.get(0) : "0";
	}
	public String getUn() {
		return counts.get(1) != null ? ""+counts.get(1) : "0";
	}
	public String getDeux() {
		return counts.get(2) != null ? ""+counts.get(2) : "0";
	}
	public String getTrois() {
		return counts.get(3) != null ? ""+counts.get(3) : "0";
	}
	
}
