class InvalidSKOSTermsChecker

	def initialize(loggingRdfReader, log)

		@validTerms = [SKOS.altLabel, SKOS.broadMatch, SKOS.broader, SKOS.broaderTransitive, SKOS.changeNote, SKOS.closeMatch, SKOS.definition, SKOS.editorialNote, SKOS.exactMatch, SKOS.example, SKOS.hasTopConcept, SKOS.hiddenLabel, SKOS.historyNote, SKOS.inScheme, SKOS.mappingRelation, SKOS.member, SKOS.memberList, SKOS.narrowMatch, SKOS.narrower, SKOS.narrowerTransitive, SKOS.notation, SKOS.note, SKOS.prefLabel, SKOS.related, SKOS.relatedMatch, SKOS.scopeNote, SKOS.semanticRelation, SKOS.topConceptOf, 
SKOS.Concept, SKOS.ConceptScheme, SKOS.Collection, SKOS.OrderedCollection, SKOS.Collection, SKOS.ConceptScheme]

		@invalidTerms = []

		loggingRdfReader.loopStatements do |statement|
			checkTerm(statement.predicate)
			checkTerm(statement.object)
		end
	end

	def getInvalidTerms
		@invalidTerms.uniq
	end

	private

	def checkTerm(term)
		if SKOSUtils.instance.inSkosNamespace?(term) && !@validTerms.include?(term)
			@invalidTerms << term
		end
	end

end
