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
		
puts		QSKOS.findLooseConcepts(allConcepts)
		#QSKOS.findComponents(allConcepts)

=begin
cycles = QSKOS.findCycles(allConcepts)
cycles.each do |cycle|
	puts cycle
	puts "---"
end
=end

		#QSKOS.getExternalLinks(allConcepts)
		#QSKOS.checkLinks

		#QSKOS.getPropertyPartitions(allConcepts)[:humanReadableLabels]
		#QSKOS.getInvalidTerms

		#rankedConcepts = QSKOS.rankConcepts(allConcepts, "http://sparql.sindice.com/sparql")
		#dumpFirstRankedConcepts(rankedConcepts)

		#QSKOS.getLanguageTagSupport
#langcoverage = QSKOS.getLanguageCoverage(allConcepts)
#puts "avgRatio: #{langcoverage[:avgRatio]}"
#puts "fullCoverageRatio: #{langcoverage[:fullCoverageRatio]}"


		#QSKOS.getAmbiguouslyLabeledConcepts(allConcepts)
		#QSKOS.getUnconnectedRelatedConcepts(allConcepts)
	end

	def dumpFirstRankedConcepts(rankedConcepts)
		sorted = rankedConcepts.to_a.sort do |x, y|
			y.last[:hosts].size <=> x.last[:hosts].size
		end
		puts sorted.first(10).to_s
	end

end

QSKOSExample.new
