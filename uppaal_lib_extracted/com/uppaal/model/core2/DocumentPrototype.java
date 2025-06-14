package com.uppaal.model.core2;

import com.uppaal.model.LayoutVisitor;
import com.uppaal.model.core2.lsc.LscConstants;
import com.uppaal.model.core2.lsc.LscElement;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.io2.UXMLResolver;
import com.uppaal.model.io2.XMLReader;
import com.uppaal.model.io2.UGIReaderParsing.UGIReader;
import com.uppaal.model.io2.XTAReaderParsing.ParseException;
import com.uppaal.model.io2.XTAReaderParsing.XTAReader;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

public class DocumentPrototype extends Element {
   public static Font FONT = new Font("Monospaced", 0, 14);

   public DocumentPrototype() {
      super((Element)null);
      ModelColors colors = ModelColors.getCurrentColors();
      Object[] properties = new Object[]{"declaration", "", "declaration:#xml.tag", "declaration!", "declaration:#name.tag", "declaration", "system", "", "system:#xml.tag", "system!", "system:#name.tag", "system", "#template", new Element((Element)null), "#template/#xml.tag", "template", "#template/name", "", "#template/name:#xml.tag", "name!", "#template/name:#name.tag", "name", "#template/parameter", "", "#template/parameter:#xml.tag", "parameter!", "#template/parameter:#name.tag", "parameter", "#template/declaration", "", "#template/declaration:#xml.tag", "declaration!", "#template/declaration:#name.tag", "declaration", "#template/#location", new Element((Element)null), "#template/#location/#xml.tag", "location", "#template/#location/name", "", "#template/#location/name:#xml.tag", "name!", "#template/#location/name:#name.tag", "name", "#template/#location/name:x", 0, "#template/#location/name:y", 0, "#template/#location/invariant", "", "#template/#location/invariant:#xml.tag", "label[@kind=\"invariant\"]", "#template/#location/invariant:#name.tag", "invariant", "#template/#location/invariant:x", 0, "#template/#location/invariant:y", 0, "#template/#location/exponentialrate", "", "#template/#location/exponentialrate:#xml.tag", "label[@kind=\"exponentialrate\"]", "#template/#location/exponentialrate:x", 0, "#template/#location/exponentialrate:y", 0, "#template/#location/init", false, "#template/#location/committed", false, "#template/#location/urgent", false, "#template/#location/x", 0, "#template/#location/y", 0, "#template/#location/color", colors.locationFillColor, "#template/#location/comments", "", "#template/#location/comments:#xml.tag", "label[@kind=\"comments\"]", "#template/#location/comments:#name.tag", "comments", "#template/#location/comments:x", 0, "#template/#location/comments:y", 0, "#template/#location/comments:color", colors.commentsColor, "#template/#location/comments:font", FONT, "#template/#location/testcodeEnter", "", "#template/#location/testcodeEnter:#xml.tag", "label[@kind=\"testcodeEnter\"]", "#template/#location/testcodeEnter:#name.tag", "testcodeEnter", "#template/#location/testcodeLeave", "", "#template/#location/testcodeLeave:#xml.tag", "label[@kind=\"testcodeLeave\"]", "#template/#location/testcodeLeave:#name.tag", "testcodeLeave", "#template/#branchpoint", new Element((Element)null), "#template/#branchpoint/#xml.tag", "branchpoint", "#template/#branchpoint/x", 0, "#template/#branchpoint/y", 0, "#template/#branchpoint/color", colors.locationFillColor, "#template/#edge", new Element((Element)null), "#template/#edge/#xml.tag", "transition", "#template/#edge/select", "", "#template/#edge/select:#xml.tag", "label[@kind=\"select\"]", "#template/#edge/select:#name.tag", "select", "#template/#edge/select:x", 0, "#template/#edge/select:y", 0, "#template/#edge/guard", "", "#template/#edge/guard:#xml.tag", "label[@kind=\"guard\"]", "#template/#edge/guard:#name.tag", "guard", "#template/#edge/guard:x", 0, "#template/#edge/guard:y", 0, "#template/#edge/synchronisation", "", "#template/#edge/synchronisation:#xml.tag", "label[@kind=\"synchronisation\"]", "#template/#edge/synchronisation:#name.tag", "synchronisation", "#template/#edge/synchronisation:x", 0, "#template/#edge/synchronisation:y", 0, "#template/#edge/assignment", "", "#template/#edge/assignment:#xml.tag", "label[@kind=\"assignment\"]", "#template/#edge/assignment:#name.tag", "assignment", "#template/#edge/assignment:x", 0, "#template/#edge/assignment:y", 0, "#template/#edge/probability", "", "#template/#edge/probability:#xml.tag", "label[@kind=\"probability\"]", "#template/#edge/probability:#name.tag", "weight", "#template/#edge/probability:x", 0, "#template/#edge/probability:y", 0, "#template/#edge/comments", "", "#template/#edge/comments:#xml.tag", "label[@kind=\"comments\"]", "#template/#edge/controllable", true, "#template/#edge/color", colors.edgeColor, "#template/#edge/comments:x", 0, "#template/#edge/comments:y", 0, "#template/#edge/comments:color", colors.commentsColor, "#template/#edge/comments:font", FONT, "#template/#edge/#nail", new Element((Element)null), "#template/#edge/#nail/x", 0, "#template/#edge/#nail/y", 0, "#template/#edge/testcode", "", "#template/#edge/testcode:#xml.tag", "label[@kind=\"testcode\"]", "#template/#edge/testcode:#name.tag", "testcode", "#template/#location/name:color", colors.locationLabelColor, "#template/#location/name:font", FONT, "#template/#location/invariant:color", colors.invariantColor, "#template/#location/invariant:font", FONT, "#template/#location/exponentialrate:color", colors.exponentialRateColor, "#template/#location/exponentialrate:font", FONT, "#template/#edge/select:color", colors.selectColor, "#template/#edge/select:font", FONT, "#template/#edge/guard:color", colors.guardColor, "#template/#edge/guard:font", FONT, "#template/#edge/synchronisation:color", colors.syncColor, "#template/#edge/synchronisation:font", FONT, "#template/#edge/assignment:color", colors.updateColor, "#template/#edge/assignment:font", FONT, "#template/#edge/probability:color", colors.probabilityWeightColor, "#template/#edge/probability:font", FONT, "#lscTemplate", new Element((Element)null), "#lscTemplate/name", "", "#lscTemplate/name:#xml.tag", "name!", "#lscTemplate/name:#name.tag", "name", "#lscTemplate/parameter", "", "#lscTemplate/parameter:#xml.tag", "parameter!", "#lscTemplate/parameter:#name.tag", "parameter", "#lscTemplate/declaration", "", "#lscTemplate/declaration:#xml.tag", "declaration!", "#lscTemplate/declaration:#name.tag", "declaration", "#lscTemplate/type", "Universal", "#lscTemplate/type:#xml.tag", "type!", "#lscTemplate/type:#name.tag", "type", "#lscTemplate/mode", "Invariant", "#lscTemplate/mode:#xml.tag", "mode!", "#lscTemplate/mode:#name.tag", "mode", "#lscTemplate/#instance", new LscElement((Element)null), "#lscTemplate/#instance:#xml.tag", "instance", "#lscTemplate/#instance/name", "", "#lscTemplate/#instance/name:#xml.tag", "name!", "#lscTemplate/#instance/name:#nane.tag", "name", "#lscTemplate/#instance/name:x", 0, "#lscTemplate/#instance/name:y", 0, "#lscTemplate/#instance/name:font", FONT, "#lscTemplate/#instance/x", 0, "#lscTemplate/#instance/y", 0, "#lscTemplate/#instance/color", LscConstants.INSTANCE_FILL_COL, "#lscTemplate/#message", new LscElement((Element)null), "#lscTemplate/#message:#xml.tag", "message", "#lscTemplate/#message/message", "", "#lscTemplate/#message/message:#xml.tag", "label[@kind=\"message\"]", "#lscTemplate/#message/message:#name.tag", "message", "#lscTemplate/#message/message:x", 0, "#lscTemplate/#message/message:f", 10.0F, "#lscTemplate/#message/message:y", 0, "#lscTemplate/#message/message:font", FONT, "#lscTemplate/#message/message:color", LscConstants.MESSAGE_LABEL_COL, "#lscTemplate/#message/x", 0, "#lscTemplate/#message/y", 0, "#lscTemplate/#message/color", LscConstants.MESSAGE_COL, "#lscTemplate/#prechart", new LscElement((Element)null), "#lscTemplate/#prechart:#xml.tag", "prechart", "#lscTemplate/#prechart/x", 0, "#lscTemplate/#prechart/y", 0, "#lscTemplate/#prechart/color", LscConstants.PRECHART_COL, "#lscTemplate/#condition", new LscElement((Element)null), "#lscTemplate/#condition:#xml.tag", "condition", "#lscTemplate/#condition/condition", "", "#lscTemplate/#condition/condition:font", FONT, "#lscTemplate/#condition/x", 0, "#lscTemplate/#condition/y", 0, "#lscTemplate/#condition/hot", false, "#lscTemplate/#condition/color", null, "#lscTemplate/#update", new LscElement((Element)null), "#lscTemplate/#update:#xml.tag", "update", "#lscTemplate/#update/update", "", "#lscTemplate/#update/update:font", FONT, "#lscTemplate/#update/x", 0, "#lscTemplate/#update/y", 0, "#lscTemplate/#update/color", LscConstants.UPDATE_COL};
      this.setProperties(properties);
   }

