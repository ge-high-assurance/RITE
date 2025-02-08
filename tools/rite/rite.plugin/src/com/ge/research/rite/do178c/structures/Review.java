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
package com.ge.research.rite.do178c.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class Review {

    private String id;

    private String type;

    private Engineer author;

    private DataItem reviewed; // (SRS or PSSA or ERD)

    private List<Engineer> reviewers =
            new ArrayList<Engineer>(); // initializing so that the list does not need to be created

    private List<ReviewLog> logs =
            new ArrayList<ReviewLog>(); // initializing so that the list does not need to be created

    private List<String> sourceDocument = new ArrayList<String>();

    /**
     * @return the sourceDocument
     */
    public List<String> getSourceDocument() {
        return sourceDocument;
    }

    /**
     * @param sourceDocument the sourceDocument to set
     */
    public void setSourceDocument(List<String> sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the author
     */
    public Engineer getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(Engineer author) {
        this.author = author;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the reviewers
     */
    public List<Engineer> getReviewers() {
        return reviewers;
    }

    /**
     * @param reviewers the reviewers to set
     */
    public void setReviewers(List<Engineer> reviewers) {
        this.reviewers = reviewers;
    }

    /**
     * Add a new reviewer to existing list if it does not already exist
     *
     * @param eng
     */
    public void addAReviewer(Engineer eng) {

        boolean existsFlag = false;
        if (this.reviewers.size() > 0) {
            for (Engineer reviewer : this.reviewers) {
                if (reviewer.getId().equalsIgnoreCase(eng.getId())) {
                    existsFlag = true;
                    break;
                }
            }
        }
        if (!existsFlag) {
            this.reviewers.add(eng);
        }
    }

    /**
     * @return the logs
     */
    public List<ReviewLog> getLogs() {
        return logs;
    }

    /**
     * @param logs the logs to set
     */
    public void setLogs(List<ReviewLog> logs) {
        this.logs = logs;
    }

    /**
     * Add a new log to existing list if it does not already exist
     *
     * @param log
     */
    public void addAReviewLog(ReviewLog log) {
        boolean existsFlag = false;
        if (this.logs.size() > 0) {
            for (ReviewLog revLog : this.logs) {
                if (revLog.getId().equalsIgnoreCase(log.getId())) {
                    existsFlag = true;
                    break;
                }
            }
        }
        if (!existsFlag) {
            this.logs.add(log);
        }
    }

    /**
     * @return the reviewed
     */
    public DataItem getReviewed() {
        return reviewed;
    }

    /**
     * @param reviewed the reviewed to set
     */
    public void setReviewed(DataItem reviewed) {
        this.reviewed = reviewed;
    }
}
