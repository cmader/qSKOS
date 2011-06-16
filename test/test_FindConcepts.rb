require 'helper'
require 'logger'
require 'qSKOS'

class TestFindConcepts < Test::Unit::TestCase

  def test_countConcepts
		QSKOS.init("test/testdata/concepts.rdf", @log)
		foundConcepts = QSKOS.findAllConcepts
		assert_equal(foundConcepts.size, 11)

		QSKOS.init("test/testdata/components.rdf", @log)
		foundConcepts = QSKOS.findAllConcepts
		assert_equal(foundConcepts.size, 16)
  end

	def setup
		@log = Logger.new(STDOUT)
	end

end
