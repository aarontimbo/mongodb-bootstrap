package com.atimbo

/******************************
* Script to generate documents
* in a Mongo DB collection from
* JSON data.
******************************/

import com.gmongo.GMongo
import groovy.json.JsonOutput


String filePath = 'collection.json'
String serverAddr = '127.0.0.1'
Integer port = 27017
String dbName = 'myDB'
String collectionName = 'things'
JsonFileHandler reader = new JsonFileHandler()

if (args && args.size() == 3) {
    dbName = args[0]
    collectionName = args[1]
    filePath = args[2]
} else {
    showUsage()
    System.exit(-1)
}

File jsonFile = new File(filePath)

if (!jsonFile.exists()) {
    println 'Doh! File not found.'
    System.exit(-1)
}
println "Processing file $filePath..."

// Read JSON file
def json = reader.read(jsonFile)
println "json::$json"

// Instantiate a com.gmongo.GMongo object
def mongo = new GMongo(serverAddr, port)

// Get a db reference
def db = mongo.getDB(dbName)

// Drop collection if it exists
db."$collectionName".drop()

// Insert documents from JSON data into mongo collection
//def c = { val ->
//    if (val.instanceOf(B))
//}
json."$collectionName".each { Map map ->
    println "inserting::${map}"
    def currencyPrice = map?.currencyPrices[0].value
    if (currencyPrice && currencyPrice instanceof BigDecimal) {
        println "Found BigDecimal. Replacing with Double"
        map.currencyPrices[0].value = currencyPrice as Double
    }
    db."$collectionName".insert(map)
}
println "${collectionName}::" + db."$collectionName".count()

def showUsage() {
    println '''\
        Doh! Execution Failed!

        Usage:

            gradle runScript -PcollectionName=collection -PjsonFile=/path/to/json/file
            '''.stripIndent()
}
println "Done"