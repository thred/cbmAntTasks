package org.cbm.ant.util;

import org.junit.Assert;
import org.junit.Test;

public class ConstantStatementTest
{

    @Test
    public void constants()
    {
        Assert.assertEquals(1, ConstantStatement.parse("1").invoke());
        Assert.assertEquals(0x0a, ConstantStatement.parse("$a").invoke());
        Assert.assertEquals(0x0b, ConstantStatement.parse("0x0b").invoke());
    }

    @Test
    public void additions()
    {
        Assert.assertEquals(1 + 2, ConstantStatement.parse("1 + 2").invoke());
        Assert.assertEquals(0x0f + -0x08, ConstantStatement.parse("$f + -$8").invoke());
    }

    @Test
    public void bracets()
    {
        Assert.assertEquals(2 * (1 + 2), ConstantStatement.parse("2 * (1 + 2)").invoke());
        Assert.assertEquals("2 * (1 + 2)", ConstantStatement.parse("2 * (1 + 2)").toString());
    }

    @Test
    public void ordering()
    {
        Assert.assertEquals(4 * 3 / 2 * 5, ConstantStatement.parse("4 * 3 / 2 * 5").invoke());
    }

    @Test
    public void precedence()
    {
        Assert.assertEquals(2 + 3 * 4, ConstantStatement.parse("2 + 3 * 4").invoke());
        Assert.assertEquals(2 * 3 + 4, ConstantStatement.parse("2 * 3 + 4").invoke());
    }
}
