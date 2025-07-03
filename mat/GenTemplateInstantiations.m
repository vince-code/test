function [] = GenTemplateInstantiations(fileID,CB_settings)
% [] = GenTemplateInstantiations(fileID,CB_settings)
% Function GenTemplateInstantiations generates something related to Uppaal where each timed automata is given a short name to help later when using the queries
% 
% The input parameters are:
%      [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
% [CB_settings] - a structure that has all the CB settings.
% 
% see also GenAutomata1

fprintf(fileID,'<system>\nFG = Fault_Generator();\n');
for k = 1:length(CB_settings.CB)
    fprintf(fileID,'CB%i = Circuit_Breaker_%i();\n',CB_settings.CB{1,k}.CB_ID,CB_settings.CB{1,k}.CB_ID);
end
fprintf(fileID,'system FG');
for k = 1:length(CB_settings.CB)
    fprintf(fileID,', CB%i',CB_settings.CB{1,k}.CB_ID);
end
fprintf(fileID,';\n</system>');
end