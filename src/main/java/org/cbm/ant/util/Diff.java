package org.cbm.ant.util;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Performs a diff operation analog to the Unix diff
 */
public final class Diff
{
    private static final Pattern WORDS_PATTERN =
        Pattern.compile("(([^\\s]+|\\s))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Provider for items and the global item count
     *
     * @param <Any> the type of item
     */
    public interface ItemProvider<Any>
    {

        Any get(int index);

        int size();

    }

    /**
     * Consumes differences
     *
     * @param <Any> the type of item
     */
    public interface Consumer<Any>
    {

        void both(int leftIndex, int rightIndex, Any leftItem, Any rightItem);

        void onlyLeft(int leftIndex, int rightIndex, Any leftItem);

        void onlyRight(int leftIndex, int rightIndex, Any rightItem);

    }

    /**
     * The type of a {@link Result}
     */
    public enum Type
    {
        BOTH,
        ONLY_LEFT,
        ONLY_RIGHT
    }

    /**
     * Holds one result of the diff operation
     *
     * @param <Any> the type of item
     */
    public static class Result<Any>
    {
        private final Type type;
        private final int leftIndex;
        private final int rightIndex;
        private final Any leftItem;
        private final Any rightItem;

        public Result(Type type, int leftIndex, int rightIndex, Any leftItem, Any rightItem)
        {
            super();
            this.type = type;
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
            this.leftItem = leftItem;
            this.rightItem = rightItem;
        }

        public Type getType()
        {
            return type;
        }

        public int getLeftIndex()
        {
            return leftIndex;
        }

        public int getRightIndex()
        {
            return rightIndex;
        }

        public Any getLeftItem()
        {
            return leftItem;
        }

        public Any getRightItem()
        {
            return rightItem;
        }

        @Override
        public String toString()
        {
            switch (type)
            {
                case BOTH:
                    return String.format("%d:   %s", leftIndex, leftItem);

                case ONLY_LEFT:
                    return String.format("%d: %s %s", leftIndex, "<", leftItem);

                case ONLY_RIGHT:
                    return String.format("%d: %s %s", leftIndex, ">", rightItem);

                default:
                    throw new UnsupportedOperationException("Invalid type: " + type);
            }
        }

    }

    /**
     * A consumer that fills a list with items
     *
     * @param <Any> the type of item
     */
    public static class Results<Any> implements Consumer<Any>, Iterable<Result<Any>>
    {
        private final List<Result<Any>> list;

        public Results()
        {
            this(new ArrayList<>());
        }

        public Results(List<Result<Any>> list)
        {
            super();

            this.list = Objects.requireNonNull(list, "List is null");
        }

        @Override
        public void both(int leftIndex, int rightIndex, Any leftItem, Any rightItem)
        {
            list.add(new Result<>(Type.BOTH, leftIndex, rightIndex, leftItem, rightItem));
        }

        @Override
        public void onlyLeft(int leftIndex, int rightIndex, Any leftItem)
        {
            list.add(new Result<>(Type.ONLY_LEFT, leftIndex, rightIndex, leftItem, null));
        }

        @Override
        public void onlyRight(int leftIndex, int rightIndex, Any rightItem)
        {
            list.add(new Result<>(Type.ONLY_RIGHT, leftIndex, rightIndex, null, rightItem));
        }

        public boolean isEmpty()
        {
            return list.isEmpty();
        }

        @Override
        public Iterator<Result<Any>> iterator()
        {
            return list.iterator();
        }

        public Results<Any> stripUnmodified(int keepDistanceCount)
        {
            List<Result<Any>> results;

            if (keepDistanceCount <= 0)
            {
                results = list.stream().filter(result -> result.type != Diff.Type.BOTH).collect(Collectors.toList());
            }
            else
            {
                results = new ArrayList<>();
                int[] distances = new int[this.list.size()];
                int distance = Integer.MAX_VALUE;

                for (int i = list.size() - 1; i >= 0; i--)
                {
                    Result<Any> result = list.get(i);

                    if (result.type != Diff.Type.BOTH)
                    {
                        distance = 0;
                    }
                    else if (distance < Integer.MAX_VALUE)
                    {
                        distance++;
                    }

                    distances[i] = distance;
                }

                distance = Integer.MAX_VALUE;

                for (int i = 0; i < list.size(); i++)
                {
                    Result<Any> result = list.get(i);

                    if (result.type != Diff.Type.BOTH)
                    {
                        distance = 0;
                    }
                    else if (distance < Integer.MAX_VALUE)
                    {
                        distance++;
                    }

                    if (Math.min(distances[i], distance) <= keepDistanceCount)
                    {
                        results.add(result);
                    }
                }
            }

            return new Results<>(results);
        }

        public void assertEmpty()
        {
            assertEmpty("Diff is not empty");
        }

        public void assertEmpty(String message)
        {
            Results<Any> modifiedDiff = stripUnmodified(1);

            if (!modifiedDiff.isEmpty())
            {
                String diff = modifiedDiff.toString();

                if (diff.length() > 1024)
                {
                    diff = diff.substring(0, 1023) + "…";
                }

                throw new AssertionError(message + "\n" + diff);
            }
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (Result<Any> result : list)
            {
                if (builder.length() > 0)
                {
                    builder.append("\n");
                }

                builder.append(result.toString());
            }

            return builder.toString();
        }

    }

    public static <Any> ItemProvider<Any> of(Any[] array)
    {
        return new ItemProvider<Any>()
        {
            @Override
            public Any get(int index)
            {
                return array[index];
            }

            @Override
            public int size()
            {
                return array.length;
            }
        };
    }

    public static <Any> ItemProvider<Any> ofNone(Class<Any> type)
    {
        return new ItemProvider<Any>()
        {
            @Override
            public Any get(int index)
            {
                return null;
            }

            @Override
            public int size()
            {
                return 0;
            }
        };
    }

    public static <Any> ItemProvider<Any> of(Collection<Any> collection)
    {
        List<Any> list = collection instanceof List ? (List<Any>) collection : new ArrayList<>(collection);

        return new ItemProvider<Any>()
        {
            @Override
            public Any get(int index)
            {
                return list.get(index);
            }

            @Override
            public int size()
            {
                return list.size();
            }
        };
    }

    public static ItemProvider<String> wordsOf(String s)
    {
        if (s == null)
        {
            return ofNone(String.class);
        }

        List<String> words = new ArrayList<>();
        Matcher matcher = WORDS_PATTERN.matcher(s);
        int index = 0;

        while (matcher.find(index))
        {
            words.add(s.substring(matcher.start(), matcher.end()));

            index = matcher.end();
        }

        return of(words);
    }

    public static ItemProvider<String> linesOf(char[] chars)
    {
        if (chars == null)
        {
            return ofNone(String.class);
        }

        try (Reader reader = new BufferedReader(new CharArrayReader(chars)))
        {
            return linesOf(reader);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Failed to read string", e);
        }
    }

    public static ItemProvider<String> linesOf(String s)
    {
        if (s == null)
        {
            return ofNone(String.class);
        }

        try (Reader reader = new BufferedReader(new StringReader(s)))
        {
            return linesOf(reader);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Failed to read string", e);
        }
    }

    public static ItemProvider<String> linesOf(Reader reader) throws IOException
    {
        List<String> lines = new ArrayList<>();

        if (reader != null)
        {
            BufferedReader bufferedReader =
                reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                lines.add(line);
            }
        }

        return of(lines);
    }

