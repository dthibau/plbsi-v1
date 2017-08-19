package com.plb.model.devis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.directory.Account;

@SuppressWarnings("serial")
@Entity
@Table(name = "devis")
public class Devis implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "client")
	@NotNull
	private String client;

	@Column(name = "contact_client")
	@NotNull
	private String contactClient;

	@NotNull
	private int nbParticipants;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_devis")
	List<DevisSession> sessions = new ArrayList<DevisSession>();

	private float remise = 0f;
	private String remiseUnit = "%";

	@ManyToOne
	private Account auteur;

	@ManyToOne
	private Formation formation;

	@ManyToOne
	private Prospect prospect;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(columnDefinition="bit")
	private Boolean particulier;
	@Column(columnDefinition="bit")
	private Boolean etranger;

	public Devis() {

	}
	
	public Devis(Prospect prospect) {
		this.prospect = prospect;
		this.client = prospect.getSociete();
		this.contactClient = prospect.getNomComplet();
		
		if (prospect.getProspectDetail() != null) {
			this.remise = prospect.getProspectDetail().getRemise() != null ? prospect.getProspectDetail().getRemise() : 0f;
			if ("Particulier".equals(prospect.getProspectDetail()
					.getNatureClient())) {
				setParticulier(true);
			} else if ("Etranger".equals(prospect.getProspectDetail()
					.getNatureClient())) {
				setEtranger(true);
			} else {
				setParticulier(false);
				setEtranger(false);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClient() {
		return client;
	}

	@Transient
	public int getNumero() {
		return getId();
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getContactClient() {
		return contactClient;
	}

	public void setContactClient(String contactClient) {
		this.contactClient = contactClient;
	}

	public int getNbParticipants() {
		return nbParticipants;
	}

	public void setNbParticipants(int nbParticipants) {
		this.nbParticipants = nbParticipants;
	}

	public List<DevisSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<DevisSession> sessions) {
		this.sessions = sessions;
	}

	public void addSession(DevisSession session) {
		this.sessions.add(session);
	}

	public void removeSession(DevisSession session) {
		this.sessions.remove(session);
	}

	public float getRemise() {
		return remise;
	}

	public void setRemise(float remise) {
		this.remise = remise;
	}

	public String getRemiseUnit() {
		return remiseUnit;
	}

	public void setRemiseUnit(String remiseUnit) {
		this.remiseUnit = remiseUnit;
	}

	public Account getAuteur() {
		return auteur;
	}

	public void setAuteur(Account auteur) {
		this.auteur = auteur;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getParticulier() {
		return particulier;
	}

	public void setParticulier(Boolean particulier) {
		this.particulier = particulier;
	}

	public Boolean getEtranger() {
		return etranger;
	}

	public void setEtranger(Boolean etranger) {
		this.etranger = etranger;
	}

	@Transient
	public Object getCopy() {
		Devis devis = new Devis();
		devis.setAuteur(getAuteur());
		devis.setClient(getClient());
		devis.setContactClient(getContactClient());
		devis.setDate(new Date());
		devis.setEtranger(devis.getEtranger());
		devis.setFormation(getFormation());
		devis.setParticulier(getParticulier());
		devis.setRemise(getRemise());
		List<DevisSession> sessions = new ArrayList<DevisSession>();
		for (DevisSession s : getSessions()) {
			sessions.add(s);
		}
		devis.setSessions(sessions);

		return devis;
	}

}
