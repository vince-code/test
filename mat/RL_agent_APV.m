%% Initialie your folder locations
clear all
clc
global XML_FOLDER; % Uppaal xml file desired location
global VTA_FOLDER; % Verifyta location on your device
global XMLFILENAME; % Name of the file with the full path

XML_FOLDER = strcat(filesep,'Users',filesep,'ahmed',filesep,'Desktop',filesep,'Work',filesep,'PhD',filesep,'Github',filesep,'ABB-protections',filesep,'output');
VTA_FOLDER = strcat(filesep,'Applications',filesep,'UPPAAL-5.1.0-beta5.app',filesep,'Contents',filesep,'Resources',filesep,'uppaal',filesep,'bin',filesep,'verifyta');
XMLFILENAME = strcat(XML_FOLDER,filesep,'Uppaal_code_generation.xml');

%cmdVerifyTA = strcat(VTA_FOLDER," -t0 ",XML_FOLDER,filesep,"Uppaal_code_generation.xml > output.log 2> trace.txt");

%% Import the JSON file
% First add all your training network names to the cell array of names called Training_file_names
% Training_file_names = {'Example_7bus_Network_1.json','Example_7bus_Network_2.json',...
%                        'Example_7bus_Network_3.json','Example_7bus_Network_4.json',...
%                        'Example_7bus_Network_6.json'};
% Training_file_names = {'Example_7bus_Network_1.json','Example_7bus_Network_2.json',...
%                        'Example_7bus_Network_3.json','Example_7bus_Network_4.json',...
%                        'Example_7bus_Network_5.json','Example_7bus_Network_6.json'};

% Training_file_names = {'Example_20bus_Network_1.json','Example_20bus_Network_2.json',...
%                        'Example_20bus_Network_4.json','Example_20bus_Network_7.json',...
%                        'Example_20bus_Network_11.json','Example_20bus_Network_13.json',...
%                        'Example_20bus_Network_16.json','Example_20bus_Network_17.json',...
%                        'Example_20bus_Network_18.json','Example_20bus_Network_19.json'};
Training_file_names = {'Example_20bus_Network_1.json','Example_20bus_Network_19.json',...
                       'Example_20bus_Network_11.json','Example_20bus_Network_13.json',...
                       'Example_20bus_Network_16.json','Example_20bus_Network_17.json',...
                       'Example_20bus_Network_18.json'};


% Training_file_names = {'Example_10bus.json'};

[~,Num_training_files] = size(Training_file_names); % Find the number of files to train on
Info_cell = {};

for i = 1:Num_training_files
    fileName = Training_file_names{i};
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
    Info.allqueries =[];% A Variable containting all the queries that needs to be checked. 
                        % It is the cell that starts with <formula> E blablabla
                        % and needs to replace the cell containing the queries
                        % in the uppaalcode variable everytime we change
                        % the t2 settings
    Info_cell{i,1} = Info;
end

