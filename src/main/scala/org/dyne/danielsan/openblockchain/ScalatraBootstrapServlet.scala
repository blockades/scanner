package org.dyne.danielsan.openblockchain

class ScalatraBootstrapServlet extends SuperchainStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say
        <a href="hello-scalate">hello to Scalate</a>
        .
      </body>
    </html>
  }

}

