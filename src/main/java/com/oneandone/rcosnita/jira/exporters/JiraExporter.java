package com.oneandone.rcosnita.jira.exporters;

/**
 * This is the API that must be provided by each jira exporter formatter.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 21.02.2012
 */

public interface JiraExporter {
	/**
	 * Method used to actually do the export from jira.
	 * 
	 * @throws Exception
	 */
	public void export() throws Exception;
	
	/**
	 * Nethod used to prepare the destination folders for the export.
	 * 
	 * @throws Exception
	 */
	public void prepareFolders() throws Exception;
}