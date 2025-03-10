package software.amazon.nextflow.rules.biowulf

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

/**
 * Checks that the URI of a container matches the ECR pattern
 *
 * @author Jesse Marks
 */
class ContainerUriRule extends AbstractAstVisitorRule {

    String name = 'ContainerUriIsEcrUriJesse'
    int priority = 1
    Class astVisitorClass = ContainerUriAstVisitor
}

class ContainerUriAstVisitor extends AbstractAstVisitor {

    final String ECR_URI_PATTERN = "\\d{12}\\.dkr\\.ecr\\..+\\.amazonaws.com-jesse/.*"

    @Override
    void visitMethodCallExpression(MethodCallExpression expression){
        if (expression.getMethod().getText() == "container") {

            def methodArguments = AstUtil.getMethodArguments(expression)

            // check that it is an ECR-jesse URI
            if (methodArguments.size() == 1) {
                final argExpression = methodArguments.get(0)
                final arg = methodArguments.get(0).getText()

                if (AstUtil.isConstantOrLiteral(argExpression) && !arg.matches(ECR_URI_PATTERN)) {

                    addViolation(expression, "The container image URI '$arg' does not match the pattern '$ECR_URI_PATTERN'. Replace with an ECR image URI.")
                }
            }
        }


        super.visitMethodCallExpression(expression)
    }
}
