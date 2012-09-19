package com.oneandone.rcosnita.jira.exporters;

import java.io.File;

import com.oneandone.rcosnita.jira.util.FileHelpers;

/**
 * Class used to provide common implementation for methods used across different exporters.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 */

public abstract class JiraExporterAbstract implements JiraExporter {
	private JiraFacade jiraFacade;
	private Integer startAt = 0;
	private Integer maxResults = 50;
	private String outputFolder;
	private String format;	
	
	/**
	 * Constructor used to initialize only the mandatory information required by all exporters.
	 * 
	 * @param jiraFacade
	 * @param startAt
	 * @param maxResults
	 * @param outputFolder
	 * @param format
	 */
	protected JiraExporterAbstract(JiraFacade jiraFacade, Integer startAt, Integer maxResults,
			String outputFolder, String format) {
		this.jiraFacade = jiraFacade;
		this.outputFolder = outputFolder;
		this.format = format;
		
		if(startAt != null) {
			this.startAt = startAt;
		}
		
		if(maxResults != null) {
			this.maxResults = maxResults;
		}		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareFolders() throws Exception {
		System.out.println(String.format("Preparing %s export folder: %s.", this.format, this.outputFolder));
		
		File destDir = new File(outputFolder);
		
		if(destDir.exists()) {
			System.out.println("Removing older export.");
			
			FileHelpers.deleteFile(destDir);
		}
		
		destDir.mkdirs();
		
		System.out.println(String.format("Folders for %s export prepared.", this.format));
	}
	
	public JiraFacade getJiraFacade() {
		return jiraFacade;
	}

	public Integer getStartAt() {
		return startAt;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public String getFormat() {
		return format;
	}
}
