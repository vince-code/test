function [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName)
% [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName)
% This function reads the JSON file and extracts all the necessary data and organize it into different variables
% The output values are:
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
% see also CodeGeneration_using_json

data = loadjson(fileName);

CB_settings = data.CB_settings;

c = length(data.Network.Lines);
Line  = [];
for k = 1:c
    Line = [Line,data.Network.Lines{1,k}.ID];
end

Bus = data.Network.Bus;
FaultBus = data.Network.FaultBus;
r = length(Bus);

A = zeros(r,c);
Iioc_values = zeros(1,c);
Ith_values = zeros(1,c);
FaultLine = [];
Bus_connections = zeros(c,2); % a vector that has in col 1 the from bus index and in col 2 the to bus index

for k = 1:c
    Idx = find(cellfun(@(x) x.ID==Line(k), data.Network.Lines));
    x_ = data.Network.Lines{1,Idx}.IncidentBus; % Incident bus ID
    x = find(Bus==x_); % Incident bus index
    A(x,k) = 1;
    y_ = data.Network.Lines{1,Idx}.OutgoingBus; % outgoing bus ID
    y = find(Bus==y_); % outgoing bus index
    A(y,k) = -1;
    Bus_connections(k,1) = y;
    Bus_connections(k,2) = x;
    Iioc_values(k) = data.Network.Lines{1,Idx}.Iioc;
    Ith_values(k) = data.Network.Lines{1,Idx}.Ith;
    Flag = data.Network.Lines{1,Idx}.FaultFlag;
    if Flag == 1
        FaultLine = [FaultLine,Line(k)];
    end
end
F_branch = [];
F_bus = [];
for k = 1:length(FaultLine)
    F_branch = [F_branch find(FaultLine(k)==Line)];
end
for k = 1:length(FaultBus)
    F_bus = [F_bus find(FaultBus(k)==Bus)];
end

%% Finding the source contribution to faults
Isc_values_Lines = zeros(c,c); % short circuit contribution of Line (colomn idx) due to fault on line (colomn idx)
Isc_values_Buses = zeros(c,r); % short circuit contribution of Line (colomn idx) due to fault on bus (row idx)
for pp = 1:length(data.Network.Sources)
    Bus_ID = data.Network.Sources{1,pp}.Bus_ID;
    Bus_Idx = find(Bus==Bus_ID);
    Line_Idx = find(Bus_connections(:,1)==Bus_Idx);
    Isc_values_Lines(Line_Idx,:) = data.Network.Sources{1,pp}.Isc_Lines; % the row idx is the one of the source line while the colomn idx is the one of the faulted line
    Isc_values_Buses(Line_Idx,:) = data.Network.Sources{1,pp}.Isc_Buses; % the row idx is the one of the source line while the colomn idx is the one of the faulted bus
end

%% Irc generation (Irc means running current i.e. the current at that point in time)
eval(strcat("syms Irc_",sprintf('%.0f',Line(1))));
dummy  = "Irc = [";
dummy = strcat(dummy, "Irc_",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms Irc_",sprintf('%.0f',Line(k))));
    dummy = strcat(dummy, "; Irc_",sprintf('%.0f',Line(k)));
end
dummy = strcat(dummy,"];");
eval(dummy);

%% C generation (C is the variable that indicates the status of the CB on line n, i.e if C1=0 then CB on line 1 is open and I1 = 0)
eval(strcat("syms C",sprintf('%.0f',Line(1))));
dummy3  = "C = [";
dummy3 = strcat(dummy3, "C",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms C",sprintf('%.0f',Line(k))));
    dummy3 = strcat(dummy3, "; C",sprintf('%.0f',Line(k)));
end
dummy3 = strcat(dummy3,"];");
eval(dummy3);

%% Finding Parallel Lines
Parallel_Lines = {}; % complete array of all parallel line, each cell contains the matlab index of the parallel lines
SetX = []; % set of parallel lines to line x
for m = 4:length(Bus_connections)
    SetX = m;
    for n = 1:length(Bus_connections)
        if m~=n
            if Bus_connections(m,:) == Bus_connections(n,:)
                SetX = [SetX,n];
            end
        end
    end
    if length(SetX)>1
        SetX = sort(SetX,'ascend');
        Parallel_Lines = [Parallel_Lines;SetX];
    end
end

% Removing duplicates
for kk = length(Parallel_Lines):-1:1
    for pp = length(Parallel_Lines):-1:1
        if kk~=pp
            if length(Parallel_Lines{kk,1})==length(Parallel_Lines{pp,1})
                if Parallel_Lines{kk,1} == Parallel_Lines{pp,1}
                    Parallel_Lines{kk,:} = [];
                end
            end
        end
    end
end
for kk = length(Parallel_Lines):-1:1
    if isempty(Parallel_Lines{kk,1})
        Parallel_Lines(kk,:) = [];
    end
end

end