class LanguageCollector

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("collecting literal languages")

		@reader = loggingRdfReader
		@log = log
		@allConcepts = allConcepts
		@nonConceptLanguages = []
		@conceptLanguages = Hash.new do |hash, key|
			hash[key] = []
		end

		collectLanguageLiterals
		removeDuplicates
	end

	def getCoverageRatioPerConcept
		differentLanguages = @conceptLanguages.values.uniq
		conceptLanguageCoverageRatio = {}

		@conceptLanguages.each do |key, value|
			langRatio = value.size.fdiv(differentLanguages.size)
			conceptLanguageCoverageRatio[key] = langRatio
		end
	
		conceptLanguageCoverageRatio
	end

	def getAvgLanguageCoverage
		ratioSum = 0;
		getCoverageRatioPerConcept.values.cycle(1) do |ratio|
			ratioSum += ratio
		end

		ratioSum.fdiv(@conceptLanguages.size)
	end

	def getFullCoverageConcepts
		fullCoverageConcepts = []
		getCoverageRatioPerConcept.each do |key, value|
			fullCoverageConcepts << key if value == 1
		end

		fullCoverageConcepts
	end

	private

	def collectLanguageLiterals
		@reader.loopStatements do |statement|
			if statement.object.literal? && statement.object.language?
				if @allConcepts.include?(statement.subject)
					@conceptLanguages[statement.subject] << statement.object.language
				else				
					@nonConceptLanguages << statement.object.language
				end
			end
		end
	end

	def removeDuplicates
		@conceptLanguages.values.each do |languageList|
			languageList.uniq!
		end
		@nonConceptLanguages.uniq!
	end

end
