/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.nextflow.rules.config

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

class ConfigExecutorRule extends AbstractAstVisitorRule {
    String name = 'ConfigExecutorRule'
    int priority = 1
    Class astVisitorClass = ConfigExecutorAstVisitor
}

class ConfigExecutorAstVisitor extends AbstractAstVisitor {

    def HPC_EXECUTORS = [
            'slurm',
            'sge',
            'pbs'
    ]

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.leftExpression instanceof VariableExpression) {
            def varExpression = expression.leftExpression as VariableExpression
            if (varExpression.text == 'executor') {
                if (expression.rightExpression instanceof ConstantExpression) {
                    def constExpression = expression.rightExpression as ConstantExpression
                    if ( HPC_EXECUTORS.contains(constExpression.value)){
                        addViolation(constExpression, "Expected a cloud-based executor such as 'awsbatch', found '${constExpression.value}'.")
                    }
                } else {
                    addViolation(expression.rightExpression, "Expected a string literal for executor such as 'awsbatch', found ${expression.rightExpression.text}")
                }
            }
        }

        super.visitBinaryExpression(expression)
    }
}
