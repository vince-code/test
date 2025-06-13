package com.uppaal.model.lscsystem;

import com.uppaal.model.AbstractProcess;
import com.uppaal.model.Translator;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.util.ArrayList;

public class LscProcess extends AbstractProcess {
   private SystemPrechart prechart;
   private ArrayList<SystemInstanceLine> instanceLines = new ArrayList();
   private ArrayList<SystemMessage> messages = new ArrayList();
   private ArrayList<SystemCondition> conditions = new ArrayList();
   private ArrayList<SystemUpdate> updates = new ArrayList();

   public LscProcess(String name, int index, LscTemplate template, Translator translator) {
      super(name, index, template, translator);
   }

   public void addMessage(Message message) {
      String name = (String)message.getPropertyValue("name");
      this.messages.add(new SystemMessage(this, this.messages.size(), name, message));
   }

   public void addInstanceLine(InstanceLine instanceLine) {
      this.instanceLines.add(new SystemInstanceLine(this, this.instanceLines.size(), instanceLine));
   }

   public void addCondition(Condition condition) {
      String name = (String)condition.getPropertyValue("name");
      this.conditions.add(new SystemCondition(this, this.conditions.size(), name, condition));
   }

   public void addUpdate(Update update) {
      String name = (String)update.getPropertyValue("name");
      this.updates.add(new SystemUpdate(this, this.updates.size(), name, update));
   }

   public void setPrechart(Prechart prechart) {
      this.prechart = new SystemPrechart(this, prechart);
   }

   public SystemMessage getMessage(int message) {
      return (SystemMessage)this.messages.get(message);
   }

   public final ArrayList<SystemMessage> getMessages() {
      return this.messages;
   }

   public SystemInstanceLine getInstanceLine(int i) {
      return (SystemInstanceLine)this.instanceLines.get(i);
   }

   public ArrayList<SystemInstanceLine> getInstanceLines() {
      return this.instanceLines;
   }

   public SystemCondition getCondition(int i) {
      return (SystemCondition)this.conditions.get(i);
   }

   public ArrayList<SystemCondition> getConditions() {
      return this.conditions;
   }

   public SystemUpdate getUpdate(int i) {
      return (SystemUpdate)this.updates.get(i);
   }

   public ArrayList<SystemUpdate> getUpdates() {
      return this.updates;
   }

   public SystemPrechart getPrechart() {
      return this.prechart;
   }
}
