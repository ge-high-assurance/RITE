# RITE - Libraries

There are two ways to add a library to RITE.

## Get the Library from an Eclipse P2 Repository

The preferred and best way is to get the library from an Eclipse P2
repository if possible, such as
[ORBIT](https://download.eclipse.org/tools/orbit/downloads/drops/R20211213173813/).
If you can find the library in ORBIT, then you need to edit only 2
files to add the library to the RACK plugin:

1. Edit <rack/rack.targetplatform/rack.targetplatform.target>, find
   the location for the ORBIT repository, and insert a new unit line
   to add the library to the RACK Plugin's target platform.  Set the
   unit's id to the library's bundle id (something like
   "com.google.gson") and set the unit's version to "0.0.0".  Please
   keep all the unit lines sorted in alphabetical order at all times.

2. Edit <rack/rack.plugin/META-INF/MANIFEST.MF>, find the
   Require-Bundle section, and insert a line to add the library to the
   RACK Plugin's required bundles.  Add the same bundle id that was
   used in step 1 above.  Please keep all the Require-Bundle lines
   sorted in alphabetical order at all times.

If the library is in a different Eclipse P2 repository (not the ORBIT
repository), then you may have to add a new location containing that
repository's url to the file in step 1 first before adding the new
unit line to its location.

## Get the Library from a Maven Repository

The second way is to get the library from a Maven repository, such as
[Maven Central](https://mvnrepository.com/repos/central).  You should
use this way only if you cannot find the library in an Eclipse P2
repository since you will have to edit 4 files, not 2 files, to add
the library to the RACK plugin:

1. Edit <rack/pom.xml> and add the library's Maven coordinates to the
   dependenciesManagement section.

2. Edit <rack.plugin/META-INF/MANIFEST.MF> and add the library's jar
   to Bundle-ClassPath.  Please keep the Bundle-ClassPath lines sorted
   in alphabetical order at all times.

3. Edit <rack.plugin/build.properties> and add the library's jar to
   bin.includes.  Please keep the bin.includes lines sorted in
   alphabetical order at all times.

4. Edit <rack.plugin/pom.xml> and add the library's Maven coordinates
   (omit the version here) to the dependencies section.

If the library brings in new transitive dependencies, you will see new
jar names appear in the rack/rack.plugin/lib directory that were not
there before.  You may have to repeat the same steps above for these
transitive dependencies as well to avoid getting class not found
exceptions at runtime.  This is another reason why the first way is
better; when you get the library from an Eclipse P2 repository, you
should not have to repeat the process for its transitive dependencies.
If the Tycho build cannot find a required transitive dependency for
some reason, the build will halt with an error message telling you the
missing dependency so you can add another repository location or
another unit line to the target platform file.
