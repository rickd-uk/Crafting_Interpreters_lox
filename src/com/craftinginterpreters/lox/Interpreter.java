package com.craftinginterpreters.lox;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Environment environment = new Environment();

   @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
       return expr.value;
   }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        // We look at its value to see if we can short-circuit. If not, and only then, do we evaluate the
        //  right operand
       Object left = evaluate(expr.left);

       if (expr.operator.type == TokenType.OR) {
           if (isTruthy(left)) return left;
       } else {
           if (!isTruthy(left)) return left;
       }
        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        return null;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        return null;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return null;
    }

    void interpret(List<Stmt> statements) {
       try {
          for (Stmt statement : statements) {
              execute(statement);
          }
       } catch (RuntimeError error) {
            Lox.runtimeError(error);
       }
    }



    @Override
   public Object visitUnaryExpr(Expr.Unary expr) {
       Object right = evaluate(expr.right);

        switch (expr.operator.type) {
           case MINUS:
               checkNumberOperand(expr.operator, right);
                   return -(double) right;
           case BANG:
               return !isTruthy(right);
       };
       return null;
   }

   @Override
   public Object visitVariableExpr(Expr.Variable expr) {
       return environment.get(expr.name);
   }


    @Override
    public Object visitBinary(Expr.Binary expr) {
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
       if (operand instanceof Double) return;
       throw new RuntimeError(operator, "Operand must be a number.");
   }

   private void checkNumberOperands(Token operator, Object left, Object right) {
       if (left instanceof Double && right instanceof Double) return;

       throw new RuntimeError(operator, "Operands must be numbers.");
   }

   private Boolean isTruthy(Object object) {
       if (object == null) return false;
       if (object instanceof Boolean) return (boolean)object;
       return true;
   }

   private Boolean isEqual(Object a, Object b) {
       if (a == null && b == null) return true;
       if (a == null) return false;

       return a.equals(b);
   }

   private String stringify(Object object) {
       if (object == null) return "nil";

       if (object instanceof Double) {
           String text = object.toString();
           if (text.endsWith(".0")) {
               text = text.substring(0, text.length() - 2);
           }
           return text;
       }
       return object.toString();
   }

   @Override
   public Object visitGroupingExpr(Expr.Grouping expr) {
       return evaluate(expr.expression);
   }

   private Object evaluate(Expr expr) {
       return expr.accept(this);
   }

   private void execute(Stmt stmt) {
       stmt.accept(this);
   }

   // This new method executes a list of statements in the context of a given
   //environment.
   void executeBlock(List<Stmt> statements, Environment environment) {
       Environment previous = this.environment;
       try {
           this.environment = environment;

           for (Stmt statement : statements) {
               execute(statement);
           }
           // As is always good practice in Java, it restores the previous environment
           //using a finally clause. That way it gets restored even if an exception is thrown.
       } finally {
           this.environment = previous;
       }
   }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment((environment)));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        return null;
    }

    @Override
   public Void visitExpressionStmt(Stmt.Expression stmt) {
       evaluate(stmt.expression);
       return null;
   }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        return null;
    }

    // The interpreter implementation is a thin wrapper around the self-same Java
    //code. It evaluates the condition. If truthy, it executes the then branch.
    //Otherwise, if there is an else branch, it executes that.
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate((stmt.condition)))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
   public Void visitPrintStmt(Stmt.Print stmt) {
       Object value = evaluate(stmt.expression);
       System.out.println(stringify(value));
       return null;
   }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
       Object value = null;
       if (stmt.initializer != null) {
           value = evaluate(stmt.initializer);
       }
         environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
       Object value = evaluate(expr.value);
       environment.assign(expr.name, value);
       return value;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
       while (isTruthy(evaluate(stmt.condition))) {
           execute(stmt.body);
       }
        return null;
    }


    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
       Object left = evaluate(expr.left);
       Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            }
            case MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            }
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
            case PLUS -> {


                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }

                //throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                if (right.equals(0)) {
                    throw new RuntimeError(expr.operator, "Cannot divide by 0!");
                }
                return (double) left / (double) right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            }
        }
       return null;
   }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        return null;
    }
}
