package org.cbm.ant.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class ConstantStatementTest
{

    @Test
    public void constants()
    {
        assertThat(ConstantStatement.parse("1972").invoke(), equalTo(1972));
        assertThat(ConstantStatement.parse("$a").invoke(), equalTo(0x0a));
        assertThat(ConstantStatement.parse("0x0b").invoke(), equalTo(0x0b));
    }

    @Test
    public void additions()
    {
        assertThat(ConstantStatement.parse("1 + 2").invoke(), equalTo(1 + 2));
        assertThat(ConstantStatement.parse("$f + -$8").invoke(), equalTo(0x0f + -0x08));
    }

    @Test
    public void bracets()
    {
        assertThat(ConstantStatement.parse("2 * (1 + 2)").invoke(), equalTo(2 * (1 + 2)));
        assertThat(ConstantStatement.parse("2 * (1 + 2)").toString(), equalTo("2 * (1 + 2)"));
    }

    @Test
    public void ordering()
    {
        assertThat(ConstantStatement.parse("4 * 3 / 2 * 5").invoke(), equalTo(4 * 3 / 2 * 5));
    }

    @Test
    public void precedence()
    {
        assertThat(ConstantStatement.parse("2 + 3 * 4").invoke(), equalTo(2 + 3 * 4));
        assertThat(ConstantStatement.parse("2 * 3 + 4").invoke(), equalTo(2 * 3 + 4));
    }
}
