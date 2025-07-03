Match = zeros([c,c]);
[rMatch,cMatch]=size(Match); 

 for i=1:c; % Compare each column of matrix A to the other columns
   for j=1:c;
     Match(i,j) = isequal (A(:,i),A(:,j)) %save the logical answers in a matrix
   end
 end

for ii = 6:-1:1,
    if sum(Match(ii,:))==1 % checks the lines with a sum of 1 which means the line is not parallel to any other line
        Match(ii,:) = []; % remove that line
    else
        dv = find(Match(ii,:)==1); % find the index of all parallel lines
        if dv(1) < ii % if the first 1 is not on the diagonal
            Match(ii,:) = []; % remove that row as well
        end
    end
end
