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
    sadl_file.write(id + ' is an Item\n    with description \"redacted description\".\n')

for i in range (1, 60):
    id = "interface"+createSerial()
    desc="redacted description"
    interface.append(id)
    sadl_file.write(id + ' is an INTERFACE\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write("OEMEngine hasInterface " + id + ".\n")


for i in range (1, 123):
    id = "SysReq"+createSerial()
    desc="redacted description"
    sysReq.append(id)
    sadl_file.write(id + ' is a SystemRequirement\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write(id + " governs OEMEngine.\n")

for i in range (1, 1543):
    id = "itemReq"+createSerial()
    desc="redacted description"
    itemReq.append(id)
    sadl_file.write(id + ' is an ItemRequirement\n    with description \"redacted description\".\n')
    if(choose() == 1):
        sadl_file.write(id + " Rq:satisfies " + random.choice(sysReq) +".\n")
    if(choose() == 1):
        sadl_file.write(id + " governs " + random.choice(item) +".\n")

#-- pretty print connections

#-- Connect interfaces to systems (while printing)

#-- Connect System requirements to systems (while printing)

#-- Connect item requirements to items and system requirements (while printing)



# #create a hypothetical value for a featuremeasurement
# def createFMVal(fmId,key):
#     if (key == 'w'):
#         base = 42
#         new = base + np.random.choice([1,-1])*(random.uniform(1,2)/1000)
#         return (new,'g')
#     if (key == 't'):    
#         base = 56.3
#         new = base + np.random.choice([1,-1])*(random.uniform(1,2500)/1000)
#         return (new,'mil')
#     if (key == 'p'):    
#         if("NA" in fmId): #normal airline
#             # base = 0.0
#             # new = base + (random.uniform(1,5)/100)
#             # return (new,'prob')
#             return (random.choice([0.0,0.0,0.0,0.0,0.0,0.0,0.05,0.01,0.02]), 'prob')
#         else: # harsh airline
#             base = 0.0
#             new = base + (random.uniform(0.1,9)/10)
#             return (new,'prob')

#     if (key == 'h'):    
#         # return np.random.randint(80, 400)                
#         if("NA" in fmId): #normal airline
#             return (random.choice([132,132,132,131,130,133,134,132]))
#         else: # harsh airline
#             return (random.choice([71,71,71,70,69,72,73,71]))


# #--------------- Synthesizing data

# # creating aircraft, aircrat associations, and engines for Normal Airline
# for i in range (1, 20):
#     id = "aircraftNA" + str(i)
#     tail = "N" + str(100 + i) + "NA" 
#     aircraftNA.append([id,tail,"jetCat_aircraftDesign"])    
#     startDate = "01-" + str(i+9) + "-2001"
#     endDate = "01-" + str(i+9) + "-2022"    
#     aircraftAssociationNA.append(["NormalAirline",id,startDate,endDate])
#     engine = "engine"+ id 
#     installedDate = "05-" + str(i+9) + "-2001"
#     serialNumber = createSerial()
#     engines.append([engine,"jetCat Core",id,installedDate,str(serialNumber),getRandomManu()])
#     #create one turbine per engine
#     turbine = "turbine"+ engine
#     installedDateTurbine = "03-" + str(i+9) + "-2001"
#     serialNumberTurbine = createSerial()
#     turbines.append([turbine,"Turbine Disk",engine,installedDateTurbine,str(serialNumberTurbine),])    



# #saving turbineMeasurements
# tm_file = open("ingest_TurbineMeasurements.csv", "w")
# tm_file.write("identifier,val,unit_identifier" + '\n')
# for x in turbineMeasurements:
#     tm_file.write(x[0]+ ',' + x[1] + '\n')
# tm_file.close()