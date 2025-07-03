function [] = update_remaining_lines2(i,affected_lines,idx_source,idx_link_lines,A,Irc,fileID)

affected_lines_no_sources = affected_lines;
for kk = 1:length(idx_source)
    affected_lines_no_sources(affected_lines_no_sources==idx_source(kk))=[];%remove the source index from the variable
end
affected_lines_no_sources(affected_lines_no_sources==idx_link_lines)=[];% remove the link lines
for pp = length(affected_lines_no_sources):-1:1
    KCLrow = find(A(:,affected_lines_no_sources(pp))==-1);
    RHS_idx = A(KCLrow,:);
    RHS_idx(:,affected_lines_no_sources(pp))=0;
    RHS(pp) = RHS_idx*Irc;
    LHS(pp) = Irc(affected_lines_no_sources(pp));
    fprintf(fileID,'   %s = %s;\n',LHS(pp),RHS(pp));
end
end