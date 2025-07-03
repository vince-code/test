fileID = fopen('../output/Mathematical_proof.txt','w');
syms Z11 Z12 Z13 Z14 Z21 Z22 Z23 Z24 Z31 Z32 Z33 Z34 Z41 Z42 Z43 Z44;
syms zL1 zL2 zL3 zL4 zL5 zL6 zL7 zL8 zL9 zL10 zL11 
syms I1 I2 I3 I4

I = [I1;I2;I3;I4];
z = [zL1 zL2 zL3 zL4 zL5];
%Z = [Z11 Z12 Z13 Z14;Z21 Z22 Z23 Z24;Z31 Z32 Z33 Z34;Z41 Z42 Z43 Z44];
Z = [Z11 Z12 Z13 Z14;Z12 Z22 Z23 Z24;Z13 Z23 Z33 Z34;Z14 Z24 Z34 Z44];

%%  Case A disconnecting line 5 then 3
% Disconnecting line 5
m = 2; % disconnected line terminal bus 1
n = 4; % disconnected line terminal bus 2
k = 5; % disconnected line index
B = Z(:,m)-Z(:,n);
C = Z(m,:)-Z(n,:);
D = Z(m,m)+Z(n,n)-2*Z(m,n)+z(k);
X = B*(inv(D))*C;
Znew1 = Z - X;

% Disconnecting line 3
m = 2; % disconnected line terminal bus 1
n = 3; % disconnected line terminal bus 2
k = 3; % disconnected line index
B = Znew1(:,m)-Znew1(:,n);
C = Znew1(m,:)-Znew1(n,:);
D = Znew1(m,m)+Znew1(n,n)-2*Znew1(m,n)+z(k);
X = B*(inv(D))*C;
Znew2 = Znew1 - X;

E = Znew2*I;
I1a = (E(1)-E(2))/z(1);

%%  Case B disconnecting line 3 then 5
% Disconnecting line 3
m = 2; % disconnected line terminal bus 1
n = 3; % disconnected line terminal bus 2
k = 3; % disconnected line index
B = Z(:,m)-Z(:,n);
C = Z(m,:)-Z(n,:);
D = Z(m,m)+Z(n,n)-2*Z(m,n)+z(k);
X = B*(inv(D))*C;
Znew1 = Z - X;

% Disconnecting line 5
m = 2; % disconnected line terminal bus 1
n = 4; % disconnected line terminal bus 2
k = 5; % disconnected line index
B = Znew1(:,m)-Znew1(:,n);
C = Znew1(m,:)-Znew1(n,:);
D = Znew1(m,m)+Znew1(n,n)-2*Znew1(m,n)+z(k);
X = B*(inv(D))*C;
Znew2 = Znew1 - X;

E = Znew2*I;
I1b = (E(1)-E(2))/z(1);

Proof = isequal(I1a,I1b);

fprintf(fileID,'%s\n\n%s',I1a,I1b);
fclose(fileID);