import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;


public class BuildManifest{
	
	public static void main (String args[]) throws Exception{
		
	     String manifestDir = args[0];
	     RackManifestIngestionBuilderUtil.version = args[1];
	     Path target = Files.createTempFile("rack-ingestion-package", ".zip");
	     zipIt((new File(manifestDir)).toPath(), target);
		
	}
	
	 private static Path zipIt(Path folder, Path zipFilepath)
	            throws IOException, Exception {

	        zipFilepath.toFile().setReadable(true, false);
	        zipFilepath.toFile().setWritable(true, false);

	        try (FileOutputStream fos = new FileOutputStream(zipFilepath.toFile());
	                ZipOutputStream zipStream = new ZipOutputStream(fos)) {

	          
	            new RackManifestIngestionBuilderUtil().zipManifestResources(folder, zipStream);
	        }

	        return zipFilepath;
	    }

}