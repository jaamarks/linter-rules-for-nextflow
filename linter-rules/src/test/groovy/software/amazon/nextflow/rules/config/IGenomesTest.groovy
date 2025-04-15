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
    void ruleProperties_areSetCorrectly() {
        assert rule.priority == 1
        assert rule.name == 'IGenomesRule'
    }


    @Test
    void paramWithValidS3Uri_hasNoViolations() {
        final SOURCE = '''
params.igenomes_base = 's3://ngi-igenomes'
'''
        assertNoViolations(SOURCE)
    }


    @Test
    void paramDiffNameWithValidS3Uri_hasNoViolations() {
        final SOURCE = '''
params.base_igenomes_Dir = 's3://ngi-igenomes'
'''
        assertNoViolations(SOURCE)
    }


    @Test
    void paramInBlockValidS3Uri_hasNoViolations() {
        final SOURCE = '''
params {
    igenomes_base = 's3://igenomes_nf'
}
'''
        assertNoViolations(SOURCE)
    }


    @Test
    void paramDiffNameInBlockValidS3Uri_hasNoViolations() {
        final SOURCE = '''
params {
    path_to_igenomes_dir = 's3://igenomes_nf'
}
'''
        assertNoViolations(SOURCE)
    }


    @Test
    void paramWithLocalPath_triggersViolations() {
        final SOURCE = '''
params.igenomes_base = '/fdb/igenomes_nf/'
'''
        assertSingleViolation(SOURCE, 2, "params.igenomes_base = '/fdb/igenomes_nf/'", "Expected an AWS S3 iGenomes base location (e.g., s3://ngi-igenomes), but found '/fdb/igenomes_nf/'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
    }


    @Test
    void paramDiffNameWithLocalPath_triggersViolations() {
        final SOURCE = '''
params.igenomes_location = '/some/local/igenomes/'
'''
        assertSingleViolation(SOURCE, 2, "params.igenomes_location = '/some/local/igenomes/'", "Expected an AWS S3 iGenomes base location (e.g., s3://ngi-igenomes), but found '/some/local/igenomes/'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
    }


    @Test
    void paramInBlockWithLocalPath_triggersViolation() {
        final SOURCE = '''
params {
  path_to_igenomes = '/fdb/igenomes_nf/'
}
'''
        assertSingleViolation(SOURCE, 3, "path_to_igenomes = '/fdb/igenomes_nf/'", "Expected an AWS S3 iGenomes base location (e.g., s3://ngi-igenomes), but found '/fdb/igenomes_nf/'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
    }
}
