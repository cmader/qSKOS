require_relative 'SKOSUtils'

class ConceptPropertiesCollector

	attr_reader :docPropertyStatements

	def initialize(loggingRdfReader, log)
		log.info("collecting concept properties")

		@reader = loggingRdfReader
		@log = log

		@documentationProperties = [SKOS.note, SKOS.changeNote, SKOS.definition, SKOS.editorialNote,
			SKOS.example, SKOS.historyNote, SKOS.scopeNote]

		@docPropertyStatements = []

		collectConceptProperties
	end

	private

	def collectConceptProperties
		@reader.loopStatements do |statement|
			if isSkosPredicate(statement.predicate)
				@docPropertyStatements << statement if @documentationProperties.include?(statement.predicate)
			end
		end
	end

	def isSkosPredicate(predicate)
		SKOSUtils.instance.inSkosNamespace?(predicate)
	end

end
