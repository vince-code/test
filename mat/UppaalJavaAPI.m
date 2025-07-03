classdef UppaalJavaAPI < handle
    % UppaalJavaAPI MATLAB wrapper for ABB-ProtectionsChecker
    %
    %   This class provides MATLAB access to a Java library (ABB-ProtectionsChecker.jar) for:
    %   - loading (RADIAL) power network configurations
    %   - generating and saving UPPAAL models for power netoworks
    %   - creating and managing a pool of UPPAAL engines
    %   - performing network model checking via UPPAAL Java API 
    %   - customizing and verifying circuit breakers configurations
    %
    %   Main Usage Flow:
    %     1. Instanciate a UppaalJavaAPI object (preferably inside a function)
    %     2. Start engines with startEngines() function
    %     3. Load network JSON with loadNetwork() function
    %     4. Manipulate CB configuration with updateCBConfig() function
    %     5. Extract misconfigured CBs with extractMisconfiguredCBs() function
    %     6. Shutdown engines with shutdownEngines() function
    %
    %   Example:
    %     function runDemoWorkflow()
    %         JavaAPI = UppaalJavaAPI('/Applications/UPPAAL-5.1.0-beta5.app', '/Users/Shared/ABB-ProtectionsChecker.jar');  
    %         JavaAPI.startEngines(4);
    %         try
    %           JavaAPI.loadNetwork('/Users/Shared/Network.json');
    %           JavaAPI.generateUppaalModel('/Users/Shared', 'model');
    %           JavaAPI.updateCBConfig(23, 1);
    %           ids = JavaAPI.extractMisconfiguredCBs(8);
    %           JavaAPI.shutdownEngines();
    %         catch ME
    %           JavaAPI.shutdownEngines();
    %           rethrow(ME); 
    %         end
    %     end

    properties
        UppaalAbsolutePath
        EngineShutdownNeeded = false
    end

    methods
        function obj = UppaalJavaAPI(uppaalAbsolutePath, abbCheckerAbsolutePath)
            %% UppaalJavaAPI Creates a UppaalJavaAPI object
            % 
            % Running this constructor inside a function ensures that the
            % handle.delete() function gets automatically called when the function goes out of
            % scope launching in this way the shutdownEngine() function
            % even in case of unexpected interruptions to normal exuction
            % flow ensuring correct shutdown of the created Uppaal engines
            %   
            % Adds two jars into the java classpath:
            % - uppaal.jar - From UPPAAL app provided at UppaalAbsolutePath
            % needed to correctly run UPPAAL engines
            %
            % - ABB-ProtectionsChecker-1.0.jar - Main Java library used to execute
            % all the UppaalJavaAPI methods
            %
            %% INPUTS:
            % - uppaalAbsolutePath: Absolute path of the UPPAAL app
            % - abbCheckerAbsolutePath: Absolute path of the ABB-ProtectionsChecker jar
            %
            %% WARNING: 
            % The import of the uppaal.jar in the classpath via UppaalJavaAPI() 
            % breaks (until a Matlab reboot) the standard Matlab function 'pwd' used to
            % get the current working directory, since uppaal.jar contains a cd.class
            % that goes in conflict with the standard cd function used in pwd.
            
            % To bypass that I'm overriding the behaviour of the standard pwd by
            % recreating a custom pwd.m file at runtime that stores the working
            % directory at first run and reuses that value after the break of the
            % standard pwd
            % 
            obj.ensureCustomPwdExists();
            
            obj.UppaalAbsolutePath = fullfile(uppaalAbsolutePath, '/Contents/Resources/uppaal');

            jar1 = abbCheckerAbsolutePath;
            jar2 = fullfile(obj.UppaalAbsolutePath, 'uppaal.jar');
        
            currentPaths = javaclasspath('-dynamic');
        
            if ~ismember(jar1, currentPaths)
                javaaddpath(jar1);
            end
        
            if ~ismember(jar2, currentPaths)
                javaaddpath(jar2);
            end
        end
        
        %% prova
        function startEngines(obj, maxInstances)
            %% startEngines Starts a UPPAAL engines pool
            %
            %   startEngines(maxInstances)
            %
            %   Starts as many UPPAAL engines as stated in the maxInstances
            %   value
            %    
            %   After this function is launched you can see in you task manager 
            %   multiple active processes called "server". 
            % 
            %   After the usage all these instances need to be closed with
            %   the shutdownEngines() function or they will remain open. 
            %
            %   After a first call to the startEngines function, if the same
            %   function is called againg before closing the previous
            %   engines with the shutdownEngines function, the pool of
            %   active engines is just expanded to the newer value of
            %   maxInstances.
            %   
            %   If the UppaalJavaAPI object is not instanciated inside a function
            %   It is recommended to include everything after the call of
            %   startEngines in a try-catch block to ensure that the
            %   shutdownEngines functions is called even in case of unattended
            %   interruptions to normal execution flow.
            %   
            %%  WARNING!!!
            %     Some instances could fail to shutdown.
            %     
            %     After the shutdown check in your task manager if any "server" process is active and eventually close it manually.   
            %   
            %%  INPUTS:
            %     maxInstances   - Number of parallel Uppaal engine ("server")
            %     instances to start
            %
            %%  USAGE ESAMPLE:
            %     UppaalJavaAPI.startEngines(4);
            %     try
            %       ...
            %       UppaalJavaAPI.shutdownEngines(); 
            %     catch ME
            %       UppaalJavaAPI.shutdownEngines();
            %       rethrow(ME); 
            %     end
            %
            obj.EngineShutdownNeeded = true;

            polimi.JavaAPI.startUppaalEngines(obj.UppaalAbsolutePath, maxInstances);
        end

        function loadNetwork(obj, jsonPath)
            %% loadNetwork Load network configuration from JSON file
            %
            %   loadNetwork(jsonPath)
            %
            %%  INPUT:
            %     jsonPath - Absolute path to JSON file containing network structure
            %
            polimi.JavaAPI.loadNetwork(jsonPath);
        end

        function cbIds = extractMisconfiguredCBs(obj, maxFaults)
            %% extractMisconfiguredCBs Performs ASQ logic returning the misconfigured CBs
            %
            %   cbIds = extractMisconfiguredCBs(8)
            %
            %%  INPUT:
            %     maxFaults - Maximum number of faults to split the network
            %     for ASQ logic
            %
            %%  OUTPUT:
            %     cbIds - array of misconfigured CB IDs extracted performing ASQ
            %     logic on the network
            %
            var = nargin;
            if nargin == 2
                cbIds = polimi.JavaAPI.extractMisconfiguredCBs(maxFaults);
            else
                cbIds = polimi.JavaAPI.extractMisconfiguredCBs();
            end
            
        end

        function updateCBConfig(obj, cbId, t2)
            %% updateCBConfig Update CB t2 parameter
            %
            %  updateCBConfig(cbId, t2)
            %
            %%  INPUTS:
            %     cbId - Integer ID of the circuit breaker to update
            %     t2   - New t2 value
            %
            polimi.JavaAPI.updateCBConfig(cbId, t2);
        end

        function generateUppaalModel(obj, outputPath, outputName)
            %% generateUppaalModel Save UPPAAL model xml file
            %
            %   generateUppaalModel('/Users/Shared', 'Network1')
            
            %
            %%   INPUTS:
            %     outputPath - Full absolute path of the destination folder
            %     outputName - Base name for the desired output xml file
            %
            polimi.JavaAPI.generateUppaalModel(outputPath, outputName);
        end

        function generateSplitUppaalModels(obj, outputPath, outputName, maxFaults)
            %% generateSplitUppaalModels Save multiple Uppaal model splitted by faults
            %
            %   generateSplitUppaalModels('/Users/Shared', 'Network1', 8)
            %
            %%   INPUTS:
            %     outputPath - Full absolute path of the destination folder
            %     outputName - Base name for split models
            %     maxFaults  - Max accepted faults per split model
            %
            polimi.JavaAPI.generateSplitUppaalModels(outputPath, outputName, maxFaults);
        end

        function path = getNetworkPath(obj, startBusId, endBusId)
            %% getNetworkPath Compute lines path between two given buses
            %
            %   path = getNetworkPath(8, 9)
            %
            %%  OUTPUT:
            %     path - Ordered array of line IDs to get from startBus to endBus
            path = polimi.JavaAPI.getNetworkPath(startBusId, endBusId);
        end

        function query = generateDefaultQuery(obj)
            %% generateDefaultQuery Return default UPPAAL query string
            query = polimi.JavaAPI.generateDefaultQuery();
        end

        function query = generateFilteredQuery(obj, excludedCBIds)
            %% generateFilteredQuery Generates a filtered version of the default query
            %
            %   query = generateFilteredQuery(excludedCBIds)
            %
            %%  INPUT:
            %     excludedCBIds - Array of CB IDs to ignore in the query
            query = polimi.JavaAPI.generateFilteredQuery(excludedCBIds);
        end

        function result = verifyDefaultQuery(obj, withTrace)
            %% verifyDefaultQuery Run default query verification (NO ASQ)
            %
            %   result = verifyDefaultQuery(true)
            %
            %   INPUT:
            %     withTrace - boolean flag to generate and add the UPPAAL
            %     trace to the VerificationResult object in output
            %
            %   OUTPUT:
            %     result - VerificationResult object
            result = VerificationResult(polimi.JavaAPI.verifyDefaultQuery(withTrace));
        end

        function result = verifyQuery(obj, query, withTrace)
            %% verifyQuery Run custom query verification (NO ASQ)
            %
            %   result = verifyQuery('E<>()', false)
            %
            %   INPUTS:
            %     query     - custom query string
            %     withTrace - boolean flag to generate and add the UPPAAL
            %     trace to the VerificationResult object in output
            %
            %   OUTPUT:
            %     result - VerificationResult object
            result = VerificationResult(polimi.JavaAPI.verifyQuery(query, withTrace));
        end

        function shutdownEngines(obj)
            %% shutdownEngines Shutdown all UPPAAL engines and cleanup
            polimi.JavaAPI.shutdown();
            obj.EngineShutdownNeeded = false;
            java.lang.System.gc();
        end
    end
    methods (Access = private)
        function delete(obj)
            %% delete Automatic shutdown engines at any interruption
            if obj.EngineShutdownNeeded
                obj.shutdownEngines();
            end
        end
        function ensureCustomPwdExists(obj)
            %% ensureCustomPwdExists Creates a custom pwd.m
            pwdFunctionCode = [
                "function workingDirectory = pwd", newline, newline, ...
                "    persistent storedPath;", newline, newline, ...
                "    if isempty(storedPath)", newline, ...
                "        storedPath = cd;", newline, ...
                "    end", newline, newline, ...
                "    workingDirectory = storedPath;", newline, ...
                "end"
            ];

            if ~isfile('pwd.m') || ~strcmp(fileread('pwd.m'), strjoin(pwdFunctionCode, ''))
                fid = fopen('pwd.m', 'w');
                fprintf(fid, '%s', strjoin(pwdFunctionCode, ''));
                fclose(fid);
                rehash path
                clear pwd
            end
            save = pwd;
        end
    end
    methods (Hidden)
        addlistener
        eq
        findobj
        findprop
        ge
        notify
        ne
        lt
        le
        listener
        gt
    end
end