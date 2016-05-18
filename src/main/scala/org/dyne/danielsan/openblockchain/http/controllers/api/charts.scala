package org.dyne.danielsan.openblockchain.http.controllers.api

import org.scalatra.ScalatraServlet


/**
  * Created by dan_mi_sun on 13/04/2016.
  */
class ChartsController extends ScalatraServlet {

  get("/charts/:id") {
    <html>
      <body>
        <h1>This is
          <a href="http://scalatra.org/2.2/guides/http/http-client.html">http/http-client</a>!
        </h1>
      </body>
    </html>
  }

}


