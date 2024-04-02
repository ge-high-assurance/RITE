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
package com.ge.research.rack.opgsn.logic;

import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.utils.RackQueryUtils;
import com.ge.research.rack.opgsn.structures.OPTree;
import com.ge.research.rack.opgsn.utils.OPTreeUtils;
import com.ge.research.rack.utils.CSVUtil;
import java.util.ArrayList;
import java.util.List;

public class DataProcessor {

    // -- Class variables
    private List<String[]> opTreeData;

    public OPTree opTreeObj = new OPTree();

    public List<OPTree.Argument> argumentObjects = new ArrayList<OPTree.Argument>();

    public List<OPTree.Premise> premiseObjects = new ArrayList<OPTree.Premise>();

    public List<OPTree.Evidence> evidenceObjects = new ArrayList<OPTree.Evidence>();

    /** Reads the csv returned by OPTree.json query and populates the OPTree object of the class */
    public void createOPTreeObject(String rackDir) {

        // read csv data into variable
        opTreeData = CSVUtil.getRows(RackQueryUtils.createCsvFilePath("OPTree", rackDir));

        // get all column numbers
        String[] opTreeCols =
                RackQueryUtils.readCSVHeader(RackQueryUtils.createCsvFilePath("OPTree", rackDir));
        // The following variable names are dependent on the query parameters for easy mapping and
        // may violate java conventions
        int op_argumentCol = CustomStringUtils.getCSVColumnIndex(opTreeCols, "op_argument");
        int justificationCol = CustomStringUtils.getCSVColumnIndex(opTreeCols, "justification");
        int argument_concernsCol =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "argument_concerns");
        int argument_conclusionCol =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "argument_conclusion");
        int opCol = CustomStringUtils.getCSVColumnIndex(opTreeCols, "op");
        int premise_lv0Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "premise_lv0");
        int statement_premise_lv0Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "statement_premise_lv0");
        int evidence_lv0Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "evidence_lv0");
        int detail_lv0Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "detail_lv0");
        int Supportive_lv0Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "Supportive_lv0");
        int EvidenceActivity_lv0Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "EvidenceActivity_lv0");
        int premise_lv1Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "premise_lv1");
        int statement_premise_lv1Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "statement_premise_lv1");
        int evidence_lv1Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "evidence_lv1");
        int detail_lv1Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "detail_lv1");
        int Supportive_lv1Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "Supportive_lv1");
        int EvidenceActivity_lv1Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "EvidenceActivity_lv1");
        int premise_lv2Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "premise_lv2");
        int statement_premise_lv2Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "statement_premise_lv2");
        int evidence_lv2Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "evidence_lv2");
        int detail_lv2Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "detail_lv2");
        int Supportive_lv2Col = CustomStringUtils.getCSVColumnIndex(opTreeCols, "Supportive_lv2");
        int EvidenceActivity_lv2Col =
                CustomStringUtils.getCSVColumnIndex(opTreeCols, "EvidenceActivity_lv2");
        //        System.out.println(op_argumentCol);
        //        System.out.println(justificationCol);
        //        System.out.println(argument_concernsCol);
        //        System.out.println(opCol);
        //        System.out.println(premise_lv0Col);
        //        System.out.println(evidence_lv0Col);
        //        System.out.println(detail_lv0Col);
        //        System.out.println(Supportive_lv0Col);
        //        System.out.println(EvidenceActivity_lv0Col);
        //        System.out.println(premise_lv1Col);
        //        System.out.println(evidence_lv1Col);
        //        System.out.println(detail_lv1Col);
        //        System.out.println(Supportive_lv1Col);
        //        System.out.println(EvidenceActivity_lv1Col);
        //        System.out.println(premise_lv2Col);
        //        System.out.println(evidence_lv2Col);
        //        System.out.println(detail_lv2Col);
        //        System.out.println(Supportive_lv2Col);
        //        System.out.println(EvidenceActivity_lv2Col);

        // -- Create all argument, premise, and evidence objects
        System.out.println("******** Nodes ******** ");
        for (String[] row : opTreeData) {
            if (row.length > op_argumentCol) {

                // find if argument object exists
                Integer argIndx =
                        OPTreeUtils.getArgumentPosition(argumentObjects, row[op_argumentCol]);
                if (argIndx != null) {
                    // add things to existing argument object
                    if (row.length > justificationCol) {
                        argumentObjects.get(argIndx).setJustification(row[justificationCol]);
                        ;
                    }
                    if (row.length > argument_concernsCol) {
                        argumentObjects.get(argIndx).setConcerns(row[argument_concernsCol]);
                        ;
                    }
                    if (row.length > opCol) {
                        argumentObjects.get(argIndx).setOp(row[opCol]);
                        ;
                    }
                    if (row.length > argument_conclusionCol) {
                        argumentObjects.get(argIndx).setConclusion(row[argument_conclusionCol]);
                        ;
                    }
                    System.out.println("Added data to " + argumentObjects.get(argIndx).getId());
                } else {
                    // create new argument object
                    OPTree.Argument newArgObj = new OPTree().new Argument();
                    if (!(row[op_argumentCol].length() == 0)) {
                        // add things to new object
                        newArgObj.setId(row[op_argumentCol]);
                        if (row.length > justificationCol) {
                            newArgObj.setJustification(row[justificationCol]);
                            ;
                        }
                        if (row.length > argument_concernsCol) {
                            newArgObj.setConcerns(row[argument_concernsCol]);
                            ;
                        }
                        if (row.length > opCol) {
                            newArgObj.setOp(row[opCol]);
                            ;
                        }
                        if (row.length > argument_conclusionCol) {
                            newArgObj.setConclusion(row[argument_conclusionCol]);
                            ;
                        }
                        // add object to list
                        argumentObjects.add(newArgObj);
                        System.out.println("Created object for " + newArgObj.getId());
                    }
                }
            }
        }

        for (String[] row : opTreeData) {
            if (row.length > premise_lv0Col) {
                // find if premise object exists
                Integer prmsIndx =
                        OPTreeUtils.getPremisePosition(premiseObjects, row[premise_lv0Col]);
                if (prmsIndx != null) {
                    // add things to existing premise object
                    if (row.length > statement_premise_lv0Col) {
                        premiseObjects.get(prmsIndx).setStatement(row[statement_premise_lv0Col]);
                        ;
                    }
                } else {
                    // create new premise object
                    OPTree.Premise newPrmsObj = new OPTree().new Premise();
                    if (!(row[premise_lv0Col].length() == 0)) {
                        // add things to new object
                        newPrmsObj.setId(row[premise_lv0Col]);
                        if (row.length > statement_premise_lv0Col) {
                            newPrmsObj.setStatement(row[statement_premise_lv0Col]);
                            ;
                        }
                        // add object to list
                        premiseObjects.add(newPrmsObj);
                        System.out.println("Created object for " + newPrmsObj.getId());
                    }
                }
            }
        }
        for (String[] row : opTreeData) {
            if (row.length > premise_lv1Col) {
                // find if premise object exists
                Integer prmsIndx =
                        OPTreeUtils.getPremisePosition(premiseObjects, row[premise_lv1Col]);
                if (prmsIndx != null) {
                    // add things to existing premise object
                    if (row.length > statement_premise_lv1Col) {
                        premiseObjects.get(prmsIndx).setStatement(row[statement_premise_lv1Col]);
                        ;
                    }
                } else {
                    // create new premise object
                    OPTree.Premise newPrmsObj = new OPTree().new Premise();
                    if (!(row[premise_lv1Col].length() == 0)) {
                        // add things to new object
                        newPrmsObj.setId(row[premise_lv1Col]);
                        if (row.length > statement_premise_lv1Col) {
                            newPrmsObj.setStatement(row[statement_premise_lv1Col]);
                            ;
                        }
                        // add object to list
                        premiseObjects.add(newPrmsObj);
                        System.out.println("Created object for " + newPrmsObj.getId());
                    }
                }
            }
        }
        for (String[] row : opTreeData) {
            if (row.length > premise_lv2Col) {
                // find if premise object exists
                Integer prmsIndx =
                        OPTreeUtils.getPremisePosition(premiseObjects, row[premise_lv2Col]);
                if (prmsIndx != null) {
                    // add things to existing premise object
                    if (row.length > statement_premise_lv2Col) {
                        premiseObjects.get(prmsIndx).setStatement(row[statement_premise_lv2Col]);
                        ;
                    }

                } else {
                    // create new premise object
                    OPTree.Premise newPrmsObj = new OPTree().new Premise();
                    if (!(row[premise_lv2Col].length() == 0)) {
                        // add things to new object
                        newPrmsObj.setId(row[premise_lv2Col]);
                        if (row.length > statement_premise_lv2Col) {
                            newPrmsObj.setStatement(row[statement_premise_lv2Col]);
                            ;
                        }
                        // add object to list
                        premiseObjects.add(newPrmsObj);
                        System.out.println("Created object for " + newPrmsObj.getId());
                    }
                }
            }
        }

        for (String[] row : opTreeData) {
            if (row.length > evidence_lv0Col) {
                // find if evidence object exists
                Integer evdncIndx =
                        OPTreeUtils.getEvidencePosition(evidenceObjects, row[evidence_lv0Col]);
                if (evdncIndx != null) {
                    // add things to existing evidence object
                    if (row.length > detail_lv0Col) {
                        evidenceObjects.get(evdncIndx).getDetail().add(row[detail_lv0Col]);
                    }
                    if (row.length > Supportive_lv0Col) {
                        evidenceObjects
                                .get(evdncIndx)
                                .setIsSupportive(Boolean.parseBoolean(row[Supportive_lv0Col]));
                        ;
                    }
                    if (row.length > EvidenceActivity_lv0Col) {
                        evidenceObjects.get(evdncIndx).setConfirmedBy(row[EvidenceActivity_lv0Col]);
                        ;
                        ;
                    }
                    System.out.println("Added data to " + evidenceObjects.get(evdncIndx).getId());
                } else {
                    // create new premise object
                    OPTree.Evidence newEvdncObj = new OPTree().new Evidence();
                    if (!(row[evidence_lv0Col].length() == 0)) {
                        // add things to new object
                        newEvdncObj.setId(row[evidence_lv0Col]);
                        if (row.length > detail_lv0Col) {
                            newEvdncObj.getDetail().add(row[detail_lv0Col]);
                        }
                        if (row.length > Supportive_lv0Col) {
                            newEvdncObj.setIsSupportive(
                                    Boolean.parseBoolean(row[Supportive_lv0Col]));
                            ;
                        }
                        if (row.length > EvidenceActivity_lv0Col) {
                            newEvdncObj.setConfirmedBy(row[EvidenceActivity_lv0Col]);
                            ;
                            ;
                        }
                        // add object to list
                        evidenceObjects.add(newEvdncObj);
                        System.out.println("Created object for " + newEvdncObj.getId());
                    }
                }
            }
        }
        for (String[] row : opTreeData) {
            if (row.length > evidence_lv1Col) {
                // find if evidence object exists
                Integer evdncIndx =
                        OPTreeUtils.getEvidencePosition(evidenceObjects, row[evidence_lv1Col]);
                if (evdncIndx != null) {
                    // add things to existing evidence object
                    if (row.length > detail_lv1Col) {
                        evidenceObjects.get(evdncIndx).getDetail().add(row[detail_lv1Col]);
                    }
                    if (row.length > Supportive_lv1Col) {
                        evidenceObjects
                                .get(evdncIndx)
                                .setIsSupportive(Boolean.parseBoolean(row[Supportive_lv1Col]));
                        ;
                    }
                    if (row.length > EvidenceActivity_lv1Col) {
                        evidenceObjects.get(evdncIndx).setConfirmedBy(row[EvidenceActivity_lv1Col]);
                        ;
                        ;
                    }
                    System.out.println("Added data to " + evidenceObjects.get(evdncIndx).getId());
                } else {
                    // create new premise object
                    OPTree.Evidence newEvdncObj = new OPTree().new Evidence();
                    if (!(row[evidence_lv1Col].length() == 0)) {
                        // add things to new object
                        newEvdncObj.setId(row[evidence_lv1Col]);
                        if (row.length > detail_lv1Col) {
                            newEvdncObj.getDetail().add(row[detail_lv1Col]);
                        }
                        if (row.length > Supportive_lv1Col) {
                            newEvdncObj.setIsSupportive(
                                    Boolean.parseBoolean(row[Supportive_lv1Col]));
                            ;
                        }
                        if (row.length > EvidenceActivity_lv1Col) {
                            newEvdncObj.setConfirmedBy(row[EvidenceActivity_lv1Col]);
                            ;
                            ;
                        }
                        // add object to list
                        evidenceObjects.add(newEvdncObj);
                        System.out.println("Created object for " + newEvdncObj.getId());
                    }
                }
            }
        }
        for (String[] row : opTreeData) {
            if (row.length > evidence_lv2Col) {
                // find if evidence object exists
                Integer evdncIndx =
                        OPTreeUtils.getEvidencePosition(evidenceObjects, row[evidence_lv2Col]);
                if (evdncIndx != null) {
                    // add things to existing evidence object
                    if (row.length > detail_lv2Col) {
                        evidenceObjects.get(evdncIndx).getDetail().add(row[detail_lv2Col]);
                    }
                    if (row.length > Supportive_lv2Col) {
                        evidenceObjects
                                .get(evdncIndx)
                                .setIsSupportive(Boolean.parseBoolean(row[Supportive_lv2Col]));
                        ;
                    }
                    if (row.length > EvidenceActivity_lv2Col) {
                        evidenceObjects.get(evdncIndx).setConfirmedBy(row[EvidenceActivity_lv2Col]);
                        ;
                        ;
                    }
                    System.out.println("Added data to " + evidenceObjects.get(evdncIndx).getId());
                } else {
                    // create new premise object
                    OPTree.Evidence newEvdncObj = new OPTree().new Evidence();
                    if (!(row[evidence_lv2Col].length() == 0)) {
                        // add things to new object
                        newEvdncObj.setId(row[evidence_lv2Col]);
                        if (row.length > detail_lv2Col) {
                            newEvdncObj.getDetail().add(row[detail_lv2Col]);
                        }
                        if (row.length > Supportive_lv2Col) {
                            newEvdncObj.setIsSupportive(
                                    Boolean.parseBoolean(row[Supportive_lv2Col]));
                            ;
                        }
                        if (row.length > EvidenceActivity_lv2Col) {
                            newEvdncObj.setConfirmedBy(row[EvidenceActivity_lv2Col]);
                            ;
                            ;
                        }
                        // add object to list
                        evidenceObjects.add(newEvdncObj);
                        System.out.println("Created object for " + newEvdncObj.getId());
                    }
                }
            }
        }

        // -- Connect objects as applicable
        // -- (evidence <- premises, premises <- premises, and then premises <- arguments)
        System.out.println("******** Connections ******** ");

        // -- adding level 2 evidence to level 2 premise
        for (String[] row : opTreeData) {
            if (row.length > premise_lv2Col) {
                String prmsLv2Id = row[premise_lv2Col];
                // find if premise object exists
                Integer prmsLv2Indx = OPTreeUtils.getPremisePosition(premiseObjects, prmsLv2Id);
                if (prmsLv2Indx != null) {
                    // add things to existing premise object
                    // find level 2 evidence connect
                    if (row.length > evidence_lv2Col) {
                        String evdncLv2Id = row[evidence_lv2Col];
                        Integer evdncLv2Indx =
                                OPTreeUtils.getEvidencePosition(evidenceObjects, evdncLv2Id);
                        if (evdncLv2Indx != null) {
                            Integer evdncPresenceIndx =
                                    OPTreeUtils.getEvidencePosition(
                                            premiseObjects.get(prmsLv2Indx).getEvidence(),
                                            evdncLv2Id);
                            if (evdncPresenceIndx == null) { // if not already added
                                premiseObjects
                                        .get(prmsLv2Indx)
                                        .getEvidence()
                                        .add(evidenceObjects.get(evdncLv2Indx));
                                System.out.println("Added " + evdncLv2Id + " to " + prmsLv2Id);
                            }
                        }
                    }
                }
            }
        }

        // -- adding level 1 evidence and level 2 premise to level 1 premise
        for (String[] row : opTreeData) {
            if (row.length > premise_lv1Col) {
                String prmsLv1Id = row[premise_lv1Col];
                // find if premise object exists
                Integer prmsLv1Indx = OPTreeUtils.getPremisePosition(premiseObjects, prmsLv1Id);
                if (prmsLv1Indx != null) {
                    // add things to existing premise object
                    // find level 1 evidence connect
                    if (row.length > evidence_lv1Col) {
                        String evdncLv1Id = row[evidence_lv1Col];
                        Integer evdncLv1Indx =
                                OPTreeUtils.getEvidencePosition(evidenceObjects, evdncLv1Id);
                        if (evdncLv1Indx != null) {
                            Integer evdncPresenceIndx =
                                    OPTreeUtils.getEvidencePosition(
                                            premiseObjects.get(prmsLv1Indx).getEvidence(),
                                            evdncLv1Id);
                            if (evdncPresenceIndx == null) { // if not already added
                                premiseObjects
                                        .get(prmsLv1Indx)
                                        .getEvidence()
                                        .add(evidenceObjects.get(evdncLv1Indx));
                                System.out.println("Added " + evdncLv1Id + " to " + prmsLv1Id);
                            }
                        }
                    }
                    // find level 2 premise connect
                    if (row.length > premise_lv2Col) {
                        String prmsLv2Id = row[premise_lv2Col];
                        Integer prmsLv2Indx =
                                OPTreeUtils.getPremisePosition(premiseObjects, prmsLv2Id);
                        if (prmsLv2Indx != null) {
                            Integer prmsPresenceIndx =
                                    OPTreeUtils.getPremisePosition(
                                            premiseObjects.get(prmsLv1Indx).getSubPremises(),
                                            prmsLv2Id);
                            if (prmsPresenceIndx == null) { // if not already added
                                premiseObjects
                                        .get(prmsLv1Indx)
                                        .getSubPremises()
                                        .add(premiseObjects.get(prmsLv2Indx));
                                System.out.println("Added " + prmsLv2Id + " to " + prmsLv1Id);
                            }
                        }
                    }
                }
            }
        }

        // -- adding level 0 evidence and level 1 premise to level 0 premise
        for (String[] row : opTreeData) {
            if (row.length > premise_lv0Col) {
                String prmslv0Id = row[premise_lv0Col];
                // find if premise object exists
                Integer prmslv0Indx = OPTreeUtils.getPremisePosition(premiseObjects, prmslv0Id);
                if (prmslv0Indx != null) {
                    // add things to existing premise object
                    // find level 0 evidence connect
                    if (row.length > evidence_lv0Col) {
                        String evdnclv0Id = row[evidence_lv0Col];
                        Integer evdnclv0Indx =
                                OPTreeUtils.getEvidencePosition(evidenceObjects, evdnclv0Id);
                        if (evdnclv0Indx != null) {
                            Integer evdncPresenceIndx =
                                    OPTreeUtils.getEvidencePosition(
                                            premiseObjects.get(prmslv0Indx).getEvidence(),
                                            evdnclv0Id);
                            if (evdncPresenceIndx == null) { // if not already added
                                premiseObjects
                                        .get(prmslv0Indx)
                                        .getEvidence()
                                        .add(evidenceObjects.get(evdnclv0Indx));
                                System.out.println("Added " + evdnclv0Id + " to " + prmslv0Id);
                            }
                        }
                    }
                    // find level 1 premise connect
                    if (row.length > premise_lv1Col) {
                        String prmslv1Id = row[premise_lv1Col];
                        Integer prmslv1Indx =
                                OPTreeUtils.getPremisePosition(premiseObjects, prmslv1Id);
                        if (prmslv1Indx != null) {
                            Integer prmsPresenceIndx =
                                    OPTreeUtils.getPremisePosition(
                                            premiseObjects.get(prmslv0Indx).getSubPremises(),
                                            prmslv1Id);
                            if (prmsPresenceIndx == null) { // if not already added
                                premiseObjects
                                        .get(prmslv0Indx)
                                        .getSubPremises()
                                        .add(premiseObjects.get(prmslv1Indx));
                                System.out.println("Added " + prmslv1Id + " to " + prmslv0Id);
                            }
                        }
                    }
                }
            }
        }

        // -- adding level 0 premise to arguments
        for (String[] row : opTreeData) {
            if (row.length > op_argumentCol) {
                String argId = row[op_argumentCol];
                // find if argument object exists
                Integer argIndx = OPTreeUtils.getArgumentPosition(argumentObjects, argId);
                if (argIndx != null) {
                    // add things to existing argument object
                    // find level 0 premise connect
                    if (row.length > premise_lv0Col) {
                        String prmslv0Id = row[premise_lv0Col];
                        Integer prmslv0Indx =
                                OPTreeUtils.getPremisePosition(premiseObjects, prmslv0Id);
                        if (prmslv0Indx != null) {
                            Integer prmsPresenceIndx =
                                    OPTreeUtils.getPremisePosition(
                                            argumentObjects.get(argIndx).getSubPremises(),
                                            prmslv0Id);
                            if (prmsPresenceIndx == null) { // if not already added
                                argumentObjects
                                        .get(argIndx)
                                        .getSubPremises()
                                        .add(premiseObjects.get(prmslv0Indx));
                                System.out.println("Added " + prmslv0Id + " to " + argId);
                            }
                        }
                    }
                }
            }
        }

        // -- adding arguments to OPTree
        opTreeObj.setArguments(argumentObjects);

        // -- Initialize other parameters of OPTree (hardcoded for now)
        opTreeObj.setId("OP");
        opTreeObj.setStatement("The SoH Subcomponent satisfies OPs");

        // -- for testing
        for (OPTree.Premise p : premiseObjects) {
            System.out.println(
                    "Premise "
                            + p.getId()
                            + " has evidence | subpremises : "
                            + p.getEvidence().size()
                            + " | "
                            + p.getSubPremises().size());
        }
    }
}
