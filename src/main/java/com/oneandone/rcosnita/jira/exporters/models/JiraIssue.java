package com.oneandone.rcosnita.jira.exporters.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to model a jira issue. It contains all possible attributes that 
 * might be useful for export process.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 21.02.2012
 */

public class JiraIssue implements Serializable, Comparable<JiraIssue> {
	private String id;
	private String link;
	private String hyperLink;
	private String summary;
	private String description;
	private String type; 
	private List<String> fixVersions = new ArrayList<String>();
	private List<String> components = new ArrayList<String>();
	private List<String> labels = new ArrayList<String>();
	private int storyPoints;	
	private List<JiraAttachment> attachments = new ArrayList<JiraAttachment>();
	private JiraIssue epic;
	private String status;
	private String reporter;
	private Priority priority;
	
	/**
	 * Enumeration used to define jira priorities.
	 * 
	 * @author Radu Viorel Cosnita
	 */
	public enum Priority {
		Undefined, Trivial, Minor, Major, Critical, Blocker
	};
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}	
	
	public String getHyperLink() {
		return hyperLink;
	}
	public void setHyperLink(String hyperLink) {
		this.hyperLink = hyperLink;
	}	
	
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getFixVersions() {
		return fixVersions;
	}
	
	public List<String> getComponents() {
		return components;
	}
	
	public List<String> getLabels() {
		return labels;
	}

	public int getStoryPoints() {
		return storyPoints;
	}
	public void setStoryPoints(int storyPoints) {
		this.storyPoints = storyPoints;
	}	
	
	public List<JiraAttachment> getAttachments() {
		return attachments;
	}
	
	public JiraIssue getEpic() {
		return epic;
	}
	public void setEpic(JiraIssue epic) {
		this.epic = epic;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	@Override
	public int compareTo(JiraIssue issue) {
		return issue.getPriority().compareTo(this.getPriority());
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	/**
	 * The equals logic for JiraIssue is different than for other objects: based on id
	 * it can be decided if a JiraIssue is equals with another issue.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof String) {
			return obj.toString().equalsIgnoreCase(this.getId());
		}
		
		if(obj instanceof JiraIssue) {
			return JiraIssue.class.cast(obj).getId().equalsIgnoreCase(this.getId());
		}
		
		return super.equals(obj);
	}
}