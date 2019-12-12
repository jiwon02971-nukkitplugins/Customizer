package iKguana.customizer.Sccript;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Parser {
    ArrayList<LinkedHashMap<String, String>> removed = new ArrayList<>();

    public static Expressions parse(String source) {
        Expressions expressions = new Expressions();
        LinkedHashMap<String, String> savedStrings = new LinkedHashMap<>();
        String nostring = source;
        int strStartIdx = -1;
        for (int i = 0; i < source.length(); i++) {
            if (nostring.charAt(i) == '"') {
                if (strStartIdx == -1)
                    strStartIdx = i;
                else {
                    String key = "STRING_" + savedStrings.size();
                    String data = nostring.substring(strStartIdx, i + 1);
                    nostring.replace(data, key);
                }
            }
        }

        if (isValid(nostring)) {
            int idx = -1;
            int repeat = 0;
            String fnc = "";

            for (int i = 0; i < source.length(); i++) {
                if (i + 4 < source.length()) {
                    String cut = source.substring(i, i + 4);
                    if (cut.equals("func"))
                        idx = i;
                }
                if (idx != -1 && source.charAt(i) == '{') {
                    if (repeat == 0)
                        if (isCorrectArguments(source.substring(idx, i)))
                            fnc = source.substring(idx, i);
                    repeat++;
                }
                if (idx != -1 && source.charAt(i) == '}')
                    if (--repeat == 0)
                        expressions.addFunction(source.substring(idx, i + 1));
            }
        }

        return new Expressions();
    }

    public static boolean isCorrectArguments(String str) {
        if (str.startsWith("func")) {
            String full = str.replace("func", "").trim();
            if (str.indexOf("(") != -1) {
                String name = full.substring(0, full.indexOf("("));
                String args = full.substring(full.indexOf("(")).trim();

                if (args.startsWith("(") && args.endsWith(")"))
                    return true;
            }
        }
        return false;
    }

    private static boolean isValid(String source) {
        String filteredSource = source;
        int sbrackets = 0; // ()
        int bbrackets = 0; // {}
        for (int idx = 0; idx < filteredSource.length(); idx++) {
            if (filteredSource.charAt(idx) == '(') sbrackets++;
            else if (filteredSource.charAt(idx) == ')') sbrackets--;
            else if (filteredSource.charAt(idx) == '{') bbrackets++;
            else if (filteredSource.charAt(idx) == '}') bbrackets--;

            if (sbrackets < 0 || bbrackets < 0) return false;
        }
        if (sbrackets == 0 && bbrackets == 0)
            return true;
        return false;
    }
}
