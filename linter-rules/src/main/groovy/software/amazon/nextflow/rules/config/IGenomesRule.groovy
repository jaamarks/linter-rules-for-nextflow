package software.amazon.nextflow.rules.config

/*
 * Ensure that `igenomes_base` references cloud location rather than a local one
 * on Biowulf.
 *
 * @author Jesse Marks
 */

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

class IGenomesRule extends AbstractAstVisitorRule {

    String name = 'IGenomesRule'
    int priority = 1
    Class astVisitorClass = IGenomesAstVisitor
}

class IGenomesAstVisitor extends AbstractAstVisitor {

    final String IGENOMES_PARAM_PATTERN = ".*igenome.*"
    final String IGENOMES_S3 = "s3://.*"

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.leftExpression instanceof VariableExpression) {
            checkExpression(expression, expression.leftExpression as VariableExpression)
        } else if (expression.leftExpression instanceof PropertyExpression) {
            checkExpression(expression, expression.leftExpression as PropertyExpression)
        }
        super.visitBinaryExpression(expression)
    }

    private void checkExpression(final BinaryExpression expression, def leftExpression) {
        String iGenomesParamName = leftExpression.text
        if (iGenomesParamName.matches(IGENOMES_PARAM_PATTERN)) {
            if (expression.rightExpression instanceof ConstantExpression) {
                def constExpression = expression.rightExpression as ConstantExpression
                if (!constExpression.value.matches(IGENOMES_S3)) {
                    addViolation(constExpression, "Expected an AWS S3 iGenomes base location (e.g., s3://ngi-igenomes), but found '${constExpression.value}'. Refer to https://ewels.github.io/AWS-iGenomes/ for a web tool to help locate the correct iGenomes references.")
                }
            } else {
                addViolation(expression.rightExpression, "Expected a string literal for iGenomes location, found ${expression.rightExpression.text}")
            }
        }
    }
}

