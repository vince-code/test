clear all
clc

fileName = 'Inputs4.json';
%% Extracting data from Json file
data = loadjson(fileName);

c = length(data.Network.Lines);
Line  = [];
for k = 1:c
    Line = [Line,data.Network.Lines{1,k}.ID];
end

Bus = data.Network.Bus;
FaultBus = data.Network.FaultBus;
r = length(Bus);
A = zeros(r,c);
FaultLine = [];
Bus_connections = zeros(c,2); % a vector that has in col 1 the from bus index and in col 2 the to bus index
z = zeros(c,1); % vector of impedance of each line

for k = 1:c
    Idx = find(cellfun(@(x) x.ID==Line(k), data.Network.Lines));
    x_ = data.Network.Lines{1,Idx}.IncidentBus;
    x = find(Bus==x_);
    A(x,k) = 1;
    y_ = data.Network.Lines{1,Idx}.OutgoingBus;
    y = find(Bus==y_);
    A(y,k) = -1;
    Bus_connections(k,1) = y;
    Bus_connections(k,2) = x;
    z(k,1) = (data.Network.Lines{1,Idx}.r) + (data.Network.Lines{1,Idx}.x)*1i;
    if data.Network.Lines{1,Idx}.FaultFlag == 1
        FaultLine = [FaultLine,Line(k)];
    end
end
%% Inputs

