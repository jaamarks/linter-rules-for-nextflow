package software.amazon.nextflow.rules.config

/*
 * Check the executor specified in nextflow.config to ensure that a
 * cloud-compatible executor is used instead of an HPC-based or local one.
 *
 * @author Jesse Marks
 */

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

class ExecutorRule extends AbstractAstVisitorRule {

    String name = 'ExecutorRule'
    int priority = 1
    Class astVisitorClass = ExecutorAstVisitor
}

class ExecutorAstVisitor extends AbstractAstVisitor {

    def EXECUTORS = [
            'local',
            // HPC
            'slurm',
            'sge',
            'pbs'
    ]

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.leftExpression instanceof VariableExpression) {
            checkExecutorExpression(expression, expression.leftExpression as VariableExpression)
        } else if (expression.leftExpression instanceof PropertyExpression) {
            checkExecutorExpression(expression, expression.leftExpression as PropertyExpression)
        }
        super.visitBinaryExpression(expression)
    }

    private void checkExecutorExpression(final BinaryExpression expression, def leftExpression) {
        String executorName = leftExpression.text
        if (executorName == 'executor' || executorName == 'process.executor') {
            if (expression.rightExpression instanceof ConstantExpression) {
                def constExpression = expression.rightExpression as ConstantExpression
                if (EXECUTORS.contains(constExpression.value)) {
                    addViolation(constExpression, "Expected a cloud-based executor such as 'awsbatch', found '${constExpression.value}'.")
                }
            } else {
                addViolation(expression.rightExpression, "Expected a string literal for executor such as 'awsbatch', found ${expression.rightExpression.text}")
            }
        }
    }
}

