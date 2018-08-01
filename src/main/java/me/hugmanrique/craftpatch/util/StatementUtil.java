package me.hugmanrique.craftpatch.util;

import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.MethodCall;

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

    private static int getParameterCount(String parameters) {
        if (parameters.trim().isEmpty()) {
            return 0;
        }

        int commas = 0;

        for (int i = 0; i < parameters.length(); i++) {
            if (parameters.charAt(i) == ',') {
                commas++;
            }
        }

        return commas + 1;
    }

    private static void checkCorrectParameterCount(int expected, int actual) {
        if (expected != actual) {
            throw new IllegalArgumentException("Illegal number of method call parameters, expected " + expected + ", got " + actual + " instead");
        }
    }

    private static void checkCorrectParameterCount(Method method, String parameters) {
        if (method.isVarArgs()) {
            return;
        }

        int parameterCount = method.getParameterCount();
        int actualCount = getParameterCount(parameters);

        checkCorrectParameterCount(parameterCount, actualCount);
    }

    public static void checkParameters(MethodCall call, String parameters) {
        if (parameters == null) {
            return;
        }

        int parameterCount;

        try {
            CtMethod method = call.getMethod();
            int modifiers = method.getModifiers();

            // Method has varargs, we cannot validate
            if ((modifiers & Modifier.VARARGS) != 0) {
                return;
            }

            parameterCount = method.getParameterTypes().length;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        int actualCount = getParameterCount(parameters);
        checkCorrectParameterCount(parameterCount, actualCount);
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
