clearvars;
clc;
close all;

run();

function run()
    
    ABB_RELATIVE_PATH = 'libs/ABB-ProtectionsChecker-1.1.jar';
   
    ABB_ABSOLUTE_PATH = fullfile(pwd, ABB_RELATIVE_PATH);
    UPPAAL_ABSOLUTE_PATH = '/Applications/UPPAAL-5.1.0-beta5.app';

    tstart = tic;

    JSON_FOLDER = fullfile(pwd, 'jsonNet');

    JavaAPI = UppaalJavaAPI(UPPAAL_ABSOLUTE_PATH, ABB_ABSOLUTE_PATH);
    JavaAPI.startEngines(9);
    
    jsonFiles = dir(fullfile(JSON_FOLDER, '*.json'));
    
    for i = 1:length(jsonFiles)
        fileName = jsonFiles(i).name;
        fullPath = fullfile(JSON_FOLDER, fileName);

        fprintf('Processing %s...\n', fileName);
    
        tstart_CodeGen = tic; 
        JavaAPI.loadNetwork(fullPath);

         % Misconfigured CBs estraction via ASQ logic:
        misconfiguredCBs = JavaAPI.extractMisconfiguredCBs(7);
        fprintf('\tMisconfigured CBs: [%s] ', strjoin(string(misconfiguredCBs), ', '));
    
        tCodeGen = toc(tstart_CodeGen);
    
        fprintf('(%.2f seconds)\n\n', tCodeGen);
    end

    JavaAPI.shutdownEngines();

end