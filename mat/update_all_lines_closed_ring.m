function [] = update_all_lines_closed_ring(Idx_F_branch,Idx_F_bus,i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,Original_sources)
% [] = update_all_lines_closed_ring(Idx_F_branch,Idx_F_bus,i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,Original_sources)
% This function updates all lines affected by the fault starting from sources reaching the fault point except source lines for a closed ring network
% Note: m is the number of buses and n is the number of lines.
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
%                [G] - a graph structure that is created to represent the network as a set of nodes and edges where nodes are the buses and edges are the lines.
%        [srcBusIdx] - a row vector containing the indecies of all source buses.
%   [idx_link_lines] - a row vector containing the indecies of all ring connections.
%             [Line] - a 1*n vector of integer values representing line IDs.
%              [Bus] - a 1*m vector of integer values representing bus IDs.
%         [LinesCBs] - a n*2 cell array where for each row column 1 contains the Line ID and column 2 contains all the CB IDs present on that line.
%      [CB_settings] - a structure that has all the CB settings.
%         [CBsLines] - a n*2 vector linking line and CB IDs where each row has the CB ID in Col 1 and Line ID in col 2.
%         [SelTypes] - a vector of integer values representing the selectivity types available in the system.
% [Isc_values_Lines] - a n*n vector of integer values of the short circuit current contribution of each source due to a fault on each line,
%                      if the value is zero then that line has no source on it, for example:
%                      Isc_values_Lines(6,3) is the current on line 6 in case of a fault on line
%                      3, if it is zero that means line 6 is not connected to a source, if it is
%                      not zero that the value is the contribution of source 6 to a fault on line 3.
% [Isc_values_Buses] - a n*m vector of integer values of the short circuit current contribution of each source due to a fault on each bus,
%                      if the value is zero then that bus has no source on it, for example:
%                      Isc_values_Lines(12,5) is the current on line 12 in case of a fault on bus 5,
%                      if it is zero that means line 12 is not connected to a source, if it is not
%                      zero that the value is the contribution of source 12 to a fault on bus 5.
% [Original_sources] - a row vector containing the indecies of all source lines at the initial operating conditions.
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

%% Finding Link-line Currents
% This next part finds the link line currents due to the individual contribuition of each source (using superpostion theory)
Link_Line_Currents = zeros(length(idx_link_lines),2); % Vector where in each row you have in colomn one the index of the line and in row two the final current where if that value is +ve it means it is in the same direction as the original ref.
for jj = 1:length(idx_link_lines)
    Link_Line_Currents(jj,1) = idx_link_lines(jj);
end
for kk = 1:length(srcBusIdx)
    [path,edgepath] = allpaths(G,srcBusIdx(kk),TerminalBus);
    if length(path) > 1
        commonlines_in_path = intersect(edgepath{1,1},edgepath{2,1});
        commonbuses_in_path = intersect(path{1,1},path{2,1});
        Bus_Path_1_G = path{1,1};
        Bus_Path_2_G = path{2,1};
        Line_Path_1_G = edgepath{1,1}; % vector for the lines on the path but using the graph idx
        Line_Path_2_G = edgepath{2,1};
        Allpaths1 = {Bus_Path_1_G,Line_Path_1_G;Bus_Path_2_G,Line_Path_2_G};
        for kkk = 1:length(commonlines_in_path)
            Line_Path_1_G(Line_Path_1_G==commonlines_in_path(kkk))=[];
            Line_Path_2_G(Line_Path_2_G==commonlines_in_path(kkk))=[];
        end
        for kkk = 1:length(commonbuses_in_path)
            Bus_Path_1_G(Bus_Path_1_G==commonbuses_in_path(kkk))=[];
            Bus_Path_2_G(Bus_Path_2_G==commonbuses_in_path(kkk))=[];
        end
        Line_Path_1 = []; % vector of the correct line Idx for path 1
        for pp = 1:length(Line_Path_1_G) % convert from the edge indexing into the line indexing
            Line_Path_1 = [Line_Path_1 G.Edges.LineIDs(Line_Path_1_G(pp))];
        end
        Line_Path_2 = []; % vector of the correct line Idx for path 2
        for pp = 1:length(Line_Path_2_G) % convert from the edge indexing into the line indexing
            Line_Path_2 = [Line_Path_2 G.Edges.LineIDs(Line_Path_2_G(pp))];
        end
        % find the source contribution
        if BusIdx == 0
            Isrc = Isc_values_Lines(idx_source(kk),i); % this is the source contribution of source srcBusIdx(kk) for a fault on Line(i)
        else
            Isrc = Isc_values_Buses(idx_source(kk),BusIdx); % this is the source contribution of source srcBusIdx(kk) for a fault on bus (BusIdx)
        end
        % finding the split coeff
        n1 = length(Line_Path_1); % number of lines in path 1
        n2 = length(Line_Path_2); % number of lines in path 2
        Split_Coeff = n2/(n1+n2);
        % The current in each path
        Ipath1 = Split_Coeff*Isrc; % this value is the magnitude of current in each line in path 1
        Ipath2 = (1-Split_Coeff)*Isrc; % this value is the magnitude of current in each line in path 2
        for jj = 1:length(idx_link_lines) % Inside this loop I must find out the magnitude and direction of the current of each link line
            if ismember(idx_link_lines(jj),Line_Path_1)
                Current_Mag = Ipath1;
            elseif ismember(idx_link_lines(jj),Line_Path_2)
                Current_Mag = Ipath2;
            end
            % now to find the direction
            Direction_vector = ones(length(Line),1); % This is  vector indicating the direction of each line where 1 means FWD and -1 means BWD
            for k = 1:length(idx_link_lines)
                ref_Dir = Bus_connections(idx_link_lines(k),:); % this is the reference direction for line idx_link_lines(k)
                G_L_ID = find(G.Edges.LineIDs==idx_link_lines(k)); % This is the graph ID of line idx_link_lines(k)
                found_it = false; % This variable is done so as not to repeat the whole process after we found the direction of the line
                for rowID = 1:2 % This loop looks in every path looking for the line we want and then updates its direction
                    if found_it == false
                        GLineIdx = find(Allpaths1{rowID,2}==G_L_ID);
                        if ~isempty(GLineIdx)
                            Current_Dir = [Allpaths1{rowID,1}(GLineIdx),Allpaths1{rowID,1}(GLineIdx+1)];
                            found_it = true;
                        end
                    end
                end
                % Now we update the direction of the current directly in Uppaal
                if ref_Dir(1) ~= Current_Dir(1)
                    % fprintf(fileID,'   IsFWD_%i = false;\n',Line(idx_link_lines(k)));
                    Direction_vector(idx_link_lines(k)) = -1;
                end
            end
            Link_Line_Currents(jj,2) = Link_Line_Currents(jj,2) + (Current_Mag*Direction_vector(idx_link_lines(jj)));
        end
    end
