clear all
clc
tstart = tic;
global XML_FOLDER; % Uppaal xml file desired location
global VTA_FOLDER; % Verifyta location on your device
global XMLFILENAME; % Name of the file with the full path


JSON_FOLDER = '/Users/enzo/Desktop/Tesi/ABB-protections/Matlab_code_v5.0/jsonNet';
XML_FOLDER = '/Users/enzo/Desktop/Tesi/ABB-protections/Matlab_code_v5.0/generated';
VTA_FOLDER = '/Users/enzo/Desktop/Tesi/apv/UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal/bin/verifyta';

jsonFiles = dir(fullfile(JSON_FOLDER, '*.json'));

for i = 1:length(jsonFiles)
    fileName = jsonFiles(i).name;
    fullPath = fullfile(JSON_FOLDER, fileName);
    [~, nameOnly, ~] = fileparts(fileName);
    
    fprintf('Processing %s...', fileName);

    XMLFILENAME = fullfile(XML_FOLDER, [nameOnly, '.xml']);

    tstart_CodeGen = tic; 

    [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fullPath);

    GenUppaalCode1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections);
    tCodeGen = toc(tstart_CodeGen);

    fprintf('(%.2f seconds)\n', tCodeGen);
end