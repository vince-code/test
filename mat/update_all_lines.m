function [] = update_all_lines(Idx_F_branch,Idx_F_bus,i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes)
% [] = update_all_lines(Idx_F_branch,Idx_F_bus,i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes)
% This function updates all lines affected by the fault starting from sources reaching the fault point except source lines for radial or open ring networks
% The input parameters are:
%     [Idx_F_branch] - an integer value representing the location of the line index inside the F_brance variable
%        [Idx_F_bus] - an integer value representing the location of the bus index inside the F_bus variable
%                [i] - an integer value representing the index of the faulted line, zero if the fault is on a bus not a line
%           [BusIdx] - an integer value representing the index of the faulted bus, zero if the fault is on a line not a bus
%       [idx_source] - a row vector representing the indecies of all source lines
%  [Bus_connections] - a n*2 vector detailing for each line the from bus in column 1 and the to bus in column 2.
%              [Irc] - a n*1 vector of symbols of Irc_i where i is the line index, Irc is the running current.
%           [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%                [A] - the m*n incidence matrix where m is the number of buses and n is the number of lines.
%                [G] - a graph structure that is created to represent the network as a set of nodes and edges where nodes are the buses and edges are the lines
%        [srcBusIdx] - a row vector containing the indecies of all source buses.
%   [idx_link_lines] - a row vector containing the indecies of all ring connections.
%             [Line] - a 1*n vector of integer values representing line IDs.
%              [Bus] - a 1*m vector of integer values representing bus IDs.
%         [LinesCBs] - a n*2 cell array where for each row column 1 contains the Line ID and column 2 contains all the CB IDs present on that line.
%      [CB_settings] - a structure that has all the CB settings.
%         [CBsLines] - a n*2 vector linking line and CB IDs where each row has the CB ID in Col 1 and Line ID in col 2.
%         [SelTypes] - a vector of integer values representing the selectivity types available in the system.
%
% see also GenDeclaration1

global Fault_Analysis_Line;
global Fault_Analysis_Bus;
if BusIdx == 0
    TerminalBus = Bus_connections(i,2); % the bus connected to the fault
    Fault_Analysis_Line{Idx_F_branch,1} = Line(i); 
else
    TerminalBus = BusIdx;
    Fault_Analysis_Bus{Idx_F_bus,1} = Bus(BusIdx);
end

Allpaths = [];
if nnz(TerminalBus==srcBusIdx)
    srcBusIdx(srcBusIdx==TerminalBus)=[]; % This is done in case one of the source buses is the faulted bus
end
for kk = 1:length(srcBusIdx) % Find all the paths between the fault and all the source buses
    [path,edgepath] = allpaths(G,srcBusIdx(kk),TerminalBus);
    Allpaths = [Allpaths;path,edgepath];
end
%% Find the longest path
% for pp = length(srcBusIdx):-1:1
%     if pp>1
%         if length(Allpaths{pp,1})>length(Allpaths{(pp-1),1})
%             Allpaths((pp-1),:) = [];
%         else
%             Allpaths(pp,:) = [];
%         end
%     end
% end
%% Remove the src buses and lines from the path
BusPath = Allpaths(:,1);
[rBus,~] = size(BusPath);
for pp = 1:rBus
    for kk = 1:length(srcBusIdx) % remove the source bus index
        BusPath{pp,1}(BusPath{pp,1}==srcBusIdx(kk))=[];
    end
    BusPath{pp,1}(BusPath{pp,1}==TerminalBus)=[]; % remove the terminal bus index
end

EdgePath = Allpaths(:,2);
[rEdgePath,~] = size(EdgePath);
LinePath = EdgePath;
for ppp = 1:rEdgePath
    LinePath1 = EdgePath{ppp,1};
    LinePath2 = [];
    for kk = 1:length(LinePath1) % convert from the edge indexing into the line indexing
        LinePath2 = [LinePath2 G.Edges.LineIDs(LinePath1(kk))];
    end
    for kk = 1:length(idx_source) % remove the source line index
        LinePath2(LinePath2==idx_source(kk))=[];
    end
    LinePath{ppp,1} = LinePath2;
end
% Sort LinePath and BusPath in Descendinf order
[~,LinePathSortedIdx] = sort(cellfun(@length,LinePath),'descend');
LinePath = LinePath(LinePathSortedIdx);
BusPath = BusPath(LinePathSortedIdx);

%% Assigning Priority Levels
% this is done in order to update the line currents in the correct order starting from the farthest point to the closest point with respect to the fault location
No_of_priority_levels = length(LinePath{1,1});
if No_of_priority_levels ~= 0
    priorityLine = {};
    priorityLine{No_of_priority_levels,1} = [];
    priorityBus = {};
    priorityBus{No_of_priority_levels,1} = [];
    for gg = 1:rEdgePath
        LinePathx = LinePath{gg,1};
        BusPathx = BusPath{gg,1};
        priority = No_of_priority_levels;
        for kk = length(LinePathx):-1:1
            if ~ismember(LinePathx(kk),priorityLine{priority,1})
                priorityLine{priority,1} = [priorityLine{priority,1},LinePathx(kk)];
                priorityBus{priority,1} = [priorityBus{priority,1},BusPathx(kk)];
            end
            priority = priority - 1;
        end
    end
end

%% Updating all the lines
%% Find all unaffected and affected lines
all_lines = 1:1:length(Irc); % vector of indicies of all unaffected lines
unaffected_lines = all_lines;
for kkk = 1:rEdgePath
    for pp = 1:length(LinePath{kkk,1}) % Removes the affected lines index from the vector
        unaffected_lines(unaffected_lines==LinePath{kkk,1}(pp))=[];
    end
end
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    unaffected_lines(unaffected_lines==idx_source(pp))=[];
end
% The affected lines are
affected_lines = all_lines;
for kk = 1:length(unaffected_lines)
    affected_lines(affected_lines==unaffected_lines(kk))=[];
end

%% Updating the direction fo the affected lines
Direction_vector = ones(length(Line),1); % This is  vector indicating the direction of each line where 1 means FWD and 0 means BWD
for k = 1:length(affected_lines)
    ref_Dir = Bus_connections(affected_lines(k),:); % this is the reference direction for line affected_lines(k)
    G_L_ID = find(G.Edges.LineIDs==affected_lines(k)); % This is the graph ID of line affected_lines(k)
    [rA,~] = size(Allpaths);
    found_it = false; % This variable is done so as not to repeat the whole process after we found the direction of the line
    for rowID = 1:rA % This loop looks in every path looking for the line we want and then updates its direction
        if found_it == false
            GLineIdx = find(Allpaths{rowID,2}==G_L_ID);
            if ~isempty(GLineIdx)
                Current_Dir = [Allpaths{rowID,1}(GLineIdx),Allpaths{rowID,1}(GLineIdx+1)];
                found_it = true;
            end
        end
    end
    % Now we update the direction of the current directly in Uppaal
    if ref_Dir(1) ~= Current_Dir(1)
        fprintf(fileID,'   IsFWD_%i = false;\n',Line(affected_lines(k)));
        Direction_vector(affected_lines(k)) = 0;
    end
end

%% Updating the Blocking signals based on the EkipLink data
% First find the IDs of the affected CBs
affected_CBs = [];
for nn = 1:length(affected_lines)
    affected_CBs = [affected_CBs,LinesCBs{affected_lines(nn),2}];
end

if ismember(3,SelTypes)
    for nn = 1:length(affected_CBs) % For only the affected CB this loop will check the Ekip Link table
        Idx = find(cellfun(@(x) x.CB_ID==affected_CBs(nn), CB_settings.Ekip_Link));
        Actors = CB_settings.Ekip_Link{1,Idx}.Actors; % Cell strcture of all the actors on affected_CBs(nn)
        if ~isempty(Actors) % this to skip if there are no block signals for this particular CB
            for kkk = 1:length(Actors) % if there are more than one actor, 1 by 1 check them
                if ismember(Actors{1,kkk}.Actor_CB,affected_CBs) % this is to skip if the actor CB is not affected
                    ActorLineID = CBsLines(find(CBsLines(:,1)==Actors{1,kkk}.Actor_CB),2);
                    ActorCBLineIdx = find(Line==ActorLineID);
                    CBLineID = CB_settings.Ekip_Link{1,Idx}.Line_ID;
                    CBLineIdx = find(Line==CBLineID);
                    if Direction_vector(ActorCBLineIdx)==Actors{1,kkk}.Actor_CB_Dir && Direction_vector(CBLineIdx)==Actors{1,kkk}.CB_Dir
                        fprintf(fileID,'   Block_%i = true;\n',affected_CBs(nn));
                    end
                end
            end
        end
    end
end
% This next small part adds the affected CB IDs to the global variables
if BusIdx == 0
    Fault_Analysis_Line{Idx_F_branch,2} = affected_CBs; 
else
    Fault_Analysis_Bus{Idx_F_bus,2} = affected_CBs;
end
%% Update the magnitude of all lines
for kk = 1:length(unaffected_lines)
    fprintf(fileID,'   %s = 0;\n',Irc(unaffected_lines(kk))); % Assign the selected lines to zero
end

if No_of_priority_levels ~= 0
    for pp = 1:No_of_priority_levels
        priorityLinex = priorityLine{pp,1};
        priorityBusx = priorityBus{pp,1};
        for kk = 1:length(priorityLinex)
            LHS = Irc(priorityLinex(kk));
            KCLrow = A(priorityBusx(kk),:);
            for ppp = 1:length(idx_link_lines)
                if KCLrow(idx_link_lines(ppp))==-1
                    KCLrow(idx_link_lines(ppp))=1;
                end
            end
            KCLrow(priorityLinex(kk)) = 0;
            RHS = KCLrow*Irc;
            fprintf(fileID,'   %s = %s;\n',LHS,RHS);
        end
    end
end

% RemainingLines = [];
% RemainingBuses = [];
% visitedLines = [];
% % First update the link lines
% for gg = 1:rEdgePath
%     LinePathx = LinePath{gg,1};
%     BusPathx = BusPath{gg,1};
%     for kk = 1:length(LinePathx)
%         if ismember(LinePathx(kk),idx_link_lines)
%             if ismember(LinePathx(kk),visitedLines)
%             else
%                 LHS = Irc(LinePathx(kk));
%                 KCLrow = A(BusPathx(kk),:);
%                 for ppp = 1:length(idx_link_lines)
%                     if KCLrow(idx_link_lines(ppp))==-1
%                         KCLrow(idx_link_lines(ppp))=1;
%                     end
%                 end
%                 KCLrow(LinePathx(kk)) = 0;
%                 RHS = KCLrow*Irc;
%                 fprintf(fileID,'   %s = %s;\n',LHS,RHS);
%                 visitedLines = [visitedLines,LinePathx(kk)];
%             end
%         else
%             if ismember(LinePathx(kk),RemainingLines)
%             else
%                 RemainingLines = [RemainingLines LinePathx(kk)];
%                 RemainingBuses = [RemainingBuses BusPathx(kk)];
%             end
%         end
%     end
% end
%
% % Update the remaining lines
% for kk = 1:length(RemainingLines)
%     LHS = Irc(RemainingLines(kk));
%     KCLrow = A(RemainingBuses(kk),:);
%     for ppp = 1:length(idx_link_lines)
%         if KCLrow(idx_link_lines(ppp))==-1
%             KCLrow(idx_link_lines(ppp))=1;
%         end
%     end
%     KCLrow(RemainingLines(kk)) = 0;
%     RHS = KCLrow*Irc;
%     fprintf(fileID,'   %s = %s;\n',LHS,RHS);
% end

end