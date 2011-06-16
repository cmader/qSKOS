#!/usr/bin/env ruby

$LOAD_PATH.unshift(File.join(File.dirname(__FILE__), '..', 'lib'))
require 'qSKOS'
require 'logger'

class QSKOSExample

	def initialize
		if (paramsValid?)
			QSKOS.init(ARGV[0], Logger.new(STDOUT))
			processVocabulary
		end
	end

	private

	def paramsValid?
		if (ARGV.size != 1)
			puts "usage: qSKOS.rb rdfFileName"
			return false
		end
		return true
	end

	def processVocabulary
		allConcepts = QSKOS.findAllConcepts
		QSKOS.findLooseConcepts(allConcepts)
	end

end

QSKOSExample.new
