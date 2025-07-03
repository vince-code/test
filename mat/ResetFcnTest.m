%% Initialie your folder locations
clear all
clc
global XML_FOLDER; % Uppaal xml file desired location
global VTA_FOLDER; % Verifyta location on your device
global XMLFILENAME; % Name of the file with the full path

XML_FOLDER = '/Users/enzo/Desktop/Tesi/splitTest';
VTA_FOLDER = '/Users/enzo/Desktop/Tesi/apv/UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal/bin/verifyta';
XMLFILENAME = strcat(XML_FOLDER,filesep,'Uppaal_code_generation.xml');

global SPLIT_FOLDER;
global SPLIT_XML_FOLDER;
global RESULT_FOLDER;

SPLIT_FOLDER = fullfile(pwd, 'splitReferences');
SPLIT_XML_FOLDER = fullfile(pwd, 'splitXML');
RESULT_FOLDER = fullfile(pwd, 'misconfiguredCBs');
if ~exist(SPLIT_FOLDER, 'dir')
    mkdir(SPLIT_FOLDER);
end

if ~exist(SPLIT_XML_FOLDER, 'dir')
    mkdir(SPLIT_XML_FOLDER);
end
if ~exist(RESULT_FOLDER, 'dir')
    mkdir(RESULT_FOLDER);
end

JSON_FOLDER = fullfile(pwd, 'jsonNet');
jsonFiles = dir(fullfile(JSON_FOLDER, '*.json'));

Info_cell = {};

for i = 1:length(jsonFiles)
    fileName = jsonFiles(i).name;
    fullPath = fullfile(JSON_FOLDER, fileName);
    fprintf('SelectivityCheck %-*s', 41,fileName);
 
    % Read the data
    [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);
    % Create the Info struct
    Info = struct('A',A,'Line',Line,'Bus',Bus,'F_branch',F_branch,'F_bus',F_bus,'Irc',Irc,'C',C,'Iioc_values',Iioc_values,'Ith_values',Ith_values,'Isc_values_Lines',Isc_values_Lines,'Isc_values_Buses',Isc_values_Buses,'CB_settings',CB_settings,'Bus_connections',Bus_connections);
    Info.jsonFileName = fileName;
    % CB settings
    NumAgents = length(Info.CB_settings.CB); % Total number of CBs
    CB_setts = [];% vector of size (k*2) where k is the number of CBs, colomn 1 has the CB IDs and colomn 2 has he short time delay setting (t2)
    for k = 1:NumAgents
        CB_setts(k,1) = Info.CB_settings.CB{1,k}.CB_ID; % extracting the CB ID to colomn 1
        CB_setts(k,2) = Info.CB_settings.CB{1,k}.t2; % extracting the short time delay setting (t2) to colomn 2
        CB_setts(k,3) = 1; % assume that all CBs are correctly configured until otherwise discovered
    end
    Info.CB_setts = CB_setts;
    Info.allqueries =[];% A Variable containting all the queries that needs to be checked.
    % It is the cell that starts with <formula> E blablabla
    % and needs to replace the cell containing the queries
    % in the uppaalcode variable everytime we change
    % the t2 settings
    Info_cell = {Info};

    % Call APV_resetFcnTest
    [~, ~] = APV_resetFcnTest(Info_cell);
end