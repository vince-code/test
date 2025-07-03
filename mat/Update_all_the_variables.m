function [Anew,Gnew,IrcNew,Cnew,Bus_connectionsNew] = Update_all_the_variables(i,A,r,c,Line,Bus,Bus_connections,Irc,C,srcBusIdx,idx_source,idx_link_lines)
% 1) Updating the A matrix and calling it Anew
Anew = A;
Anew(r+1,c+1) = 0; % added a new empty row and colomn
newLineIdx = c+1;
newBusIdx = r+1;
newLineID = max(Line) + 1;
newBusID = max(Bus) + 1;
% modify the new line to reflect the new connections
Anew(newBusIdx,newLineIdx) = -1;
Anew(Bus_connections(i,2),newLineIdx) = 1;
% modify the old line to reflect the new connections
Anew(Bus_connections(i,2),i) = 0;
Anew(newBusIdx,i) = 1;

% 2) Updating the Bus, Line, Bus_connections variables
BusNew = [Bus,newBusID];
LineNew = [Line,newLineID];
Bus_connectionsNew = Bus_connections;
Bus_connectionsNew(i,2) = newBusIdx;
Bus_connectionsNew(newLineIdx,:) = [newBusIdx,Bus_connections(i,2)];

% 3) Updating the Irc and C variables
newIrc = sprintf('Irc_%.0f',newLineID);
IrcNew = [Irc;newIrc];
newC = sprintf('C_%.0f',newLineID);
Cnew = [C;newC];

% 4) Recalculating the dependencies
global depNew;
depNew = cell(1,c+1);
for i = 1:c+1
    depNew{1,i} = FindDep(Anew,i);
end
for ii = 1:c+1
    depNew{1,ii} = unique(depNew{1,ii},'stable');
end
DepMatrixNew = zeros(c+1,c+1); % Every row of this matrix shows the dependcies of the index of the column that have 1 to the index of the row
for ii =1:c+1
    for jj = 1:length(depNew{1,ii})
        DepMatrixNew(depNew{1,ii}(jj),ii) = 1;
    end
end

% 5) Recalculating the link lines idx
LineIDs = [1:c+1]'; % vector of line IDs from 1 to c+1
LineIDs = table(LineIDs); % turning the vector into a table
EndNodes = Bus_connectionsNew; % vector of bus connections
GtableNew = table(EndNodes); % turning the vector again into a table so it has the vector name as a title
GtableNew = [GtableNew LineIDs]; % adding the line IDs for later use
Gnew = graph(GtableNew); % creating the graph which contains a list of Edges and Nodes
idx_link_linesNew = [];
for iii = 1:length(srcBusIdx)
    for jjj = 1:length(srcBusIdx)
        if iii~=jjj
            [~,edgepath] = allpaths(Gnew,srcBusIdx(iii),srcBusIdx(jjj)); % Finding all the path in G between the source node iii and target node jjj
            commonLines = edgepath{1,1};
            commonLinesID = [];
            for kk = 1:length(commonLines)
                commonLinesID = [commonLinesID Gnew.Edges.LineIDs(commonLines(kk))];
            end
            idx_link_linesNew = [idx_link_linesNew commonLinesID];
        end
    end
end
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    idx_link_linesNew(idx_link_linesNew==idx_source(pp))=[];
end
idx_link_linesNew = unique(idx_link_linesNew,"stable");

% 6) recalculating multSrx_cols
multSrx_rowsNew = []; % index of the intermediate buses that have more than one incoming line
multSrx_colsNew =[]; % this is a matrix where each row has the index of the all the lines that are incoming on specific bus
for iii = 1:r+1
    if nnz(Anew(iii,:)) == 1
        if sum(Anew(iii,:))==1
        elseif sum(Anew(iii,:))== -1
        end
    else
        if length(find(Anew(iii,:)==1)) == 1
        else
            multSrx_rowsNew = [multSrx_rowsNew i];
        end
    end
end
for kk = 1:length(multSrx_rowsNew)
    multSrx_colsNew = [multSrx_colsNew; find(A(multSrx_rowsNew(kk),:)== 1)];
end
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    multSrx_colsNew(multSrx_colsNew==idx_source(pp))=[];
end
for pp = 1:length(idx_link_lines) % Removes the link lines index from the vector
    multSrx_colsNew(multSrx_colsNew==idx_link_lines(pp))=[];
end

end
