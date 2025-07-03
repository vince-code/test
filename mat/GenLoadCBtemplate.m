function [] = GenLoadCBtemplate(fileID,Line,k,CB_settings)
fprintf(fileID,'<template>\n  <name>Circuit_Breaker_%i</name>\n',Line(k));
GenCBlocalDeclaration(fileID,Line,k,CB_settings);
GenCBtemplateLocations(fileID);
% There are 4 transitions is total
% 1) Lk_fault transition
GlabelCB_LF = "";
SlabelCB_LF = sprintf('L%.0f_fault?',Line(k));
AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',Line(k));
GenTransition("id4","id5",GlabelCB_LF,SlabelCB_LF,AlabelCB_LF,fileID);

% 2) Standby to Closed transition
GlabelCB_S2C = sprintf('Irc_%.0f&lt;Ith_%.0f',Line(k),Line(k));
SlabelCB_S2C = "";
AlabelCB_S2C = "";
GenTransition("id5","id4",GlabelCB_S2C,SlabelCB_S2C,AlabelCB_S2C,fileID);

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