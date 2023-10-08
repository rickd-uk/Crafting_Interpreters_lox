package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public abstract class  Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();

  Resolver(Interpreter interpreter) {
      this.interpreter = interpreter;
  }

  void resolve(List<Stmt> statements) {
      for (Stmt statement : statements) {
          resolve(statement);
      }
  }

  private void resolve(Stmt stmt) {
      stmt.accept(this);
  }

  private void resolve(Expr expr) {
      expr.accept(this);
  }

  private void beginScope() {
      scopes.push(new HashMap<String, Boolean>());
  }

  private void endScope() {
      scopes.pop();
  }

  @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolver(stmt.statements);
        endScope();
        return null;
  }
}
