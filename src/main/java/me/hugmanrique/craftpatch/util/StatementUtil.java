package me.hugmanrique.craftpatch.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

    private static int getParameterCount(String arguments) {
        if (arguments.trim().isEmpty()) {
            return 0;
        }

        int commas = 0;

        for (int i = 0; i < arguments.length(); i++) {
            if (arguments.charAt(i) == ',') {
                commas++;
            }
        }

        return commas + 1;
    }

    private static void checkCorrectParameterCount(Method method, String parameters) {
        int parameterCount = method.getParameterCount();
        int actualCount = getParameterCount(parameters);

        if (parameterCount != actualCount) {
            throw new IllegalArgumentException("Illegal number of method call parameters, expected " + parameterCount + ", got " + actualCount + " instead");
        }
    }

    public static String generateMethodInvocation(Method method, String parameters) {
        int modifiers = method.getModifiers();

        if (!Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("Prepended or appended method must be static");
        }

        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Prepended or appended method must be public");
        }

        checkCorrectParameterCount(method, parameters);

        return method.getDeclaringClass().getName() + "." + method.getName() + "(" + parameters + ");";
    }
}
