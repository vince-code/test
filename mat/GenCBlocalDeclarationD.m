function [] = GenCBlocalDeclarationD(fileID,k,CB_settings)
% [] = GenCBlocalDeclarationD(fileID,k,CB_settings)
% Function GenCBlocalDeclarationD generates the local declaration for a CB timed automaton that is using Directional selectivity
% The input parameters are:
%      [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%           [k] - the index for the circuit breaker.
% [CB_settings] - a structure that has all the CB settings.
% 
% see also GenTemplate

%Idx = find(cellfun(@(x) x.ID==Line(k), CB_settings.CB));

fprintf(fileID,'  <declaration>\n  clock x;\n\n  int t;\n');
fprintf(fileID,'  int m = %i;\n',CB_settings.CB{1,k}.m);
fprintf(fileID,'  int i1 = %i;\n',CB_settings.CB{1,k}.i1);
fprintf(fileID,'  int t1 = %i;\n',CB_settings.CB{1,k}.t1);
fprintf(fileID,'  int i7 = %i;\n',CB_settings.CB{1,k}.i7);
fprintf(fileID,'  int t7FW = %i;\n',CB_settings.CB{1,k}.t7FW);   
fprintf(fileID,'  int t7BW = %i;\n',CB_settings.CB{1,k}.t7BW);

%% The Trip time Function
fprintf(fileID,'  void triptime(int I, bool IsFWD){\n');
fprintf(fileID,'   if(I&lt;i1){\n    t = 9999;\n   }\n');
fprintf(fileID,'   else if(I&gt;=i1 &amp;&amp; I&lt;i7){\n');
fprintf(fileID,'    t = (m*i1*m*i1*t1)/(I*I);\n   }\n');
fprintf(fileID,'   else if(I&gt;=i7){\n');
fprintf(fileID,'     if(IsFWD == true){\n      t = t7FW;\n     }\n');
fprintf(fileID,'     else if(IsFWD == false){\n      t = t7BW;\n     }\n     }\n  }\n');

fprintf(fileID,'\n  </declaration>\n');
end