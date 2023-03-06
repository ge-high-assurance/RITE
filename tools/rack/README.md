# Building RACK Integrated Development Environment (IDE)

## About the RACK IDE
RACK IDE bundles the [Semantic Application Design Language (SADL)](http://sadl.sourceforge.net) and [Rapid Assurance Curation Kit (RACK)](https://github.com/ge-high-assurance/RACK) to facilitate the certification and assurance of systems.


Prerequisites:
-------------
1) Have Rack-in-a-box running ([Instructions here](https://github.com/ge-high-assurance/RACK/wiki/02-Run-a-RACK-Box-container)).
   
2) Have Java 11 installed on your machine
    Just follow the JDK 11 installation steps at : 
     
     https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
3) Have Maven installed:

   On Mac: ```brew install maven```
   
   On Windows: https://maven.apache.org/install.html
     

Steps to use the RACK IDE as an Eclipse Instance
---------------------------------------------------
1) Configure Core and Overlay Projects in the RACK Preference Page, click Apply and Close
    a) Core project is typical RACK-Ontology SADL project from the RACK github repo
    b) Overlay project can be GE-Ontology or SRI-Ontology and so on  
    
2) From the RACK Panel, click on Upload Overlay Ontology 

3) Click on Generate Nodegroups to generate all nodegroups locally: the nodegroups (json files) will be generated in the Overlay Project's nodegroup folder

4) Click on Upload Nodegroups to upload all the nodegroups to RACK

5) Try Loading all the nodegroups: the nodegroups must be filtered into Core, Overlay and Other nodegroups

6) Working with nodegroups: 
     a) Can check a nodegoup, right-click on it and select "Create instance Data" to create a CSV file associated with the nodegroup
	 b) The CSV file will be created in the InstanceData folder
	 c) After making some entries into the CSV table, right click on the table -> Ingest CSV to RACK to directly upload the CSV to RACK
	 d) After uploading CSV, we can quickly check whether the data was indeed uploading, by right-clicking on nodegroup -> Query Nodegroup, to see its 
	    query results
	    Note that: data ingestion is not always guaranteed to work as there might be dependencies between nodegroups inorder to successfully ingest data
	    This is part of future work


For Developers
--------------
1) Follow the [rack-in-a-box instructions](https://github.com/ge-high-assurance/RACK/wiki/02-Run-a-RACK-Box-container#step-2-download-a-rack-box-container-image) to start the RACK as a docker container
2) Download [Eclipse IDE for Java developers 2022-03](https://www.eclipse.org/downloads/packages/release/2022-03/r/eclipse-ide-java-developers)  
   This version of eclipse was used to test the RACK plugin, other versions may work
3) Import the repo's `/assurance-case-pipeline/tools/rack` folder as a Maven project in Eclipse by navigating to `Application Menu Bar > File > Import > Existing Maven Projects`
4) Update targetplatform
    a) Open rack.targetplatform project, and double-click on `rack.targetplatform.target` file. The target defintion UI (pictured below) should appear. If it does not, follow the Eclipse PDE installation instructions at the end.
    <img width="947" alt="image" src="https://user-images.githubusercontent.com/44778536/199801631-27d74fe5-809d-47f5-9d81-8a5c70c7f0f2.png">
    b) Click on Update Target Platform (or Reload Target Platform) in the top right corner of the target definition UI (pictured above). 
       Note: This step will download all required dependencies on initialization
5) From a command line, navigate to the rack directory : `/assurance-case-pipeline/tools/rack`
6) Run `mvn clean install`. This will build all the projects inside rack folder and also the plugin
7) From the Eclipse IDE, select all projects and `right-click on any selected project > Maven > Update Project...`. After the update finishes, `right-click on rack.plugin > Run As > Run Configurations... > Double click Eclipse Application > Run` (pictured below). 	    	Subsequent runs can use the same configuration. This will launch the RACK Plugin in a SADL Eclipse Environment 
	<img width="1062" alt="image" src="https://user-images.githubusercontent.com/44778536/199816508-9b100b99-74b3-432a-96d2-9c8bd3022906.png">

#### Eclipse IDE Installation Instructions:
*Access to the Eclipse marketplace is required. For GE users, disable corporate proxies*
1. From eclipse open the eclipse marketplace by clicking `App Menu Bar > Help > Eclipse Marketplace...` 
2. In the Search tab, search for and install the Eclipse PDE (Plugin Development Environment) latest 
3. Click install
4. After the plugin is installed restart Eclipse


