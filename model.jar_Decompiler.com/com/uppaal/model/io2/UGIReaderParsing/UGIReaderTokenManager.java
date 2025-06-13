package com.uppaal.model.io2.UGIReaderParsing;

import java.io.IOException;
import java.io.PrintStream;

public class UGIReaderTokenManager implements UGIReaderConstants {
   public PrintStream debugStream;
   static final int[] jjnextStates = new int[0];
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, "process", "location", "locationName", "branchpoint", "templateName", "paramList", "globalDecl", "imports", "procAssign", "systemDef", "localDecl", "externalDecl", "invariant", "exponentialrate", "trans", "select", "guard", "sync", "assign", "probability", "graphinfo", "lcolor", "ecolor", ",", ";", ".", "(", ")", null, null, null, "{", "}", "-", "+"};
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{1099511627745L};
   static final long[] jjtoSkip = new long[]{30L};
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
         if ((active0 & 268435424L) != 0L) {
            this.jjmatchedKind = 33;
            return 1;
         }

         return -1;
      case 1:
         if ((active0 & 268435424L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 1;
            return 1;
         }

         return -1;
      case 2:
         if ((active0 & 268435424L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 2;
            return 1;
         }

         return -1;
      case 3:
         if ((active0 & 264241120L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 3;
            return 1;
         } else {
            if ((active0 & 4194304L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 4:
         if ((active0 & 261619680L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 4;
            return 1;
         } else {
            if ((active0 & 2621440L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 5:
         if ((active0 & 50855904L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 5;
            return 1;
         } else {
            if ((active0 & 210763776L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 6:
         if ((active0 & 50851776L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 6;
            return 1;
         } else {
            if ((active0 & 4128L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 7:
         if ((active0 & 50851584L) != 0L) {
            if (this.jjmatchedPos != 7) {
               this.jjmatchedKind = 33;
               this.jjmatchedPos = 7;
            }

            return 1;
         } else {
            if ((active0 & 192L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 8:
         if ((active0 & 17116032L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 8;
            return 1;
         } else {
            if ((active0 & 33735680L) != 0L) {
               return 1;
            }

            return -1;
         }
      case 9:
         if ((active0 & 10240L) != 0L) {
            return 1;
         } else {
            if ((active0 & 17105792L) != 0L) {
               this.jjmatchedKind = 33;
               this.jjmatchedPos = 9;
               return 1;
            }

            return -1;
         }
      case 10:
         if ((active0 & 16777472L) != 0L) {
            return 1;
         } else {
            if ((active0 & 328320L) != 0L) {
               this.jjmatchedKind = 33;
               this.jjmatchedPos = 10;
               return 1;
            }

            return -1;
         }
      case 11:
         if ((active0 & 66176L) != 0L) {
            return 1;
         } else {
            if ((active0 & 262144L) != 0L) {
               this.jjmatchedKind = 33;
               this.jjmatchedPos = 11;
               return 1;
            }

            return -1;
         }
      case 12:
         if ((active0 & 262144L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 12;
            return 1;
         }

         return -1;
      case 13:
         if ((active0 & 262144L) != 0L) {
            this.jjmatchedKind = 33;
            this.jjmatchedPos = 13;
            return 1;
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
      case '(':
         return this.jjStopAtPos(0, 31);
      case ')':
         return this.jjStopAtPos(0, 32);
      case '+':
         return this.jjStopAtPos(0, 39);
      case ',':
         return this.jjStopAtPos(0, 28);
      case '-':
         return this.jjStopAtPos(0, 38);
      case '.':
         return this.jjStopAtPos(0, 30);
      case ';':
         return this.jjStopAtPos(0, 29);
      case 'a':
         return this.jjMoveStringLiteralDfa1_0(8388608L);
      case 'b':
         return this.jjMoveStringLiteralDfa1_0(256L);
      case 'e':
         return this.jjMoveStringLiteralDfa1_0(134545408L);
      case 'g':
         return this.jjMoveStringLiteralDfa1_0(35653632L);
      case 'i':
         return this.jjMoveStringLiteralDfa1_0(135168L);
      case 'l':
         return this.jjMoveStringLiteralDfa1_0(67141824L);
      case 'p':
         return this.jjMoveStringLiteralDfa1_0(16786464L);
      case 's':
         return this.jjMoveStringLiteralDfa1_0(5259264L);
      case 't':
         return this.jjMoveStringLiteralDfa1_0(524800L);
      case '{':
         return this.jjStopAtPos(0, 36);
      case '}':
         return this.jjStopAtPos(0, 37);
      default:
         return this.jjMoveNfa_0(0, 0);
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
      case 'a':
         return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
      case 'b':
      case 'd':
      case 'f':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'p':
      case 'q':
      case 't':
      case 'v':
      case 'w':
      default:
         return this.jjStartNfa_0(0, active0);
      case 'c':
         return this.jjMoveStringLiteralDfa2_0(active0, 201326592L);
      case 'e':
         return this.jjMoveStringLiteralDfa2_0(active0, 1049088L);
      case 'l':
         return this.jjMoveStringLiteralDfa2_0(active0, 2048L);
      case 'm':
         return this.jjMoveStringLiteralDfa2_0(active0, 4096L);
      case 'n':
         return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
      case 'o':
         return this.jjMoveStringLiteralDfa2_0(active0, 32960L);
      case 'r':
         return this.jjMoveStringLiteralDfa2_0(active0, 50864416L);
      case 's':
         return this.jjMoveStringLiteralDfa2_0(active0, 8388608L);
      case 'u':
         return this.jjMoveStringLiteralDfa2_0(active0, 2097152L);
      case 'x':
         return this.jjMoveStringLiteralDfa2_0(active0, 327680L);
      case 'y':
         return this.jjMoveStringLiteralDfa2_0(active0, 4210688L);
      }
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
         case 'a':
            return this.jjMoveStringLiteralDfa3_0(active0, 36176128L);
         case 'b':
         case 'd':
         case 'e':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'q':
         case 'u':
         default:
            return this.jjStartNfa_0(1, active0);
         case 'c':
            return this.jjMoveStringLiteralDfa3_0(active0, 32960L);
         case 'l':
            return this.jjMoveStringLiteralDfa3_0(active0, 1048576L);
         case 'm':
            return this.jjMoveStringLiteralDfa3_0(active0, 512L);
         case 'n':
            return this.jjMoveStringLiteralDfa3_0(active0, 4194304L);
         case 'o':
            return this.jjMoveStringLiteralDfa3_0(active0, 218114080L);
         case 'p':
            return this.jjMoveStringLiteralDfa3_0(active0, 266240L);
         case 'r':
            return this.jjMoveStringLiteralDfa3_0(active0, 1024L);
         case 's':
            return this.jjMoveStringLiteralDfa3_0(active0, 8404992L);
         case 't':
            return this.jjMoveStringLiteralDfa3_0(active0, 65536L);
         case 'v':
            return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
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
         case 'a':
            return this.jjMoveStringLiteralDfa4_0(active0, 165056L);
         case 'b':
            return this.jjMoveStringLiteralDfa4_0(active0, 16779264L);
         case 'c':
            if ((active0 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 22, 1);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 8224L);
         case 'd':
         case 'f':
         case 'g':
         case 'h':
         case 'j':
         case 'k':
         case 'm':
         case 'q':
         case 's':
         default:
            return this.jjStartNfa_0(2, active0);
         case 'e':
            return this.jjMoveStringLiteralDfa4_0(active0, 1114112L);
         case 'i':
            return this.jjMoveStringLiteralDfa4_0(active0, 8388608L);
         case 'l':
            return this.jjMoveStringLiteralDfa4_0(active0, 201326592L);
         case 'n':
            return this.jjMoveStringLiteralDfa4_0(active0, 524544L);
         case 'o':
            return this.jjMoveStringLiteralDfa4_0(active0, 266240L);
         case 'p':
            return this.jjMoveStringLiteralDfa4_0(active0, 33554944L);
         case 'r':
            return this.jjMoveStringLiteralDfa4_0(active0, 2097152L);
         case 't':
            return this.jjMoveStringLiteralDfa4_0(active0, 16384L);
         }
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
         case 'A':
            return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
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
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'b':
         case 'f':
         case 'i':
         case 'j':
         case 'k':
         case 'p':
         case 'q':
         default:
            break;
         case 'a':
            return this.jjMoveStringLiteralDfa5_0(active0, 16779264L);
         case 'c':
            return this.jjMoveStringLiteralDfa5_0(active0, 1048832L);
         case 'd':
            if ((active0 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 21, 1);
            }
            break;
         case 'e':
            return this.jjMoveStringLiteralDfa5_0(active0, 16416L);
         case 'g':
            return this.jjMoveStringLiteralDfa5_0(active0, 8388608L);
         case 'h':
            return this.jjMoveStringLiteralDfa5_0(active0, 33554432L);
         case 'l':
            return this.jjMoveStringLiteralDfa5_0(active0, 33280L);
         case 'm':
            return this.jjMoveStringLiteralDfa5_0(active0, 1024L);
         case 'n':
            return this.jjMoveStringLiteralDfa5_0(active0, 262144L);
         case 'o':
            return this.jjMoveStringLiteralDfa5_0(active0, 201326592L);
         case 'r':
            return this.jjMoveStringLiteralDfa5_0(active0, 200704L);
         case 's':
            if ((active0 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 19, 1);
            }
            break;
         case 't':
            return this.jjMoveStringLiteralDfa5_0(active0, 192L);
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
         case 'D':
            return this.jjMoveStringLiteralDfa6_0(active0, 32768L);
         case 'L':
            return this.jjMoveStringLiteralDfa6_0(active0, 1024L);
         case 'a':
            return this.jjMoveStringLiteralDfa6_0(active0, 512L);
         case 'b':
            return this.jjMoveStringLiteralDfa6_0(active0, 16777216L);
         case 'e':
            return this.jjMoveStringLiteralDfa6_0(active0, 262144L);
         case 'h':
            return this.jjMoveStringLiteralDfa6_0(active0, 256L);
         case 'i':
            return this.jjMoveStringLiteralDfa6_0(active0, 33685696L);
         case 'l':
            return this.jjMoveStringLiteralDfa6_0(active0, 2048L);
         case 'm':
            return this.jjMoveStringLiteralDfa6_0(active0, 16384L);
         case 'n':
            if ((active0 & 8388608L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 23, 1);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 65536L);
         case 'r':
            if ((active0 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 26, 1);
            } else if ((active0 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 27, 1);
            }
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
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
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'c':
         case 'd':
         case 'f':
         case 'g':
         case 'j':
         case 'k':
         case 'o':
         case 'p':
         case 'q':
         default:
            return this.jjStartNfa_0(4, active0);
         case 's':
            return this.jjMoveStringLiteralDfa6_0(active0, 8224L);
         case 't':
            return (active0 & 1048576L) != 0L ? this.jjStartNfaWithStates_0(5, 20, 1) : this.jjMoveStringLiteralDfa6_0(active0, 4096L);
         }
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
         case 'D':
            return this.jjMoveStringLiteralDfa7_0(active0, 18432L);
         case 'a':
            return this.jjMoveStringLiteralDfa7_0(active0, 196608L);
         case 'e':
            return this.jjMoveStringLiteralDfa7_0(active0, 32768L);
         case 'i':
            return this.jjMoveStringLiteralDfa7_0(active0, 16778240L);
         case 'n':
            return this.jjMoveStringLiteralDfa7_0(active0, 33816576L);
         case 'o':
            return this.jjMoveStringLiteralDfa7_0(active0, 192L);
         case 'p':
            return this.jjMoveStringLiteralDfa7_0(active0, 256L);
         case 's':
            if ((active0 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 5, 1);
            } else {
               if ((active0 & 4096L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 12, 1);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 8192L);
            }
         case 't':
            return this.jjMoveStringLiteralDfa7_0(active0, 512L);
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
         case 'c':
            return this.jjMoveStringLiteralDfa8_0(active0, 32768L);
         case 'd':
         case 'g':
         case 'h':
         case 'j':
         case 'k':
         case 'm':
         case 'p':
         case 'q':
         case 'r':
         default:
            return this.jjStartNfa_0(6, active0);
         case 'e':
            return this.jjMoveStringLiteralDfa8_0(active0, 18944L);
         case 'f':
            return this.jjMoveStringLiteralDfa8_0(active0, 33554432L);
         case 'i':
            return this.jjMoveStringLiteralDfa8_0(active0, 8192L);
         case 'l':
            return this.jjMoveStringLiteralDfa8_0(active0, 16842752L);
         case 'n':
            if ((active0 & 64L) != 0L) {
               this.jjmatchedKind = 6;
               this.jjmatchedPos = 7;
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 131200L);
         case 'o':
            return this.jjMoveStringLiteralDfa8_0(active0, 256L);
         case 's':
            return this.jjMoveStringLiteralDfa8_0(active0, 1024L);
         case 't':
            return this.jjMoveStringLiteralDfa8_0(active0, 262144L);
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
         case 'D':
            return this.jjMoveStringLiteralDfa9_0(active0, 65536L);
         case 'N':
            return this.jjMoveStringLiteralDfa9_0(active0, 640L);
         case 'c':
            return this.jjMoveStringLiteralDfa9_0(active0, 2048L);
         case 'f':
            if ((active0 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 14, 1);
            }
            break;
         case 'g':
            return this.jjMoveStringLiteralDfa9_0(active0, 8192L);
         case 'i':
            return this.jjMoveStringLiteralDfa9_0(active0, 17039616L);
         case 'l':
            if ((active0 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 15, 1);
            }
            break;
         case 'o':
            if ((active0 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 25, 1);
            }
            break;
         case 't':
            if ((active0 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 10, 1);
            }

            if ((active0 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 17, 1);
            }
         }

         return this.jjStartNfa_0(7, active0);
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
         case 'a':
            return this.jjMoveStringLiteralDfa10_0(active0, 262784L);
         case 'e':
            return this.jjMoveStringLiteralDfa10_0(active0, 65536L);
         case 'l':
            if ((active0 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 11, 1);
            }
         default:
            return this.jjStartNfa_0(8, active0);
         case 'n':
            if ((active0 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 13, 1);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 256L);
         case 't':
            return this.jjMoveStringLiteralDfa10_0(active0, 16777216L);
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
         case 'c':
            return this.jjMoveStringLiteralDfa11_0(active0, 65536L);
         case 'l':
            return this.jjMoveStringLiteralDfa11_0(active0, 262144L);
         case 'm':
            return this.jjMoveStringLiteralDfa11_0(active0, 640L);
         case 't':
            if ((active0 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 8, 1);
            }
            break;
         case 'y':
            if ((active0 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 24, 1);
            }
         }

         return this.jjStartNfa_0(9, active0);
      }
   }

   private int jjMoveStringLiteralDfa11_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(9, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(10, active0);
            return 11;
         }

         switch(this.curChar) {
         case 'e':
            if ((active0 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 7, 1);
            }

            if ((active0 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 9, 1);
            }
            break;
         case 'l':
            if ((active0 & 65536L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 16, 1);
            }
            break;
         case 'r':
            return this.jjMoveStringLiteralDfa12_0(active0, 262144L);
         }

         return this.jjStartNfa_0(10, active0);
      }
   }

   private int jjMoveStringLiteralDfa12_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(10, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(11, active0);
            return 12;
         }

         switch(this.curChar) {
         case 'a':
            return this.jjMoveStringLiteralDfa13_0(active0, 262144L);
         default:
            return this.jjStartNfa_0(11, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa13_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(11, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(12, active0);
            return 13;
         }

         switch(this.curChar) {
         case 't':
            return this.jjMoveStringLiteralDfa14_0(active0, 262144L);
         default:
            return this.jjStartNfa_0(12, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa14_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(12, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(13, active0);
            return 14;
         }

         switch(this.curChar) {
         case 'e':
            if ((active0 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(14, 18, 1);
            }
         default:
            return this.jjStartNfa_0(13, active0);
         }
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
      this.jjnewStateCnt = 5;
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
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 34) {
                        kind = 34;
                     }

                     this.jjCheckNAdd(2);
                  } else if (this.curChar == '#') {
                     this.jjCheckNAdd(4);
                  }
                  break;
               case 1:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 33) {
                        kind = 33;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 1;
                  }
                  break;
               case 2:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 34) {
                        kind = 34;
                     }

                     this.jjCheckNAdd(2);
                  }
                  break;
               case 3:
                  if (this.curChar == '#') {
                     this.jjCheckNAdd(4);
                  }
                  break;
               case 4:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 35) {
                        kind = 35;
                     }

                     this.jjCheckNAdd(4);
                  }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if ((576460743847706622L & l) != 0L) {
                     if (kind > 33) {
                        kind = 33;
                     }

                     this.jjCheckNAdd(1);
                  }
                  break;
               case 1:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 33) {
                        kind = 33;
                     }

                     this.jjCheckNAdd(1);
                  }
               case 2:
               case 3:
               default:
                  break;
               case 4:
                  if ((541165879422L & l) != 0L) {
                     if (kind > 35) {
                        kind = 35;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 4;
                  }
               }
            } while(i != startsAt);
         } else {
            int i2 = (this.curChar & 255) >> 6;
            long var7 = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 5 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var9) {
            return curPos;
         }
      }
   }

   public UGIReaderTokenManager(SimpleCharStream stream) {
      this.debugStream = System.out;
      this.jjrounds = new int[5];
      this.jjstateSet = new int[10];
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = stream;
   }

   public UGIReaderTokenManager(SimpleCharStream stream, int lexState) {
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

      for(int i = 5; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
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
      boolean var2 = false;

      while(true) {
         Token matchedToken;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var8) {
            this.jjmatchedKind = 0;
            matchedToken = this.jjFillToken();
            return matchedToken;
         }

         try {
            this.input_stream.backup(0);

            while(this.curChar <= ' ' && (4294977024L & 1L << this.curChar) != 0L) {
               this.curChar = this.input_stream.BeginToken();
            }
         } catch (IOException var10) {
            continue;
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int curPos = this.jjMoveStringLiteralDfa0_0();
         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var9) {
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
            return matchedToken;
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
}
