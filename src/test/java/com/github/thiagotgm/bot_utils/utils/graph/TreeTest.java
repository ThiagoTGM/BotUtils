/*
 * This file is part of BotUtils.
 *
 * BotUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BotUtils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BotUtils. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.thiagotgm.bot_utils.utils.graph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Unit tests for an implementation of the {@link Tree} interface.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-20
 */
@DisplayName( "Tree tests" )
public abstract class TreeTest {

    /**
     * Expected mappings used in some tests.
     */
    protected static final Map<List<String>, List<Integer>> TEST_MAPPINGS;

    static {

        Map<List<String>, List<Integer>> testMappings = new HashMap<>();

        // Initialize map with test mappings.
        testMappings.put( Arrays.asList( "foobar" ), Arrays.asList( 20, 43, -10 ) );
        testMappings.put( Arrays.asList( "tryOne", "nowAnotherOne" ), Arrays.asList( 42 ) );
        testMappings.put( Arrays.asList( "Boop", "Wings", "Hammer" ), Arrays.asList( -1000, 420, 0, 111 ) );
        testMappings.put( Arrays.asList( "Boop", "Wings" ), Arrays.asList( -1000, 420, 0 ) );
        testMappings.put( Arrays.asList(), Arrays.asList() );
        testMappings.put( Arrays.asList( "maven", "gradle" ), Arrays.asList( 2018, 9, 21, 5, 32, 30, 999 ) );
        testMappings.put( Arrays.asList( "cards", "poker", "blackjack", "uno" ),
                Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 1 ) );
        testMappings.put( Arrays.asList( "JUnit", "5", "is", "nice" ), Arrays.asList( 5, 13 ) );
        testMappings.put( Arrays.asList( "Magic", "numbers" ), Arrays.asList( -1, 0, 1 ) );

