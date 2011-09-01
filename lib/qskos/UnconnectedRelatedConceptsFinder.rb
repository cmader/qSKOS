class UnconnectedRelatedConceptsFinder

	attr_reader :unconnectedRelatedConcepts, :relatedConcepts

	def initialize(loggingRdfReader, log, conceptLabels)
		log.info("finding semantically related unconnected concepts")

		@reader = loggingRdfReader
		@conceptLabels = conceptLabels
		@relatedConcepts = []

		identifyMatches
		checkConnectivity
	end

	private

	def identifyMatches
		@conceptLabels.each_pair do |concept, language|
			language.each_pair do |langKey, labels|
				compareWithOthers(concept, langKey, labels.values.flatten)
			end
		end
	end

	def compareWithOthers(referenceConcept, langKey, labelValues)
		@conceptLabels.each_pair do |concept, language|
			if (concept != referenceConcept)
				if (langKey != nil)
					compareLabelValues(referenceConcept, labelValues, concept, language[langKey].values.flatten)
				else
					otherLabels = getLabelsFromLanguageHash(language)
					compareLabelValues(referenceConcept, labelValues, concept, otherLabels)
				end
			end
		end
	end

	def getLabelsFromLanguageHash(language)
		labels = []
		language.each_value do |labelHash|
			labelHash.each_value do |labelList|
				labels << labelList
			end
		end
		labels.flatten
	end

	def compareLabelValues(concept1, labels1, concept2, labels2)
		labels1.each do |label1Value|
			labels2.each do |label2Value|
				addToRelatedConcepts(ConceptSimilarity.new(concept1, concept2, [label1Value, label2Value].uniq)) if similar?(label1Value, label2Value)
			end
		end
	end

	def addToRelatedConcepts(conceptSimilarity)
		index = @relatedConcepts.index(conceptSimilarity)
		if index == nil
			@relatedConcepts << conceptSimilarity
		else
			@relatedConcepts[index].appendValue(conceptSimilarity.value)
		end
	end

	def similar?(label1, label2)
		# up to now, two labels are treated as similar if they are identical
		label1.downcase == label2.downcase
	end

	def checkConnectivity
		@unconnectedRelatedConcepts = @relatedConcepts.clone

		@reader.loopStatements do |statement|
			if statement.subject.resource? && statement.object.resource?
				@unconnectedRelatedConcepts.delete(ConceptSimilarity.new(statement.subject, statement.object))
			end
		end
	end

end

class ConceptSimilarity

	attr_reader :concept1, :concept2, :value

	def initialize(concept1, concept2, value = nil)
		@concept1 = concept1
		@concept2 = concept2
		@value = [value]
	end

	def ==(other)
		(concept1 == other.concept1 && concept2 == other.concept2) ||
		(concept1 == other.concept2 && concept2 == other.concept1)
	end

	def to_s
		"#{concept1}, #{concept2}, #{value}"
	end

	def appendValue(value)
		@value << value
		@value.flatten!.uniq!
	end
end
