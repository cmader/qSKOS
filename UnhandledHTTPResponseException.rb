class UnhandledHTTPResponseException < Exception

		attr_reader :code

		def initialize(code)
			@code = code
		end

end