%   L1  L2  L3  L4  L5  L6  L7  L8  L9  L10 L11
% A = [
%        -1  -1  -1   0   0   0   0   0   0   0   0  % B1
%         1   0   0  -1  -1  -1  -1   0   0   0   0  % B2
%         0   0   0   1   0   0   0  -1  -1   0   0  % B3
%         0   1   0   0   1   0   0   0   0  -1   0  % B4
%         0   0   1   0   0   1   0   1   0   1  -1  % B5
%         0   0   0   0   0   0   1   0   1   0   1  % B6
%     ];
% y1 = 2-4i;
% y2 = 1/(0.05+2i);
% y3 = 1/(0.08+0.3i);
% y4 = 1/(0.05+0.25i);
% y5 = 4-8i;
% y6 = 1-3i;
% y7 = 1/(0.07+0.2i);
% y8 = 1/(0.12+0.26i);
% y9 = 1/(0.02+0.1i);
% y10 = 1-2i;
% y11 = 1-3i;
%
% y = [y1  0   0   0   0   0   0   0   0   0   0
%      0   y2  0   0   0   0   0   0   0   0   0
%      0   0   y3  0   0   0   0   0   0   0   0
%      0   0   0   y4  0   0   0   0   0   0   0
%      0   0   0   0   y5  0   0   0   0   0   0
%      0   0   0   0   0   y6  0   0   0   0   0
%      0   0   0   0   0   0   y7  0   0   0   0
%      0   0   0   0   0   0   0   y8  0   0   0
%      0   0   0   0   0   0   0   0   y9  0   0
%      0   0   0   0   0   0   0   0   0   y10 0
%      0   0   0   0   0   0   0   0   0   0   y11
%     ];
%
% Y = (-A)*y*(-A.');
% Y = [
%         4-11.748i      -2+4i            0        -1.176+4.7i     -0.83+3.11i       0
%          -2+4i      12.56-27.35i   -0.77+3.85i      -4+8i            -1+3i      -1.56+4.454i
%            0         -0.77+3.85i    4.156-16.567i     0         -1.46+3.17i   -1.923+9.62i
%         -1.176+4.7i     -4+8i           0        6.176-14.636i      -1+2i           0
%         -0.83+3.11i     -1+3i       -1.46+3.17i     -1+2i          3.83-11i      -1+3i
%            0         -1.56+4.454i   -1.92+9.62i       0             -1+3i      -4.482-17i
%     ];
% Z = inv(Y);

mpc = case6ww;

Sref = mpc.baseMVA; % in MVA
Vref = mpc.bus(1,10); % in kV
Zref = ((Vref*1000)^2)/(Sref*1e6);
Iref = (Vref*1000)/Zref;
[Ybus, Yf, Yt] = makeYbus(mpc);
Z = inv(Ybus);
%% Calculating the linear factors
% The current injection distribution factor CIDF
CIDF = zeros(c,r);
for m = 1:c % m is for the monitored lines (rows)
    for inj = 1:r % inj is for the injection buses (cols)
        fm = Bus_connections(m,1); % from bus index for monitored line
        tm = Bus_connections(m,2); % to bus index for monitored line
        CIDF(m,inj) = (Z(fm,inj)-Z(tm,inj))/z(m);
    end
end

% The Line-outage distribution factor LODF
LODF = zeros(c,c);
for m = 1:c
    for o = 1:c
        fm = Bus_connections(m,1); % from bus index for monitored line
        tm = Bus_connections(m,2); % to bus index for monitored line
        fo = Bus_connections(o,1); % from bus index for outaged line
        to = Bus_connections(o,2); % to bus index for outaged line
        LODF(m,o) = ((Z(fm,to)-Z(fm,fo)-Z(tm,to)+Z(tm,fo))/(Z(fo,fo)+Z(to,to)-(2*Z(fo,to))-z(o)))*(z(o)/z(m));
    end
end
%% Running the study case
results = runpf(mpc);
Vmag = results.bus(:,8);
Vangle = results.bus(:,9);
VangleRad = deg2rad(Vangle);
[VpuReal, VpuImag] = pol2cart(VangleRad,Vmag);
Vpu = VpuReal + VpuImag.*1i;
% deltaVpu = zeros(
% for k = 1:c
%     fk = Bus_connections(k,1); % from bus index for the selected line
%     tk = Bus_connections(k,2); % to bus index for the selected line
%     deltaVpu(k,1) = Vpu(fk) - Vpu(tk);
% end
% If = (Yf*Vpu);
% It = (Yt*Vpu);
%[Ifmag_pu,Ifangle] = cart2pol(real(If),imag(If));
%Ifmag = Ifmag_pu*Iref;
I_branch_pu = zeros(c,1);
I_branch_mag = zeros(c,1);
for k = 1:c
    I_branch_pu(k) = ((Vpu(Bus_connections(k,1)))-(Vpu(Bus_connections(k,2))))/z(k);
    I_branch_mag(k) = abs(I_branch_pu(k))*Iref;
end

% Error estimation comparing LODF and ACPF
% Only one line is outed at a time and then all other lines are checked
Error = zeros(c,c);
for o = 1:c % index of the outed line
    mpc_mod = mpc;
    mpc_mod.branch(o,:) = [];
    results_mod = runpf(mpc_mod);
    Vmag_mod = results_mod.bus(:,8);
    Vangle_mod = results_mod.bus(:,9);
    VangleRad_mod = deg2rad(Vangle_mod);
    [VpuReal_mod, VpuImag_mod] = pol2cart(VangleRad_mod,Vmag_mod);
    Vpu_mod = VpuReal_mod + VpuImag_mod.*1i;
    for m = 1:c % index of the monitored line
        I_pu_LODF = (I_branch_pu(o)*LODF(m,o)) + I_branch_pu(m);
        I_mag_LODF = abs(I_pu_LODF)*Iref;
        I_pu_PF = ((Vpu_mod(Bus_connections(m,1)))-(Vpu_mod(Bus_connections(m,2))))/z(m);
        I_mag_PF = abs(I_pu_PF)*Iref;
        Error(m,o) = ((I_mag_LODF-I_mag_PF)/I_mag_PF)*100;
    end
end

% % Comparing outing line 11 then 10 to the opposite
% I_pu_PF = ((Vpu(Bus_connections(1,1)))-(Vpu(Bus_connections(1,2))))/z(1);
% I1a0 = abs(I_pu_PF)*Iref;
% % Case A line 11 then 10
% mpc_mod = mpc;
% mpc_mod.branch(11,:) = [];
% results_mod = runpf(mpc_mod);
% Vmag_mod = results_mod.bus(:,8);
% Vangle_mod = results_mod.bus(:,9);
% VangleRad_mod = deg2rad(Vangle_mod);
% [VpuReal_mod, VpuImag_mod] = pol2cart(VangleRad_mod,Vmag_mod);
% Vpu_mod = VpuReal_mod + VpuImag_mod.*1i;
% I_pu_PF = ((Vpu_mod(Bus_connections(1,1)))-(Vpu_mod(Bus_connections(1,2))))/z(1);
% I1a1_PF = abs(I_pu_PF)*Iref;
% I_pu_LODF = (I_branch_pu(11)*LODF(1,11)) + I_branch_pu(1);
% I1a1_LODF = abs(I_pu_LODF)*Iref;
% [Ybus_mod, Yf_mod, Yt_mod] = makeYbus(mpc_mod);
% Z_mod = inv(Ybus_mod);
% LODF_moda = zeros(c-1,c-1);
% for m = 1:c-1
%     for o = 1:c-1
%         fm = Bus_connections(m,1); % from bus index for monitored line
%         tm = Bus_connections(m,2); % to bus index for monitored line
%         fo = Bus_connections(o,1); % from bus index for outaged line
%         to = Bus_connections(o,2); % to bus index for outaged line
%         LODF_moda(m,o) = ((Z_mod(fm,to)-Z_mod(fm,fo)-Z_mod(tm,to)+Z_mod(tm,fo))/(Z_mod(fo,fo)+Z_mod(to,to)-(2*Z_mod(fo,to))-z(o)))*(z(o)/z(m));
%     end
% end
% mpc_mod.branch(10,:) = [];
% results_mod = runpf(mpc_mod);
% Vmag_mod = results_mod.bus(:,8);
% Vangle_mod = results_mod.bus(:,9);
% VangleRad_mod = deg2rad(Vangle_mod);
% [VpuReal_mod, VpuImag_mod] = pol2cart(VangleRad_mod,Vmag_mod);
% Vpu_mod = VpuReal_mod + VpuImag_mod.*1i;
% I_pu_PF = ((Vpu_mod(Bus_connections(1,1)))-(Vpu_mod(Bus_connections(1,2))))/z(1);
% I1a2_PF = abs(I_pu_PF)*Iref;
% I_pu_LODF = (I_branch_pu(10)*LODF(1,10)) + I_branch_pu(1);
% I1a2_LODF = abs(I_pu_LODF)*Iref;
%
% % Case B line 10 then 11
% mpc_mod = mpc;
% mpc_mod.branch(10,:) = [];
% results_mod = runpf(mpc_mod);
% Vmag_mod = results_mod.bus(:,8);
% Vangle_mod = results_mod.bus(:,9);
% VangleRad_mod = deg2rad(Vangle_mod);
% [VpuReal_mod, VpuImag_mod] = pol2cart(VangleRad_mod,Vmag_mod);
% Vpu_mod = VpuReal_mod + VpuImag_mod.*1i;
% I_pu_PF = ((Vpu_mod(Bus_connections(1,1)))-(Vpu_mod(Bus_connections(1,2))))/z(1);
% I1b1_PF = abs(I_pu_PF)*Iref;
% I_pu_LODF = (I_branch_pu(10)*LODF(1,10)) + I_branch_pu(1);
% I1b1_LODF = abs(I_pu_LODF)*Iref;
% [Ybus_mod, Yf_mod, Yt_mod] = makeYbus(mpc_mod);
% Z_mod = inv(Ybus_mod);
% LODF_modb = zeros(c-1,c-1);
% for m = 1:c-1
%     for o = 1:c-1
%         fm = Bus_connections(m,1); % from bus index for monitored line
%         tm = Bus_connections(m,2); % to bus index for monitored line
%         fo = Bus_connections(o,1); % from bus index for outaged line
%         to = Bus_connections(o,2); % to bus index for outaged line
%         LODF_modb(m,o) = ((Z_mod(fm,to)-Z_mod(fm,fo)-Z_mod(tm,to)+Z_mod(tm,fo))/(Z_mod(fo,fo)+Z_mod(to,to)-(2*Z_mod(fo,to))-z(o)))*(z(o)/z(m));
%     end
% end
% mpc_mod.branch(10,:) = [];
% results_mod = runpf(mpc_mod);
% Vmag_mod = results_mod.bus(:,8);
% Vangle_mod = results_mod.bus(:,9);
% VangleRad_mod = deg2rad(Vangle_mod);
% [VpuReal_mod, VpuImag_mod] = pol2cart(VangleRad_mod,Vmag_mod);
% Vpu_mod = VpuReal_mod + VpuImag_mod.*1i;
% I_pu_PF = ((Vpu_mod(Bus_connections(1,1)))-(Vpu_mod(Bus_connections(1,2))))/z(1);
% I1b2_PF = abs(I_pu_PF)*Iref;
% I_pu_LODF = (I_branch_pu(10)*LODF(1,10)) + I_branch_pu(1);
% I1b2_LODF = abs(I_pu_LODF)*Iref;

% Error estimation comparing LODF and Actual current in the SC conditions
% Only one line is outed at a time and then all other lines are checked
Error_sc = zeros(c,c);
Isc = 5*[8+0.405i;1.72-1.021i;3.464-2i;0;0;0];
Isc(5) = -sum(Isc);
Vsc = Z*Isc;
I_branch_pu_sc = zeros(c,1);
I_branch_mag_sc = zeros(c,1);
I_branch_all_1outed = zeros(c,c);
for k = 1:c
    I_branch_pu_sc(k) = ((Vsc(Bus_connections(k,1)))-(Vsc(Bus_connections(k,2))))/z(k);
    I_branch_mag_sc(k) = abs(I_branch_pu_sc(k))*Iref;
end
for o = 1:c % index of the outed line
    mpc_mod = mpc;
    mpc_mod.branch(o,:) = [];
    [Ybus, Yf, Yt] = makeYbus(mpc_mod);
    Zmod = inv(Ybus);
    Vpu_mod = Zmod*Isc;
    for m = 1:c % index of the monitored line
        I_pu_LODF_sc = (I_branch_pu_sc(o)*LODF(m,o)) + I_branch_pu_sc(m);
        I_mag_LODF_sc = abs(I_pu_LODF_sc)*Iref;
        I_pu_PF_sc = ((Vpu_mod(Bus_connections(m,1)))-(Vpu_mod(Bus_connections(m,2))))/z(m);
        I_mag_PF_sc = abs(I_pu_PF_sc)*Iref;
        I_branch_all_1outed(m,o) = I_mag_PF_sc/10;
        Error_sc(m,o) = ((I_mag_LODF_sc-I_mag_PF_sc)/I_mag_PF_sc)*100;
    end
end
%% 

Error_oneLine = zeros(c,c);
for m = 1:6
    mpc_mod = mpc;
    mpc_mod.branch(c,:) = [];
    for o = c-1:-1:7
        mpc_mod.branch(o,:) = [];
        [Ybus1, Yf1, Yt1] = makeYbus(mpc_mod);
        Zmod = inv(Ybus1);
        Vpu_mod = Zmod*Isc;
        I_pu_LODF_sc = (I_branch_pu_sc(o)*LODF(m,o)) + I_branch_pu_sc(m);
        I_mag_LODF_sc = abs(I_pu_LODF_sc)*Iref;
        I_pu_PF_sc = ((Vpu_mod(Bus_connections(m,1)))-(Vpu_mod(Bus_connections(m,2))))/z(m);
        I_mag_PF_sc = abs(I_pu_PF_sc)*Iref;
        Error_oneLine(m,o) = ((I_mag_LODF_sc-I_mag_PF_sc)/I_mag_PF_sc)*100;
    end
end