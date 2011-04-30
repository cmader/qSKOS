require 'singleton'

class SKOSUtils

	include Singleton

	def inSkosNamespace?(predicate)
		predicate.to_uri.start_with?(SKOS.to_uri)
	end

end
