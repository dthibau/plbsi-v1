package com.plb.si.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jboss.seam.core.SeamResourceBundle;

import com.plb.model.Prospect;
import com.plb.model.ProspectDetail;
import com.plb.model.directory.Role;
import com.plb.si.util.Labels;

public class ProspectDto implements Serializable, Comparable<ProspectDto> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4145594650368208106L;
	public static ResourceBundle bundle = SeamResourceBundle.getBundle();
	public static String statut1 = bundle.getString("prospect.nonAffecte");
	public static String statut2 = bundle.getString("prospect.gagne");
	private Prospect prospect;
	// Le rôle visualisant le prospect
	private Role role;

	public ProspectDto(Prospect prospect, Role role) {
		this.prospect = prospect;
		this.role = role;
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public Integer getIdProspect() {
		return prospect.getIdProspect();
	}

	// Fonction permettant d'aattribuer une couleur a un statut de prospect
	public String getColor() {
		String couleur = "";

		if (statut1.equals(prospect.getStatut())
				&& (role.equals(Role.MANAGER) || role.equals(Role.DISPATCHER))) {
			couleur = "#fe6f5e";
		} else if ("En attente".equals(prospect.getStatut())
				&& role.equals(Role.COMMERCIAL)) {
			couleur = "#fe6f5e";
		} else if ("En cours".equals(prospect.getStatut())) {
			couleur = "#d3d3d3";
		} else if (statut2.equals(prospect.getStatut())) {
			couleur = "#98fb98";
		} else if ("Perdu".equals(prospect.getStatut())) {
			couleur = "#987654";
		} else if ("Abandon".equals(prospect.getStatut())) {
//			couleur = "#5A5983";
			couleur = "#AC9A68";
			
		}
//		if (getAsuivre()) {
//			couleur = "#D59730";
//		}
		return couleur;
	}

	public static List<ProspectDto> buildDTO(List<Prospect> prospects, Role role) {
		List<ProspectDto> ret = new ArrayList<ProspectDto>();
		for (Prospect prospect : prospects) {
			ret.add(new ProspectDto(prospect, role));
		}
		return ret;
	}

	public String getStatut() {
		return prospect.getStatut();
	}

	public ProspectDetail getProspectDetail() {
		return prospect.getProspectDetail();
	}

	public Date getDateCreation() {
		return prospect.getDateCreation();
	}

	public String getDateDevis() {
		return prospect.getProspectDetail() != null
				&& prospect.getProspectDetail().getDatedevis() != null ? Labels.dateFormat
				.format(prospect.getProspectDetail().getDatedevis())
				: "Non envoyé";
	}

	public String getDateSession() {
		return prospect.getProspectDetail() != null
				&& prospect.getProspectDetail().getDate() != null ? prospect
				.getProspectDetail().getDate() : "N/C";
	}

	public String getSociete() {
		return (prospect.getSociete() != null && prospect.getSociete().length() > 0) ? prospect
				.getSociete() : bundle.getString("prospect.nonRenseigne");
	}

	public String getReference() {
		return prospect.getReference();
	}

	public boolean getAsuivre() {
		return prospect.getProspectDetail() != null ? prospect
				.getProspectDetail().getAsuivre() : false;
	}

	@Override
	public int compareTo(ProspectDto o) {

		return -(prospect.getDateCreation().compareTo(o.getProspect()
				.getDateCreation()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((prospect == null) ? 0 : prospect.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProspectDto other = (ProspectDto) obj;
		if (prospect == null) {
			if (other.prospect != null)
				return false;
		} else if (!prospect.equals(other.prospect))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProspectDto [prospect=" + prospect + ", role=" + role + "]";
	}

	public String getPotentielAsString() {
		if (prospect != null && prospect.getProspectDetail() != null) {
			return Labels.getString("prospect.potentiel."
					+ prospect.getProspectDetail().getPotentiel());
		} else {
			return "";
		}
	}
}
