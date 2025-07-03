function [] = GenCBlocalDeclarationTC(fileID,k,CB_settings)
% [] = GenCBlocalDeclarationTC(fileID,k,CB_settings)
% Function GenCBlocalDeclarationTC generates the local declaration for a CB timed automaton that is using Time Current selectivity
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
if CB_settings.CB{1,k}.i2_flag==1
    fprintf(fileID,'  int i2 = %i;\n',CB_settings.CB{1,k}.i2);
    fprintf(fileID,'  int t2 = %i;\n',CB_settings.CB{1,k}.t2);
end
if CB_settings.CB{1,k}.i3_flag==1
    fprintf(fileID,'  int i3 = %i;\n',CB_settings.CB{1,k}.i3);
    fprintf(fileID,'  int t3 = %i;\n',CB_settings.CB{1,k}.t3);
end
fprintf(fileID,'  void triptime(int I) {\n');
fprintf(fileID,'   if(I&lt;i1){\n    t = 9999;\n   }\n');
%this next part depends on which settings are on and which are off
if CB_settings.CB{1,k}.i2_flag == 1
    fprintf(fileID,'   else if(I&gt;=i1 &amp;&amp; I&lt;i2){\n');
    fprintf(fileID,'    t = (m*i1*m*i1*t1)/(I*I);\n   }\n');
    if CB_settings.CB{1,k}.i3_flag == 1
        fprintf(fileID,'   else if(I&gt;=i2 &amp;&amp; I&lt;i3){\n');
        fprintf(fileID,'    t = t2;\n');
        fprintf(fileID,'   else if(I&gt;=i3){\n');
        fprintf(fileID,'    t = t3;\n   }\n  }\n');
    elseif CB_settings.CB{1,k}.i3_flag == 0
        fprintf(fileID,'   else if(I&gt;=i2){\n');
        fprintf(fileID,'    t = t2;\n   }\n  }\n');
    end
elseif CB_settings.CB{1,k}.i2_flag == 0
    if CB_settings.CB{1,k}.i3_flag == 1
        fprintf(fileID,'   else if(I&gt;=i1 &amp;&amp; I&lt;i3){\n');
        fprintf(fileID,'    t = (m*i1*m*i1*t1)/(I*I);\n   }\n');
        fprintf(fileID,'   else if(I&gt;=i3){\n');
        fprintf(fileID,'    t = t3;\n   }\n  }\n');
    elseif CB_settings.CB{1,k}.i3_flag == 0
%         fprintf(fileID,'   else if(I&gt;=i2){\n');
%         fprintf(fileID,'    t = t2;\n   }\n}');
    end
end
fprintf(fileID,'  </declaration>\n');
end