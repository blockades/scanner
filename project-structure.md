.
├── LICENSE.txt
├── README.md
├── doc
│   └── architecture.zargo
├── project
│   ├── build.properties
│   ├── build.scala
│   ├── plugins.sbt
│
├── project-structure.md
├── sbt
├── src
    ├── main
    │   ├── resources
    │   │   ├── META-INF
    │   │   │   └── resources
    │   │   │       └── webjars
    │   │   │           └── swagger-ui
    │   │   │               └── 2.0.21
    │   │   │                   └── index.html
    │   │   ├── logback.xml
    │   │   └── mocks
    │   │       ├── bitcoin-example.conf
    │   │       ├── block_transaction_counts.csv
    │   │       ├── blocks.csv
    │   │       └── transactions.csv
    │   ├── scala
    │   │   ├── ScalatraBootstrap.scala
    │   │   └── org
    │   │       └── dyne
    │   │           └── danielsan
    │   │               └── openblockchain
    │   │                   ├── Driver.scala
    │   │                   ├── OpenBlockchainStack.scala
    │   │                   ├── ScalatraBootstrapServlet.scala
    │   │                   ├── client
    │   │                   │   └── BitcoinClient.scala
    │   │                   ├── data
    │   │                   │   ├── connector
    │   │                   │   │   └── config.scala
    │   │                   │   ├── database
    │   │                   │   │   └── Database.scala
    │   │                   │   ├── entity
    │   │                   │   │   ├── Block.scala
    │   │                   │   │   ├── ScriptPubKey.scala
    │   │                   │   │   ├── Transaction.scala
    │   │                   │   │   ├── Vin.scala
    │   │                   │   │   └── Vout.scala
    │   │                   │   ├── model
    │   │                   │   │   ├── GenericBlockModel.scala
    │   │                   │   │   ├── GenericScriptPubKeyModel.scala
    │   │                   │   │   ├── GenericTransactionModel.scala
    │   │                   │   │   ├── GenericVinModel.scala
    │   │                   │   │   ├── GenericVoutModel.scala
    │   │                   │   │   └── target
    │   │                   │   └── service
    │   │                   │       ├── BlocksService.scala
    │   │                   │       └── TransactionsService.scala
    │   │                   └── http
    │   │                       └── controllers
    │   │                           ├── ApiDocsController.scala
    │   │                           └── api
    │   │                               ├── BlocksController.scala
    │   │                               ├── ChartsController.scala
    │   │                               ├── TransactionsController.scala
    │   │                               └── swaggerdocs
    │   │                                   ├── BlocksControllerDocs.scala
    │   │                                   └── TransactionsControllerDocs.scala
    │   └── webapp
    │       ├── WEB-INF
    │       │   ├── templates
    │       │   │   ├── layouts
    │       │   │   │   └── default.jade
    │       │   │   └── views
    │       │   │       └── hello-scalate.jade
    │       │   └── web.xml
    │       ├── admin
    │       │   └── charts
    │       │       └── highchart.html
    │       └── assets
    │           └── javascripts
    │               └── d3
    │                   ├── LICENSE
    │                   ├── d3.js
    │                   └── d3.min.js
    └── test
        └── scala
            ├── AppSpec.scala
            └── org
                └── dyne
                    └── danielsan
                        └── openblockchain
                            ├── ScalatraBootstrapServletSpec.scala
                            ├── client
                            │   └── BitcoinClientTest.scala
                            └── testsupport
                                └── TestStack.scala
