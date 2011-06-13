class UnknownTermsChecker

	def initialize(loggingRdfReader, log)
		@validProperties = {}
		@validClasses = {}

		loggingRdfReader.loopStatements do |statement|
			checkPredicate(statement.predicate)
		end
	end

	private

	def checkPredicate(predicate)

	end

end
