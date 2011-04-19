#!/usr/bin/env ruby

require 'rdf'
require 'rdf/ntriples'
require 'rdf/raptor'
require 'logger'

require_relative 'ConceptFinder'

include RDF

class QSKOS

	def initialize
		if (paramsValid?)
			@log = Logger.new(STDOUT)
			rdfReader = readFile(ARGV[0])

			processGraph(rdfReader)
		end
	end

	private

	def paramsValid?
		if (ARGV.size != 1)
			puts "usage: qSKOS.rb rdfFileName"
			return false
		end
		return true
	end

	def readFile(fileName)
		@log.info("opening rdf file")
		RDF::Reader.open(fileName)
	end

	def processGraph(rdfReader)
		conceptFinder = ConceptFinder.new(rdfReader, @log)
		puts conceptFinder.getAllConcepts.size
	end

end

QSKOS.new

