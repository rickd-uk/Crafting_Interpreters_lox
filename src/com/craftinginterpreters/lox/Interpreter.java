package com.craftinginterpreters.lox;

public class Interpreter implements Expr.Visitor<Object> {

   @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
       return expr.value;
   }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        return null;
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
        return null;
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

   @Override
   public Object visitGroupingExpr(Expr.Grouping expr) {
       return evaluate(expr.expression);
   }

   private Object evaluate(Expr expr) {
       return expr.accept(this);
   }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
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
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
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