        TEST_MAPPINGS = Collections.unmodifiableMap( testMappings );

    }

    /**
     * Creates a blank tree.
     * 
     * @return The tree.
     */
    protected abstract Tree<String, List<Integer>> getTree();

    /**
     * Determines whether the tree accepts <tt>null</tt> keys in a path.
     *
     * @return <tt>true</tt> if the tree accepts <tt>null</tt> keys.
     */
    protected abstract boolean acceptsNullKeys();

    /**
     * Determines whether the map accepts <tt>null</tt> values.
     *
     * @return <tt>true</tt> if the map accepts <tt>null</tt> values.
     */
    protected abstract boolean acceptsNullValues();

    /**
     * Test for {@link Tree#size()}.
     */
    @Test
    @DisplayName( "size() test" )
    public void testSize() {

        Tree<String, List<Integer>> tree = getTree();
        assertEquals( 0, tree.size(), "Blank tree should be empty." );

        tree.put( Arrays.asList( "The key" ), Arrays.asList( 10 ) );
        tree.put( Arrays.asList( "Two keys" ), Arrays.asList( 0, -2 ) );
        tree.put( Arrays.asList( "Master key" ), Arrays.asList( 100, 404, 11 ) );
        assertEquals( 3, tree.size(), "Incorrect tree size after adding." );

    }

    /**
     * Test for {@link Tree#isEmpty()}.
     */
    @Test
    @DisplayName( "isEmpty() test" )
    public void testIsEmpty() {

        Tree<String, List<Integer>> tree = getTree();
        assertTrue( tree.isEmpty(), "Blank tree should be empty." );

        tree.put( Arrays.asList( "The key" ), Arrays.asList( 10 ) );
        tree.put( Arrays.asList( "Two keys" ), Arrays.asList( 0, -2 ) );
        tree.put( Arrays.asList( "Master key" ), Arrays.asList( 100, 404, 11 ) );
        assertFalse( tree.isEmpty(), "Tree after adding should not be empty." );

    }

    /**
     * Test for {@link Tree#containsPath(List)}.
     */
    @Test
    @DisplayName( "containsPath(List<?>) test" )
    public void testContainsPath() {

        Tree<String, List<Integer>> tree = getTree();

        /* Test paths before inserting */

        for ( List<String> path : TEST_MAPPINGS.keySet() ) {

            assertFalse( tree.containsPath( path ), "Should not have path before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            tree.put( entry.getKey(), entry.getValue() );

        }

        for ( List<String> path : TEST_MAPPINGS.keySet() ) {

            assertTrue( tree.containsPath( path ), "Should have path after inserting." );

        }

        /* Test non-existing paths */

        assertFalse( tree.containsPath( new Integer( 10 ) ), "Should not have path of wrong type." );
        assertFalse( tree.containsPath( Arrays.asList( "not here" ) ), "Should not have inexistent path." );
        assertFalse( tree.containsPath( Arrays.asList( "bazinga" ) ), "Should not have inexistent path." );
        assertFalse( tree.containsPath( Arrays.asList( "ayyy" ) ), "Should not have inexistent path." );

        /* Check null path */

        assertThrows( NullPointerException.class, () -> tree.containsPath( (List<?>) null ),
                "Shoud throw an exception for containsPath with null path." );

    }

    /**
     * Test for {@link Tree#containsValue(Object)}.
     */
    @Test
    @DisplayName( "containsValue(Object) test" )
    public void testContainsValue() {

        Tree<String, List<Integer>> tree = getTree();

        /* Test values before inserting */

        for ( List<Integer> value : TEST_MAPPINGS.values() ) {

            assertFalse( tree.containsValue( value ), "Should not have value before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            tree.put( entry.getKey(), entry.getValue() );

        }

        for ( List<Integer> value : TEST_MAPPINGS.values() ) {

            assertTrue( tree.containsValue( value ), "Should have value after inserting." );

        }

        /* Test non-existing values */

        assertFalse( tree.containsValue( new Integer( 10 ) ), "Should not have value of wrong type." );
        assertFalse( tree.containsValue( Arrays.asList( 11 ) ), "Should not have inexistent value." );
        assertFalse( tree.containsValue( Arrays.asList( 42, 11 ) ), "Should not have inexistent value." );
        assertFalse( tree.containsValue( Arrays.asList( 20, 42, -10 ) ), "Should not have inexistent value." );

    }

    /**
     * Test for {@link Tree#put(List, Object)} and {@link Tree#get(List)}.
     */
    @Test
    @DisplayName( "put(List<K>,V) and get(List<?>) test" )
    public void testPutAndGet() {

        Tree<String, List<Integer>> tree = getTree();

        /* Test getting before inserting */

        for ( List<String> path : TEST_MAPPINGS.keySet() ) {

            assertNull( tree.get( path ), "Should return null before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertNull( tree.put( entry.getKey(), entry.getValue() ),
                    "Inserting path for first time should return null." );

        }

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.get( entry.getKey() ), "Returned value does not match put value." );

        }

        /* Insert over same paths */

        int i = 0;
        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.put( entry.getKey(), Arrays.asList( i++ ) ),
                    "Re-inserting path returned wrong old value." );

        }

        i = 0;
        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( Arrays.asList( i++ ), tree.get( entry.getKey() ),
                    "Returned value does not match updated value." );

        }

        /* Check inexistent paths */

        assertNull( tree.get( Arrays.asList( "not here" ) ), "Should return null on inexistent path." );
        assertNull( tree.get( Arrays.asList( "" ) ), "Should return null on inexistent path." );
        assertNull( tree.get( new Integer( 5 ) ), "Should return null on path of wrong type." );

        /* Check null path */

        assertThrows( NullPointerException.class, () -> tree.put( null, Arrays.asList( 1 ) ),
                "Shoud throw an exception for put with null path." );
        assertThrows( NullPointerException.class, () -> tree.get( (List<?>) null ),
                "Shoud throw an exception for get with null path." );

    }

    /**
     * Test for {@link Tree#remove(List)}.
     */
    @Test
    @DisplayName( "remove(List<?>) test" )
    public void testRemove() {

        Tree<String, List<Integer>> tree = getTree();

        // Add test values
        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            tree.put( entry.getKey(), entry.getValue() );

        }
        List<String> extraPath = Arrays.asList( "extra" );
        List<Integer> extraValue = Arrays.asList( 1, 2, 3, 4, 5 );
        tree.put( extraPath, extraValue );

        /* Try removing non-existent values */

        assertNull( tree.remove( Arrays.asList( "none" ) ), "Should fail to remove inexistent path." );
        assertNull( tree.remove( Arrays.asList( "I do not exist" ) ), "Should fail to remove inexistent path." );
        assertNull( tree.remove( new Integer( 0 ) ), "Should fail to remove wrong-type path." );

        // Ensure existing keys weren't changed.
        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.get( entry.getKey() ), "Unrelated path was changed." );

        }
        assertEquals( extraValue, tree.get( extraPath ), "Unrelated path was changed." );

        /* Try removing existing values */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.remove( entry.getKey() ), "Wrong old value returned." );

        }

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertNull( tree.get( entry.getKey() ), "Mapping was not deleted." );
            assertFalse( tree.containsPath( entry.getKey() ), "Should not contain deleted path." );

        }

        // Check that unrelated values are untouched.
        assertEquals( extraValue, tree.get( extraPath ), "Unrelated path was changed." );

    }

    /**
     * Test for {@link Tree#put(List, Object)}, {@link Tree#get(List)}, and
     * {@link Tree#remove(List)} on <tt>null</tt> keys (elements of a path) and/or
     * values. If the tree does not allow such keys and/or values, expects an
     * exception on put().
     * 
     * @throws Throwable
     *             if an unexpected exception was thrown.
     */
    @Test
    @DisplayName( "null keys and values test" )
    public void testNullObjects() throws Throwable {

        Tree<String, List<Integer>> tree = getTree();

        Executable nullKeyTest = () -> {

            assertNull( tree.put( Arrays.asList( null, "lol" ), Arrays.asList( 1, 2 ) ),
                    "Inserting path for first time should return null." );
            assertTrue( tree.containsPath( Arrays.asList( null, "lol" ) ), "Should contain put path." );
            assertTrue( tree.containsValue( Arrays.asList( 1, 2 ) ), "Should contain put value." );
            assertEquals( Arrays.asList( 1, 2 ), tree.get( Arrays.asList( null, "lol" ) ),
                    "Incorrect value retrieved for put path." );
            assertEquals( Arrays.asList( 1, 2 ), tree.remove( Arrays.asList( null, "lol" ) ),
                    "Incorrect value retrieved for remove path." );
            assertTrue( tree.isEmpty(), "Tree should have been cleared." );

        };

        Executable nullValueTest = () -> {

            assertNull( tree.put( Arrays.asList( "null value" ), null ),
                    "Inserting path for first time should return null." );
            assertTrue( tree.containsPath( Arrays.asList( "null value" ) ), "Should contain put path." );
            assertTrue( tree.containsValue( null ), "Should contain put value." );
            assertNull( tree.get( Arrays.asList( "null value" ) ), "Incorrect value retrieved for put path." );
            assertNull( tree.remove( Arrays.asList( "null value" ) ), "Incorrect value retrieved for remove path." );
            assertTrue( tree.isEmpty(), "Tree should have been cleared." );

        };

        Executable nullKeyAndValueTest = () -> {

            assertNull( tree.put( Arrays.asList( null, "lol" ), null ),
                    "Inserting path for first time should return null." );
            assertTrue( tree.containsPath( Arrays.asList( null, "lol" ) ), "Should contain put path." );
            assertTrue( tree.containsValue( null ), "Should contain put value." );
            assertNull( tree.get( Arrays.asList( null, "lol" ) ), "Incorrect value retrieved for put path." );
            assertNull( tree.remove( Arrays.asList( null, "lol" ) ), "Incorrect value retrieved for remove path." );
            assertTrue( tree.isEmpty(), "Tree should have been cleared." );

        };

        if ( acceptsNullKeys() ) {
            nullKeyTest.execute();
        } else {
            assertThrows( NullPointerException.class, nullKeyTest, "Null keys should throw an exception." );
        }

        if ( acceptsNullValues() ) {
            nullValueTest.execute();
        } else {
            assertThrows( NullPointerException.class, nullValueTest, "Null values should throw an exception." );
        }

        if ( acceptsNullKeys() && acceptsNullValues() ) {
            nullKeyAndValueTest.execute();
        } else {
            assertThrows( NullPointerException.class, nullKeyAndValueTest,
                    "Null keys and values should throw an exception." );
        }

    }

    /**
     * Test for {@link Tree#putAll(Graph)}.
     */
    @Test
    @DisplayName( "putAll(Graph<? extends K,? extends V>) test" )
    public void testPutAll() {

        Tree<String, List<Integer>> tree = getTree();
        Tree<String, List<Integer>> otherTree = getTree();

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            // Fill other tree with mappings.
            otherTree.put( entry.getKey(), entry.getValue() );

        }

        assertTrue( tree.isEmpty(), "Tree should be empty at first." ); // Ensure map is empty.

        tree.putAll( otherTree ); // Put all mappings.

        /* Check for inserted values */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.get( entry.getKey() ), "Did not put correct value." );

        }

    }

    /**
     * Test for {@link Tree#putAll(Map)}.
     */
    @Test
    @DisplayName( "putAll(Map<? extends List<? extends K>,? extends V>) test" )
    public void testPutAllMap() {

        Tree<String, List<Integer>> tree = getTree();

        assertTrue( tree.isEmpty(), "Tree should be empty at first." ); // Ensure tree is empty.

        tree.putAll( TEST_MAPPINGS ); // Put all mappings.

        /* Check for inserted values */

        for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), tree.get( entry.getKey() ), "Did not put correct value." );

        }

    }

    /**
     * Test for {@link Tree#toMap(Map)}.
     */
    @Test
    @DisplayName( "toMap(Map<? super List<? extends K>, ? super V>) test" )
    public void testToMap() {

        /* Test empty first */

        Tree<String, List<Integer>> tree = getTree();

        Map<List<String>, List<Integer>> otherMap = tree.toMap( new HashMap<>() );
        assertEquals( new HashMap<>(), otherMap, "Obtained map was not as expected." );

        /* Now test filled */

        tree.putAll( TEST_MAPPINGS ); // Put all mappings.

        otherMap = tree.toMap( new HashMap<>() );
        assertEquals( TEST_MAPPINGS, otherMap, "Obtained map was not as expected." );

    }

    /**
     * Test for {@link Tree#clear()}.
     */
    @Test
    @DisplayName( "clear() test" )
    public void testClear() {

        Tree<String, List<Integer>> tree = getTree();

        assertTrue( tree.isEmpty(), "Tree should be empty at first." ); // Ensure tree is empty.

        tree.putAll( TEST_MAPPINGS ); // Insert mappings.

        assertFalse( tree.isEmpty(), "Tree should not be empty after insertion." ); // Check if tree is now not empty.

        tree.clear(); // Clear mappings.

        assertTrue( tree.isEmpty(), "Tree should be empty after clear." ); // Check if tree is empty again.

    }

    /**
     * Test for {@link Tree#equals(Object)}.
     */
    @Test
    @DisplayName( "equals(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testEquals() {

        Tree<String, List<Integer>> tree = getTree();

        Tree<String, List<Integer>> emptyTree = new HashTree<>();
        Tree<String, List<Integer>> testTree = new HashTree<>( TEST_MAPPINGS );
        Tree<String, List<Integer>> otherTree = new HashTree<>();
        otherTree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );

        /* Test while empty */

        // Check itself.
        assertTrue( tree.equals( tree ) );

        // Check non-tree.
        assertFalse( tree.equals( "str" ), "Should not be equal to non-tree." );
        assertFalse( "str".equals( tree ), "Should not be equal to non-tree." );

        // Check empty tree.
        assertTrue( tree.equals( emptyTree ), "Should be equal to empty tree." );
        assertTrue( emptyTree.equals( tree ), "Should be equal to empty tree." );

        // Check test mappings.
        assertFalse( tree.equals( testTree ), "Should not be equal to test tree." );
        assertFalse( testTree.equals( tree ), "Should not be equal to test tree." );

        // Check other tree.
        assertFalse( tree.equals( otherTree ), "Should not be equal to other tree." );
        assertFalse( otherTree.equals( tree ), "Should not be equal to other tree." );

        /* Test with test mappings */

        tree.putAll( TEST_MAPPINGS );

        // Check itself.
        assertTrue( tree.equals( tree ) );

        // Check non-tree.
        assertFalse( tree.equals( "str" ), "Should not be equal to non-tree." );
        assertFalse( "str".equals( tree ), "Should not be equal to non-tree." );

        // Check empty tree.
        assertFalse( tree.equals( emptyTree ), "Should not be equal to empty tree." );
        assertFalse( emptyTree.equals( tree ), "Should not be equal to empty tree." );

        // Check test mappings.
        assertTrue( tree.equals( testTree ), "Should be equal to test tree." );
        assertTrue( testTree.equals( tree ), "Should be equal to test tree." );

        // Check other tree.
        assertFalse( tree.equals( otherTree ), "Should not be equal to other tree." );
        assertFalse( otherTree.equals( tree ), "Should not be equal to other tree." );

    }

    /**
     * Test for {@link Tree#hashCode()}.
     */
    @Test
    @DisplayName( "hashCode() test" )
    public void testHashCode() {

        Tree<String, List<Integer>> map = getTree();
        assertEquals( new HashMap<>().hashCode(), map.hashCode(), "Did not match empty hash code." );

        map.putAll( TEST_MAPPINGS );
        assertEquals( new HashMap<>( TEST_MAPPINGS ).hashCode(), map.hashCode(), "Did not match filled hash code." );

    }

    /**
     * Tests for the {@link Tree#pathSet() key set} view.
     */
    @Nested
    @DisplayName( "Path set tests" )
    public class PathSetTest {

        /**
         * Test for {@link Set#size()}.
         */
        @Test
        @DisplayName( "size() test" )
        public void testSize() {

            Tree<String, List<Integer>> tree = getTree();
            assertEquals( 0, tree.pathSet().size(), "Wrong size of path set of empty tree." );

            tree.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), tree.pathSet().size(), "Wrong size of path set of filled tree." );

        }

        /**
         * Test for {@link Set#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Tree<String, List<Integer>> tree = getTree();
            assertTrue( tree.pathSet().isEmpty(), "Path set of empty tree should be empty." );

            tree.putAll( TEST_MAPPINGS );
            assertFalse( tree.pathSet().isEmpty(), "Path set of filled tree should not be empty." );

        }

        /**
         * Test for {@link Set#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Set<List<String>> paths = tree.pathSet();

            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( paths.contains( path ), "Should contain path." );

            }

            assertFalse( paths.contains( new Integer( 42 ) ), "Should not contain path of wrong type." );
            assertFalse( paths.contains( "foobar" ), "Should not contain path of wrong type." );
            assertFalse( paths.contains( Arrays.asList( "lololol" ) ), "Should not contain inexistent path." );
            assertFalse( paths.contains( Arrays.asList( "wroooooooong" ) ), "Should not contain inexistent path." );

        }

        /**
         * Test for {@link Set#iterator()}.
         */
        @Test
        @DisplayName( "iterator() test" )
        public void testIterator() {

            Tree<String, List<Integer>> tree = getTree();
            assertFalse( tree.pathSet().iterator().hasNext(), "Iterator on empty path set shouldn't have a next." );

            tree.putAll( TEST_MAPPINGS );
            Iterator<List<String>> iter = tree.pathSet().iterator();
            Set<List<String>> expected = new HashSet<>( TEST_MAPPINGS.keySet() );

            while ( iter.hasNext() ) { // Check if iterator only returns expected keys.

                assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected path." );

            }

            assertTrue( expected.isEmpty(), "Not all expected paths were found." ); // Ensure all expected paths were
                                                                                    // returned.

        }

        /**
         * Test for {@link Iterator#remove()}.
         */
        @Test
        @DisplayName( "iterator().remove() test" )
        public void testIteratorRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );

            Set<List<String>> toFind = new HashSet<>( tree.pathSet() );
            Iterator<List<String>> iter = tree.pathSet().iterator();

            // Try removing before calling next.
            assertThrows( IllegalStateException.class, () -> iter.remove(),
                    "Should throw an exception when removing before next." );

            while ( iter.hasNext() ) {

                List<String> next = iter.next();
                assertTrue( toFind.remove( next ), "Found unexpected path." );
                if ( next.equals( Arrays.asList( "one" ) ) ) {
                    iter.remove(); // Remove one element.
                    assertThrows( IllegalStateException.class, () -> iter.remove(),
                            "Should throw an exception when removing twice." );
                }

            }

            assertTrue( toFind.isEmpty(), "Did not iterate through all paths." ); // Ensure iterated over everything.

            assertFalse( tree.containsPath( Arrays.asList( "one" ) ), "Did not remove path." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( path ), "Unrelated path was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ), "Unrelated path was removed." );

        }

        /**
         * Test for {@link Set#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Tree<String, List<Integer>> map = getTree();

            /* Test with empty map */

            assertArrayEquals( new Object[0], map.pathSet().toArray(),
                    "Empty tree's path set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( map.pathSet().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.keySet().toArray() );

            assertEquals( expected.size(), actual.size(), "Tree path set array has incorrect size." );
            assertTrue( actual.containsAll( expected ), "Tree path set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Tree<String, List<Integer>> map = getTree();

            /* Test with empty map */

            assertArrayEquals( new List[arrSize], map.pathSet().toArray( new List[arrSize] ),
                    "Empty tree's path set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            @SuppressWarnings( "unchecked" )
            List<String>[] actual = map.pathSet().toArray( (List<String>[]) new List[arrSize] );

            assertEquals( arrSize, actual.length, "Tree path set array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue( Arrays.asList( actual ).containsAll( TEST_MAPPINGS.keySet() ),
                    "Tree path set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#add(Object)}.
         */
        @Test
        @DisplayName( "add(List<K>) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class, () -> getTree().pathSet().add( Arrays.asList( "fail" ) ),
                    "Attempting to add to path set should throw an exception." );

        }

        /**
         * Test for {@link Set#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );

            assertTrue( tree.pathSet().remove( Arrays.asList( "one" ) ), "Path could not be removed." );

            assertFalse( tree.containsPath( Arrays.asList( "one" ) ), "Did not remove path." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( path ), "Unrelated path was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ), "Unrelated path was removed." );

        }

        /**
         * Test for {@link Set#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Map<List<String>, List<Integer>> emptyTree = new HashMap<>();
            Map<List<String>, List<Integer>> otherTree = new HashMap<>( TEST_MAPPINGS );
            otherTree.put( Arrays.asList( "plane" ), Arrays.asList( 99 ) );

            Tree<String, List<Integer>> map = getTree();

            assertTrue( map.pathSet().containsAll( emptyTree.keySet() ), "Should contain all elements." );
            assertFalse( map.pathSet().containsAll( TEST_MAPPINGS.keySet() ), "Should not contain all elements." );
            assertFalse( map.pathSet().containsAll( otherTree.keySet() ), "Should not contain all elements." );

            map.putAll( TEST_MAPPINGS );

            assertTrue( map.pathSet().containsAll( emptyTree.keySet() ), "Should contain all elements." );
            assertTrue( map.pathSet().containsAll( TEST_MAPPINGS.keySet() ), "Should contain all elements." );
            assertFalse( map.pathSet().containsAll( otherTree.keySet() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Set#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends List<K>>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getTree().pathSet().addAll( TEST_MAPPINGS.keySet() ),
                    "Attempting to add to path set should throw an exception." );

        }

        /**
         * Test for {@link Set#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Tree<String, List<Integer>> map = getTree();
            map.putAll( TEST_MAPPINGS );
            Set<List<String>> paths = map.pathSet();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<List<String>, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<List<String>, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( paths.removeAll( toRemove.keySet() ), "Should have removed something." ); // Remove.

            for ( List<String> path : toRemove.keySet() ) { // Check removed.

                assertFalse( paths.contains( path ), "One of the paths was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( paths.containsAll( toRetain.keySet() ), "An unexpected path was removed." );

            assertFalse( paths.removeAll( toRemove.keySet() ), "Should have nothing to remove." ); // Try removing
                                                                                                   // again.

        }

        /**
         * Test for {@link Set#retainAll(Collection)}.
         */
        @Test
        @DisplayName( "retainAll(Collection<?>) test" )
        public void testRetainAll() {

            Tree<String, List<Integer>> map = getTree();
            map.putAll( TEST_MAPPINGS );
            Set<List<String>> paths = map.pathSet();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<List<String>, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<List<String>, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( paths.retainAll( toRetain.keySet() ), "Should have removed something." ); // Remove.

            for ( List<String> path : toRemove.keySet() ) { // Check removed.

                assertFalse( paths.contains( path ), "One of the paths was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( paths.containsAll( toRetain.keySet() ), "An unexpected path was removed." );

            assertFalse( paths.removeAll( toRemove.keySet() ), "Should have nothing to remove." ); // Try removing
                                                                                                   // again.

        }

        /**
         * Test for {@link Set#clear()}.
         */
        @Test
        @DisplayName( "clear() test" )
        public void testClear() {

            Tree<String, List<Integer>> map = getTree();

            map.pathSet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.putAll( TEST_MAPPINGS );

            assertFalse( map.isEmpty(), "Should not be empty after adding." );

            map.pathSet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.pathSet().clear(); // Try doing it again.
            assertTrue( map.isEmpty(), "Should still be empty after clearing twice." );

        }

        /**
         * Test for {@link Set#equals(Object)}.
         */
        @Test
        @DisplayName( "equals(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testEquals() {

            Map<List<String>, List<Integer>> emptyMap = new HashMap<>();
            Map<List<String>, List<Integer>> otherMap = new HashMap<>( TEST_MAPPINGS );
            otherMap.put( Arrays.asList( "one" ), Arrays.asList( 6, 7, 6 ) );

            Tree<String, List<Integer>> tree = getTree();
            Set<List<String>> paths = tree.pathSet();

            /* Test with empty map */

            // Check itself.
            assertTrue( paths.equals( paths ) );

            // Check the map itself.
            assertFalse( paths.equals( tree ) );
            assertFalse( tree.equals( paths ) );

            // Check right map but the map itself.
            assertFalse( paths.equals( emptyMap ) );
            assertFalse( emptyMap.equals( paths ) );

            // Check non-map.
            assertFalse( paths.equals( "str" ) );
            assertFalse( "str".equals( paths ) );

            // Check empty map.
            assertTrue( paths.equals( emptyMap.keySet() ) );
            assertTrue( emptyMap.keySet().equals( paths ) );

            // Check test map.
            assertFalse( paths.equals( TEST_MAPPINGS.keySet() ) );
            assertFalse( TEST_MAPPINGS.keySet().equals( paths ) );

            // Check other map.
            assertFalse( paths.equals( otherMap.keySet() ) );
            assertFalse( otherMap.keySet().equals( paths ) );

            /* Test with filled map */

            tree.putAll( TEST_MAPPINGS );

            // Check itself.
            assertTrue( paths.equals( paths ) );

            // Check the map itself.
            assertFalse( paths.equals( tree ) );
            assertFalse( tree.equals( paths ) );

            // Check right map but the map itself.
            assertFalse( paths.equals( TEST_MAPPINGS ) );
            assertFalse( TEST_MAPPINGS.equals( paths ) );

            // Check non-map.
            assertFalse( paths.equals( "str" ) );
            assertFalse( "str".equals( paths ) );

            // Check empty map.
            assertFalse( paths.equals( emptyMap.keySet() ) );
            assertFalse( emptyMap.keySet().equals( paths ) );

            // Check test map.
            assertTrue( paths.equals( TEST_MAPPINGS.keySet() ) );
            assertTrue( TEST_MAPPINGS.keySet().equals( paths ) );

            // Check other map.
            assertFalse( paths.equals( otherMap.keySet() ) );
            assertFalse( otherMap.keySet().equals( paths ) );

        }

        /**
         * Test for {@link Set#hashCode()}.
         */
        @Test
        @DisplayName( "hashCode() test" )
        public void testHashCode() {

            Tree<String, List<Integer>> map = getTree();
            assertEquals( new HashMap<>().keySet().hashCode(), map.pathSet().hashCode(),
                    "Empty tree path set hash code not correct." );
            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.keySet().hashCode(), map.pathSet().hashCode(),
                    "Filled tree path set hash code not correct." );

        }

    }

    /**
     * Tests for the {@link Tree#values() value collection} view.
     */
    @Nested
    @DisplayName( "Value collection tests" )
    public class ValueCollectionTest {

        /**
         * Test for {@link Collection#size()}.
         */
        @Test
        @DisplayName( "size() test" )
        public void testSize() {

            Tree<String, List<Integer>> tree = getTree();
            assertEquals( 0, tree.values().size(), "Wrong size of value collection of empty tree." );

            tree.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), tree.values().size(),
                    "Wrong size of value collection of filled tree." );

        }

        /**
         * Test for {@link Collection#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Tree<String, List<Integer>> tree = getTree();
            assertTrue( tree.values().isEmpty(), "Value collection of empty tree should be empty." );

            tree.putAll( TEST_MAPPINGS );
            assertFalse( tree.values().isEmpty(), "Value collection of filled tree should not be empty." );

        }

        /**
         * Test for {@link Collection#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = tree.values();

            for ( List<Integer> value : TEST_MAPPINGS.values() ) {

                assertTrue( values.contains( value ), "Should contain value." );

            }

            assertFalse( values.contains( "thinkTak" ), "Should not contain value of wrong type." );
            assertFalse( values.contains( Arrays.asList( 98, 99, 100 ) ), "Should not contain inexistent value." );
            assertFalse( values.contains( Arrays.asList( 11, 12, 12 ) ), "Should not contain inexistent value." );

        }

        /**
         * Test for {@link Collection#iterator()}.
         */
        @Test
        @DisplayName( "iterator() test" )
        public void testIterator() {

            Tree<String, List<Integer>> tree = getTree();
            assertFalse( tree.values().iterator().hasNext(),
                    "Iterator on empty value collection shouldn't have a next." );

            tree.putAll( TEST_MAPPINGS );
            Iterator<List<Integer>> iter = tree.values().iterator();
            Collection<List<Integer>> expected = new ArrayList<>( TEST_MAPPINGS.values() );

            while ( iter.hasNext() ) { // Check if iterator only returns expected values.

                assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected value." );

            }

            assertTrue( expected.isEmpty(), "Not all expected values were found." ); // Ensure all expected values were
                                                                                     // returned.

        }

        /**
         * Test for {@link Iterator#remove()}.
         */
        @Test
        @DisplayName( "iterator().remove() test" )
        public void testIteratorRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );
            tree.put( Arrays.asList( "two.2" ), Arrays.asList( 1, 2 ) );
            tree.put( Arrays.asList( "three" ), Arrays.asList( 1, 2, 3 ) );

            Collection<List<Integer>> toFind = new ArrayList<>( tree.values() );
            Iterator<List<Integer>> iter = tree.values().iterator();

            // Try removing before calling next.
            assertThrows( IllegalStateException.class, () -> iter.remove(),
                    "Should throw an exception when removing before next." );

            boolean shouldRemove = true;
            while ( iter.hasNext() ) {

                List<Integer> next = iter.next();
                assertTrue( toFind.remove( next ), "Found unexpected value." );
                if ( shouldRemove && next.equals( Arrays.asList( 1, 2 ) ) ) {
                    iter.remove(); // Remove one element.
                    shouldRemove = false; // Should not remove another one.
                    assertThrows( IllegalStateException.class, () -> iter.remove(),
                            "Should throw an exception when removing twice." );
                }

            }

            assertTrue( toFind.isEmpty(), "Did not iterate through all values." ); // Ensure iterated over everything.

            assertFalse( tree.containsPath( Arrays.asList( "two" ) ) && tree.containsPath( Arrays.asList( "two.2" ) ),
                    "Did not remove value." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( path ), "Unrelated value was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ) || tree.containsPath( Arrays.asList( "two.2" ) ),
                    "Should have removed only one matching value." );
            assertTrue( tree.containsPath( Arrays.asList( "one" ) ), "Unrelated value was removed." );
            assertTrue( tree.containsPath( Arrays.asList( "three" ) ), "Unrelated value was removed." );

        }

        /**
         * Test for {@link Collection#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Tree<String, List<Integer>> tree = getTree();

            /* Test with empty tree */

            assertArrayEquals( new Object[0], tree.values().toArray(),
                    "Empty tree's value collection should become empty array." );

            /* Test with filled tree */

            tree.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( tree.values().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.values().toArray() );

            assertEquals( expected.size(), actual.size(), "Tree value collection array has incorrect size." );
            assertTrue( actual.containsAll( expected ),
                    "Tree value collection array does not have all expected elements." );

        }

        /**
         * Test for {@link Collection#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Tree<String, List<Integer>> tree = getTree();

            /* Test with empty map */

            assertArrayEquals( new List[arrSize], tree.values().toArray( new List[arrSize] ),
                    "Empty tree's value collection should become empty array." );

            /* Test with filled map */

            tree.putAll( TEST_MAPPINGS );
            @SuppressWarnings( "unchecked" )
            List<Integer>[] actual = tree.values().toArray( (List<Integer>[]) new List[arrSize] );

            assertEquals( arrSize, actual.length, "Tree value collection array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue( Arrays.asList( actual ).containsAll( TEST_MAPPINGS.values() ),
                    "Tree value collection array does not have all expected elements." );

        }

        /**
         * Test for {@link Collection#add(Object)}.
         */
        @Test
        @DisplayName( "add(V) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class, () -> getTree().values().add( Arrays.asList( 101 ) ),
                    "Attempting to add to value collection should throw an exception." );

        }

        /**
         * Test for {@link Collection#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );
            tree.put( Arrays.asList( "two.2" ), Arrays.asList( 1, 2 ) );
            tree.put( Arrays.asList( "three" ), Arrays.asList( 1, 2, 3 ) );

            assertTrue( tree.values().remove( Arrays.asList( 1, 2 ) ), "Value could not be removed." );

            assertFalse( tree.containsPath( Arrays.asList( "two" ) ) && tree.containsPath( Arrays.asList( "two.2" ) ),
                    "Did not remove value." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> key : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( key ), "Unrelated value was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ) || tree.containsPath( Arrays.asList( "two.2" ) ),
                    "Should have removed only one matching value." );
            assertTrue( tree.containsPath( Arrays.asList( "one" ) ), "Unrelated value was removed." );
            assertTrue( tree.containsPath( Arrays.asList( "three" ) ), "Unrelated value was removed." );

        }

        /**
         * Test for {@link Collection#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Tree<String, List<Integer>> emptyTree = new HashTree<>();
            Tree<String, List<Integer>> otherTree = new HashTree<>( TEST_MAPPINGS );
            otherTree.put( Arrays.asList( "plane" ), Arrays.asList( 99 ) );

            Tree<String, List<Integer>> tree = getTree();

            assertTrue( tree.values().containsAll( emptyTree.values() ), "Should contain all elements." );
            assertFalse( tree.values().containsAll( TEST_MAPPINGS.values() ), "Should not contain all elements." );
            assertFalse( tree.values().containsAll( otherTree.values() ), "Should not contain all elements." );

            tree.putAll( TEST_MAPPINGS );

            assertTrue( tree.values().containsAll( emptyTree.values() ), "Should contain all elements." );
            assertTrue( tree.values().containsAll( TEST_MAPPINGS.values() ), "Should contain all elements." );
            assertFalse( tree.values().containsAll( otherTree.values() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Collection#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends V>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getTree().values().addAll( TEST_MAPPINGS.values() ),
                    "Attempting to add to value collection should throw an exception." );

        }

        /**
         * Test for {@link Collection#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = tree.values();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine values to remove */

            Map<List<String>, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine values to maintain */

            Map<List<String>, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( values.removeAll( toRemove.values() ), "Should have removed something." ); // Remove.

            for ( List<Integer> value : toRemove.values() ) { // Check removed.

                assertFalse( values.contains( value ), "One of the values was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( values.containsAll( toRetain.values() ), "An unexpected value was removed." );

            assertFalse( values.removeAll( toRemove.values() ), "Should have nothing to remove." ); // Try removing
            // again.

        }

        /**
         * Test for {@link Collection#retainAll(Collection)}.
         */
        @Test
        @DisplayName( "retainAll(Collection<?>) test" )
        public void testRetainAll() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = tree.values();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine values to remove */

            Map<List<String>, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine values to maintain */

            Map<List<String>, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( values.retainAll( toRetain.values() ), "Should have removed something." ); // Remove.

            for ( List<Integer> value : toRemove.values() ) { // Check removed.

                assertFalse( values.contains( value ), "One of the values was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( values.containsAll( toRetain.values() ), "An unexpected value was removed." );

            assertFalse( values.removeAll( toRemove.values() ), "Should have nothing to remove." ); // Try removing
            // again.

        }

        /**
         * Test for {@link Collection#clear()}.
         */
        @Test
        @DisplayName( "clear() test" )
        public void testClear() {

            Tree<String, List<Integer>> tree = getTree();

            tree.values().clear();
            assertTrue( tree.isEmpty(), "Should be empty after clearing." );

            tree.putAll( TEST_MAPPINGS );

            assertFalse( tree.isEmpty(), "Should not be empty after adding." );

            tree.values().clear();
            assertTrue( tree.isEmpty(), "Should be empty after clearing." );

            tree.values().clear(); // Try doing it again.
            assertTrue( tree.isEmpty(), "Should still be empty after clearing twice." );

        }

    }

    /**
     * Tests for the {@link Tree#entrySet() entry set} view.
     */
    @Nested
    @DisplayName( "Entry set tests" )
    public class EntrySetTest {

        /**
         * Test for {@link Set#size()}.
         */
        @Test
        @DisplayName( "size() test" )
        public void testSize() {

            Tree<String, List<Integer>> tree = getTree();
            assertEquals( 0, tree.entrySet().size(), "Wrong size of entry set of empty tree." );

            tree.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), tree.entrySet().size(), "Wrong size of entry set of filled tree." );

        }

        /**
         * Test for {@link Set#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Tree<String, List<Integer>> tree = getTree();
            assertTrue( tree.entrySet().isEmpty(), "Entry set of empty tree should be empty." );

            tree.putAll( TEST_MAPPINGS );
            assertFalse( tree.entrySet().isEmpty(), "Entry set of filled tree should not be empty." );

        }

        /**
         * Test for {@link Set#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Set<Tree.Entry<String, List<Integer>>> entries = tree.entrySet();

            for ( Map.Entry<List<String>, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

                assertTrue( entries.contains( new AbstractTree.SimpleEntry<>( entry ) ), "Should contain entry." );

            }

            assertFalse( entries.contains( new Integer( 42 ) ), "Should not contain entry of wrong type." );
            assertFalse(
                    entries.contains(
                            new AbstractTree.SimpleEntry<>( Arrays.asList( "lololol" ), Arrays.asList( 0 ) ) ),
                    "Should not contain inexistent entry." );
            assertFalse(
                    entries.contains(
                            new AbstractTree.SimpleEntry<>( Arrays.asList( "wroooooooong" ), Arrays.asList( -1, 0 ) ) ),
                    "Should not contain inexistent entry." );

        }

        /**
         * Test for {@link Set#iterator()}.
         */
        @Test
        @DisplayName( "iterator() test" )
        public void testIterator() {

            Tree<String, List<Integer>> tree = getTree();
            assertFalse( tree.entrySet().iterator().hasNext(), "Iterator on empty entry set shouldn't have a next." );

            tree.putAll( TEST_MAPPINGS );
            Iterator<Tree.Entry<String, List<Integer>>> iter = tree.entrySet().iterator();
            Set<Graph.Entry<String, List<Integer>>> expected = TEST_MAPPINGS.entrySet().stream()
                    .map( e -> new AbstractGraph.SimpleEntry<>( e ) ).collect( Collectors.toSet() );

            while ( iter.hasNext() ) { // Check if iterator only returns expected entries.

                assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected entry." );

            }

            assertTrue( expected.isEmpty(), "Not all expected entries were found." ); // Ensure all expected paths were
                                                                                      // returned.

        }

        /**
         * Test for {@link Iterator#remove()}.
         */
        @Test
        @DisplayName( "iterator().remove() test" )
        public void testIteratorRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );

            Set<Tree.Entry<String, List<Integer>>> toFind = new HashSet<>( tree.entrySet() );
            Iterator<Tree.Entry<String, List<Integer>>> iter = tree.entrySet().iterator();

            // Try removing before calling next.
            assertThrows( IllegalStateException.class, () -> iter.remove(),
                    "Should throw an exception when removing before next." );

            while ( iter.hasNext() ) {

                Tree.Entry<String, List<Integer>> next = iter.next();
                assertTrue( toFind.remove( next ), "Found unexpected entry." );
                if ( next.getPath().equals( Arrays.asList( "one" ) ) ) {
                    iter.remove(); // Remove one element.
                    assertThrows( IllegalStateException.class, () -> iter.remove(),
                            "Should throw an exception when removing twice." );
                }

            }

            assertTrue( toFind.isEmpty(), "Did not iterate through all entries." ); // Ensure iterated over everything.

            assertFalse( tree.containsPath( Arrays.asList( "one" ) ), "Did not remove entry." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( path ), "Unrelated entry was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ), "Unrelated entry was removed." );

        }

        /**
         * Tests for {@link com.github.thiagotgm.bot_utils.utils.graph.Graph.Entry
         * Tree.Entry}.
         */
        @Nested
        @DisplayName( "Entry tests" )
        public class EntryTest {

            /**
             * Tests the entry methods.
             */
            @Test
            @DisplayName( "General test" )
            public void test() {

                Tree<String, List<Integer>> tree = getTree();
                tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
                tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );
                tree.put( Arrays.asList( "three" ), Arrays.asList( 1, 2, 3 ) );

                for ( Tree.Entry<String, List<Integer>> entry : tree.entrySet() ) {

                    assertEquals( 1, entry.getPath().size(), "Unexpected path returned." );
                    switch ( entry.getPath().get( 0 ) ) {

                        case "one":
                            assertEquals( Arrays.asList( 1 ), entry.getValue(), "Entry does not have expected value." );
                            break;

                        case "two":
                            assertEquals( Arrays.asList( 1, 2 ), entry.getValue(),
                                    "Entry does not have expected value." );
                            assertEquals( Arrays.asList( 1, 2 ), entry.setValue( Arrays.asList( 2 ) ),
                                    "Old value was not the expected." ); // Try setting value.
                            assertEquals( Arrays.asList( 2 ), entry.getValue(),
                                    "Entry does not have expected new value." );
                            break;

                        case "three":
                            assertEquals( Arrays.asList( 1, 2, 3 ), entry.getValue(),
                                    "Entry does not have expected value." );
                            break;

                        default:
                            fail( "Unexpected path returned." );

                    }

                }

                assertEquals( 3, tree.size(), "Tree does not have expected size." );
                assertEquals( Arrays.asList( 1 ), tree.get( Arrays.asList( "one" ) ),
                        "Entry does not have expected value." );
                assertEquals( Arrays.asList( 2 ), tree.get( Arrays.asList( "two" ) ),
                        "Entry does not have expected value." );
                assertEquals( Arrays.asList( 1, 2, 3 ), tree.get( Arrays.asList( "three" ) ),
                        "Entry does not have expected value." );

            }

        }

        /**
         * Test for {@link Set#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Tree<String, List<Integer>> tree = getTree();

            /* Test with empty tree */

            assertArrayEquals( new Object[0], tree.entrySet().toArray(),
                    "Empty tree's key set should become empty array." );

            /* Test with filled tree */

            tree.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( tree.entrySet().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.entrySet().stream()
                    .map( e -> new AbstractGraph.SimpleEntry<>( e ) ).collect( Collectors.toSet() ).toArray() );

            assertEquals( expected.size(), actual.size(), "Tree entry set array has incorrect size." );
            assertTrue( actual.containsAll( expected ), "Tree entry set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Tree<String, List<Integer>> tree = getTree();

            /* Test with empty tree */

            assertArrayEquals( new Tree.Entry[arrSize], tree.entrySet().toArray( new Tree.Entry[arrSize] ),
                    "Empty tree's entry set should become empty array." );

            /* Test with filled tree */

            tree.putAll( TEST_MAPPINGS );
            @SuppressWarnings( "unchecked" )
            Tree.Entry<String, List<Integer>>[] actual = tree.entrySet()
                    .toArray( (Tree.Entry<String, List<Integer>>[]) new Tree.Entry[arrSize] );

            assertEquals( arrSize, actual.length, "Tree entry set array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue(
                    Arrays.asList( actual )
                            .containsAll( TEST_MAPPINGS.entrySet().stream()
                                    .map( e -> new AbstractGraph.SimpleEntry<>( e ) ).collect( Collectors.toSet() ) ),
                    "Tree entry set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#add(Object)}.
         */
        @Test
        @DisplayName( "add(Tree.Entry<K,V>) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getTree().entrySet()
                            .add( new AbstractTree.SimpleEntry<>( Arrays.asList( "a" ), Arrays.asList( 1 ) ) ),
                    "Attempting to add to entry set should throw an exception." );

        }

        /**
         * Test for {@link Set#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            tree.put( Arrays.asList( "one" ), Arrays.asList( 1 ) );
            tree.put( Arrays.asList( "two" ), Arrays.asList( 1, 2 ) );

            assertTrue(
                    tree.entrySet()
                            .remove( new AbstractTree.SimpleEntry<>( Arrays.asList( "one" ), Arrays.asList( 1 ) ) ),
                    "Entry could not be removed." );

            assertFalse( tree.containsPath( Arrays.asList( "one" ) ), "Did not remove entry." ); // Check removed.

            // Check that only the right one got removed.
            for ( List<String> path : TEST_MAPPINGS.keySet() ) {

                assertTrue( tree.containsPath( path ), "Unrelated entry was removed." );

            }
            assertTrue( tree.containsPath( Arrays.asList( "two" ) ), "Unrelated entry was removed." );

        }

        /**
         * Test for {@link Set#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Set<Graph.Entry<String, List<Integer>>> testMappings = TEST_MAPPINGS.entrySet().stream()
                    .map( e -> new AbstractGraph.SimpleEntry<>( e ) ).collect( Collectors.toSet() );
            Tree<String, List<Integer>> emptyTree = new HashTree<>();
            Tree<String, List<Integer>> otherTree = new HashTree<>( TEST_MAPPINGS );
            otherTree.put( Arrays.asList( "plane" ), Arrays.asList( 99 ) );

            Tree<String, List<Integer>> tree = getTree();

            assertTrue( tree.entrySet().containsAll( emptyTree.entrySet() ), "Should contain all elements." );
            assertFalse( tree.entrySet().containsAll( testMappings ), "Should not contain all elements." );
            assertFalse( tree.entrySet().containsAll( otherTree.entrySet() ), "Should not contain all elements." );

            tree.putAll( TEST_MAPPINGS );

            assertTrue( tree.entrySet().containsAll( emptyTree.entrySet() ), "Should contain all elements." );
            assertTrue( tree.entrySet().containsAll( testMappings ), "Should contain all elements." );
            assertFalse( tree.entrySet().containsAll( otherTree.entrySet() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Set#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends Tree.Entry<K,V>>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getTree().entrySet()
                            .addAll( TEST_MAPPINGS.entrySet().stream().map( e -> new AbstractGraph.SimpleEntry<>( e ) )
                                    .collect( Collectors.toSet() ) ),
                    "Attempting to add to entry set should throw an exception." );

        }

        /**
         * Test for {@link Set#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Set<Tree.Entry<String, List<Integer>>> entries = tree.entrySet();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Tree<String, List<Integer>> toRemove = new HashTree<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Tree<String, List<Integer>> toRetain = new HashTree<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( entries.removeAll( toRemove.entrySet() ), "Should have removed something." ); // Remove.

            for ( Tree.Entry<String, List<Integer>> entry : toRemove.entrySet() ) { // Check removed.

                assertFalse( entries.contains( entry ), "One of the entries was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( entries.containsAll( toRetain.entrySet() ), "An unexpected entry was removed." );

            assertFalse( entries.removeAll( toRemove.entrySet() ), "Should have nothing to remove." ); // Try removing
                                                                                                       // again.

        }

        /**
         * Test for {@link Set#retainAll(Collection)}.
         */
        @Test
        @DisplayName( "retainAll(Collection<?>) test" )
        public void testRetainAll() {

            Tree<String, List<Integer>> tree = getTree();
            tree.putAll( TEST_MAPPINGS );
            Set<Tree.Entry<String, List<Integer>>> entries = tree.entrySet();

            Iterator<Map.Entry<List<String>, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Tree<String, List<Integer>> toRemove = new HashTree<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Tree<String, List<Integer>> toRetain = new HashTree<>();
            while ( iter.hasNext() ) {

                Map.Entry<List<String>, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( entries.retainAll( toRetain.entrySet() ), "Should have removed something." ); // Remove.

            for ( Tree.Entry<String, List<Integer>> entry : toRemove.entrySet() ) { // Check removed.

                assertFalse( entries.contains( entry ), "One of the entries was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( entries.containsAll( toRetain.entrySet() ), "An unexpected entry was removed." );

            assertFalse( entries.removeAll( toRemove.entrySet() ), "Should have nothing to remove." ); // Try removing
                                                                                                       // again.

        }

        /**
         * Test for {@link Set#clear()}.
         */
        @Test
        @DisplayName( "clear() test" )
        public void testClear() {

            Tree<String, List<Integer>> tree = getTree();

            tree.entrySet().clear();
            assertTrue( tree.isEmpty(), "Should be empty after clearing." );

            tree.putAll( TEST_MAPPINGS );

            assertFalse( tree.isEmpty(), "Should not be empty after adding." );

            tree.entrySet().clear();
            assertTrue( tree.isEmpty(), "Should be empty after clearing." );

            tree.entrySet().clear(); // Try doing it again.
            assertTrue( tree.isEmpty(), "Should still be empty after clearing twice." );

        }

        /**
         * Test for {@link Set#equals(Object)}.
         */
        @Test
        @DisplayName( "equals(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testEquals() {

            Set<Graph.Entry<String, List<Integer>>> noMappings = new HashSet<>();
            Set<Graph.Entry<String, List<Integer>>> testMappings = TEST_MAPPINGS.entrySet().stream()
                    .map( e -> new AbstractGraph.SimpleEntry<>( e ) ).collect( Collectors.toSet() );
            Set<Graph.Entry<String, List<Integer>>> otherMappings = new HashSet<>( testMappings );
            otherMappings.add( new AbstractGraph.SimpleEntry<>( Arrays.asList( "one" ), Arrays.asList( 6, 7, 6 ) ) );

            Tree<String, List<Integer>> tree = getTree();
            Set<Tree.Entry<String, List<Integer>>> entries = tree.entrySet();

            /* Test with empty tree */

            // Check itself.
            assertTrue( entries.equals( entries ) );

            // Check the tree itself.
            assertFalse( entries.equals( tree ) );
            assertFalse( tree.equals( entries ) );

            // Check right tree but the tree itself.
            assertFalse( entries.equals( new HashTree<>() ) );
            assertFalse( new HashTree<>().equals( entries ) );

            // Check non-tree.
            assertFalse( entries.equals( "str" ) );
            assertFalse( "str".equals( entries ) );

            // Check empty tree.
            assertTrue( entries.equals( noMappings ) );
            assertTrue( noMappings.equals( entries ) );

            // Check test tree.
            assertFalse( entries.equals( testMappings ) );
            assertFalse( testMappings.equals( entries ) );

            // Check other tree.
            assertFalse( entries.equals( otherMappings ) );
            assertFalse( otherMappings.equals( entries ) );

            /* Test with filled tree */

            tree.putAll( TEST_MAPPINGS );

            // Check itself.
            assertTrue( entries.equals( entries ) );

            // Check the tree itself.
            assertFalse( entries.equals( tree ) );
            assertFalse( tree.equals( entries ) );

            // Check right tree but the tree itself.
            assertFalse( entries.equals( new HashTree<>( TEST_MAPPINGS ) ) );
            assertFalse( new HashTree<>( TEST_MAPPINGS ).equals( entries ) );

            // Check non-tree.
            assertFalse( entries.equals( "str" ) );
            assertFalse( "str".equals( entries ) );

            // Check empty tree.
            assertFalse( entries.equals( noMappings ) );
            assertFalse( noMappings.equals( entries ) );

            // Check test tree.
            assertTrue( entries.equals( testMappings ) );
            assertTrue( testMappings.equals( entries ) );

            // Check other tree.
            assertFalse( entries.equals( otherMappings ) );
            assertFalse( otherMappings.equals( entries ) );

        }

        /**
         * Test for {@link Set#hashCode()}.
         */
        @Test
        @DisplayName( "hashCode() test" )
        public void testHashCode() {

            Tree<String, List<Integer>> tree = getTree();
            assertEquals( new HashMap<>().entrySet().hashCode(), tree.entrySet().hashCode(),
                    "Empty tree entry set hash code not correct." );
            tree.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.entrySet().hashCode(), tree.entrySet().hashCode(),
                    "Filled tree entry set hash code not correct." );

        }

    }

}
