function [] = GenQueries1(fileID,Line,Bus,F_branch,F_bus,CB_settings,LinesCBs,Bus_connections)
% [] = GenQueries1(fileID,Line,Bus,F_branch,F_bus,CB_settings,LinesCBs,Bus_connections)
% Function GenQueries1 generates the queries required to check for selectivity
% Note: m is the number of buses and n is the number of lines
% The input parameters are:
%           [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%             [Line] - a 1*n vector of integer values representing line IDs.
%              [Bus] - a 1*m vector of integer values representing bus IDs.
%         [F_branch] - a vector of integer values representing line IDs that the user wants to see faults on (it is a subset of Line).
%            [F_bus] - a vector of integer values representing bus IDs that the user wants to see faults on (it is a subset of Bus).
%      [CB_settings] - a structure that has all the CB settings
%         [LinesCBs] - a n*2 cell array where for each row column 1 contains the Line ID and column 2 contains all the CB IDs present on that line.
%  [Bus_connections] - a n*2 vector detailing for each line the from bus in column 1 and the to bus in column 2.
%
% see also GenAutomata1

global Fault_Analysis_Line;
global Fault_Analysis_Bus;
% Vector where the first colomn has the CB ID and the second colomn has the Bus ID
CB_Bus = [];
for k = 1:length(CB_settings.CB)
    CB_Bus = [CB_Bus;[CB_settings.CB{1,k}.CB_ID,CB_settings.CB{1,k}.Bus_ID]];
end

fprintf(fileID,'\n<queries>\n');
fprintf(fileID,'  <query>\n');
fprintf(fileID,'    <formula>E&lt;&gt;(');

%% Queries for the faulted lines
x = Fault_Analysis_Line{1, 2}; % vector of all affected CBs by fault F1
LineIDx = find(Line==Fault_Analysis_Line{1,1});
CBsOnLine = LinesCBs{LineIDx,2}; % These are all the CBs that are on the faulted line but for now lets assume that there is only one CB per line
faulted_CB_ID = CBsOnLine(1); % The CB that should open
Upstream_CB_ID = x(1);
if Upstream_CB_ID ~= faulted_CB_ID
    fprintf(fileID,'(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
    for kk = 2:length(x)
        Upstream_CB_ID = x(kk);
        if Upstream_CB_ID ~= faulted_CB_ID
            fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
        end
    end
else
    Upstream_CB_ID = x(2);
    fprintf(fileID,'(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
    for kk = 3:length(x)
        Upstream_CB_ID = x(kk);
        if Upstream_CB_ID ~= faulted_CB_ID
            fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
        end
    end
end

for k = 2:length(F_branch)
    x = Fault_Analysis_Line{k, 2}; % vector of all affected CBs by fault Fk
    if ~isempty(Fault_Analysis_Line{k,1})
        LineIDx = find(Line==Fault_Analysis_Line{k,1});
        CBsOnLine = LinesCBs{LineIDx, 2}; % These are all the CBs that are on the faulted line but for now lets assume that there is only one CB per line
        faulted_CB_ID = CBsOnLine(1);
        for kk = 1:length(x)
            Upstream_CB_ID = x(kk);
            if Upstream_CB_ID ~= faulted_CB_ID
                fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(k)));
            end
        end
    end
end
%% Queries for the faulted Buses
for k = 1:length(F_bus)
    y = Fault_Analysis_Bus{k,2}; % vector of all affected CBs by fault FBk
    ConnectedLines = find(Bus_connections(:,1)==F_bus(k)); % Vector of all the lines connected directly to the faulted Bus
    ConnectedLines = [ConnectedLines;find(Bus_connections(:,2)==F_bus(k))];
    ConnectedCBs = []; % Vector of all CBs that should open
    for kk = 1:length(ConnectedLines)
        CBs = LinesCBs{ConnectedLines(kk), 2};
        if length(CBs)==1
            ConnectedCBs = [ConnectedCBs,CBs];
        else
            CorrectCBIdx = find(CB_Bus(:,2)==Bus(F_bus(k)));
            CorrectCB = intersect(CBs,CB_Bus(CorrectCBIdx,1));
            ConnectedCBs = [ConnectedCBs,CorrectCB];
        end
    end
    for p = 1:length(ConnectedCBs)
        for pp = 1:length(y)
            faulted_CB_ID = ConnectedCBs(p);
            Upstream_CB_ID = y(pp);
            if ismember(faulted_CB_ID,y)
                if ~ismember(Upstream_CB_ID,ConnectedCBs)
                    if Upstream_CB_ID ~= faulted_CB_ID
                        fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)',faulted_CB_ID,Upstream_CB_ID,Bus(F_bus(k)));
                    end
                end
            end
        end
    end
end

fprintf(fileID,')</formula>\n');
fprintf(fileID,'    <comment></comment>\n');
fprintf(fileID,'  </query>\n');

% The deadlock query
% fprintf(fileID,'  <query>\n');
% fprintf(fileID,'    <formula>E&lt;&gt; deadlock</formula>\n');
% fprintf(fileID,'    <comment></comment>\n');
% fprintf(fileID,'  </query>\n');


fprintf(fileID,'</queries>\n');
end