   public Document load(URI uri) throws IOException {
      return this.load(uri, new ArrayList());
   }

   public static Document load(String location) throws IOException {
      URI uri;
      try {
         uri = (new URL(location)).toURI();
      } catch (MalformedURLException | URISyntaxException var3) {
         uri = (new File(location)).toURI();
      }

      return (new DocumentPrototype()).load(uri);
   }

   public Document load(URI uri, List<Problem> problems) throws IOException {
      InputStream is = uri.getScheme().equals("file") ? new FileInputStream(uri.getPath()) : uri.toURL().openStream();
      Object ugi = null;

      Document var18;
      try {
         String path = uri.toURL().getPath().toLowerCase();
         Document document;
         if (!path.endsWith(".xta") && !path.endsWith(".ta")) {
            document = this.loadXML((InputStream)is, problems);
         } else {
            try {
               String gfxPath = uri.getPath().replaceFirst("\\....?$", ".ugi");
               if (uri.getQuery() != null) {
                  gfxPath = gfxPath + "?" + uri.getQuery();
               }

               URL ugiUrl = new URL(uri.getScheme(), uri.getHost(), gfxPath);
               if ("file".equals(ugiUrl.getProtocol())) {
                  ugi = new FileInputStream(ugiUrl.getPath());
               } else {
                  ugi = ugiUrl.openStream();
               }
            } catch (FileNotFoundException var14) {
            }

            document = this.loadXTA((InputStream)is, (InputStream)ugi);
         }

         document.acceptSafe(new LayoutVisitor());
         var18 = document;
      } catch (IOException var15) {
         var15.printStackTrace(System.err);
         throw var15;
      } catch (Exception var16) {
         var16.printStackTrace(System.err);
         throw new IOException(var16.getMessage(), var16);
      } finally {
         ((InputStream)is).close();
         if (ugi != null) {
            ((InputStream)ugi).close();
         }

      }

      return var18;
   }

   public Document loadXML(InputStream stream, List<Problem> problems) throws IOException, XMLStreamException {
      XMLReader reader = new XMLReader(new UXMLResolver(problems), stream);
      Document document = reader.parse(this, problems);
      return document;
   }

   public Document loadXTA(InputStream xta, InputStream ugi) throws IOException, ParseException {
      XTAReader xtaReader = new XTAReader(xta, StandardCharsets.UTF_8.displayName());
      Document document = xtaReader.parse(this);
      if (ugi != null) {
         try {
            UGIReader ugiReader = new UGIReader(ugi);
            ugiReader.parse(document);
         } catch (IOException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new IOException(var7.getMessage(), var7);
         }
      }

      return document;
   }

   public String getFriendlyName() {
      return "prototype";
   }
}
