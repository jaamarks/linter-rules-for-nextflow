/*
 * Tests for IGenomesRule
 *
 * @author Jesse Marks
*/

package software.amazon.nextflow.rules.config

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

class IGenomesTest extends AbstractRuleTestCase<IGenomesRule> {
    @Override
    protected IGenomesRule createRule() {
        return new IGenomesRule()
    }

    @Test
    void ruleProperties() {
        assert rule.priority == 1
        assert rule.name == 'IGenomesRule'
    }

    @Test
    void IGenomesProperty_NoViolations() {
        final SOURCE = '''
params.igenomes_base = 's3://igenomes_nf'
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void IGenomesBlock_NoViolations() {
        final SOURCE = '''
params {
    igenomes_base = 's3://igenomes_nf'
}
'''
        assertNoViolations(SOURCE)
    }

// change this local location to be different from fdb
    @Test
    void IGenomesProperty_LocalViolation() {
        final SOURCE = '''
params.igenomes_base = '/fdb/igenomes_nf/'
'''
        assertSingleViolation(SOURCE, 2, "params.igenomes_base = '/fdb/igenomes_nf/'", "Expected a cloud-based iGenomes base location (e.g., s3://ngi-igenomes), but found '/fdb/igenomes_nf/'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
    }

    @Test
    void IGenomesBlock_LocalViolation() {
        final SOURCE = '''
params {
  igenomes_base = '/fdb/igenomes_nf/'
}
'''
        assertSingleViolation(SOURCE, 3, "igenomes_base = '/fdb/igenomes_nf/'", "Expected a cloud-based iGenomes base location (e.g., s3://ngi-igenomes), but found '/fdb/igenomes_nf/'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
    }
}
