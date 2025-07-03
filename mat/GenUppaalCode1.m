function [] = GenUppaalCode1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections)
% [] = GenUppaalCode1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections)
% This function generates the entire Uppaal code so it is ready to be verified
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
% see also Reading_json_data1
global XMLFILENAME;

fileID = fopen(XMLFILENAME,'w');
GenPreamble(fileID);
if isempty(F_branch) || isempty(F_bus)
    fprintf("Minimum number of faults not selected \n Please select at least 1 line fault and 1 bus fault \n")
else
    GenAutomata1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,fileID,CB_settings,Bus_connections);
end
% GenAutomata1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,fileID,CB_settings,Bus_connections);
fclose(fileID);
end