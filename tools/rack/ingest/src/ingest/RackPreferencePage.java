package ingest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class RackPreferencePage {
        
    public static String getProtocol() {
        return "http";
    }

    public static String getServer() {
        return "localhost";
    }

    public static String getUtilityPort() {
        return "12060";
    }

    public static String getNGEPort() {
        return "12058";
    }

    public static String getStorePort() {
        return "12056";
    }

    public static String getQueryPort() {
        return "12050";
    }

    public static String getOntologyPort() {
        return "12057";
    }

    public static String getConnType() {
        return "fuseki";
    }

    public static String getConnURL() {
        return "http://localhost:3030/RACK";
    }

    public static String getDefaultModelGraph() {
        return "http://rack001/model";
    }

    public static String getDefaultDataGraph() {
        return "http://rack001/data";
    }

    public static String getUser() {
        return "rack";
    }

    public static String getPassword() {
        return "rack";
    }

   
    
}