% fileName = 'Example_20bus_new_source_structure.json';
% fileName = 'Example_10bus.json';
% fileName = 'Example_6bus_new_source_structure.json';
% fileName = 'Example_7bus_Network_1.json';
% % Read the data
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);
% % Create the Info struct
% Info = struct('A',A,'Line',Line,'Bus',Bus,'F_branch',F_branch,'F_bus',F_bus,'Irc',Irc,'C',C,'Iioc_values',Iioc_values,'Ith_values',Ith_values,'Isc_values_Lines',Isc_values_Lines,'Isc_values_Buses',Isc_values_Buses,'CB_settings',CB_settings,'Bus_connections',Bus_connections);
% 
% rng(2024); % Set the seed to get the same random numbers for reproducability 
%% Create the Environment
% Observation Information (ObsInfo)
% NumAgents = length(CB_settings.CB);
[n,m] = size(A); % where n is the number of buses and m is the number of lines
% ObsInfo = cell(1,NumAgents);
% for i = 1:NumAgents
%     ObsInfo{1,i} = rlNumericSpec([1,3]);
%     ObsInfo{1,i}.Name = sprintf('CB%i info',CB_setts(i,1));
%     ObsInfo{1,i}.Description = 'List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)';
% end
% for i = 1:NumAgents
%     ObsInfo{1,i} = {rlNumericSpec([1,3]),rlNumericSpec([n,m])};
%     ObsInfo{1,i}{1,1}.Name = 'CB info';
%     ObsInfo{1,i}{1,1}.Description = 'List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)';
%     ObsInfo{1,i}{1,2}.Name = 'Incidence Matrix';
%     ObsInfo{1,i}{1,2}.Description = 'Matrix describing the connections between buses and lines';
%     ObsInfo{1,i}{1,2}.UpperLimit = 1;
%     ObsInfo{1,i}{1,2}.LowerLimit = -1;
% end
% for i = 1:NumAgents
%     ObsInfo{1,i} = {rlNumericSpec([1,3]),rlFiniteSetSpec([1 0])};
%     ObsInfo{1,i}{1,1}.Name = sprintf('CB%i info',CB_setts(i,1));
%     ObsInfo{1,i}{1,1}.Description = 'List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)';
%     ObsInfo{1,i}{1,2}.Name = 'Selectivity Index';
%     ObsInfo{1,i}{1,2}.Description = 'The selectivity achieved or not observation true for achieved and false for not achieved';
% end
% ObsInfo = [ rlNumericSpec([NumAgents,3])   % List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)
%             rlFiniteSetSpec([1 0])      % The selectivity achieved or not observation
%           ];
% ObsInfo(1).Name = "CB_Info";
% ObsInfo(1).Description = "List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)";
% 
% ObsInfo(2).Name = "Selectivity_Index";
% ObsInfo(2).Description = "A boolean variable indicating wether selectivity was achieved or not";

for i = 1:NumAgents
    ObsInfo{1,i} = [rlNumericSpec([1,3]),rlNumericSpec([n,m])];
    ObsInfo{1,i}(1,1).Name = 'CB info';
    ObsInfo{1,i}(1,1).Description = 'List of CB IDs, their t2 settings and wether they are misconfigured or not(col1 CB IDs, col2 t2 setting, col3 1=right and 0=wrong)';
    ObsInfo{1,i}(1,2).Name = 'Incidence Matrix';
    ObsInfo{1,i}(1,2).Description = 'Matrix describing the connections between buses and lines';
    ObsInfo{1,i}(1,2).UpperLimit = 1;
    ObsInfo{1,i}(1,2).LowerLimit = -1;
end


% Action Information (ActInfo)
ActInfo = cell(1,NumAgents);
for i = 1:NumAgents
    ActInfo{1,i} = rlFiniteSetSpec([1:4]);
    ActInfo{1,i}.Name = 't2 settings';
    ActInfo{1,i}.Description = 'All possible values for t2 settings for each CB assuming they are the same for now';
end

% ActInfo = rlNumericSpec([NumAgents,2]);
% ActInfo.Name = "CB IDs and their t2 settings";
% ActInfo.UpperLimit = 20;
% ActInfo.LowerLimit = 1;
% ActInfo.DataType = 'single';


% Custom Reset Function
ResetHandle = @() APV_resetFcn2(Info_cell);

% use rlFunctionEnv
% env = rlFunctionEnv(ObsInfo,ActInfo,"APV_stepFcn",ResetHandle);
% env = rlMultiAgentFunctionEnv(ObsInfo,ActInfo,"APV_stepFcn",ResetHandle);
env = rlMultiAgentFunctionEnv(ObsInfo,ActInfo,"APV_stepFcn1",ResetHandle);

% Define the simulation time Tf and the sample time Ts
% Tf = 200;
% Ts = 1.0;

%% Create the Agents
% DQN Agent
% for k = 1:length(Info.CB_settings.CB)
%     %Action(k,1) = Info.CB_settings.CB{1,k}.CB_ID;
%     Action(k) = randi([1 10]);
% end

% Decentralized training
% DQN = cell(1,NumAgents); % A struct containing one agent per field
% for i = 1:NumAgents
%     DQN{1,i} = rlDQNAgent(ObsInfo{1,i},ActInfo{1,i});
% end

