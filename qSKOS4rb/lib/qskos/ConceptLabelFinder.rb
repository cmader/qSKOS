class ConceptLabelFinder

	attr_reader :conceptLabels

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("collecting SKOS concept labels")

		@reader = loggingRdfReader
		@allConcepts = allConcepts

		@labels = {SKOS.prefLabel => :prefLabel, SKOS.altLabel => :altLabel, SKOS.hiddenLabel => :hiddenLabel}
		@conceptLabels = Hash.new do |hash1, concept|
			hash1[concept] = Hash.new do |hash2, language|
				hash2[language] = Hash.new do |hash3, label|
					hash3[label] = []
				end
			end
		end

		collectLabels
	end

	private

	def collectLabels
		@reader.loopStatements do |statement|
			if @allConcepts.include?(statement.subject) && @labels.keys.include?(statement.predicate)
				addLabel(statement.subject, @labels[statement.predicate], statement.object)
			end
		end
	end

	def addLabel(concept, label, object)
		@conceptLabels[concept][object.language][label] << object.value
		@conceptLabels[concept][object.language][label].uniq!
	end

end
