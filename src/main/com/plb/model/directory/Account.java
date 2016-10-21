package com.plb.model.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.plb.model.Contact;

@Entity
@Table(name="account")
public class Account implements Contact {

	@Id @GeneratedValue
	private long id;
	private String email;
	private String telephone;
	private String nom,prenom;
	private String login;
	private String password;
	@Temporal(TemporalType.DATE)
	private Date deletedDate;
	@Column(name="role")
	@Access(AccessType.PROPERTY)
	private String roleString;
	@Transient
	private List<Role> roles;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Transient
	public String getNomComplet() {
		return prenom + " " + nom;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRoleString() {
		return roleString;
	}
	public void setRoleString(String roleString) {
		this.roleString = roleString;
		String[] arrayRoles = roleString.split(";");
		this.roles = new ArrayList<Role>(arrayRoles.length);
		for ( String role : arrayRoles ) {
			roles.add(Role.valueOf(role));
		}		
	}
	
	public Role getPreferredRole() {
		return roles.get(0);
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	
	public Date getDeletedDate() {
		return deletedDate;
	}
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}
	@Transient
	public boolean isManager() {
		return roles.contains(Role.MANAGER);
	}
	@Transient
	public boolean isExtendedManager() {
		return roles.contains(Role.MANAGER) || roles.contains(Role.INTERVENANTS_MANAGER);
	}
	@Transient
	public boolean isDispatcher() {
		return roles.contains(Role.DISPATCHER);
	}
	@Transient
	public boolean isCommercial() {
		return roles.contains(Role.COMMERCIAL);
	}
	
	@Transient
	public boolean isOnlyCommercial() {
		return roles.size() == 1 && isCommercial();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Account other = (Account) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public String toString() {
		
		return getNomComplet();
	}
	
}
