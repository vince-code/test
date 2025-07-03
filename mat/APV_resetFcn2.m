function [InitialObs,Info] = APV_resetFcn2(Info_cell)
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
rng(2025); % Set the seed to get the same random numbers for reproducability
%% Randomly select a network json file
Random_index = randi([1 length(Info_cell)]); % Randomly choose a dataset
Info = Info_cell{Random_index,1};

%% Randomly change the CB settings
NumAgents = length(Info.CB_settings.CB);

%% Analysis of the network
Total_faults=length(Info.F_bus)+length(Info.F_branch);

if mod(Total_faults,8)==0
    div=floor(Total_faults/8);
else
    div=floor(Total_faults/8)+mod(Total_faults,8)/mod(Total_faults,8);
end

%% Creation of F_branch_matrix and F_bus_matrix
num=fix(length(Info.F_branch)/div);
rest=mod(length(Info.F_branch),div);
k=1;
for i=1:div
    for j=1:num+1
        if k>length(Info.F_branch)
            F_branch_matrix(i,j)=0;
        elseif i+rest<=div & j==num+1
            if j==1
                F_branch_matrix(i,j)=Info.F_branch(k);
            else
                F_branch_matrix(i,j)=0;
            end
        else
            F_branch_matrix(i,j)=Info.F_branch(k);
            k=k+1;
        end
    end
end
[F_branch_rows,F_branch_cols]=size(F_branch_matrix);


num_bus=fix(length(Info.F_bus)/div);
rest_bus=mod(length(Info.F_bus),div);
k=1;
for i=1:div
    for j=1:num_bus+1
        if k>length(Info.F_bus)


            F_bus_matrix(i,j)=0;

        elseif i+rest_bus<=div & j==num_bus+1
            F_bus_matrix(i,j)=0;
        else
            F_bus_matrix(i,j)=Info.F_bus(k);
            k=k+1;
        end
    end
end

%% Create the Uppaal code and Run the Verification to get the intial information
% Files_to_verify = 1:div; % This variable holds the indecies of the files that need to be
% verified. In case a file is verified once and was found to have no misconfigurations we
% can skip verifying this file on the next run of the while loop.

