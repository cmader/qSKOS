#!/usr/bin/env ruby

require 'rdf'
require 'rdf/ntriples'
require 'rdf/raptor'
require 'logger'

require_relative 'ConceptFinder'
require_relative 'LooseConceptFinder'
require_relative 'LoggingRdfReader'
require_relative 'ComponentFinder'

include RDF

class QSKOS

	def initialize
		if (paramsValid?)
			@statInfo = []
			@log = Logger.new(STDOUT)
			loggingRdfReader = readFile(ARGV[0])

			processGraph(loggingRdfReader)
			outputStatInfo
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
		LoggingRdfReader.new(RDF::Reader.open(fileName), @log)
	end

	def processGraph(loggingRdfReader)
		conceptFinder = ConceptFinder.new(loggingRdfReader, @log)
		@statInfo << "number of triples: #{loggingRdfReader.totalStatements}";
		allConcepts = conceptFinder.getAllConcepts
		@statInfo << "number of concepts: #{allConcepts.size}";

		#Loose Concepts
		#looseConceptFinder = LooseConceptFinder.new(loggingRdfReader, @log, allConcepts)
		#@statInfo << "number of loose concepts: #{looseConceptFinder.getLooseConcepts.size}";

		#Weakly connected components
		componentFinder = ComponentFinder.new(loggingRdfReader, @log, allConcepts)
		@statInfo << "number of unconnected components: #{componentFinder.getComponentCount}";
	end

	def outputStatInfo
		@statInfo.each do |line|
			puts line
		end
	end

end

QSKOS.new

