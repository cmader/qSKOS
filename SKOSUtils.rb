require 'singleton'

class SKOSUtils

	include Singleton

	def inSkosNamespace?(resource)
		if 	!resource.node? && (resource.resource? || resource.uri?)
			return resource.to_uri.start_with?(SKOS.to_uri)
		end
		false
	end

end
