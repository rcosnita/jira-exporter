package com.oneandone.rcosnita.jira.exporters;

import java.util.List;

import com.oneandone.rcosnita.jira.exporters.models.JiraAttachment;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;

/**
 * 
 * 
 * @author Radu Viorel Cosntia
 * @version 1.0
 * @since 21.02.2012
 */
public interface JiraFacade {
	/**
	 * Method used to execute a jql query. 
	 * 
	 * @param jiraQuery A valid jql query.
	 * @param startAt The index of the issue we want to start with.
	 * @param maxResults The maximum number of issues we want to retrieve.
	 * @return All matching jira issues (no Binary information is included).
	 * @throws Exception each internal exception is thrown for better control.
	 */
	public List<JiraIssue> executeQuery(String jiraQuery, Integer startAt, Integer maxResults) throws Exception;
	
	/**
	 * Method used to download light information about a specified jira issue. This
	 * will not download jira attachments content.
	 * 
	 * @param issue The issue we want to download.
	 * @return
	 * @throws Exception each internal exception is thrown for better control.
	 */
	public void downloadIssueLight(JiraIssue jiraIssue) throws Exception;
		
	/**
	 * Method used to download all attachments for a specified issue.
	 * @param issueId The issue unique identifier we want to download.
	 * @return A list of attachments
	 * @throws Exception each internal exception is thrown for better control.
	 */
	public void downloadIssueAttachments(JiraIssue issue) throws Exception;
	
	/**
	 * Method used to obtain a custom field identifier starting from the custom
	 * fiel name.
	 * 
	 * @param name The name of the custom field.
	 * @return The custom field identifier or empty string if no compatible match is found.
	 */
	public String getCustomFieldId(String name);
}
