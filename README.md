JIRA Agile Exporter
===================

JIRA exporter is a tool I created for helping me with a SCRUM project. At that moment I needed an exporter that was able to
extract user stories from Sprints / Product backlog for SCRUM ceremonies. More over I also used this tool to extract a complete
backlog that was used for Affinity Estimations.

Supported formats
-----------------

Currently there are two supported formats in Jira exporter:

   * BULK - a format which only extracts the user story information (without any attachment).
   * SUSIE - a format which extracts full details about the user stories as well as information about the story theme.

Getting started
---------------

1. git clone git://github.com/rcosnita/jira-exporter.git
2. cd jira-exporter
3. mvn clean install
4. For bulk format execute: java -jar target/jira-exports-scrum-1.0-SNAPSHOT-jar-with-dependencies.jar --format bulk --username [your jira username] --password [your jira password] --startat 0 --maxresults 1000 --query "[your jira jql you want to execute]"
5. For susie format execute: java -jar target/jira-exports-scrum-1.0-SNAPSHOT-jar-with-dependencies.jar --format susie --username [your jira username] --password [your jira password] --startat 0 --maxresults 1000 --workproject '[your jira project id]' --version '[your jira version]'

Be aware that for susie document you need a structure like the following:

   1. Group user stories within themes. 
   2. Assign a label to each user story with the following format: epic_<you_project_id>_<theme_number>
   3. Schedule user stories for a version (ex: Sprint 1).
   4. Into the assigned version create a card of type information where you describe the sprint goal.
   5. This is it.

Samples
-------

Directly in the source code you can find two samples that shows the output of the jira exporter:

   * [BULK](https://github.com/rcosnita/jira-exporter/blob/master/bulk/bulk.pdf?raw=true)
   * [SUSI](https://github.com/rcosnita/jira-exporter/blob/master/susie/susie.pdf?raw=true)
