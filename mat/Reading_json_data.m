function [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections] = Reading_json_data(fileName)
% This function reads the JSON file and extracts all the necessary data and organize it into different variables
% The variables are:
% [A] is the incidence matrix where rows represent buses and columns represent lines
% [Line] is a vector of all line IDs
% [Bus] is a vector of all bus IDs
% [F_branch] is a vector of line IDs that the user wants to see faults on
% [F_bus] is a vector of bus IDs that the user wants to see faults on
% [Irc] is a vector of symbols of Irc_i where i is the line index, Irc is the running current
% [Iioc_values] is a vector of the currents in the intial operating conditions for each line
% [Isc_values] is a vector of short circuit currents due to a fault on each line
% [Ith_values] is a vector of threshold current values on each line
% [CB_settings] is a structure that has all the CB settings
% [Bus_connections] is a vector detailing for each line the from and to bus
% [C] is a vector of symbols of Ci where i is the line index, each symbol can have a value of 1 or 0, 1 if the CB on line i is open and 0 if the CB on line i is closed

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
Isc_values = [];
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
    Isc_contribution = data.Network.Lines{1,Idx}.Isc;
    if Isc_contribution == 0
        Isc_values = [Isc_values;zeros(1,c)];
    else
        Isc_values = [Isc_values;Isc_contribution];
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