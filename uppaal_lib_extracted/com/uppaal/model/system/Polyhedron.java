package com.uppaal.model.system;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Polyhedron {
   private final UppaalSystem system;
   private final ArrayList<DBMConstraint> constraintList = new ArrayList();
   private static final String PROTOCOL_ERROR = "While reading polyhedron";
   private int[][] dbm = null;

   static boolean isStrict(int bound) {
      return (bound & 1) == 0;
   }

   static int getValue(int bound) {
      return bound >> 1;
   }

   static int makeBound(int val, boolean strict) {
      return strict ? val << 1 : (val << 1) + 1;
   }

   static int setStrict(int val) {
      return val & -2;
   }

   private String getClockName(int i) {
      return this.system.getClockName(i);
   }

   public Polyhedron(UppaalSystem system) {
      this.system = system;
   }

   private void computeCanonicalDBM(int[][] dbm) {
      int size = this.system.getNoOfClocks();

      int j;
      for(j = 0; j < size; ++j) {
         dbm[0][j] = 1;
      }

      int i;
      for(i = 1; i < size; ++i) {
         for(j = 0; j < size; ++j) {
            dbm[i][j] = DBMConstraint.MAXBOUND;
         }
      }

      DBMConstraint constr;
      for(Iterator var6 = this.constraintList.iterator(); var6.hasNext(); dbm[constr.i][constr.j] = constr.bound) {
         constr = (DBMConstraint)var6.next();

         assert constr.isValid() : "Polyhedron::computeCanonicalDBM(): illegal value for bound";
      }

      for(int k = 0; k < size; ++k) {
         for(j = 0; j < size; ++j) {
            if (dbm[k][j] < DBMConstraint.MAXBOUND) {
               for(i = 0; i < size; ++i) {
                  if (dbm[i][k] < DBMConstraint.MAXBOUND) {
                     int sum = (dbm[i][k] & -2) + (dbm[k][j] & -2) | dbm[i][k] & dbm[k][j] & 1;
                     if (sum < dbm[i][j]) {
                        dbm[i][j] = sum;
                     }
                  }
               }
            }
         }
      }

   }

   public void writeTextualFormat(Writer writer) throws IOException {
      Iterator var2 = this.constraintList.iterator();

      while(var2.hasNext()) {
         DBMConstraint c = (DBMConstraint)var2.next();
         writer.write(String.valueOf(c.i));
         writer.write(10);
         writer.write(String.valueOf(c.j));
         writer.write(10);
         writer.write(String.valueOf(c.bound ^ 1));
         writer.write(10);
         writer.write(".\n");
      }

      writer.write(".\n");
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      Iterator var2 = this.constraintList.iterator();

      while(var2.hasNext()) {
         DBMConstraint c = (DBMConstraint)var2.next();
         s.append(c.i).append(' ').append(c.j).append(' ').append(c.bound ^ 1).append(' ');
      }

      return s.toString().trim();
   }

   private String lt(int bound) {
      return isStrict(bound) ? " < " : " ≤ ";
   }

   private String gt(int bound) {
      return isStrict(bound) ? " > " : " ≥ ";
   }

   private void appendInterval(int lower, int upper, StringBuilder s) {
      if (getValue(lower) == -getValue(upper)) {
         s.append(" = ");
         s.append(getValue(upper));
      } else if (lower == DBMConstraint.MAXBOUND) {
         s.append(this.lt(upper)).append(getValue(upper));
      } else if (upper == DBMConstraint.MAXBOUND) {
         s.append(this.gt(lower)).append(-getValue(lower));
      } else {
         s.append(" ∈ ");
         if (isStrict(lower)) {
            s.append("(");
         } else {
            s.append("[");
         }

         s.append(-getValue(lower));
         s.append(",");
         s.append(getValue(upper));
         if (isStrict(upper)) {
            s.append(")");
         } else {
            s.append("]");
         }
      }

   }

   private void addConstraintToList(int i, int j, List<String> res) {
      assert this.dbm != null;

      assert i != j;

      StringBuilder s = new StringBuilder();
      int lower = this.dbm[j][i];
      int upper = this.dbm[i][j];
      if (lower != DBMConstraint.MAXBOUND || upper != DBMConstraint.MAXBOUND) {
         String iname = this.getClockName(i);
         String jname = this.getClockName(j);
         if (i == 0) {
            s.append(jname);
            this.appendInterval(upper, lower, s);
         } else if (j == 0) {
            s.append(iname);
            this.appendInterval(lower, upper, s);
         } else if (getValue(lower) == 0 && getValue(upper) == 0) {
            s.append(iname).append(" = ").append(jname);
         } else if (getValue(upper) == 0 && lower == DBMConstraint.MAXBOUND) {
            s.append(iname).append(this.lt(upper)).append(jname);
         } else if (getValue(lower) == 0 && upper == DBMConstraint.MAXBOUND) {
            s.append(iname).append(this.gt(lower)).append(jname);
         } else if (lower == DBMConstraint.MAXBOUND) {
            s.append(iname).append(" - ").append(jname).append(this.lt(upper)).append(getValue(upper));
         } else if (upper == DBMConstraint.MAXBOUND) {
            s.append(jname).append(" - ").append(iname).append(this.lt(lower)).append(getValue(lower));
         } else if (getValue(upper) < 0) {
            s.append(jname).append(" - ").append(iname);
            this.appendInterval(upper, lower, s);
         } else {
            s.append(iname).append(" - ").append(jname);
            this.appendInterval(lower, upper, s);
         }

         res.add(s.toString());
      }
   }

   public void getAllConstraints(List<String> result) {
      int size = this.system.getNoOfClocks();
      if (this.dbm == null) {
         this.dbm = new int[size][size];
         this.computeCanonicalDBM(this.dbm);
      }

      for(int i = 0; i < size; ++i) {
         for(int j = i + 1; j < size; ++j) {
            this.addConstraintToList(i, j, result);
         }
      }

   }

   public void getSufficientConstraints(List<String> result) {
      int size = this.system.getNoOfClocks();
      if (this.dbm == null) {
         this.dbm = new int[size][size];
         this.computeCanonicalDBM(this.dbm);
      }

      for(int j = 1; j < size; ++j) {
         this.addConstraintToList(0, j, result);
      }

      Iterator var5 = this.constraintList.iterator();

      while(var5.hasNext()) {
         DBMConstraint c = (DBMConstraint)var5.next();
         if (this.dbm[c.i][c.j] < DBMConstraint.MAXBOUND && c.i > 0 && c.j > 0) {
            this.addConstraintToList(c.i, c.j, result);
            this.dbm[c.i][c.j] = this.dbm[c.j][c.i] = DBMConstraint.MAXBOUND;
         }
      }

      this.dbm = null;
   }

   public List<DBMConstraint> getRawConstraintList() {
      return this.constraintList;
   }

   public int[][] getDBM() {
      if (this.dbm == null) {
         int size = this.system.getNoOfClocks();
         this.dbm = new int[size][size];
         this.computeCanonicalDBM(this.dbm);
      }

      return this.dbm;
   }

   public void addStrictConstraint(int i, int j, int bound) {
      this.constraintList.add(new DBMConstraint(i, j, 2 * bound));
   }

   public void addNonStrictConstraint(int i, int j, int bound) {
      this.constraintList.add(new DBMConstraint(i, j, 2 * bound + 1));
   }

   public void add(int i, int j, int bound) {
      this.constraintList.add(new DBMConstraint(i, j, bound));
   }

   public void trim() {
      this.constraintList.trimToSize();
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         Polyhedron p = (Polyhedron)obj;
         return this.system == p.system && this.constraintList.equals(p.constraintList);
      }
   }
}
