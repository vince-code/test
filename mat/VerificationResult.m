classdef VerificationResult
    %VerificationResult Wrapper class for the Java object 
    %polimi.model.VerificationResult
    
    properties
        % boolean - Whether the query verification is satisfied or not
        PropertySatisfied
        % string - 'F1'/'FB1' - Where the fault happend (if not satisfied) 
        Fault
        % array - Array of misconfigured CBs (if not satisfied)
        MisconfiguredCBIds
        % string - Trace of the Uppaal verification (if not satisfied)
        Trace
    end
    
    methods
        function obj = VerificationResult(JavaAPIVerificationResult)
            %% VerificationResult Creates a VerificationResult object
            %starting from polimi.model.VerificationResult

            if ~isa(JavaAPIVerificationResult, 'polimi.model.VerificationResult')
                error('Input must be a VerificationResult Java object');
            end
           
            obj.PropertySatisfied = JavaAPIVerificationResult.isSatisfied();

            fault = JavaAPIVerificationResult.getFault();
            if isempty(fault)
                obj.Fault = 'NO_FAULT';
            else
                obj.Fault = fault;
            end
            
            misconfiguredCBs = JavaAPIVerificationResult.getMisconfiguredCBsArray();
            if isempty(misconfiguredCBs)
                obj.MisconfiguredCBIds = [];
            else
                obj.MisconfiguredCBIds = misconfiguredCBs;
            end

            trace = JavaAPIVerificationResult.getTrace();
            if isempty(trace)
                obj.Trace = '';
            else
                obj.Trace = trace;
            end
        end
    end
end

