class ConceptPropertiesCollector

	attr_reader :docPropertiesCount, :deprPropertiesCount

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("collecting concept properties")

		@reader = loggingRdfReader
		@log = log

		@documentationProperties = [SKOS.note, SKOS.changeNote, SKOS.definition, SKOS.editorialNote,
			SKOS.example, SKOS.historyNote, SKOS.scopeNote]
		@deprecatedProperties =  [SKOS.symbol, SKOS.prefSymbol, SKOS.altSymbol, SKOS.CollectableProperty,
	    SKOS.subject, SKOS.isSubjectOf, SKOS.primarySubject, SKOS.isPrimarySubjectOf, SKOS.subjectIndicator]

		initConceptsWithPropertiesHash(allConcepts)
		collectConceptProperties
		countProperties
	end

	private

	def countProperties
		@docPropertiesCount = 0
		@deprPropertiesCount = 0
		@conceptsWithProperties.values.each do |properties|
			properties.each do |property|
				@docPropertiesCount += 1 if @documentationProperties.include?(property)
				@deprPropertiesCount += 1 if @deprecatedProperties.include?(property)
			end	
		end
	end

	def initConceptsWithPropertiesHash(allConcepts)
		@conceptsWithProperties = {}
		allConcepts.each do |concept|
			@conceptsWithProperties[concept] = []
		end
	end

	def collectConceptProperties
		@reader.loopStatements do |statement|
			if isSkosPredicate(statement.predicate) && @conceptsWithProperties.keys.include?(statement.subject)
				@conceptsWithProperties[statement.subject] << statement.predicate
			end
		end
	end

	def isSkosPredicate(predicate)
		return SKOSUtils.instance.inSkosNamespace?(predicate)
	end

end
