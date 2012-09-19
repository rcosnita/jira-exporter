package com.oneandone.rcosnita.jira.threads;

import java.util.concurrent.Callable;

import com.oneandone.rcosnita.jira.exporters.JiraFacade;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;

/**
 * Class used to downaloder
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 */
public class AttachmentsDownloader implements Callable<JiraIssue> {
	private JiraFacade jiraFacade;
	private JiraIssue jiraIssue;
	
	public AttachmentsDownloader(JiraFacade jiraFacade, JiraIssue issue) {
		this.jiraFacade = jiraFacade;
		this.jiraIssue = issue;
	}

	/**
	 * Download the light version of a jira issue.
	 */
	@Override
	public JiraIssue call() throws Exception {
		this.jiraFacade.downloadIssueAttachments(jiraIssue);
		
		return jiraIssue;
	}		
}