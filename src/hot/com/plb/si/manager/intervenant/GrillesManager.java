package com.plb.si.manager.intervenant;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Hex;
import org.richfaces.component.SortOrder;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.plb.model.Fichier;
import com.plb.model.directory.Account;
import com.plb.model.event.NotificationIntervenantsEvent;
import com.plb.model.intervenant.GrilleCompetence;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.dto.GrilleDto;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.service.IntervenantDao;
import com.plb.si.service.NotificationService;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import org.apache.jackrabbit.spi.commons.query.sql.QueryBuilder;
import com.plb.si.util.Labels;

@Name("grillesManager")
@Scope(ScopeType.CONVERSATION)
public class GrillesManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6183914950500140773L;

	public static int pageSize = 20;
	@Out
	public static int OUTDATED=0,UPDATED=1,ALL=2;

	List<GrilleDto> results,filteredResults;

	private Map<String,SortOrder> orders = new HashMap<String, SortOrder>();

	@In
	EntityManager entityManager;

	@In
	Account loggedUser;
	
	IntervenantDao intervenantDao;

	NotificationIntervenantsEvent notificationEvent;
	
	@Out
	public static int GRILLE=0;
	@Out
	public static int LIBRE=1;
	private int typeNotification=GRILLE;
	

	private boolean needPerformQuery = true;
	private boolean needFilter = true;

	@In
	FacesMessages facesMessages;
	
	@In(create=true)
	NotificationService notificationService;

	// Critères de recherche
	private int rangInf=0,rangSup=100;
	private int updateFilter = ALL;
	
	private Date lastQueryDate = new Date();
	@In
	ApplicationManager applicationManager;
	@Logger
	Log log;
	
	@Create
	@Begin(join = true)
	public void init() {
		intervenantDao = new IntervenantDao(entityManager);
		orders.put("nomComplet", SortOrder.ascending);
		orders.put("email", SortOrder.unsorted);
		orders.put("rang", SortOrder.unsorted);
		orders.put("tarif", SortOrder.unsorted);
		orders.put("dateNotify", SortOrder.unsorted);
		orders.put("dateGrille", SortOrder.unsorted);
		
		
	}

	@Begin(join = true)
	public List<GrilleDto> getResults() {
		if (needPerformQuery || lastQueryDate.before(applicationManager.getGrillesUpdateDate())) {
			entityManager.clear();
			List<Intervenant> intervenants = _performQuery();
			results = _buildDto(intervenants);
			needPerformQuery = false;
			lastQueryDate = new Date();
		}
		if ( needFilter ) {
			filteredResults = _filter();
			needFilter = false;
		}
		return filteredResults;
	}

	public void search() {
		needPerformQuery = true;
		needFilter = true;
	}
	public void filter() {
		needFilter = true;
	}

	public void toggle(GrilleDto gDto) {
		gDto.setSelected(!gDto.isSelected());
	}
	
	public void selectAll() {
		for ( GrilleDto gDto : results ) {
			gDto.setSelected(true);
		}
	}
	public void unSelectAll() {
		for ( GrilleDto gDto : results ) {
			gDto.setSelected(false);
		}
	}

	public void setDefaultNotification() {
		notificationEvent.setSubject(Labels.getString("mail.grille.default.subject"));
		notificationEvent.setMessage(Labels.getString("mail.grille.default.message"));
	}
	public void createNotification() {
		notificationEvent = new NotificationIntervenantsEvent();
		notificationEvent.setIntervenants(_getSelectedIntervenants());
		setTypeNotification(GRILLE);
		
	}
	public void sendNotification() {
		notificationEvent.setAccount(loggedUser);
		notificationEvent.setDate(new Date());
		notificationService.sendMailIntervenant(1000, notificationEvent);
		for (Intervenant intervenant : notificationEvent.getIntervenants()) {
			if ( intervenant.getEmail() != null && intervenant.getEmail().length() > 0 ) {
				if ( notificationEvent.isIncludeUrl() ) {
					intervenant = entityManager.find(Intervenant.class,intervenant.getId());
					
					intervenant.getGrilleCompetence().setNotifiedDate(new Date());
				}
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.persist(notificationEvent);
		facesMessages.addFromResourceBundle(Severity.INFO,
				"notification.started");
		unSelectAll();
		needPerformQuery = true;
	}

	public SortOrder getOrder(String key) {
		return orders.get(key);
	}
	public SortOrder setOrder(String key, SortOrder order) {
		return orders.put(key,order);
	}
	
	public boolean isAscending(String key) {
		return orders.get(key) != null && orders.get(key).equals(SortOrder.ascending);
	}

	public boolean isDescending(String key) {
		return orders.get(key) != null && orders.get(key).equals(SortOrder.descending);
	}

	public void sortBy(String key) {
		for ( String k : orders.keySet() ) {
			if ( !k.equals(key) ) {
				orders.put(k, SortOrder.unsorted);
			} else {
				if ( orders.get(k).equals(SortOrder.unsorted) && ( key.equals("dateNotify") || key.equals("dateGrille") ) ) {
					orders.put(k, SortOrder.descending);
				} else if (orders.get(k).equals(SortOrder.ascending)) {
					orders.put(k, SortOrder.descending);
				} else {
					orders.put(k, SortOrder.ascending);
				}
			}
		}
	}

	
	private List<Intervenant> _performQuery() {
		return intervenantDao.findAll();
	}
	private List<GrilleDto> _buildDto(List<Intervenant> intervenants) {
		List<GrilleDto> ret= new ArrayList<GrilleDto>();
		for ( Intervenant intervenant : intervenants ) {
				ret.add(new GrilleDto(intervenant));
		}
		return ret;
	}

	public int getRangInf() {
		return rangInf;
	}

	public void setRangInf(int rangInf) {
		this.rangInf = rangInf;
	}

	public int getRangSup() {
		return rangSup;
	}

	public void setRangSup(int rangSup) {
		this.rangSup = rangSup;
	}

	public int getUpdateFilter() {
		return updateFilter;
	}

	public void setUpdateFilter(int updateFilter) {
		this.updateFilter = updateFilter;
	}

	public NotificationIntervenantsEvent getNotificationEvent() {
		return notificationEvent;
	}

	public void setNotificationEvent(NotificationIntervenantsEvent notificationEvent) {
		this.notificationEvent = notificationEvent;
	}


	private List<GrilleDto> _filter() {
		List<GrilleDto> ret = new ArrayList<GrilleDto>();
		for ( GrilleDto gDto : results ) {
			if ( gDto.getIntervenant().getRang() < rangInf || gDto.getIntervenant().getRang() > rangSup ) {
				continue;
			}
			if ( updateFilter == ALL ) {
				ret.add(gDto);
			} else if ( updateFilter == OUTDATED && gDto.isOutdated() ) {
				ret.add(gDto);
			} else if ( updateFilter == UPDATED && !gDto.isOutdated() ) {
				ret.add(gDto);
			}
		}
		return ret;
	}
	
	private List<Intervenant> _getSelectedIntervenants() {
		List<Intervenant> selected = new ArrayList<Intervenant>();
		for ( GrilleDto gDto : filteredResults ) {
			if ( gDto.isSelected() ) {
				Intervenant intervenant = entityManager.find(Intervenant.class, gDto.getIntervenant().getId());
				if ( intervenant.getGrilleCompetence() == null ) { // Backward compatibility, dans la base les intervenants n'ont pas tous une grille de compétences
					GrilleCompetence grilleCompetence = new GrilleCompetence(intervenant);
					grilleCompetence.setIntervenant(intervenant);
					intervenant.setGrilleCompetence(grilleCompetence);
					entityManager.flush();
				}
				selected.add(intervenant);
			}
		}
		return selected;
	}

	public int getTypeNotification() {
		return typeNotification;
	}

	public void setTypeNotification(int typeNotification) {
		this.typeNotification = typeNotification;
		if ( typeNotification == GRILLE ) {
			notificationEvent.setIncludeUrl(true);
			setDefaultNotification();
			
		} else {
			notificationEvent.setIncludeUrl(false);
			notificationEvent.setSubject("");
			notificationEvent.setMessage("");
		}
		
	}
	public void uploadAttachment(FileUploadEvent event) throws IOException {
		UploadedFile item = event.getUploadedFile();
		Fichier fichier = new Fichier();
		fichier.setName(item.getName());
		fichier.setContentType(item.getContentType());
		fichier.setData(item.getData());
		fichier.setLength((int)item.getSize());
		notificationEvent.addAttachment(fichier);
    }
	
	public void removeAttachment(Fichier attachment) {
		notificationEvent.removeAttachment(attachment);
	}
}
