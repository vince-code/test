clear all
clc
tstart = tic;

%% Method 3:

%% Selection of the Network

% fileName = 'Example_10bus.json'; % <--



 fileName = 'Example_20bus.json'; % <--



% fileName = 'Example_30bus.json'; % <--



% fileName = 'Example_40bus.json'; % <--



% fileName = 'Example_50bus.json'; % <--



% fileName = 'Example_60bus.json'; % <--



% fileName = 'Example_70bus.json'; % <--



% fileName = 'Example_80bus.json'; % <--



% fileName = 'Example_90bus.json'; % <--



% fileName = 'Example_100bus.json'; % <--






%%% fileName = 'Example_10bus_test.json'; % <--



 fileName = 'Example_50bus_test.json'; % <--



%%% fileName = 'Example_100bus_test.json'; % <--


%% json file reading 
[A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fileName);

%% Analysis of the network
Total_faults=length(F_bus)+length(F_branch);

if mod(Total_faults,8)==0
    div=floor(Total_faults/8);
else
    div=floor(Total_faults/8)+mod(Total_faults,8)/mod(Total_faults,8);
end

 %% Time estimation
% 
% Table=readtable("Data_For_Estimation.xlsx");
% Num_lines=length(Line);
% i_table=1;
% while i_table<=length(Table.N_Lines)
%     if Table.N_Lines(i_table)>=Num_lines
%         break
%     end
%     i_table=i_table+1;
% end
% i_table=i_table-1;



%% Creation of F_branch_matrix and F_bus_matrix

num=fix(length(F_branch)/div);
rest=mod(length(F_branch),div);
k=1;
for i=1:div
    for j=1:num+1
        if k>length(F_branch)            
            F_branch_matrix(i,j)=0;
        elseif i+rest<=div & j==num+1
            if j==1
                F_branch_matrix(i,j)=F_branch(k);
            else
            F_branch_matrix(i,j)=0;
            end
        else
            F_branch_matrix(i,j)=F_branch(k);
            k=k+1;
        end
    end
end
[F_branch_rows,F_branch_cols]=size(F_branch_matrix);


num_bus=fix(length(F_bus)/div);
rest_bus=mod(length(F_bus),div);
k=1;
for i=1:div
    for j=1:num_bus+1
        if k>length(F_bus)
            
           
            F_bus_matrix(i,j)=0;

        elseif i+rest_bus<=div & j==num_bus+1
            F_bus_matrix(i,j)=0;
        else
            F_bus_matrix(i,j)=F_bus(k);
            k=k+1;
        end
    end
end

%% Verification phase
tverstart=tic;
verification_time=0;
Myfile=fopen("output4.log", "w");
for i=1:div
    if isempty(F_branch)
        fprintf("Minimum number of faults not selected. \nPlease select at least 1 line fault. \n")
        break
    end
    file_names_uppaal(i)=strcat("Uppaal_code_generation_",string(i),".xml");
    file_path=fullfile('../output/',file_names_uppaal(i));
    file_path_system=fullfile('../../My_folder/output/',file_names_uppaal(i));
    file_output='../../UPPAAL/Contents/Resources/uppaal/bin/verifyta -t0 ' + file_path_system;
    if (i+rest>div && i+rest_bus<=div) || (F_branch_cols==1)
        GenUppaalCode2(A,Line,Bus,F_branch_matrix(i,1:end),F_bus_matrix(i,1:end-1),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections,file_path);
        t_excess=toc(tverstart);
        [status,output]=system(file_output);
        verification_time=verification_time+toc(tverstart)-t_excess;
        fprintf(Myfile,'%s',output);
    elseif (i+rest<=div && i+rest_bus>div)  
        GenUppaalCode2(A,Line,Bus,F_branch_matrix(i,1:end-1),F_bus_matrix(i,1:end),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections,file_path);
        t_excess=toc(tverstart);
        [status,output]=system(file_output);
        verification_time=verification_time+toc(tverstart)-t_excess;
        fprintf(Myfile,'%s',output);
    elseif i+rest<=div && i+rest_bus<=div
        GenUppaalCode2(A,Line,Bus,F_branch_matrix(i,1:end-1),F_bus_matrix(i,1:end-1),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections,file_path);
        t_excess=toc(tverstart);
        [status,output]=system(file_output);
        verification_time=verification_time+toc(tverstart)-t_excess;
        fprintf(Myfile,'%s',output);    
    else
        GenUppaalCode2(A,Line,Bus,F_branch_matrix(i,1:end),F_bus_matrix(i,1:end),Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections,file_path);
        t_excess=toc(tverstart);
        [status,output]=system(file_output);
        verification_time=verification_time+toc(tverstart)-t_excess;
        fprintf(Myfile,'%s',output);
    end
end
fclose(Myfile);

tfullcode = toc(tstart);





%% Check if there is a problem

tstart_Check=tic;

Myfile=fopen("output4.log", "r");
eof=0; 
Error='[2K -- Formula is satisfied.' ;
End_Error='[2K';
idx=0;
error_presence=0;
count=0;
Myerror=fopen("error_trace.log","w");
if isempty(F_branch)
    eof=1;
end
while eof==0 
    rec =fgetl(Myfile); 
    idx=idx+1;
    if count~=0 & length(rec)==length(End_Error)
        if rec==End_Error
            count=0;
            fprintf(Myerror,'%s\n\n\n\n\n',"This is the end of this trace.");
        end
        
    end
    if count~=0
        fprintf(Myerror,'%s\n',rec);
        testttttt=rec;
        if length(rec_p)==length('Transition:')
            if rec(3:13)=="FG.No_Fault"
                for pos=12:length(rec)
                    if rec(pos)=="F" & rec(pos+1)~="G" & rec(pos+1)~="a"
                        break
                    end
                end
                fprintf("%s\n",rec(pos:end-1))
            end
            pos_2=strfind(rec,".Standby");
            if length(pos_2)==1
                fprintf("%sOpen\n",rec(3:pos_2))
            end
        end
    end
    if (rec==-1) & error_presence==0
        fprintf("Everything is fine\n")
        eof=1; 
    elseif (rec==-1) & error_presence==1
        eof=1;

    elseif length(rec)==length(Error)
        if rec==Error
            fprintf("ERROR. Something is wrong in the %d row of the output file. Check this:\n",idx)
             error_presence=1;
             count=1;
        end
    end
    rec_p=rec;
end
fclose(Myfile);

tCheck=toc(tstart_Check);

tTotal=tfullcode+tCheck;

