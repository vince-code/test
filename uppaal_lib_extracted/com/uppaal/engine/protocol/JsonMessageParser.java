package com.uppaal.engine.protocol;

import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.engine.ModelProblemException;
import com.uppaal.engine.ProtocolException;
import com.uppaal.engine.protocol.viewmodel.ErrorMessage;
import com.uppaal.engine.protocol.viewmodel.ModelProblemsViewModel;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Optional;

public class JsonMessageParser {
   private final JsonParser parser;

   public JsonMessageParser(byte[] arr, Class<? extends RuntimeException> engineSpecificException) {
      this.parser = new JsonParser(arr, engineSpecificException);
   }

   public JsonMessageParser(InputStreamReader in, Class<? extends RuntimeException> engineSpecificException) {
      this.parser = new JsonParser(in, engineSpecificException);
   }

   public <T> T parseGeneric(Type type) throws ProtocolException {
      Response response = this.parseValidatedResponse();
      return response.expectGeneric(type);
   }

   public <T> T parseResponse(Class<T> type) throws ProtocolException {
      Response response = this.parseValidatedResponse();
      return response.expect(type);
   }

   public Response parseRawResponse() {
      return (Response)this.parser.parse(Response.class);
   }

   public Response parseValidatedResponse() throws ProtocolException {
      Response response = this.parseRawResponse();
      this.validateResponse(response);
      return response;
   }

   private void validateResponse(Response response) throws ProtocolException {
      String var2 = response.type;
      byte var3 = -1;
      switch(var2.hashCode()) {
      case 3548:
         if (var2.equals("ok")) {
            var3 = 0;
         }
         break;
      case 100709:
         if (var2.equals("err")) {
            var3 = 2;
         }
         break;
      case 96784904:
         if (var2.equals("error")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
         return;
      case 1:
         throw new CannotEvaluateException((ErrorMessage)response.expect(ErrorMessage.class));
      case 2:
         this.handleException(response);
      default:
         if (!response.type.isEmpty()) {
            throw new ProtocolException("Received unknown response '" + response.type + "'");
         } else {
            throw new RuntimeException("Engine failed to respond to request");
         }
      }
   }

   public <T> T parseWithManyErrors(Class<T> type) throws ProtocolException, ModelProblemException {
      Response response = this.parseRawResponse();
      this.checkForMultipleProblems(response);
      this.validateResponse(response);
      return response.expect(type);
   }

   private void checkForMultipleProblems(Response response) throws ProtocolException, ModelProblemException {
      if ("error".equals(response.type)) {
         Optional<ModelProblemsViewModel> problems = response.as(ModelProblemsViewModel.class);
         if (problems.isPresent()) {
            if (((ModelProblemsViewModel)problems.get()).isEmpty()) {
               throw new ProtocolException("Model failed to compile but no errors were given");
            } else {
               throw new ModelProblemException((ModelProblemsViewModel)problems.get());
            }
         }
      }
   }

   private void handleException(Response response) throws ProtocolException {
      String content = (String)response.expect(String.class);
      if (content.equals("License key is not installed")) {
         throw new LicenseMissingException();
      } else {
         throw new ProtocolException("Error: " + content);
      }
   }
}
