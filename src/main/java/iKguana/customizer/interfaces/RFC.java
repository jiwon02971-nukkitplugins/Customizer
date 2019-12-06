package iKguana.customizer.interfaces;

import java.util.ArrayList;

import iKguana.artonline.SubClasses.UsefulFunctions;
import iKguana.customizer.tools.Expression;

public abstract class RFC extends UsefulFunctions {
    protected static String replaceAll(String str, String[] args) {
        for (int i = 0; i < args.length; i++)
            str = str.replace("%" + (i + 1), args[i]);
        return str;
    }

    protected static <T> String ALtoString(ArrayList<T> al) {
        return ALtoString(al, ", ");
    }

    protected static <T> String ALtoString(ArrayList<T> al, String join) {
        String str = "";
        for (T t : al)
            str += t + join;
        if (al.size() > 0)
            str = str.substring(0, str.lastIndexOf(join));
        return str;
    }

    protected static boolean runStatements(String syntax) {
        return Expression.getIt().calculate(syntax).equals("true");
    }

    protected static String calculate(String syntax) {
        return Expression.getIt().calculate(syntax);
    }

}
