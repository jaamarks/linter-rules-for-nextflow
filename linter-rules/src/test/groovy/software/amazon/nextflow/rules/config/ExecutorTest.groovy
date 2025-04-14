/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.nextflow.rules.config

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

class ExecutorTest extends AbstractRuleTestCase<ExecutorRule>{
    @Override
    protected ExecutorRule createRule() {
        return new ExecutorRule()
    }

    @Test
    void ruleProperties(){
        assert rule.priority == 1
        assert rule.name == 'ExecutorRule'
    }

    @Test
    void configExecutor_NoViolations(){
        final SOURCE = '''
process {
  executor = 'awsbatch'
}
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void configExecutor_LocalViolations(){
        final SOURCE = '''
process {
  executor = 'local'
}
'''
        assertSingleViolation(SOURCE, 3, "executor = 'local'", "Expected a cloud-based executor such as 'awsbatch', found 'local'.")
    }

    @Test
    void configExecutor_HpcSlurmViolation(){
        final SOURCE = '''
process {
  executor = 'slurm'
}
'''
        assertSingleViolation(SOURCE, 3, "executor = 'slurm'", "Expected a cloud-based executor such as 'awsbatch', found 'slurm'.")
    }

    @Test
    void configExecutor_HpcPbsViolation(){
        final SOURCE = '''
process {
  executor = 'pbs'
}
'''
        assertSingleViolation(SOURCE, 3, "executor = 'pbs'", "Expected a cloud-based executor such as 'awsbatch', found 'pbs'.")
    }

    @Test
    void configExecutor_HpcSgeViolation(){
        final SOURCE = '''
process {
  executor = 'sge'
}
'''
        assertSingleViolation(SOURCE, 3, "executor = 'sge'", "Expected a cloud-based executor such as 'awsbatch', found 'sge'.")
    }
}