    public static ItemProvider<String> linesOf(File file) throws IOException
    {
        try (FileReader reader = new FileReader(file))
        {
            return linesOf(reader);
        }
    }

    public static <Any> Predicate<Any> ignoreNone()
    {
        return item -> false;
    }

    public static Predicate<String> ignoreWhitespace()
    {
        return s -> s == null || s.isBlank();
    }

    public static <Any> BiPredicate<Any, Any> byIdentity()
    {
        return (leftItem, rightItem) -> leftItem == rightItem;
    }

    public static <Any> BiPredicate<Any, Any> byEquality()
    {
        return Objects::equals;
    }

    public static BiPredicate<String, String> byEqualityIgnoringCase()
    {
        return (left, right) -> left == right || left != null && left.equalsIgnoreCase(right);
    }

    public static BiPredicate<String, String> byEqualityIgnoringWhitespace()
    {
        return (left, right) -> left == right || left != null && right != null && left.trim().equals(right.trim());
    }

    public static BiPredicate<String, String> byEqualityIgnoringWhitespaceAndCase()
    {
        return (left, right) -> left == right
            || left != null && right != null && left.trim().equalsIgnoreCase(right.trim());
    }

    public static <Any> Results<Any> diff(ItemProvider<Any> leftItems, ItemProvider<Any> rightItems)
    {
        return diff(leftItems, rightItems, null, null);
    }