% Creating i files to be verified
for i = 1:div
    % if ~ismember(i,Files_to_verify) % If file i was eliminated at some point from the pool of files to be verified
    %     break % Then don't bother verifying 
    % end
    if isempty(Info.F_branch) || isempty(Info.F_bus)
        fprintf("Minimum number of faults not selected. \nPlease select at least 1 line fault. \n")
        break
    end
    XMLFILENAME = cell(div,1);
    XMLFILENAME{i,1} = strcat(XML_FOLDER,filesep,'Uppaal_code_generation_',string(i),'.xml');
    % Choosing the F_branch and F_bus to use
    if (i+rest>div && i+rest_bus<=div) || (F_branch_cols==1)
        F_branch_div = F_branch_matrix(i,1:end);
        F_bus_div = F_bus_matrix(i,1:end-1);
    elseif (i+rest<=div && i+rest_bus>div)
        F_branch_div = F_branch_matrix(i,1:end-1);
        F_bus_div = F_bus_matrix(i,1:end);
    elseif i+rest<=div && i+rest_bus<=div
        F_branch_div = F_branch_matrix(i,1:end-1);
        F_bus_div = F_bus_matrix(i,1:end-1);
    else
        F_branch_div = F_branch_matrix(i,1:end);
        F_bus_div = F_bus_matrix(i,1:end);
    end
    % Create file i
    fileID = fopen(XMLFILENAME{i,1},'w');
    GenPreamble(fileID);
    fprintf(fileID,"<nta>\n");
    [LinesCBs,F_branch_div] = GenDeclaration1(Info.A,Info.Line,Info.Bus,F_branch_div,F_bus_div,Info.Irc,Info.C,Info.Iioc_values,Info.Ith_values,Info.Isc_values_Lines,Info.Isc_values_Buses,fileID,Info.Bus_connections,Info.CB_settings);
    GenTemplate(fileID,Info.Line,Info.Bus,F_branch_div,F_bus_div,Info.Irc,Info.CB_settings);
    GenTemplateInstantiations(fileID,Info.CB_settings);
    GenQueries1(fileID,Info.Line,Info.Bus,F_branch_div,F_bus_div,Info.CB_settings,LinesCBs,Info.Bus_connections);
    fprintf(fileID,"</nta>\n");
    fclose(fileID);

    % Prepare verify command
    cmdVerifyTA{i,1} = strcat(VTA_FOLDER," -t0 ",XML_FOLDER,filesep,"Uppaal_code_generation_",string(i),'.xml');
    % Save the queries of file i
    uppaal_code{i,1} = importdata(XMLFILENAME{i,1}); % Import the Uppaal model as a string array
    querycell = regexpcell(uppaal_code{i,1},'formula>E&lt'); % the cell number in the uppaal code where the query for selectivity is written
    Info.allqueries{i,1} = uppaal_code{i,1}{querycell,1}; % save the queries of file i to the corresponding position in Info.allqueries variable
    % At this point in the code, I should have n XML files, n cmdVerifyTA
    % commands, n uppaal_code string arrays, and n allqueries saved to Info.
    % Where n is the number of times we split the network = div
    disp("QUERY INIZIALE:");
    disp(uppaal_code{i,1}{querycell,1});
    WrongCBs = true; % condition for the while loop, assumes at the beginning that there are some misconfigurations
    while WrongCBs == true
        % Check if the query is empty
        query_check = regexp(uppaal_code{i,1}{querycell,1},'CB','once');
        if isempty(query_check)
            break % if so, no need to verify 
        end
        % If not empty then verify the file
        [status, cmdout] = system(cmdVerifyTA{i,1});
        if status ~= 0
            disp(status);
            break
        end
        %% Get the results (here is where we build the WrongCB_Pairs_IDs vector
        outputLog = string(cmdout);

        selectivityIdx = regexp(outputLog,'Formula is satisfied','once'); % if the expression appears once then there is a misconfiguration
        if isempty(selectivityIdx)
            WrongCBs = false;
        else
            WrongCB_Expressions = regexp(outputLog,'CB[\d*]+.Closed->CB[\d*]+.Standby','match');
            WrongCB_ID_Exp = regexp(WrongCB_Expressions,'[1234567890]+','match');
            AllStandByCBs = [];
            for k = 1:length(WrongCB_ID_Exp)
                AllStandByCBs = [AllStandByCBs,',',WrongCB_ID_Exp{1,k}{1,1}];
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
                fprintf("FAULT LINEA ATTIVATO: %s\n", LineFaultLoc{1});
                disp("CB entrati in Standby:");
                disp(AllStandByCBs);
                LineFaultLocID = regexp(LineFaultLoc,'[1234567890]+','match');
                % FaultedLineID = str2double(LineFaultLocID{1,1}{1,1});
                FaultedLineID = str2double(LineFaultLocID);
                [forCounter,~] = size(WrongCB_pairs);
                for ii = 1:forCounter
                    faultedCB_ID = WrongCB_pairs(ii,1);
                    UpstreamCB_ID = WrongCB_pairs(ii,2);
                    remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)||',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                    regexploc = regexpcell(uppaal_code{i,1},remove_exp_loc);
                    if isempty(regexploc)
                        remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)||',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i\\)\\|\\|',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i',UpstreamCB_ID,faultedCB_ID,FaultedLineID);
                    else
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i\\)\\|\\|',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i',faultedCB_ID,UpstreamCB_ID,FaultedLineID);
                    end
                    regexploc = regexpcell(uppaal_code{i,1},remove_exp_loc); % find the location of the expression you want to remove
                    if ~isempty(regexploc)
                        dummie = regexprep(uppaal_code{i,1}{regexploc,1}, remove_exp, ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie; % Add the new expression to the string ar
                        dummie2 = regexprep(uppaal_code{i,1}{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie2; % Add the new expression to the string array
                        dummie3 = regexprep(uppaal_code{i,1}{regexploc,1}, '\(\)', ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie3; % Add the new expression to the string array
                      
                        IslastOR = regexp(uppaal_code{i,1}{regexploc,1},'\|\|\)');
                        if ~isempty(IslastOR)
                            All_Ors = regexp(uppaal_code{i,1}{regexploc,1},'\|\|');
                            LastOccurence = length(All_Ors);
                            dummie2 = regexprep(uppaal_code{i,1}{regexploc,1}, '\|\|', '',LastOccurence); % Replace the expression with nothing
                            uppaal_code{i,1}{regexploc,1} = dummie2; % Add the new expression to the string array
                        end
                    end
                    regexploc = regexpcell(uppaal_code{i,1},'formula>E&lt');
                    if exist('FaultedLineID','var')
                        faultTag = sprintf('F%d', FaultedLineID);
                    elseif exist('FaultedBusID','var')
                        faultTag = sprintf('FB%d', FaultedBusID);
                    else
                        faultTag = 'UNKNOWN';
                    end
                    
                    fprintf("Rimozione: (!CB%d.Open && CB%d.Open && %s)\n", faultedCB_ID, UpstreamCB_ID, faultTag);

                    dummie = regexprep(uppaal_code{i,1}{regexploc,1}, '\|\|\(\)', ''); % Replace the expression with nothing
                    uppaal_code{i,1}{regexploc,1} = dummie; % Add the new expression to the string array
                    disp("QUERY MODIFICATA:");
                    disp(uppaal_code{i,1}{regexploc,1});
                end
            else
                BusFaultLoc = regexp(outputLog,'FB[\d*]+ := true','match');
                fprintf("FAULT BUS ATTIVATO: %s\n", BusFaultLoc{1});
                BusFaultLocID = regexp(BusFaultLoc,'[1234567890]+','match');
                % FaultedBusID = str2double(BusFaultLocID{1,1}{1,1});
                FaultedBusID = str2double(BusFaultLocID);
                [forCounter,~] = size(WrongCB_pairs);
                for ii = 1:forCounter
                    faultedCB_ID = WrongCB_pairs(ii,1);
                    UpstreamCB_ID = WrongCB_pairs(ii,2);
                    remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)||',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                    regexploc = regexpcell(uppaal_code{i,1},remove_exp_loc);
                    if isempty(regexploc)
                        remove_exp_loc = sprintf('(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)||',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i\\)\\|\\|',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i',UpstreamCB_ID,faultedCB_ID,FaultedBusID);
                    else
                        % remove_exp = sprintf('\\(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i\\)\\|\\|',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                        remove_exp = sprintf('!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i',faultedCB_ID,UpstreamCB_ID,FaultedBusID);
                    end
                    regexploc = regexpcell(uppaal_code{i,1},remove_exp_loc); % find the location of the expression you want to remove
                    if ~isempty(regexploc)
                        dummie = regexprep(uppaal_code{i,1}{regexploc,1}, remove_exp, ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie; % Add the new expression to the string array
                        dummie2 = regexprep(uppaal_code{i,1}{regexploc,1}, '\(\)\|\|', ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie2; % Add the new expression to the string array
                        dummie3 = regexprep(uppaal_code{i,1}{regexploc,1}, '\(\)', ''); % Replace the expression with nothing
                        uppaal_code{i,1}{regexploc,1} = dummie3; % Add the new expression to the string array
                        IslastOR = regexp(uppaal_code{i,1}{regexploc,1},'\|\|\)');
                        if ~isempty(IslastOR)
                            All_Ors = regexp(uppaal_code{i,1}{regexploc,1},'\|\|');
                            LastOccurence = length(All_Ors);
                            dummie3 = regexprep(uppaal_code{i,1}{regexploc,1}, '\|\|', '',LastOccurence); % Replace the expression with nothing
                            uppaal_code{i,1}{regexploc,1} = dummie3; % Add the new expression to the string array
                        end
                    end
                    regexploc = regexpcell(uppaal_code{i,1},'formula>E&lt');
                    dummie = regexprep(uppaal_code{i,1}{regexploc,1}, '\|\|\(\)', ''); % Replace the expression with nothing
                    uppaal_code{i,1}{regexploc,1} = dummie; % Add the new expression to the string array
                    disp("QUERY MODIFICATA:");
                    disp(uppaal_code{i,1}{regexploc,1});
                end
            end
            disp("QUERY PRIMA DELLA SCRITTURA:");
            disp(uppaal_code{i,1}{querycell,1});
            writecell(uppaal_code{i,1},XMLFILENAME{i,1},FileType='text',QuoteStrings='none'); % write the array back to the same file
        end
    end
end


InitialObs = cell(1,NumAgents);
for k = 1:NumAgents
    InitialObs{1,k} = {Info.CB_setts(k,:),Info.A};
end

end