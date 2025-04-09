package software.amazon.nextflow.rules.config

/*
 * Check the executor specified in nextflow.config to ensure that a
 * cloud-compatible executor is used instead of an HPC-based one.
 *
 * @author Jesse Marks
 */

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
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
            checkVariableExpression(expression)
        } else if (expression.leftExpression instanceof PropertyExpression) {
            checkPropertyExpression(expression)
        }
        super.visitBinaryExpression(expression)
    }

    private checkVariableExpression(final BinaryExpression expression) {

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

    private checkPropertyExpression(final BinaryExpression expression) {

        def propExpression = expression.leftExpression as PropertyExpression
        if (propExpression.text == 'process.executor') {
            if (expression.rightExpression instanceof ConstantExpression) {
                def constExpression = expression.rightExpression as ConstantExpression
                if ( HPC_EXECUTORS.contains(constExpression.value) ) {
                    addViolation(constExpression, "Expected a cloud-based executor such as 'awsbatch', found '${constExpression.value}'.")
                }
            } else {
                addViolation(expression.rightExpression, "Expected a string literal for executor such as 'awsbatch', found ${expression.rightExpression.text}")
            }
        }
    }
}
