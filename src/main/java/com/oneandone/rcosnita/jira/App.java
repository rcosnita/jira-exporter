package com.oneandone.rcosnita.jira;

import jargs.gnu.CmdLineParser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.oneandone.rcosnita.jira.exporters.JiraExporter;
import com.oneandone.rcosnita.jira.exporters.JiraExporterBulk;
import com.oneandone.rcosnita.jira.exporters.JiraExporterSusie;
import com.oneandone.rcosnita.jira.threads.ThreadPoolExecutor;

/**
 * Main entry point for jira exporter application.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 21.02.2012
 */
public class App 
{
	private final static String EOL = System.getProperty("line.separator"); 
	private CmdLineParser argsParser;
	private Map<String, CmdLineParser.Option> parserOptions;
	private Map<String, JiraExporter> supportedFormats;
	
	/**
	 * Public constructor used to initialize the arguments parser attribute as well as other
	 * internal attributes.
	 */
	public App(String[] args, Map<String, JiraExporter> supportedFormats) {
		this.supportedFormats = supportedFormats;		
		
		this.parserOptions = new HashMap<String, CmdLineParser.Option>();		
		this.argsParser = buildArgsParser();
		
		try {
			if(args.length == 0) {
				throw new Exception("No arguments specified.");
			}
			
			this.argsParser.parse(args);
			
			validateArgs();
		}
		catch(Exception opex) {
			System.err.println(opex.getMessage());
			printUsage();
			System.exit(2);
		}		
	}
	
	/**
	 * Method used to build the arguments parser object.
	 * @return
	 */
	private CmdLineParser buildArgsParser() {
		CmdLineParser parser = new CmdLineParser();
		
		parserOptions.put("format", parser.addStringOption('f', "format"));
		parserOptions.put("help", parser.addBooleanOption('h', "help"));
		parserOptions.put("username", parser.addStringOption('u', "username"));		
		parserOptions.put("password", parser.addStringOption('p', "password"));		
		parserOptions.put("workproject", parser.addStringOption('w', "workproject"));
		parserOptions.put("startAt", parser.addIntegerOption('s', "startat"));
		parserOptions.put("maxResults", parser.addIntegerOption('m', "maxresults"));
		parserOptions.put("query", parser.addStringOption('q', "query"));
		parserOptions.put("version", parser.addStringOption('v', "version"));	
		
		return parser;
	}
	
	/**
	 * Method use to print the usage for the command line application.
	 */
	private void printUsage() {
		StringBuilder usage = new StringBuilder();
		
		usage.append("Usage: ");
		usage.append(EOL);
		usage.append("[-f,--format] Specify the format you want to use: SUSIE / BULK");
		usage.append(EOL);
		usage.append("[-h,--help] Option$!issue.storyPointsal argument in which you specify the jira query you want to use.");
		usage.append(EOL);
		usage.append("[-u,--user] Jira username.");
		usage.append(EOL);
		usage.append("[-p,--password] Jira password.");
		usage.append(EOL);
		usage.append("[-q,--query] Optional argument in which you specify the jira query you want to use.");		
		usage.append(EOL);
		usage.append("[-s,--startAt] Jira start retrieving issues from specified index.");
		usage.append(EOL);
		usage.append("[-m,--maxResults] Jira maximum number of retrieved results.");	
		usage.append(EOL);
		usage.append("[-w,--workproject] Optional argument in which you specify the workproject you want to use.");
		usage.append(EOL);		
		usage.append("[-v,--version] Optional argument in which you can specify the version you want to use in your document.");
		
		System.err.println(usage.toString());
	}
	
	/**
	 * Method used to validate the parsed arguments.
	 * 
	 * @throws Exception Throw an exception in case of invalid arguments.
	 */
	private void validateArgs() throws Exception {
		Object format = this.argsParser.getOptionValue(this.parserOptions.get("format"));
		
		if(format == null) {
			throw new Exception("You must specify the format of the export.");
		}		
		
		Object username = this.argsParser.getOptionValue(this.parserOptions.get("username"));
		
		if(username == null) {
			throw new Exception("You must specify the jira username.");
		}
		
		Object password = this.argsParser.getOptionValue(this.parserOptions.get("password"));
		
		if(password == null) {
			throw new Exception("You must specify the jira password.");
		}
	}
	
