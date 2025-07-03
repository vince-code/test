    function [LinesCBs,F_branch] = GenDeclaration1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,fileID,Bus_connections,CB_settings)
% [LinesCBs,F_branch] = GenDeclaration1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,fileID,Bus_connections,CB_settings)
% Function GenDeclaration1 generates the entire global declaration section of the Uppaal code.
% Note: m is the number of buses and n is the number of lines.
% The input parameters are:
%                [A] - the m*n incidence matrix where m is the number of buses and n is the number of lines
%             [Line] - a 1*n vector of integer values representing line IDs
%              [Bus] - a 1*m vector of integer values representing bus IDs
%         [F_branch] - a vector of integer values representing line IDs that the user wants to see faults on (it is a subset of Line)
%            [F_bus] - a vector of integer values representing bus IDs that the user wants to see faults on (it is a subset of Bus)
%              [Irc] - a n*1 vector of symbols of Irc_i where i is the line index, Irc is the running current
%      [Iioc_values] - a 1*n vector of integer values of the currents in the intial operating conditions for each line
%       [Ith_values] - a 1*n vector of integer values of the threshold currents on each line
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
%      [CB_settings] - a structure that has all the CB settings
%  [Bus_connections] - a n*2 vector detailing for each line the from bus in column 1 and the to bus in column 2.
%                [C] - a n*1 vector of symbols of Ci where i is the line index, each symbol can have a value of 1 or 0, 1 if the CB on line i is open and 0 if the CB on line i is closed
%           [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
% 
% The output parameters are:
%         [LinesCBs] - a n*2 cell array where for each row column 1 contains the Line ID and column 2 contains all the CB IDs present on that line.
%         [F_branch] - a vector of integer values representing line IDs that the user wants to see faults on, this is not the same as input F_branch because this function modifies it
% 
% see also GenAutomata1

[r, c]=size(A); % the rows (r) refer to the buses while the colomns (c) refer to the lines
fprintf(fileID,"<declaration>\n");
%% Check inputs for error
if length(Line) ~= c
    %     error("The number of elements in ""Line"" must be equal to the number of columns of A")
    errordlg("The number of elements in ""Line"" must be equal to the number of columns of A","Incompatible sizes")
end
%% Channel Declaration
% This is where we define the channels that are used to synchronize the various timed automata interactions
fprintf(fileID,"broadcast chan L%i_fault",Line(1));
for k = 2:length(Line)
    fprintf(fileID,", L%i_fault",Line(k));
end
fprintf(fileID,";\nbroadcast chan LB%i_fault",Bus(1));
for k = 2:length(Bus)
    fprintf(fileID,", LB%i_fault",Bus(k));
end
fprintf(fileID,";\nbroadcast chan CBopen, Faults, Reset, Fault_cleared, Close;\n\n");
% fprintf(fileID,";\nbroadcast chan CBopen, Faults, Reset;\n\n");
%% Finding the loads and the sources and other variables
idx_load = []; % index of all the load lines(terminals/antennas)(colomn)
loadBusIdx = []; % index of all the load buses(terminals/antennas)(rows)
idx_source = []; % index of all source lines(colomn)
srcBusIdx = []; % index of all source buses(row)
idx_link_lines = []; % index of all the lines that link the buses in a ring network 13
KCL_rows = []; % index of the intermediate buses that have only one incoming line
multSrx_rows = []; % index of the intermediate buses that have more than one incoming line
multSrx_cols =[]; % this is a matrix where each row has the index of the all the lines that are incoming on specific bus

for i = 1:r
    if nnz(A(i,:)) == 1
        if sum(A(i,:))==1
            idx_load = [idx_load find(A(i,:))];
            loadBusIdx = [loadBusIdx i];
        elseif sum(A(i,:))== -1
            idx_source = [idx_source find(A(i,:))];
            srcBusIdx = [srcBusIdx i];
        end
    else
        if length(find(A(i,:)==1)) == 1
            KCL_rows = [KCL_rows i];
        else
            multSrx_rows = [multSrx_rows i];
        end
    end
end

% This first part is done to create the graph within Matlab called G
LineIDs = [1:c]'; % vector of line IDs from 1 to r
LineIDs = table(LineIDs); % turning the vector into a table
EndNodes = Bus_connections; % vector of bus connections
Gtable = table(EndNodes); % turning the vector again into a table so it has the vector name as a title
Gtable = [Gtable LineIDs]; % adding the line IDs for later use
G = graph(Gtable); % creating the graph which contains a list of Edges and Nodes

for iii = 1:length(srcBusIdx)
    for jjj = 1:length(srcBusIdx)
        if iii~=jjj
            [~,edgepath] = allpaths(G,srcBusIdx(iii),srcBusIdx(jjj)); % Finding all the path in G between the source node iii and target node jjj
            commonLines = edgepath{1,1};
            commonLinesID = [];
            for kk = 1:length(commonLines)
                commonLinesID = [commonLinesID G.Edges.LineIDs(commonLines(kk))];
            end
            idx_link_lines = [idx_link_lines commonLinesID];
        end
    end
end
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    idx_link_lines(idx_link_lines==idx_source(pp))=[];
end
idx_link_lines = unique(idx_link_lines,"stable");


for kk = 1:length(multSrx_rows)
    multSrx_cols = [multSrx_cols, find(A(multSrx_rows(kk),:)== 1)];
end
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    multSrx_cols(multSrx_cols==idx_source(pp))=[];
end
for pp = 1:length(idx_link_lines) % Removes the link lines index from the vector
    multSrx_cols(multSrx_cols==idx_link_lines(pp))=[];
end
% in this next 3 lines of code the source bus is located and moved to the
% end of the vector so that when writing the equations later the source
% bus current is the last one to be calculated
if length(idx_source)>1
    for kk = 1:length(idx_source)
        srcRow = find(A(:,idx_source(kk))==1);
        KCL_rows(KCL_rows==srcRow)=[];
        KCL_rows(end + 1) = srcRow;
    end
else
    srcRow = find(A(:,idx_source)==1);
    KCL_rows(KCL_rows==srcRow)=[];
    KCL_rows(end + 1) = srcRow;
end

% Here I disable any faults on source or link lines by remnoving them from F_branch
for k = 1:length(idx_source)
    if ismember(idx_source(k),F_branch)
        F_branch(F_branch==idx_source(k)) = [];
    end
end
for k = 1:length(idx_link_lines)
    if ismember(idx_link_lines(k),F_branch)
        F_branch(F_branch==idx_link_lines(k)) = [];
    end
end


%% This is the variable linking the Line and CB IDs
LinesCBs = cell(c,2); % a cell array where each row contains the Line ID and all the CB IDs present on that line
CBsLines = zeros(length(CB_settings.CB),2); % a vector of two colomns where each row has the CB ID in Col 1 and Line ID in col 2
SelTypes = []; % vector of the selectivity types available in the system
for nn = 1:c
    LinesCBs{nn,1} = Line(nn);
end
for kk = 1:length(CB_settings.CB)
    LocInCA = find(Line==CB_settings.CB{1,kk}.Line_ID); % Location in the cell array
    LinesCBs{LocInCA,2} = [LinesCBs{LocInCA,2},CB_settings.CB{1,kk}.CB_ID];
    CBsLines(kk,1) = CB_settings.CB{1,kk}.CB_ID;
    CBsLines(kk,2) = CB_settings.CB{1,kk}.Line_ID;
    SelTypes = [SelTypes,CB_settings.CB{1,kk}.Sel_Type];
end
SelTypes = unique(SelTypes);

global Fault_Analysis_Line; % col one contains the ID of the faulted line, col 2 contains the ID of all affected/involved CB
global Fault_Analysis_Bus; % col one contains the index of the faulted Buses, col 2 contains the ID of all affected/involved CB
Fault_Analysis_Line = cell(length(F_branch),2);
Fault_Analysis_Bus = cell(length(F_bus),2);

%% Variable Intialization
% Iioc generation (Iioc intial operating conditions current)
for k = 1:length(Line)
    fprintf(fileID,"int Iioc_%i = %i;\n",Line(k),Iioc_values(k));
end
fprintf(fileID,"\n");
% Ith generation (Ith threshold current)
for k = 1:length(Line)
    fprintf(fileID,"int Ith_%i = %i;\n",Line(k),Ith_values(k));
end
fprintf(fileID,"\n");
% Isc generation (Isc means short circuit current)
for k = 1:length(idx_source)
    for kk = 1:c
        fprintf(fileID,"int Isc_%i_%i = %i;\n",Line(idx_source(k)),Line(kk),Isc_values_Lines(idx_source(k),kk));
    end
end
fprintf(fileID,"\n");
for k = 1:length(idx_source)
    for kk = 1:r
        fprintf(fileID,"int Iscb_%i_%i = %i;\n",Line(idx_source(k)),Bus(kk),Isc_values_Buses(idx_source(k),kk));
    end
end

fprintf(fileID,"\n");
% Assigning the running conditions current to the intial conditions value
for k = 1:length(Line)
    fprintf(fileID,"int %s = %i;\n",Irc(k),Iioc_values(k));
end
fprintf(fileID,"\n");

% F generation (F is the boolian variable that indicates a fault on its corresponding line)
for k = 1:length(Line)
    fprintf(fileID,"bool F%i = false;\n",Line(k));
end
fprintf(fileID,"\n");
for k = 1:length(Bus)
    fprintf(fileID,"bool FB%i = false;\n",Bus(k));
end
fprintf(fileID,"\n");
fprintf(fileID,"\nint F;\nint FB;\n\n");

% C is a variable that indicates if a CB is open or closed
for k = 1:length(Line)
    fprintf(fileID,"int C%i = 1;\n",Line(k));
end
fprintf(fileID,"\n");

%fprintf(fileID,"\nint n;\nint Idiff;\n\n");

% The IsFWD variable indicates the direction of the current
for k = 1:length(Line)
    fprintf(fileID,"bool IsFWD_%i = true;\n",Line(k));
end
fprintf(fileID,"\n");

% The Block variable indicates if the current CB is blocked by another CB or not
for k = 1:length(CB_settings.CB)
    fprintf(fileID,"bool Block_%i = false;\n",CB_settings.CB{1,k}.CB_ID);
end
fprintf(fileID,"\n");


%% Finding the Dependencies
% 
% global dep;
% dep = cell(1,c);
% for i = 1:c
%     dep{1,i} = FindDep(A,i);
% end
% for ii = 1:c
%     dep{1,ii} = unique(dep{1,ii},'stable');
% end
% DepMatrix = zeros(c,c); % Every row of this matrix shows the dependcies of the index of the column that have 1 to the index of the row
% for ii =1:c
%     for jj = 1:length(dep{1,ii})
%         DepMatrix(dep{1,ii}(jj),ii) = 1;
%     end
% end
%% Fault function generator Line Faults
% The Isc function is made of two parts, the first on is this one the Isc for line faults
% This is where we create the part of the Isc function related to the line faults
% Function declaration
fprintf(fileID,'void Isc(int L, int LB){'); % This is to write the beginning of the Isc function in the Uppaal format

% This first "if" condition outside the for loop is just to make the first line of the code start with an if instead of an else if

% Here we split the Isc generation into two possibilities 1 if the network is radial or Open ring and 2 if the network is closed ring

% Closed ring network Isc
if hascycles(G)==1 % if the network have a loop then it is a closed ring
    fprintf(fileID,'\n  if (L==%i){\n',Line(F_branch(1)));
    fprintf(fileID,'   F = %i;\n',Line(F_branch(1)));
    for kk = 1:length(idx_source)
        fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(F_branch(1))); % Assign the source lines to the SC current
    end
    % update all line currents based on the closed ring algorithem created in the following function
    update_all_lines_closed_ring(1,0,F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,idx_source)
    for i = 2:length(F_branch) % this part is made because all lines starting from the second line will take "else if" while the first one started with an "if"
        if any(F_branch(i) == idx_source) % For a fault on one of the source lines
            fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
        else                     % For a fault on any other line
            fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
            fprintf(fileID,'   F = %i;\n',Line(F_branch(i)));
            for kk = 1:length(idx_source)
                fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(F_branch(i))); % Assign the source lines to the SC current
            end
            update_all_lines_closed_ring(i,0,F_branch(i),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,idx_source);
        end
    end
