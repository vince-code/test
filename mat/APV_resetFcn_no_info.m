function [InitialObs,Info] = APV_resetFcn_no_info()
%% ResetFcn
% Environment reset function, specified as a function name, function handle, or handle to an anonymous function.
% The sim function calls your reset function to reset the environment at the start of each simulation, and the train
% function calls it at the start of each training episode.
% The reset function that you provide must have no inputs and two outputs, as illustrated by the following signature.
% [InitialObservation,Info] = myResetFunction
% The reset function sets the environment to an initial state and computes the initial value of the observation.
% For example, you can create a reset function that randomizes certain state values, such that each training episode
% begins from different initial conditions. The InitialObservation output must match the dimensions and data type of
% observationInfo.
% The Info output of ResetFcn initializes the Info property of your environment and contains any data that you want
% to pass from one step to the next. This can be the environment state or a structure containing state and parameters.
% The simulation or training function (train or sim) supplies the current value of Info as the second input argument
% of StepFcn, then uses the fourth output argument returned by StepFcn to update the value of Info.
% To use additional input arguments beyond the allowed two, define your argument in the MATLAB workspace, then
% specify stepFcn as an anonymous function that in turn calls your custom function with the additional arguments
% defined in the workspace, as shown in the example Create Custom Environment Using Step and Reset Functions.
global XML_FOLDER; % Uppaal xml file desired location
global VTA_FOLDER; % Verifyta location on your device
global XMLFILENAME; % Name of the file with the full path
rng(2024); % Set the seed to get the same random numbers for reproducability
%% Randomly select a network json file
% First add all your training network names to the cell array of names
% called Training_file_names
% Training_file_names = {'Example_7bus_Network_1.json','Example_7bus_Network_2.json',...
%                        'Example_7bus_Network_3.json','Example_7bus_Network_4.json',...
%                        'Example_7bus_Network_6.json'};
% Training_file_names = {'Example_7bus_Network_1.json','Example_7bus_Network_2.json',...
%                        'Example_7bus_Network_3.json','Example_7bus_Network_4.json',...
%                        'Example_7bus_Network_5.json','Example_7bus_Network_6.json'};
Training_file_names = {'Example_10bus.json'};

[~,Num_training_files] = size(Training_file_names); % Find the number of files to train on
file_name_index = randi([1 Num_training_files]); % Randomly choose a file among them

fileName = Training_file_names{file_name_index}; 
% Read the data
[A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);
% Create the Info struct
Info = struct('A',A,'Line',Line,'Bus',Bus,'F_branch',F_branch,'F_bus',F_bus,'Irc',Irc,'C',C,'Iioc_values',Iioc_values,'Ith_values',Ith_values,'Isc_values_Lines',Isc_values_Lines,'Isc_values_Buses',Isc_values_Buses,'CB_settings',CB_settings,'Bus_connections',Bus_connections);
% CB settings
NumAgents = length(Info.CB_settings.CB); % Total number of CBs
CB_setts = [];% vector of size (k*2) where k is the number of CBs, colomn 1 has the CB IDs and colomn 2 has he short time delay setting (t2)
for k = 1:NumAgents
    CB_setts(k,1) = Info.CB_settings.CB{1,k}.CB_ID; % extracting the CB ID to colomn 1
    CB_setts(k,2) = Info.CB_settings.CB{1,k}.t2; % extracting the short time delay setting (t2) to colomn 2
    CB_setts(k,3) = 1; % assume that all CBs are correctly configured until otherwise discovered
end
Info.CB_setts = CB_setts;
 
%% Randomly change the CB settings
for k = 1:NumAgents
    Info.CB_settings.CB{1, k}.t2 = randi([1 10]);
end
%% Run the Verification to get the intial information
% CB_Info = []; List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)
WrongCBs = true;
%WrongCB_Pairs_IDs = []; % circuit breaker pairs with problems col1 is the first CB and col2 is the second CB
% xmlFilename = strcat(XML_FOLDER,filesep,'Uppaal_code_generation.xml');
fileID = fopen(XMLFILENAME,'w');
GenPreamble(fileID);
if isempty(Info.F_branch) || isempty(Info.F_bus)
    fprintf("Minimum number of faults not selected \n Please select at least 1 line fault and 1 bus fault \n")