    public static <Any> Results<Any> diff(ItemProvider<Any> leftItems, ItemProvider<Any> rightItems,
        BiPredicate<Any, Any> isMatching)
    {
        return diff(leftItems, rightItems, null, isMatching);
    }

    public static <Any> Results<Any> diff(ItemProvider<Any> leftItems, ItemProvider<Any> rightItems,
        Predicate<Any> isIgnorable, BiPredicate<Any, Any> isMatching)
    {
        return diff(leftItems, rightItems, isIgnorable, isMatching, new Results<Any>());
    }

    public static <Any, AnyConsumer extends Consumer<Any>> AnyConsumer diff(ItemProvider<Any> leftItems,
        ItemProvider<Any> rightItems, Predicate<Any> isIgnorable, BiPredicate<Any, Any> isMatching,
        AnyConsumer consumer)
    {
        if (isMatching == null)
        {
            isMatching = Objects::equals;
        }

        LCSTable<Any> table = new LCSTable<>(leftItems, rightItems, isIgnorable, isMatching);
        int leftIndex = 0;
        int rightIndex = 0;
        int leftSize = leftItems != null ? leftItems.size() : 0;
        int rightSize = rightItems != null ? rightItems.size() : 0;

        while (leftIndex < leftSize && rightIndex < rightSize)
        {
            Any leftItem = leftItems != null ? leftItems.get(leftIndex) : null;

            if (isIgnorable != null && isIgnorable.test(leftItem))
            {
                leftIndex++;

                continue;
            }

            Any rightItem = rightItems != null ? rightItems.get(rightIndex) : null;

            if (isIgnorable != null && isIgnorable.test(rightItem))
            {
                rightIndex++;

                continue;
            }

            if (isMatching.test(leftItem, rightItem))
            {
                consumer.both(leftIndex, rightIndex, leftItem, rightItem);

                leftIndex++;
                rightIndex++;
            }
            else if (table.get(leftIndex + 1)[rightIndex] >= table.get(leftIndex)[rightIndex + 1])
            {
                consumer.onlyLeft(leftIndex, rightIndex, leftItem);

                leftIndex++;
            }
            else
            {
                consumer.onlyRight(leftIndex, rightIndex, rightItem);

                rightIndex++;
            }
        }

        // remove remaining
        while (leftIndex < leftSize && leftItems != null)
        {
            Any leftItem = leftItems.get(leftIndex);

            if (isIgnorable == null || !isIgnorable.test(leftItem))
            {
                consumer.onlyLeft(leftIndex, rightIndex, leftItem);
            }

            leftIndex++;
        }

        // add remaining
        while (rightIndex < rightSize && rightItems != null)
        {
            Any rightItem = rightItems.get(rightIndex);

            if (isIgnorable == null || !isIgnorable.test(rightItem))
            {
                consumer.onlyRight(leftIndex, rightIndex, rightItem);
            }

            rightIndex++;
        }

        return consumer;
    }

