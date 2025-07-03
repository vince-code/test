function [] = GenQueries(fileID,Line,Bus,idx_source,dep,F_branch,F_bus,srcBusIdx,A,CB_settings,idx_link_lines)
fprintf(fileID,'\n<queries>\n');
% for k = 1:length(Line)
%     if ~ismember(k,idx_source)
%         x = dep{1,k};
%         for kk = 1:length(x)
%             fprintf(fileID,'  <query>\n');
%             fprintf(fileID,'    <formula>E&lt;&gt;(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)</formula>\n',Line(k),Line(x(kk)),Line(k));
%             fprintf(fileID,'    <comment></comment>\n');
%             fprintf(fileID,'  </query>\n');
%         end
%     end
% end
% fprintf(fileID,'  <query>\n');
% fprintf(fileID,'    <formula>E&lt;&gt;(');
% if ~ismember(1,idx_source)
%     x = dep{1,1};
%     fprintf(fileID,'(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',Line(1),Line(x(1)),Line(1));
%     for kk = 2:length(x)
%         fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',Line(1),Line(x(kk)),Line(1));
%     end
% end
% for k = 2:length(Line)
%     if ~ismember(k,idx_source)
%         x = dep{1,k};
%         for kk = 1:length(x)
%             fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',Line(k),Line(x(kk)),Line(k));
%         end
%     end
% end
% fprintf(fileID,')</formula>\n');
% fprintf(fileID,'    <comment></comment>\n');
% fprintf(fileID,'  </query>\n');

fprintf(fileID,'  <query>\n');
fprintf(fileID,'    <formula>E&lt;&gt;(');

% Finding out which lines have circuit breakers
Lines_CB_IDs = []; % Variable containing the line IDs in col 1 and the corresponding CB IDs in col 2
for kk = 1:length(CB_settings.CB)
    Lines_CB_IDs = [Lines_CB_IDs;CB_settings.CB{1,kk}.Line_ID,CB_settings.CB{1,kk}.CB_ID];
end


% Queries for the faulted lines
if ~ismember(F_branch(1),idx_source)
    if ~ismember(F_branch(1),idx_link_lines)
        x = dep{1,F_branch(1)}; % finding the dependent lines upstream
        CB_Idx = find(Lines_CB_IDs(:,1)==Line(x(1)));
        if ~isempty(CB_Idx) % checking to see if the dependent lines have CBs or not
            faulted_CB_Idx = find(Lines_CB_IDs(:,1)==Line(F_branch(1)));
            faulted_CB_ID = Lines_CB_IDs(faulted_CB_Idx,2);
            Upstream_CB_ID = Lines_CB_IDs(CB_Idx,2);
            fprintf(fileID,'(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
        end
        for kk = 2:length(x)
            CB_Idx = find(Lines_CB_IDs(:,1)==Line(x(kk)));
            if ~isempty(CB_Idx) % checking to see if the dependent lines have CBs or not
                Upstream_CB_ID = Lines_CB_IDs(CB_Idx,2);
                fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(1)));
            end
        end
    end
end

for k = 2:length(F_branch)
    if ~ismember(F_branch(k),idx_source) && ~ismember(F_branch(k),idx_link_lines)
        x = dep{1,F_branch(k)};
        for kk = 1:length(x)
            CB_Idx = find(Lines_CB_IDs(:,1)==Line(x(kk)));
            if ~isempty(CB_Idx) % checking to see if the dependent lines have CBs or not
                faulted_CB_Idx = find(Lines_CB_IDs(:,1)==Line(F_branch(k)));
                faulted_CB_ID = Lines_CB_IDs(faulted_CB_Idx,2);
                Upstream_CB_ID = Lines_CB_IDs(CB_Idx,2);
                fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; F%i)',faulted_CB_ID,Upstream_CB_ID,Line(F_branch(k)));
            end
        end
    end
end
%% Queries for the faulted Buses
for k = 1:length(F_bus)
    if ~ismember(F_bus(k),srcBusIdx)
        y = find(A(F_bus(k),:)==1);
        for pp = 1:length(y)
            x = dep{1,y(pp)};
            for kk = 1:length(x)
                CB_Idx = find(Lines_CB_IDs(:,1)==Line(x(kk)));
                if ~isempty(CB_Idx) % checking to see if the dependent lines have CBs or not
                    faulted_CB_Idx = find(Lines_CB_IDs(:,1)==Line(y(pp)));
                    faulted_CB_ID = Lines_CB_IDs(faulted_CB_Idx,2);
                    Upstream_CB_ID = Lines_CB_IDs(CB_Idx,2);
                    fprintf(fileID,'||(!CB%i.Open &amp;&amp; CB%i.Open &amp;&amp; FB%i)',faulted_CB_ID,Upstream_CB_ID,Bus(F_bus(k)));
                end
            end
        end
    end
end

fprintf(fileID,')</formula>\n');
fprintf(fileID,'    <comment></comment>\n');
fprintf(fileID,'  </query>\n');

% The deadlock query
fprintf(fileID,'  <query>\n');
fprintf(fileID,'    <formula>E&lt;&gt; deadlock</formula>\n');
fprintf(fileID,'    <comment></comment>\n');
fprintf(fileID,'  </query>\n');
fprintf(fileID,'</queries>\n');
end