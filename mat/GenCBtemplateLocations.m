function [] = GenCBtemplateLocations(fileID)
% [] = GenCBtemplateLocations(fileID)
% Function GenCBtemplateLocations creates the three fixed locations inside any circuit breaker timed automaton 
% The input parameters are:
% [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%
% see also GenCBtemplate

fprintf(fileID,'  <location id="id4" x="-364" y="-68">\n    <name x="-382" y="-102">Closed</name>\n  </location>');
fprintf(fileID,'\n  <location id="id5" x="-119" y="-200">\n    <name x="-144" y="-234">Standby</name>\n    <label kind="invariant" x="-127" y="-190">x&lt;=t</label>\n  </location>');
fprintf(fileID,'\n  <location id="id6" x="119" y="-68">\n    <name x="109" y="-102">Open</name>\n  </location>');
fprintf(fileID,'\n  <init ref="id4"/>\n');
end