    protected static <Any> int[][] buildLCSTable(ItemProvider<Any> leftItems, ItemProvider<Any> rightItems,
        Predicate<Any> isIgnorable, BiPredicate<Any, Any> isMatching)
    {
        int leftSize = leftItems != null ? leftItems.size() : 0;
        int rightSize = rightItems != null ? rightItems.size() : 0;
        int[][] table = new int[leftSize + 1][rightSize + 1];

        for (int leftIndex = leftSize; leftIndex >= 0; leftIndex--)
        {
            Any leftItem = leftItems != null && leftIndex < leftSize ? leftItems.get(leftIndex) : null;

            if (isIgnorable != null && isIgnorable.test(leftItem))
            {
                continue;
            }

            for (int rightIndex = rightSize - 1; rightIndex >= 0; rightIndex--)
            {
                Any rightItem = rightItems != null && rightIndex < rightSize ? rightItems.get(rightIndex) : null;

                if (isIgnorable != null && isIgnorable.test(rightItem))
                {
                    continue;
                }

                if (leftIndex >= leftSize)
                {
                    table[leftIndex][rightIndex] = 0;
                }
                else if (isMatching.test(leftItem, rightItem))
                {
                    table[leftIndex][rightIndex] = table[leftIndex + 1][rightIndex + 1] + 1;
                }
                else
                {
                    table[leftIndex][rightIndex] =
                        Math.max(table[leftIndex + 1][rightIndex], table[leftIndex][rightIndex + 1]);
                }
            }
        }

        return table;
    }

    private static class LCSTable<Any>
    {
        private static final int MAX_CAPACITY = 1024 * 256;

        private final List<int[]> table;

        private final ItemProvider<Any> leftItems;
        private final ItemProvider<Any> rightItems;
        private final Predicate<Any> isIgnorable;
        private final BiPredicate<Any, Any> isMatching;

        private int leftStartIndex;

        private final int leftSize;
        private final int rightSize;

        private final int leftCapacity;

        LCSTable(ItemProvider<Any> leftItems, ItemProvider<Any> rightItems, Predicate<Any> isIgnorable,
            BiPredicate<Any, Any> isMatching)
        {
            this.leftItems = leftItems;
            this.rightItems = rightItems;
            this.isIgnorable = isIgnorable;
            this.isMatching = isMatching;

            leftStartIndex = 0;

            leftSize = leftItems != null ? leftItems.size() : 0;
            rightSize = rightItems != null ? rightItems.size() : 0;

            leftCapacity =
                Math.min(leftSize + 1, Math.max(3, Math.min(leftSize * rightSize, MAX_CAPACITY) / rightSize));

            table = new ArrayList<>(leftCapacity);

            computeTable();
        }

        public int[] get(int leftIndex)
        {
            leftIndex -= leftStartIndex;

            if (leftIndex >= leftCapacity)
            {
                leftStartIndex = Math.max(0, leftStartIndex + leftIndex - 1);

                computeTable();
            }

            return table.get(leftIndex);
        }

        protected void computeTable()
        {
            table.clear();

            for (int leftIndex = leftSize; leftIndex >= leftStartIndex; leftIndex--)
            {
                int[] line = new int[rightSize + 1];

                while (table.size() >= leftCapacity)
                {
                    table.remove(table.size() - 1);
                }

                table.add(0, line);

                Any leftItem = leftItems != null && leftIndex < leftSize ? leftItems.get(leftIndex) : null;

                if (isIgnorable != null && isIgnorable.test(leftItem))
                {
                    continue;
                }

                for (int rightIndex = rightSize - 1; rightIndex >= 0; rightIndex--)
                {
                    Any rightItem = rightItems != null && rightIndex < rightSize ? rightItems.get(rightIndex) : null;

                    if (isIgnorable != null && isIgnorable.test(rightItem))
                    {
                        continue;
                    }

                    if (leftIndex >= leftSize)
                    {
                        line[rightIndex] = 0;
                    }
                    else if (isMatching.test(leftItem, rightItem))
                    {
                        line[rightIndex] = table.get(1)[rightIndex + 1] + 1;
                    }
                    else
                    {
                        line[rightIndex] = Math.max(table.get(1)[rightIndex], line[rightIndex + 1]);
                    }
                }
            }
        }
    }

    private Diff()
    {
        super();
    }
}
