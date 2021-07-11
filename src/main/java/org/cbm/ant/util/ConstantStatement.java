package org.cbm.ant.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Grammar: statement = expression { operator expression }.<br>
 * expression = [unaryOperator] ( "(" statement ")" | INT ).<br>
 * operator = "+" | "-" | "*" | "/" | "&" | "|".<br>
 * unaryOperator = "-" | "~".<br>
 */
public abstract class ConstantStatement
{

    private static final Map<String, Operator> OPERATORS = new HashMap<>();

    static
    {
        OPERATORS.put("*", new Operator("*", (a, b) -> a.invoke() * b.invoke(), 3));
        OPERATORS.put("/", new Operator("/", (a, b) -> a.invoke() / b.invoke(), 3));
        OPERATORS.put("+", new Operator("+", (a, b) -> a.invoke() + b.invoke(), 4));
        OPERATORS.put("-", new Operator("-", (a, b) -> a.invoke() - b.invoke(), 4));
        OPERATORS.put("&", new Operator("&", (a, b) -> a.invoke() & b.invoke(), 8));
        OPERATORS.put("|", new Operator("|", (a, b) -> a.invoke() | b.invoke(), 10));
    }

    private static class Operator
    {
        private final String symbol;
        private final BiFunction<ConstantStatement, ConstantStatement, Integer> fn;
        private final int precendence;

        public Operator(String symbol, BiFunction<ConstantStatement, ConstantStatement, Integer> fn, int precendence)
        {
            super();

            this.symbol = symbol;
            this.fn = fn;
            this.precendence = precendence;
        }

        public String getSymbol()
        {
            return symbol;
        }

        public BiFunction<ConstantStatement, ConstantStatement, Integer> getFn()
        {
            return fn;
        }

        public int getPrecendence()
        {
            return precendence;
        }

    }

    private static class Tokenizer extends StringTokenizer
    {
        private String token = null;

        public Tokenizer(String source)
        {
            super(source, "()-~" + OPERATORS.keySet().stream().collect(Collectors.joining()), true);
        }

        public String get()
        {
            return token;
        }

        public String next()
        {
            do
            {
                if (!hasMoreTokens())
                {
                    token = null;
                }
                else
                {
                    token = nextToken().trim();
                }
            } while (token != null && token.length() == 0);

            return token;
        }
    }

    public static ConstantStatement constant(int value)
    {
        return new ConstantStatement()
        {
            @Override
            public int invoke()
            {
                return value;
            }

            @Override
            public String format(Function<Integer, String> formatter)
            {
                return formatter.apply(value);
            }
        };
    }

    public static ConstantStatement grouped(ConstantStatement statement)
    {
        return new ConstantStatement()
        {
            @Override
            public int invoke()
            {
                return statement.invoke();
            }

            @Override
            public String format(Function<Integer, String> formatter)
            {
                return "(" + statement.format(formatter) + ")";
            }
        };
    }

    public static ConstantStatement operation(Operator operator, ConstantStatement a, ConstantStatement b)
    {
        return new ConstantStatement()
        {
            @Override
            public int invoke()
            {
                return operator.getFn().apply(a, b);
            }

            @Override
            public String format(Function<Integer, String> formatter)
            {
                return a.format(formatter) + " " + operator.getSymbol() + " " + b.format(formatter);
            }
        };
    }

    public static int evaluate(String source)
    {
        return parse(source).invoke();
    }

    public static ConstantStatement parse(String source)
    {
        Tokenizer tokenizer = new Tokenizer(source);
        String token = tokenizer.next();

        if (token == null)
        {
            throw new IllegalArgumentException("Source missing.");
        }

        try
        {
            ConstantStatement statement = parseStatement(tokenizer, Integer.MAX_VALUE);

            if (tokenizer.get() != null)
            {
                throw new IllegalArgumentException("Dangling characters.");
            }

            return statement;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Failed to parse: " + source, e);
        }
    }

    private static ConstantStatement parseStatement(Tokenizer tokenizer, int precedence)
    {
        ConstantStatement statement = parseExpression(tokenizer);

        while (true)
        {
            String token = tokenizer.get();

            if (token == null)
            {
                break;
            }

            Operator operator = OPERATORS.get(token);

            if (operator == null || operator.getPrecendence() >= precedence)
            {
                break;
            }

            token = tokenizer.next();

            if (token == null)
            {
                throw new IllegalArgumentException("Dangling operator.");
            }

            statement = operation(operator, statement, parseStatement(tokenizer, operator.getPrecendence()));
        }

        return statement;
    }

    private static ConstantStatement parseExpression(Tokenizer tokenizer)
    {
        ConstantStatement statement;
        String token = tokenizer.get();
        String unaryOperator = null;

        if ("-".equals(token) || "~".equals(token))
        {
            unaryOperator = token;
            token = tokenizer.next();

            if (token == null)
            {
                throw new IllegalArgumentException("Dangling unary operator.");
            }

        }

        if ("(".equals(token))
        {
            token = tokenizer.next();
            statement = grouped(parseStatement(tokenizer, Integer.MAX_VALUE));

            token = tokenizer.get();

            if (token == null || !")".equals(token))
            {
                throw new IllegalArgumentException("Unclosed bracket.");
            }

            token = tokenizer.next();
        }
        else
        {
            if (token.startsWith("$"))
            {
                token = "0x" + token.substring(1);
            }

            try
            {
                statement = constant(Integer.decode(token));
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Failed to parse constant: " + token, e);
            }

            token = tokenizer.next();
        }

        if ("-".equals(unaryOperator))
        {
            return new ConstantStatement()
            {
                @Override
                public int invoke()
                {
                    return -statement.invoke();
                }

                @Override
                public String format(Function<Integer, String> formatter)
                {
                    return "-" + statement;
                }
            };
        }
        else if ("~".equals(unaryOperator))
        {
            return new ConstantStatement()
            {

                @Override
                public int invoke()
                {
                    return ~statement.invoke();
                }

                @Override
                public String format(Function<Integer, String> formatter)
                {
                    return "~" + statement.format(formatter);
                }

            };
        }

        return statement;
    }

    public abstract int invoke();

    public abstract String format(Function<Integer, String> formatter);

    public String toHexString()
    {
        return format(Integer::toHexString);
    }

    public String toDecimalString()
    {
        return format(String::valueOf);
    }

    @Override
    public String toString()
    {
        return toDecimalString();
    }
}
