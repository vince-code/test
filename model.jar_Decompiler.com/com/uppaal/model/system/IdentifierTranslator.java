package com.uppaal.model.system;

import com.uppaal.engine.protocol.viewmodel.ParameterReferenceViewModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class IdentifierTranslator implements com.uppaal.model.Translator {
   private Map<String, String> map;
   private final String delim = "\t\n\r\f,.<>&|!+-*/%?:;=()/{}[]^ ";

   public IdentifierTranslator(Map<String, String> map) {
      this.map = map;
   }

   public IdentifierTranslator(List<ParameterReferenceViewModel> bindings) {
      this.map = new HashMap();
      Iterator var2 = bindings.iterator();

      while(var2.hasNext()) {
         ParameterReferenceViewModel binding = (ParameterReferenceViewModel)var2.next();
         this.map.put(binding.getFormalParameter(), binding.getArgument());
      }

   }

   public String translate(String content) {
      StringBuffer result = new StringBuffer();
      StringTokenizer tokenizer = new StringTokenizer(content, "\t\n\r\f,.<>&|!+-*/%?:;=()/{}[]^ ", true);
      boolean lastWasDotOperator = false;

      while(true) {
         while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String value = (String)this.map.get(token);
            if (value != null && !lastWasDotOperator) {
               result.append(value);
            } else {
               result.append(token);
               lastWasDotOperator = ".".equals(token);
            }
         }

         return result.toString();
      }
   }
}
