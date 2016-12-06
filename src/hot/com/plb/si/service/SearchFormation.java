package com.plb.si.service;

import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;

import com.plb.model.Formation;

public class SearchFormation {

	private FullTextEntityManager entityManager;

	private class SearchField {
		private String name;
		private float boost;

		SearchField(String name, float boost) {
			this.name = name;
			this.boost = boost;
		}
	}

	SearchField[] fields = { new SearchField("reference", 4.0f),
			new SearchField("libelle", 3.0f),
			new SearchField("baliseKeyWords", 2.0f),
			new SearchField("motClePrimaire", 1.0f),
			new SearchField("origine", 1.0f),
			new SearchField("precision", 1.0f),
			new SearchField("remarques", 1.0f) };

	SearchField[] optionnalFields = { new SearchField("contenu", 1.0f) };

	public SearchFormation(FullTextEntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<Formation> search(String searchString, boolean moreResults)
			throws ParseException {
		org.apache.lucene.search.Query luceneQuery = getLuceneQuery(
				searchString, moreResults);
		javax.persistence.Query query = entityManager.createFullTextQuery(
				luceneQuery, Formation.class);
		return query.getResultList();
		
	}

	private org.apache.lucene.search.Query getLuceneQuery(String searchWord,
			boolean moreResults) throws ParseException {
		Query luceneQuery = null;

		SearchFactory searchFactory = entityManager.getSearchFactory();
		QueryParser parser = new QueryParser(Version.LUCENE_35, "reference",
				searchFactory.getAnalyzer(Formation.class));
		searchWord = searchWord.replace(":", "").replace("^", "").replace("~", "");
		StringBuffer sbf = new StringBuffer();
		String[] tokens = searchWord.split(" ");
		boolean bFirst = true;
		for (String token : tokens) {
			if ( _invalidToken(token) )
				continue;
			if (!moreResults) { // In this case, we use AND
				if (bFirst) {
					bFirst = false;
				} else {
					sbf.append(" AND ");
				}
				sbf.append("(");
			}
			for (SearchField field : fields) {
				sbf.append(field.name).append(":").append(token).append("^")
						.append(field.boost).append(" ");
			}
			if (moreResults) {
				for (SearchField field : optionnalFields) {
					sbf.append(field.name).append(":").append(token)
							.append("^").append(field.boost).append(" ");
				}
			}
			if (!moreResults) {
				sbf.append(")");
			}
		}
		luceneQuery = parser.parse(sbf.toString());

//		System.out.println(luceneQuery);
		return luceneQuery;
	}
	
	private boolean _invalidToken(String token ) {
		if ( token == null || token.isEmpty() )
			return true;
		
		return false;
	}
}
