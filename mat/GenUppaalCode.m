function [] = GenUppaalCode(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,CB_settings,Bus_connections)
fileID = fopen('../output/Uppaal_code_generation.xml','w');
GenPreamble(fileID);
GenAutomata(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values,fileID,CB_settings,Bus_connections);
fclose(fileID);
end