end
Link_Line_Currents = round(Link_Line_Currents);


%% This is to find the affected and unaffected lines
Allpaths = [];
if ismember(TerminalBus,srcBusIdx)
    srcBusIdx(srcBusIdx==TerminalBus)=[]; % This is done in case one of the source buses is the faulted bus
end
for kk = 1:length(srcBusIdx) % Find all the paths between the fault and all the source buses
    [path,edgepath] = allpaths(G,srcBusIdx(kk),TerminalBus);
    Allpaths = [Allpaths;path,edgepath];
end

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

%% Updating the direction of the affected lines
[rA,~] = size(Allpaths);
Direction_vector = ones(length(Line),1); % This is  vector indicating the direction of each line where 1 means FWD and 0 means BWD
affected_lines1 = affected_lines;
for ppp = 1:length(idx_link_lines)
    affected_lines1(affected_lines1 == idx_link_lines(ppp)) = [];
end

for k = 1:length(affected_lines1)
    ref_Dir = Bus_connections(affected_lines1(k),:); % this is the reference direction for line affected_lines1(k)
    G_L_ID = find(G.Edges.LineIDs==affected_lines1(k)); % This is the graph ID of line affected_lines1(k)
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
        fprintf(fileID,'   IsFWD_%i = false;\n',Line(affected_lines1(k)));
        Direction_vector(affected_lines1(k)) = 0;
    end
end


%% Update the magnitude of all lines
for kk = 1:length(unaffected_lines)
    fprintf(fileID,'   %s = 0;\n',Irc(unaffected_lines(kk))); % Assign the selected lines to zero
end
% now update the link lines
[rr,~] = size(Link_Line_Currents);
for kk = 1:rr
    LHS = Irc(Link_Line_Currents(kk,1));
    RHS = abs(Link_Line_Currents(kk,2));
    fprintf(fileID,'   %s = %i;\n',LHS,RHS);
    if Link_Line_Currents(kk,2) < 0
        fprintf(fileID,'   IsFWD_%i = false;\n',Line(Link_Line_Currents(kk,1)));
        Direction_vector(Link_Line_Currents(kk,1)) = 0;
    end
end
% Find the remaining lines if any
RemainingLines_G = Allpaths{1,2};
for pp = 2:rA
    RemainingLines_G = intersect(RemainingLines_G,Allpaths{pp,2},'stable');
end
RemainingLines = []; % vector of the correct line Idx for the remaining lines
for pp = 1:length(RemainingLines_G) % convert from the edge indexing into the line indexing
    RemainingLines = [RemainingLines G.Edges.LineIDs(RemainingLines_G(pp))];
end
% update the remaining lines if any
if ~isempty(RemainingLines)
    for kk = 1:length(RemainingLines)
        if ismember(RemainingLines(kk),Original_sources)
            LHS = Irc(RemainingLines(kk));
            KCLrowIdx = find(A(:,RemainingLines(kk))==1);
            KCLrow = A(KCLrowIdx,:);
            for ppp = 1:length(idx_link_lines)
                if KCLrow(idx_link_lines(ppp))==-1
                    KCLrow(idx_link_lines(ppp))=1;
                end
            end
            KCLrow(RemainingLines(kk)) = 0;
            RHS = KCLrow*Irc;
            fprintf(fileID,'   %s = %s;\n',LHS,RHS);
        else
            LHS = Irc(RemainingLines(kk));
            KCLrowIdx = find(A(:,RemainingLines(kk))==-1);
            KCLrow = A(KCLrowIdx,:);
            for ppp = 1:length(idx_link_lines)
                if KCLrow(idx_link_lines(ppp))==-1
                    KCLrow(idx_link_lines(ppp))=1;
                end
            end
            KCLrow(RemainingLines(kk)) = 0;
            RHS = KCLrow*Irc;
            fprintf(fileID,'   %s = %s;\n',LHS,RHS);
        end
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

end