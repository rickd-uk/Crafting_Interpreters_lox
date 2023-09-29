package com.craftinginterpreters.lox;

import java.util.List;

// Once we’ve got the callee and the arguments ready, all that remains is to
//perform the call. We do that by casting the callee to a LoxCallable and then
//invoking a call() method on it. The Java representation of any Lox object
//that can be called like a function will implement this interface. That includes
//user-defined functions, naturally, but also class objects since classes are “called”
//to construct new instances.

interface LoxCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}


