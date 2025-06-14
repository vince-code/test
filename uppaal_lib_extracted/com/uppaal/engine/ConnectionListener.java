package com.uppaal.engine;

import java.util.EventListener;

public interface ConnectionListener extends EventListener {
   void disconnected();

   void licenseConfirmed();

   void beforeConnected();

   void afterConnected();

   void noLicenseException();
}