else
    fprintf(fileID,"<nta>\n");
    % Generate the global declaration where the functions Isc, Update and Clear are defined
    [LinesCBs,F_branch] = GenDeclaration1(Info.A,Info.Line,Info.Bus,Info.F_branch,Info.F_bus,Info.Irc,Info.C,Info.Iioc_values,Info.Ith_values,Info.Isc_values_Lines,Info.Isc_values_Buses,fileID,Info.Bus_connections,Info.CB_settings);
    Info.F_branch = F_branch;
    % Generate all timed automata (FG and CBs)
    GenTemplate(fileID,Info.Line,Info.Bus,Info.F_branch,Info.F_bus,Info.Irc,Info.CB_settings);
    % Generate something related to Uppaal where each timed automata is given a short name to help later when using the queries
    GenTemplateInstantiations(fileID,Info.CB_settings);
    % Generate all necessary queries needed to check for selectivty
    GenQueries1(fileID,Info.Line,Info.Bus,Info.F_branch,Info.F_bus,Info.CB_settings,LinesCBs,Info.Bus_connections);
    fprintf(fileID,"</nta>\n");
    fclose(fileID);
    % Run the verification
    cmdVerifyTA = strcat(VTA_FOLDER," -t0 ",XML_FOLDER,filesep,"Uppaal_code_generation.xml > output.log 2> trace.txt");
    % XML_FOLDER = '/Users/ahmed/Desktop/Work/PhD/Github/ABB-protections/output';
    % cmdVerifyTA = strcat(filesep,'Applications',filesep,'UPPAAL-5.1.0-beta5.app',filesep,'Contents',filesep,'Resources',filesep,'uppaal',filesep,'bin',filesep,'verifyta'," -t0 ",...
    %     XML_FOLDER,filesep,"Uppaal_code_generation.xml > output.log 2> trace.txt");
    Counter = 0;
    SelectIdx = 0;
    while WrongCBs == true 
        system(cmdVerifyTA);
        uppaal_code = importdata(XMLFILENAME); % Import the Uppaal model as a string array
        %% Get the results (here is where we build the WrongCB_Pairs_IDs vector
        outputLogCell = importdata('output.log','\n');
        % if isstruct(outputLogCell)
        %     outputLogCell1 = outputLogCell.textdata;
        %     outputLog = strjoin(outputLogCell1);
        % else
        %     outputLog = strjoin(outputLogCell);
        % end
        outputLog = strjoin(outputLogCell);
        
        selectivityIdx = regexp(outputLog,'Formula is satisfied','once'); % if the expression appears once then there is a misconfiguration
        if isempty(selectivityIdx)
            WrongCBs = false;
            if Counter == 1
                SelectIdx = 1;
            end
            Counter = Counter + 1;
        else
            WrongCB_Expressions = regexp(outputLog,'CB[\d*]+.Closed->CB[\d*]+.Standby','match');
            WrongCB_ID_Exp = regexp(WrongCB_Expressions,'[1234567890]+','match');
            AllStandByCBs = [];
            for i = 1:length(WrongCB_ID_Exp)
                AllStandByCBs = [AllStandByCBs,',',WrongCB_ID_Exp{1,i}{1,1}];
            end
            AllStandByCBs = str2num(AllStandByCBs);
            WrongCB_pairs = nchoosek(AllStandByCBs, 2);
            %WrongCB_Pairs_IDs = [WrongCB_Pairs_IDs;WrongCB_pairs];
            for ii = 1:length(AllStandByCBs)
                CB_setts_idx = find(Info.CB_setts(:,1)==AllStandByCBs(ii));
                Info.CB_setts(CB_setts_idx,3) = 0; % set the CB to misconfigured option
            end
            %% Remove the query from the file to run it again
            % Find the location of the fault and remove its query
            LineFaultLoc = regexp(outputLog,'F[\d*]+ := true','match');
            if ~isempty(LineFaultLoc)
                LineFaultLocID = regexp(LineFaultLoc,'[1234567890]+','match');
                FaultedLineID = str2double(LineFaultLocID{1,1}{1,1});
                [forCounter,~] = size(WrongCB_pairs); 
                for i = 1:forCounter
                    faultedCB_ID = WrongCB_pairs(i,1);
                    UpstreamCB_ID = WrongCB_pairs(i,2);
                    remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)||',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                    regexploc = regexpcell(uppaal_code,remove_exp_loc);
                    if isempty(regexploc)
                        remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)||',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i\\)\\|\\|',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                    else
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i\\)\\|\\|',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                    end
                    regexploc = regexpcell(uppaal_code,remove_exp_loc); % find the location of the expression you want to remove
                    if ~isempty(regexploc)
                        dummie = regexprep(uppaal_code{regexploc,1}, remove_exp, ''); % Replace the expression with nothing
                        uppaal_code{regexploc,1} = dummie; % Add the new expression to the string ar
                        dummie2 = regexprep(uppaal_code{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        uppaal_code{regexploc,1} = dummie2; % Add the new expression to the string array
                        dummie3 = regexprep(uppaal_code{regexploc,1}, '\(\)', ''); % Replace the expression with nothing
                        IslastOR = regexp(uppaal_code{regexploc,1},'\|\|\)');
                        if ~isempty(IslastOR)
                            All_Ors = regexp(uppaal_code{regexploc,1},'\|\');
                            LastOccurence = length(All_Ors);
                            dummie2 = regexprep(uppaal_code{regexploc,1}, '\|\|', '',LastOccurence); % Replace the expression with nothing
                            uppaal_code{regexploc,1} = dummie2; % Add the new expression to the string array
                        end
                        % dummie2 = regexprep(uppaal_code{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        % uppaal_code{regexploc,1} = dummie2; % Add the new expression to the string array
                    end
                end
            else
                BusFaultLoc = regexp(outputLog,'FB[\d*]+ := true','match');
                BusFaultLocID = regexp(BusFaultLoc,'[1234567890]+','match');
                FaultedBusID = str2double(BusFaultLocID{1,1}{1,1});
                [forCounter,~] = size(WrongCB_pairs);
                for i = 1:forCounter
                    faultedCB_ID = WrongCB_pairs(i,1);
                    UpstreamCB_ID = WrongCB_pairs(i,2);
                    remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)||',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                    regexploc = regexpcell(uppaal_code,remove_exp_loc);
                    if isempty(regexploc)
                        remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)||',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i\\)\\|\\|',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                    else
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i\\)\\|\\|',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                    end
                    regexploc = regexpcell(uppaal_code,remove_exp_loc); % find the location of the expression you want to remove
                    if ~isempty(regexploc)
                        dummie = regexprep(uppaal_code{regexploc,1}, remove_exp, ''); % Replace the expression with nothing
                        uppaal_code{regexploc,1} = dummie; % Add the new expression to the string array
                        dummie2 = regexprep(uppaal_code{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        uppaal_code{regexploc,1} = dummie2; % Add the new expression to the string array
                        dummie3 = regexprep(uppaal_code{regexploc,1}, '\(\)', ''); % Replace the expression with nothing
                        IslastOR = regexp(uppaal_code{regexploc,1},'\|\|\)');
                        if ~isempty(IslastOR)
                            All_Ors = regexp(uppaal_code{regexploc,1},'\|\');
                            LastOccurence = length(All_Ors);
                            dummie3 = regexprep(uppaal_code{regexploc,1}, '\|\|', '',LastOccurence); % Replace the expression with nothing
                            uppaal_code{regexploc,1} = dummie3; % Add the new expression to the string array
                        end
                        % dummie2 = regexprep(uppaal_code{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        % uppaal_code{regexploc,1} = dummie2; % Add the new expression to the string array
                        % dummie3 = regexprep(uppaal_code{regexploc,1}, '\(\)', ''); % Replace the expression with nothing
                        % uppaal_code{regexploc,1} = dummie3; % Add the new expression to the string array
                    end
                end
            end
            writecell(uppaal_code,XMLFILENAME,FileType='text',QuoteStrings='none'); % write the array back to the same file
            Counter = Counter + 1;
        end
    end
    
    InitialObs = cell(1,NumAgents);
    for k = 1:NumAgents
        InitialObs{1,k} = {Info.CB_setts(k,:),Info.A};
        % InitialObs{1,k} = {Info.CB_setts(k,:),SelectIdx};
    end
end

end