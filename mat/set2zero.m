function [affected_lines] = set2zero(i,idx_source,idx_link_lines,Irc,dep,fileID)

affected_lines = dep{1,i};
all_lines = 1:1:length(Irc); % vector of indicies of all unaffected lines
unaffected_lines = all_lines;
for pp = 1:length(affected_lines) % Removes the affected lines index from the vector
    unaffected_lines(unaffected_lines==affected_lines(pp))=[];
end
unaffected_lines(unaffected_lines==i)=[]; % remove the faulted line index from this vector
for pp = 1:length(idx_source) % Removes the source lines index from the vector
    unaffected_lines(unaffected_lines==idx_source(pp))=[];
end
for pp = 1:length(idx_link_lines) % Removes the link lines index from the vector
    unaffected_lines(unaffected_lines==idx_link_lines(pp))=[];
end
for kk = 1:length(unaffected_lines)
    fprintf(fileID,'   %s = 0;\n',Irc(unaffected_lines(kk))); % Assign the selected lines to zero
end

end