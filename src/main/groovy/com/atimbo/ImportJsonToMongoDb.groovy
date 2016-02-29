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

def c = { val ->
    if (val instanceof BigDecimal) {
        println "Found BigDecimal. Replacing with Double"
        return val as Double
    } else {
        return val
    }
}

// Insert documents from JSON data into mongo collection
json."$collectionName".each { Map map ->
    println "inserting::${map}"
    // WARNING: This only goes one level deep if the map contains list
    map.each { k, v ->
        println "Processing key::${k}"
        if (v instanceof List) {
            v.each { Map subMap ->
                subMap.each { subk, subv ->
                    subMap[subk] = c(subv)    
                }
            }
        } else {
            map[k] = c(v)
        }
    }
    db."$collectionName".insert(map)
}
println "${collectionName}::" + db."$collectionName".count()

def showUsage() {
    println '''\
        Doh! Execution Failed!

        Usage:

            gradle runScript -PdbName=db -PcollectionName=collection -PjsonFile=/path/to/json/file
            '''.stripIndent()
}
println "Done"