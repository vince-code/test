clear all;
clc;

% === CONFIG ===
JSON_FOLDER = fullfile(pwd, 'jsonNet');            % Path to input .json files
OUTPUT_FOLDER = fullfile(pwd, 'splitReferences');   % Output folder
mkdir(OUTPUT_FOLDER);                               % Ensure output dir exists

% === FETCH JSON FILES ===
jsonFiles = dir(fullfile(JSON_FOLDER, '*.json'));

for i = 1:length(jsonFiles)
    % --- Load JSON ---
    fileName = jsonFiles(i).name;
    fullPath = fullfile(JSON_FOLDER, fileName);
    [~, nameOnly, ~] = fileparts(fileName);

    fprintf('Processing %s...\n', fileName);
    
    try
        % --- Read network data ---
        [A,Line,Bus,F_branch,F_bus,Irc,C,Iioc_values,Ith_values,Isc_values_Lines,Isc_values_Buses,CB_settings,Bus_connections] = Reading_json_data1(fullPath);
        
        % --- Compute fault subdivision (copied from APV_resetFcnTest) ---
        Total_faults = length(F_bus) + length(F_branch);
        if mod(Total_faults,8)==0
            div = floor(Total_faults/8);
        else
            div = floor(Total_faults/8)+mod(Total_faults,8)/mod(Total_faults,8);
        end

        num = fix(length(F_branch)/div);
        rest = mod(length(F_branch),div);
        k = 1;
        for d = 1:div
            for j = 1:num+1
                if k > length(F_branch)
                    F_branch_matrix(d,j) = 0;
                elseif d + rest <= div && j == num+1
                    F_branch_matrix(d,j) = 0;
                else
                    F_branch_matrix(d,j) = F_branch(k);
                    k = k + 1;
                end
            end
        end

        num_bus = fix(length(F_bus)/div);
        rest_bus = mod(length(F_bus),div);
        k = 1;
        for d = 1:div
            for j = 1:num_bus+1
                if k > length(F_bus)
                    F_bus_matrix(d,j) = 0;
                elseif d + rest_bus <= div && j == num_bus+1
                    F_bus_matrix(d,j) = 0;
                else
                    F_bus_matrix(d,j) = F_bus(k);
                    k = k + 1;
                end
            end
        end

        % --- Build result struct ---
        splitResult = struct();
        splitResult.div = div;
        splitResult.F_branch_matrix = F_branch_matrix;
        splitResult.F_bus_matrix = F_bus_matrix;

        % --- Save to .json ---
        jsonOut = jsonencode(splitResult);
        fid = fopen(fullfile(OUTPUT_FOLDER, [nameOnly, '_split.json']), 'w');
        fwrite(fid, jsonOut, 'char');
        fclose(fid);
        fprintf('Saved split to %s\n', [nameOnly, '_split.json']);

    catch ME
        warning('Error processing %s: %s', fileName, ME.message);
    end
end

fprintf('All splits generated.\n');