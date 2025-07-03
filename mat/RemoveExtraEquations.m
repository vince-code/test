% Removing unneccessary equations function and creating a variable with the
% correct order of indices of rows in the A matrix that need to be used to
% update the currents, in other words the correct order of updating the
% currents from downstream to upstream
function [KCL_rows2, multSrx_rows2, OrderedKCLrows] = RemoveExtraEquations(KCL_rows,multSrx_rows,i,dep,A)
KCL_rows2 = KCL_rows;
multSrx_rows2 = multSrx_rows;
AffectedLines = []; % vector of the selected line and all the lines that are affected by it
SelectedRows = []; % Extracts a row from the A matrix to be checked
ExtractedLines = []; % Extracts the values that corresponds to the AffectedLines in the SelectedRows
OrderedKCLrows = []; % A vector of A matrix row indices ordered in the correct way to update the currents
for k = length(KCL_rows2):-1:1
    AffectedLines = [i dep{1,i}];
    SelectedRows = A(KCL_rows2(k),:);
    ExtractedLines = SelectedRows(AffectedLines);
    if nnz(ExtractedLines) == 0 % if the row doen't have any of the affected lines
        KCL_rows2(k) = []; % remove that row so as to not write any unneccessary equations
    end
end
for k = length(multSrx_rows2):-1:1
    AffectedLines = [i dep{1,i}];
    SelectedRows = A(multSrx_rows2(k),:);
    ExtractedLines = SelectedRows(AffectedLines);
    if nnz(ExtractedLines) == 0
        multSrx_rows2(k) = [];
    end
end
remove = find(A(:,i)==1);
KCL_rows2(KCL_rows2==remove) = []; % remove the equation containing the faulted line from the list of equations to be excuted so as not to override the previous code

UpstreamLines = dep{1,i}; % vector of all the lines that are upstream from the selected line
for k = 1:length(UpstreamLines)
    OrderedKCLrows = [OrderedKCLrows find(A(:,UpstreamLines(k))==1)];
end
OrderedKCLrows = unique(OrderedKCLrows,'stable');

end