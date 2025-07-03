function [idx_source,dep,srcBusIdx] = GenDeclaration(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,fileID,Bus_connections)
[r, c]=size(A);
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
fprintf(fileID,";\nbroadcast chan CBopen, Faults, Reset, Fault_cleared;\n\n");
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
        fprintf(fileID,"int Isc_%i_%i = %i;\n",Line(idx_source(k)),Line(kk),Isc_values(idx_source(k),kk));
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

for k = 1:length(Line)
    fprintf(fileID,"int C%i = 1;\n",Line(k));
end
fprintf(fileID,"\nint n;\nint Idiff;\n\n");


%% Finding the Dependencies

global dep;
dep = cell(1,c);
for i = 1:c
    dep{1,i} = FindDep(A,i);
end
for ii = 1:c
    dep{1,ii} = unique(dep{1,ii},'stable');
end
DepMatrix = zeros(c,c); % Every row of this matrix shows the dependcies of the index of the column that have 1 to the index of the row
for ii =1:c
    for jj = 1:length(dep{1,ii})
        DepMatrix(dep{1,ii}(jj),ii) = 1;
    end
end
%% Fault function generator
% Function declaration
fprintf(fileID,'void Isc(int L, int LB){'); % This is to write the beginning of the Isc function in the Uppaal format

% This first "if" condition outside the for loop is just to make the first
% line of the code start with an if instead of an else if

