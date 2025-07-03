function workingDirectory = pwd

    persistent storedPath;

    if isempty(storedPath)
        storedPath = cd;
    end

    workingDirectory = storedPath;
end