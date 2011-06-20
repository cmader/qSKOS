#!/usr/bin/env ruby

require 'rdf'
require 'rdf/ntriples'
require 'rdf/raptor'

include RDF

module QSKOS

	autoload :ConceptFinder, 'qskos/ConceptFinder'
	autoload :LooseConceptFinder, 'qskos/LooseConceptFinder'
	autoload :LoggingRdfReader, 'qskos/LoggingRdfReader'
	autoload :ComponentFinder, 'qskos/ComponentFinder'
	autoload :CycleFinder, 'qskos/CycleFinder'
	autoload :ConceptLinkFinder, 'qskos/ConceptLinkFinder'
	autoload :LinkChecker, 'qskos/LinkChecker'
	autoload :ConceptPropertiesCollector, 'qskos/ConceptPropertiesCollector'
	autoload :InvalidSKOSTermsChecker, 'qskos/InvalidSKOSTermsChecker'

	def QSKOS.init(rdfFileName, log)
		@log = log

		readFile(rdfFileName)
	end

	def QSKOS.findAllConcepts
		conceptFinder = ConceptFinder.new(@loggingRdfReader, @log)
		@log.info("number of triples: #{@loggingRdfReader.totalStatements}")

		allConcepts = conceptFinder.getAllConcepts
		@log.info("number of concepts: #{allConcepts.size}")

		allConcepts
	end

	def QSKOS.findLooseConcepts(allConcepts)
		looseConceptFinder = LooseConceptFinder.new(@loggingRdfReader, @log, allConcepts)

		looseConcepts = looseConceptFinder.getLooseConcepts
		@log.info("number of loose concepts: #{looseConcepts.size}")

		looseConcepts
	end

	def QSKOS.findComponents(allConcepts)
		componentFinder = ComponentFinder.new(@loggingRdfReader, @log, allConcepts, true)
		components = componentFinder.getComponents
		@log.info("number of unconnected components: #{components.size}")

		components.each_index do |componentIndex|
			@log.info("vertices in component #{componentIndex}: #{components[componentIndex].vcount}")
		end

		components
	end

	def QSKOS.findCycles(allConcepts)
		cycleFinder = CycleFinder.new(@loggingRdfReader, @log, allConcepts)

		allCycles = cycleFinder.getCycles
		@log.info("number of minimum cycles: #{allCycles.size}")

		allCycles
	end

	def QSKOS.getExternalLinks(allConcepts)
		extLinkFinder = ConceptLinkFinder.new(@loggingRdfReader, @log, allConcepts)

		externalLinks = extLinkFinder.getExternalLinks 
		@log.info("number of external links: #{externalLinks.size}, avg. external links per concept: #{externalLinks.size.fdiv(allConcepts.size).round(3)}")

		externalLinks
	end

	def QSKOS.getDeprecatedPropertiesCount(allConcepts)
		propCollector = ConceptPropertiesCollector.new(@loggingRdfReader, @log, allConcepts)

		depPropCount = propCollector.deprPropertiesCount
		@log.info("total deprecated properties: #{depPropCount}, avg. documentation properties per concept: #{depPropCount.fdiv(allConcepts.size).round(3)}")

		depPropCount
	end

	def QSKOS.checkLinks
		linkChecker = LinkChecker.new(@loggingRdfReader, @log, true)
		checkedURIs = linkChecker.checkedURIs
		derefURIs = linkChecker.dereferencableURIs
		percentage = derefURIs.size.fdiv(checkedURIs.size).round(5) * 100

		@log.info("#{derefURIs.size} of #{checkedURIs.size} URIs dereferencable")
		@log.info("percentage of available link targets: #{percentage}")

		yield(checkedURIs, derefURIs)
	end

	def QSKOS.getInvalidSKOSTerms
		invalidSKOSTermsChecker = InvalidSKOSTermsChecker.new(@loggingRdfReader, @log)

		invalidTerms = invalidSKOSTermsChecker.getInvalidTerms.size
		@statInfo << "#{invalidTermsCount} invalid SKOS terms found" if invalidTerms.size > 0

		invalidTerms
	end

	private

	def QSKOS.readFile(fileName)
		@log.info("opening rdf file")
		@loggingRdfReader = LoggingRdfReader.new(RDF::Reader.open(fileName), @log)
	end

end

