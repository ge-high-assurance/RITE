/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, General Electric Company and Galois, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ge.research.rack.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Set;

// Adapted from: https://www.javatpoint.com/json-validator-java

// create class to validate JSON document
public class YamlValidator {

    // create inputStreamFromClasspath() method to load the JSON data from the class path
    private static InputStream inputStreamFromClasspath(String path) {

        // returning stream
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    // main() method start
    public static String validate(Object yaml, File schemaFile) throws Exception {

        // create instance of the ObjectMapper class
        ObjectMapper objectMapper = new ObjectMapper();

        // create an instance of the JsonSchemaFactory using version flag
        JsonSchemaFactory schemaFactory =
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

        // store the JSON data in InputStream
        try (InputStream schemaStream = new FileInputStream(schemaFile)) {

            // get schema from the schemaStream and store it into JsonSchema
            JsonSchema schema = schemaFactory.getSchema(schemaStream);

            // convert yaml to json
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(yaml, LinkedHashMap.class);

            // read data from the stream and store it into JsonNode
            JsonNode json = objectMapper.readTree(jsonString);

            // create set of validation message and store result in it
            Set<ValidationMessage> validationResult = schema.validate(json);

            StringBuilder sb = new StringBuilder();

            // show all the validation error
            validationResult.forEach(
                    vm -> {
                        sb.append(vm.getMessage() + "\n");
                    });

            return sb.toString();
        } catch (Exception e) {
            return "Exception during validation against schema\n" + e.getMessage() + "\n";
        }
    }
}
