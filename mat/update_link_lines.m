function [] = update_link_lines(i,affected_lines,idx_link_lines,idx_source,Bus_connections,Irc,fileID,A)

affected_lines2 = [i affected_lines]; % add the faulted line to the variable
% remove the link lines index
for k = 1:length(idx_link_lines)
    affected_lines2(affected_lines2==idx_link_lines(k)) = [];
end
%remove the source index from the variable
for kk = 1:length(idx_source)
    affected_lines2(affected_lines2==idx_source(kk))=[];
end
% in this loop we find all the buses that the affected lines are connected
% to this is to find out which row in the A matrix is needed to correctly
% update the current
Connected_buses = [];
for kk = 1:length(affected_lines2)
    Connected_buses = [Connected_buses Bus_connections(affected_lines2(kk),:)];
end
Connected_buses = unique(Connected_buses,'stable');

for kk = 1:length(idx_link_lines)
    Link_line_bus = Bus_connections(idx_link_lines(kk),:);
    KCL_row2 = [];
    for ppp = 1:2
        Res = Link_line_bus(ppp)==Connected_buses;
        KCL_row2 = [KCL_row2 Connected_buses(Res)];
    end
    KCL_row3 = Link_line_bus;
    KCL_row3(KCL_row3==KCL_row2) = [];
    RHS_idx2 = A(KCL_row3,:);
    RHS_idx2(1,idx_link_lines(kk))=0;
    RHS2 = RHS_idx2*Irc;
    LHS2 = Irc(idx_link_lines(kk));
    fprintf(fileID,'   %s = %s;\n',LHS2,RHS2);
end

end