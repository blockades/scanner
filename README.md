### Intro

openblockchain is a Bitcoin blockchain explorer focusing on 'more-than-economic' uses of the distributed decentralised database. An example of such uses can be found here: [https://twitter.com/dan_mi_sun/status/710278666354368513](https://twitter.com/dan_mi_sun/status/710278666354368513)

The first implementation of this seeks to identify transactions which contain the OP_RETURN flag. More background on the introduction of OP_RETURN can be found [here](http://www.slideshare.net/coinspark/bitcoin-2-and-opreturns-the-blockchain-as-tcpip).

In a nutshell the plan is to run a bitcoin server. Scala will be used to build a scanner which crawls through the entire Bitcoin blockchain to extract all block and transaction information. It's planned to then extract the entire bitcoin blockchain into cassandra to create a more easily queryable format of the blockchain and all Bitcoin transactions.

Once this has been done the plan is to make an interactive web interface (similar to [blockchain.info](https://blockchain.info/charts)) which displays the number of insertions of 'more-than-economic' uses of the blockchain. 

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

### Setup

Need to install [bitcoin daemon](https://bitcoin.org/en/full-node#what-is-a-full-node) & also [cassandra](http://cassandra.apache.org/download/)


For accessing the bitcoin server ensure that bitcoin.conf has the following options enabled.

> Listen for RPC connections on this TCP port:
> rpcport=8332

You can use Bitcoin or bitcoind to send commands to Bitcoin/bitcoind running on another host using this option:

> rpcconnect=127.0.0.1

To have this running you need to ensure there is both a running version of cassandra and also a running version of the
bitcoin daemon. Bitcoin sever does not automatically create the full transaction index, so you will need to ensure that
you have the flags enabled.

> bitcoind -daemon -reindex -txindex

By doing so you ensure you have a local index of the transactions which you are querying.

Within this code base you need to ensure that BitcoinClient.scala 

>   def auth = {
      "Basic " + Base64.encodeString("username:password")
    }
    
Ensure that your username and password matches that which you set up in the bitcoin.conf (which you downloaded as part of the bitcoin daemon).

If everything is setup correctly, then you should be able to issue the commands in the High Level Steps.

---

### High Level Steps:

> $ bitcoin-cli getblockhash <INTEGER>

- get blockhash (how to figure out which is the latest?)
- insert the blockhash into the DB
- Loop through all INTEGERS till all blockhashes have been inserted into the DB
(another way to do this is from the JSON from the block itself as it has 'next' and 'previous')

> $ bitcoin-cli getblock <BLOCKHASH>

- Loop through all blockhashes in the DB and get the block JSON and insert into the DB

    NEED TO WRITE LOOP for each block JSON

- Loop through each block and extract into the DB a list of all tx from within the block (these are the rawTransactions)
- This means looping through each tx array to extract each tx code within the array for each block

> $ bitcoin-cli getrawtransaction <RAW-TX-ID>

- Loop through all RawTransactions
- Insert JSON response into Transaction within DB

---

### Example Bitcoin Server Commands

The following are the primary commands of the bitcoin server which we need to issue to be able to build up a 'full'
version of the bitcoin blockchain.

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

### All of the below are working notes and TODO items

### NEXT STEPS:

    - [DONE] Write model for blockhash 
    - [DONE] Insert the blockhash into the DB (using fixed value)
    - [DONE] Write a loop to go through all integers to get all blockhashes and insert each into DB

    - [DONE] Write model for block
    - [DONE] Insert a block JSON into the DB using a hardcoded value
    - [DONE] Write loop to go through all blockhashes to extract all block JSON and insert into DB

    - [DONE] Write model for RAW-TX-ID
    - [DONE] Write a function which can extract the RAW-TX-ID from the block JSON (using fixed block JSON to begin with)
    - [DONE] Write a function which can insert RAW-TX-IDs into DB
    - [DONE] Write a loop which can go through all blocks, extract RAW-TX-ID and insert into DB

    - [DONE] Write a function which can insert Transaction into DB (using fixed value)
    - [DONE] Write a function which loops through all the RAW-TX-ID and then inserts the Transaction JSON into DB

    - Write a function which can go through all the Transactions and note if they have OP_RETURN or not
    - **Question: Does ^ step require a new Model?
    - **It might be enough to use Angular to pull that right out of the DB and stick it up on a chart

---

### TODO

- none of this has been TDD, so a big area I need to work on is how to write tests and move forward that way... will do
this once I get my head around one CORE route to make sure there are no super bad pitfalls.
- ideally it would be nice to set up some automated tests such as with Jenkins - but this is pie in the sky thinking
 - Have also not considered how to use Spark
 - Have also not considered how to use Kafka (if at all)
 - Have also not considered how to use Angular
 - Have also not considered how to use ReactJS
 - Have also not considered how to use Scalatra
 - Have also not considered how to use https://gephi.org/ (if at all)

 - Must set up server and run blockchain so there is a full transaction history available on the server

### PAST TODO:

Do this for getrawtransaction at first, with hardcoded values to begin with.

figure out how to get the BITCOIN-RPC to work within the existing code (at the moment there are two competing uses of
App)

— does it currently have the correct fields for what is a chainEntry? (look at ChainRepository.scala and look over actual
output from bitcoin server to see which info would be best to be recorded)
— What constitutes a complete query able blockchain in cassandra
— try to get an insertion into Cassandra through the ‘correct route’ (i.e. through the existing code which is similar to
WhiskeySteak)

### NOT YET
— this might include getting INPUT from OUTPUT of previous methods (such as the chain of commands needed to get to the
correct level to find OP_RETURN

/**
  * TODO FOR CODING
  * What needs to happen now is to upgrade to the latest phantom DSL
  * This will require looking through: http://outworkers.com/blog/post/a-series-on-phantom-part-1-getting-started-with-phantom
  * Once I have updated this I then need to make sure that I can still insert
  * Once I have done this I then need to get working with the Transaction class
  * Once I have done this I then need to reassess approach for MVP
  * Also need to consider more thorough testing - what is the minimum amount
  * Pie in the sky is to also use spark - but this should come after the MVP (data + visualisation + sonification?)
  *
  */