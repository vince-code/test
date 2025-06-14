package com.uppaal.model.core2;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangedDebounced implements DocumentListener {
   private final Timer debouncingTimer;

   public DocumentChangedDebounced(int milliseconds) {
      this.debouncingTimer = new Timer(milliseconds, (e) -> {
         this.documentChanged();
      });
      this.debouncingTimer.setRepeats(false);
   }

   public abstract void documentChanged();

   public void insertUpdate(DocumentEvent e) {
      this.debouncingTimer.restart();
   }

   public void removeUpdate(DocumentEvent e) {
      this.debouncingTimer.restart();
   }

   public void changedUpdate(DocumentEvent e) {
   }
}
