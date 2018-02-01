package com.plb.si.manager.filiere;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.plb.model.Filiere;
import com.plb.model.directory.Account;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.util.Labels;

@Name("filiereManager")
@Scope(ScopeType.CONVERSATION)
public class FiliereManager implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@Out(required = false)
	public Filiere filiere;


	@RequestParameter
	String filiereId;
	
	@In
	EntityManager entityManager;

	@In
	Account loggedUser;

	@In
	ApplicationManager applicationManager;
	
	@In
	FacesMessages facesMessages;
	
	@Logger
	Log log;

	

	@Create
	public void init() {
		log.debug("Creating FiliereManager : loggedUser="+loggedUser);
	}

	@Begin(join = true)
	public void select(Filiere filiere) {
		log.debug("Select filiere : " + filiere);
		filiere = entityManager.find(Filiere.class,
				filiere.getId());

	}

	@Begin(join = true)
	public String select() {
		log.debug("Select filiere : " + filiereId);
		filiere = entityManager.find(Filiere.class,
				Integer.parseInt(filiereId));


		return "/mz/filiere/filiere.xhtml";
	}
	
	@Begin(join = true)
	public String createNew() {
		log.debug("createNew()");
		filiere = new Filiere();

		return "/mz/filiere/filiere.xhtml";
	}

	@RaiseEvent("filiereUpdated")
	public String save() {
		log.debug("save()");
		if (!entityManager.contains(filiere)) {
			entityManager.persist(filiere);
		}
		applicationManager.fetchFilieres();
		return "/mz/filiere/filieres.xhtml";
	}


	@End
	@RaiseEvent("filiereUpdated")
	public String deleteFiliere() {
		entityManager.remove(filiere);
		facesMessages.addFromResourceBundle(Severity.INFO, "");
		return "/mz/filiere/filieres.xhtml";
	}

	public void uploadPetit(FileUploadEvent event) throws IOException {
		UploadedFile item = event.getUploadedFile();
		filiere.setImagePetit(item.getName());
		_storeImage(item,"filiere/images/");
    }
	public void uploadMoyen(FileUploadEvent event) throws IOException {
		UploadedFile item = event.getUploadedFile();
		filiere.setImageMoyen(item.getName());
		_storeImage(item,"filiere/images/");
    }
	public void uploadLogo(FileUploadEvent event) throws IOException {
		UploadedFile item = event.getUploadedFile();
		filiere.setLogo(item.getName());
		_storeImage(item,"filiere/logo/");
    }
	private void _storeImage(UploadedFile item, String path) throws IOException {	
        // Transfer them to the Web server
		FTPClient ftp = new FTPClient();
//	    FTPClientConfig config = new FTPClientConfig();
//	    config.setXXX(YYY); // change required options
	    // for example config.setServerTimeZoneId("Pacific/Pitcairn")
//	    ftp.configure(config );
	    boolean error = false;
	    try {
	      int reply;
	      ftp.connect(ApplicationManager.FTP_URL);
	      ftp.login(ApplicationManager.FTP_LOGIN, ApplicationManager.FTP_PASSWORD);
	      log.debug("Connected to FTP server.");
	      log.debug(ftp.getReplyString());
	      // After connection attempt, you should check the reply code to verify
	      // success.
	      reply = ftp.getReplyCode();

	      if(!FTPReply.isPositiveCompletion(reply)) {
	        ftp.disconnect();
	        throw new IOException("FTP server refused connection.");
	      }
	      String fullPath = ApplicationManager.WEB_ROOT_IMG + "/" + path;
	      String subDirs[] = fullPath.split("/");
	      for ( String subDir : subDirs ) {
	    	  if ( !ftp.changeWorkingDirectory(subDir) ) {
	    		  ftp.makeDirectory(subDir);
	    		  ftp.changeWorkingDirectory(subDir);
	    	  }
	      }
	      ftp.setFileType(FTP.BINARY_FILE_TYPE);
	      ftp.storeFile(item.getName(), new ByteArrayInputStream(item.getData()));
//	      ftp.storeFile(item.getName(), new FileInputStream("/home/dthibau/Images/Anchor.png"));
	      log.debug("Storing "+ApplicationManager.WEB_ROOT_IMG+"/"+item.getName());
	      ftp.logout();
	    } catch(IOException e) {
	      throw e;
	    } finally {
	      if(ftp.isConnected()) {
	        try {
	          ftp.disconnect();
	        } catch(IOException ioe) {
	          // do nothing
	        }
	      }
	    }
        log.debug("uploadPetit UploadItem "+item);
        

	}
	
	public void uploadVisuel(FileUploadEvent event) throws Exception {
        UploadedFile item = event.getUploadedFile();
        // Store them in the Web content
        log.debug("UploadItem "+item);
        log.debug("Storing "+ApplicationManager.WEB_ROOT_IMG+"/uploads/Image/visuelFil"+filiere.getId()+".gif");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(ApplicationManager.WEB_ROOT_IMG+"/visuelFil"+filiere.getId()+".gif", false));
        os.write(item.getData());
        os.close();
        //
        String tagImg = "<img style=\"float: left;\" title=\""+filiere.getTitre() +"\" src=" + ApplicationManager.WEB_URL_IMG +"/visuelFil"+filiere.getId()+".gif alt=\"Visuel " + filiere.getTitre() +"\" width=\"250\" height=\"196\">";
        if ( filiere.getDescription() != null && filiere.getDescription().indexOf("<img") != -1 ) {
        	int start = filiere.getDescription().indexOf("<img");
        	int stop = filiere.getDescription().indexOf(">", start);
        	filiere.setDescription(tagImg+filiere.getDescription().substring(stop));
        } else {
        	filiere.setDescription(tagImg + (filiere.getDescription() != null ? filiere.getDescription() : ""));
        }
    }
	
	public void updateMeta() {
		if ( filiere.getMetaTitre() == null || filiere.getMetaTitre().length() == 0 ) {
			filiere.setMetaTitre(Labels.getString("filiere.metaTitre.default",filiere.getLibelle()));
		}
		if ( filiere.getMetaDescription() == null || filiere.getMetaDescription().length() == 0 ) {
			filiere.setMetaDescription(Labels.getString("filiere.metaDescription.default",filiere.getLibelle()));
		}
	}
}
