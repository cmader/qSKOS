#!/usr/bin/env ruby

require 'rdf'
require 'rdf/ntriples'
require 'rdf/raptor'
require 'logger'

require_relative 'ConceptFinder'
require_relative 'LooseConceptFinder'
require_relative 'LoggingRdfReader'
require_relative 'ComponentFinder'
require_relative 'CycleFinder'
require_relative 'ConceptLinkFinder'
require_relative 'LinkChecker'
require_relative 'ConceptPropertiesCollector'
require_relative 'InvalidSKOSTermsChecker'

include RDF

class QSKOS

	def initialize
		if (paramsValid?)
			@statInfo = []
			@log = Logger.new(STDOUT)
		
			readFile(ARGV[0])
			processGraph()

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
		@loggingRdfReader = LoggingRdfReader.new(RDF::Reader.open(fileName), @log)
	end

	def processGraph()
		#allConcepts = findAllConcepts
		#findLooseConcepts(allConcepts)
		#findComponents(allConcepts)
		#findCycles(allConcepts)
		#getExtLinkDegree(allConcepts)
		#checkLinks
		#getDocumentationAndDeprecatedCoverage(allConcepts)
		checkForInvalidSKOSTerms
	end

	def findAllConcepts
		conceptFinder = ConceptFinder.new(@loggingRdfReader, @log)
		@statInfo << "number of triples: #{@loggingRdfReader.totalStatements}";
		allConcepts = conceptFinder.getAllConcepts
		@statInfo << "number of concepts: #{allConcepts.size}";

		return allConcepts
	end

	def findLooseConcepts(allConcepts)
		looseConceptFinder = LooseConceptFinder.new(@loggingRdfReader, @log, allConcepts)
		@statInfo << "number of loose concepts: #{looseConceptFinder.getLooseConcepts.size}";
	end

	def findComponents(allConcepts)
		componentFinder = ComponentFinder.new(@loggingRdfReader, @log, allConcepts, true)
		components = componentFinder.getComponents
		@statInfo << "number of unconnected components: #{components.size}";

		components.each_index do |componentIndex|
			@statInfo << "vertices in component #{componentIndex}: #{components[componentIndex].vcount}";
		end
	end

	def findCycles(allConcepts)
		cycleFinder = CycleFinder.new(@loggingRdfReader, @log, allConcepts)
		allCycles = cycleFinder.getCycles
		@statInfo << "number of minimum cycles: #{allCycles.size}";
	end

	def getExtLinkDegree(allConcepts)
		extLinkFinder = ConceptLinkFinder.new(@loggingRdfReader, @log, allConcepts)
		extLinkCount = extLinkFinder.getExternalLinks.size
		@statInfo << "number of external links: #{extLinkCount}, avg. external links per concept: #{extLinkCount.fdiv(allConcepts.size).round(3)}";
	end

	def getDocumentationAndDeprecatedCoverage(allConcepts)
		propCollector = ConceptPropertiesCollector.new(@loggingRdfReader, @log, allConcepts)
		docPropCount = propCollector.docPropertiesCount
		@statInfo << "total documentation properties: #{docPropCount}, avg. documentation properties per concept: #{docPropCount.fdiv(allConcepts.size).round(3)}";

		depPropCount = propCollector.deprPropertiesCount
		@statInfo << "total deprecated properties: #{depPropCount}, avg. documentation properties per concept: #{depPropCount.fdiv(allConcepts.size).round(3)}";
	end

	def checkLinks
		linkChecker = LinkChecker.new(@loggingRdfReader, @log, true)
		checkedURIs = linkChecker.checkedURIs
		derefURIs = linkChecker.dereferencableURIs
		percentage = derefURIs.size.fdiv(checkedURIs.size).round(5) * 100

		@statInfo << "#{derefURIs.size} of #{checkedURIs.size} URIs dereferencable"
		@statInfo << "percentage of available link targets: #{percentage}"
	end

	def checkForInvalidSKOSTerms
		invalidSKOSTermsChecker = InvalidSKOSTermsChecker.new(@loggingRdfReader, @log)
		invalidTermsCount = invalidSKOSTermsChecker.getInvalidTerms.size

		@statInfo << "#{invalidTermsCount} invalid SKOS terms found" if invalidTermsCount > 0
	end

	def outputStatInfo
		@statInfo.each do |line|
			puts line
		end
	end

end

QSKOS.new

