package com.uppaal.model.io2.XTAReaderParsing;

import java.io.IOException;
import java.io.PrintStream;

public class XTAReaderTokenManager implements XTAReaderConstants {
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{0L, 0L, -1L, -1L};
   static final int[] jjnextStates = new int[]{11, 12, 15, 6, 7, 9, 5, 10};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, "body", "process", "local", "external", "int", "chan", "clock", "double", "const", "urgent", "init", "state", "branchpoint", "trans", "commit", "select", "guard", "sync", "assign", "probability", "system", "import", "from", ":=", "<", "<=", "(", ")", "?", "!", ",", ";", "{", "}", "/", ".", "=", "[", "]", "+", "*", "-", "->", "-u->", ":", "!=", "==", ">=", ">", null, null, null};
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{72057594037927921L};
   static final long[] jjtoSkip = new long[]{14L};
   static final long[] jjtoSpecial = new long[]{14L};
   protected SimpleCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
      switch(pos) {
      case 0:
         if ((active0 & 134217712L) != 0L) {
            this.jjmatchedKind = 53;
            return 2;
         } else {
            if ((active0 & 274877906944L) != 0L) {
               return 5;
            }

            return -1;
         }
      case 1:
         if ((active0 & 134217712L) != 0L) {
            this.jjmatchedKind = 53;
            this.jjmatchedPos = 1;
            return 2;
         }

         return -1;
      case 2:
         if ((active0 & 256L) != 0L) {
            return 2;
         } else {
            if ((active0 & 134217456L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 2;
               return 2;
            }

            return -1;
         }
      case 3:
         if ((active0 & 69222928L) != 0L) {
            return 2;
         } else {
            if ((active0 & 64994528L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 3;
               return 2;
            }

            return -1;
         }
      case 4:
         if ((active0 & 1217600L) != 0L) {
            return 2;
         } else {
            if ((active0 & 63776928L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 4;
               return 2;
            }

            return -1;
         }
      case 5:
         if ((active0 & 55322624L) != 0L) {
            return 2;
         } else {
            if ((active0 & 8454304L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 5;
               return 2;
            }

            return -1;
         }
      case 6:
         if ((active0 & 32L) != 0L) {
            return 2;
         } else {
            if ((active0 & 8454272L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 6;
               return 2;
            }

            return -1;
         }
      case 7:
         if ((active0 & 128L) != 0L) {
            return 2;
         } else {
            if ((active0 & 8454144L) != 0L) {
               this.jjmatchedKind = 53;
               this.jjmatchedPos = 7;
               return 2;
            }

            return -1;
         }
      case 8:
         if ((active0 & 8454144L) != 0L) {
            this.jjmatchedKind = 53;
            this.jjmatchedPos = 8;
            return 2;
         }

         return -1;
      case 9:
         if ((active0 & 8454144L) != 0L) {
            this.jjmatchedKind = 53;
            this.jjmatchedPos = 9;
            return 2;
         }

         return -1;
      default:
         return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch(this.curChar) {
      case '!':
         this.jjmatchedKind = 33;
         return this.jjMoveStringLiteralDfa1_0(562949953421312L);
      case '"':
      case '#':
      case '$':
      case '%':
      case '&':
      case '\'':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '@':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '\\':
      case '^':
      case '_':
      case '`':
      case 'h':
      case 'j':
      case 'k':
      case 'm':
      case 'n':
      case 'o':
      case 'q':
      case 'r':
      case 'v':
      case 'w':
      case 'x':
      case 'y':
      case 'z':
      case '|':
      default:
         return this.jjMoveNfa_0(1, 0);
      case '(':
         return this.jjStopAtPos(0, 30);
      case ')':
         return this.jjStopAtPos(0, 31);
      case '*':
         return this.jjStopAtPos(0, 44);
      case '+':
         return this.jjStopAtPos(0, 43);
      case ',':
         return this.jjStopAtPos(0, 34);
      case '-':
         this.jjmatchedKind = 45;
         return this.jjMoveStringLiteralDfa1_0(211106232532992L);
      case '.':
         return this.jjStopAtPos(0, 39);
      case '/':
         return this.jjStartNfaWithStates_0(0, 38, 5);
      case ':':
         this.jjmatchedKind = 48;
         return this.jjMoveStringLiteralDfa1_0(134217728L);
      case ';':
         return this.jjStopAtPos(0, 35);
      case '<':
         this.jjmatchedKind = 28;
         return this.jjMoveStringLiteralDfa1_0(536870912L);
      case '=':
         this.jjmatchedKind = 40;
         return this.jjMoveStringLiteralDfa1_0(1125899906842624L);
      case '>':
         this.jjmatchedKind = 52;
         return this.jjMoveStringLiteralDfa1_0(2251799813685248L);
      case '?':
         return this.jjStopAtPos(0, 32);
      case '[':
         return this.jjStopAtPos(0, 41);
      case ']':
         return this.jjStopAtPos(0, 42);
      case 'a':
         return this.jjMoveStringLiteralDfa1_0(4194304L);
      case 'b':
         return this.jjMoveStringLiteralDfa1_0(65552L);
      case 'c':
         return this.jjMoveStringLiteralDfa1_0(267776L);
      case 'd':
         return this.jjMoveStringLiteralDfa1_0(2048L);
      case 'e':
         return this.jjMoveStringLiteralDfa1_0(128L);
      case 'f':
         return this.jjMoveStringLiteralDfa1_0(67108864L);
      case 'g':
         return this.jjMoveStringLiteralDfa1_0(1048576L);
      case 'i':
         return this.jjMoveStringLiteralDfa1_0(33571072L);
      case 'l':
         return this.jjMoveStringLiteralDfa1_0(64L);
      case 'p':
         return this.jjMoveStringLiteralDfa1_0(8388640L);
      case 's':
         return this.jjMoveStringLiteralDfa1_0(19431424L);
      case 't':
         return this.jjMoveStringLiteralDfa1_0(131072L);
      case 'u':
         return this.jjMoveStringLiteralDfa1_0(8192L);
      case '{':
         return this.jjStopAtPos(0, 36);
      case '}':
         return this.jjStopAtPos(0, 37);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active0) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, active0);
         return 1;
      }

      switch(this.curChar) {
      case '=':
         if ((active0 & 134217728L) != 0L) {
            return this.jjStopAtPos(1, 27);
         }

         if ((active0 & 536870912L) != 0L) {
            return this.jjStopAtPos(1, 29);
         }

         if ((active0 & 562949953421312L) != 0L) {
            return this.jjStopAtPos(1, 49);
         }

         if ((active0 & 1125899906842624L) != 0L) {
            return this.jjStopAtPos(1, 50);
         }

         if ((active0 & 2251799813685248L) != 0L) {
            return this.jjStopAtPos(1, 51);
         }
         break;
      case '>':
         if ((active0 & 70368744177664L) != 0L) {
            return this.jjStopAtPos(1, 46);
         }
         break;
      case 'e':
         return this.jjMoveStringLiteralDfa2_0(active0, 524288L);
      case 'h':
         return this.jjMoveStringLiteralDfa2_0(active0, 512L);
      case 'l':
         return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
      case 'm':
         return this.jjMoveStringLiteralDfa2_0(active0, 33554432L);
      case 'n':
         return this.jjMoveStringLiteralDfa2_0(active0, 16640L);
      case 'o':
         return this.jjMoveStringLiteralDfa2_0(active0, 268368L);
      case 'r':
         return this.jjMoveStringLiteralDfa2_0(active0, 75702304L);
      case 's':
         return this.jjMoveStringLiteralDfa2_0(active0, 4194304L);
      case 't':
         return this.jjMoveStringLiteralDfa2_0(active0, 32768L);
      case 'u':
         return this.jjMoveStringLiteralDfa2_0(active0, 140737489403904L);
      case 'x':
         return this.jjMoveStringLiteralDfa2_0(active0, 128L);
      case 'y':
         return this.jjMoveStringLiteralDfa2_0(active0, 18874368L);
      }

      return this.jjStartNfa_0(0, active0);
   }

   private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(0, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
         }

         switch(this.curChar) {
         case '-':
            return this.jjMoveStringLiteralDfa3_0(active0, 140737488355328L);
         case 'a':
            return this.jjMoveStringLiteralDfa3_0(active0, 1278464L);
         case 'c':
            return this.jjMoveStringLiteralDfa3_0(active0, 64L);
         case 'd':
            return this.jjMoveStringLiteralDfa3_0(active0, 16L);
         case 'g':
            return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
         case 'i':
            return this.jjMoveStringLiteralDfa3_0(active0, 16384L);
         case 'l':
            return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
         case 'm':
            return this.jjMoveStringLiteralDfa3_0(active0, 262144L);
         case 'n':
            return this.jjMoveStringLiteralDfa3_0(active0, 2101248L);
         case 'o':
            return this.jjMoveStringLiteralDfa3_0(active0, 75498528L);
         case 'p':
            return this.jjMoveStringLiteralDfa3_0(active0, 33554432L);
         case 's':
            return this.jjMoveStringLiteralDfa3_0(active0, 20971520L);
         case 't':
            if ((active0 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 8, 2);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 128L);
         case 'u':
            return this.jjMoveStringLiteralDfa3_0(active0, 2048L);
         default:
            return this.jjStartNfa_0(1, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(1, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
         }

         switch(this.curChar) {
         case '>':
            if ((active0 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(3, 47);
            }
            break;
         case 'a':
            return this.jjMoveStringLiteralDfa4_0(active0, 64L);
         case 'b':
            return this.jjMoveStringLiteralDfa4_0(active0, 8390656L);
         case 'c':
            if ((active0 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 21, 2);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 1056L);
         case 'e':
            return this.jjMoveStringLiteralDfa4_0(active0, 532608L);
         case 'i':
            return this.jjMoveStringLiteralDfa4_0(active0, 4194304L);
         case 'm':
            if ((active0 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 26, 2);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 262144L);
         case 'n':
            if ((active0 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 9, 2);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 196608L);
         case 'o':
            return this.jjMoveStringLiteralDfa4_0(active0, 33554432L);
         case 'r':
            return this.jjMoveStringLiteralDfa4_0(active0, 1048576L);
         case 's':
            return this.jjMoveStringLiteralDfa4_0(active0, 4096L);
         case 't':
            if ((active0 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 14, 2);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 16809984L);
         case 'y':
            if ((active0 & 16L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 4, 2);
            }
         }

         return this.jjStartNfa_0(2, active0);
      }
   }

   private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(2, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
         }

         switch(this.curChar) {
         case 'a':
            return this.jjMoveStringLiteralDfa5_0(active0, 8388608L);
         case 'b':
         case 'f':
         case 'h':
         case 'j':
         case 'm':
         case 'o':
         case 'p':
         case 'q':
         default:
            break;
         case 'c':
            return this.jjMoveStringLiteralDfa5_0(active0, 589824L);
         case 'd':
            if ((active0 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 20, 2);
            }
            break;
         case 'e':
            if ((active0 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 15, 2);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 16777248L);
         case 'g':
            return this.jjMoveStringLiteralDfa5_0(active0, 4194304L);
         case 'i':
            return this.jjMoveStringLiteralDfa5_0(active0, 262144L);
         case 'k':
            if ((active0 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 10, 2);
            }
            break;
         case 'l':
            if ((active0 & 64L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 6, 2);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
         case 'n':
            return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
         case 'r':
            return this.jjMoveStringLiteralDfa5_0(active0, 33554560L);
         case 's':
            if ((active0 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 17, 2);
            }
            break;
         case 't':
            if ((active0 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 12, 2);
            }
         }

         return this.jjStartNfa_0(3, active0);
      }
   }

   private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(3, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
         }

         switch(this.curChar) {
         case 'b':
            return this.jjMoveStringLiteralDfa6_0(active0, 8388608L);
         case 'c':
         case 'd':
         case 'f':
         case 'g':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         default:
            break;
         case 'e':
            if ((active0 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 11, 2);
            }
            break;
         case 'h':
            return this.jjMoveStringLiteralDfa6_0(active0, 65536L);
         case 'm':
            if ((active0 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 24, 2);
            }
            break;
         case 'n':
            if ((active0 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 22, 2);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 128L);
         case 's':
            return this.jjMoveStringLiteralDfa6_0(active0, 32L);
         case 't':
            if ((active0 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 13, 2);
            }

            if ((active0 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 18, 2);
            }

            if ((active0 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 19, 2);
            }

            if ((active0 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 25, 2);
            }
         }

         return this.jjStartNfa_0(4, active0);
      }
   }

   private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(4, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
         }

         switch(this.curChar) {
         case 'a':
            return this.jjMoveStringLiteralDfa7_0(active0, 128L);
         case 'i':
            return this.jjMoveStringLiteralDfa7_0(active0, 8388608L);
         case 'p':
            return this.jjMoveStringLiteralDfa7_0(active0, 65536L);
         case 's':
            if ((active0 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 5, 2);
            }
         default:
            return this.jjStartNfa_0(5, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(5, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
         }

         switch(this.curChar) {
         case 'l':
            if ((active0 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 7, 2);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 8388608L);
         case 'o':
            return this.jjMoveStringLiteralDfa8_0(active0, 65536L);
         default:
            return this.jjStartNfa_0(6, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(6, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
         }

         switch(this.curChar) {
         case 'i':
            return this.jjMoveStringLiteralDfa9_0(active0, 8454144L);
         default:
            return this.jjStartNfa_0(7, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(7, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
         }

         switch(this.curChar) {
         case 'n':
            return this.jjMoveStringLiteralDfa10_0(active0, 65536L);
         case 't':
            return this.jjMoveStringLiteralDfa10_0(active0, 8388608L);
         default:
            return this.jjStartNfa_0(8, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa10_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(8, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(9, active0);
            return 10;
         }

         switch(this.curChar) {
         case 't':
            if ((active0 & 65536L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 16, 2);
            }
            break;
         case 'y':
            if ((active0 & 8388608L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 23, 2);
            }
         }

         return this.jjStartNfa_0(9, active0);
      }
   }

   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_0(state, pos + 1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 16;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if ((4294977024L & l) != 0L) {
                     if (kind > 1) {
                        kind = 1;
                     }

                     this.jjCheckNAdd(0);
                  }
                  break;
               case 1:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 54) {
                        kind = 54;
                     }

                     this.jjCheckNAdd(3);
                  } else if ((4294977024L & l) != 0L) {
                     if (kind > 1) {
                        kind = 1;
                     }

                     this.jjCheckNAdd(0);
                  } else if ((103079215104L & l) != 0L) {
                     if (kind > 53) {
                        kind = 53;
                     }

                     this.jjCheckNAdd(2);
                  } else if (this.curChar == '/') {
                     this.jjAddStates(6, 7);
                  }
                  break;
               case 2:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 53) {
                        kind = 53;
                     }

                     this.jjCheckNAdd(2);
                  }
                  break;
               case 3:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 54) {
                        kind = 54;
                     }

                     this.jjCheckNAdd(3);
                  }
                  break;
               case 4:
                  if (this.curChar == '/') {
                     this.jjAddStates(6, 7);
                  }
                  break;
               case 5:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(0, 2);
                  } else if (this.curChar == '/') {
                     this.jjCheckNAddStates(3, 5);
                  }
                  break;
               case 6:
                  if ((-9217L & l) != 0L) {
                     this.jjCheckNAddStates(3, 5);
                  }
                  break;
               case 7:
                  if ((9216L & l) != 0L && kind > 2) {
                     kind = 2;
                  }
                  break;
               case 8:
                  if (this.curChar == '\n' && kind > 2) {
                     kind = 2;
                  }
                  break;
               case 9:
                  if (this.curChar == '\r') {
                     this.jjstateSet[this.jjnewStateCnt++] = 8;
                  }
                  break;
               case 10:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 11:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 12:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 13;
                  }
                  break;
               case 13:
                  if ((-140737488355329L & l) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 14:
                  if (this.curChar == '/' && kind > 3) {
                     kind = 3;
                  }
                  break;
               case 15:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 14;
                  }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 1:
               case 2:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 53) {
                        kind = 53;
                     }

                     this.jjCheckNAdd(2);
                  }
               case 3:
               case 4:
               case 5:
               case 7:
               case 8:
               case 9:
               case 10:
               case 12:
               default:
                  break;
               case 6:
                  this.jjAddStates(3, 5);
                  break;
               case 11:
               case 13:
                  this.jjCheckNAddStates(0, 2);
               }
            } while(i != startsAt);
         } else {
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 6:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     this.jjAddStates(3, 5);
                  }
                  break;
               case 11:
               case 13:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 16 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var9) {
            return curPos;
         }
      }
   }

   public XTAReaderTokenManager(SimpleCharStream stream) {
      this.debugStream = System.out;
      this.jjrounds = new int[16];
      this.jjstateSet = new int[32];
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = stream;
   }

   public XTAReaderTokenManager(SimpleCharStream stream, int lexState) {
      this(stream);
      this.SwitchTo(lexState);
   }

   public void ReInit(SimpleCharStream stream) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = stream;
      this.ReInitRounds();
   }

   private void ReInitRounds() {
      this.jjround = -2147483647;

      for(int i = 16; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(SimpleCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 1 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }

   protected Token jjFillToken() {
      String im = jjstrLiteralImages[this.jjmatchedKind];
      String curTokenImage = im == null ? this.input_stream.GetImage() : im;
      int beginLine = this.input_stream.getBeginLine();
      int beginColumn = this.input_stream.getBeginColumn();
      int endLine = this.input_stream.getEndLine();
      int endColumn = this.input_stream.getEndColumn();
      Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;
      return t;
   }

   public Token getNextToken() {
      Token specialToken = null;
      boolean var3 = false;

      while(true) {
         Token matchedToken;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var9) {
            this.jjmatchedKind = 0;
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            return matchedToken;
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int curPos = this.jjMoveStringLiteralDfa0_0();
         if (this.jjmatchedPos == 0 && this.jjmatchedKind > 55) {
            this.jjmatchedKind = 55;
         }

         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var10) {
               EOFSeen = true;
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != '\n' && this.curChar != '\r') {
                  ++error_column;
               } else {
                  ++error_line;
                  error_column = 0;
               }
            }

            if (!EOFSeen) {
               this.input_stream.backup(1);
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < curPos) {
            this.input_stream.backup(curPos - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            return matchedToken;
         }

         if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            if (specialToken == null) {
               specialToken = matchedToken;
            } else {
               matchedToken.specialToken = specialToken;
               specialToken = specialToken.next = matchedToken;
            }
         }
      }
   }

   private void jjCheckNAdd(int state) {
      if (this.jjrounds[state] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = state;
         this.jjrounds[state] = this.jjround;
      }

   }

   private void jjAddStates(int start, int end) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
      } while(start++ != end);

   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      this.jjCheckNAdd(state1);
      this.jjCheckNAdd(state2);
   }

   private void jjCheckNAddStates(int start, int end) {
      do {
         this.jjCheckNAdd(jjnextStates[start]);
      } while(start++ != end);

   }
}
