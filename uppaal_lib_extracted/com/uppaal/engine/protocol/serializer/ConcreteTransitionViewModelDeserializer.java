package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.uppaal.engine.Range;
import com.uppaal.engine.RangeSet;
import com.uppaal.engine.protocol.viewmodel.ConcreteTransitionViewModel;
import com.uppaal.engine.protocol.viewmodel.EdgeFieldViewModel;
import java.lang.reflect.Type;
import java.util.List;

public class ConcreteTransitionViewModelDeserializer implements JsonDeserializer<ConcreteTransitionViewModel> {
   public ConcreteTransitionViewModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      JsonObject object = json.getAsJsonObject();
      List<EdgeFieldViewModel> edges = (List)context.deserialize(object.get("edges"), (new TypeToken<List<EdgeFieldViewModel>>() {
      }).getType());
      List<Range> rangeList = (List)context.deserialize(object.get("ranges"), (new TypeToken<List<Range>>() {
      }).getType());
      RangeSet ranges = new RangeSet(rangeList);
      ConcreteTransitionViewModel res = new ConcreteTransitionViewModel();
      res.edges = edges;
      res.ranges = ranges;
      return res;
   }
}
