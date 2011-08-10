require_relative 'SKOSUtils'

class PropertyPartitionsFinder

	attr_reader :docPropertyStatements, :naturalLanguageLiteralsWithoutLangTag

	def initialize(loggingRdfReader, log)
		log.info("collecting concept properties")

		@reader = loggingRdfReader
		@log = log

		@labels = [SKOS.prefLabel, SKOS.altLabel, SKOS.hiddenLabel]
		@documentationProperties = [SKOS.note, SKOS.changeNote, SKOS.definition, SKOS.editorialNote,
			SKOS.example, SKOS.historyNote, SKOS.scopeNote]
		@naturalLanguageProperties = @labels + @documentationProperties

		@docPropertyStatements = []
		@naturalLanguageLiteralsWithoutLangTag = []

		identifyPartitions
	end

	private

	def identifyPartitions
		@reader.loopStatements do |statement|
			@docPropertyStatements << statement if @documentationProperties.include?(statement.predicate)
			@naturalLanguageLiteralsWithoutLangTag << statement.object if isNaturalLanguage(statement.predicate) && hasNoLangTag(statement.object)
		end
	end

	def isNaturalLanguage(predicate)
		@naturalLanguageProperties.include?(predicate)
	end

	def hasNoLangTag(object)
		object.literal? && !object.language?
	end

end
