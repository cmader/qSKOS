require_relative 'SKOSUtils'

class PropertyPartitionsFinder

	attr_reader :docPropertyStatements, :naturalLanguageLiteralsWithoutLangTag, :naturalLanguageLiteralCount

	def initialize(loggingRdfReader, log)
		log.info("collecting concept properties")

		@reader = loggingRdfReader
		@log = log

		@labels = [SKOS.prefLabel, SKOS.altLabel, SKOS.hiddenLabel]
		@documentationProperties = [SKOS.note, SKOS.changeNote, SKOS.definition, SKOS.editorialNote,
			SKOS.example, SKOS.historyNote, SKOS.scopeNote]
		@naturalLanguageProperties = @labels + @documentationProperties + [RDFS.label, DC.title]

		@docPropertyStatements = []
		@naturalLanguageLiteralsWithoutLangTag = []
		@naturalLanguageLiteralCount = 0

		identifyPartitions
	end

	private

	def identifyPartitions
		@reader.loopStatements do |statement|
			checkDocumentationProperty(statement)	
			checkNaturalLanguage(statement)
		end
	end

	def checkDocumentationProperty(statement)
		@docPropertyStatements << statement if @documentationProperties.include?(statement.predicate)
	end

	def checkNaturalLanguage(statement)
		if isNaturalLanguage(statement.predicate)
			@naturalLanguageLiteralCount += 1
			@naturalLanguageLiteralsWithoutLangTag << statement.object if hasNoLangTag(statement.object)
		end
	end

	def isNaturalLanguage(predicate)
		@naturalLanguageProperties.include?(predicate)
	end

	def hasNoLangTag(object)
		object.plain? && !object.language?
	end

end
