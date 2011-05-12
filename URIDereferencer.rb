require 'net/http'
require_relative 'UnhandledHTTPResponseException'

class URIDereferencer

	def dereferencable?(uri)
		if uri.respond_to?('request_uri')
			response = Net::HTTP.get_response(uri)
			return validResponse?(uri, response)
		else
			raise NoResponseUriFoundException.new(uri)
		end
	end

	private

	def validResponse?(uri, response)
		case response.code
		when "200"
			return true
		when "404"
			return false
		when "301", "302", "303"
			return redirectedUriDereferencable?(uri, URI.parse(response["location"]))
		else
			raise UnhandledHTTPResponseException.new(response.code)
		end
		return false
	end

	def	redirectedUriDereferencable?(origUri, redirUri)
		if origUri != redirUri
			if redirUri.to_s.start_with?("/")
				redirUri = getUriFromRelativePath(origUri, redirUri)
			end
			return dereferencable?(redirUri)
		else
			return false
		end
	end

	def getUriFromRelativePath(origUri, relPath)
		origUri.path=(relPath.to_s)
		return origUri
	end

end
