class LoggingRdfReader

	attr_reader :totalStatements

	def initialize(rdfReader, log)
		@rdfReader = rdfReader
		@log = log
	end

	def loopStatements
		i = 1;
		@prevPercentage = 0

		@rdfReader.each_statement do |statement|
			if (@totalStatements == nil)
				outputProcessedTriples(i)
			else
				outputPercentage(i)
			end
			i += 1

			yield(statement)
		end

		@totalStatements = i-1
	end

	private

	def outputProcessedTriples(count)
		if (count % 5000 == 0) 
			@log.info("processed >#{count} triples")
		end
	end

	def outputPercentage(count)
		percentage = Integer((count.fdiv(@totalStatements)) * 100)

		if (percentage % 10 == 0 && percentage > @prevPercentage)
			@log.info("#{percentage}% finished")
		
			@prevPercentage = percentage
		end
	end

end
