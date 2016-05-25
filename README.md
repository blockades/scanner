### Intro

openblockchain is a Bitcoin blockchain explorer focusing on 'more-than-economic' uses of the distributed decentralised database. An example of such uses can be found here: [https://twitter.com/dan_mi_sun/status/710278666354368513](https://twitter.com/dan_mi_sun/status/710278666354368513)

The first implementation of this seeks to identify transactions which contain the OP_RETURN flag. More background on the introduction of OP_RETURN can be found [here](http://www.slideshare.net/coinspark/bitcoin-2-and-opreturns-the-blockchain-as-tcpip).

In a nutshell the plan is to run a bitcoin server. Scala will be used to build a scanner which crawls through the entire Bitcoin blockchain to extract all block and transaction information into cassandra to create a more easily queryable format of the blockchain and all Bitcoin transactions.

Once this has been done the plan is to make an interactive web interface (similar to [blockchain.info](https://blockchain.info/charts)) which displays the number of insertions of 'more-than-economic' uses of the blockchain. 

---

### Project Aims

#### Technologies

One of the main purposes of this project is to learn a new stack of technologies. The intention at this stage is to use as many of the following as possible.

- scala
- scalatra
- json4s
- phantom-dsl
- cassandra
- spark
- reactjs or angularjs
- bitcoin server

#### Big Data Analysis

At the time of writing the Bitcoin blockchain is 80GB of transaction data. One of the key areas of focus for this project is to learn some techniques and tools to make some useful learnings from a sizeable data-set. Once the foundations of the blockchain scanner have been built, the focus will turn to data visualisation/sonification and BDA tools and techniques.

---

### Setup

#### Bitcoin Daemon

Follow the official documentation to install [bitcoin daemon](https://bitcoin.org/en/full-node#what-is-a-full-node). Ensure you follow the directions to install the **daemon**. The main bitcoin-qt client does not ship with the developer tools and bitcoin command line interface (bitcoin-cli) for the bitcoin server, which we need. You *also* need to download the bitcoin-qt, which is what will download the bitcoin home directory.

There is a boilerplate bitcoin.conf (bitcoin-example.conf) attached to this project. Rename the file to `bitcoin.conf` and then move the file to the root of the `Bitcoin` folder which was downloaded with the Bitcoin Daemon link. It contains the username and password which will work as the deafault for this project.

For accessing the bitcoin server ensure that bitcoin.conf has the following options enabled.

```sh
# Listen for RPC connections on this TCP port:

rpcport=8332
```

You can use Bitcoin or bitcoind to send commands to Bitcoin/bitcoind running on another host using this option:

```sh
rpcconnect=127.0.0.1
```

```sh
# You must set rpcuser and rpcpassword to secure the JSON-RPC api

rpcuser=test

rpcpassword=test1
```

Bitcoin sever does not automatically create the full transaction index, so you will need to ensure that you have the flags enabled. You do this by issuing the following command on the command line when you are initiating the bitcoin server.

```sh
$ bitcoind -daemon -reindex -txindex
```

By doing so you ensure you have a local index of the transactions which you are querying.

Within this codebase you need to ensure that BitcoinClient.scala 

```scala
   def auth = {
      "Basic " + Base64.encodeString("test:test1")
    }
```
    
Ensure that your username and password matches that which you set up in the bitcoin.conf (which you downloaded as part of the bitcoin daemon).

If everything is setup correctly, then you should be able to issue the commands in the following section: Example Bitcoin Server Commands.

#### Cassandra

Follow the official documentation to download and install [cassandra](http://www.planetcassandra.org/cassandra/?dlink=http://downloads.datastax.com/community/dsc-cassandra-2.1.9-bin.tar.gz)


#### Build & Run Scalatra App (at the moment this is optional, as the Bitcoin and Cassandra sections are testable without needing to run the Webapp)

```sh
$ cd superchain
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

#### Last notes

To have this running you need to ensure there is both a running version of cassandra and also a running version of the bitcoin daemon. It is not required that you run the Scalatra App

### Testing your setup (Bitcoin and Cassandra)

```sh
// This turns on the indexed version of the bitcoin server, making all transaction and block data available
$ bitcoind -daemon -reindex -txindex
$ cd path/to/cassandra
// This turns on your cassandra DB
$ ./bin/cassandra
$ cd superchain
$ sbt
$ run
```

---

### Example Bitcoin Server Commands (i.e. sanity check that the bitcoin client is working):

Providing the bitcoin daemon has been correctly installed you should be able to issue the following commands at the commandline interface

The following are the primary commands of the bitcoin server which we need to issue to be able to build up a 'full' version of the bitcoin blockchain.

The route of commands that we go through is:

> $ bitcoin-cli getblockhash 1 // it is worth noting this starts from 1 _not_ 0. This is by design.

    00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048

> $ bitcoin-cli getblock 00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048


    {
    "hash" : "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
    "confirmations" : 299191,
    "size" : 215,
    "height" : 1,
    "version" : 1,
    "merkleroot" : "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
    "tx" : [
      "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098"
    ],
    "time" : 1231469665,
    "nonce" : 2573394689,
    "bits" : "1d00ffff",
    "difficulty" : 1.00000000,
    "chainwork" : "0000000000000000000000000000000000000000000000000000000200020002",
    "previousblockhash" : "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",
    "nextblockhash" : "000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd"
    }

> $ bitcoin-cli getrawtransaction 0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098 1

    {
    "hex" : "01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0704ffff001d0104ffffffff0
    100f2052a0100000043410496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e
    62294721166bf621e73a82cbf2342c858eeac00000000",
    "txid" : "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
    "version" : 1,
    "locktime" : 0,
    "vin" : [
      {
      "coinbase" : "04ffff001d0104",
      "sequence" : 4294967295
      }
    ],
    "vout" : [
      {
      "value" : 50.00000000,
      "n" : 0,
      "scriptPubKey" : {
        "asm" : "0496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e6229472
            1166bf621e73a82cbf2342c858ee OP_CHECKSIG",
        "hex" : "410496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e62294
        721166bf621e73a82cbf2342c858eeac",
        "reqSigs" : 1,
        "type" : "pubkey",
        "addresses" : [
          "12c6DSiU4Rq3P4ZxziKxzrL5LmMBrzjrJX"
        ]
       }
       }
       ],
    "blockhash" : "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
    "confirmations" : 299485,
    "time" : 1231469665,
    "blocktime" : 1231469665
    }

---

### Swagger API Documentation

Providing you have built and run the Scalatra Application you should be able to run the [Swagger API documentation](http://localhost:8080/api/blocks/swagger-ui/index.html).


---

### TODO

- none of this has been TDD, so a big area I need to work on is how to write tests and move forward that way... will do
this once I get my head around one CORE route to make sure there are no super bad pitfalls.
- ideally it would be nice to set up some automated tests such as with Jenkins - but this is pie in the sky thinking
 - Have also not considered how to use Spark
 - Have also not considered how to use Kafka (if at all)
 - Have also not considered how to use ReactJS
 - Have also not considered how to use Scalatra
 - Have also not considered how to use https://gephi.org/ (if at all)

 - Must set up server and run blockchain so there is a full transaction history available on the server
 
 - MUST go through code and find out where these recommendations have been implemented: https://groups.google.com/forum/#!topic/scalatra-user/rr1ciqI8BqE
 
 ---
 
 Cassandra Modelling
 
 - Query1 should find a block by it's hash
 - Query2 should find a transaction by it's hash
 - Query3 should find all transactions with OP_RETURN
    - need to figure out the specifics of this query