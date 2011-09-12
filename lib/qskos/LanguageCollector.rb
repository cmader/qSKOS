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

	def getAllLanguages
		@conceptLanguages.values.flatten.uniq
	end

	def getCoverageRatioPerConcept
		differentLanguages = getAllLanguages
		conceptLanguageCoverageRatio = {}

		@conceptLanguages.each do |key, value|
			langRatio = value.size.fdiv(differentLanguages.size)
			conceptLanguageCoverageRatio[key] = langRatio
		end
	
		conceptLanguageCoverageRatio
	end

	def getAvgLanguageRatio
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

	def getFullCoverageRatio
		getFullCoverageConcepts.size.fdiv(@conceptLanguages.keys.size)
	end

	def getLanguageDistribution
		# collects concepts with one/two/three/.../n language(s) defined
		langDistribution = Hash.new do |hash, key|
			hash[key] = []
		end

		getAllLanguages.each_index do |index|
			@conceptLanguages.each do |concept, languages|
				langDistribution[index + 1] << concept if languages.size == index + 1
			end
		end

		langDistribution
	end

	private

	def collectLanguageLiterals
		@reader.loopStatements do |statement|
			if statement.object.literal? && statement.object.language?
				processStatement(statement)
			end
		end
	end

	def processStatement(statement)
		if @allConcepts.include?(statement.subject)
			@conceptLanguages[statement.subject] << statement.object.language
		else				
			@nonConceptLanguages << statement.object.language
		end
	end

	def removeDuplicates
		@conceptLanguages.values.each do |languageList|
			languageList.uniq!
		end
		@nonConceptLanguages.uniq!
	end

end
