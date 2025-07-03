function [] = GenMidCBtemplate(fileID,Line,Bus,k,A,F_bus,DepMatrix,CB_settings)
fprintf(fileID,'<template>\n  <name>Circuit_Breaker_%i</name>\n',Line(k));
GenCBlocalDeclaration(fileID,Line,k,CB_settings);
GenCBtemplateLocations(fileID);
% There are 4 categories of transitions is total
% 1) Lk_fault transitions
x = find(DepMatrix(k,:)==1); % In this vector we have the index of all the lines that line (k) can detect a fault on
for ii = 1:length(x)
    GlabelCB_LF = "";
    SlabelCB_LF = sprintf('L%.0f_fault?',Line(x(ii)));
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
    GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);
end
GlabelCB_LF = "";
SlabelCB_LF = sprintf('L%.0f_fault?',Line(k));
AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);

for jj = 1:length(F_bus)
    kIncidentBus = find(A(:,k)==1); % The index of the bus where line(k) is incident on
    if kIncidentBus == F_bus(jj)
        GlabelCB_LF = "";
        SlabelCB_LF = sprintf('LB%.0f_fault?',Bus(F_bus(jj)));
        AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
        GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);
    end
end


% 2) Standby to Closed transition
for jj = 1:length(x)
    GlabelCB_S2C = "";
    SlabelCB_S2C = sprintf('CB%.0f_open?',Line(x(jj)));
    AlabelCB_S2C = "";
    GenTransition("id5","id4",GlabelCB_S2C,SlabelCB_S2C,AlabelCB_S2C,fileID);
end

% 3) Lk_fault_clear transition
for ii = 1:length(x)
    GlabelCB_FC = "";
    SlabelCB_FC = sprintf('L%.0f_fault_clear?',Line(x(ii)));
    AlabelCB_FC = sprintf('clear(), C%.0f=1',Line(k));
    GenTransition("id6","id4",GlabelCB_FC,SlabelCB_FC,AlabelCB_FC,fileID);
end
GlabelCB_FC = "";
SlabelCB_FC = sprintf('L%.0f_fault_clear?',Line(k));
AlabelCB_FC = sprintf('clear(), C%.0f=1',Line(k));
GenTransition("id6","id4",GlabelCB_FC,SlabelCB_FC,AlabelCB_FC,fileID);
for jj = 1:length(F_bus)
    kIncidentBus = find(A(:,k)==1); % The index of the bus where line(k) is incident on
    if kIncidentBus == F_bus(jj)
        GlabelCB_FC = "";
        SlabelCB_FC = sprintf('LB%.0f_fault_clear?',Bus(F_bus(jj)));
        AlabelCB_FC = sprintf('clear(), C%.0f=1',Line(k));
        GenTransition("id6","id4",GlabelCB_FC,SlabelCB_FC,AlabelCB_FC,fileID);
    end
end


% 4) CBk_open transition
GlabelCB_CBO = sprintf('x==t &amp;&amp; Irc_%.0f&gt;=Ith_%.0f',Line(k),Line(k));
SlabelCB_CBO = sprintf('CB%.0f_open!',Line(k));
AlabelCB_CBO = "update(";
for ii = 1:(length(Line)-1)
    if ii==k
        AlabelCB_CBO = strcat(AlabelCB_CBO,'0,');
    else
        AlabelCB_CBO = strcat(AlabelCB_CBO,'1,');
    end
end
ii = length(Line);
if ii==k
    AlabelCB_CBO = strcat(AlabelCB_CBO,'0)');
else
    AlabelCB_CBO = strcat(AlabelCB_CBO,'1)');
end
GenTransition("id5","id6",GlabelCB_CBO,SlabelCB_CBO,AlabelCB_CBO,fileID);

fprintf(fileID,'</template>\n');
end