if any(F_branch(1) == idx_source) % For a fault on one of the source lines
    fprintf(fileID,'\n  if (L==%i){\n',Line(F_branch(1)));
    %[Anew,Gnew,IrcNew,Cnew,Bus_connectionsNew] = Update_all_the_variables(i,A,r,c,Line,Bus,Bus_connections,Irc,C,srcBusIdx,idx_source,idx_link_lines);
    %     for kk = 1:length(idx_source)
    %         fprintf(fileID,'   %s = Isc_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk))); % Assign the source lines to the SC current
    %     end
    %update_all_lines(1,0,idx_source,Bus_connectionsNew,IrcNew,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
    %update_all_lines(1,0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
else                     % For a fault on any other line
    fprintf(fileID,'\n  if (L==%i){\n',Line(F_branch(1)));
    %     [affected_lines] = set2zero(1,idx_source,idx_link_lines,Irc,dep,fileID); % set the unaffected lines to zero
    fprintf(fileID,'   F = %i;\n',Line(F_branch(1)));
    for kk = 1:length(idx_source)
        fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(1)); % Assign the source lines to the SC current
    end
    update_all_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines)
    %     if ~isempty(idx_link_lines)
    %         update_link_lines(1,affected_lines,idx_link_lines,idx_source,Bus_connections,Irc,fileID,A); % update the link lines current
    %     end
    %
    %     % in this next part I will find the lines affected by the fault and
    %     % update them one by one from upstream to downstream
    %     update_remaining_lines(1,affected_lines,idx_source,idx_link_lines,A,Irc,fileID,multSrx_cols,C);
end


for i = 2:length(F_branch)
    if any(F_branch(i) == idx_source) % For a fault on one of the source lines
        fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
        %[Anew,Gnew,IrcNew,~,Bus_connectionsNew] = Update_all_the_variables(i,A,r,c,Line,Bus,Bus_connections,Irc,C,srcBusIdx,idx_source,idx_link_lines);
        %         for kk = 1:length(idx_source)
        %             fprintf(fileID,'   %s = Isc_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk))); % Assign the source lines to the SC current
        %         end
        %update_all_lines(i,0,idx_source,Bus_connectionsNew,IrcNew,fileID,Anew,Gnew,srcBusIdx,idx_link_lines);
        %update_all_lines(i,0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
        %         % now we use the same code from the Bus fault section
        %         DepLinesNew = depNew{1,i}; % Find all dependent lines
        %         DepLinesNew = unique(DepLinesNew,"stable"); % remove duplicates if present
        %         affected_linesNew = DepLinesNew;
        %         DepLinesNew = [i,DepLinesNew]; % add the lines themselves
        %         all_linesNew = 1:1:length(IrcNew);
        %         % vector of indicies of all unaffected lines
        %         unaffected_linesNew = all_linesNew;
        %         for pp = 1:length(DepLinesNew) % Removes the affected lines index from the vector
        %             unaffected_linesNew(unaffected_linesNew==DepLinesNew(pp))=[];
        %         end
        %         for pp = 1:length(idx_source) % Removes the source lines index from the vector
        %             unaffected_linesNew(unaffected_linesNew==idx_source(pp))=[];
        %         end
        %         for pp = 1:length(idx_link_linesNew) % Removes the link lines index from the vector
        %             unaffected_linesNew(unaffected_linesNew==idx_link_linesNew(pp))=[];
        %         end
        %         for kk = 1:length(unaffected_linesNew)% Assign the selected lines to zero
        %             fprintf(fileID,'   %s = 0;\n',Irc(unaffected_linesNew(kk)));
        %         end
        %         for kk = 1:length(idx_source)% Assign the source lines to the SC current
        %             fprintf(fileID,'   %s = Isc_%i;\n',IrcNew(idx_source(kk)),LineNew(idx_source(kk)));
        %         end
        %         if ~isempty(idx_link_linesNew)% update link lines current
        %             update_link_lines(i,affected_linesNew,idx_link_linesNew,idx_source,Bus_connectionsNew,IrcNew,fileID,Anew);
        %         end
        %         update_remaining_lines_busFault(i,affected_linesNew,idx_source,idx_link_linesNew,Anew,IrcNew,fileID,multSrx_colsNew,CNew);
        %         set2zero(i,idx_source,idx_link_lines,Irc,dep,fileID);
    else                     % For a fault on any other line
        fprintf(fileID,'  }\n  else if (L==%i){\n',Line(F_branch(i)));
        fprintf(fileID,'   F = %i;\n',Line(F_branch(i)));
        for kk = 1:length(idx_source)
            fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(F_branch(i))); % Assign the source lines to the SC current
        end
        update_all_lines(F_branch(i),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
        %         [affected_lines] = set2zero(i,idx_source,idx_link_lines,Irc,dep,fileID);
        %         for kk = 1:length(idx_source)
        %             fprintf(fileID,'   %s = Isc_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk))); % Assign the source lines to the SC current
        %         end
        %         % update the link lines current
        %         if ~isempty(idx_link_lines)
        %             update_link_lines(i,affected_lines,idx_link_lines,idx_source,Bus_connections,Irc,fileID,A);
        %         end
        %         % in this next part I will find the lines affected by the fault and
        %         % update them one by one from upstream to downstream
        %         update_remaining_lines(i,affected_lines,idx_source,idx_link_lines,A,Irc,fileID,multSrx_cols,C);
    end
end

%% Bus faults
% for i = 1:length(F_bus)
%     if any(F_bus(i) == loadBusIdx) % For a fault on one of the load buses
%         fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
%         x = find(A(F_bus(i),:));% index of the line incoming on the faulted bus (load line index)
%         [affected_lines] = set2zero(x,idx_source,idx_link_lines,Irc,dep,fileID);
%         for kk = 1:length(idx_source)
%             fprintf(fileID,'   %s = Isc_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk))); % Assign the source lines to the SC current
%         end
%         % update the link lines current
%         if ~isempty(idx_link_lines)
%             update_link_lines(i,affected_lines,idx_link_lines,idx_source,Bus_connections,Irc,fileID,A);
%         end
%         update_remaining_lines(i,affected_lines,idx_source,idx_link_lines,A,Irc,fileID,multSrx_cols,C);
%     elseif any(F_bus(i) == srcBusIdx) % For a fault on one of the source buses
%         x = find(A(F_bus(i),:));
%         fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
%         fprintf(fileID,'   %s = Isc_%i;\n',Irc(x),Line(x));% Assign the current corresponding to that line the SC current
%         for ii = 1:length(Irc) % Assign all other currents to zero
%             if ii ~= x % To ignore the source current that was already assigned to Isc
%                 fprintf(fileID,'   %s = 0;\n',Irc(ii));
%             end
%         end
%     else                     % For a fault on one of the intermediate
%         fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
%         x = find(A(F_bus(i),:)==1); % index of all branches that are incoming on bus i
%         DepLines = [];
%         for pp = 1:length(x)
%             DepLines = [DepLines,dep{1,x(pp)}]; % Add all dependent lines
%         end
%         DepLines = unique(DepLines,"stable"); % remove duplicates if present
%         affected_lines = DepLines;
%         DepLines = [x,DepLines]; % add the lines themselves
%         all_lines = 1:1:length(Irc);
%         % vector of indicies of all unaffected lines
%         unaffected_lines = all_lines;
%         for pp = 1:length(DepLines) % Removes the affected lines index from the vector
%             unaffected_lines(unaffected_lines==DepLines(pp))=[];
%         end
%         for pp = 1:length(idx_source) % Removes the source lines index from the vector
%             unaffected_lines(unaffected_lines==idx_source(pp))=[];
%         end
%         for pp = 1:length(idx_link_lines) % Removes the link lines index from the vector
%             unaffected_lines(unaffected_lines==idx_link_lines(pp))=[];
%         end
%         for kk = 1:length(unaffected_lines)% Assign the selected lines to zero
%             fprintf(fileID,'   %s = 0;\n',Irc(unaffected_lines(kk)));
%         end
%         for kk = 1:length(idx_source)% Assign the source lines to the SC current
%             fprintf(fileID,'   %s = Isc_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)));
%         end
%         if ~isempty(idx_link_lines)% update link lines current
%             % update_link_lines(x,affected_lines,idx_link_lines,idx_source,Bus_connections,Irc,fileID,A);
%         end
%         update_remaining_lines_busFault(x,affected_lines,idx_source,idx_link_lines,A,Irc,fileID,multSrx_cols,C);
%     end
% end
% fprintf(fileID,'  }\n}\n');
for i = 1:length(F_bus)
    x = find(A(i,:)==1);
    fprintf(fileID,'  }\n  else if (LB==%i){\n',Bus(F_bus(i)));
    fprintf(fileID,'   FB = %i;\n',Bus(F_bus(i)));
    for kk = 1:length(idx_source)
        fprintf(fileID,'   %s = Isc_%i_%i;\n',Irc(idx_source(kk)),Line(idx_source(kk)),Line(x)); % Assign the source lines to the SC current
    end
    update_all_lines(0,F_bus(i),idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
end
fprintf(fileID,'  }\n}\n');

%% Update Function Generator
% fprintf(fileID,'void update(int CB_ID){'); % This is to write the beginning of the update function in the Uppaal format
% % This first if condition outside the for loop is just to make the first
% % line of the code start with an if instead of an else if
%
% if ismember(1,idx_load) % For a CB opening on one of the load lines
%     fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
%     fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
%     fprintf(fileID,'   %s = 0;\n',Irc(1)); % Assign the current corresponding to that line to zero
%     [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,1,dep,A);
%     UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
% elseif ismember(1,idx_source) % For a CB opening on one of the source lines
%     fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
%     fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
%     for ii = 1:length(Irc) % Assign all currents to zero
%         fprintf(fileID,'   %s = 0;\n',Irc(ii));
%     end
% elseif ismember(1,multSrx_cols) % For a CB opening on one of the intermediate lines that is not the only one incoming
%     [rMult,~] = find(multSrx_cols==1);% index of which row in multSrx_cols is our line located
%     multSrx_cols3 = multSrx_cols(rMult,:);
%     remove = find(A(:,1)==1); % index of the bus where this line is incident
%     downIdx2 = find(A(remove,:)==-1); % index of the outgoing lines for that bus
%     sumDownIdx = sum(Irc(downIdx2));
%     fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(1));
%     fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
%     N = sum(C(multSrx_cols3));
%     fprintf(fileID,'   n = %s;\n',N);
%     fprintf(fileID,'   if(FB%i==true){\n',Bus(remove));
%     fprintf(fileID,'    Idiff = %s;\n',Irc(1)); % Assign the value of the current before the CB opening to a variable for later use
%     fprintf(fileID,'    %s = 0;\n',Irc(1)); % Assign the current corresponding to that line to zero
%     fprintf(fileID,'    if (n&gt;0){\n');
%     for pp = 1:length(multSrx_cols3)
%         fprintf(fileID,'     %s += %s*(Idiff/n);\n',Irc(multSrx_cols3(pp)),C(multSrx_cols3(pp)));
%     end
%     [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,1,dep,A);
%     multSrx_rows2(multSrx_rows2==remove) = []; % remove the equation containing the opened line from the list of equations to be excuted so as not to override the previous code
%     UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%     fprintf(fileID,'    }\n    else{\n');
%     for pp = 1:length(multSrx_cols3)
%         fprintf(fileID,'     %s = 0;\n',Irc(multSrx_cols3(pp)));
%     end
%     UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%     fprintf(fileID,'    }\n   }\n   else{\n');
%     fprintf(fileID,'    %s = 0;\n',Irc(1)); % Assign the current corresponding to that line to zero
%     multSrx_cols2 = multSrx_cols3;
%     multSrx_cols2(multSrx_cols2==1)=[];
%     for pp = 1:length(multSrx_cols2)
%         fprintf(fileID,'    %s = %s*((%s)/n);\n',Irc(multSrx_cols2(pp)),C(multSrx_cols2(pp)),sumDownIdx);
%     end
%     UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%     fprintf(fileID,'   }\n');
% else                     % For a CB opening on one of the intermediate lines
%     fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
%     fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
%     downIdx = find(DepMatrix(1,:));
%     for j = 1:length(downIdx) % assign all downstream lines to zero
%         fprintf(fileID,'   %s = 0;\n',Irc(downIdx(j)));
%     end
%     fprintf(fileID,'   %s = 0;\n',Irc(1));
%     [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,1,dep,A);
%     UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
% end
%
% for i = 2:c
%     if ismember(i,idx_load) % For a CB opening on one of the load lines
%         fprintf(fileID,'   }\n  else if (CB_ID==%i){\n',Line(i));
%         fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
%         fprintf(fileID,'   %s = 0;\n',Irc(i)); % Assign the current corresponding to that line to zero
%         [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,i,dep,A);
%         UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%     elseif ismember(i,idx_source) % For a CB opening on one of the source lines
%         fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(i));
%         fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
%         for ii = 1:length(Irc) % Assign all currents to zero
%             fprintf(fileID,'   %s = 0;\n',Irc(ii));
%         end
%     elseif ismember(i,multSrx_cols) % For a CB opening on one of the intermediate lines that is not the only one incoming
%         [rMult,~] = find(multSrx_cols==i);% index of which row in multSrx_cols is our line located
%         multSrx_cols3 = multSrx_cols(rMult,:);
%         remove = find(A(:,i)==1); % index of the bus where this line is incident
%         downIdx2 = find(A(remove,:)==-1); % index of the outgoing lines for that bus
%         sumDownIdx = sum(Irc(downIdx2));
%         fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(i));
%         fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
%         N = sum(C(multSrx_cols3));
%         fprintf(fileID,'   n = %s;\n',N);
%         fprintf(fileID,'   if(FB%i==true){\n',Bus(remove));
%         fprintf(fileID,'    Idiff = %s;\n',Irc(i)); % Assign the value of the current before the CB opening to a variable for later use
%         fprintf(fileID,'    %s = 0;\n',Irc(i)); % Assign the current corresponding to that line to zero
%         fprintf(fileID,'    if (n&gt;0){\n');
%         for pp = 1:length(multSrx_cols3)
%             fprintf(fileID,'     %s += %s*(Idiff/n);\n',Irc(multSrx_cols3(pp)),C(multSrx_cols3(pp)));
%         end
%         [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,i,dep,A);
%         multSrx_rows2(multSrx_rows2==remove) = []; % remove the equation containing the opened line from the list of equations to be excuted so as not to override the previous code
%         UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%         fprintf(fileID,'    }\n    else{\n');
%         for pp = 1:length(multSrx_cols3)
%             fprintf(fileID,'     %s = 0;\n',Irc(multSrx_cols3(pp)));
%         end
%         UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%         fprintf(fileID,'    }\n   }\n   else{\n');
%         fprintf(fileID,'    %s = 0;\n',Irc(i)); % Assign the current corresponding to that line to zero
%         multSrx_cols2 = multSrx_cols3;
%         multSrx_cols2(multSrx_cols2==i)=[];
%         for pp = 1:length(multSrx_cols2)
%             fprintf(fileID,'    %s = %s*((%s)/n);\n',Irc(multSrx_cols2(pp)),C(multSrx_cols2(pp)),sumDownIdx);
%         end
%         UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%         fprintf(fileID,'   }\n');
%     else                     % For a CB opening on one of the intermediate lines that is the only one incoming
%         fprintf(fileID,'   }\n  else if (CB_ID==%i){\n',Line(i));
%         fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
%         downIdx = find(DepMatrix(i,:));
%         for j = 1:length(downIdx) % assign all downstream lines to zero
%             fprintf(fileID,'   %s = 0;\n',Irc(downIdx(j)));
%         end
%         fprintf(fileID,'   %s = 0;\n',Irc(i));
%         [KCL_rows2,multSrx_rows2,OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,i,dep,A);
%         UpdateDependentLines(KCL_rows2,multSrx_rows2,OrderedKCLrows,A,Irc,C,fileID);
%     end
% end
% fprintf(fileID,'  }\n }\n');

fprintf(fileID,'void update(int CB_ID){'); % This is to write the beginning of the update function in the Uppaal format

fprintf(fileID,'\n if (F==%i){',Line(F_branch(1)));

% This first if condition outside the for loop is just to make the first
% line of the code start with an if instead of an else if
fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
ind = find(G.Edges.LineIDs==1); % finding the index of the line inside the graph from the Line ID
Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

% this part checks if there is still a connection between the faulted
% line and any of the sources
incidentBusIdx = Bus_connections(F_branch(1),2); % find the faulted bus index
Connection2src = [];
for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
    [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
    Connection2src = [Connection2src,edgepath2];
end
if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
    for k = 1:length(Line)
        fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
    end
else
    fprintf(fileID,"  %s = 0;\n",Irc(1));% Assign only the opened line to zero
    update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
end

for i = 2:c
    fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(i));
    fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
    ind = find(G.Edges.LineIDs==i); % finding the index of the line inside the graph from the Line ID
    Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

    % this part checks if there is still a connection between the faulted
    % line and any of the sources
    incidentBusIdx = Bus_connections(F_branch(1),2); % find the faulted bus index
    Connection2src = [];
    for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
        Connection2src = [Connection2src,edgepath2];
    end
    if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
        for k = 1:length(Line)
            fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
        end
    else
        fprintf(fileID,"  %s = 0;\n",Irc(i));% Assign only the opened line to zero
        update_affected_lines(F_branch(1),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
    end

end
fprintf(fileID,'  }\n }\n');

for tt = 2:length(F_branch)
    fprintf(fileID,'\n else if (F==%i){',Line(F_branch(tt)));
    % This first if condition outside the for loop is just to make the first
    % line of the code start with an if instead of an else if
    fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
    fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
    ind = find(G.Edges.LineIDs==1); % finding the index of the line inside the graph from the Line ID
    Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

    % this part checks if there is still a connection between the faulted
    % line and any of the sources
    incidentBusIdx = Bus_connections(F_branch(tt),2); % find the faulted bus index
    Connection2src = [];
    for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
        Connection2src = [Connection2src,edgepath2];
    end
    if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
        for k = 1:length(Line)
            fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
        end
    else
        fprintf(fileID,"  %s = 0;\n",Irc(1));% Assign only the opened line to zero
        update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
    end

    for i = 2:c
        fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(i));
        fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
        ind = find(G.Edges.LineIDs==i); % finding the index of the line inside the graph from the Line ID
        Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

        % this part checks if there is still a connection between the faulted
        % line and any of the sources
        incidentBusIdx = Bus_connections(F_branch(tt),2); % find the faulted bus index
        Connection2src = [];
        for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
            [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
            Connection2src = [Connection2src,edgepath2];
        end
        if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
            for k = 1:length(Line)
                fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
            end
        else
            fprintf(fileID,"  %s = 0;\n",Irc(i));% Assign only the opened line to zero
            update_affected_lines(F_branch(tt),0,idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
        end

    end
    fprintf(fileID,'  }\n }\n');
end
% Now for a fault on the Buses
for ttt = 1:length(F_bus)
    fprintf(fileID,'\n else if (FB==%i){',Bus(F_bus(ttt)));
    % This first if condition outside the for loop is just to make the first
    % line of the code start with an if instead of an else if
    fprintf(fileID,'\n  if (CB_ID==%i){\n',Line(1));
    fprintf(fileID,'   %s = 0;\n',C(1)); % Assign the variable "C" corresponding to that line to zero
    ind = find(G.Edges.LineIDs==1); % finding the index of the line inside the graph from the Line ID
    Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

    % this part checks if there is still a connection between the faulted
    % line and any of the sources
    incidentBusIdx = F_bus(ttt); % find the faulted bus index
    Connection2src = [];
    for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
        [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
        Connection2src = [Connection2src,edgepath2];
    end
    if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
        for k = 1:length(Line)
            fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
        end
    else
        fprintf(fileID,"  %s = 0;\n",Irc(1));% Assign only the opened line to zero
        update_affected_lines(0,F_bus(ttt),idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
    end

    for i = 2:c
        fprintf(fileID,'  }\n  else if (CB_ID==%i){\n',Line(i));
        fprintf(fileID,'   %s = 0;\n',C(i)); % Assign the variable "C" corresponding to that line to zero
        ind = find(G.Edges.LineIDs==i); % finding the index of the line inside the graph from the Line ID
        Gnew = rmedge(G,ind); % remove the faulted line from the graph G and save it in a new variable Gnew

        % this part checks if there is still a connection between the faulted
        % line and any of the sources
        incidentBusIdx = F_bus(ttt); % find the faulted bus index
        Connection2src = [];
        for kk = 1:length(srcBusIdx) % find -if any- the path between the faulted bus and every source
            [~,edgepath2] = allpaths(Gnew,incidentBusIdx,srcBusIdx(kk));
            Connection2src = [Connection2src,edgepath2];
        end
        if isempty(Connection2src) % if this variable is empty it means that there is no connection between the disconnected bus and any of the source
            for k = 1:length(Line)
                fprintf(fileID,"  %s = 0;\n",Irc(k)); % assign all the currents to a value less than the threshold (zero)
            end
        else
            fprintf(fileID,"  %s = 0;\n",Irc(i));% Assign only the opened line to zero
            update_affected_lines(0,F_bus(ttt),idx_source,Bus_connections,Irc,fileID,A,G,srcBusIdx,idx_link_lines);
        end

    end
    fprintf(fileID,'  }\n }\n');
end

fprintf(fileID,'}\n');

%% Clear Function Generator
fprintf(fileID,'void clear (){\n');
for k = 1:length(Line)
    fprintf(fileID,"  %s = Iioc_%i;\n",Irc(k),Line(k));
end
fprintf(fileID,'}\n</declaration>\n');
end