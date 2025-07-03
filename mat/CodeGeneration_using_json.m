clear all
clc
tstart = tic;
global XML_FOLDER; % Uppaal xml file desired location
global VTA_FOLDER; % Verifyta location on your device
global XMLFILENAME; % Name of the file with the full path

XML_FOLDER = '/Users/enzo/Desktop/Tesi/ABB-protections/output';
VTA_FOLDER = '/Users/enzo/Desktop/Tesi/apv/UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal/bin/verifyta';
XMLFILENAME = strcat(XML_FOLDER,filesep,'Uppaal_code_generation.xml');

%% For this section use "Reading_json_data" and "GenUppaalCode"

% fileName = 'Example_5_inputs2.json'; % <--
% fileName = 'Example_20bus.json'; % <--
% fileName = 'Example_50bus_part_1_18_faults.json'; % <--
% fileName = 'Example_40bus_28_faults.json'; % <--
% fileName = 'Example_Open_Ring_2_new_source_currents_CB_IDs.json'; % <--
% fileName = 'Example_Open_Ring_3_Parallel_lines.json';

%% For this section use "Reading_json_data1" and "GenUppaalCode1"

% fileName = 'Example_Open_Ring_3_added_sources_structure.json'; % need to add the selectivity type
% fileName = 'Example_Open_Ring_4.json'; % need to add the selectivity type
% fileName = 'Example_Open_Ring_4_parallel_lines.json'; % need to add the selectivity type
% fileName = 'Example_with_Motor_load_1.json'; % need to add the selectivity type

% fileName = 'Example_6bus_new_source_structure.json';
% fileName = 'Example_20bus_new_source_structure.json';
% fileName = 'Example_30bus.json'; % <--
% fileName = 'Example_40bus.json'; % <--
% fileName = 'Example_50bus.json'; % <--
% fileName = 'Example_50bus_part_1a_23_faults_new.json'; 
% fileName = 'Example_50bus_part_2a_23_faults_new.json'; 
% fileName = 'Example_50bus_part_3a_23_faults_new.json'; 

% fileName = 'Example_Dirc_Zone_6Bus.json';
% fileName = 'Example_Open_Ring_4_DZ.json';
% fileName = 'Example_Open_Ring_4_DZ_no_CB19.json';
% fileName = 'Example_Open_Ring_4_DZ_TC_no_CB19.json';
% fileName = 'outputJSON.json';

% fileName = 'Example_Closed_Ring_4_DZ_TC_no_CB19.json';
% fileName = 'Example_Closed_Ring_4_D_Z_DZ_TC_no_CB19.json';

% fileName = 'OR_6s_30l.json';

tstart_CodeGen = tic; 
%% Step 1: Read JSON file data
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections] = Reading_json_data(fileName);
[A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);

%% Step 2: Generate the Uppaal code base on the read network
% GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections);
GenUppaalCode1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections);
tCodeGen = toc(tstart_CodeGen);

%% Step 3: Verify the generated Uppal network and record the results
% xml_folder = '/Users/ahmed/Desktop/Work/PhD/Github/ABB-protections/output';
% cmdVerifyTA = strcat(filesep,'Applications',filesep,'UPPAAL-5.1.0-beta5.app',filesep,'Contents',filesep,'Resources',filesep,'uppaal',filesep,'bin',filesep,'verifyta'," -t0 ",...
%     xml_folder,filesep,"Uppaal_code_generation.xml > output.log 2> trace.txt");
cmdVerifyTA = strcat(VTA_FOLDER," -t0 ",XML_FOLDER,filesep,"Uppaal_code_generation.xml > output.log 2> trace.txt");
system(cmdVerifyTA)
%system('Users\ahmed\Desktop\UPPAAL-5.1.0-beta5.app\Contents\Resources\uppaal\bin\verifyta -t0 ..\output\output.log');
% This measures the time needed for both the generatino and verification
tfullcode = toc(tstart);
tverification = tfullcode - tCodeGen;
% [user,sys] = memory;

%% This section was made when a bigger network needed to be divided into many files each with a small number to faults to check
% tstart2 = tic;
% fileName2 = 'Example_50bus_part_2_17_faults.json'; % <--
% tstart_CodeGen2 = tic; 
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections] = Reading_json_data(fileName2);
% GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections);
% tCodeGen2 = toc(tstart_CodeGen2);
% system('D:\Work\Uppaal\uppaal64-4.1.26-1\uppaal64-4.1.26-1\bin-Windows\verifyta -t0 ..\..\Github\output\Uppaal_code_generation.xml > output2.log 2> trace2.txt');
% tfullcode2 = toc(tstart2);
% tverification2 = tfullcode2 - tCodeGen2;
% 
% tstart3 = tic;
% fileName3 = 'Example_50bus_part_3_17_faults.json'; % <--
% tstart_CodeGen3 = tic; 
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections] = Reading_json_data(fileName3);
% GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections);
% tCodeGen3 = toc(tstart_CodeGen3);
% system('D:\Work\Uppaal\uppaal64-4.1.26-1\uppaal64-4.1.26-1\bin-Windows\verifyta -t0 ..\..\Github\output\Uppaal_code_generation.xml > output3.log 2> trace3.txt');
% tfullcode3 = toc(tstart3);
% tverification3 = tfullcode3 - tCodeGen3;
% 
% tstart4 = tic;
% fileName4 = 'Example_50bus_part_4_17_faults.json'; % <--
% tstart_CodeGen4 = tic; 
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections] = Reading_json_data(fileName4);
% GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections);
% tCodeGen4 = toc(tstart_CodeGen4);
% system('D:\Work\Uppaal\uppaal64-4.1.26-1\uppaal64-4.1.26-1\bin-Windows\verifyta -t0 ..\..\Github\output\Uppaal_code_generation.xml > output4.log 2> trace4.txt');
% tfullcode4 = toc(tstart4);
% tverification4 = tfullcode4 - tCodeGen4;