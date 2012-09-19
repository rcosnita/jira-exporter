package com.oneandone.rcosnita.jira.threads;

import java.util.concurrent.Callable;

import com.oneandone.rcosnita.jira.exporters.JiraFacade;
import com.oneandone.rcosnita.jira.exporters.models.JiraAttachment;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;

/**
 * Class used to implement the threaded download behavior for jira issue.
 * 
 * @author Radu Viorel Cosnita
 * @since 24.02.2012
 * @version 1.0
 */
public class IssueDownloaderLight implements Callable<JiraIssue> {
	private JiraFacade jiraFacade;
	private JiraIssue jiraIssue;
	private String outputFolder;
	
	public IssueDownloaderLight(JiraFacade jiraFacade, JiraIssue issue, String outputFolder) {
		this.jiraFacade = jiraFacade;
		this.jiraIssue = issue;
		this.outputFolder = outputFolder;
	}
	
	/**
	 * Download the light version of a jira issue.
	 */
	@Override
	public JiraIssue call() throws Exception {
		this.jiraFacade.downloadIssueLight(jiraIssue);
		
		for(JiraAttachment attachment : jiraIssue.getAttachments()) {
			attachment.setFilePath(String.format("%s/%s", this.outputFolder, attachment.getName()));
		}
		
		return jiraIssue;
	}
}