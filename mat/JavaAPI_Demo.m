clearvars;
clc;
close all;

%% IMPORTANT!
% Instantiating the UppaalJavaAPI object into a function ensures that the
% UppaalJavaAPI.delete() function gets called whenever the object goes out
% of scope, shuting down correctly the UPPAAL engines at any interruption. 

runDemo();


function runDemo()
    
    % Relative Paths
    
    ABB_RELATIVE_PATH = 'libs/ABB-ProtectionsChecker-1.1.jar';
    JSON_RELATIVE_PATH = 'Example_20bus_Network_1.json';
    OUTPUT_RELATIVE_PATH = 'JavaTest';
    
    % Convert all relative paths into absolute paths that can be used in
    % UppaalJavaAPI methods
    
    ABB_ABSOLUTE_PATH = fullfile(pwd, ABB_RELATIVE_PATH);
    JSON_ABSOLUTE_PATH = fullfile(pwd, JSON_RELATIVE_PATH);
    OUTPUT_ABSOLUTE_PATH = fullfile(pwd, OUTPUT_RELATIVE_PATH);
    
    UPPAAL_ABSOLUTE_PATH = '/Applications/UPPAAL-5.1.0-beta5.app';
    
    % Instantiate a UppaalJavaAPI object providing: 
    % - Absolute path of the UPPAAL app
    % - Absolute path of the ABB-ProtectionsChecker.jar library
    % In case of a jar update run "clear java" command to refresh the Java
    % classpath
    % 
    % Running this command inside a function ensures correct shutdown of UPPAAL engines in 
    % case of unexpected interruption in the normal execution flow

    %% WARNING!!
    % The import of the uppaal.jar in the classpath via UppaalJavaAPI() 
    % breaks (until a Matlab reboot) the standard Matlab function 'pwd' used to
    % get the current working directory, since uppaal.jar contains a cd.class
    % that goes in conflict with the standard cd function used in pwd.
    
    % To bypass that I'm overriding the behaviour of the standard pwd by
    % recreating a custom pwd.m file at runtime that stores the working
    % directory at first run and reuses that value after the break of the
    % standard pwd
    
    JavaAPI = UppaalJavaAPI(UPPAAL_ABSOLUTE_PATH, ABB_ABSOLUTE_PATH);

    % Start 6 Uppaal Engines ("server" process in task manager)
    JavaAPI.startEngines(6);
    
    % If the JavaAPI object is not instantiated inside a function a try
    % catch should be used after startEngines() to ensure the shutdown of
    % the engines via shutdownEngines() in the catch cause
    try

        %Load the Network from json file. The method takes in input the full absoute
        %path of the json file.
        JavaAPI.loadNetwork(JSON_ABSOLUTE_PATH);
        
        % Create output folder if not present
        if ~exist(OUTPUT_ABSOLUTE_PATH, 'dir')
            mkdir(OUTPUT_ABSOLUTE_PATH);
        end

        % Generate and save UPPAAL XML model test.xml into the folder specified at OUPUT_ABSOLUTE_PATH
        JavaAPI.generateUppaalModel(OUTPUT_ABSOLUTE_PATH, "test");
    
        % Generate and save multiple UPPAAL XML models test_split_i.xml splitted in 8 maximum
        % faults per model into the folder specified at OUPUT_ABSOLUTE_PATH
        JavaAPI.generateSplitUppaalModels(OUTPUT_ABSOLUTE_PATH, "test_split", 8);
    
        % Get lines oriented path from Bus 8 to Bus 9
        path = JavaAPI.getNetworkPath(8,9);
        fprintf('--- Line id path from Bus 8 to Bus 9 ---\n\n\t[%s]\n\n', strjoin(string(path), ', '));
        
        % Default Query Generation
        fprintf('--- Default Query Generation ---\n\n\t%s\n\n', JavaAPI.generateDefaultQuery());
    
        % Default Query Verification
        fprintf('--- Default Query Verification ---\n\n');
        result = JavaAPI.verifyDefaultQuery(true);
        printVerificationResult(result);
        
        % Filtered Query Generation
        excludedCBs = [3, 5, 8];
        filteredQuery = JavaAPI.generateFilteredQuery(excludedCBs);
        fprintf('--- Filtered Query Generation (excluded CBs [%s]) ---\n\n\t%s\n\n', num2str(excludedCBs), filteredQuery);
    
        % Custom Query Verification without trace
        customQuery = "E<>(!CB3.Open && CB5.Open && F3)";
        fprintf('--- Custom Query Verification without trace %s ---\n\n', customQuery);
        result = JavaAPI.verifyQuery(customQuery, false);
        printVerificationResult(result);
        
        % Misconfigured CBs estraction via ASQ logic:
        misconfiguredCBs = JavaAPI.extractMisconfiguredCBs(8);
        fprintf('--- Initial Misconfigured CBs ---\n\n\t[%s]\n\n', strjoin(string(misconfiguredCBs), ', '));
        
        % Updating t2 of CB_23 from 6 to 1
        fprintf('--- Updating t2 of CB_23 from 6 to 1 ---\n\n');
        JavaAPI.updateCBConfig(23, 1);
    
        % Misconfigured CBs estraction after config update:
        misconfiguredCBs = JavaAPI.extractMisconfiguredCBs();
        fprintf('--- Misconfigured CBs after config update ---\n\n\t[%s]\n\n', strjoin(string(misconfiguredCBs), ', '));
        
        % Normal Shutdown of Uppaal Engines
        JavaAPI.shutdownEngines();
    catch ME
        %Shutdown Uppaal Processes in case of unexpected error
        JavaAPI.shutdownEngines();
        
        % Relaunch the exception to print the error stacktrace
        rethrow(ME); 
    end
end

% Help function to print a VerificationResult.
function printVerificationResult(verificationResult)
    fprintf('\tProperty Satisfied: %s\n', mat2str(verificationResult.PropertySatisfied));
    fprintf('\tFault Triggered: %s\n', verificationResult.Fault);
    fprintf('\tMisconfigured CBs: [%s]\n', strjoin(string(verificationResult.MisconfiguredCBIds), ', '));
    fprintf('\tTrace: \n%s\n', verificationResult.Trace);
end
