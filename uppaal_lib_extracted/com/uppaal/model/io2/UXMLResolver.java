package com.uppaal.model.io2;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.stream.XMLResolver;

public class UXMLResolver implements XMLResolver {
   private static final String UNKNOWN_XML;
   private static final int MAJOR_VERSION = 1;
   private static final int MINOR_VERSION = 6;
   private static final String PUBLIC_PREFIX = "-//Uppaal Team//DTD Flat System ";
   private static final String PUBLIC_ID = "-//Uppaal Team//DTD Flat System 1.6//EN";
   private static final String SYSTEM_PREFIX = "http://www.it.uu.se/research/group/darts/uppaal/flat-";
   private static final String SYSTEM_ID = "http://www.it.uu.se/research/group/darts/uppaal/flat-1_6.dtd";
   private static final String DTD_PREFIX = "com/uppaal/resource/flat-";
   private static final String DTD = "com/uppaal/resource/flat-1_6.dtd";
   private static final URL DTD_RES;
   private static final String PUBLIC_ID34 = "-//Uppaal Team//DTD Flat System 1.0//EN";
   private static final String SYSTEM_ID34 = "http://www.docs.uu.se/docs/rtmv/uppaal/xml/flat-1_0.dtd";
   private static final URL DTD34;
   private final List<Problem> problems;

   public UXMLResolver(List<Problem> problems) {
      this.problems = problems;
   }

   public static String getPublicID() {
      return "-//Uppaal Team//DTD Flat System 1.6//EN".replace('"', '\'');
   }

   public static String getSystemID() {
      return "http://www.it.uu.se/research/group/darts/uppaal/flat-1_6.dtd".replace('"', '\'');
   }

   public static boolean publicIDMatch(String publicid) {
      if (!publicid.startsWith("-//Uppaal Team//DTD Flat System ")) {
         return false;
      } else {
         int dot = publicid.indexOf(46, "-//Uppaal Team//DTD Flat System ".length());
         if (dot < 0) {
            return false;
         } else {
            int major_version = Integer.valueOf(publicid.substring("-//Uppaal Team//DTD Flat System ".length(), dot));
            int slash = publicid.indexOf(47, dot + 1);
            if (slash < 0) {
               return false;
            } else {
               int minor_version = Integer.valueOf(publicid.substring(dot + 1, slash));
               if (major_version > 1) {
                  return false;
               } else {
                  return minor_version <= 6;
               }
            }
         }
      }
   }

   public static boolean systemIDMatch(String systemid) {
      if (!systemid.startsWith("http://www.it.uu.se/research/group/darts/uppaal/flat-")) {
         return false;
      } else {
         int dot = systemid.indexOf(95, "http://www.it.uu.se/research/group/darts/uppaal/flat-".length());
         if (dot < 0) {
            return false;
         } else {
            int major_version = Integer.valueOf(systemid.substring("http://www.it.uu.se/research/group/darts/uppaal/flat-".length(), dot));
            int slash = systemid.indexOf(46, dot + 1);
            if (slash < 0) {
               return false;
            } else {
               int minor_version = Integer.valueOf(systemid.substring(dot + 1, slash));
               if (major_version > 1) {
                  return false;
               } else {
                  return minor_version <= 6;
               }
            }
         }
      }
   }

   public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) {
      if ("-//Uppaal Team//DTD Flat System 1.0//EN".equals(publicID) && "http://www.docs.uu.se/docs/rtmv/uppaal/xml/flat-1_0.dtd".equals(systemID)) {
         try {
            return DTD34.openStream();
         } catch (IOException var7) {
            System.err.println("Warning: Local DTD not found");
         }
      }

      if (!publicIDMatch(publicID)) {
         this.problems.add(new Problem("WARNING", "/!DOCTYPE/PUBLIC", UNKNOWN_XML + " " + publicID));
      }

      if (!systemIDMatch(systemID)) {
         this.problems.add(new Problem("WARNING", "/!DOCTYPE/SYSTEM", UNKNOWN_XML + " " + systemID));
      }

      try {
         return DTD_RES.openStream();
      } catch (IOException var6) {
         System.err.println("Warning: Local DTD not found");
         return null;
      }
   }

   static {
      UNKNOWN_XML = XMLReader.LOCALE.getString("unknown_xml");
      DTD_RES = ClassLoader.getSystemResource("com/uppaal/resource/flat-1_6.dtd");
      DTD34 = ClassLoader.getSystemResource("com/uppaal/resource/uppaal-3.4.dtd");
   }
}
