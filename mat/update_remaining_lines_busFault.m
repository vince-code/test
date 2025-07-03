function [] = update_remaining_lines_busFault(i,affected_lines,idx_source,idx_link_lines,A,Irc,fileID,multSrx_cols,C)

affected_lines_no_sources = affected_lines;
affected_lines_no_sources = [i,affected_lines_no_sources];
for kk = 1:length(idx_source)
    affected_lines_no_sources(affected_lines_no_sources==idx_source(kk))=[];%remove the source index from the variable
end
if ~isempty(idx_link_lines)
    for xxx = 1:length(idx_link_lines)
        affected_lines_no_sources(affected_lines_no_sources==idx_link_lines(xxx))=[];% remove the link lines
    end
end
loopCounter = 0;
for pp = length(affected_lines_no_sources):-1:1
    if ismember(affected_lines_no_sources(pp),multSrx_cols)
        LHS(pp) = Irc(affected_lines_no_sources(pp));
        usedRow = find(A(:,affected_lines_no_sources(pp))==-1);
        src_idx = A(usedRow,:)==1;
        RHS_up(pp) = sum(Irc(src_idx));
        parallel_lines_idx = A(usedRow,:)== -1;
        n = sum(C(parallel_lines_idx));
        if loopCounter == 0
            fprintf(fileID,'   n = %s;\n',n);
        end
        fprintf(fileID,'   %s = %s * ((%s)/n);\n',LHS(pp),C(affected_lines_no_sources(pp)),RHS_up(pp));
        loopCounter = loopCounter + 1;
    else
        KCLrow = find(A(:,affected_lines_no_sources(pp))==-1);
        RHS_idx = A(KCLrow,:);
        for ppp = 1:length(idx_link_lines)
            if RHS_idx(1,idx_link_lines(ppp))==-1
               RHS_idx(1,idx_link_lines(ppp))=1;
            end
        end
        RHS_idx(:,affected_lines_no_sources(pp))=0;
        RHS(pp) = RHS_idx*Irc;
        LHS(pp) = Irc(affected_lines_no_sources(pp));
        fprintf(fileID,'   %s = %s;\n',LHS(pp),RHS(pp));
    end
end