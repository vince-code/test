package com.uppaal.model.core2;

import java.util.Optional;

public class Node extends Element {
   public Node previous;
   public Node next;
   public Node first;

   public Node(Element prototype) {
      super(prototype);
   }

   public Node getPrevious() {
      return this.previous;
   }

   public Node getNext() {
      return this.next;
   }

   public Node getFirst() {
      return this.first;
   }

   public Node getLast() {
      Node node = this.first;
      if (node != null) {
         while(node.next != null) {
            node = node.next;
         }
      }

      return node;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitNode(this);
   }

   public <T extends Node> Optional<T> getFirstInstance(Class<T> type) {
      Node child;
      for(child = this.getFirst(); child != null && !type.isInstance(child); child = child.getNext()) {
      }

      return child != null ? Optional.of((Node)type.cast(child)) : Optional.empty();
   }

   public Node insert(Node node, Node position) {
      node.parent = this;
      node.previous = position;
      if (position == null) {
         node.next = this.first;
         this.first = node;
      } else {
         node.next = position.next;
         position.next = node;
      }

      if (node.next != null) {
         node.next.previous = node;
      }

      node.fireInsertionEvent(this);
      return node;
   }

   public Node move(Node child, Node position) {
      assert child.parent == this;

      assert position == null || position.parent == this;

      if (this.first == child) {
         this.first = child.next;
      } else {
         child.previous.next = child.next;
      }

      if (child.next != null) {
         child.next.previous = child.previous;
      }

      child.previous = position;
      if (position == null) {
         child.next = this.first;
         this.first = child;
      } else {
         child.next = position.next;
         position.next = child;
      }

      if (child.next != null) {
         child.next.previous = child;
      }

      this.fireAfterMoveEvent(child);
      return this;
   }

   public void remove() {
      assert this.parent != null;

      Node thisParent = (Node)this.parent;
      this.fireBeforeRemoveEvent(thisParent);
      if (thisParent.first == this) {
         thisParent.first = this.next;
      } else {
         this.previous.next = this.next;
      }

      if (this.next != null) {
         this.next.previous = this.previous;
      }

      this.parent = null;
      this.fireAfterRemoveEvent(thisParent);
   }

   void fireBeforeRemoveEvent(Node parent) {
      if (parent.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.beforeRemoval(parent, this);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   void fireAfterRemoveEvent(Node parent) {
      if (parent.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.afterRemoval(parent, this);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   void fireInsertionEvent(Node parent) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.afterInsertion(parent, this);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   void fireAfterMoveEvent(Node child) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.afterMove(this, child);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public Object clone() throws CloneNotSupportedException {
      Node node = (Node)super.clone();
      node.previous = null;
      node.next = null;
      node.first = null;
      Node child = this.first;

      for(Node pos = null; child != null; child = child.getNext()) {
         pos = node.insert((Node)child.clone(), pos);
      }

      return node;
   }

   public void setPrototype(Element prototype) {
      super.setPrototype(prototype);

      for(Node node = this.first; node != null; node = node.getNext()) {
         node.importInto(this);
      }

   }

   public String getXPathTag() {
      String tag = (String)this.getPropertyValue("#xml.tag");
      if (tag == null) {
         return null;
      } else if (tag.contains("[")) {
         return tag;
      } else if (tag.endsWith("!")) {
         return tag.substring(0, tag.length() - 1);
      } else {
         int index = 1;

         for(Node older = this.previous; older != null; older = older.previous) {
            if (older.getClass().equals(this.getClass())) {
               ++index;
            }
         }

         return tag + "[" + index + "]";
      }
   }
}
