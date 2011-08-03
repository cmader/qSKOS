require_relative 'SKOSUtils'

class ConceptPropertiesCollector

	attr_reader :docPropertyStatements, :docHumanReadableLabels

	def initialize(loggingRdfReader, log)
		log.info("collecting concept properties")

		@reader = loggingRdfReader
		@log = log

		@labels = [SKOS.prefLabel, SKOS.altLabel, SKOS.hiddenLabel, RDFS.label]
		@documentationProperties = [SKOS.note, SKOS.changeNote, SKOS.definition, SKOS.editorialNote,
			SKOS.example, SKOS.historyNote, SKOS.scopeNote]

		@docPropertyStatements = []
		@docHumanReadableLabels = []

		collectConceptProperties
	end

	private

	def collectConceptProperties
		@reader.loopStatements do |statement|
			@docPropertyStatements << statement if @documentationProperties.include?(statement.predicate)
			@docHumanReadableLabels << statement if hasHumanReadableLabel(statement)
		end
	end

	def hasHumanReadableLabel(statement)
		(statement.object.literal? && statement.object.language != nil) || @labels.include?(statement.predicate)
	end

end
