package com.uppaal.language.viewmodels;

import com.google.gson.annotations.SerializedName;

public class FindDeclarationRequest {
   @SerializedName("xpath")
   public String xpath;
   @SerializedName("identifier")
   public String identifier;
   @SerializedName("offset")
   public int offset;

   public FindDeclarationRequest(String xpath, String identifier, int offset) {
      this.xpath = xpath;
      this.identifier = identifier;
      this.offset = offset;
   }
}
