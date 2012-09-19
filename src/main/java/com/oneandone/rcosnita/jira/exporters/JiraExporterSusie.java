package com.oneandone.rcosnita.jira.exporters;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.oneandone.rcosnita.jira.exporters.models.JiraAttachment;
import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;
import com.oneandone.rcosnita.jira.threads.AttachmentsDownloader;
import com.oneandone.rcosnita.jira.threads.IssueDownloaderLight;
import com.oneandone.rcosnita.jira.threads.ThreadPoolExecutor;
import com.oneandone.rcosnita.jira.util.JiraMarkupFormatters;

/**
 * Class used to export the SUSIE document.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 21.02.2012
 */
public class JiraExporterSusie extends JiraExporterAbstract {
	private final static String OUTPUT_FILE = "susie.html";
	private final static String TEMPLATE_FILE = "susie_template.html";
	private final static String OUTPUT_FOLDER = "susie";
	private final static int IMG_MAX_WIDTH = 600;
	private final static int IMG_MAX_HEIGHT = 400;
	
	private String project;
	private String version;
	
	/**
	 * Cosntructor used to initialize the susie exporter. Internally a <code>JiraFacadeRest</code>
	 * instance is created.
	 * 
	 * @param baseUri REST base location.
	 * @param username Jira username
	 * @param password Jira password.
	 */
	public JiraExporterSusie(String baseUri, String linksUri, String username, String password, String project, 
			String version, Integer startAt, Integer maxResults) {
		super(new JiraFacadeRest(baseUri, linksUri, username, password), 
				startAt, maxResults, OUTPUT_FOLDER, "SUSIE");
		
		this.project = project;
		this.version = version;		
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export() throws Exception {
		String query = String.format("project = '%s' and fixVersion='%s' and issuetype='STORY' and status = 'OPEN' order by \"GreenHopper Ranking\", priority desc",
							this.project, this.version);
		
		List<JiraIssue> issues = this.getJiraFacade().executeQuery(query, this.getStartAt(), this.getMaxResults());
		
		Set<Future<JiraIssue>> runningThreads = new HashSet<Future<JiraIssue>>();
		
		for(JiraIssue issue : issues) {			
			IssueDownloaderLight downloader = new IssueDownloaderLight(getJiraFacade(), issue, this.getOutputFolder()) {
				@Override
				public JiraIssue call() throws Exception {
					JiraIssue issue = super.call();
					
					List<JiraIssue> issues = new ArrayList<JiraIssue>();
					issues.add(issue);
					
					saveAttachmentsToDisk(issues);
					
					return issue;
				}
			};

			runningThreads.add(ThreadPoolExecutor.getThreadPool().submit(downloader));
		}
		
		for(Future<JiraIssue> jiraDownloader : runningThreads) {
			JiraIssue issue = jiraDownloader.get();
			
			if(issue.getDescription() == null) {
				throw new RuntimeException(String.format("Please provide a description for issue %s:%s.", issue.getId(), issue.getSummary()));
			}
			
			issue.setDescription(JiraMarkupFormatters.transformJiraMarkupToHtml(issue.getDescription()));
		}
		
		Map<JiraIssue, List<JiraIssue>> susie = buildSusieDataStructure(issues);
		buildEpicsDetail(susie.keySet());
		
		buildSusieDocument(susie);
	}
	
	/**
	 * Method used to build the SUSIE document data structure that can later on be converted
	 * to the correct format.
	 * 
	 * @return
	 * @throws Exception if an issue does not have an epic associated.
	 */
	private Map<JiraIssue, List<JiraIssue>> buildSusieDataStructure(List<JiraIssue> issues) throws Exception {
		Map<JiraIssue, List<JiraIssue>> susie = new HashMap<JiraIssue, List<JiraIssue>>();
		
		for(JiraIssue issue : issues) {
			if(!issue.getStatus().equalsIgnoreCase("open")) {
				continue;
			}			
			
			JiraIssue epic = issue.getEpic();
			
			if(epic == null || epic.getId().isEmpty()) {
				throw new Exception(String.format("No epic specified for issue %s.", issue.getId()));
			}
			
			if(!susie.containsKey(epic)) {
				susie.put(epic, new ArrayList<JiraIssue>());
			}
			
			List<JiraIssue> epicIssues = susie.get(epic);
			
			epicIssues.add(issue);
		}
		
		return susie;
	}
	
	/**
	 * Method used to download all specified epics detail.
	 * 
	 * @param epics A list of epic ids we want to use,
	 * @return
	 */
	private void buildEpicsDetail(Set<JiraIssue> epics) throws Exception {
		Set<Future<JiraIssue>> runningThreads = new HashSet<Future<JiraIssue>>();
		
		for(JiraIssue epic : epics) {
			IssueDownloaderLight downloader = new IssueDownloaderLight(getJiraFacade(), epic, getOutputFolder());
			runningThreads.add(ThreadPoolExecutor.getThreadPool().submit(downloader));
		}
		
		for(Future<JiraIssue> downloader : runningThreads) {
			JiraIssue epic = downloader.get();
			
			if(epic.getDescription() == null) {
				throw new RuntimeException(String.format("Please provide a description for epic %s:%s.", epic.getId(), epic.getSummary()));
			}
			
			epic.setDescription(JiraMarkupFormatters.transformJiraMarkupToHtml(epic.getDescription()));
			
			List<JiraIssue> attachmentIssues = new ArrayList<JiraIssue>();
			attachmentIssues.add(epic);
			
			saveAttachmentsToDisk(attachmentIssues);
		}
	}	
	
    /**
     * Method used to download the information issue for the specified sprint.
     * 
     * @return
     * @throws Exception
     */
    private JiraIssue getVersionInfo() throws Exception {
    	String query = String.format("project='%s' and fixVersion='%s' and issuetype='INFORMATION'", 
    						this.project, this.version);
    	
    	List<JiraIssue> issues = getJiraFacade().executeQuery(query, 0, 1);
    	
    	if(issues.isEmpty()) {
    		throw new Exception("No information for the current version. Unable to generate SUSIE.");
    	}
    	
    	Future<JiraIssue> downloader = ThreadPoolExecutor.getThreadPool()
    										.submit(new IssueDownloaderLight(getJiraFacade(), issues.get(0), OUTPUT_FOLDER));
    	JiraIssue info = downloader.get();
    	
    	info.setDescription(JiraMarkupFormatters.transformJiraMarkupToHtml(info.getDescription()));
    	
    	issues = new ArrayList<JiraIssue>();
    	issues.add(info);
    	
    	this.saveAttachmentsToDisk(issues);
    	
    	return info;
    }
    
    /**
     * Method used to save all attachments on disk.
     */
    private void saveAttachmentsToDisk(List<JiraIssue> issues) throws Exception {
    	Set<Future<JiraIssue>> downloaders = new HashSet<Future<JiraIssue>>();
    	
    	for(JiraIssue issue : issues) {
    		Future<JiraIssue> downloader = ThreadPoolExecutor.getThreadPool().submit(new AttachmentsDownloader(getJiraFacade(), issue));
    		downloaders.add(downloader);
    	}
    	
    	for(Future<JiraIssue> downloader : downloaders) {   		
    		JiraIssue issue = downloader.get(); 
    		
    		for(JiraAttachment attachment : issue.getAttachments()) {
    			if(attachment.getType().contains("image")) {
    				attachment.setRenderTag("img");
    				
    				String imgPath = String.format("%s/%s", OUTPUT_FOLDER, attachment.getName());
    				imgPath = (new File(imgPath)).getAbsolutePath();
    				
    				Image img = Toolkit.getDefaultToolkit().getImage(imgPath);
    				
    			    PixelGrabber grabber = new PixelGrabber(img, 0, 0, -1, -1, false);
    			    grabber.grabPixels();
    			    
    				int width = grabber.getWidth();
    				int height = grabber.getHeight();
    				
    				double ratio = -1;
    				
    				if(width > IMG_MAX_WIDTH) {
	    				ratio = Math.min(width, height) * 1.0 / Math.max(width, height);
	    				width = IMG_MAX_WIDTH;
	    				height = (int)(width * ratio);
    				}
    				else if(height > IMG_MAX_HEIGHT) {
    					ratio = Math.min(width, height) * 1.0 / Math.max(width, height);
	    				height = IMG_MAX_HEIGHT;
	    				width = (int)(height * ratio);    					
    				}

    				attachment.setWidth(width);
    				attachment.setHeight(height);
    				
    			}
    		}
    	}
    }
    
	/**
	 * Method used to transform the susie data structure into an html document.
	 * 
	 * @param epics Epics details. The key is the epic id and the value is the complete JiraIssue.
	 * @param susieData This is the data structure for susie document: key is the epic while the value is the list of issues assigned with the epic.
	 */
	private void buildSusieDocument(Map<JiraIssue, List<JiraIssue>> susieData) throws Exception {
		List<JiraIssue> issues = new ArrayList<JiraIssue>();
		
		saveAttachmentsToDisk(issues);
		
		JiraIssue[] epics = new JiraIssue[susieData.keySet().size()];
		
		Arrays.sort(susieData.keySet().toArray(epics));
		
		for(JiraIssue epic : epics) {
			issues.add(epic);
			
			issues.addAll(susieData.get(epic));
		}
		
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("file.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate(TEMPLATE_FILE);
        VelocityContext context = new VelocityContext();
        context.put("issues", issues);
        context.put("project", project);
        context.put("version", version);
        context.put("versionInfo", this.getVersionInfo());
        
        BufferedWriter writer = null;
        
        try {
        	writer = new BufferedWriter(new FileWriter(String.format("%s/%s", OUTPUT_FOLDER, OUTPUT_FILE)));
        	t.merge(context, writer);
        }
        finally {
        	if(writer != null) {
        		try {
        			writer.close();
        		}
        		catch(Exception ex) {}
        	}
        }
	}
}