package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.Map;
import java.util.logging.Logger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.structurizr.model.Component;

public class CallGraphVisitorBuilder extends GenericVisitorAdapter<CallGraphModel, CallGraphModel> {
	private static final Logger logger =Logger.getLogger(CallGraphVisitorBuilder.class.getName());
	private boolean inMethod;
	@Override
	public CallGraphModel visit(MethodDeclaration n, CallGraphModel model) {
		inMethod = true;
		try {
			return super.visit(n, model);
		} finally {
			inMethod = false;
		}
	}
	
	@Override
	public CallGraphModel visit(MethodCallExpr methodCall, CallGraphModel model) {
		if(inMethod) {
//			logger.info("Found method call "+methodCall);
			ResolvedMethodDeclaration resolvedMethodCall = methodCall.resolve();
			ResolvedReferenceTypeDeclaration declaringType = resolvedMethodCall.declaringType();
			model.addCall(methodCall);
		}
		return super.visit(methodCall, model);
	}
}
