package org.cbm.ant.util;

import org.cbm.ant.util.Diff.Results;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class DiffTest
{

    @Test
    public void test()
    {
        String a = "a test this is";
        String b = "this is a test";

        Results<String> results =
            Diff.diff(Diff.wordsOf(a), Diff.wordsOf(b), Diff.ignoreWhitespace(), Diff.byEqualityIgnoringCase());

        MatcherAssert
            .assertThat(results.toString(), Matchers
                .is("0: < a\n" //
                    + "2: < test\n"
                    + "4:   this\n"
                    + "6:   is\n"
                    + "7: > a\n"
                    + "7: > test"));
    }
}