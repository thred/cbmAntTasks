package org.cbm.ant.oscar64;

public class Define
{
    private String symbol;
    private String value;

    public Define()
    {
        super();
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.format("Define [symbol=%s, value=%s]", symbol, value);
    }
}
