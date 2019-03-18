//package com.compiler.interpreter;
//
//import com.compiler.parser.Expression;
//
//class Interpreter implements Expression.Visitor<Object> {
//    @Override
//    public Object visitAssignExpr(Expression.Assign expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitSetExpr(Expression.Set expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitBinaryExpr(Expression.Binary expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitCallExpr(Expression.Call expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitGetExpr(Expression.Get expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitSubscriptionExpr(Expression.Subscription expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitPropertyRefExpr(Expression.PropertyRef expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitGroupingExpr(Expression.Grouping expr) {
//        return evaluate(expr.expression);
//    }
//
//    @Override
//    public Object visitLiteralExpr(Expression.Literal expr) {
//        return expr.value;
//    }
//
//    @Override
//    public Object visitLogicalExpr(Expression.Logical expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitUnaryExpr(Expression.Unary expr) {
//        return null;
//    }
//
//    @Override
//    public Object visitVariableExpr(Expression.Variable expr) {
//        return null;
//    }
//}
