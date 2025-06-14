package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceViewModel;
import com.uppaal.engine.protocol.viewmodel.ErrorMessage;
import com.uppaal.engine.protocol.viewmodel.ModelCheckStatus;
import com.uppaal.engine.protocol.viewmodel.PlotViewModel;
import com.uppaal.engine.protocol.viewmodel.QueryResultViewModel;
import com.uppaal.engine.protocol.viewmodel.SymbolicTraceNode;
import com.uppaal.engine.protocol.viewmodel.SymbolicTraceViewModel;
import com.uppaal.engine.protocol.viewmodel.TraceViewModel;
import java.lang.reflect.Type;
import java.util.List;

public class QueryResultViewModelDeserializer extends AbstractTypeAdapter implements JsonDeserializer<QueryResultViewModel> {
   public QueryResultViewModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      QueryResultViewModel res = new QueryResultViewModel();
      JsonObject object = json.getAsJsonObject();
      res.setStatus((String)context.deserialize(object.get("status"), String.class));
      if (res.getStatus() == ModelCheckStatus.ERROR) {
         return this.deserializeError(context, res, object);
      } else {
         res.setMessage((String)context.deserialize(object.get("message"), String.class));
         res.setResult((String)context.deserialize(object.get("result"), String.class));
         res.setStrategy((String)context.deserialize(object.get("strategy_decl"), String.class));
         boolean stat = (Boolean)context.deserialize(object.get("stat"), Boolean.class);
         if (stat) {
            res.setPlots((List)context.deserialize(object.get("plots"), (new TypeToken<List<PlotViewModel>>() {
            }).getType()));
            res.setTrace((TraceViewModel)context.deserialize(object.get("trace"), ConcreteTraceViewModel.class));
         } else {
            res.setCycleLength((Integer)context.deserialize(object.get("cyclelen"), Integer.class));
            this.deserializeSymbolicTrace(context, res, object);
         }

         return res;
      }
   }

   private void deserializeSymbolicTrace(JsonDeserializationContext context, QueryResultViewModel res, JsonObject object) {
      List<SymbolicTraceNode> traceList = (List)context.deserialize(object.get("trace"), (new TypeToken<List<SymbolicTraceNode>>() {
      }).getType());
      res.setTrace(new SymbolicTraceViewModel(traceList));
   }

   private QueryResultViewModel deserializeError(JsonDeserializationContext context, QueryResultViewModel res, JsonObject object) {
      res.setError((ErrorMessage)context.deserialize(object.get("error"), ErrorMessage.class));
      boolean stat = (Boolean)context.deserialize(object.get("stat"), Boolean.class);
      if (stat) {
         res.setTrace((TraceViewModel)context.deserialize(object.get("trace"), ConcreteTraceViewModel.class));
      } else {
         this.deserializeSymbolicTrace(context, res, object);
      }

      return res;
   }
}
