require_relative 'URIDereferencer'

class LinkChecker

	include RDF

	def initialize(loggingRdfReader, log)
		@reader = loggingRdfReader
		@log = log
		@checkedURIs = []
		@dereferencableURIs = []
		@dereferencer = URIDereferencer.new

		iterateAll
	end

	def getDereferencableURIPercentage
		@dereferencableURIs.size.fdiv(@checkedURIs.size).round(3)
	end

	private

	def iterateAll
		@log.info("checking link target availability")
		@reader.loopStatements do |statement|
			checkForURIs(statement)
		end
	end

	def checkForURIs(statement)
		if (statement.subject.uri?)
			checkURI(statement.subject)
		end
		if (statement.object.uri?)
			checkURI(statement.object)
		end
	end

	def checkURI(uri)
		uri = stripHashPortion(uri)
		if (!hasBeenChecked(uri))
			dereferenceURI(uri)
			@checkedURIs << uri
		end
	end

	def stripHashPortion(uri)
		hashPos = uri.to_s.rindex('#')
		if hashPos != nil
			newUri = uri.to_s[0..hashPos-1]
			uri = URI.new(newUri)
		end
		return uri
	end

	def hasBeenChecked(uri)
		return @checkedURIs.include?(uri)
	end

	def dereferenceURI(uri)
		@log.info("checking #{uri}")
		
		@dereferencableURIs << uri if @dereferencer.dereferencable?(uri)
	end

end
