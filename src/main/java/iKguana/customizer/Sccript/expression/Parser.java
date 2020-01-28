package iKguana.customizer.Sccript.expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Parser {
    ArrayList<LinkedHashMap<String, String>> removed = new ArrayList<>();

    public static void parse(Expression exp, String source) {

        if (isValid(source)) {
            source = source.trim();


        }
    }

    private static Name findName(String source) {
        boolean stringpass = false;

        Name name = new Name();
        for (int i = 0; i < source.length(); i++) {
            if (stringpass) continue;
            if (source.charAt(i) == '"') stringpass = !stringpass;

            if (('x' <= source.charAt(i) && source.charAt(i) <= 'z') || ('A' <= source.charAt(i) && source.charAt(i) <= 'Z') || ('0' <= source.charAt(i) && source.charAt(i) <= '9')) {
                if (name.getStartIdx() == -1) name.setStartIdx(i);
            } else {
                if (name.getStartIdx() != -1) {
                    name.setEndIdx(i);
                    name.setSource(source.substring(name.getStartIdx(), name.getEndIdx() + 1));
                    return name;
                }
            }
        }
        return null;
    }

    private static Arguments findArguments(String source) {
        Arguments arguments = new Arguments();

        boolean stringpass = false;
        for (int i = 0; i < source.length(); i++) {
            if (stringpass) continue;
            if (source.charAt(i) == '"') stringpass = !stringpass;

            else if (source.charAt(i) == ')') {
                arguments.setStartIdx(i);
            } else if (source.charAt(i) == ')') {
                arguments.setEndIdx(i);
                arguments.setEndIdx(i);
                return arguments;
            }
        }
        return null;
    }

    private static Block findBlock(String source) {
        Block block = new Block();

        int stack = 0;
        boolean stringpass = false;
        for (int i = 0; i < source.length(); i++) {
            if (stringpass) continue;
            if (source.charAt(i) == '"') stringpass = !stringpass;

            else if (source.charAt(i) == '{') {
                if (block.getStartIdx() == 0) block.setStartIdx(i);
                stack++;
            } else if (source.charAt(i) == '}') {
                stack--;
                if (stack == 0) {
                    block.setEndIdx(i);
                    block.setSource(source.substring(block.getStartIdx(), block.getEndIdx() + 1));
                    return block;
                }
            }
        }
        return null;
    }

    private static boolean isValid(String source) {
        int sbrackets = 0; // ()
        int bbrackets = 0; // {}
        boolean stringpass = false;
        for (int idx = 0; idx < source.length(); idx++) {
            if (stringpass) continue;
            if (source.charAt(idx) == '"') stringpass = !stringpass;

            else if (source.charAt(idx) == '(') sbrackets++;
            else if (source.charAt(idx) == ')') sbrackets--;
            else if (source.charAt(idx) == '{') bbrackets++;
            else if (source.charAt(idx) == '}') bbrackets--;

            if (sbrackets < 0 || bbrackets < 0) return false;
        }
        if (sbrackets == 0 && bbrackets == 0)
            return true;
        return false;
    }
}