% The network is Radial or Open ring
else 
    if any(F_branch(1) == idx_source) % For a fault on one of the source lines
        %     fprintf(fileID,'\n  if (L==%i){\n',Line(F_branch(1)));
    else                     % For a fault on any other line
        fprintf(fileID,'\n  if (L==%i){\n',Line(F_branch(1)));
        fprintf(fileID,'   F = %i;\n',Line(F_branch(1)));
        for kk = 1:length(idx_source)
            fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(F_branch(1))); % Assign the source lines to the SC current
        end
        % update all line currents based on the radial/open ring algorithem created in the following function
        update_all_lines(1,0,F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes)
    end
    for i = 2:length(F_branch)
        if any(F_branch(i) == idx_source) % For a fault on one of the source lines
            fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
        else                     % For a fault on any other line
            fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
            fprintf(fileID,'   F = %i;\n',Line(F_branch(i)));
            for kk = 1:length(idx_source)
                fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(F_branch(i))); % Assign the source lines to the SC current
            end
            update_all_lines(i,0,F_branch(i),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes);
        end
    end
end

%% Fault function generator Bus faults
% This entire section is basically the same and the previous one except it does the Isc for the bus faults
if hascycles(G)==1
    for i = 1:length(F_bus)
        fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
        fprintf(fileID,'   FB = %i;\n',Bus(F_bus(i)));
        srcBusIdx1 = srcBusIdx;
        idx_source1 = idx_source;
        if ismember(F_bus(i),srcBusIdx)
            srcBusIdx1(F_bus(i)==srcBusIdx) = [];
            srcLineIdx = find(A(F_bus(i),:)==-1);
            idx_source1(idx_source1==srcLineIdx) = [];
            for kk = 1:length(idx_source1)
                fprintf(fileID,'   %s = Iscb_%i_%i;\n',Irc(idx_source1(kk)),Line(idx_source1(kk)),Bus(F_bus(i))); % Assign the source lines to the SC current except the faulted source
            end
            update_all_lines_closed_ring(0,i,0,F_bus(i),idx_source1,Bus_connections,Irc,fileID,A,G,srcBusIdx1,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,idx_source);
        else
            for kk = 1:length(idx_source)
                fprintf(fileID,'   %s = Iscb_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Bus(F_bus(i))); % Assign the source lines to the SC current
            end
            update_all_lines_closed_ring(0,i,0,F_bus(i),idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes,Isc_values_Lines,Isc_values_Buses,idx_source);
        end
    end
else
    for i = 1:length(F_bus)
        fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
        fprintf(fileID,'   FB = %i;\n',Bus(F_bus(i)));
        srcBusIdx1 = srcBusIdx;
        idx_source1 = idx_source;
        if ismember(F_bus(i),srcBusIdx)
            srcBusIdx1(F_bus(i)==srcBusIdx) = [];
            srcLineIdx = find(A(F_bus(i),:)==-1);
            idx_source1(idx_source1==srcLineIdx) = [];
            for kk = 1:length(idx_source1)
                fprintf(fileID,'   %s = Iscb_%i_%i;\n',Irc(idx_source1(kk)),Line(idx_source1(kk)),Bus(F_bus(i))); % Assign the source lines to the SC current except the faulted source
            end
            update_all_lines(0,i,0,F_bus(i),idx_source1,Bus_connections,Irc,fileID,A,G,srcBusIdx1,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes);
        else
            for kk = 1:length(idx_source)
                fprintf(fileID,'   %s = Iscb_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Bus(F_bus(i))); % Assign the source lines to the SC current
            end
            update_all_lines(0,i,0,F_bus(i),idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line,Bus,LinesCBs,CB_settings,CBsLines,SelTypes);
        end
    end
end
fprintf(fileID,'  }\n}\n');

%% Update Function Generator
% This the function that is called each time a CB opens, its job is to update the magnitude and direction of currents to reflect the opening of the selected CB
fprintf(fileID,'void update(int CB_ID){'); % This is to write the beginning of the update function in the Uppaal format

fprintf(fileID,'\n if (F==%i){',Line(F_branch(1)));

% This first if condition outside the for loop is just to make the first
% line of the code start with an if instead of an else if
Line_Idx = 1;
Line_ID = Line(Line_Idx);
Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
CB_ID = CB_settings.CB{1,Idx}.CB_ID;

fprintf(fileID,'\n  if (CB_ID==%i){\n',CB_ID);
fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
Anew = A;
Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

% this part checks if there is still a connection between the faulted
% line and any of the sources
incidentBusIdx = Bus_connections(F_branch(1),2); % find the faulted bus index
Connection2src = [];
Disconn_Src = [];
for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
    [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
    if isempty(edgepath2)
        Disconn_Src = [Disconn_Src,srcBusIdx(kk)];
    end
    Connection2src = [Connection2src;edgepath2];
end
if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
    for k = 1:length(Line)
        fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
    end
else
    fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
    if ismember(CB_ID,Fault_Analysis_Line{1,2})
        if hascycles(G) % if it is a closed ring network
            if ismember(Line_Idx,idx_source)
                Disconnected_Src_Line_ID = Line_ID;
                update_source_lines_closed_ring(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
            elseif ismember(Line_Idx,idx_link_lines)
                update_link_lines_closed_ring(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C);
                update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
            else
                fprintf(fileID,"  %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
                update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
            end
        else
            fprintf(fileID,"  %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
            for ppp = 1:length(Disconn_Src)
                Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
            end
            update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
        end
    end
end

for i = 2:c
    Line_Idx = i;
    Line_ID = Line(Line_Idx);
    Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
    for pp = 1:length(Idx)
        CB_ID = CB_settings.CB{1,Idx(pp)}.CB_ID;
        fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',CB_ID);
        fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
        ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
        Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
        Anew = A;
        Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

        % this part checks if there is still a connection between the faulted
        % line and any of the sources
        incidentBusIdx = Bus_connections(F_branch(1),2); % find the LineIdxfaulted bus index
        Connection2src = [];
        Disconn_Src = [];
        for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
            [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
            if isempty(edgepath2)
                Disconn_Src = [Disconn_Src,srcBusIdx(kk)];
            end
            Connection2src = [Connection2src;edgepath2];
        end
        if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
            for k = 1:length(Line)
                fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
            end
        else
            fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
            if ismember(CB_ID,Fault_Analysis_Line{1,2})
                if hascycles(G) % if it is a closed ring network
                    if ismember(Line_Idx,idx_source)
                        Disconnected_Src_Line_ID = Line_ID;
                        update_source_lines_closed_ring(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
                    elseif ismember(Line_Idx,idx_link_lines)
                        update_link_lines_closed_ring(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C);
                        update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                    else
                        update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                    end
                else
                    for ppp = 1:length(Disconn_Src)
                        Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                        Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                        fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
                    end
                    update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                end
            end
        end
    end
end
fprintf(fileID,'  }\n }\n');

for tt = 2:length(F_branch)
    fprintf(fileID,'\n else if (F==%i){',Line(F_branch(tt)));
    Line_Idx = 1;
    Line_ID = Line(Line_Idx);
    Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
    CB_ID = CB_settings.CB{1,Idx}.CB_ID;
    % This first if condition outside the for loop is just to make the first
    % line of the code start with an if instead of an else if
    fprintf(fileID,'\n  if (CB_ID==%i){\n',CB_ID);
    fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
    ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
    Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
    Anew = A;
    Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

    % this part checks if there is still a connection between the faulted
    % line and any of the sources
    incidentBusIdx = Bus_connections(F_branch(tt),2); % find the faulted bus index
    Connection2src = [];
    Disconn_Src = [];
    for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
        if isempty(edgepath2)
            Disconn_Src = [Disconn_Src,srcBusIdx(kk)];
        end
        Connection2src = [Connection2src;edgepath2];
    end
    if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
        for k = 1:length(Line)
            fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
        end
    else
        fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
        if ismember(CB_ID,Fault_Analysis_Line{tt,2})
            if hascycles(G) % if it is a closed ring network
                if ismember(Line_Idx,idx_source)
                    Disconnected_Src_Line_ID = Line_ID;
                    update_source_lines_closed_ring(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
                elseif ismember(Line_Idx,idx_link_lines)
                    update_link_lines_closed_ring(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C);
                    update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                else
                    update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                end
            else
                for ppp = 1:length(Disconn_Src)
                    Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                    Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                    fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
                end
                update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
            end
        end
    end

    for i = 2:c
        Line_Idx = i;
        Line_ID = Line(Line_Idx);
        Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
        for pp = 1:length(Idx)
            CB_ID = CB_settings.CB{1,Idx(pp)}.CB_ID;
            fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',CB_ID);
            fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
            ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
            Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
            Anew = A;
            Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

            % this part checks if there is still a connection between the faulted
            % line and any of the sources
            incidentBusIdx = Bus_connections(F_branch(tt),2); % find the faulted bus index
            Connection2src = [];
            Disconn_Src = [];
            for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
                [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
                if isempty(edgepath2)
                    Disconn_Src = [Disconn_Src,srcBusIdx(kk)];
                end
                Connection2src = [Connection2src;edgepath2];
            end
            if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
                for k = 1:length(Line)
                    fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
                end
            else
                fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
                if ismember(CB_ID,Fault_Analysis_Line{tt,2})
                    if hascycles(G) % if it is a closed ring network
                        if ismember(Line_Idx,idx_source)
                            Disconnected_Src_Line_ID = Line_ID;
                            update_source_lines_closed_ring(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
                        elseif ismember(Line_Idx,idx_link_lines)
                            update_link_lines_closed_ring(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines,Line_Idx,Gnew,C);
                            update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                        else
                            update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                        end
                    else
                        for ppp = 1:length(Disconn_Src)
                            Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                            Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                            fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
                        end
                        update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
                    end
                end
            end
        end
    end
    fprintf(fileID,'  }\n }\n');
end
% Now for a fault on the Buses
for ttt = 1:length(F_bus)
    srcBusIdx1 = srcBusIdx;
    idx_source2 = idx_source;
    if ismember(F_bus(ttt),srcBusIdx)
        srcBusIdx1(srcBusIdx1==F_bus(ttt)) = [];
        idxx = find(srcBusIdx == F_bus(ttt));
        idx_source2(idxx) = [];
    end
    fprintf(fileID,'\n else if (FB==%i){',Bus(F_bus(ttt)));
    Line_Idx = 1;
    Line_ID = Line(Line_Idx);
    Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
    CB_ID = CB_settings.CB{1,Idx}.CB_ID;
    % This first if condition outside the for loop is just to make the first
    % line of the code start with an if instead of an else if
    fprintf(fileID,'\n  if (CB_ID==%i){\n',CB_ID);
    fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
    ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
    Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
    Anew = A;
    Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

    % this part checks if there is still a connection between the faulted
    % line and any of the sources
    incidentBusIdx = F_bus(ttt); % find the faulted bus index
    Connection2src = [];
    Disconn_Src = [];
    for kk = 1:length(srcBusIdx1) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx1(kk));
        if isempty(edgepath2)
            Disconn_Src = [Disconn_Src,srcBusIdx1(kk)];
        end
        Connection2src = [Connection2src;edgepath2];
    end
    if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
        for k = 1:length(Line)
            fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
        end
    else
        fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
        if ismember(CB_ID,Fault_Analysis_Bus{ttt,2})
            if hascycles(G) % if it is a closed ring network
                if ismember(Line_Idx,idx_source2)
                    Disconnected_Src_Line_ID = Line_ID;
                    update_source_lines_closed_ring(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
                elseif ismember(Line_Idx,idx_link_lines)
                    update_link_lines_closed_ring(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,A,G,srcBusIdx1,idx_link_lines,Line_Idx,Gnew,C);
                    update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
                else
                    update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
                end
            else
                for ppp = 1:length(Disconn_Src)
                    Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                    Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                    fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
                end
                update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
            end
        end
    end

    for i = 2:c
        Line_Idx = i;
        Line_ID = Line(Line_Idx);
        Idx = find(cellfun(@(x) x.Line_ID==Line_ID, CB_settings.CB));
        for pp = 1:length(Idx)
            CB_ID = CB_settings.CB{1,Idx(pp)}.CB_ID;
            fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',CB_ID);
            fprintf(fileID,'   %s = 0;\n',C(Line_Idx)); % Assign the variable "C" corresponding to that line to zero
            ind = find(G.Edges.LineIDs==Line_Idx); % finding the index of the line inside the graph from the Line ID
            Gnew = rmedge(G,ind); % remove the opened line from the graph G and save it in a new variable Gnew
            Anew = A;
            Anew(:,Line_Idx) = 0; % remove the opened line from the incidence matrix

            % this part checks if there is still a connection between the faulted
            % line and any of the sources
            incidentBusIdx = F_bus(ttt); % find the faulted bus index
            Connection2src = [];
            Disconn_Src = [];
            for kk = 1:length(srcBusIdx1) % find -if any- the path between the faulted bus and every source
                [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx1(kk));
                if isempty(edgepath2)
                    Disconn_Src = [Disconn_Src,srcBusIdx1(kk)];
                end
                Connection2src = [Connection2src;edgepath2];
            end
            if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
                for k = 1:length(Line)
                    fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
                end
            else
                fprintf(fileID,"   %s = 0;\n",Irc(Line_Idx));% Assign only the opened line to zero
                if ismember(CB_ID,Fault_Analysis_Bus{ttt,2})
                    if hascycles(G) % if it is a closed ring network
                        if ismember(Line_Idx,idx_source2)
                            Disconnected_Src_Line_ID = Line_ID;
                            update_source_lines_closed_ring(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines,Disconnected_Src_Line_ID,Line,Bus);
                        elseif ismember(Line_Idx,idx_link_lines)
                            update_link_lines_closed_ring(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,A,G,srcBusIdx1,idx_link_lines,Line_Idx,Gnew,C);
                            update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
                        else
                            update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
                        end
                    else
                        for ppp = 1:length(Disconn_Src)
                            Disconnected_Source_Bus_Index = Disconn_Src(ppp);
                            Source_Line_Index = find(Bus_connections(:,1)==Disconnected_Source_Bus_Index);
                            fprintf(fileID,"   %s = 0;\n",Irc(Source_Line_Index));% Assign the dissconnected source line connection to zero
                        end
                        update_affected_lines(0,F_bus(ttt),idx_source2,Bus_connections,Irc,fileID,Anew,Gnew,srcBusIdx1,idx_link_lines);
                    end
                end
            end
        end
    end
    fprintf(fileID,'  }\n }\n');
end

fprintf(fileID,'}\n');

%% Clear Function Generator
% This function just resets the network by setting all line currents to their origninal magnitude and direction before any fault happened
fprintf(fileID,'void clear (){\n');
for k = 1:length(Line)
    fprintf(fileID,"  %s = Iioc_%i;\n",Irc(k),Line(k));
end
for k = 1:length(Line)
    fprintf(fileID,"  IsFWD_%i = true;\n",Line(k));
end
for k = 1:length(CB_settings.CB)
    fprintf(fileID,"  Block_%i = false;\n",CB_settings.CB{1,k}.CB_ID);
end
fprintf(fileID,'}\n</declaration>\n');
end