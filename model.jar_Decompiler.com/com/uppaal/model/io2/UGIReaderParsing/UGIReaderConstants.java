package com.uppaal.model.io2.UGIReaderParsing;

public interface UGIReaderConstants {
   int EOF = 0;
   int PROCESS = 5;
   int LOCATION = 6;
   int LOCATIONNAME = 7;
   int BRANCHPOINT = 8;
   int TEMPLATENAME = 9;
   int PARAMLIST = 10;
   int GLOBALDECL = 11;
   int IMPORTS = 12;
   int PROCASSIGN = 13;
   int SYSTEMDEF = 14;
   int LOCALDECL = 15;
   int EXTERNALDECL = 16;
   int INVARIANT = 17;
   int EXPRATE = 18;
   int TRANS = 19;
   int SELECT = 20;
   int GUARD = 21;
   int SYNC = 22;
   int ASSIGN = 23;
   int PROBABILITY = 24;
   int GRAPHINFO = 25;
   int LCOLOR = 26;
   int ECOLOR = 27;
   int COMMA = 28;
   int SEMICOLON = 29;
   int DOT = 30;
   int OPPAR = 31;
   int CLPAR = 32;
   int ID = 33;
   int NAT = 34;
   int COLOR = 35;
   int DEFAULT = 0;
   String[] tokenImage = new String[]{"<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"process\"", "\"location\"", "\"locationName\"", "\"branchpoint\"", "\"templateName\"", "\"paramList\"", "\"globalDecl\"", "\"imports\"", "\"procAssign\"", "\"systemDef\"", "\"localDecl\"", "\"externalDecl\"", "\"invariant\"", "\"exponentialrate\"", "\"trans\"", "\"select\"", "\"guard\"", "\"sync\"", "\"assign\"", "\"probability\"", "\"graphinfo\"", "\"lcolor\"", "\"ecolor\"", "\",\"", "\";\"", "\".\"", "\"(\"", "\")\"", "<ID>", "<NAT>", "<COLOR>", "\"{\"", "\"}\"", "\"-\"", "\"+\""};
}
