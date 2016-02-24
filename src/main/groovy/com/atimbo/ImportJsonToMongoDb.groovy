package com.atimbo

/******************************
* Script to generate documents
* in a Mongo DB collection from
* JSON data.
******************************/

import com.gmongo.GMongo

String filePath = 'collection.json'
String serverAddr = '127.0.0.1'
Integer port = 27017
String dbName = 'myDB'
String collectionName = 'things'
JsonFileHandler reader = new JsonFileHandler()

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
json."$collectionName".each { 
    println "inserting::$it" 
    db."$collectionName".insert(it)
}
println "things::${db.things.count()}"

println "Done"