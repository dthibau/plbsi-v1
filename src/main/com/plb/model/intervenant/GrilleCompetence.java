package com.plb.model.intervenant;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jboss.seam.util.Hex;

import com.plb.model.Formation;

@Entity 
@Table(name="grille_competence")
public class GrilleCompetence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8501460589282121660L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private Date filledDate,notifiedDate;
	
	private String url;
	
	@OneToOne
	Intervenant intervenant;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="grilleCompetence", orphanRemoval=true)
	List<Competence> competences = new ArrayList<Competence>();

	public GrilleCompetence() {
		super();
	}
	
	public GrilleCompetence(Intervenant intervenant) {
		setIntervenant(intervenant);
		setUrl(_generateUrl(intervenant));
		intervenant.setGrilleCompetence(this);
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getFilledDate() {
		return filledDate;
	}

	public void setFilledDate(Date filledDate) {
		this.filledDate = filledDate;
	}

	public Date getNotifiedDate() {
		return notifiedDate;
	}

	public void setNotifiedDate(Date notifiedDate) {
		this.notifiedDate = notifiedDate;
	}

	public Intervenant getIntervenant() {
		return intervenant;
	}

	public void setIntervenant(Intervenant intervenant) {
		this.intervenant = intervenant;
	}

	public List<Competence> getCompetences() {
		return competences;
	}

	public void setCompetences(List<Competence> competences) {
		this.competences = competences;
	}

	public void addCompetence(Formation formation) {
		Competence competence = new Competence(formation);
		competence.setGrilleCompetence(this);
		competences.add(competence);
	}
	public void removeCompetence(Formation formation) {
		for ( Competence competence : competences ) {
			if ( competence.getFormation().equals(formation) ) {
				competences.remove(competence);
				break;
			}
		}
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Transient
	public Competence getCompetence(Formation formation) {
		for ( Competence competence : competences ) {
			if ( competence.getFormation().equals(formation) ) {
				return competence;
			}
		}
		return null;
	}

	private String _generateUrl(Intervenant intervenant) {
		StringBuffer sbf = new StringBuffer();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] shaRes = md.digest( (System.currentTimeMillis() + "").getBytes());
		sbf.append(Hex.encodeHex( shaRes));
		return sbf.toString();
	}
}
