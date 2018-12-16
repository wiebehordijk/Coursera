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

    # Runs the command: scala Solver -file=tmp.data

    process = Popen(['java', '-Xmx256M', '-Xms32M', 
                     '-Dscala.usejavacp=true', '-cp',
                     r'\"C:\Program Files (x86)\scala\lib\scala-actors-migration_2.11-1.1.0.jar;C:\Program Files (x86)\scala\lib\scala-compiler.jar;C:\Program Files (x86)\scala\lib\scala-continuations-library_2.11-1.0.2.jar;C:\Program Files (x86)\scala\lib\scala-continuations-plugin_2.11.8-1.0.2.jar;C:\Program Files (x86)\scala\lib\scala-library.jar;C:\Program Files (x86)\scala\lib\scala-parser-combinators_2.11-1.0.4.jar;C:\Program Files (x86)\scala\lib\scala-reflect.jar;C:\Program Files (x86)\scala\lib\scalap-2.11.8.jar\"',
                     'scala.tools.nsc.MainGenericRunner',
                     '-cp', 'out/production/knapsack', 'Solver', '-file=' + tmp_file_name], stdout=PIPE)
    (stdout, stderr) = process.communicate()

    # removes the temporay file
    os.remove(tmp_file_name)

    return stdout.decode('utf8').strip()


import sys

if __name__ == '__main__':
    if len(sys.argv) > 1:
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_data = input_data_file.read()
        print (solve_it(input_data))
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/ks_4_0)')

