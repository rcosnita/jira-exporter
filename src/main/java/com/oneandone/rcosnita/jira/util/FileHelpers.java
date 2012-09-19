package com.oneandone.rcosnita.jira.util;

import java.io.File;
import java.io.IOException;

/**
 * Class used to provide common files operations that are not provided by jdk.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 *
 */
public class FileHelpers {
	/**
	 * Method used to delete a file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void deleteFile(File file) throws IOException {
    	if(file.isDirectory()){
    		 
    		//directory is empty, then delete it
    		if(file.list().length==0){
 
    		   file.delete();
    		   System.out.println("Directory is deleted : " 
                                                 + file.getAbsolutePath());
 
    		}else{
 
    		   //list all the directory contents
        	   String files[] = file.list();
 
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
 
        	      //recursive delete
        	     deleteFile(fileDelete);
        	   }
 
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     System.out.println("Directory is deleted : " 
                                                  + file.getAbsolutePath());
        	   }
    		}
 
    	}else{
    		//if file, then delete it
    		file.delete();
    		System.out.println("File is deleted : " + file.getAbsolutePath());
    	}		
	}
}
