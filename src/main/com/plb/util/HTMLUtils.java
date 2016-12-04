package com.plb.util;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLUtils {
	   
	private static final HtmlUnicode [] HTML_UNICODE = {
	                                                    new HtmlUnicode( "&amp;", '&' ),
	                                                    new HtmlUnicode( "&lt;",  '<' ),
	                                                    new HtmlUnicode( "&gt;", '>' ),
	                                                    new HtmlUnicode( "&quot;", '"' ),   
	                                                    new HtmlUnicode( "&raquo;", '\u00bb' ),
	                                                    new HtmlUnicode( "&laquo;", '\u00ab' ),
	                                                    new HtmlUnicode( "&deg;", '\u00B0' ),
	                                                    new HtmlUnicode( "&euro;", '\u20AC' ),
	                                                    new HtmlUnicode( "&rsquo;", '\u2019' ),
	                                                    new HtmlUnicode( "&lsquo;", '\u2018' ),
	                                                    new HtmlUnicode( "&sbquo;", '\u201A' ),
	                                                    new HtmlUnicode( "&ldquo;", '\u201C' ),
	                                                    new HtmlUnicode( "&rdquo;", '\u201D' ),
	                                                    new HtmlUnicode( "&bdquo;", '\u201E' ),
	                                                                                                        
	                                                    
	                                                    //MAJUSCULES
	                                                    new HtmlUnicode( "&Agrave;", '\u00c0' ),
	                                                    new HtmlUnicode( "&Aacute;", '\u00c1' ),
	                                                    new HtmlUnicode( "&Acirc;", '\u00c2' ),
	                                                    new HtmlUnicode( "&Atilde;", '\u00c3' ),
	                                                    new HtmlUnicode( "&Auml;", '\u00c4' ),
	                                                    new HtmlUnicode( "&Aring;", '\u00c5' ),
	                                                    new HtmlUnicode( "&AElig;", '\u00c6' ),
	                                                    new HtmlUnicode( "&Ccedil;", '\u00c7' ),
	                                                    new HtmlUnicode( "&Egrave;", '\u00c8' ),
	                                                    new HtmlUnicode( "&Eacute;", '\u00c9' ),
	                                                    new HtmlUnicode( "&Ecirc;", '\u00ca' ),
	                                                    new HtmlUnicode( "&Euml;", '\u00cb' ),
	                                                    new HtmlUnicode( "&Igrave;", '\u00cc' ),
	                                                    new HtmlUnicode( "&Iacute;", '\u00cd' ),
	                                                    new HtmlUnicode( "&Icirc;", '\u00ce' ),
	                                                    new HtmlUnicode( "&Iuml;", '\u00cf' ),
	                                                    new HtmlUnicode( "&ETH;", '\u00d0' ),
	                                                    new HtmlUnicode( "&Ntilde;", '\u00d1' ),
	                                                    new HtmlUnicode( "&Ograve;", '\u00d2' ),
	                                                    new HtmlUnicode( "&Oacute;", '\u00d3' ),
	                                                    new HtmlUnicode( "&Ocirc;", '\u00d4' ),
	                                                    new HtmlUnicode( "&Otilde;", '\u00d5' ),
	                                                    new HtmlUnicode( "&Ouml;", '\u00d6' ),
	                                                    new HtmlUnicode( "&Oslash;", '\u00d8' ),
	                                                    new HtmlUnicode( "&Ugrave;", '\u00d9' ),
	                                                    new HtmlUnicode( "&Uacute;", '\u00da' ),
	                                                    new HtmlUnicode( "&Ucirc;", '\u00db' ),
	                                                    new HtmlUnicode( "&Uuml;", '\u00dc' ),
	                                                    new HtmlUnicode( "&Yacute;", '\u00dd' ),
	                                                    new HtmlUnicode( "&THORN;", '\u00de' ),
	                                                    new HtmlUnicode( "&szlig;", '\u00df' ),
	                                                    //MNUSCULES
	                                                    new HtmlUnicode( "&agrave;", '\u00e0' ),
	                                                    new HtmlUnicode( "&aacute;", '\u00e1' ),
	                                                    new HtmlUnicode( "&acirc;", '\u00e2' ),
	                                                    new HtmlUnicode( "&atilde;", '\u00e3' ),
	                                                    new HtmlUnicode( "&auml;", '\u00e4' ),
	                                                    new HtmlUnicode( "&aring;", '\u00e5' ),
	                                                    new HtmlUnicode( "&aelig;", '\u00e6' ),
	                                                    new HtmlUnicode( "&ccedil;", '\u00e7' ),
	                                                    new HtmlUnicode( "&egrave;", '\u00e8' ),
	                                                    new HtmlUnicode( "&eacute;", '\u00e9' ),
	                                                    new HtmlUnicode( "&ecirc;", '\u00ea' ),
	                                                    new HtmlUnicode( "&euml;", '\u00eb' ),
	                                                    new HtmlUnicode( "&igrave;", '\u00ec' ),
	                                                    new HtmlUnicode( "&iacute;", '\u00ed' ),
	                                                    new HtmlUnicode( "&icirc;", '\u00ee' ),
	                                                    new HtmlUnicode( "&iuml;", '\u00ef' ),
	                                                    new HtmlUnicode( "&eth;", '\u00f0' ),
	                                                    new HtmlUnicode( "&ntilde;", '\u00f1' ),
	                                                    new HtmlUnicode( "&ograve;", '\u00f2' ),
	                                                    new HtmlUnicode( "&oacute;", '\u00f3' ),
	                                                    new HtmlUnicode( "&oelig;", '\u0153' ),                                           
	                                                    new HtmlUnicode( "&ocirc;", '\u00f4' ),
	                                                    new HtmlUnicode( "&otilde;", '\u00f5' ),
	                                                    new HtmlUnicode( "&ouml;", '\u00f6' ),
	                                                    new HtmlUnicode( "&oslash;", '\u00f8' ),
	                                                    new HtmlUnicode( "&ugrave;", '\u00f9' ),
	                                                    new HtmlUnicode( "&uacute;", '\u00fa' ),
	                                                    new HtmlUnicode( "&ucirc;", '\u00fb' ),
	                                                    new HtmlUnicode( "&uuml;", '\u00fc' ),
	                                                    new HtmlUnicode( "&yacute;", '\u00fd' ),
	                                                    new HtmlUnicode( "&thorn;", '\u00fe' ),
	                                                    new HtmlUnicode( "&yuml;", '\u00ff' )
	                                                };
	   
	    public static String decode(String text) {

	        for(HtmlUnicode hu : HTML_UNICODE)
	        	text = text.replaceAll(hu.getHtml(), ""+hu.getUnicode());
	        
	        return text;
	    }

	    public static String encode(String text) {
	        String tok = "";
	        for(HtmlUnicode hu : HTML_UNICODE)
	            tok += hu.getUnicode();
	        StringTokenizer tokenizer = new StringTokenizer(text, tok, true);
	        int count = tokenizer.countTokens();

	        // pas besoin d'encoder
	        if (count == 1)
	            return text;
	        // on met un buffer plus large vu qu'un caractere est remplace par
	        // plusieurs
	        StringBuilder buff = new StringBuilder(text.length() + count * 8);
	        while (tokenizer.hasMoreTokens()) {
	            String token = tokenizer.nextToken();
	            if (token.length() == 1) {
	            	boolean bTrouve = false;
	                for(HtmlUnicode hu : HTML_UNICODE) {
	                    if(hu.getUnicode() == token.charAt(0)) {
	                        buff.append(hu.getHtml());
	                        bTrouve =true;
	                    }
	                }
	                if ( !bTrouve ) { // Caratère simple n'étant pas un caractère à encodé
	                	buff.append(token);
	                }
	            } else {
	                buff.append(token);
	            }
	        }
	        return buff.toString();
	    }
	    //cette classe permet un acces plus simplifie pour les tableaux
	    private static class HtmlUnicode{
	        private char unicode;
	        private String html;
	       
	        public HtmlUnicode(String html, char unicode){
	            this.html = html;
	            this.unicode = unicode;
	        }

	        /**
	         * @return the html
	         */
	        public String getHtml() {
	            return html;
	        }

	        /**
	         * @param html the html to set
	         */
	        public void setHtml(String html) {
	            this.html = html;
	        }

	        /**
	         * @return the unicode
	         */
	        public char getUnicode() {
	            return unicode;
	        }

	        /**
	         * @param unicode the unicode to set
	         */
	        public void setUnicode(char unicode) {
	            this.unicode = unicode;
	        }
	       
	    }

	    public static boolean isHTMLContent(String text) {
	    	return text == null || text.startsWith("<");
	    }
	    public static String getHTMLContent(String textContent) {
	    	return "<p>" + textContent.replaceAll("\n", "<br/>");
	    }
	    
	    public static String getSimpleHTML(String textContent) {
	    	Document doc = Jsoup.parse(textContent);
	    	Iterator<Element> h3Iterator = doc.select("h3").iterator();
	    	while ( h3Iterator.hasNext() ) {
	    		Element h3 = h3Iterator.next();
	    		String text = h3.text();
	    		h3.html("<br/><br/><font size=\"5\"><b>"+text+"<b/></font><br/>");
	    	}
	    	Iterator<Element> ssTitres = doc.select("span.sous_titre").iterator();
	    	while ( ssTitres.hasNext() ) {
	    		Element ssTitre = ssTitres.next();
	    		String text = ssTitre.text();
	    		ssTitre.html("<br/><font size=\"3\"><b>"+text+"<b/></font><br/>");
	    	}
	    	Iterator<Element> titreTps = doc.select("span.titre_travaux_pratiques").iterator();
	    	while ( titreTps.hasNext() ) {
	    		Element elt = titreTps.next();
	    		String text = elt.text();
	    		elt.html("<font size=\"3\"><i><u>"+text+"<u/></i></font>");
	    	}
	    	Iterator<Element> tps = doc.select("span.corps_travaux_pratiques").iterator();
	    	while ( tps.hasNext() ) {
	    		Element elt = tps.next();
	    		String text = elt.text();
	    		elt.html("<i>"+text+"</i>");
	    	}
	    	return doc.html();
	    }
	    
	    public static String getData(String htmlContent) {
	    	Document doc = Jsoup.parse(htmlContent);
	    	return doc.text();
	    }
	    
	}
