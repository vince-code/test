function [] = update_affected_lines(i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines)
% [] = update_affected_lines(i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines)
% This function updates all lines affected by an opening of a circuit breaker for a radial or an open ring network
% Note: m is the number of buses and n is the number of lines
% The input parameters are:
%                [i] - an integer value representing the index of the faulted line, zero if the fault is on a bus not a line
%           [BusIdx] - an integer value representing the index of the faulted bus, zero if the fault is on a line not a bus
%       [idx_source] - a row vector representing the indecies of all source lines
%  [Bus_connections] - a n*2 vector detailing for each line the from bus in column 1 and the to bus in column 2.
%              [Irc] - a n*1 vector of symbols of Irc_i where i is the line index, Irc is the running current.
%           [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%                [A] - the m*n incidence matrix where m is the number of buses and n is the number of lines.
%                [G] - a graph structure that is created to represent the network as a set of nodes and edges where nodes are the buses and edges are the lines.
%                      This input can vary depending on how the network topology changes due to opening of CBs
%        [srcBusIdx] - a row vector containing the indecies of all source buses.
%   [idx_link_lines] - a row vector containing the indecies of all ring connections.
% 
% see also GenDeclaration1

if BusIdx == 0
    TerminalBus = Bus_connections(i,2); % the bus connected to the fault (the from bus for the disconnected line)
else
    TerminalBus = BusIdx;
end 

Allpaths = [];
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

%% Assigning Priority Levels
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

%% Updating affected the lines

if No_of_priority_levels ~= 0
    for pp = 1:No_of_priority_levels
        priorityLinex = priorityLine{pp,1};
        priorityBusx = priorityBus{pp,1};
        for kk = 1:length(priorityLinex)
%             if priorityLinex(kk)~=i
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
%             end
        end
    end
end


end