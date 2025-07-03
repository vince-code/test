% the output should be: Irc, Isc, F_branch, F_bus, C
clear 
clc
% Inputs
A = [];
Line  = [1 2 3 5 6 8];
FaultLine = [1 2 3 5 6 8];
FaultBus = [];

% Outputs
c = 6;

F_branch = [];
F_bus = [];
for k = 1:length(FaultLine)
    F_branch = [F_branch find(FaultLine(k)==Line)];
end
for k = 1:length(FaultBus)
    F_bus = [F_bus find(FaultBus(k)==Bus)];
end

% Irc generation (Irc means running current i.e. the current at that point in time)
eval(strcat("syms Irc_",sprintf('%.0f',Line(1))));
dummy  = "Irc = [";
dummy = strcat(dummy, "Irc_",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms Irc_",sprintf('%.0f',Line(k))));
    dummy = strcat(dummy, "; Irc_",sprintf('%.0f',Line(k)));
end
dummy = strcat(dummy,"]");
eval(dummy)

% Isc generation (Isc means short circuit current)
eval(strcat("syms Isc_",sprintf('%.0f',Line(1))));
dummy2  = "Isc = [";
dummy2 = strcat(dummy2, "Isc_",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms Isc_",sprintf('%.0f',Line(k))));
    dummy2 = strcat(dummy2, "; Isc_",sprintf('%.0f',Line(k)));
end
dummy2 = strcat(dummy2,"]");
eval(dummy2)

% C generation (C is the variable that indicates the status of the CB on line n, i.e if C1=0 then CB on line 1 is open and I1 = 0)
eval(strcat("syms C_",sprintf('%.0f',Line(1))));
dummy3  = "C = [";
dummy3 = strcat(dummy3, "C",sprintf('%.0f',Line(1)));
for k = 2:c
    eval(strcat("syms C",sprintf('%.0f',Line(k))));
    dummy3 = strcat(dummy3, "; C",sprintf('%.0f',Line(k)));
end
dummy3 = strcat(dummy3,"]");
eval(dummy3)