import os
import sys
import errno
import shutil
import random
import numpy as np



#--------------- Global variables
serialNumberCounter = 0

#--------------- Storage variables
sysReq = [] #[id,description]
itemReq = [] #[id,description]
item = [] #[id,description]
interface = [] #[id,description] 


#--------------- Functions
#outputs a new unique serial number and updates counter
def createSerial():
    global serialNumberCounter 
    serialNumberCounter += 1
    return str(serialNumberCounter)

#outputs a 0 or 1 randomly
def choose():
    return random.choice([0,1])    

#-- Create Requirements, Items, and interfaces and print
sadl_file = open("oem-arp-synthetic-data.sadl", "w")
sadl_file.write('uri "http://sadl.org/oem-arp-synthetic-data.sadl" alias oemarpsd. \n')
sadl_file.write('import "http://arcos.rack/OEM". \n')
sadl_file.write('import "http://sadl.org/oem_arp4754_dap.sadl". \n')
sadl_file.write('//-- process 1 data \n')
sadl_file.write('// system design description \n')
sadl_file.write('OEMDesignDescription is a SystemDesignDescription \n')
sadl_file.write('   with identifier "OEMDesignDescription" \n')
sadl_file.write('   with entityURL "https://github.com/ge-high-assurance/RITE". \n')
sadl_file.write('//-- Documents for Objective 1-1 \n')
sadl_file.write('CertificationPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "CertificationPlan". \n')
sadl_file.write('SafetyProgramPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "SafetyProgramPlan". \n')
sadl_file.write('DevelopmentPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "DevelopmentPlan". \n')
sadl_file.write('ValidationPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "ValidationPlan". \n')
sadl_file.write('VerificationPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "VerificationPlan". \n')
sadl_file.write('ConfigurationManagementPlan is a DOCUMENT \n')
sadl_file.write('   with identifier "ConfigurationManagementPlan". \n')
sadl_file.write('ProcessAssurancePlan is a DOCUMENT \n')
sadl_file.write('   with identifier "ProcessAssurancePlan". \n')
sadl_file.write("\n\n\n\n//-- AUTO GENERATED DATA BELOW --\n")

for i in range (1, 40):
    id = "item"+createSerial()
    desc="redacted description"
    item.append(id)
    sadl_file.write(id + ' is an Item\n    with identifier \"'+ id + '\"\n    with description \"redacted description\".\n')

for i in range (1, 60):
    id = "interface"+createSerial()
    desc="redacted description"
    interface.append(id)
    sadl_file.write(id + ' is an INTERFACE\n    with identifier \"'+ id + '\"\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write("OEMEngine hasInterface " + id + ".\n")


for i in range (1, 123):
    id = "SysReq"+createSerial()
    desc="redacted description"
    sysReq.append(id)
    sadl_file.write(id + ' is a SystemRequirement\n    with identifier \"'+ id + '\"\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write(id + " governs OEMEngine.\n")

for i in range (1, 1543):
    id = "itemReq"+createSerial()
    desc="redacted description"
    itemReq.append(id)
    sadl_file.write(id + ' is an ItemRequirement\n    with identifier \"'+ id + '\"\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write(id + " Rq:satisfies " + random.choice(sysReq) +".\n")
    if(choose() == 1):
        sadl_file.write(id + " governs " + random.choice(item) +".\n")

#-- pretty print connections

#-- Connect interfaces to systems (while printing)

#-- Connect System requirements to systems (while printing)

#-- Connect item requirements to items and system requirements (while printing)


