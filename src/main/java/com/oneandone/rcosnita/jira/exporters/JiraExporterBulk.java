package com.oneandone.rcosnita.jira.exporters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.oneandone.rcosnita.jira.exporters.models.JiraIssue;
import com.oneandone.rcosnita.jira.threads.IssueDownloaderLight;
import com.oneandone.rcosnita.jira.threads.ThreadPoolExecutor;
import com.oneandone.rcosnita.jira.util.JiraMarkupFormatters;


/**
 * Class used to provide the bulk exporter from Jira. This is extremly useful
 * when exporting data for affinity estimations.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 */
public class JiraExporterBulk extends JiraExporterAbstract {
	private final static String OUTPUT_FILE = "bulk.html";
	private final static String TEMPLATE_FILE = "bulk_template.html";
	private final static String OUTPUT_FOLDER = "bulk";
	
	private String jiraQuery;
	
	/**
	 * Constructor used to initialize required attributes for this exporter.
	 * 
	 * @param jiraFacade
	 * @param jiraQuery
	 * @param startAt
	 * @param maxResults
	 */
	public JiraExporterBulk(String baseUri, String linksUri, String username, String password, String jiraQuery, 
			Integer startAt, Integer maxResults) {
		super(new JiraFacadeRest(baseUri, linksUri, username, password), 
				startAt, maxResults, OUTPUT_FOLDER, "BULK");
		
		this.jiraQuery = jiraQuery;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export() throws Exception {
		List<JiraIssue> issues = this.getJiraFacade().executeQuery(this.jiraQuery, this.getStartAt(), this.getMaxResults());
		
		Set<Future<JiraIssue>> runningThreads = new HashSet<Future<JiraIssue>>();
		
		for(JiraIssue issue : issues) {			
			IssueDownloaderLight downloader = new IssueDownloaderLight(getJiraFacade(), issue, this.getOutputFolder());
			runningThreads.add(ThreadPoolExecutor.getThreadPool().submit(downloader));
		}
		
		for(Future<JiraIssue> jiraDownloader : runningThreads) {
			JiraIssue issue = jiraDownloader.get();
			issue.setDescription(JiraMarkupFormatters.transformJiraMarkupToHtml(issue.getDescription()));
		}
		
		exportBulkHtml(issues);
	}
	
	/**
	 * Method used to export the issues to html bulk format.
	 * @param issues The complete list of issues we want to export.
	 * @throws Exception
	 */
	private void exportBulkHtml(List<JiraIssue> issues) throws Exception {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("file.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate(TEMPLATE_FILE);
        VelocityContext context = new VelocityContext();
        context.put("issueList", issues);
        
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