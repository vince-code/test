function [] = GenAutomata(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,fileID,CB_settings,Bus_connections)
% GenAutomata(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,fileID,CB_settings,Bus_connections)
% This is an old version of GenAutomata that is used for backwards
% compatebility with GenUppaalCode
% 
% See also GENUPPAALCODE

fprintf(fileID,"<nta>\n");
[idx_source,dep,srcBusIdx] = GenDeclaration(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,fileID,Bus_connections);
GenTemplate(fileID,Line,Bus,F_branch,F_bus,Irc,CB_settings);
GenTemplateInstantiations(fileID,CB_settings);
GenQueries(fileID,Line,Bus,idx_source,dep,F_branch,F_bus,srcBusIdx,A,CB_settings);
fprintf(fileID,"</nta>\n");
end