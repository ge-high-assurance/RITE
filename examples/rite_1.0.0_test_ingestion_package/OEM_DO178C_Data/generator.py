import os
import sys
import errno
import shutil
import random

pids = [] #[id]
csid = [] #[id]
srs = [] #[id, satisfies]
subdd = [] #[id, satisfies]
sbvt_test = [] #[id, status, verifies]
swcomponent = []


for i in range (1, 20):
    id = "PIDS_REQ_" + str(i)
    print(id)
    pids.append(id)
    

for i in range (1, 30):
    id = "CSID_REQ_" + str(i)
    print(id)
    csid.append(id)
        
for i in range (1, 200):
    id = "SRS_REQ_" + str(i)
    if random.choice([True, False]): # has trace
        if random.choice([True, False]): # PIDS trace
            trace = random.choice(pids)
            srs.append([id,trace])
            print([id,trace])
        else: # CSID trace
            trace = random.choice(csid)
            srs.append([id,trace])
            print([id,trace])    
    else: # no trace
        srs.append([id,"BLANK"])
        print([id,"BLANK"])    
    
for i in range (1, 1400):
    id = "SubDD_REQ_" + str(i)
    if random.choice([True, False]): # has trace
        trace = random.choice(srs)[0]
        subdd.append([id,trace])
        print([id,trace])
    else: # no trace
        subdd.append([id,"BLANK"])
        print([id,"BLANK"])    


for i in range (1, 800):
    id = "SBVT_TEST_" + str(i)
    if random.choice([True, False]): # SRS trace
        trace = random.choice(srs)[0]
        sbvt_test.append([id,'Passed',trace])
        print([id,'Passed',trace])
    else: # SubDD trace
        trace = random.choice(subdd)[0]
        sbvt_test.append([id,'Passed',trace])
        print([id,'Passed',trace])
    
    
for i in range (1, 1800):
    id = "swComponent_" + str(i)
    if random.choice([True, False]): # has trace
        trace = random.choice(subdd)[0]
        swcomponent.append([id,trace])
        print([id,trace])
    else: # no trace
        swcomponent.append([id,"BLANK"])
        print([id,"BLANK"])    


pids_file = open("PIDS.csv", "w")
pids_file.write("identifier" + '\n')
for x in pids:
    pids_file.write(x + '\n')
pids_file.close()


csid_file = open("CSID.csv", "w")
csid_file.write("identifier" + '\n')
for x in csid:
    csid_file.write(x + '\n')
csid_file.close()
    

srs_file = open("SRS.csv", "w")
srs_file.write("identifier,satisfies_identifier,description" + '\n')
for x in srs:
    if(x[1]!="BLANK"):
        srs_file.write(x[0]+','+x[1]+",Default_Description"+'\n')
    else:
        srs_file.write(x[0]+','+",Default_Description"+'\n')
srs_file.close()
    
    
subdd_file = open("SUBDD.csv", "w")
subdd_file.write("identifier,satisfies_identifier,description" + '\n')
for x in subdd:
    if(x[1]!="BLANK"):
        subdd_file.write(x[0]+','+x[1]+",Default_Description"+'\n')
    else:
        subdd_file.write(x[0]+','+",Default_Description"+'\n')
subdd_file.close()


sbvt_file = open("SBVT_Test.csv", "w")
sbvt_file.write("identifier,verifies_identifier" + '\n')
for x in sbvt_test:
    sbvt_file.write(x[0]+','+x[2]+'\n')
sbvt_file.close()    

sbvt_result_file = open("SBVT_Result.csv", "w")
sbvt_result_file.write("identifier,confirms_identifier,result_identifier" + '\n')
for x in sbvt_test:
    sbvt_result_file.write(x[0]+"_res"+','+ x[0]+','+x[1]+'\n')
sbvt_result_file.close()    



swComponent_file = open("SWCOMPONENT.csv", "w")
swComponent_file.write("identifier,wasImpactedBy_identifier" + '\n')
for x in swcomponent:
    if(x[1]!="BLANK"):
        swComponent_file.write(x[0]+','+x[1]+'\n')
    else:
        swComponent_file.write(x[0]+','+'\n')
swComponent_file.close()    



document_file = open("DOCUMENT.csv", "w")
document_file.write("identifier,content_identifier,title,dataInsertedBy_identifier" + '\n')
for x in srs:
    document_file.write("Default_Source_Document"+','+x[0]+",Default_SrcDoc,Sample_Activity"+'\n')
for x in subdd:
    document_file.write("Default_Source_Document"+','+x[0]+",Default_SrcDoc,Sample_Activity"+'\n')
document_file.close()    