% Centralized training
dqnOpts = rlDQNAgentOptions;
dqnOpts.EpsilonGreedyExploration.Epsilon = 1; % Start with full exploration
dqnOpts.EpsilonGreedyExploration.EpsilonDecay = 0.01; % Slow decay over episodes
dqnOpts.EpsilonGreedyExploration.EpsilonMin = 0.1; % Keep some exploration
dqnOpts.ExperienceBufferLength = 1e6; % Increase buffer size for better training
dqnOpts.TargetUpdateFrequency = 1000;    % Update target network more frequently

DQN = cell(1,NumAgents); % A struct containing one agent per field
for i = 1:NumAgents
    DQN{1,i} = rlDQNAgent(ObsInfo{1,i},ActInfo{1,i},dqnOpts);
end

%Extract the deep neural network from both the critic.
% criticNet = getModel(getCritic(DQN(1)));
% plot(criticNet)

%% Train the Agent
% First define the training options

cnOpts = rlMultiAgentTrainingOptions( ... 
    AgentGroups={[1:NumAgents]}, ... 
    LearningStrategy= "centralized" , ... 
    MaxEpisodes=75, ... 
    MaxStepsPerEpisode=100, ... 
    StopTrainingCriteria= "AverageReward",...
    StopTrainingValue=750 );

% dcnOpts = rlMultiAgentTrainingOptions( ... 
%     AgentGroups={[1:NumAgents]}, ... 
%     LearningStrategy= "decentralized" , ... 
%     MaxEpisodes=200, ... 
%     MaxStepsPerEpisode=200, ... 
%     ScoreAveragingWindowLength=30, ... 
%     StopTrainingCriteria= "AverageReward",...
%     StopTrainingValue=500 );

% trainOpts = rlTrainingOptions(...
%     MaxEpisodes=5000, ...
%     MaxStepsPerEpisode=ceil(Tf/Ts), ...
%     ScoreAveragingWindowLength=20, ...
%     Verbose=false, ...
%     Plots="training-progress",...
%     StopTrainingCriteria="AverageReward",...
%     StopTrainingValue=100);
% trainingStats = train(agent,env,trainOpts);

trainingStats = train([DQN{:}],env,cnOpts);
%save('DQN_6_Agents_Centralized.mat',"DQN");

%% Validate Trained Agent
% Load or define the trained agent
% TA = load('DQN_6_Agents_Centralized.mat');
% agentName = fieldnames(TA);
% TrainedAgent = TA.(agentName{1,1});

TrainedAgent = DQN;

% Simulate the trained agent
simOpts = rlSimulationOptions(MaxSteps=ceil(200),StopOnError="on");
experiences = sim(env,[TrainedAgent{:}],simOpts);

%% Visualize the results
col1 = [];
col2 = [];
col3 = [];
col4 = [];
for i = 1:NumAgents
    col1 = [col1 ; experiences(i).Observation.CBInfo.Data(1,1,1)]; % getting the CB_IDs
    col2 = [col2 ; experiences(i).Action.t2Settings.Data(:,:,end)]; % getting the Actions
    col3 = [col3 ; experiences(i).Reward.Data(end)];
    col4 = [col4 ; experiences(i).Observation.CBInfo.Data(1,3,end)];
end

columnNames = ["CB_ID","Action","Reward","Status"];

resultsTable = table(col1,col2,col3,col4,'VariableNames',columnNames)

%% Plot the network structure from the incidence matrix
incidenceMatrix = experiences(1).Observation.IncidenceMatrix.Data(:,:,1); % Input incidence matrix

[numNodes, numEdges] = size(incidenceMatrix); % Find the number of nodes and edges

% Initialize source and target arrays for edges
source = [];
target = [];

% Parse the incidence matrix to determine source and target nodes for edges
for edge = 1:numEdges
    % Find nodes connected by this edge
    outgoingNode = find(incidenceMatrix(:, edge) == -1);
    incomingNode = find(incidenceMatrix(:, edge) == 1);
    
    % Add to the source and target arrays
    if ~isempty(outgoingNode) && ~isempty(incomingNode)
        source = [source; outgoingNode];
        target = [target; incomingNode];
    end
end

% Create and plot the graph
G = digraph(source, target); % Directed graph
figure;
plot(G, 'Layout', 'layered', 'NodeLabel', 1:numNodes, 'EdgeLabel', 1:numEdges);
title('Graph Representation of Incidence Matrix');

