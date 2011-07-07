require_relative 'SKOSUtils'

class TermsChecker

	def initialize(loggingRdfReader, log)

		@knownTerms = [SKOS.altLabel, SKOS.broadMatch, SKOS.broader, SKOS.broaderTransitive, SKOS.changeNote, SKOS.closeMatch, SKOS.definition, SKOS.editorialNote, SKOS.exactMatch, SKOS.example, SKOS.hasTopConcept, SKOS.hiddenLabel, SKOS.historyNote, SKOS.inScheme, SKOS.mappingRelation, SKOS.member, SKOS.memberList, SKOS.narrowMatch, SKOS.narrower, SKOS.narrowerTransitive, SKOS.notation, SKOS.note, SKOS.prefLabel, SKOS.related, SKOS.relatedMatch, SKOS.scopeNote, SKOS.semanticRelation, SKOS.topConceptOf, 
SKOS.Concept, SKOS.ConceptScheme, SKOS.Collection, SKOS.OrderedCollection, SKOS.Collection, SKOS.ConceptScheme]

		@deprecatedTerms =  [SKOS.symbol, SKOS.prefSymbol, SKOS.altSymbol, SKOS.CollectableProperty,
	    SKOS.subject, SKOS.isSubjectOf, SKOS.primarySubject, SKOS.isPrimarySubjectOf, SKOS.subjectIndicator]

		@unknownTermStatements = []
		@deprecatedTermStatements = []

		loggingRdfReader.loopStatements do |statement|
			checkStatement(statement)
		end
	end

	def getInvalidTerms
		[@unknownTermStatements.uniq, @deprecatedTermStatements.uniq]
	end

	private

	def checkStatement(statement)
		if containsDeprecatedTerms(statement)
			@deprecatedTermStatements << statement
		else
			@unknownTermStatements << statement if containsUnknownTerms(statement)
		end
	end

	def containsUnknownTerms(statement)
		termUnknown?(statement.predicate) || termUnknown?(statement.object)
	end

	def termUnknown?(term)
		SKOSUtils.instance.inSkosNamespace?(term) && !@knownTerms.include?(term)
	end

	def termDeprecated?(term)
		@deprecatedTerms.include?(term)
	end

	def containsDeprecatedTerms(statement)
		termDeprecated?(statement.predicate) || termDeprecated?(statement.object)
	end

end
