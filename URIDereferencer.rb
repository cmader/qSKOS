require 'net/http'

class URIDereferencer

	def dereferencable?(uri)
		response = Net::HTTP.get_response(uri)
		return validResponse?(response)
	end

	private

	def validResponse?(response)
puts response.code
		case response.code
		when "200"
			return true
		when "404"
			return false
		when "301", "303"
			return dereferencable?(URI.parse(response["location"]))
		else
			raise UnhandledHTTPResponseException.new
		end
		return false
	end

	class UnhandledHTTPResponseException < Exception
	end

end