%% Get a suggestion from a trained agent
% Define the current observation
% fileName1 = 'Example_7bus_Network_5.json';
fileName1 = 'Example_20bus_Network_11.json';
[A1,Line1,Bus1,F_branch1,F_bus1,Irc1,C1,Iioc_values1,Ith_values1,Isc_values_Lines1,Isc_values_Buses1,CB_settings1,Bus_connections1] = Reading_json_data1(fileName1);
% Create the Info struct
Info1 = struct('A',A1,'Line',Line1,'Bus',Bus1,'F_branch',F_branch1,'F_bus',F_bus1,'Irc',Irc1,'C',C1,'Iioc_values',Iioc_values1,'Ith_values',Ith_values1,'Isc_values_Lines',Isc_values_Lines1,'Isc_values_Buses',Isc_values_Buses1,'CB_settings',CB_settings1,'Bus_connections',Bus_connections1);
% CB settings
NumAgents = length(Info1.CB_settings.CB); % Total number of CBs
CB_setts = [];% vector of size (k*2) where k is the number of CBs, colomn 1 has the CB IDs and colomn 2 has he short time delay setting (t2)
for k = 1:NumAgents
    CB_setts(k,1) = Info1.CB_settings.CB{1,k}.CB_ID; % extracting the CB ID to colomn 1
    CB_setts(k,2) = Info1.CB_settings.CB{1,k}.t2; % extracting the short time delay setting (t2) to colomn 2
    CB_setts(k,3) = 1; % assume that all CBs are correctly configured until otherwise discovered
end
Info1.CB_setts = CB_setts;
[CurrentObs,Info1] = APV_resetFcn(Info1);

%%
incidenceMatrix = Info1.A;

[numNodes, numEdges] = size(incidenceMatrix); % Find the number of nodes and edges

% Initialize source and target arrays for edges
source = [];
target = [];

% Parse the incidence matrix to determine source and target nodes for edges
for edge = 1:numEdges
    % Find nodes connected by this edge
    outgoingNode = find(incidenceMatrix(:, edge) == -1);
    incomingNode = find(incidenceMatrix(:, edge) == 1);
    
    % Add to the source and target arrays
    if ~isempty(outgoingNode) && ~isempty(incomingNode)
        source = [source; outgoingNode];
        target = [target; incomingNode];
    end
end

% Create and plot the graph
G1 = digraph(source, target); % Directed graph
figure;
plot(G1, 'Layout', 'layered', 'NodeLabel', 1:numNodes, 'EdgeLabel', 1:numEdges);
title('Graph Representation of Incidence Matrix');

%% Create the correct number of agents
NumTrainedAgents = length(TrainedAgent);
NumNeededAgents = NumAgents - NumTrainedAgents;

if NumNeededAgents == 0
    agent = TrainedAgent;
elseif NumNeededAgents > 0
    for i = 1:NumNeededAgents
        TrainedAgent{1,NumTrainedAgents+i} = TrainedAgent{1,i};
    end
elseif NumNeededAgents < 0
    for i = 1:abs(NumNeededAgents)
        TrainedAgent(end) = [];
    end
end

%% Get a suggestion
numAgents = numel(TrainedAgent);         % Number of agents
ActionsCell = cell(1, numAgents);        % Preallocate for cell array
ActionsVector = zeros(numAgents, 1);     % Preallocate for numeric column vector

for i = 1:numAgents
    % Get action for each agent
    action = getAction(TrainedAgent{i}, CurrentObs{i});
    
    % Store action in the cell array
    ActionsCell{i} = action; 
    
    % Convert to numeric and store in the vector if possible
    if iscell(action)
        ActionsVector(i) = cell2mat(action); % Extract value if it's a cell
    else
        ActionsVector(i) = action; % Assign directly if already numeric
    end
end
%% Verify the suggested data
[FinalObs,Reward,IsDone,UpdatedInfo] = APV_stepFcn1(ActionsVector,Info1);

%% Random Action generation
ActionsCell = cell(1, numAgents);        % Preallocate for cell array
for i = 1:NumAgents
    ActionsCell{1,i} = randi([1 4]);
end