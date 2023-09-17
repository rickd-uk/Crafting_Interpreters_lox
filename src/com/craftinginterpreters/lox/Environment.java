package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    // For the global scope environment
    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }
    void define(String name, Object value) {
        values.put(name, value);
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        // If the variable isn’t found in this environment, we simply try the enclosing one.
        //That in turn does the same thing recursively, so this will ultimately walk the
        //entire chain. If we reach an environment with no enclosing one and still don’t
        //find the variable, then we give up and report an error as before.
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined var '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        //Again, if the variable isn’t in this environment, it checks the outer one, recursively.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name,
                "Undefined var '" + name.lexeme + "'.");
    }
}

