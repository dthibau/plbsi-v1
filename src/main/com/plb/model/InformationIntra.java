package com.plb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.plb.model.directory.Account;

@Entity
@Table(name = "information_intra")

public class InformationIntra implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7440001746395961058L;
	
	//Attributs de l'objet
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "generator_client2")
	@SequenceGenerator (name = "generator_client2", sequenceName = "windev_seq2", initialValue = 3000, allocationSize = 1)
	@Column(name = "id_information_intra")
	private int idInforamtionIntra;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datecreation")
	private Date dateCreation;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datemodification")
	private Date dateModification;
	
	@ManyToOne
	@JoinColumn(name="id_commercial_account")
	Account commercial;
	
	@Column(name = "duree")
	private String duree;
	
	@Column(name = "prob_transformation", columnDefinition="text")
	@Lob
	private String probTransformation;
	
	@Column(name = "profil_participant", columnDefinition="text")
	@Lob
	private String profilParticipant;
	
	@Column(name = "pre_requis", columnDefinition="text")
	@Lob
	private String preRequis;
	
	@Column(name = "connaissance_sujet")
	private String connaissanceSujet;

	@Column(name = "contraintes_particulieres", columnDefinition="text")
	@Lob
	private String contraintesParticulieres;

	@Column(name = "objectif", columnDefinition="text")
	@Lob
	private String objectif;

	@Column(name = "coordonnees_contact_tech", columnDefinition="text")
	@Lob
	private String coordonneesContactTech;
	
	@Column(name = "coordonnees_contact_support", columnDefinition="text")
	@Lob
	private String coordonneesContactsupport;
	
	@Column(name = "heure_deb")
	private String heureDeb;
	
	@Column(name = "heure_fin")
	private String heureFin;
	
	@Column(name = "statut_intra")
	private String statutIntra;
	
//	@Column(name = "cv")
//	private Integer cv;
	
	@Column(name="format")
	private String format;
	
	@Column(name="certification")
	private String certification;
	
//	@Column(name = "frais")
//	private Integer frais;
	
	
	
	@Column(name = "montant_frais")
	private Float montantFrais;
	
	@Column(name = "raison_perte", columnDefinition="text")
	@Lob
	private String raisonPerte;
	
	@Column(name = "note_reservation", columnDefinition="text")
	@Lob
	private String noteReservation;
	
	@Column(name = "valide")
	private Integer valide;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_formulaire_contact")
	private Prospect prospect;
	
	@OneToMany(mappedBy="informationIntra",cascade=CascadeType.ALL)
	List<IntraReferenceSpe> intraReferenceSpe = new ArrayList<IntraReferenceSpe>();
	
	//Permettant de constater l'évolution du statut entre l'admin intra et les commerciaux
	
	@Column(name = "changement_com")
	private Integer changementCom;
	
	@Column(name = "changement_admin_intra")
	private Integer changementAdminIntra;
	
	//GETTEURS AND SETTEURS
	
	
	public int getIdInforamtionIntra() {
		return idInforamtionIntra;
	}

	public void setIdInforamtionIntra(int idInforamtionIntra) {
		this.idInforamtionIntra = idInforamtionIntra;
	}

	public Account getCommercial() {
		return commercial;
	}

	public void setCommercial(Account commercial) {
		this.commercial = commercial;
	}

	public String getDuree() {
		return duree;
	}

	public void setDuree(String duree) {
		this.duree = duree;
	}

	public String getProbTransformation() {
		return probTransformation;
	}

	public void setProbTransformation(String probTransformation) {
		this.probTransformation = probTransformation;
	}

	public String getProfilParticipant() {
		return profilParticipant;
	}

	public void setProfilParticipant(String profilParticipant) {
		this.profilParticipant = profilParticipant;
	}

	public String getPreRequis() {
		return preRequis;
	}

	public void setPreRequis(String environnementTech) {
		this.preRequis = environnementTech;
	}

	public String getConnaissanceSujet() {
		return connaissanceSujet;
	}

	public void setConnaissanceSujet(String connaissanceSujet) {
		this.connaissanceSujet = connaissanceSujet;
	}

	public String getContraintesParticulieres() {
		return contraintesParticulieres;
	}

	public void setContraintesParticulieres(String contraintesParticulieres) {
		this.contraintesParticulieres = contraintesParticulieres;
	}

	public String getObjectif() {
		return objectif;
	}

	public void setObjectif(String objectif) {
		this.objectif = objectif;
	}

	public String getCoordonneesContactTech() {
		return coordonneesContactTech;
	}

	public void setCoordonneesContactTech(String coordonneesContactTech) {
		this.coordonneesContactTech = coordonneesContactTech;
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
		prospect.setInformationIntra(this);
	}

	public String getStatutIntra() {
		return statutIntra;
	}

	public void setStatutIntra(String statutIntra) {
		this.statutIntra = statutIntra;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getCoordonneesContactsupport() {
		return coordonneesContactsupport;
	}

	public void setCoordonneesContactsupport(String coordonneesContactsupport) {
		this.coordonneesContactsupport = coordonneesContactsupport;
	}

	public String getHeureDeb() {
		return heureDeb;
	}

	public void setHeureDeb(String heureDeb) {
		this.heureDeb = heureDeb;
	}

	@Transient
	public String getOnlyHeureDeb() {
		return _extractHeure(heureDeb);
	}
	@Transient
	public String getOnlyMinuteDeb() {
		return _extractMinute(heureDeb);
	}
	public String getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(String heureFin) {
		this.heureFin = heureFin;
	}

	@Transient
	public String getOnlyHeureFin() {
		return _extractHeure(heureFin);
	}
	@Transient
	public String getOnlyMinuteFin() {
		return _extractMinute(heureFin);
	}
	private String _extractHeure(String horaire) {
		if (horaire != null && !":".equals(horaire)
				&& !"".equals(horaire)	) {
			try {
				String[] t = horaire.split(":");
				if ( t.length == 2 ) {
					return t[0];
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private String _extractMinute(String horaire) {
		if (horaire != null && !":".equals(horaire)
				&& !"".equals(horaire)	) {
			try {
				String[] t = horaire.split(":");
				if ( t.length == 2 ) {
					return t[1];
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public List<IntraReferenceSpe> getIntraReferenceSpe() {
		return intraReferenceSpe;
	}

	public void setIntraReferenceSpe(List<IntraReferenceSpe> intraReferenceSpe) {
		this.intraReferenceSpe = intraReferenceSpe;
	}

	public String getRaisonPerte() {
		return raisonPerte;
	}

	public void setRaisonPerte(String raisonPerte) {
		this.raisonPerte = raisonPerte;
	}

	public Integer getChangementCom() {
		return changementCom;
	}

	public void setChangementCom(Integer changementCom) {
		this.changementCom = changementCom;
	}

	public Integer getChangementAdminIntra() {
		return changementAdminIntra;
	}

	public void setChangementAdminIntra(Integer changementAdminIntra) {
		this.changementAdminIntra = changementAdminIntra;
	}

	public Float getMontantFrais() {
		return montantFrais;
	}

	public void setMontantFrais(Float montantFrais) {
		this.montantFrais = montantFrais;
	}

	public Integer getValide() {
		return valide;
	}

	public void setValide(Integer valide) {
		this.valide = valide;
	}

	public String getNoteReservation() {
		return noteReservation;
	}

	public void setNoteReservation(String noteReservation) {
		this.noteReservation = noteReservation;
	}
	
}
