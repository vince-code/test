package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;

public class Simregion extends LscElement {
   protected Condition condition = null;
   protected Update update = null;
   protected Message message = null;

   public Simregion(Element prototype) {
      super(prototype);
   }

   public boolean hasCondition() {
      return this.condition != null;
   }

   public boolean hasUpdate() {
      return this.update != null;
   }

   public boolean hasMessage() {
      return this.message != null;
   }

   public Condition getCondition() {
      return this.condition;
   }

   public void setCondition(Condition condition) {
      this.condition = condition;
   }

   public Update getUpdate() {
      return this.update;
   }

   public void setUpdate(Update update) {
      this.update = update;
   }

   public Message getMessage() {
      return this.message;
   }

   public void setMessage(Message message) {
      this.message = message;
   }

   public int getY() {
      if (this.update != null) {
         return this.update.anchoredToCondition == null ? this.update.getY() : this.update.getY() + 20 - 2;
      } else {
         return this.condition != null ? this.condition.getY() : this.message.getY();
      }
   }

   public String getFriendlyName() {
      return "simregion";
   }
}