	/**
	 * Method used to trigger the export process for the application.
	 * 
	 * @throws Exception All internal exceptions are pushed to the upper context.
	 */
	public void export() throws Exception {		
		String formatType = getOptionValue(String.class, "format").toLowerCase().toString();

		JiraExporter exporter = this.supportedFormats.get(formatType);

		exporter.prepareFolders();
		exporter.export();
	}
	
	/**
	 * Method used to return an option value from the command line parser.
	 * 
	 * @param type The type we want to return.
	 * @param name The name of the command line option.
	 * @return
	 * @throws Exception An exception is thrown if the name can not be found.
	 */
	public<T> T getOptionValue(Class<T> type, String name) throws Exception{
		CmdLineParser.Option option = this.parserOptions.get(name);
		
		if(option == null) {
			throw new Exception(String.format("Option %s value can not be retrieved.", name));
		}
		
		return type.cast(this.argsParser.getOptionValue(option));
	}
	
	/**
	 * Method used to read the solution configuration.
	 * 
	 * @return
	 * @throws Exception Throws an exception if the config.properties file is not found.
	 */
	private static Properties readSolutionConfig() throws Exception {
		Properties props = new Properties();
		
		props.load(App.class.getClassLoader().getResourceAsStream("config.properties"));
		
		return props;
	}
	
	/**
	 * Method used to create all supported formats map.
	 * 
	 * @param supportedFormats The map that will hold all supported formats.
	 * @param jira
	 * @param baseUri
	 */
	private static void buildExporters(Map<String, JiraExporter> supportedFormats, 
			App jira, String baseUri, String linksUri) throws Exception {
    	String format = jira.getOptionValue(String.class, "format");
    	String username = jira.getOptionValue(String.class, "username");
    	String password = jira.getOptionValue(String.class, "password");
    	String project = jira.getOptionValue(String.class, "workproject");    	
    	String version = jira.getOptionValue(String.class, "version");
    	String query = jira.getOptionValue(String.class, "query");
    	Integer startAt = jira.getOptionValue(Integer.class, "startAt");
    	Integer maxResults = jira.getOptionValue(Integer.class, "maxResults");
    	
    	System.out.println(String.format("Project %s, version %s, document format %s export started.",
    						project, version, format));
    	
    	supportedFormats.put("susie", new JiraExporterSusie(baseUri, linksUri, username, password, project, version,
    									startAt, maxResults));    	
    	supportedFormats.put("bulk", new JiraExporterBulk(baseUri, linksUri, username, password, query, startAt, maxResults));
	}
	
	/**
	 * Jira export entry point.
	 *  
	 * @param args The command line received arguments.
	 * @throws Exception Exceptions from export process are simple raised to the generic error handler.
	 */
    public static void main(String[] args) throws Exception {
    	long start = Calendar.getInstance().getTimeInMillis();
    	
    	Properties config = readSolutionConfig();
    	
    	int numThreads = Integer.parseInt(config.getProperty("max_threads"));
    	ThreadPoolExecutor.getThreadPool(numThreads);
    	
    	String baseUri = config.getProperty("jira_1and1");
    	String linksUri = config.getProperty("jira_1and1_links");
    	
    	Map<String, JiraExporter> supportedFormats = new HashMap<String, JiraExporter>();
    	
    	App jira = new App(args, supportedFormats);
    	
    	buildExporters(supportedFormats, jira, baseUri, linksUri);
    	    	
    	jira.export();
    	
    	long end = Calendar.getInstance().getTimeInMillis();
    	
    	System.out.println(String.format("Export process took %s seconds.", (end - start) / 1000));

    	System.out.println("Cleanup procedure.");
    	ThreadPoolExecutor.getThreadPool().shutdown();
    	
    	System.out.println("Done.");
    }
}
