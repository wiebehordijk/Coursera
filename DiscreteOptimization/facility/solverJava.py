#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
from subprocess import Popen, PIPE

def solve_it(input_data):

    # Writes the inputData to a temporay file

    tmp_file_name = 'tmp.data'
    tmp_file = open(tmp_file_name, 'w')
    tmp_file.write(input_data)
    tmp_file.close()

    # Runs the command: java Solver -file=tmp.data

    process = Popen(['java', '--add-opens', 'java.base/java.lang=ALL-UNNAMED', '-cp',
                     r'out\production\facility;D:\Code\Coursera\Algorithms\algs4.jar;D:\Code\ChocoSolver\choco-4.10.1\choco-solver-4.10.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\activation-1.1.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\animal-sniffer-annotations-1.14.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\antlr-runtime-3.5.2.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\checker-compat-qual-2.0.0.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\commons-codec-1.10.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\commons-io-2.6.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\commons-lang3-3.8.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\commons-math3-3.4.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\drools-compiler-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\drools-core-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\ecj-4.6.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\error_prone_annotations-2.1.3.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\freemarker-2.3.26.jbossorg-1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\guava-25.0-jre.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\j2objc-annotations-1.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jackson-annotations-2.9.8.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jackson-core-2.9.8.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jackson-databind-2.9.8.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\javassist-3.22.0-GA.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\javax.persistence-api-2.2.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jaxb-core-2.3.0.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jaxb-impl-2.3.0.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jboss-jaxb-api_2.3_spec-1.0.1.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jcommon-1.0.23.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jfreechart-1.0.19.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\jsr305-1.3.9.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-api-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-internal-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-soup-commons-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-soup-maven-support-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-soup-project-datamodel-api-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\kie-soup-project-datamodel-commons-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\mvel2-2.4.4.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-benchmark-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-core-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-persistence-common-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-persistence-jackson-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-persistence-jaxb-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-persistence-jpa-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-persistence-xstream-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\optaplanner-test-7.23.0.Final.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\protobuf-java-3.6.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\reflections-0.9.11.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\slf4j-api-1.7.26.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\xmlpull-1.1.3.1.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\xpp3_min-1.1.4c.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\xstream-1.4.10.jar;D:\Code\OptaPlanner\optaplanner-distribution-7.23.0.Final\binaries\slf4j-simple-1.7.26.jar',
                     'FacilitySolver', 'tmp.data'], stdout=PIPE)
    (stdout, stderr) = process.communicate()

    # removes the temporay file
    os.remove(tmp_file_name)

    strippedSolution = stdout.decode('utf8').strip()
    
    cleanSolution = ''
    for line in strippedSolution.splitlines():
        if line[0].isdigit():
            cleanSolution += line + "\n"

    return cleanSolution


import sys

if __name__ == '__main__':
    if len(sys.argv) > 1:
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_data = input_data_file.read()
        print( solve_it(input_data) )
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/gc_4_1)')

