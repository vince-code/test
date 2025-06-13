package com.uppaal.model.core2;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangedListener implements DocumentListener {
   public abstract void documentChanged(DocumentEvent var1);

   public void insertUpdate(DocumentEvent e) {
      this.documentChanged(e);
   }

   public void removeUpdate(DocumentEvent e) {
      this.documentChanged(e);
   }

   public void changedUpdate(DocumentEvent e) {
      this.documentChanged(e);
   }
}
