package com.plb.si.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.plb.dto.TarifInterDto;
import com.plb.model.TarifInter;
import com.plb.model.UpdateTarif;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.TarifUpdatedEvent;
import com.plb.si.service.NotificationService;
import com.plb.si.util.PlbUtil;

@Name("tarifsManager")
@Scope(ScopeType.CONVERSATION)
public class TarifsManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7955373831495004459L;
	@In
	EntityManager entityManager;
	@In
	Account loggedUser;
	@In
	FacesMessages facesMessages;
	
	List<TarifInter> tarifsInter;
	
	List<TarifInterDto> tarifsInterDto;
	
	@In(create=true,value="tarifsInter")
    Map<String, Float> oldTarifs;
	
	@In(create=true)
	NotificationService notificationService;
	
	@SuppressWarnings("unchecked")
	@Begin(join=true)
	public List<TarifInterDto> getTarifsInter() {
		if ( tarifsInterDto == null ) {
			tarifsInter = (List<TarifInter>)entityManager.createQuery("from TarifInter").getResultList();
			Map<String,TarifInterDto> map = new HashMap<String, TarifInterDto>();
			for ( TarifInter tarifInter : tarifsInter ) {			
				if ( map.get(tarifInter.getRootCode()) == null ) {
					map.put(tarifInter.getRootCode(), new TarifInterDto(tarifInter.getRootCode()));
				}
				map.get(tarifInter.getRootCode()).setPrix(tarifInter.getDuree()-1,tarifInter.getTarif());
			}
			tarifsInterDto = new ArrayList<TarifInterDto>(map.values());
			Collections.sort(tarifsInterDto);
		}
		return tarifsInterDto;
	}
	
	@RaiseEvent(value="tarifsUpdated")
	public void save() {
		_update();
		String modif = PlbUtil.diffTarifsInter(oldTarifs, tarifsInter);
		if (modif.length() > 0) {
			UpdateTarif update = new UpdateTarif();
			update.setDate(new Date());
			update.setUpdater(loggedUser);
			entityManager.persist(update);
			
			Event event =new TarifUpdatedEvent(loggedUser, null, modif);
			entityManager.persist(event);

			notificationService.resolveDestinataires(loggedUser, event);
			notificationService.sendMail(1000, null, event);
			
			facesMessages.addFromResourceBundle(Severity.INFO,
					"tarifs.updated");

		}

	}
	
	public void _update() {
		for ( TarifInterDto tarifInterDto : tarifsInterDto) {
			int i=1;
			for ( Float prix : tarifInterDto.getPrix() ) {
				String code = tarifInterDto.getCodeTarif() + i;
				int index = -1;int t=0;
				for ( TarifInter tarifInter : tarifsInter ) {
					if ( tarifInter.getCode().equals(code) ) {
						index=t;
						break;
					}
					t++;
				}
				if ( index != -1 ) {
					tarifsInter.get(index).setTarif(prix);
				}
				i++;
			}
		}
	}
}
