A1 = importdata("Uppaal_code_generation.xml"); % Import the Uppaal model as a string array
regexploc = regexpcell(A1,'(!CB1.Open &amp;&amp; CB5.Open &amp;&amp; F500)||'); % find the location of the expression you want to remove
dummie = regexprep(A1(regexploc), '\(!CB1.Open &amp;&amp; CB5.Open &amp;&amp; F1\)\|\|', ''); % Replace the expression with nothing
A1{regexploc,1} = dummie; % Add the new expression to the string array
writecell(A1,'Uppaal_code_generation.xml',FileType='text',QuoteStrings='none'); % write the array back to the same file
