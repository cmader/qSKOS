require 'net/http'
require_relative 'UnhandledHTTPResponseException'

class URIDereferencer

	def dereferencable?(uri)
		response = Net::HTTP.get_response(uri)
		return validResponse?(uri, response)
	end

	private

	def validResponse?(uri, response)
		case response.code
		when "200"
			return true
=begin
		when "404"
			return false
=end
		when "301", "303"
			return redirectedUriDereferencable?(uri, URI.parse(response["location"]))
		else
			raise UnhandledHTTPResponseException.new(response.code)
		end
		return false
	end

	def	redirectedUriDereferencable?(origUri, redirUri)
		if origUri != redirUri
			return dereferencable?(redirUri)
		else
			return false
		end
	end

end
