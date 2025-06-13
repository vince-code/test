package com.uppaal.model.core2;

/** @deprecated */
@Deprecated
public class SetQueryCommand extends AbstractCommand {
   private final Query query;
   private final String oldFormula;
   private String newFormula;
   private final String oldComment;
   private String newComment;
   private boolean isRedo;

   public SetQueryCommand(Query query, String oldFormula, String oldComment, String newFormula, String newComment) {
      this.query = query;
      if (oldFormula.equals(newFormula)) {
         this.oldFormula = null;
         this.newFormula = null;
      } else {
         this.oldFormula = oldFormula;
         this.newFormula = newFormula;
      }

      this.oldComment = oldComment;
      this.newComment = newComment;
   }

   public void execute() {
      if (this.isRedo) {
         this.query.setFormulaAndComment(this.newFormula, this.newComment);
      }

      this.isRedo = true;
   }

   public void undo() {
      this.query.setFormulaAndComment(this.oldFormula, this.oldComment);
   }

   public Element getModifiedElement() {
      return null;
   }

   public Query getQueryProperty() {
      return this.query;
   }

   public void setText(String newFormula, String newComment) {
      this.newFormula = newFormula;
      this.newComment = newComment;
   }
}
