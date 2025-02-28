/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.autoGsn.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
// import org.davidmoten.text.utils.WordWrap;

/**
 * Generic functions for string manipulation
 *
 * @author Saswata Paul
 */
public class CustomStringUtils {

    /**
     * returns the gsn artifacts output path as a subfolder of the instance's project
     *
     * @param instanceFilePath
     * @return
     */
    public static String getGsnOutDir(String instanceFilePath) {
        // String dirPath = "";

        // get the instance folder path
        String instDir = CustomFileUtils.getFileDirectory(instanceFilePath);

        // TO DO: For now, returning the same path as the instance
        // Future: Create a different folder for the GSN artifacts

        return instDir;
    }

    /**
     * Takes a string and returns substring present within quotes
     *
     * @param str
     * @return
     */
    public static String returnContentWithinQuotes(String str) {
        String out = "";

        int first = 0;
        int last = str.length();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\"') {
                first = i + 1;
                for (int j = i + 1; j < str.length(); j++) {
                    if (str.charAt(j) == '\"') {
                        last = j; // substring function will return before this
                        break;
                    }
                }
                break;
            }
        }

        out = out + str.substring(first, last);

        return out;
    }

    /**
     * Checks if a list contains a string
     *
     * @param list
     * @param str
     * @return
     */
    public static Boolean listContains(List<String> list, String str) {
        for (String element : list) {
            if (element.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints a list of sentences on console standard output
     *
     * @param sentences
     */
    public static void printListString(List<String> sentences) {
        for (int i = 0; i < sentences.size(); i++) {
            System.out.println(sentences.get(i));
        }
    }

    /**
     * Takes a string and wraps it by adding a linebreak after every 50 characters Used to edit goal
     * statements which are taken "as is" from verdict
     *
     * @param original
     * @return
     */
    public static String stringWrapper(String original) {
        // normalize spaces in the string
        String normal = original.replaceAll("\\s{2,}", " ").trim();

        // TODO implement a wrapper
        String out = normal.replaceAll(".{30}", "$0\n"); // TODO: Test

        return out;
    }

    /**
     * Takes a list of strings and filepath and saves the list as a new file (deletes if older
     * exists)
     *
     * @param str
     * @param filePath
     * @throws IOException
     */
    public static void writeStrListAsNewFile(List<String> str, String filePath) throws IOException {

        try {
            // Delete file if exists
            Path fileToDeletePath = Paths.get(filePath);
            Files.delete(fileToDeletePath);
        } catch (Exception e) {
            System.out.println("No old file. Creating new.");
        }

        try {
            // Create fileoutputstreams for writing to the file
            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (int i = 0; i < str.size(); i++) {
                bw.write(str.get(i));
            }

            // close file
            bw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a string and filepath and saves the string as a new file (deletes if older exists)
     *
     * @param str
     * @param filePath
     * @throws IOException
     */
    public static void writeStrAsNewFile(String str, String filePath) throws IOException {

        try {
            // Delete file if exists
            Path fileToDeletePath = Paths.get(filePath);
            Files.delete(fileToDeletePath);
        } catch (Exception e) {
            System.out.println("No old file. Creating new.");
        }

        try {
            // Create fileoutputstreams for writing to the file
            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            bw.write(str);

            // close file
            bw.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Given "elementId: Description" returns "elementId"
     *
     * @param elementIdWithDescription
     * @return
     */
    public static String separateElementIdFromDescription(String elementIdWithDescription) {
        String[] parts = elementIdWithDescription.split("\\:");

        return parts[0];
    }

    /**
     * Remoes duplicates from a given list of strings
     *
     * @param nonUnique
     * @return
     */
    public static List<String> removeDuplicates(List<String> nonUnique) {
        // Create a new LinkedHashSet
        Set<String> set = new LinkedHashSet<>();

        // Add the elements to set to remove duplicates
        set.addAll(nonUnique);

        List<String> unique = new ArrayList<String>();

        unique.addAll(set);

        return unique;
    }

    /**
     * Takes a string and replaces all commas in quotes with space
     *
     * @param str
     * @return
     */
    public static String removeCommasInQuotes(String str) {
        boolean deleteCommas = false;
        String newStr = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\"') { // found a quote
                newStr = newStr + str.charAt(i);
                if (!deleteCommas) { // quote is a starting quote
                    deleteCommas = true; // start deleting commas
                    for (int j = i + 1;
                            j < str.length();
                            j++) { // looking at chars after starting quote
                        if (str.charAt(j) == ',') {
                            newStr = newStr + " "; // replacing commas in quote with space
                            // need to move i to j+1 so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j + 1;

                        } else if (str.charAt(j) == '\"') { // found ending quote
                            deleteCommas = false; // will not delete commas
                            newStr = newStr + str.charAt(j); // add the quote
                            // need to move i to j so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j;
                            break; // get out of inner loop once the end quote is found
                        } else { // chars after a starting quote, but not a comma or ending quote
                            newStr = newStr + str.charAt(j); // add the char
                            // need to move i to j+1 so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j + 1;
                        }
                    }
                } else if (deleteCommas) { // quote is an ending quote
                    deleteCommas = false; // will not delete commas
                    newStr = newStr + str.charAt(i); // add the quote
                }
            } else { // char not a quote
                newStr = newStr + str.charAt(i); // add the char
            }
        }

        return newStr;
    }

    /**
     * Takes a string and removes all commas, tabs, and newlines within quotes
     *
     * @param str
     * @return
     */
    public static String removeCommasAndNewlinesInQuotes(String str) {
        boolean deleteCommas = false;
        String newStr = "";
        if (str == null) {
            return "";
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\"') { // found a quote
                newStr = newStr + str.charAt(i);
                if (!deleteCommas) { // quote is a starting quote
                    deleteCommas = true; // start deleting commas
                    for (int j = i + 1;
                            j < str.length();
                            j++) { // looking at chars after starting quote
                        if (str.charAt(j) == ','
                                || str.charAt(j) == '\t'
                                || str.charAt(j) == '\n'
                                || str.charAt(j) == '\r') {
                            newStr = newStr + ""; // replacing commas/newlines in quote with nothing
                            // need to move i to j+1 so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j + 1;

                        } else if (str.charAt(j) == '\"') { // found ending quote
                            deleteCommas = false; // will not delete commas
                            newStr = newStr + str.charAt(j); // add the quote
                            // need to move i to j so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j;
                            break; // get out of inner loop once the end quote is found
                        } else { // chars after a starting quote, but not a comma or ending quote
                            newStr = newStr + str.charAt(j); // add the char
                            // need to move i to j+1 so that the outer loop does not repeat chars
                            // already added in inner loop
                            i = j + 1;
                        }
                    }
                } else if (deleteCommas) { // quote is an ending quote
                    deleteCommas = false; // will not delete commas
                    newStr = newStr + str.charAt(i); // add the quote
                }
            } else { // char not a quote
                newStr = newStr + str.charAt(i); // add the char
            }
        }

        return newStr;
    }

    /**
     * Given a csv headerline and a key, returns the column index for the key
     *
     * @param headerLine
     * @param key
     * @return
     */
    public static Integer getCSVColumnIndex(String[] headerLine, String key) {
        for (int i = 0; i < headerLine.length; i++) {
            if (headerLine[i].equalsIgnoreCase(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Creates a string by repeating a character a given number of times
     *
     * @param n
     * @param c
     * @return
     */
    public static String repeatChar(int n, char c) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s = s + c;
        }
        return s;
    }

    // checks if a word exists in a sentence
    public static boolean checkWordExistence(String word, String sentence) {

        if (sentence.contains(word)) {
            int start = sentence.indexOf(word);
            int end = start + word.length();

            boolean valid_left = ((start == 0) || (sentence.charAt(start - 1) == ' '));
            boolean valid_right = ((end == sentence.length()) || (sentence.charAt(end) == ' '));

            return valid_left && valid_right;
        }
        return false;
    }

    public static boolean checkWordExistenceV2(String word, String sentence) {
        sentence = sentence.toUpperCase();
        word = word.toUpperCase();
        if (sentence.contains(word)) {
            int start = sentence.indexOf(word);
            int end = start + word.length();

            boolean valid_left = ((start == 0) || (sentence.charAt(start - 1) == ' '));
            boolean valid_right = ((end == sentence.length()) || (sentence.charAt(end) == ' '));

            return valid_left && valid_right;
        }
        return false;
    }
}
