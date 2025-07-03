function [] = update_source_lines_closed_ring(i,BusIdx,~,Bus_connections,Irc,fileID,~,G,srcBusIdx,~,Disconnected_Src_Line_ID,Line,Bus)
% [] = update_source_lines_closed_ring(i,BusIdx,~,Bus_connections,Irc,fileID,~,G,srcBusIdx,~,Disconnected_Src_Line_ID,Line,Bus)
% This function updates all lines affected by an opening of a line connected directly to a source bus
% Note: m is the number of buses and n is the number of lines.
% The input parameters are:
%                        [i] - an integer value representing the index of the faulted line, zero if the fault is on a bus not a line
%                   [BusIdx] - an integer value representing the index of the faulted bus, zero if the fault is on a line not a bus
%          [Bus_connections] - a n*2 vector detailing for each line the from bus in column 1 and the to bus in column 2.
%                      [Irc] - a n*1 vector of symbols of Irc_i where i is the line index, Irc is the running current.
%                   [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%                        [G] - a graph structure that is created to represent the network as a set of nodes and edges where nodes are the buses and edges are the lines
%                              This input can vary depending on how the network topology changes due to opening of CBs
%                [srcBusIdx] - a row vector containing the indecies of all source buses.
% [Disconnected_Src_Line_ID] - as the name suggests, it is the index of the disconnected source line.
%                     [Line] - a 1*n vector of integer values representing line IDs.
%                      [Bus] - a 1*m vector of integer values representing bus IDs.
% 
% see also GenDeclaration1

if BusIdx == 0
    Faulted_Line_ID = Line(i);
    TerminalBus = Bus_connections(i,2); % the bus connected to the fault (the from bus for the disconnected line)
else
    Faulted_Bus_ID = Bus(BusIdx);
    TerminalBus = BusIdx;
end

Allpaths = [];
for kk = 1:length(srcBusIdx) % Find all the paths between the fault and all the source buses
    [path,edgepath] = allpaths(G,srcBusIdx(kk),TerminalBus);
    Allpaths = [Allpaths;path,edgepath];
end
[rA,~] = size(Allpaths);


%% Find the remaining lines if any
RemainingLines_G = Allpaths{1,2};
for pp = 2:rA
    RemainingLines_G = intersect(RemainingLines_G,Allpaths{pp,2},'stable');
end
RemainingLines = []; % vector of the correct line Idx for the remaining lines
for pp = 1:length(RemainingLines_G) % convert from the edge indexing into the line indexing
    RemainingLines = [RemainingLines G.Edges.LineIDs(RemainingLines_G(pp))];
end
%% update the remaining lines if any
if ~isempty(RemainingLines)
    for kk = 1:length(RemainingLines)
        Symb = Irc(RemainingLines(kk));
        if BusIdx == 0
            fprintf(fileID,'   %s = %s - Isc_%i_%i;\n',Symb,Symb,Disconnected_Src_Line_ID,Faulted_Line_ID);
        else
            fprintf(fileID,'   %s = %s - Iscb_%i_%i;\n',Symb,Symb,Disconnected_Src_Line_ID,Faulted_Bus_ID);
        end
    end
end


end