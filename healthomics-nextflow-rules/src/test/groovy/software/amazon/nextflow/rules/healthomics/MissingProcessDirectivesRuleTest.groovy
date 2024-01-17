package software.amazon.nextflow.rules.healthomics

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

class MissingProcessDirectivesRuleTest extends AbstractRuleTestCase<MissingProcessDirectivesRule> {
    @Override
    protected MissingProcessDirectivesRule createRule(){
        new MissingProcessDirectivesRule()
    }

    @Test
    void ruleProperties(){
        assert rule.priority == 2
        assert rule.name == "MissingProcessDirectivesRule"
    }

    @Test
    void noDirectives_allDirectivesMissingViolation(){
        final SOURCE =  'process MY_PROCESS {}'
        assertViolations(SOURCE,
            [line:1, source:SOURCE, message:'MY_PROCESS does not contain a cpus directive which is recommended for reproducibility'],
            [line:1, source:SOURCE, message:'MY_PROCESS does not contain a memory directive which is recommended for reproducibility'],
            [line:1, source:SOURCE, message:'MY_PROCESS does not contain a container directive which is recommended for reproducibility']
        )
    }

    @Test
    void onlyCpu_MemoryAndContainerMissing(){
        final SOURCE =  'process MY_PROCESS {cpus 2}'
        assertViolations(SOURCE,
                [line:1, source:SOURCE, message:'MY_PROCESS does not contain a memory directive which is recommended for reproducibility'],
                [line:1, source:SOURCE, message:'MY_PROCESS does not contain a container directive which is recommended for reproducibility']
        )
    }

    @Test
    void onlyCpuAndMemory_ContainerMissing(){
        final SOURCE =
'''process MY_PROCESS {
        memory '2 GB'
        cpus 2
    }
'''
        assertViolations(SOURCE,
                [line:1, source: 'process MY_PROCESS {', message:'MY_PROCESS does not contain a container directive which is recommended for reproducibility']
        )
    }

    @Test
    void hasAllDirectives_NoViolations(){
        final SOURCE =
'''process MY_PROCESS {
        memory '2 GB'
        cpus 2
        container 'foo:baa'
    }
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void hasLabel_NoViolations(){
        final SOURCE = "process MY_PROCESS {label 'BigMem'}"
        assertNoViolations(SOURCE)
    }

    @Test
    void hasLabelAndDirectives_NoViolations(){
final SOURCE =
'''process MY_PROCESS {
        label 'BigMem'
        memory '2 GB'
        cpus 2
        container 'foo:baa'
    }
'''
        assertNoViolations(SOURCE)
    }
}
