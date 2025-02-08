# Building RITE


Prerequisites:
-------------
1) Have Rack-in-a-box running ([Instructions here](https://github.com/ge-high-assurance/RACK/wiki/02-Run-a-RACK-Box-container)).

2) Have Java 17 installed on your machine
    Just follow the JDK 17 installation steps at :

     https://www.oracle.com/java/technologies/downloads/

3) Have Maven installed:

   On Mac: ```brew install maven```

   On Windows: https://maven.apache.org/install.html


For Developers
--------------
1) Follow the [rack-in-a-box instructions](https://github.com/ge-high-assurance/RACK/wiki/02-Run-a-RACK-Box-container#step-2-download-a-rack-box-container-image) to start the RACK as a docker container
2) Download [Eclipse IDE for Java developers 2022-12](https://www.eclipse.org/downloads/packages/release/2022-12/r/eclipse-ide-java-developers)
   This version of eclipse was used to test the RITE plugin, other versions may work
3) Import the repo's `/RITE/tools/rite` folder as a Maven project in Eclipse by navigating to `Application Menu Bar > File > Import > Existing Maven Projects`
4) Update targetplatform
    a) Open rite.targetplatform project, and double-click on `rite.targetplatform.target` file. The target defintion UI (pictured below) should appear. If it does not, follow the Eclipse PDE installation instructions at the end.
    <img width="947" alt="image" src="https://user-images.githubusercontent.com/44778536/199801631-27d74fe5-809d-47f5-9d81-8a5c70c7f0f2.png">
    b) Click on Update Target Platform (or Reload Target Platform) in the top right corner of the target definition UI (pictured above).
       Note: This step will download all required dependencies on initialization
5) From a command line, navigate to the rite directory : `/RITE/tools/rite`
6) Run `mvn clean install`. This will build all the projects inside rite folder and also the plugin
7) From the Eclipse IDE, select all projects and `right-click on any selected project > Maven > Update Project...`. After the update finishes, `right-click on rite.plugin > Run As > Run Configurations... > Double click Eclipse Application > Run` (pictured below). 	    	Subsequent runs can use the same configuration. This will launch the RITE Plugin in a SADL Eclipse Environment
	<img width="1062" alt="image" src="https://user-images.githubusercontent.com/44778536/199816508-9b100b99-74b3-432a-96d2-9c8bd3022906.png">

#### Eclipse IDE Installation Instructions:
*Access to the Eclipse marketplace is required. For GE users, disable corporate proxies*
1. From eclipse open the eclipse marketplace by clicking `App Menu Bar > Help > Eclipse Marketplace...`
2. In the Search tab, search for and install the Eclipse PDE (Plugin Development Environment) latest
3. Click install
4. After the plugin is installed restart Eclipse
