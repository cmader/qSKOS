require_relative 'URIDereferencer'
require_relative 'UnhandledHTTPResponseException'
require_relative 'NoResponseUriFoundException'

class LinkChecker

	include RDF
	include URI

	attr_reader :checkedURIs, :dereferencableURIs

	def initialize(loggingRdfReader, log, logCheckedURIs = false)
		@reader = loggingRdfReader
		@log = log
		@logCheckedURIs = logCheckedURIs

		@checkedURIs = []
		@dereferencableURIs = []
		@dereferencer = URIDereferencer.new

		iterateAll
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
		if (uri.scheme == "http")
			uri = stripHashPortion(uri)
			if (!hasBeenChecked(uri))
				dereferenceURI(uri)
				@checkedURIs << uri
			end
		end
	end

	def stripHashPortion(uri)
		hashPos = uri.to_s.rindex('#')
		if hashPos != nil
			newUri = uri.to_s[0..hashPos-1]
			uri = URI.parse(newUri)
		end
		return uri
	end

	def hasBeenChecked(uri)
		return @checkedURIs.include?(uri)
	end

	def dereferenceURI(uri)
		@log.info("checking #{uri}") if @logCheckedURIs

		begin		
			if @dereferencer.dereferencable?(uri)
				@dereferencableURIs << uri 
			elsif @logCheckedURIs
				@log.info("failed")
			end		
		rescue UnhandledHTTPResponseException => unhandledExc
			@log.error("unhandled http response: #{unhandledExc.code}")
		rescue NoResponseUriFoundException => uriExc
			@log.error("cannot determine respose uri for '#{uriExc.uri}'");
		rescue Exception => exc
			@log.error("error dereferencing uri '#{uri}'; #{exc}");
		end
	end

end
