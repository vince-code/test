function [] = GenTransition(SourceID,TargetID,GuardLabel,SyncLabel,AssignLabel,fileID)
% [] = GenTransition(SourceID,TargetID,GuardLabel,SyncLabel,AssignLabel,fileID)
% This function generates a transition based on the associated inputs which are:
%    [SourceID] - the source location (from location in the TA)
%    [TargetID] - the target location (to location in the TA)
%  [GuardLabel] - what will be written inside the guard function of the transition
%   [SyncLabel] - what will be written inside the synchronize function of the transition
% [AssignLabel] - what will be written inside the update function of the transition
%      [fileID] - the name of the file that code writes to and in the end becomes the xml Uppaal code file, for example "UppaalCode.xml".
% 
% see also GenTemplate, GenCBtemplate

fprintf(fileID,"    <transition>\n");
fprintf(fileID,"      <source ref=""%s""/>\n",SourceID);
fprintf(fileID,"      <target ref=""%s""/>\n",TargetID);
fprintf(fileID,"      <label kind=""guard"">%s</label>\n",GuardLabel);
fprintf(fileID,"      <label kind=""synchronisation"">%s</label>\n",SyncLabel);
fprintf(fileID,"      <label kind=""assignment"">%s</label>\n",AssignLabel);
fprintf(fileID,"    </transition>\n");
end