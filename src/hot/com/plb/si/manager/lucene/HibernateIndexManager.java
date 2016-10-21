package com.plb.si.manager.lucene;

import java.io.Serializable;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.log.Log;

import com.plb.model.Formation;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.manager.ApplicationManager;


@Name("hibernateIndexManager")
@AutoCreate
public class HibernateIndexManager implements Serializable {

	@Logger
	Log log;
	
	@In
	ApplicationManager applicationManager;
	
	@SuppressWarnings("rawtypes")
	public static final Class[] INDEXED_CLASSES = new Class[] {
			Formation.class, Intervenant.class };
	/**
	 * 
	 */
	private static final long serialVersionUID = 2192510359002194855L;

	// Same as hibernate.jdbc.batch_size
	private final int BATCH_SIZE = 20;

	@In
	private FullTextEntityManager entityManager;

	@Asynchronous
	public void indexAll() {
		applicationManager.refresh();
		log.info("> indexing All starting ....");
		index(INDEXED_CLASSES);
		log.info("< indexing All done ....");
	}		

	@SuppressWarnings("unchecked")
	@Asynchronous
	public void index(Class[] entityTypes) {
		for (Class entityType : entityTypes) {
			indexEntity(entityType);
		}
	}

	private FullTextSession getFullTextSession() {
		FullTextSession fullTextSession = (FullTextSession) entityManager
				.getDelegate();
		fullTextSession.setCacheMode(CacheMode.IGNORE);
		fullTextSession.setFlushMode(FlushMode.AUTO);
		return fullTextSession;
	}

	@SuppressWarnings("unchecked")
	@Asynchronous
	public void indexEntity(Class entityType) {
		FullTextSession fullTextSession = getFullTextSession();

		// To use after HibernateSearch upgrade
		fullTextSession.purgeAll(entityType);

		List<Serializable> results = fullTextSession.createQuery(
				"select " + entityType.getSimpleName() + ".id as "
						+ entityType.getSimpleName().toLowerCase() + " from "
						+ entityType.getSimpleName() + " "
						+ entityType.getSimpleName().toLowerCase())
				.setFetchSize(BATCH_SIZE).list();

		int index = 0;
		for (Serializable id : results) {
			try {
				Object entity = fullTextSession.load(entityType, id);

				fullTextSession.index(entity); // index each element
				log.info("> indexing entity (" + entityType.getName()
						+ ") id: (" + id + ")");

				// if (index != 0 && index%BATCH_SIZE == 0) {
				// log.info("...clearing " + entityType.getName());
				fullTextSession.flush();

				fullTextSession.evict(entity); // clear every batchSize
				// }
			} catch (Exception e) {
				log
						.error("Exception ocurred during indexing "
								+ results.get(0));
				e.printStackTrace();
			}
			index++;
		}

	}

}
