class UnconnectedRelatedConceptsFinder

	def initialize(loggingRdfReader, log, conceptLabels)
		log.info("finding semantically related unconnected concepts")

		@conceptLabels = conceptLabels
		@relatedConcepts = []

		identifyMatches

puts @relatedConcepts

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
				@relatedConcepts << [concept1, concept2, [label1Value, label2Value].uniq] if similar?(label1Value, label2Value)
			end
		end
	end

	def similar?(label1, label2)
		# up to now, two labels are treated as similar if they are identical
		label1 == label2
	end

end


