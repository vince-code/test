function [] = GenCBtemplate(fileID,k,CB_settings)
% [] = GenCBtemplate(fileID,k,CB_settings)
% Function GenCBtemplate generates one circuit breaker timed automaton.
% The input parameters are:
%      [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
%           [k] - the index for the circuit breaker.
% [CB_settings] - a structure that has all the CB settings.
% 
% see also GenTemplate

fprintf(fileID,'<template>\n  <name>Circuit_Breaker_%i</name>\n',CB_settings.CB{1,k}.CB_ID);
%% Generate the CB local declaration based on the selectivity type
if CB_settings.CB{1,k}.Sel_Type == 1 % this means the type of selectivity is time current selectivity
    GenCBlocalDeclarationTC(fileID,k,CB_settings);
elseif CB_settings.CB{1,k}.Sel_Type == 2 % this means the type of selectivity is directional current selectivity
    GenCBlocalDeclarationD(fileID,k,CB_settings);
elseif CB_settings.CB{1,k}.Sel_Type == 3 % this means the type of selectivity is directional zone selectivity
    GenCBlocalDeclarationDZ(fileID,k,CB_settings);
elseif CB_settings.CB{1,k}.Sel_Type == 4 % this means the type of selectivity is zone selectivity
    GenCBlocalDeclarationZ(fileID,k,CB_settings);
end
%% Generate the CB TA locations which are exactly the same for any selectivity type 
GenCBtemplateLocations(fileID); % this is a simple function that creates the three locations for any CB TA

%% Generate the transitions based on the CB type and the CB settings 
%Check the type of CB selectivity
if CB_settings.CB{1,k}.Sel_Type == 1 % this means the type of selectivity is time current selectivity
    % 1) Fault transition
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',CB_settings.CB{1,k}.Line_ID);
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
elseif CB_settings.CB{1,k}.Sel_Type == 2 % this means the type of selectivity is directional current selectivity
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f,IsFWD_%.0f)',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
elseif CB_settings.CB{1,k}.Sel_Type == 3 % this means the type of selectivity is directional zone selectivity
    % 1a) Fault transition
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f &amp;&amp; Block_%i == false',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.CB_ID);
    AlabelCB_LF = sprintf('x=0, tsel(IsFWD_%.0f)',CB_settings.CB{1,k}.Line_ID);
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
    % 1b) Fault transition
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f &amp;&amp; Block_%i == true',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.CB_ID);
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f,IsFWD_%.0f)',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
elseif CB_settings.CB{1,k}.Sel_Type == 4 % this means the type of selectivity is zone selectivity
    % 1a) Fault transition
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f &amp;&amp; Block_%i == false',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.CB_ID);
    AlabelCB_LF = sprintf('x=0, t=tsel');
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
    % 1b) Fault transition
    GlabelCB_LF = sprintf('Irc_%.0f&gt;Ith_%.0f &amp;&amp; Block_%i == true',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.CB_ID);
    AlabelCB_LF = sprintf('x=0,triptime(Irc_%.0f)',CB_settings.CB{1,k}.Line_ID);
    GenTransition("id4","id5",GlabelCB_LF,'Faults?',AlabelCB_LF,fileID);
end

% There are 5-6 transitions in total
% 1-2 transitions already made based on the type of selectivity
% The following 4 transitions don't change based on the type of selectivity

% 2a) Standby to Closed transition
GlabelCB_S2C = sprintf('Irc_%.0f&lt;Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
SlabelCB_S2C = "Fault_cleared?";
% SlabelCB_S2C = "";
AlabelCB_S2C = "";
GenTransition("id5","id4",GlabelCB_S2C,SlabelCB_S2C,AlabelCB_S2C,fileID);

% 2b) Standby to Closed transition
GlabelCB_S2C = sprintf('Irc_%.0f&lt;Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
% SlabelCB_S2C = "Fault_cleared?";
SlabelCB_S2C = "Close?";
AlabelCB_S2C = "";
GenTransition("id5","id4",GlabelCB_S2C,SlabelCB_S2C,AlabelCB_S2C,fileID);

% 3) Reset transition
GlabelCB_FC = "";
SlabelCB_FC = "Reset?";
AlabelCB_FC = sprintf('clear(), C%.0f=1',CB_settings.CB{1,k}.Line_ID);
GenTransition("id6","id4",GlabelCB_FC,SlabelCB_FC,AlabelCB_FC,fileID);

% 4) CBk_open transition
% GlabelCB_CBO = sprintf('Irc_%.0f&gt;=Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
GlabelCB_CBO = sprintf('x==t &amp;&amp; Irc_%.0f&gt;=Ith_%.0f',CB_settings.CB{1,k}.Line_ID,CB_settings.CB{1,k}.Line_ID);
SlabelCB_CBO = "CBopen!";
AlabelCB_CBO = sprintf('update(%.0f)',CB_settings.CB{1,k}.CB_ID);
% AlabelCB_CBO = "update(";
% for ii = 1:(length(Line)-1)
%     if ii==k
%         AlabelCB_CBO = strcat(AlabelCB_CBO,'0,');
%     else
%         AlabelCB_CBO = strcat(AlabelCB_CBO,'1,');
%     end
% end
% ii = length(Line);
% if ii==k
%     AlabelCB_CBO = strcat(AlabelCB_CBO,'0)');
% else
%     AlabelCB_CBO = strcat(AlabelCB_CBO,'1)');
% end
GenTransition("id5","id6",GlabelCB_CBO,SlabelCB_CBO,AlabelCB_CBO,fileID);

fprintf(fileID,'</template>\n');
end