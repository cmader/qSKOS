require 'helper'
require 'logger'
require 'qSKOS'

class TestDereferenceLinks < Test::Unit::TestCase

  def test_dereferenceLinks_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		
		linkCheckResult = QSKOS.checkLinks 
		checkedUris = linkCheckResult[:checkedURIs]
		dereferencableUris = linkCheckResult[:derefURIs]
		
		assert_equal(checkedUris.size, 1)
		assert_equal(dereferencableUris.size, 1)
  end

  def test_dereferenceLinks_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))

		linkCheckResult = QSKOS.checkLinks 
		checkedUris = linkCheckResult[:checkedURIs]
		dereferencableUris = linkCheckResult[:derefURIs]
		failed = checkedUris - dereferencableUris

		assert_equal(checkedUris.size, 19)
		assert_equal(dereferencableUris.size, 13)
		assert_equal(failed.size, 6)
  end

end
