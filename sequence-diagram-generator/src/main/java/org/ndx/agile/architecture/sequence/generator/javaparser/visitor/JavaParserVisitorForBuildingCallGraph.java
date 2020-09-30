package org.ndx.agile.architecture.sequence.generator.javaparser.visitor;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CallGraphModel;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.structurizr.model.Component;

public class JavaParserVisitorForBuildingCallGraph extends GenericVisitorAdapter<CallGraphModel, CallGraphModel> {
	private static final Logger logger =Logger.getLogger(JavaParserVisitorForBuildingCallGraph.class.getName());
	private boolean inMethod;
	public JavaParserVisitorForBuildingCallGraph(CallGraphModel model) {
	}

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
			try {
				model.addCall(methodCall);
			} catch(RuntimeException e) {
				// The fact that JavaParser throws RuntimeException really stinks, but well, we have to use their exceptions
				logger.log(Level.SEVERE, String.format("Unable to resolve call %s", methodCall), e);
			}
		}
		return super.visit(methodCall, model);
	}
}