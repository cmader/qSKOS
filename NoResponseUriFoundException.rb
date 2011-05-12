class NoResponseUriFoundException < Exception

	attr_reader :uri

	def initialize(uri)
		@uri = uri
	end

end
