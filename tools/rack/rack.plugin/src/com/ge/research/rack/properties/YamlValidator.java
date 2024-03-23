package com.ge.research.rack.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Set;  

import com.fasterxml.jackson.databind.JsonNode;  
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;  
import com.networknt.schema.JsonSchemaFactory;  
import com.networknt.schema.SpecVersion;  
import com.networknt.schema.ValidationMessage;  


// Adapted from: https://www.javatpoint.com/json-validator-java

// create class to validate JSON document  
public class YamlValidator {


	// create inputStreamFromClasspath() method to load the JSON data from the class path    
	private static InputStream inputStreamFromClasspath( String path ) {  

		// returning stream  
		return Thread.currentThread().getContextClassLoader().getResourceAsStream( path );  
	}  

	// main() method start  
	public static String validate(Object yaml, File schemaFile) throws Exception {  

		// create instance of the ObjectMapper class  
		ObjectMapper objectMapper = new ObjectMapper();  

		// create an instance of the JsonSchemaFactory using version flag  
		JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012 );  

		// store the JSON data in InputStream  
		try(  
				InputStream schemaStream = new FileInputStream(schemaFile)  
				){  

			// get schema from the schemaStream and store it into JsonSchema  
			JsonSchema schema = schemaFactory.getSchema(schemaStream);  

			// convert yaml to json
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(yaml,LinkedHashMap.class);

			// read data from the stream and store it into JsonNode  
			JsonNode json = objectMapper.readTree(jsonString);  

			// create set of validation message and store result in it  
			Set<ValidationMessage> validationResult = schema.validate( json );  

			StringBuilder sb = new StringBuilder();

			// show all the validation error  
			validationResult.forEach(vm -> { sb.append(vm.getMessage() + "\n"); });
			
			return sb.toString();
		} catch (Exception e) {
			return "Exception during validation against schema\n" + e.getMessage() + "\n";
		}
	}  
}
