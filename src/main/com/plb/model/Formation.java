package com.plb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.apache.solr.analysis.HTMLStripCharFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.CharFilterDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.validator.constraints.Length;

import com.plb.dto.SessionDto;
import com.plb.dto.SessionOrganismeDto;
import com.plb.dto.SessionOrganismesDto;
import com.plb.util.HTMLUtils;
import com.plb.util.Util;

@Entity
@Indexed
@Table(name = "formation")
@AnalyzerDef(name = "htmlAnalyzer", charFilters = { @CharFilterDef(factory = HTMLStripCharFilterFactory.class) }, tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class))
public class Formation implements Serializable, Comparable<Formation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -211404718465836235L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "formationGenerator")
	@SequenceGenerator(name = "formationGenerator", sequenceName = "FORMATION_ID", initialValue = 700000, allocationSize = 1)
	@Column(name = "id_formation")
	private int idFormation;
	@Column(name = "for_libelle", columnDefinition = "text")
	@Field
	private String libelle;
	@Column(name = "for_toplibelle", columnDefinition = "text")
	private String topLibelle;
	
	@Column(name = "for_precision_libelle", columnDefinition = "text")
	@Field
	private String precision;
	@Column(name = "for_prix", columnDefinition = "decimal")
	private float prix;
	@Temporal(TemporalType.DATE)
	private Date lastUpdatePrix;
	@Column(name = "for_duree", columnDefinition = "decimal")
	@Min(value = 1)
	private int duree;
	@Column(name = "for_reference", columnDefinition = "text")
	@Field
	private String reference;
	@Column(name = "for_remarques", columnDefinition = "mediumtext")
	@Field
	private String remarques = "";

	@Lob
	private String statut;

	@Column(name = "for_origine", columnDefinition = "text")
	@Field
	private String origine;
	@Column(name = "for_mot_cle_primaire")
	@Field
	@Length(max = 50)
	private String motClePrimaire;
	@Column(name = "for_balise_title", columnDefinition = "mediumtext")
	private String baliseTitle;

	@Column(name = "for_url", columnDefinition = "text")
	private String url;

	@Column(name = "for_type", columnDefinition = "text")
	private String type;

	@Column(name = "for_visible", columnDefinition = "enum")
	private String visible = "non";

	@Column(name = "tar_code_intra", columnDefinition = "char")
	String tarifIntra = "";

	@Column(name = "remise", columnDefinition = "varchar(3)")
	@Length(max=3)
	String remise = "";
	
	@Lob
	@Column(name = "moyens_pedagogiques", columnDefinition = "longtext")
	String moyensPedagogiques = "";
	
	@Lob
	@Column(name = "modalites_suivi", columnDefinition = "longtext")
	String modalitesSuivi = "";
	
	@Column(name = "for_nouveaute", columnDefinition = "enum")
	String nouveaute = "non";

	String libreIntra;

	@ManyToOne
	@JoinColumn(name = "id_categorie")
	private Categorie categorie;

	@Column(name = "for_rang_categorie")
	private Integer rangCategorie;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formation", fetch = FetchType.EAGER)
	@OrderBy("rang ASC")
	private List<FormationFiliere> formationFilieres = new ArrayList<FormationFiliere>();

	@OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("debut")
	private List<Session> sessions = new ArrayList<Session>();

	@OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FormationPartenaire> formationsPartenaire = new ArrayList<FormationPartenaire>();

	@Lob
	@Column(name = "for_balise_description", columnDefinition = "mediumtext")
	private String baliseDescription;
	@Lob
	@Column(name = "for_balise_keywords", columnDefinition = "mediumtext")
	@Field
	private String baliseKeyWords;
	@Lob
	private String campagneAdWords;

	@Column(name = "for_cours_officiel")
	private String officiel;

	@Column(columnDefinition = "bit")
	private Boolean support;
	@Column(columnDefinition = "bit")
	private Boolean plbInter;
	@Length(max = 255)
	private String coursPlbCataloguePartenaire;

	@ManyToOne(optional = true)
	private FormationMutualisees formationMutualisees;

	@ManyToMany
	@JoinTable(name = "formation_formation", joinColumns = { @JoinColumn(name = "id_formation_principale", referencedColumnName = "id_formation") }, inverseJoinColumns = { @JoinColumn(name = "id_formation_suivante", referencedColumnName = "id_formation") })
	private List<Formation> formationAssociees;

	@Temporal(TemporalType.TIMESTAMP)
	private Date archivedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "for_date_creation")
	private Date dateCreation;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "for_date_derniere_modif")
	private Date dateModification;

	@Lob
	private String argumentaire;

	// Contenu web
	@Lob
	@Column(columnDefinition = "mediumtext")
	private String descriptif;
	
	@Lob
	@Column(name = "for_objectifs_operationnels", columnDefinition = "mediumtext")
	private String objectifs_operationnels;
	
	@Lob
	@Column(name = "for_objectifs", columnDefinition = "mediumtext")
	private String objectifs_pedagogiques;

	@Lob
	@Column(name = "for_prerequis", columnDefinition = "mediumtext")
	private String prerequis;

	@Lob
	@Column(name = "for_travaux_pratiques", columnDefinition = "mediumtext")
	private String travauxPratiques;

	@Lob
	@Column(name = "for_infos", columnDefinition = "mediumtext")
	private String infos;

	@Column(name = "for_replace", columnDefinition = "text")
	private String replace;

	@Lob
	@Column(name = "for_participants", columnDefinition = "mediumtext")
	private String participants;

	@Lob
	@Column(name = "for_contenu", columnDefinition = "mediumtext")
	@Field
	private String contenu;

	@Lob
	@Column(name = "for_description", columnDefinition = "mediumtext")
	@Field
	private String description;

	@Column(name = "for_top10", columnDefinition = "tinyint")
	private Integer top10 = 0;

	private String certification;

	private String distance = "Non";

	@Column(name = "for_eligible_cpf", columnDefinition = "tinyint")
	private Boolean eligibleCpf;

	private String codeCpf;
	
	@Column(name = "for_elearning", columnDefinition = "tinyint")
	private Boolean elearning;

	@Column(name = "for_niveau")
	private String niveau = "Fondamental";

	@Column(name = "data_version")
	private Integer dataVersion = 3;
	
	@Column(name = "autre_objectif_simple")
	private String autreObjectifSimple;

	@Transient
	public boolean contains(Partenaire partenaire) {
		// Force loading of a lazy association
		List<FormationPartenaire> fps = getFormationsPartenaire();
		for (FormationPartenaire fp : fps) {
			if (fp.getPartenaire().equals(partenaire)) {
				return true;
			}
		}
		return false;
	}

	public Formation getCopy() {
		Formation ret = new Formation();
		ret.setReference(reference);
		ret.setLibelle(libelle);
		ret.setCategorie(categorie);
		ret.setPrecision(precision);
		ret.setStatut(statut);
		ret.setDuree(duree);
		ret.setMotClePrimaire(motClePrimaire);
		ret.setBaliseTitle(baliseTitle);
		ret.setCampagneAdWords(campagneAdWords);
		ret.setRemarques(remarques);
		ret.setPrix(prix);
		ret.setLibreIntra(libreIntra);
		ret.setOrigine(origine);
		ret.setCoursPlbCataloguePartenaire(coursPlbCataloguePartenaire);
		ret.setPlbInter(plbInter);
		ret.setSupport(support);
		ret.setArgumentaire(argumentaire);

		for (Session session : sessions) {
			ret.addSession(session);
		}
		ret.setType(type);
		for (FormationPartenaire fp : formationsPartenaire) {
			ret.addFormationPartenaire(fp);
		}

		return ret;

	}

	public int getIdFormation() {
		return idFormation;
	}

	public void setIdFormation(int idFormation) {
		this.idFormation = idFormation;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getTopLibelle() {
		return topLibelle;
	}

	public void setTopLibelle(String topLibelle) {
		this.topLibelle = topLibelle;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public float getPrix() {
		return prix;
	}

	public void setPrix(float prix) {
		this.prix = prix;
	}

	public Date getLastUpdatePrix() {
		return lastUpdatePrix;
	}

	public void setLastUpdatePrix(Date lastUpdatePrix) {
		this.lastUpdatePrix = lastUpdatePrix;
	}

	public boolean hasObsoleteTarif(Date lastUpdate) {
		return (lastUpdatePrix == null || lastUpdatePrix.before(lastUpdate));
	}

	public boolean hasObsoleteSession(int year) {
		for (FormationPartenaire fp : getAllFormationsPartenaire()) {
			if (getSessions(fp.getPartenaire().getNom(), year).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasObsoletePartenaireSession(int year) {
		for (FormationPartenaire fp : getFormationsPartenaire()) {
			if (getSessions(fp.getPartenaire().getNom(), year).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public String getTarifIntra() {
		return tarifIntra;
	}

	public void setTarifIntra(String tarifIntra) {
		this.tarifIntra = tarifIntra;
	}

	public String getNouveaute() {
		return nouveaute;
	}

	public void setNouveaute(String nouveaute) {
		this.nouveaute = nouveaute;
	}

	// This buisiness rule is also present in FormationDTO
	public boolean isintra() {
		return getPrix() == 0;
	}

	public String getLibreIntra() {
		return libreIntra;
	}

	public void setLibreIntra(String libreIntra) {
		this.libreIntra = libreIntra;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Transient
	public String getUrlPlb() {
		if (getUrl() == null || getFilierePrincipale().getUrl() == null) {
			return null;
		}
		return "/formation/" + getFilierePrincipale().getUrl() + "/" + getUrl()
				+ "," + getFilierePrincipale().getId() + "-" + getIdFormation()
				+ ".php";
	}

	@Transient
	public Filiere getFilierePrincipale() {
		return getCategorie() != null ? getCategorie().getFiliere() : null;
		
	}

	@Transient
	public int getRangFilierePrincipale() {
		return getRangCategorie();
		
	}

	@Transient
	public String getUrlPdf() {

		return getReference() != null ? "/Formation/Telecharger?reference="
				+ getReference()
				: "";
	}

	@Transient
	public String getUrlRTF() {

		return getReference() != null ? "/home/rtf?reference="
				+ getReference()
				: "";
	}

	@Transient
	public String getUrlBO() {

		return getIdFormation() != 0 ? "http://www.plb.fr/admin/gestion-formations.php?mode=mod&formation="
				+ getIdFormation()
				: null;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarques() {
		return remarques;
	}

	public void setRemarques(String remarques) {
		this.remarques = remarques;
	}

	@Transient
	public String getShortRemarques() {
		if (remarques != null) {
			String remarquesData = HTMLUtils.getData(remarques);
			return remarquesData.length() < 50 ? remarquesData : remarquesData
					.substring(0, 46) + " ...";
		}
		return null;
	}

	@Transient
	public String getMultiLigneRemarques() {
		return remarques != null ? remarques.replace("\n", "<br/>") : "";
	}

	@Transient
	public String getMultiLigneArgumentaire() {
		return argumentaire != null ? argumentaire.replace("\n", "<br/>")
				: null;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public void addSession(Session session) {
		sessions.add(session);
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}

	/**
	 * Année en cours
	 * 
	 * @param month
	 * @return
	 */
	@Transient
	public String getAllSessionsAsString(int month) {
		StringBuilder sb = new StringBuilder();
		List<Date> dates = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		boolean bFirst = true;

		for (Session s : sessions) {
			if (s.getYear() == cal.get(Calendar.YEAR) && s.getMonth() == month
					&& !dates.contains(s.getDebut())) {
				dates.add(s.getDebut());
				if (bFirst) {
					sb.append(s.getDay() + "-" + s.getDayFin());
					bFirst = false;
				} else {
					sb.append(";" + s.getDay() + "-" + s.getDayFin());
				}
			}
		}

		return sb.toString();
	}

	@Transient
	public String getAllSessionsAsString(Date month) {
		StringBuilder sb = new StringBuilder();
		List<Date> dates = new ArrayList<Date>();
		boolean bFirst = true;
		for (Session s : sessions) {
			if (Util.monthEqual(s.getDebut(), month)
					&& !dates.contains(s.getDebut())) {
				dates.add(s.getDebut());
				if (bFirst) {
					sb.append(s.getDay() + "-" + s.getDayFin());
					bFirst = false;
				} else {
					sb.append(";" + s.getDay() + "-" + s.getDayFin());
				}
			}
		}

		// Pour l'export excel on renvoie un caractère blanc
		return sb.length() > 0 ? sb.toString() : " ";
	}

	@Transient
	public List<Session> getNextSessions() {
		List<Session> ret = new ArrayList<Session>();
		Date now = new Date();

		for (Session s : getSessions()) {
			if (s.getDebut().after(now)) {
				ret.add(s);
			}
		}
		Collections.sort(ret);
		return ret;

	}

	@Transient
	public List<Session> getNextSessionsPartenaire(FormationPartenaire fp) {
		List<Session> ret = new ArrayList<Session>();
		Date now = new Date();

		for (Session s : fp.getSessions()) {
			if (s.getDebut().after(now)) {
				ret.add(s);
			}
		}
		return ret;

	}

	@Transient
	public List<SessionOrganismeDto> getNextSessionsOrganismeDto() {
		List<SessionOrganismeDto> ret = new ArrayList<SessionOrganismeDto>();
		Date now = new Date();
		List<Session> sessionsPLB = getSessionsPLB();
		for (Session s : sessionsPLB) {
			if (s.getDebut().after(now)) {
				ret.add(new SessionOrganismeDto("PLB", s));
			}
		}
		for (FormationPartenaire fp : getFormationsPartenaire()) {

			for (Session s : fp.getSessions()) {
				if (s.getDebut().after(now)) {
					ret.add(new SessionOrganismeDto(
							fp.getPartenaire().getNom(), s));
				}
			}
		}
		Collections.sort(ret);
		return ret;

	}

	@Transient
	public List<SessionOrganismesDto> getNextSessionsOrganismesDto() {
		List<SessionOrganismeDto> sods = getNextSessionsOrganismeDto();
		List<SessionOrganismesDto> ret = new ArrayList<SessionOrganismesDto>();

		for (SessionOrganismeDto sod : sods) {
			boolean bTrouve = false;
			for (SessionOrganismesDto sosd : ret) {
				if (sosd.getSession().getDebut()
						.equals(sod.getSession().getDebut())) {
					bTrouve = true;
					sosd.addOrganisme(sod.getOrganisme());
					break;
				}
			}
			if (!bTrouve) {
				SessionOrganismesDto sosd = new SessionOrganismesDto(
						sod.getSession());
				sosd.addOrganisme(sod.getOrganisme());
				ret.add(sosd);
			}
		}

		return ret;

	}

	@Transient
	public List<SessionOrganismeDto> getAllSessionsOrganismeDto(int year) {
		List<SessionOrganismeDto> ret = new ArrayList<SessionOrganismeDto>();
		List<Session> sessionsPLB = getSessionsPLB();
		for (Session s : sessionsPLB) {
			if (s.getYear() == year) {
				ret.add(new SessionOrganismeDto("PLB", s));
			}
		}
		for (FormationPartenaire fp : getFormationsPartenaire()) {

			for (Session s : fp.getSessions()) {
				if (s.getYear() == year) {
					ret.add(new SessionOrganismeDto(
							fp.getPartenaire().getNom(), s));
				}
			}
		}
		Collections.sort(ret);
		return ret;

	}

	public List<SessionDto> getAllSessionsDto(int year) {

		List<SessionDto> currentSessions = new ArrayList<SessionDto>();
		currentSessions.add(new SessionDto(getSessionsPLB(), year));
		for (FormationPartenaire fp : getFormationsPartenaire()) {
			currentSessions.add(new SessionDto(fp, year));
		}

		return currentSessions;
	}

	public List<Session> getSessions(String organisme, int year) {
		List<Session> ret = new ArrayList<Session>();
		if (organisme.equals("PLB")) {
			List<Session> sessionsPLB = getSessionsPLB();
			for (Session s : sessionsPLB) {
				if (s.getYear() == year) {
					ret.add(s);
				}
			}
		} else {
			for (FormationPartenaire fp : formationsPartenaire) {
				if (fp.getPartenaire().getNom().equals(organisme)) {
					for (Session s : fp.getSessions()) {
						if (s.getYear() == year) {
							ret.add(s);
						}
					}
				}
			}
		}
		return ret;
	}

	public List<Session> getSessionsPLB() {
		List<Session> ret = new ArrayList<Session>();
		for (Session session : sessions) {
			boolean bTrouve = false;
			for (FormationPartenaire fp : formationsPartenaire) {
				if (fp.getSessions().contains(session)) {
					bTrouve = true;
					break;
				}
			}
			if (!bTrouve) {
				ret.add(session);
			}
		}
		return ret;
	}

	@Transient 
	public List<FormationFiliere> getCategoriesSecondaires() {
		return formationFilieres.stream().filter(ff -> !ff.isPrincipale()).collect(Collectors.toList());
	}

	public List<FormationFiliere> getFormationFilieres() {
		return formationFilieres;
	}

	public void setFormationFilieres(List<FormationFiliere> formationFilieres) {
		this.formationFilieres = formationFilieres;
	}

	public void addFormationFiliere(FormationFiliere formationFiliere) {
		this.formationFilieres.add(formationFiliere);
		formationFiliere.setFormation(this);
	}

	public void removeFormationFiliere(FormationFiliere formationFiliere) {
		this.formationFilieres.remove(formationFiliere);
	}

	@Transient
	public List<FormationFiliere> getCopyFormationFilieres() {
		List<FormationFiliere> ret = new ArrayList<FormationFiliere>();
		for (FormationFiliere ff : formationFilieres) {
			ret.add(ff.getCopy());
		}
		return ret;
	}

	@Transient
	public boolean contains(Filiere filiere) {
		for (FormationFiliere ff : formationFilieres) {
			if (ff.getFiliere().equals(filiere)) {
				return true;
			}
		}
		return false;
	}
	@Transient
	public FormationFiliere getFormationFiliere(Filiere filiere) {
		for (FormationFiliere ff : formationFilieres) {
			if (ff.getFiliere().equals(filiere)) {
				return ff;
			}
		}
		return null;
	}
	@Transient
	public boolean contains(Categorie categorie) {
		for (FormationFiliere ff : formationFilieres) {
			if (ff.getCategorie() != null && ff.getCategorie().equals(categorie) ) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public List<Filiere> getFilieres() {
		List<Filiere> ret = new ArrayList<Filiere>();
		for (FormationFiliere f : getFormationFilieres()) {
			ret.add(f.getFiliere());
		}
		return ret;
	}

	@Transient
	public String getFilieresAsString() {
		
		StringBuffer sbf = new StringBuffer();
		
		sbf.append("<b>").append(categorie.getFiliere().getLibelle()).append("</b>");
		for (FormationFiliere f : getFormationFilieres()) {
			if (!f.isPrincipale()) {		
				sbf.append("<br/>").append(f.getLibelle());
			}
		}
		return sbf.toString();
	}
	@Transient
	public String getCategoriesAsString() {
		
		StringBuffer sbf = new StringBuffer();
		
		sbf.append("<b>").append(categorie.getLibelle()).append("</b>");
		for (FormationFiliere f : getFormationFilieres()) {
			if (!f.isPrincipale()) {		
				sbf.append("<br/>").append(f.getCategorie().getLibelle());
			}
		}
		return sbf.toString();
	}


	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public Integer getRangCategorie() {
		return rangCategorie;
	}

	public void setRangCategorie(Integer rangCategorie) {
		this.rangCategorie = rangCategorie;
	}

	public List<FormationPartenaire> getFormationsPartenaire() {
		return formationsPartenaire;
	}

	public void setFormationsPartenaire(
			List<FormationPartenaire> formationPartenaire) {
		this.formationsPartenaire = formationPartenaire;
	}

	public void addFormationPartenaire(FormationPartenaire fp) {
		formationsPartenaire.add(fp);
	}

	public void removeFormationPartenaire(FormationPartenaire fp) {

		formationsPartenaire.remove(fp);

	}

	@Transient
	public List<FormationPartenaire> getCopyFormationsPartenaire() {
		List<FormationPartenaire> ret = new ArrayList<FormationPartenaire>();
		for (FormationPartenaire fp : formationsPartenaire) {
			ret.add(fp.getCopy());
		}
		return ret;
	}

	public void addSessionPartenaire(FormationPartenaire formationPartenaire) {
		for (FormationPartenaire fp : formationsPartenaire) {
			if (fp.getPartenaire().equals(formationPartenaire.getPartenaire())) {
				fp.addSessions(formationPartenaire.getSessions());
				return;
			}
		}
		// Partenaire pas encore attach� sur cette formation =>
		addFormationPartenaire(formationPartenaire);
	}

	@Transient
	public String getPartenairesAsString() {
		StringBuffer sbf = new StringBuffer();
		boolean bFirst = true;
		for (FormationPartenaire fp : getFormationsPartenaire()) {
			if (bFirst) {
				sbf.append(fp.getPartenaire().getNom());
				bFirst = false;
			} else {
				sbf.append("<br/>" + fp.getPartenaire().getNom());
			}
		}
		return sbf.toString();
	}

	@Transient
	public String getFormationsPartenairesAsString() {
		StringBuffer sbf = new StringBuffer();
		boolean bFirst = true;
		for (FormationPartenaire fp : getFormationsPartenaire()) {
			if (bFirst) {
				sbf.append(fp.getPartenaire().getNom() + "(" + fp.getPrix()
						+ ")");
				bFirst = false;
			} else {
				sbf.append("/" + fp.getPartenaire().getNom() + "("
						+ fp.getPrix() + ")");
			}
		}
		return sbf.toString();
	}

	@Transient
	public List<FormationPartenaire> getAllFormationsPartenaire() {
		List<FormationPartenaire> ret = new ArrayList<FormationPartenaire>();
		FormationPartenaire plb = new FormationPartenaire();
		plb.setFormation(this);
		Partenaire plbPartenaire = new Partenaire();
		plbPartenaire.setNom("PLB");
		plb.setPartenaire(plbPartenaire);
		plb.setCode(getReference());

		plb.setUrl(getUrlPlb());

		ret.add(plb);
		ret.addAll(formationsPartenaire);

		return ret;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public String getMotClePrimaire() {
		return motClePrimaire;
	}

	public void setMotClePrimaire(String motClePrimaire) {
		this.motClePrimaire = motClePrimaire;
	}

	public String getBaliseTitle() {
		return baliseTitle;
	}

	public void setBaliseTitle(String baliseTitle) {
		this.baliseTitle = baliseTitle;
	}

	public String getCampagneAdWords() {
		return campagneAdWords;
	}

	public void setCampagneAdWords(String campagneAdWords) {
		this.campagneAdWords = campagneAdWords;
	}

	public Boolean getSupport() {
		return support;
	}

	public void setSupport(Boolean support) {
		this.support = support;
	}

	public String getCoursPlbCataloguePartenaire() {
		return coursPlbCataloguePartenaire;
	}

	public void setCoursPlbCataloguePartenaire(
			String coursPlbCataloguePartenaire) {
		this.coursPlbCataloguePartenaire = coursPlbCataloguePartenaire;
	}

	public Boolean getPlbInter() {
		return plbInter;
	}

	public void setPlbInter(Boolean plbInter) {
		this.plbInter = plbInter;
	}

	public FormationMutualisees getFormationMutualisees() {
		return formationMutualisees;
	}


	public void setFormationMutualisees(
			FormationMutualisees formationMutualisees) {
		this.formationMutualisees = formationMutualisees;
	}

	public List<Formation> getSameFormations() {
		List<Formation> ret = new ArrayList<Formation>();
		for (Formation f : getFormations()) {
			if (!f.equals(this)) {
				ret.add(f);
			}
		}

		return ret;
	}

	public List<Formation> getFormations() {
		if (formationMutualisees != null) {
			return formationMutualisees.getFormations();
		}
		return new ArrayList<Formation>();
	}

	public List<Formation> getFormationAssociees() {
		return formationAssociees;
	}

	public void setFormationAssociees(List<Formation> formationAssociees) {
		this.formationAssociees = formationAssociees;
	}

	public Date getArchivedDate() {
		return archivedDate;
	}

	public void setArchivedDate(Date archivedDate) {
		this.archivedDate = archivedDate;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getBaliseDescription() {
		return baliseDescription;
	}

	public void setBaliseDescription(String baliseDescription) {
		this.baliseDescription = baliseDescription;
	}

	public String getBaliseKeyWords() {
		return baliseKeyWords;
	}

	public void setBaliseKeyWords(String baliseKeyWords) {
		this.baliseKeyWords = baliseKeyWords;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		if (contenu != null) {
			this.contenu = HTMLUtils.decode(contenu);
		} else {
			this.contenu = null;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArgumentaire() {
		return argumentaire;
	}

	public void setArgumentaire(String argumentaire) {
		this.argumentaire = argumentaire;
	}

	public String getDescriptif() {
		return descriptif;
	}
	
	public String getDescriptifAsString() {
		return descriptif != null ? HTMLUtils.getData(descriptif) : "";
	}

	public void setDescriptif(String descriptif) {
		this.descriptif = descriptif;
	}

	public String getObjectifs_pedagogiques() {
		return objectifs_pedagogiques;
	}

	public String getObjectifs_pedagogiquesAsString() {
		return objectifs_pedagogiques != null ? HTMLUtils.getData(objectifs_pedagogiques) : "";
	}

	public void setObjectifs_pedagogiques(String objectifs) {
		this.objectifs_pedagogiques = objectifs;
	}

	public String getObjectifs_operationnels() {
		return objectifs_operationnels;
	}

	public void setObjectifs_operationnels(String objectifs_operationnels) {
		this.objectifs_operationnels = objectifs_operationnels;
	}

	public String getGescofObjectifs() {
		return objectifs_operationnels != null ?HTMLUtils.getData(objectifs_operationnels) : (objectifs_pedagogiques != null ? HTMLUtils.getData(objectifs_pedagogiques) : "");
	}
	public String getGescofEvaluation() {
		return objectifs_pedagogiques != null ?HTMLUtils.getData(objectifs_pedagogiques) : (objectifs_operationnels != null ? HTMLUtils.getData(objectifs_operationnels) : (description != null ? HTMLUtils.getData(description) : "") );
	}

	public String getTravauxPratiques() {
		return travauxPratiques;
	}

	public void setTravauxPratiques(String travauxPratiques) {
		this.travauxPratiques = travauxPratiques;
	}

	public String getPrerequis() {
		return prerequis;
	}
	
	public String getPrerequisAsString() {
		return prerequis !=null ? HTMLUtils.getData(prerequis) : "";
	}

	public void setPrerequis(String prerequis) {
		if (prerequis != null) {
			this.prerequis = HTMLUtils.decode(prerequis);
		} else {
			this.prerequis = null;
		}
	}

	public String getPrerequis4Front() {
		if (prerequis.trim().startsWith("<p>")) {
			return prerequis.trim().substring(4, prerequis.trim().length() - 4);
		}
		return prerequis.trim();
	}

	public String getInfos() {
		return infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

	public String getLibelleInformation() {
		return _toLibelleInformation(replace);
	}

	public void setLibelleInformation(String libelleInformation) {
		replace = _toReplace(libelleInformation);
	}

	private String _toLibelleInformation(String replace) {
		if (replace != null && replace.indexOf("for_infos") != -1) {
			int index = replace.indexOf("for_infos");
			String s = replace.substring(index + "for_infos".length() + 1);
			index = s.indexOf("\"") + 1;
			int stop = s.lastIndexOf("\"");
			return HTMLUtils.decode(s.substring(index, stop));
		}
		return null;
	}

	private String _toReplace(String libelleInformation) {
		if (libelleInformation == null
				|| libelleInformation.trim().length() == 0) {
			return null;
		}
		StringBuffer sbf = new StringBuffer("a:1:{s:9:\"for_infos\";s:");
		sbf.append(HTMLUtils.encode(libelleInformation).length());
		sbf.append(":\"");
		sbf.append(HTMLUtils.encode(libelleInformation));
		sbf.append("\";}");
		return sbf.toString();
	}

	public String getReplace() {
		return replace;
	}

	public void setReplace(String for_replace) {
		this.replace = for_replace;
	}

	public String getParticipants() {
		return participants;
	}

	public void setParticipants(String participants) {
		this.participants = participants;
	}

	public Integer getTop10() {
		return top10;
	}

	public void setTop10(Integer top10) {
		this.top10 = top10;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Boolean getEligibleCpf() {
		return eligibleCpf;
	}

	public void setEligibleCpf(Boolean eligibleCpf) {
		this.eligibleCpf = eligibleCpf;
	}

	public String getCodeCpf() {
		return codeCpf;
	}

	public void setCodeCpf(String codeCpf) {
		this.codeCpf = codeCpf;
	}

	public Boolean getElearning() {
		return elearning;
	}

	public void setElearning(Boolean elearning) {
		this.elearning = elearning;
	}

	public String getNiveau() {
		return niveau;
	}

	public void setNiveau(String niveau) {
		this.niveau = niveau;
	}

	public String getOfficiel() {
		return officiel;
	}

	public void setOfficiel(String officiel) {
		this.officiel = officiel;
	}

	public Integer getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(Integer dataVersion) {
		this.dataVersion = dataVersion;
	}

	public String getRemise() {
		return remise;
	}

	public void setRemise(String remise) {
		this.remise = remise;
	}

	public String getMoyensPedagogiques() {
		return moyensPedagogiques;
	}
	
	public String getMoyensPedagogiquesAsString() {
		return moyensPedagogiques != null ? HTMLUtils.getData(moyensPedagogiques) : "";
	}

	public void setMoyensPedagogiques(String moyensPedagogiques) {
		this.moyensPedagogiques = moyensPedagogiques;
	}

	public String getModalitesSuivi() {
		return modalitesSuivi;
	}
	public String getModalitesSuiviAsString() {
		return modalitesSuivi != null ? HTMLUtils.getData(modalitesSuivi) : "";
	}

	public void setModalitesSuivi(String modalitesSuivi) {
		this.modalitesSuivi = modalitesSuivi;
	}

	public String getAutreObjectifSimple() {
		return autreObjectifSimple;
	}

	public void setAutreObjectifSimple(String autreObjectifSimple) {
		this.autreObjectifSimple = autreObjectifSimple;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idFormation;
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
		Formation other = (Formation) obj;
		if (idFormation != other.idFormation)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return reference + "/" + libelle;
	}

	public int compareInFiliereTo(Formation o) {
		// TODO Auto-generated method stub
		return getRangFilierePrincipale() - o.getRangFilierePrincipale();
	}

	@Override
	public int compareTo(Formation o) {
		// TODO Auto-generated method stub
		return getLibelle().compareTo(o.getLibelle());
	}

}
