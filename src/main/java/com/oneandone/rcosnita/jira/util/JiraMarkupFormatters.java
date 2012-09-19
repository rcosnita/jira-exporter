package com.oneandone.rcosnita.jira.util;

/**
 * Class used to provide helper methods for changing jira markup text to
 * different formats.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 */
public class JiraMarkupFormatters {
    /**
     * Method used to transform jira markup to html markup.
     * 
     * @return
     */
    public static String transformJiraMarkupToHtml(String result) {
    	String EOL = System.getProperty("line.separator");
    	
    	result = result.replace(EOL, "<br/>");
    	
    	result = result.replace("\\", "<br/>");
    	    	
    	result = result.replaceAll("\\*(.*?)\\*", "<b>$1</b>");
    	
    	result = result.replaceAll("\\* (.*)", "<ul><li>$1</li></ul>");
    	    	
    	return result;
    }
}
