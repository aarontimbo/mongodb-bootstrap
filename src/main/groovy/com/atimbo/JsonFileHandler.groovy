package com.atimbo

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class JsonFileHandler {
	
	def read(File jsonFile) {
		JsonSlurper jsonSlurper = new JsonSlurper()
		def reader = new BufferedReader(new FileReader(jsonFile))
		return jsonSlurper.parse(reader)	
	}

	def write(def content, File file) {
		file.write(new JsonBuilder(content).toPrettyString())
	}

}