package com.uppaal.model.core2;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.swing.event.EventListenerList;

public class Element implements Cloneable, Serializable {
   public static final String ERRORS = "#errors";
   public EventListenerList listeners;
   public Element prototype;
   Element parent;
   protected Map<String, Property> properties;
   private CommandManager commandManager;

   public Element(Element prototype) {
      this.prototype = prototype;
   }

   public boolean isPropertyLocal(String name) {
      return this.properties != null && this.properties.containsKey(name);
   }

   public Property getProperty(String name) {
      Property property = null;
      if (this.properties != null) {
         property = (Property)this.properties.get(name);
      }

      if (property == null && this.prototype != null) {
         property = this.prototype.getProperty(name);
      }

      return property;
   }

   public Property getLocalProperty(String name) {
      return this.properties == null ? null : (Property)this.properties.get(name);
   }

   public <T> T getPropertyValue(String name) {
      Property property = this.getProperty(name);
      return property != null ? property.getValue() : null;
   }

   public boolean hasFlag(String property) {
      Object value = this.getPropertyValue(property);
      return value != null && (Boolean)value;
   }

   public int getX() {
      return (Integer)this.getPropertyValue("x");
   }

   public int getY() {
      return (Integer)this.getPropertyValue("y");
   }

   public Color getColor() {
      return (Color)this.getPropertyValue("color");
   }

   public Color getLocalColor() {
      Property colorProperty = this.getLocalProperty("color");
      return colorProperty != null ? (Color)colorProperty.getValue() : null;
   }

   public Property setProperty(String name, Object value) {
      Property property = null;
      if (this.properties == null) {
         if (value == null) {
            return null;
         }

         this.properties = new HashMap();
      } else {
         property = (Property)this.properties.get(name);
      }

      if (property != null && value == null) {
         this.properties.remove(name);
         this.firePropertyChanged(property, name, property.getValue(), (Object)null);
         property = null;
      } else if (property == null && value != null) {
         property = new Property(this, this.getProperty(name), value);
         this.properties.put(name, property);
         this.firePropertyChanged(property, name, (Object)null, value);
      } else if (property != null && value != null) {
         property.setValue(value, name);
      }

      return property;
   }

   public void setPropertyFromPath(String path, Object value) {
      Element element = this;
      StringTokenizer tokenizer = new StringTokenizer(path, "/:", true);

      String name;
      for(name = tokenizer.nextToken(); tokenizer.hasMoreTokens(); name = tokenizer.nextToken()) {
         if (tokenizer.nextToken().equals(":")) {
            element = ((Element)element).getProperty(name);
         } else {
            element = (Element)((Element)element).getPropertyValue(name);
         }
      }

      ((Element)element).setProperty(name.intern(), value);
   }

   public Element getPropertyFromPath(String path) {
      Element element = this;
      StringTokenizer tokenizer = new StringTokenizer(path, "/:", true);

      String name;
      for(name = tokenizer.nextToken(); tokenizer.hasMoreTokens(); name = tokenizer.nextToken()) {
         if (tokenizer.nextToken().equals(":")) {
            element = ((Element)element).getProperty(name);
         } else {
            element = (Element)((Element)element).getPropertyValue(name);
         }
      }

      return ((Element)element).getProperty(name);
   }

   public void setProperties(Object[] properties) {
      int i = 0;

      while(i < properties.length) {
         String property = (String)properties[i++];
         Object value = properties[i++];
         this.setPropertyFromPath(property, value);
      }

   }

   void restoreProperty(String name, Property property) {
      assert property != null;

      assert property.parent == this;

      if (this.properties == null) {
         this.properties = new HashMap();
      }

      assert !this.properties.containsKey(name);

      this.properties.put(name, property);
      this.firePropertyChanged(property, name, (Object)null, property.getValue());
   }

   public Map<String, Property> getProperties() {
      return this.properties == null ? Collections.EMPTY_MAP : this.properties;
   }

   public Element getParent() {
      return this.parent;
   }

   public void addListener(EventListener l) {
      if (this.listeners == null) {
         this.listeners = new EventListenerList();
      }

      this.listeners.add(EventListener.class, l);
   }

   public void removeListener(EventListener l) {
      if (this.listeners != null) {
         this.listeners.remove(EventListener.class, l);
      }

   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitElement(this);
   }

   public void acceptSafe(Visitor visitor) {
      try {
         this.accept(visitor);
      } catch (Exception var3) {
         throw new AssertionError(var3);
      }
   }

   void firePropertyChanged(Property property, String name, Object old, Object value) {
      assert this == property.getParent();

      if (this.getDocument() != null) {
         Object element = property;

         do {
            ((Element)element).fireEvent((l) -> {
               l.propertyChanged(property, name, old, value);
            });
            element = ((Element)element).prototype;
         } while(element != null);
      }

      this.fireEvent((l) -> {
         l.propertyChanged(property, name, old, value);
      });
   }

   private void fireEvent(Consumer<EventListener> event) {
      if (this.listeners != null) {
         Object[] listenersArray = this.listeners.getListenerList();

         for(int i = listenersArray.length - 2; i >= 0; i -= 2) {
            if (listenersArray[i] == EventListener.class) {
               event.accept((EventListener)listenersArray[i + 1]);
            }
         }

      }
   }

   public Element getPrototype() {
      return this.prototype;
   }

   public Document getDocument() {
      return this.parent != null ? this.parent.getDocument() : null;
   }

   public AbstractTemplate getTemplate() {
      return this.parent != null ? this.parent.getTemplate() : null;
   }

   public Object clone() throws CloneNotSupportedException {
      Element element = (Element)super.clone();
      this.copyInto(element);
      return element;
   }

   public void copyInto(Element element) throws CloneNotSupportedException {
      element.parent = null;
      element.prototype = null;
      if (this.properties != null) {
         element.properties = new HashMap();
         Iterator var2 = this.getProperties().entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, Property> entry = (Entry)var2.next();
            String name = (String)entry.getKey();
            Property value = (Property)entry.getValue();
            Property property = (Property)value.clone();
            property.parent = element;
            element.properties.put(name, property);
         }
      }

      if (this.getCommandManager() != null) {
         element.setCommandManager(new CommandManager(this.getCommandManager()));
      }

   }

   public Element getPrototypeFromParent(Element parent) {
      return null;
   }

   public void importInto(Element parent) {
      this.setPrototype(this.getPrototypeFromParent(parent));
   }

   public void setPrototype(Element prototype) {
      this.prototype = prototype;
      if (this.properties != null) {
         this.getProperties().forEach((key, value) -> {
            value.importInto(this);
         });
      }

   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public void setCommandManager(CommandManager commandManager) {
      this.commandManager = commandManager;
   }

   public String getXPathTag() {
      String tag = (String)this.getPropertyValue("#xml.tag");
      if (tag == null) {
         System.out.println(this.getClass());
      }

      return tag;
   }

   public String getXPath() {
      if (this.parent == null) {
         return this.getXPathTag();
      } else {
         String var10000 = this.parent.getXPath();
         return var10000 + "/" + this.getXPathTag();
      }
   }

   public String getFriendlyName() {
      assert false;

      return null;
   }

   public String getFriendlyPath() {
      if (this.parent == null) {
         return this.getFriendlyName();
      } else {
         String var10000 = this.parent.getFriendlyPath();
         return var10000 + "/" + this.getFriendlyName();
      }
   }
}
