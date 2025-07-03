function a = FindDep(A,x)
    global dep;
    a = [];
    y = find(A(:,x)==-1);
    z = find(A(y,:)==1);
    a = [a,z];
    for ii = 1:length(z)
        b = FindDep(A,z(ii));
        a = [a,b];
    end
end