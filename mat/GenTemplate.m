function [] = GenTemplate(fileID,Line,Bus,F_branch,F_bus,Irc,CB_settings)
% [] = GenTemplate(fileID,Line,Bus,F_branch,F_bus,Irc,CB_settings)
% This function generates the Fault generator timed automaton and the circuit breakers timed automata
% Note: m is the number of buses and n is the number of lines.
% The input parameters are:
%           [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%             [Line] - a 1*n vector of integer values representing line IDs.
%              [Bus] - a 1*m vector of integer values representing bus IDs.
%         [F_branch] - a vector of integer values representing line IDs that the user wants to see faults on (it is a subset of Line).
%            [F_bus] - a vector of integer values representing bus IDs that the user wants to see faults on (it is a subset of Bus).
%              [Irc] - a n*1 vector of symbols of Irc_i where i is the line index, Irc is the running current.
%      [CB_settings] - a structure that has all the CB settings.
% 
% see also GenAutomata1

%% Fault Generator Template
% First I create the FG TA locations
fprintf(fileID,'<template>\n  <name>Fault_Generator</name>');
fprintf(fileID,'\n  <location id="id0" x="-306" y="-127">\n    <name x="-357" y="-161">No_Fault</name>\n  </location>');
fprintf(fileID,'\n  <location id="id1" x="263" y="-127">\n    <name x="253" y="-161">Fault_Signal</name>\n    <committed/>\n  </location>');
fprintf(fileID,'\n  <location id="id2" x="-306" y="221">\n    <name x="-349" y="238">Reset_Ready</name>\n  </location>');
fprintf(fileID,'\n  <location id="id3" x="272" y="221">\n    <name x="238" y="238">Check_Fault</name>\n    <committed/>\n  </location>');
fprintf(fileID,'\n  <location id="id4" x="476" y="25">\n    <name x="466" y="-9">Fault</name>\n  </location>');
fprintf(fileID,'\n  <init ref="id0"/>\n');

% Then I create the transitions some of which change based on the network size 
% The no fault transition
GlabelFG_NF = string(Irc(1));
GlabelFG_NF = strcat(GlabelFG_NF,sprintf('&lt;Ith_%.0f',Line(1)));
for k = 2:length(Line)
    GlabelFG_NF = strcat(GlabelFG_NF,sprintf(' &amp;&amp; %s&lt;Ith_%.0f',Irc(k),Line(k)));
end
GenTransition("id3","id2",GlabelFG_NF,"Fault_cleared!","",fileID);
% GenTransition("id3","id2",GlabelFG_NF,"","",fileID);

% The fault transition
GlabelFG_F = string(Irc(1));
GlabelFG_F = strcat(GlabelFG_F,'&gt;=',sprintf('Ith_%.0f',Line(1)));
for k = 2:length(Line)
    GlabelFG_F = strcat(GlabelFG_F,sprintf(' or %s&gt;=Ith_%.0f',Irc(k),Line(k)));
end
GenTransition("id3","id4",GlabelFG_F,"Close!","",fileID);

% CB_open transitions
GenTransition("id4","id3","","CBopen?","",fileID);

% Fault signal to Fault transition
GenTransition("id1","id4","","Faults!","",fileID);

% Li_fault transitions
for k = 1:length(F_branch) % L_fault transitions
    GlabelFG_Lfault = string(Irc(F_branch(k)));
    GlabelFG_Lfault = strcat(GlabelFG_Lfault,'!=0');
    SlabelFG_LFault = sprintf('L%.0f_fault!',Line(F_branch(k)));
    AlabelFG_Lfault = sprintf('Isc(%.0f,0), F%.0f=true',Line(F_branch(k)),Line(F_branch(k)));
    GenTransition('id0','id1',GlabelFG_Lfault,SlabelFG_LFault,AlabelFG_Lfault,fileID);
end
for k = 1:length(F_bus) % LB_fault transitions
    GlabelFG_Lfault = "";
    SlabelFG_LFault = sprintf('LB%.0f_fault!',Bus(F_bus(k)));
    AlabelFG_Lfault = sprintf('Isc(0,%.0f), FB%.0f=true',Bus(F_bus(k)),Bus(F_bus(k)));
    GenTransition('id0','id1',GlabelFG_Lfault,SlabelFG_LFault,AlabelFG_Lfault,fileID);
end

% The Reset transition
% GlabelFG_Reset = sprintf('F%.0f==true',Line(F_branch(1)));
GlabelFG_Reset = '';
AlabelFG_Reset = sprintf('F=0, FB=0, F%.0f=false',Line(F_branch(1)));
for k = 2:length(F_branch) % L_fault_clear transitions
    %GlabelFG_Reset = strcat(GlabelFG_Reset,' or ',sprintf(' F%.0f==true',Line(F_branch(k))));
    AlabelFG_Reset = strcat(AlabelFG_Reset,sprintf(', F%.0f=false',Line(F_branch(k))));
end
if length(F_bus)>1
    for kk = 1:length(F_bus)
        %GlabelFG_Reset = strcat(GlabelFG_Reset,' or ',sprintf(' FB%.0f==true',Bus(F_bus(kk))));
        AlabelFG_Reset = strcat(AlabelFG_Reset,sprintf(', FB%.0f=false',Bus(F_bus(kk))));
    end
elseif length(F_bus)==1
    %GlabelFG_Reset = strcat(GlabelFG_Reset,' or ',sprintf(' FB%.0f==true',Bus(F_bus(1))));
    AlabelFG_Reset = strcat(AlabelFG_Reset,sprintf(', FB%.0f=false',Bus(F_bus(1))));
end
GenTransition('id2','id0',GlabelFG_Reset,'Reset!',AlabelFG_Reset,fileID);
fprintf(fileID,'</template>\n');
%% Circuit Breakers Templates
% CB_settings = readtable(fileName);%input file as a name that is provided in the beginning
for k = 1:length(CB_settings.CB)
    GenCBtemplate(fileID,k,CB_settings);
end
% for k = 1:length(Line)
%     if ismember(k,idx_load)
%         GenLoadCBtemplate(fileID,Line,k,CB_settings);
%     elseif ismember(k,idx_source)
%         GenSourceCBtemplate(fileID,Line,Bus,k,idx_source,F_bus,CB_settings);
%     else
%         GenMidCBtemplate(fileID,Line,Bus,k,A,F_bus,DepMatrix,CB_settings);
%     end
% end
end