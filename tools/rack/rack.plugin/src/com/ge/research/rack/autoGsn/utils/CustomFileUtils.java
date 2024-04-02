/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.autoGsn.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

/**
 * @author Saswata Paul
 */
public class CustomFileUtils {

    /**
     * List all files and directories in a given path
     *
     * @param addr
     */
    public static void listFiles(String addr) {
        /* Testing if the resources directory is working */
        File folder = new File(addr);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles.length > 0) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
        } else {
            System.out.println("Empty directory!");
        }
    }

    /**
     * deletes all files with the given extension in a given directory
     *
     * @param ext
     * @param dir
     */
    public static boolean delFilesWithExt(String ext, String dir) {
        boolean deletionSuccessful = false;
        File getDir = new File(dir);

        File[] files = getDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("." + ext)) {
                System.out.println("Deleting " + file.getName());
                deletionSuccessful = (new File(String.valueOf(file)).delete());
            }
        }
        return deletionSuccessful;
    }

    /**
     * Creates a directory if does not already exist, cleans if exists
     *
     * @param dirPath
     */
    public static void createNewDirectory(String dirPath) {
        Path tempDirPath = Paths.get(dirPath);
        new File(tempDirPath.toAbsolutePath().toString()).mkdirs();

        System.out.println("Successfully created directory at: " + dirPath);
    }

    /**
     * Opens a sadl file and gets its uri
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getUriFromSADLFile(String filePath) {

        String uri = "";

        try {
            File file = new File(filePath); // creates a new file instance
            FileReader fr = new FileReader(file); // reads the file
            BufferedReader br =
                    new BufferedReader(fr); // creates a buffering character input stream
            String line;

            while ((line = br.readLine()) != null) {
                String content = line.trim();
                if (content.startsWith("uri")) {
                    System.out.println(content);
                    uri = uri + CustomStringUtils.returnContentWithinQuotes(content);
                    break;
                }
            }
            fr.close(); // closes the stream and release the resources

        } catch (Exception e) {
            System.out.println("ERROR: Could not read SADL file to create URI!");
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * Creates a RACK directory in the /tmp of the machine and returns path to /tmp/RACK
     *
     * @return
     * @throws IOException
     */
    public static String getRackDir() {
        System.out.println("Inside getRackDir");

        // get temporary directory address for the machine
        String tempDir = System.getProperty("java.io.tmpdir");

        System.out.println("Machine tempdir: " + tempDir);

        // get Path object to the temporary directory
        Path tempDirPath = Paths.get(tempDir);

        // create sub-directories for RACK/auto and RACK/images in the Path object for the temp
        // directory
        new File(
                        tempDirPath.toAbsolutePath().toString()
                                + java.io.File.separator
                                + "RACK"
                                + java.io.File.separator
                                + "auto")
                .mkdirs();
        new File(
                        tempDirPath.toAbsolutePath().toString()
                                + java.io.File.separator
                                + "RACK"
                                + java.io.File.separator
                                + "images")
                .mkdirs();

        // Return the temporary directory/RACK path
        return (tempDirPath.toAbsolutePath().toString()
                + java.io.File.separator
                + "RACK"
                + java.io.File.separator);
    }

    /**
     * Returns true if a location is valid, otherwise false
     *
     * @param location
     * @return
     */
    public static boolean validLocation(String location) {
        File locFile = new File(location);
        if (locFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Given a string filepath, gets the path to its directory
     *
     * @param filePath
     * @return
     */
    public static String getFileDirectory(String filePath) {
        String dirPath = "";

        // get the filename
        File f = new File(filePath);
        String fileName = f.getName();

        // remove filename from filePath
        dirPath = dirPath + filePath.replaceAll(fileName, "");

        return dirPath;
    }

    /**
     * Reads an entire file as a string object
     *
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (Exception e) {
            System.out.println("Error: Could not read file: " + path + " as string!");
            return null;
        }
    }

    /**
     * Clears a given directory
     *
     * @param rackDir
     */
    public static void clearDirectory(String rackDir) {
        try {
            // clean the outputs directory
            File targetDirectory = new File(rackDir);
            FileUtils.cleanDirectory(targetDirectory);
        } catch (IOException e) {
            System.out.println("ERROR: Was unable to successfuly clear rackDir!!\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
