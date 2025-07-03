clc
close all
clear all
%% System inputs

% The incidence matrix "A" is built accourding to these rules
% 1) if Li is incident on Bi then it takes 1
% 2) if Li is not incident on Bi then it takes -1
% 3) if Li is not connected to Bi then it takes 0

%   L1  L2  L3  L5  L6  L8
A = [
    1   0   0   0   0   0   % B1
    0   1   0   0   0   0   % B2
    0   0   1   0   0   0   % B3
   -1  -1  -1   1   0   0   % B4
    0   0   0  -1  -1   1   % B5
    0   0   0   0   1   0   % B6
    0   0   0   0   0  -1   % B8
];

Line = [1 2 3 5 6 8]; % vector of the graph index of the lines as shown above the matrix A
Bus = [1 2 3 4 5 6 8];
FaultLine = [1 2 3 5 6 8]; % All requested line fault locations
FaultBus = []; % All requested bus fault locations
%All currents are divided by 10 so uppaal can hendle it,i.e. if the current is 200 A the input in these vectors must be 20
Iioc_values = [20 17 17 54 20 74]; % vector of the values of currents in the intial operating conditions(ioc)
Ith_values = [25 25 25 63 25 80]; % vector of the values of the threshold currents
Isc_values = [5450 5450 5450 7400 7400 9500]; % vector of the values of the short circuit currents

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

% % Isc generation (Isc means short circuit current)
% eval(strcat("syms Isc_",sprintf('%.0f',Line(1))));
% dummy1  = "Isc = [";
% dummy1 = strcat(dummy1, "Isc_",sprintf('%.0f',Line(1)));
% for k = 2:c
%     eval(strcat("syms Isc_",sprintf('%.0f',Line(k))));
%     dummy1 = strcat(dummy1, "; Isc_",sprintf('%.0f',Line(k)));
% end
% dummy1 = strcat(dummy1,"]");
% eval(dummy1);

% % Iioc generation (Iioc intial operating conditions current)
% eval(strcat("syms Iioc_",sprintf('%.0f',Line(1))));
% dummy2  = "Iioc = [";
% dummy2 = strcat(dummy2, "Iioc_",sprintf('%.0f',Line(1)));
% for k = 2:c
%     eval(strcat("syms Iioc_",sprintf('%.0f',Line(k))));
%     dummy2 = strcat(dummy2, "; Iioc_",sprintf('%.0f',Line(k)));
% end
% dummy2 = strcat(dummy2,"]");
% eval(dummy2);

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

% % Ith generation (Ith threshold current)
% eval(strcat("syms Ith_",sprintf('%.0f',Line(1))));
% dummy4  = "Ith = [";
% dummy4 = strcat(dummy4, "Ith_",sprintf('%.0f',Line(1)));
% for k = 2:c
%     eval(strcat("syms Ith_",sprintf('%.0f',Line(k))));
%     dummy4 = strcat(dummy4, "; Ith_",sprintf('%.0f',Line(k)));
% end
% dummy4 = strcat(dummy4,"]");
% eval(dummy4);

% % F generation (F is the boolian variable that indicates a fault on its corresponding line)
% eval(strcat("syms F",sprintf('%.0f',Line(1))));
% dummy5  = "F = [";
% dummy5 = strcat(dummy5, "F",sprintf('%.0f',Line(1)));
% for k = 2:c
%     eval(strcat("syms F",sprintf('%.0f',Line(k))));
%     dummy5 = strcat(dummy5, "; F",sprintf('%.0f',Line(k)));
% end
% dummy5 = strcat(dummy5,"]");
% eval(dummy5);

%% 
% GenCode(A,Line,Bus,F_branch,F_bus,Irc,Isc,C)
GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values);