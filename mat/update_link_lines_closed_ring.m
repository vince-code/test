function [] = update_link_lines_closed_ring(i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C)
% [] = update_link_lines_closed_ring(i,BusIdx,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C)
% This function updates the source line currents in case a disconnection of a link line caused a source disconnection for a closed ring network
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
%        [srcBusIdx] - a row vector containing the indecies of all source buses.
%   [idx_link_lines] - a row vector containing the indecies of all ring connections.
%         [Line_Idx] - the index of the disconnected line
%             [Gnew] - a new graph structure that is created after removing an edge due to a certain CB opening
%                [C] - a n*1 vector of symbols of Ci where i is the line index, each symbol can have a value of 1 or 0, 1 if the CB on line i is open and 0 if the CB on line i is closed.
% 
% see also GenDeclaration1

if BusIdx == 0
    TerminalBus = Bus_connections(i,2); % the bus connected to the fault (the from bus for the disconnected line)
else
    TerminalBus = BusIdx;
end

%% Update the disconnected sources for a second opening
% First take the Gnew were the opened line is already removed and for each
% of the link lines remove and see which sources gets disconnected
Remaining_idx_link_lines = idx_link_lines;
Remaining_idx_link_lines(Remaining_idx_link_lines==Line_Idx) = [];
% fprintf(fileID,'   if (%s == 0){\n',C(Remaining_idx_link_lines(1)));
% ind = find(Gnew.Edges.LineIDs==Remaining_idx_link_lines(1)); % finding the index of the line inside the graph from the Line ID
% Gnew1 = rmedge(Gnew,ind); % remove the opened line from the graph G and save it in a new variable Gnew
% Disconnected_Src = [];
% for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
%     [~,edgepath2] = allpaths(Gnew1,TerminalBus,srcBusIdx(kk));
%     if isempty(edgepath2)
%         Disconnected_Src = [Disconnected_Src,srcBusIdx(kk)];
%     end
% end
% for kk = 1:length(Disconnected_Src)
%     srcLinelocation = find(srcBusIdx==Disconnected_Src(kk));
%     Disconnected_Src_Line_Idx = idx_source(srcLinelocation);
%     fprintf(fileID,'     %s = 0;\n',Irc(Disconnected_Src_Line_Idx));
% end
% fprintf(fileID,'     }\n');

for jj = 1:length(Remaining_idx_link_lines)
    fprintf(fileID,'   if (%s == 0){\n',C(Remaining_idx_link_lines(jj)));
    ind = find(Gnew.Edges.LineIDs==Remaining_idx_link_lines(jj)); % finding the index of the line inside the graph from the Line ID
    Gnew1 = rmedge(Gnew,ind); % remove the opened line from the graph G and save it in a new variable Gnew
    Disconnected_Src = [];
    for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew1,TerminalBus,srcBusIdx(kk));
        if isempty(edgepath2)
            Disconnected_Src = [Disconnected_Src,srcBusIdx(kk)];
        end
    end
    for kk = 1:length(Disconnected_Src)
        srcLinelocation = find(srcBusIdx==Disconnected_Src(kk));
        Disconnected_Src_Line_Idx = idx_source(srcLinelocation);
        fprintf(fileID,'     %s = 0;\n',Irc(Disconnected_Src_Line_Idx));
    end
    fprintf(fileID,'     }\n');
end


end