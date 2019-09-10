package com.payneteasy.http.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogMessageFormatter {

    public String format(String aMessage, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Date());
        sb.append(" ");
        sb.append(aMessage);
        sb.append(' ');

        if(args != null) {
            sb.append("[ ");
            for (Object arg : args) {
                sb.append(arg).append(' ');
            }
            sb.append(" ]");
        }
        return sb.toString();
    }

    public String formatException(String aMessage, Exception aException) {
        StringWriter out    = new StringWriter();
        PrintWriter  writer = new PrintWriter(out);
        aException.printStackTrace(writer);
        writer.flush();

        return aMessage +
                "\n" +
                out.toString();
    }
}
