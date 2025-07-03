clear all
clc
%% Inputs
mpc = case7_radial;

c = size(mpc.branch,1); % c is the number of branches while r is the number of buses
r = size(mpc.bus,1);
z = zeros(c,1);
for k = 1:c
    z(k,1) = (mpc.branch(k,3)) + (mpc.branch(k,4))*1i;
end
Bus_connections = mpc.branch(:,1:2);
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
I_branch_pu = zeros(c,1);
I_branch_mag = zeros(c,1);
for k = 1:c
    I_branch_pu(k) = ((Vpu(Bus_connections(k,1)))-(Vpu(Bus_connections(k,2))))/z(k);
    I_branch_mag(k) = abs(I_branch_pu(k))*Iref;
end

% Error estimation comparing LODF and ACPF
% Only one line is outed at a time and then all other lines are checked
Error = zeros(c,c);
for o = 2:c % index of the outed line
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
