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

import com.ge.research.rack.autoGsn.structures.PatternInfo;

import java.util.List;

/**
 * @author Saswata Paul
 *     <p>Functions to help with List<PatternInfo.StratPat> data structures that represent linear
 *     paths from patterns
 */
public class ListStratPatUtils {

    /**
     * Takes a list of Stratpats represnting a path and prints the path for testing
     *
     * @param stratPatList
     */
    public static void printStratPatListPath(List<PatternInfo.StratPat> stratPatList) {
        // Test printing
        System.out.print("PATH :: ");
        for (PatternInfo.StratPat node : stratPatList) {
            System.out.print(node.getGoalClass());
            System.out.print(" --> ");
            if ((stratPatList.indexOf(node) == stratPatList.size() - 1)) { // last stratpat in list
                System.out.print(node.getSubGoalClass());
                System.out.println("\n");

            } else {

            }
        }
    }

    /**
     * Takes a class Id and all paths and returns the subPath corresponding to the class id
     *
     * <p>Note: Since we restrict only one path per class, all subPaths corresponding to the class
     * will be the same
     *
     * @param classId
     * @param allPaths
     * @return
     */
    public static List<PatternInfo.StratPat> getClassSubPath(
            String classId, List<List<PatternInfo.StratPat>> allPaths) {

        for (List<PatternInfo.StratPat> path : allPaths) {
            for (PatternInfo.StratPat classStratPat : path) {
                if (classStratPat.getGoalClass().equals(classId)) {
                    // return the subPath starting from classStratPat
                    int startIndex = path.indexOf(classStratPat);
                    // get subPath
                    List<PatternInfo.StratPat> subPath = path.subList(startIndex, (path.size()));
                    return subPath;
                }
            }
        }

        return null; // if nothing is found
    }
}
