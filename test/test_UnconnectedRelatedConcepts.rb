require 'helper'
require 'logger'
require 'qSKOS'

class TestUnconnectedRelatedConcepts < Test::Unit::TestCase

  def test_unconnectedRelatedConcepts
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		result =  QSKOS.getUnconnectedRelatedConcepts(allConcepts)
		
		assert_equal(result.size, 2)
  end

end
