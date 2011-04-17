#!/usr/bin/env ruby

require 'rdf/raptor'
require 'logger'

require_relative 'ConceptFinder'

include RDF

class QSKOS

	def initialize
		if (paramsValid?)
			@log = Logger.new(STDOUT)
			rdfGraph = readFile(ARGV[0])

			processGraph(rdfGraph)
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
		@log.info("reading rdf graph")
		RDF::Graph.load(fileName)
	end

	def processGraph(rdfGraph)
		ConceptFinder.new(rdfGraph, @log)
	end

end

QSKOS.new

