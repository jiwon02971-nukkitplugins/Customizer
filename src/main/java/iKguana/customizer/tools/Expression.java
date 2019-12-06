package iKguana.customizer.tools;

import iKguana.artonline.SubClasses.UsefulFunctions;

import java.util.ArrayList;
import java.util.Arrays;

public class Expression extends UsefulFunctions {
    private static Expression instance;
    ArrayList<ArrayList<String>> orderedTokens;
    ArrayList<String> tokens;

    public Expression() {
        getTokens();
    }

    public void getTokens() {
        orderedTokens = new ArrayList<ArrayList<String>>();

        orderedTokens.add(new ArrayList<String>(Arrays.asList(new String[]{"*", "/"})));
        orderedTokens.add(new ArrayList<String>(Arrays.asList(new String[]{"+", "-"})));
        orderedTokens.add(new ArrayList<String>(Arrays.asList(new String[]{"==", "!="})));

        tokens = new ArrayList<String>();
        for (ArrayList<String> tks : orderedTokens)
            tokens.addAll(tks);
    }


    public String calculate(String exp) {
        int startIdx = -1;
        for (int idx = 0; idx < exp.length(); idx++) {
            if (exp.charAt(idx) == '(') {
                startIdx = idx;
            } else if (exp.charAt(idx) == ')') {
                exp = exp.substring(0, startIdx) + cal(exp.substring(startIdx + 1, idx)) + exp.substring(idx + 1);
            }
        }
        String result = cal(exp);

        return result.endsWith(".0") ? result.substring(0, result.indexOf(".0")) : result;
    }

    private String cal(String exp) {
        ArrayList<String> exps = new ArrayList<String>();

        for (int i = 0; i < exp.length(); i++) {

            for (int t = 0; t < tokens.size(); t++) {
                String token = tokens.get(t);

                if (exp.length() > i + token.length()) {
                    if (exp.substring(i, i + token.length()).equals(token)) {
                        exps.add(exp.substring(0, i));
                        exps.add(token);
                        exp = exp.substring(i + token.length());
                        i = 0;
                    }
                } else {
                    if (t == tokens.size() - 1) {
                        exps.add(exp);
                    } else {
                        continue;
                    }
                }
            }
        }

        for (int idx = 0; idx < exps.size(); idx++) {
            for (ArrayList<String> tks : orderedTokens)
                for (String token : tks) {
                    if (exps.get(idx).equals(token)) {
                        String var1 = exps.get(idx - 1).trim();
                        String var2 = exps.get(idx + 1).trim();
                        String result = "err";

                        switch (token) {
                            case "*":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) * Double.parseDouble(var2));
                                break;
                            case "/":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) / Double.parseDouble(var2));
                                break;
                            case "+":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) + Double.parseDouble(var2));
                                break;
                            case "-":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) - Double.parseDouble(var2));
                                break;
                            case "==":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) == Double.parseDouble(var2));
                                else
                                    result = String.valueOf(var1.equals(var2));
                                break;
                            case "!=":
                                if (isNumber(var1, var2))
                                    result = String.valueOf(Double.parseDouble(var1) != Double.parseDouble(var2));
                                else
                                    result = String.valueOf(var1.equals(var2));
                                break;
                        }
                        if (result.equals("err"))
                            return "NULL";

                        exps.set(idx, result);
                        exps.remove(idx - 1);
                        exps.remove(idx);
                        idx = 0;
                    }
                }
        }
        return exps.get(0);
    }


    public static Expression getIt() {
        if (instance == null)
            instance = new Expression();
        return instance;
    }
}
