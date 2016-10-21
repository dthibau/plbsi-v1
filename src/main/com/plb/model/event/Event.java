package com.plb.model.event;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.Prospect;
import com.plb.model.directory.Account;
import com.plb.model.intervenant.Intervenant;
import com.plb.util.Util;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "eventtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Event")
@Table(name="event")
public class Event {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Lob
	private String message;
	
	@ManyToOne
	private Account account;
	
	@ManyToMany
	List<Account> destinataires;
	
	@ManyToOne
	private Formation formation;
	
	@ManyToOne
	private Prospect prospect;
	
	@ManyToOne
	private InformationIntra intra;
	
	private String statutIntra;
	
	@ManyToOne
	private Intervenant intervenant;
	
	@Transient
	private String[] messageAsArray = new String[1];
	
	public Event() {
		super();
	}
	
	public Event(Account account, Formation formation, String message) {
		date = new Date();
		this.account = account;
		this.formation = formation;
		this.message = message;
	}
	
	public Event(Account account, Prospect prospect, String message) {
		date = new Date();
		this.account = account;
		this.prospect = prospect;
		this.message = message;
	}
	
	public Event(Account account, InformationIntra intra, String message, String statut) {
		date = new Date();
		this.account = account;
		this.intra = intra;
		this.message = message;
		this.statutIntra = statut;
	}
	
	public Event(Account account, Intervenant intervenant, String message) {
		date = new Date();
		this.account = account;
		this.intervenant = intervenant;
		this.message = message;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public List<Account> getDestinataires() {
		return destinataires;
	}
	public void setDestinataires(List<Account> destinataires) {
		this.destinataires = destinataires;
	}
	public String getDestinatairesAsString() {
		return Util.getCollectionAsString(destinataires);
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

	public Intervenant getIntervenant() {
		return intervenant;
	}

	public void setIntervenant(Intervenant intervenant) {
		this.intervenant = intervenant;
	}

	public String getMessage() {
		return message;
	}
	public String getMessageAsHTML() {
		return message.replace("\n", "<br/>");
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Transient 
	public boolean hasDestinataires() {
		return destinataires != null && destinataires.size() > 0;
	}
	@Transient 
	public String[] getMessageAsArray() {
		messageAsArray[0] = message;
		return messageAsArray;
	}
	@Transient 
	public String getHistoriqueKey() {
		return "historique." + this.getClass().getSimpleName();
	}
	@Transient 
	public String getNotificationKey() {
		return "notification." + this.getClass().getSimpleName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Event other = (Event) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getStatutIntra() {
		return statutIntra;
	}

	public void setStatutIntra(String statutIntra) {
		this.statutIntra = statutIntra;
	}
	
	public InformationIntra getIntra() {
		return intra;
	}

	public void setIntra(InformationIntra intra) {
		this.intra = intra;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", date=" + date + ", message=" + message
				+ ", account=" + account + ", destinataires=" + destinataires
				+ ", formation=" + formation + ", prospect=" + prospect
				+ ", intra=" + intra + ", statutIntra=" + statutIntra
				+ ", intervenant=" + intervenant + ", messageAsArray="
				+ Arrays.toString(messageAsArray) + "]";
	}

	
}
