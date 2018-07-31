package me.hugmanrique.craftpatch.util;

import java.lang.reflect.Method;

/**
 * @author Hugo Manrique
 * @since 31/07/2018
 */
public class StatementUtil {
    public static String checkStatement(String statement, char lastChar) {
        statement = statement.trim();

        if (statement.isEmpty() || statement.charAt(statement.length() - 1) != lastChar) {
            throw new IllegalArgumentException("Statement must end with '" + lastChar + "'");
        }

        return statement;
    }

    public static String checkStatement(String statement) {
        return checkStatement(statement, ';');
    }

    public static String checkClosingBrace(String statement) {
        return checkStatement(statement, '}');
    }

    public static String generateMethodInvocation(Method method, String arguments) {
        return method.getDeclaringClass().getName() + "." + method.getName() + "(" + arguments + ");";
    }
}
