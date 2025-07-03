function [] = GenSourceCBtemplate(fileID,Line,Bus,k,idx_source,F_bus,CB_settings)
fprintf(fileID,'<template>\n  <name>Circuit_Breaker_%i</name>\n',Line(k));
GenCBlocalDeclaration(fileID,Line,k,CB_settings);
GenCBtemplateLocations(fileID);
% There are 4 categories of transitions is total
% 1) Lk_fault transitions
for ii = 1:length(Line) % This for loop generates a transition for each line in the network because technically the source CB can detect faults on every line
    GlabelCB_LF = "";
    SlabelCB_LF = sprintf('L%.0f_fault?',Line(ii));
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
    GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);
end

for jj = 1:length(F_bus) % This loop generates a transition for each bus fault assuming there are any
    GlabelCB_LF = "";
    SlabelCB_LF = sprintf('LB%.0f_fault?',Bus(F_bus(jj)));
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
    GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);
end


% 2) Standby to Closed transition
x = Line; % this is just to remove the source index from the vector used to genrate the transitions because this category of transitions is for all lines except the source 
x(x==Line(idx_source)) = [];
for jj = 1:length(x)
    GlabelCB_S2C = "";
    SlabelCB_S2C = sprintf('CB%.0f_open?',x(jj));
    AlabelCB_S2C = "";
    GenTransition("id5","id4",GlabelCB_S2C,SlabelCB_S2C,AlabelCB_S2C,fileID);
end

% 3) Lk_fault_clear transition
GlabelCB_FC = "";
SlabelCB_FC = sprintf('L%.0f_fault_clear?',Line(k));
AlabelCB_FC = sprintf('clear(), C%.0f=1',Line(k));
GenTransition("id6","id4",GlabelCB_FC,SlabelCB_FC,AlabelCB_FC,fileID);

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