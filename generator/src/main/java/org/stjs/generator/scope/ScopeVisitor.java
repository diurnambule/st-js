package org.stjs.generator.scope;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import org.stjs.generator.handlers.utils.PreConditions;

/**
 * This class visits the code's tree to determine the scope of each name. The problem is that a f
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ScopeVisitor extends VoidVisitorAdapter<NameScope> {

	@Override
	public void visit(CompilationUnit n, NameScope currentScope) {
		ImportScope scope = new ImportScope(currentScope, n.getPackage() != null ? n.getPackage().getName().getName()
				: null);
		if (n.getImports() != null) {
			for (ImportDeclaration importDecl : n.getImports()) {
				scope.addImport(importDecl.getName().getName(), importDecl.isStatic(), importDecl.isAsterisk());
			}
		}
		super.visit(n, scope);
	}

	@Override
	public void visit(BlockStmt n, NameScope currentScope) {
		super.visit(n, new VariableScope("block-" + n.getBeginLine(), currentScope));
	}

	@Override
	public void visit(VariableDeclarationExpr n, NameScope currentScope) {
		PreConditions.checkState(currentScope instanceof VariableScope, "A VariableScope expected");
		if (n.getVars() != null) {
			VariableScope varScope = (VariableScope) currentScope;
			for (VariableDeclarator var : n.getVars()) {
				varScope.addVariable(var.getId().getName());
			}
		}
		super.visit(n, currentScope);
	}

	@Override
	public void visit(CatchClause n, NameScope currentScope) {
		super.visit(n, new ParameterScope("catch-" + n.getBeginLine(), currentScope, n.getExcept().getId().getName()));
	}

	@Override
	public void visit(MethodDeclaration n, NameScope currentScope) {
		ParameterScope scope = new ParameterScope("param-" + n.getBeginLine(), currentScope);
		if (n.getParameters() != null) {
			for (Parameter p : n.getParameters()) {
				scope.addParameter(p.getId().getName());
			}
		}
		super.visit(n, scope);
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, NameScope currentScope) {
		NameScope classScope = currentScope;
		if (n.getExtends() != null && n.getExtends().size() > 0 && !n.isInterface()) {
			classScope = new ParentTypeScope(classScope, n.getExtends().get(0).getName());
		}

		if (n.getMembers() != null) {
			classScope = visitTypeDeclaration("type-" + n.getName(), n.getMembers(), currentScope);
		}
		super.visit(n, classScope);
	}

	private NameScope visitTypeDeclaration(String name, List<BodyDeclaration> declarations, NameScope currentScope) {
		TypeScope typeScope = new TypeScope(name, currentScope);
		for (BodyDeclaration member : declarations) {
			if (member instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) member;
				for (VariableDeclarator var : field.getVariables()) {
					if (ModifierSet.isStatic(field.getModifiers())) {
						typeScope.addStaticField(var.getId().getName());
					} else {
						typeScope.addInstanceField(var.getId().getName());
					}
				}
			} else if (member instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) member;
				if (ModifierSet.isStatic(method.getModifiers())) {
					typeScope.addStaticMethod(method.getName());
				} else {
					typeScope.addInstanceMethod(method.getName());
				}

			}
		}
		return typeScope;
	}

	@Override
	public void visit(ObjectCreationExpr n, NameScope currentScope) {
		NameScope newScope = currentScope;
		if (n.getAnonymousClassBody() != null) {
			newScope = visitTypeDeclaration("anonymous-" + n.getBeginLine(), n.getAnonymousClassBody(), currentScope);
		}
		super.visit(n, newScope);
	}

	@Override
	public void visit(ConstructorDeclaration n, NameScope currentScope) {
		ParameterScope paramScope = new ParameterScope("constructor-" + n.getBeginLine(), currentScope);
		for (Parameter p : n.getParameters()) {
			paramScope.addParameter(p.getId().getName());
		}
		super.visit(n, paramScope);
	}

	@Override
	public void visit(EnumDeclaration n, NameScope arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(ForeachStmt n, NameScope arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(ForStmt n, NameScope arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodCallExpr n, NameScope arg) {
		// here identify call
		super.visit(n, arg);
	}

	@Override
	public void visit(SwitchStmt n, NameScope arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

}