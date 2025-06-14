package com.uppaal.engine;

import com.uppaal.model.core2.EngineSettings;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EngineOptions {
   private static final ResourceBundle LOCALE = ResourceBundle.getBundle("locale.Options");
   private final ArrayList<Option<?>> options = new ArrayList();
   private final Map<String, Option<?>> index = new HashMap();

   private static String localize(String text) {
      return LOCALE.containsKey(text) ? LOCALE.getString(text) : text;
   }

   public void parse(String engineOptionsInfo) throws EngineException {
      assert engineOptionsInfo != null;

      this.index.clear();
      this.options.clear();

      try {
         InputSource source = new InputSource(new StringReader(engineOptionsInfo));
         Document optionsDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
         NodeIterator i = ((DocumentTraversal)optionsDoc).createNodeIterator(optionsDoc.getDocumentElement(), 1, (NodeFilter)null, false);
         Node node = i.nextNode();

         while(true) {
            while(node != null) {
               if (node.getNodeName().equals("option")) {
                  Element element = (Element)node;
                  String name = element.getAttribute("name");

                  assert name != null;

                  String type = element.getAttribute("type");
                  if (type == null) {
                     throw new EngineException("Error: engine option '" + name + "' has no 'type' attribute");
                  }

                  byte var10 = -1;
                  switch(type.hashCode()) {
                  case -1536230375:
                     if (type.equals("parameterset")) {
                        var10 = 2;
                     }
                     break;
                  case -1361224287:
                     if (type.equals("choice")) {
                        var10 = 0;
                     }
                     break;
                  case 64711720:
                     if (type.equals("boolean")) {
                        var10 = 1;
                     }
                  }

                  String display;
                  String defaultValue;
                  switch(var10) {
                  case 0:
                     ArrayList<Choice> choices = new ArrayList();
                     String display = localize(element.getAttribute("display"));
                     display = element.getAttribute("default");
                     defaultValue = element.getAttribute("value");

                     for(node = i.nextNode(); node != null && node.getNodeName().equals("choice"); node = i.nextNode()) {
                        element = (Element)node;
                        choices.add(new Choice(element.getAttribute("name"), localize(element.getAttribute("display"))));
                     }

                     ChoiceOption option = new ChoiceOption(name, display, choices, display, defaultValue);
                     this.options.add(option);
                     this.index.put(option.getName(), option);
                     break;
                  case 1:
                     BooleanOption option = new BooleanOption(name, localize(element.getAttribute("display")), element.getAttribute("default"), element.getAttribute("value"));
                     this.options.add(option);
                     this.index.put(option.getName(), option);
                     node = i.nextNode();
                     break;
                  case 2:
                     OptionSet parameterSet = new OptionSet(name, localize(element.getAttribute("display")));
                     node = i.nextNode();

                     while(node != null && "parameter".equals(node.getNodeName())) {
                        element = (Element)node;
                        name = element.getAttribute("name");

                        assert name != null;

                        type = element.getAttribute("type");
                        if ("integer".equals(type)) {
                           IntegerOption parameter = new IntegerOption(name, localize(element.getAttribute("display")), element.getAttribute("default"), element.getAttribute("value"), element.getAttribute("rangemin"), element.getAttribute("rangemax"));
                           parameterSet.add(parameter);
                           this.index.put(parameter.getName(), parameter);
                           node = i.nextNode();
                        } else if ("decimal".equals(type)) {
                           DecimalOption parameter = new DecimalOption(name, localize(element.getAttribute("display")), element.getAttribute("default"), element.getAttribute("value"), element.getAttribute("rangemin"), element.getAttribute("rangemax"), element.getAttribute("fracmin"), element.getAttribute("fracmax"));
                           parameterSet.add(parameter);
                           this.index.put(parameter.getName(), parameter);
                           node = i.nextNode();
                        } else {
                           if (!"choice".equals(type)) {
                              throw new EngineException("Error: unrecognized parameter type '" + type + "' for parameter '" + name + "'");
                           }

                           ArrayList<Choice> choices = new ArrayList();
                           display = localize(element.getAttribute("display"));
                           defaultValue = element.getAttribute("default");
                           String value = element.getAttribute("value");

                           for(node = i.nextNode(); node != null && node.getNodeName().equals("choice"); node = i.nextNode()) {
                              element = (Element)node;
                              choices.add(new Choice(element.getAttribute("name"), localize(element.getAttribute("display"))));
                           }

                           ChoiceOption parameter = new ChoiceOption(name, display, choices, defaultValue, value);
                           parameterSet.add(parameter);
                           this.index.put(parameter.getName(), parameter);
                        }
                     }

                     this.options.add(parameterSet);
                     break;
                  default:
                     throw new EngineException("Error: unrecognized option type '" + type + "'");
                  }
               } else {
                  if (!node.getNodeName().equals("server")) {
                     throw new EngineException("Error: unrecognized tag '" + node.getNodeName() + "' in engine options");
                  }

                  node = i.nextNode();
               }
            }

            return;
         }
      } catch (SAXException | IOException | ParserConfigurationException var17) {
         throw new EngineException("Engine option parsing error: " + var17.getMessage());
      }
   }

   public void reset() {
      this.index.clear();
      this.options.clear();
   }

   public void validate(EngineSettings settings, List<String> unsupportedOptions) {
      if (settings == null) {
         throw new IllegalArgumentException("Settings cannot be null");
      } else if (unsupportedOptions == null) {
         throw new IllegalArgumentException("Unsupported option list cannot be null");
      } else {
         List<String> bad = new ArrayList();
         com.uppaal.model.core2.Element prototype = settings.getPrototype();
         if (prototype == null) {
            settings.setPrototype(this.getDefaultSettings());
         }

         settings.getProperties().forEach((name, prop) -> {
            Option<?> opt = (Option)this.index.get(name);
            if (opt == null) {
               unsupportedOptions.add(name);
               bad.add(name);
            } else {
               try {
                  opt.fromString((String)prop.getValue());
               } catch (Exception var7) {
                  unsupportedOptions.add(name);
               }
            }

         });
         if (!bad.isEmpty()) {
            settings.resetToDefault((List)bad);
         }

      }
   }

   public String getText(final EngineSettings settings) {
      final StringBuilder sb = new StringBuilder();
      this.accept(new GenericOptionVisitor() {
         public <T> void handleOption(Option<T> opt) {
            T value = settings.getValue(opt);
            if (value != null) {
               sb.append(opt.name).append(' ').append(value).append(' ');
            }

         }
      });
      return sb.toString();
   }

   public EngineSettings getDefaultSettings() {
      final EngineSettings settings = new EngineSettings((com.uppaal.model.core2.Element)null);
      this.accept(new GenericOptionVisitor() {
         public <T> void handleOption(Option<T> option) {
            settings.setValue(option, option.getDefaultValue());
         }
      });
      return settings;
   }

   public void accept(OptionVisitor visitor) {
      Iterator var2 = this.options.iterator();

      while(var2.hasNext()) {
         Option<?> o = (Option)var2.next();
         o.accept(visitor);
      }

   }
}
