function [] = GenPreamble(fileID)
% This function just writes a few lines of code at the beginning of the Uppaal file
    fprintf(fileID,"<?xml version=""1.0"" encoding=""utf-8""?>\n<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd'>\n");
end