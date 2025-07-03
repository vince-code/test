clear all
clc
tstart = tic;

%% For this section use "Reading_json_data1" and "GenUppaalCode1"

%  fileName = 'Example_10bus.json'; % <--

% fileName = 'Example_20bus.json'; % <--

% fileName = 'Example_20bus_part_1a_10_faults_new.json'; 
% fileName = 'Example_20bus_part_2a_10_faults_new.json'; 

% fileName = 'Example_20bus_part_1b_7_faults_new.json'; 
% fileName = 'Example_20bus_part_2b_7_faults_new.json';
% fileName = 'Example_20bus_part_3b_7_faults_new.json';

% fileName = 'Example_20bus_part_1c_1bus.json'; 
% fileName = 'Example_20bus_part_2c_1bus.json'; 
% fileName = 'Example_20bus_part_3c_1bus.json'; 
% fileName = 'Example_20bus_part_4c_1bus.json'; 
% fileName = 'Example_20bus_part_5c_1bus.json'; 
% fileName = 'Example_20bus_part_6c_1bus.json'; 
% fileName = 'Example_20bus_part_7c_1bus.json'; 
% fileName = 'Example_20bus_part_8c_1bus.json'; 
% fileName = 'Example_20bus_part_9c_1bus.json'; 

% fileName = 'Example_30bus.json'; % <--
% fileName = 'Example_30bus_part_1a_15_faults_new.json'; 
% fileName = 'Example_30bus_part_2a_15_faults_new.json'; 
% fileName = 'Example_30bus_part_1b_10_faults_new.json'; 
% fileName = 'Example_30bus_part_2b_10_faults_new.json'; 
% fileName = 'Example_30bus_part_3b_10_faults_new.json'; 



% fileName = 'Example_40bus.json'; % <--
% fileName = 'Example_40bus_part_1a_14_faults_new.json'; 
% fileName = 'Example_40bus_part_2a_14_faults_new.json'; 
% fileName = 'Example_40bus_part_3a_15_faults_new.json';

% fileName = 'Example_40bus_part_1b_10_faults_new.json'; 
% fileName = 'Example_40bus_part_2b_10_faults_new.json'; 
% fileName = 'Example_40bus_part_3b_10_faults_new.json';
% fileName = 'Example_40bus_part_4b_10_faults_new.json';



% fileName = 'Example_50bus.json'; % <--
% fileName = 'Example_50bus_part_1a_21_faults_new.json'; 
% fileName = 'Example_50bus_part_2a_16_faults_new.json'; 
% fileName = 'Example_50bus_part_3a_16_faults_new.json';

% fileName = 'Example_50bus_part_1b_13_faults_new.json'; 
% fileName = 'Example_50bus_part_2b_13_faults_new.json'; 
% fileName = 'Example_50bus_part_3b_13_faults_new.json';
% fileName = 'Example_50bus_part_4b_13_faults_new.json';

% fileName = 'Example_50bus_part_1c_10_faults_new.json'; 
% fileName = 'Example_50bus_part_2c_10_faults_new.json'; 
% fileName = 'Example_50bus_part_3c_10_faults_new.json';
% fileName = 'Example_50bus_part_4c_10_faults_new.json';
% fileName = 'Example_50bus_part_5c_10_faults_new.json'; 

% fileName = 'Example_50bus_part_1d_8_faults_new.json'; 
% fileName = 'Example_50bus_part_2d_8_faults_new.json'; 
% fileName = 'Example_50bus_part_3d_8_faults_new.json';
% fileName = 'Example_50bus_part_4d_8_faults_new.json';
% fileName = 'Example_50bus_part_5d_8_faults_new.json'; 
% fileName = 'Example_50bus_part_6d_8_faults_new.json';

% fileName = 'Example_50bus_part_1e_7_faults_new.json'; 
% fileName = 'Example_50bus_part_2e_7_faults_new.json'; 
% fileName = 'Example_50bus_part_3e_7_faults_new.json';
% fileName = 'Example_50bus_part_4e_7_faults_new.json';
% fileName = 'Example_50bus_part_5e_7_faults_new.json'; 
% fileName = 'Example_50bus_part_6e_7_faults_new.json'; 
% fileName = 'Example_50bus_part_7e_7_faults_new.json'; 

% fileName = 'Example_Open_Ring_4_DZ.json';

fileName = 'Example_60bus.json'; % <--


%% Method 1: Load the full network to then divide it in length(F_bus) parts



[A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);

num=fix(length(F_branch)/length(F_bus));
rest=mod(length(F_branch),length(F_bus));
F_branch_matrix=zeros(length(F_bus),num+1);
k=1;
for i=1:length(F_bus)
    for j=1:num+1
        if i+rest<=length(F_bus) & j==num+1
            F_branch_matrix(i,j)=0;
        else
            F_branch_matrix(i,j)=F_branch(k);
            k=k+1;
        end
    end
end   


Myfile=fopen("output4.log", "w");
for i=1:length(F_bus)
    if i+rest<=length(F_bus) 
        GenUppaalCode1(A,Line,Bus,F_branch_matrix(i,1:end-1),F_bus(i),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections);
        [status,output]=system('D:\Work\Uppaal\uppaal64-4.1.26-1\uppaal64-4.1.26-1\bin-Windows\verifyta -t0 ..\..\Github\output\Uppaal_code_generation.xml');
        fprintf(Myfile,'%s',output);
    else
        GenUppaalCode1(A,Line,Bus,F_branch_matrix(i,1:end),F_bus(i),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections);
        [status,output]=system('D:\Work\Uppaal\uppaal64-4.1.26-1\uppaal64-4.1.26-1\bin-Windows\verifyta -t0 ..\..\Github\output\Uppaal_code_generation.xml');
        fprintf(Myfile,'%s',output);
    end

end
fclose(Myfile);

tfullcode = toc(tstart);





%% Old code
% tstart_CodeGen = tic; 
% 
%  [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);
%  treading=toc(tstart_CodeGen);
%  GenUppaalCode1(A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections);
% 
%  tCodeGen = toc(tstart_CodeGen);
%  system('../../UPPAAL/Contents/Resources/uppaal/bin/verifyta -t0 ../../My_folder/output/Uppaal_code_generation.xml > output4.log 2> trace4.txt');
%  tfullcode = toc(tstart);
%  tverification = tfullcode - tCodeGen;
% %%%[user,sys] = memory;


%% Check if there is a problem

tstart_Check=tic;

Myfile=fopen("output4.log", "r");
eof=0; 
Error='[2K -- Formula is satisfied.' ;  
idx=0;
error_presence=0;
while eof==0 
    rec =fgetl(Myfile); 
    idx=idx+1;
    if (rec==-1) & error_presence==0
        fprintf("\nEverything is fine\n")
        eof=1; 
    elseif (rec==-1) & error_presence==1
        eof=1;

    elseif length(rec)==length(Error)
        if rec==Error
            fprintf("ERROR. Something is wrong in the %d row of the output file. Check this: %s\n",idx,rec_p)
             error_presence=1;
        end
    end
    rec_p=rec;
end
fclose(Myfile);

tCheck=toc(tstart_Check);

tTotal=toc(tstart);
