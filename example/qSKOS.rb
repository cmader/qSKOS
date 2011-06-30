#!/usr/bin/env ruby

$LOAD_PATH.unshift(File.join(File.dirname(__FILE__), '..', 'lib'))
require 'qSKOS'
require 'logger'

class QSKOSExample

	def initialize
		if (paramsValid?)
			QSKOS.init(ARGV[0], Logger.new(STDOUT))
			processVocabulary
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

	def processVocabulary
		allConcepts = QSKOS.findAllConcepts
		rankedConcepts = QSKOS.rankConcepts(allConcepts, "http://sparql.sindice.com/sparql")
		dumpFirstRankedConcepts(rankedConcepts)
	end

	def dumpFirstRankedConcepts(rankedConcepts)
		sorted = rankedConcepts.to_a.sort do |x, y|
			y.last[:hosts].size <=> x.last[:hosts].size
		end
		puts sorted.first(10).to_s
	end

end

QSKOSExample.new
