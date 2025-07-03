clc
close all
clear all
%% System inputs

% The incidence matrix "A" is built accourding to these rules
% 1) if Li is incident on Bi then it takes 1
% 2) if Li is not incident on Bi then it takes -1
% 3) if Li is not connected to Bi then it takes 0

%   1   2   3   4   5   6   7   8   9   10  11
%   L1  L2  L3  L5  L6  L8  L9  L10 L11 L12 L13
A = [
    1   0   0   0   0   0   0   0   0   0   0   % B1
    0   1   0   0   0   0   0   0   0   0   0   % B2
    0   0   1   0   0   0   0   0   0   0   0   % B3
   -1  -1  -1   1   0   0   0   0   0   0   0   % B4
    0   0   0  -1  -1   1   0   0   0   0  -1   % B5
    0   0   0   0   1   0   0   0   0   0   0   % B6
    0   0   0   0   0  -1   0   0   0   0   0   % B8
    0   0   0   0   0   0   1   0   0   0   0   % B9
    0   0   0   0   0   0   0   1   0   0   0   % B10
    0   0   0   0   0   0   0   0   1   0   0   % B11
    0   0   0   0   0   0  -1  -1   0   1   0   % B12
    0   0   0   0   0   0   0   0  -1  -1   1   % B13 
];

Line = [1 2 3 5 6 8 9 10 11 12 13]; % vector of the graph index of the lines as shown above the matrix A
Bus = [1 2 3 4 5 6 8 9 10 11 12 13];
FaultLine = [1 2 3 5 6 8 9 10 11 12 13]; % All requested line fault locations
FaultBus = []; % All requested bus fault locations

%% Generate required variables
F_branch = [];
F_bus = [];
for k = 1:length(FaultLine)
    F_branch = [F_branch find(FaultLine(k)==Line)];
end
for k = 1:length(FaultBus)
    F_bus = [F_bus find(FaultBus(k)==Bus)];
end

[r, c]=size(A);

% Irc generation (Irc means running current i.e. the current at that point in time)
eval(strcat("syms Irc_",sprintf('%.0f',Line(1))));
dummy  = "Irc = [";
dummy = strcat(dummy, "Irc_",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms Irc_",sprintf('%.0f',Line(k))));
    dummy = strcat(dummy, "; Irc_",sprintf('%.0f',Line(k)));
end
dummy = strcat(dummy,"]");
eval(dummy);

% C generation (C is the variable that indicates the status of the CB on line n, i.e if C1=0 then CB on line 1 is open and I1 = 0)
eval(strcat("syms C",sprintf('%.0f',Line(1))));
dummy3  = "C = [";
dummy3 = strcat(dummy3, "C",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms C",sprintf('%.0f',Line(k))));
    dummy3 = strcat(dummy3, "; C",sprintf('%.0f',Line(k)));
end
dummy3 = strcat(dummy3,"]");
eval(dummy3);
%% 

GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings);