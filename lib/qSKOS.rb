#!/usr/bin/env ruby

require 'rdf'
require 'rdf/ntriples'
require 'rdf/raptor'

include RDF

module QSKOS

	attr_writer :rankingSparqlEndpoints

	autoload :ConceptFinder, 'qskos/ConceptFinder'
	autoload :LooseConceptFinder, 'qskos/LooseConceptFinder'
	autoload :LoggingRdfReader, 'qskos/LoggingRdfReader'
	autoload :ComponentFinder, 'qskos/ComponentFinder'
	autoload :CycleFinder, 'qskos/CycleFinder'
	autoload :ConceptLinkFinder, 'qskos/ConceptLinkFinder'
	autoload :LinkChecker, 'qskos/LinkChecker'
	autoload :PropertyPartitionsFinder, 'qskos/PropertyPartitionsFinder'
	autoload :TermsChecker, 'qskos/TermsChecker'
	autoload :ConceptRanker, 'qskos/ConceptRanker'
	autoload :LanguageCollector, 'qskos/LanguageCollector'
	autoload :AmbiguousLabelFinder, 'qskos/AmbiguousLabelFinder'
	autoload :ConceptLabelFinder, 'qskos/ConceptLabelFinder'
	autoload :UnconnectedRelatedConceptsFinder, 'qskos/UnconnectedRelatedConceptsFinder'

	def QSKOS.init(rdfFileName, log)
		@log = log
		@rankingSparqlEndpoints = []

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
		@log.info("number of external links (unique/total): #{externalLinks.uniq.size}/#{externalLinks.size}, avg. external links per concept: #{externalLinks.size.fdiv(allConcepts.size).round(3)}")

		externalLinks
	end

	def QSKOS.getPropertyPartitions(allConcepts)
		propCollector = PropertyPartitionsFinder.new(@loggingRdfReader, @log)

		docStatements = propCollector.docPropertyStatements
		@log.info("total documentation properties: #{docStatements.size}, avg. per concept: #{docStatements.size.fdiv(allConcepts.size).round(3)}")

		{:docStatements => docStatements}
	end

	def QSKOS.checkLinks
		linkChecker = LinkChecker.new(@loggingRdfReader, @log, true)
		checkedURIs = linkChecker.checkedURIs
		derefURIs = linkChecker.dereferencableURIs
		percentage = derefURIs.size.fdiv(checkedURIs.size).round(5) * 100

		@log.info("#{derefURIs.size} of #{checkedURIs.size} URIs dereferencable")
		@log.info("percentage of available link targets: #{percentage}")

		{:checkedURIs => checkedURIs, :derefURIs => derefURIs}
	end

	def QSKOS.getInvalidTerms
		termsChecker = TermsChecker.new(@loggingRdfReader, @log)

		invalidTerms = termsChecker.getInvalidTerms
		unknownTermStatements = invalidTerms.first
		deprecatedTermStatements = invalidTerms.last

		@log.info("#{unknownTermStatements.size} unknown terms, #{deprecatedTermStatements.size} deprecated terms") if invalidTerms.size > 0

		{:unknownTermStatements => unknownTermStatements, :deprecatedTermStatements => deprecatedTermStatements}
	end

	def QSKOS.rankConcepts(allConcepts, sparqlEndpoint = nil)
		@rankingSparqlEndpoints = [sparqlEndpoint] unless sparqlEndpoint.nil?
		rankedConcepts = ConceptRanker.new(@log, @rankingSparqlEndpoints).rankConcepts(allConcepts)

		rankSum = 0
		rankedConcepts.each do |rankedConcept|
			rankSum = rankSum + rankedConcept.last[:hosts].size
		end
		avgRank = rankSum.fdiv(allConcepts.size).round(3)

		@log.info("concepts ranked; avg. #{avgRank}")
		rankedConcepts
	end

	def QSKOS.getLanguageCoverage(allConcepts)
		langCollector = LanguageCollector.new(@loggingRdfReader, @log, allConcepts)

		coverageRatioPerConcept = langCollector.getCoverageRatioPerConcept
		avgRatio = langCollector.getAvgLanguageRatio
		fullCoverageConcepts = langCollector.getFullCoverageConcepts
		fullCoverageRatio =	langCollector.getFullCoverageRatio
		allLanguages = langCollector.getAllLanguages

		{:coverageRatioPerConcept => coverageRatioPerConcept, :fullCoverageConcepts => fullCoverageConcepts, :avgRatio => avgRatio, :fullCoverageRatio => fullCoverageRatio, :allLanguages => allLanguages}
	end

	def QSKOS.getAmbiguouslyLabeledConcepts(allConcepts)
		conceptLabels = ConceptLabelFinder.new(@loggingRdfReader, @log, allConcepts).conceptLabels
		AmbiguousLabelFinder.new(@loggingRdfReader, @log, conceptLabels).getAmbiguouslyLabeledConcepts
	end

	def QSKOS.getUnconnectedRelatedConcepts(allConcepts)
		conceptLabels = ConceptLabelFinder.new(@loggingRdfReader, @log, allConcepts).conceptLabels
		UnconnectedRelatedConceptsFinder.new(@loggingRdfReader, @log, conceptLabels).unconnectedRelatedConcepts
	end

	def QSKOS.getLanguageTagSupport
		propCollector = PropertyPartitionsFinder.new(@loggingRdfReader, @log)
		propCollector.naturalLanguageLiteralsWithoutLangTag
	end

	private

	def QSKOS.readFile(fileName)
		@log.info("opening rdf file")
		@loggingRdfReader = LoggingRdfReader.new(RDF::Reader.open(fileName), @log)
	end

end

