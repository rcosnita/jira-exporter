package com.oneandone.rcosnita.jira.exporters;

import java.io.FileOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.oneandone.rcosnita.jira.exporters.models.JiraAttachment;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue.Priority;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * Class used to provide a rest facade for interacting with jira issues.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 21.02.2012
 */
public class JiraFacadeRest implements JiraFacade {
	private String baseUri;
	private String linksBase;
	private String username;
	private String password;
	private String storyPointsField;
	
	/**
	 * Constructor used to initialize all attributes for jira REST requests. 
	 * 
	 * @param baseUri
	 * @param username
	 * @param password
	 */
	public JiraFacadeRest(String baseUri, String linksBase, String username, String password) {
		this.baseUri = baseUri;
		this.username = username;
		this.password = password;
		this.linksBase = linksBase;
		this.storyPointsField = getCustomFieldId("story points");
	}
	
	@Override
	public String getCustomFieldId(String name) {
		WebResource webResource = this.prepareRestCall();
		webResource = webResource.path("field");
		
		JSONArray fields = webResource.get(JSONArray.class);
		
		try {
			for(int i = 0; i < fields.length(); i++) {
				if(fields.getJSONObject(i).getString("name").equalsIgnoreCase(name)) {
					return fields.getJSONObject(i).getString("id");
				}
			}
		}
		catch(JSONException ex) {
			ex.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * Method used to prepare the rest call: it automatically adds authentication mechanisms
	 * within the call.
	 * 
	 * @return
	 */
	protected WebResource prepareRestCall() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.addFilter(new HTTPBasicAuthFilter(username, password));

        WebResource webResource = client.resource(this.baseUri);
        webResource.type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        return webResource;		
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JiraIssue> executeQuery(String jiraQuery, Integer startAt, Integer maxResults) throws Exception {
		WebResource webResource = this.prepareRestCall();
		webResource = webResource.path("search").queryParam("jql", jiraQuery);
		webResource = webResource.queryParam("startAt", String.valueOf(startAt))
				   				 .queryParam("maxResults", String.valueOf(maxResults));
		
		List<JiraIssue> issues = new ArrayList<JiraIssue>();
		
		JSONArray issuesJSON = webResource.get(JSONObject.class).getJSONArray("issues");
		
		for (int i = 0; i < issuesJSON.length(); i++) {
            JSONObject jsonHead = issuesJSON.getJSONObject(i);
            
            JiraIssue issue = new JiraIssue();
            issue.setId(jsonHead.getString("key"));
                     
            issue.setHyperLink(linksBase + issue.getId());
            issue.setLink(jsonHead.getString("self"));
            
            issues.add(issue);
		}
		
		return issues;
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void downloadIssueLight(JiraIssue issue) throws Exception {
		WebResource webResource = this.prepareRestCall();
		webResource = webResource.path("issue").path(issue.getId());
		
		JSONObject jsonDetail = webResource.get(JSONObject.class);
		JSONObject fields = jsonDetail.getJSONObject("fields");
		
		issue.setSummary(fields.get("summary").toString());
		
        String description = fields.getString("description");
        
        if(description != null && !description.toString().trim().isEmpty()) {
        	issue.setDescription(description.toString());
        }
		
		String type = fields.getJSONObject("issuetype").getString("name").toUpperCase();
		issue.setType(type);
		
		JSONArray versions = fields.getJSONArray("fixVersions");
		
		for(int i = 0; i < versions.length(); i++) {
			issue.getFixVersions().add(versions.getJSONObject(i).getString("name"));
		}
		
		JSONArray components = fields.getJSONArray("components");
		
		for(int i = 0; i < components.length(); i++) {
			issue.getComponents().add(components.getJSONObject(i).getString("name"));
		}
		
		JSONArray labels = fields.getJSONArray("labels");
		
		for(int i = 0; i < labels.length(); i++) {
			String label = labels.getString(i);
			issue.getComponents().add(label);
			
			if(label.startsWith("epic_")) {
				String epicId = label.substring(5, label.length());				
				epicId = epicId.toUpperCase().replace('_', '-');
				
				JiraIssue epic = new JiraIssue();
				epic.setId(epicId);
				epic.setType("EPIC");
				
				issue.setEpic(epic);
			}
		}
		
		Iterator<String> iterator = fields.keys();
		
		int storyPoints = -1;
		
		if(fields.has(this.storyPointsField) &&
				fields.get(this.storyPointsField) != JSONObject.NULL) {
			storyPoints = fields.getInt(this.storyPointsField);	
		}			
		
		issue.setStoryPoints(storyPoints);
		
		issue.setStatus(fields.getJSONObject("status").getString("name"));
		
		issue.setReporter(fields.getJSONObject("reporter").getString("displayName"));		
		
		issue.setPriority(Priority.valueOf(fields.getJSONObject("priority").getString("name")));
		
		JSONArray attachments = fields.getJSONArray("attachment");

		for(int i = 0; i < attachments.length(); i++ ) {
			JSONObject attachment = attachments.getJSONObject(i);
			
			JiraAttachment jiraAttach = new JiraAttachment();
			jiraAttach.setName(attachment.getString("filename"));
			jiraAttach.setType(attachment.getString("mimeType"));
			jiraAttach.setUrl(attachment.getString("content"));
			
			String self = attachment.getString("self");
			
			int lastIndexSlash = self.lastIndexOf("/");
			jiraAttach.setId(Integer.parseInt(self.substring(lastIndexSlash + 1)));
			
			issue.getAttachments().add(jiraAttach);
		}
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void downloadIssueAttachments(JiraIssue issue) throws Exception {        
        for(JiraAttachment attachment : issue.getAttachments()) {
        	byte[] content = this.downloadAttachment(attachment.getUrl());
        	
        	FileOutputStream output = null;
        	
        	try {
        		output = new FileOutputStream(attachment.getFilePath());
        		
        		output.write(content);
        	}
        	finally {
        		if(output != null) {
        			try {
        				output.close();
        			}
        			catch(Exception ex) {}
        		}
        	}
        }
	}
	
	/**
	 * Method used to download a specified attachment from jira.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private byte[] downloadAttachment(String url) throws Exception {	
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.addFilter(new HTTPBasicAuthFilter(username, password));      
        
        String contentType = URLConnection.guessContentTypeFromName(url);
        
        WebResource webResource = client.resource(url);
        webResource.type(contentType);
        
        ClientResponse content = webResource.get(ClientResponse.class);
                
        byte[] buffer = new byte[0];        
        buffer = content.getEntity(buffer.getClass());
        
        return buffer;
	}
}