require 'singleton'

class SKOSUtils

	include Singleton

	def initialize
		@inverseProperties = {
			SKOS.hasTopConcept => SKOS.topConceptOf,
			SKOS.topConceptOf => SKOS.hasTopConcept,
			SKOS.broader => SKOS.narrower,
			SKOS.narrower => SKOS.broader,
			SKOS.broaderTransitive => SKOS.narrowerTransitive,
			SKOS.narrowerTransitive => SKOS.broaderTransitive,
			SKOS.broadMatch => SKOS.narrowMatch,
			SKOS.narrowMatch => SKOS.broadMatch
		}
	end

	def inSkosNamespace?(resource)
		if 	!resource.node? && (resource.resource? || resource.uri?)
			return resource.to_uri.start_with?(SKOS.to_uri)
		end
		false
	end

	def getInverseProperty(property)
		value = @inverseProperties[property]

		raise NoSuchPropertyException.new if value == nil
		return value	
	end

	class NoSuchPropertyException < Exception
	end

end
