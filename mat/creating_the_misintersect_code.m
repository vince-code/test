%% This first part is done to create the graph within Matlab called G
LineIDs = [1:length(Line)]'; % vector of line IDs from 1 to r
LineIDs = table(LineIDs); % turning the vector into a table
EndNodes = Bus_connections; % vector of bus connections
Gtable = table(EndNodes); % turning the vector again into a table so it has the vector name as a title
Gtable = [Gtable LineIDs]; % adding the line IDs for later use
G = graph(Gtable); % creating the graph which contains a list of Edges and Nodes

%% 
[path,edgepath] = allpaths(G,7,14); % Finding all the path in G between the source node 7 and target node 14
edgepath{1,2} = [6,7,12,13];
edgepath{1,3} = [6,7,13,15];

rpath = size(edgepath,2);
dummy = 'mintersect(edgepath{1,1}';
if rpath>1
    for ppp = 2:rpath
        dummy = strcat(dummy,sprintf(',edgepath{1,%d}',ppp));
    end
    dummy = strcat(dummy,')');
end
commonLines = eval(dummy);
commonLinesID = [];
for kk = 1:length(commonLines)
    commonLinesID = [commonLinesID G.Edges.LineIDs(commonLines(kk))];
end
% by removing the src line IDs we get the linnk line IDs
idx_link_lines = commonLinesID;
idx_source = [6,12];
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    idx_link_lines(idx_link_lines==idx_source(pp))=[];
end