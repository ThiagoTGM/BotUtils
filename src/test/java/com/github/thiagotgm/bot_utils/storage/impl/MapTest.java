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

package com.github.thiagotgm.bot_utils.storage.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.AbstractMap;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.github.thiagotgm.bot_utils.storage.Data;

/**
 * Unit tests for an implementation of the {@link Map} interface.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-20
 */
@DisplayName( "Map tests" )
public abstract class MapTest {

    /**
     * Expected mappings used in some tests.
     */
    protected static final Map<String, List<Integer>> TEST_MAPPINGS;

    static {

        Map<String, List<Integer>> testMappings = new HashMap<>();

        // Initialize map with test mappings.
        testMappings.put( "foobar", Arrays.asList( 20, 43, -10 ) );
        testMappings.put( "tryOne", Arrays.asList( 42 ) );
        testMappings.put( "Boop", Arrays.asList( -1000, 420, 0, 111 ) );
        testMappings.put( "empty", Arrays.asList() );
        testMappings.put( "maven", Arrays.asList( 2018, 9, 21, 5, 32, 30, 999 ) );
        testMappings.put( "cards", Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 1 ) );
        testMappings.put( "JUnit", Arrays.asList( 5, 13 ) );
        testMappings.put( "Magic numbers", Arrays.asList( -1, 0, 1 ) );

        TEST_MAPPINGS = Collections.unmodifiableMap( testMappings );

    }

    /**
     * Creates a blank map.
     * 
     * @return The map.
     */
    protected abstract Map<String, List<Integer>> getMap();

    /**
     * Determines whether the map accepts <tt>null</tt> keys.
     *
     * @return <tt>true</tt> if the map accepts <tt>null</tt> keys.
     */
    protected abstract boolean acceptsNullKeys();

    /**
     * Determines whether the map accepts <tt>null</tt> values.
     *
     * @return <tt>true</tt> if the map accepts <tt>null</tt> values.
     */
    protected abstract boolean acceptsNullValues();

    /**
     * Test for {@link Map#size()}.
     */
    @Test
    @DisplayName( "size() test" )
    public void testSize() {

        Map<String, List<Integer>> map = getMap();
        assertEquals( 0, map.size(), "Blank map should be empty." );

        map.put( "The key", Arrays.asList( 10 ) );
        map.put( "Two keys", Arrays.asList( 0, -2 ) );
        map.put( "Master key", Arrays.asList( 100, 404, 11 ) );
        assertEquals( 3, map.size(), "Incorrect map size after adding." );

    }

    /**
     * Test for {@link Map#isEmpty()}.
     */
    @Test
    @DisplayName( "isEmpty() test" )
    public void testIsEmpty() {

        Map<String, List<Integer>> map = getMap();
        assertTrue( map.isEmpty(), "Blank map should be empty." );

        map.put( "The key", Arrays.asList( 10 ) );
        map.put( "Two keys", Arrays.asList( 0, -2 ) );
        map.put( "Master key", Arrays.asList( 100, 404, 11 ) );
        assertFalse( map.isEmpty(), "Map after adding should not be empty." );

    }

    /**
     * Test for {@link Map#containsKey(Object)}.
     */
    @Test
    @DisplayName( "containsKey(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testContainsKey() {

        Map<String, List<Integer>> map = getMap();

        /* Test keys before inserting */

        for ( String key : TEST_MAPPINGS.keySet() ) {

            assertFalse( map.containsKey( key ), "Should not have key before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            map.put( entry.getKey(), entry.getValue() );

        }

        for ( String key : TEST_MAPPINGS.keySet() ) {

            assertTrue( map.containsKey( key ), "Should have key after inserting." );

        }

        /* Test non-existing keys */

        assertFalse( map.containsKey( new Integer( 10 ) ), "Should not have key of wrong type." );
        assertFalse( map.containsKey( "not here" ), "Should not have inexistent key." );
        assertFalse( map.containsKey( "bazinga" ), "Should not have inexistent key." );
        assertFalse( map.containsKey( "ayyy" ), "Should not have inexistent key." );

    }

    /**
     * Test for {@link Map#containsValue(Object)}.
     */
    @Test
    @DisplayName( "containsValue(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testContainsValue() {

        Map<String, List<Integer>> map = getMap();

        /* Test values before inserting */

        for ( List<Integer> value : TEST_MAPPINGS.values() ) {

            assertFalse( map.containsValue( value ), "Should not have value before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            map.put( entry.getKey(), entry.getValue() );

        }

        for ( List<Integer> value : TEST_MAPPINGS.values() ) {

            assertTrue( map.containsValue( value ), "Should have value after inserting." );

        }

        /* Test non-existing values */

        assertFalse( map.containsValue( new Integer( 10 ) ), "Should not have value of wrong type." );
        assertFalse( map.containsValue( Arrays.asList( 11 ) ), "Should not have inexistent value." );
        assertFalse( map.containsValue( Arrays.asList( 42, 11 ) ), "Should not have inexistent value." );
        assertFalse( map.containsValue( Arrays.asList( 20, 42, -10 ) ), "Should not have inexistent value." );

    }

    /**
     * Test for {@link Map#put(Object, Object)} and {@link Map#get(Object)}.
     */
    @Test
    @DisplayName( "put(K,V) and get(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testPutAndGet() {

        Map<String, List<Integer>> map = getMap();

        /* Test getting before inserting */

        for ( String key : TEST_MAPPINGS.keySet() ) {

            assertNull( map.get( key ), "Should return null before inserting." );

        }

        /* Insert and check again */

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertNull( map.put( entry.getKey(), entry.getValue() ),
                    "Inserting key for first time should return null." );

        }

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), map.get( entry.getKey() ), "Returned value does not match put value." );

        }

        /* Insert over same keys */

        int i = 0;
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), map.put( entry.getKey(), Arrays.asList( i++ ) ),
                    "Re-inserting key returned wrong old value." );

        }

        i = 0;
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( Arrays.asList( i++ ), map.get( entry.getKey() ),
                    "Returned value does not match updated value." );

        }

        /* Check inexistent keys */

        assertNull( map.get( "not here" ), "Should return null on inexistent key." );
        assertNull( map.get( "" ), "Should return null on inexistent key." );
        assertNull( map.get( new Integer( 5 ) ), "Should return null on key of wrong type." );

    }

    /**
     * Test for {@link Map#remove(Object)}.
     */
    @Test
    @DisplayName( "remove(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testRemove() {

        Map<String, List<Integer>> map = getMap();

        // Add test values
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            map.put( entry.getKey(), entry.getValue() );

        }
        String extraKey = "extra";
        List<Integer> extraValue = Arrays.asList( 1, 2, 3, 4, 5 );
        map.put( extraKey, extraValue );

        /* Try removing non-existant values */

        assertNull( map.remove( "none" ) );
        assertNull( map.remove( "I do not exist" ) );
        assertNull( map.remove( new Integer( 0 ) ) );

        // Ensure existing keys weren't changed.
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), map.get( entry.getKey() ), "Unrelated key was changed." );

        }
        assertEquals( extraValue, map.get( extraKey ), "Unrelated key was changed." );

        /* Try removing existing values */

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), map.remove( entry.getKey() ), "Wrong old value returned." );

        }

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertNull( map.get( entry.getKey() ), "Mapping was not deleted." );
            assertFalse( map.containsKey( entry.getKey() ), "Should not contain deleted key." );

        }

        // Check that unrelated values are untouched.
        assertEquals( extraValue, map.get( extraKey ), "Unrelated key was changed." );

    }

    /**
     * Test for {@link Map#put(Object, Object)}, {@link Map#get(Object)}, and
     * {@link Map#remove(Object)} on <tt>null</tt> keys and/or values. If the map
     * does not allow such keys and/or values, expects an exception on put().
     * 
     * @throws Throwable
     *             if an unexpected exception was thrown.
     */
    @Test
    @DisplayName( "null keys and values test" )
    public void testNullObjects() throws Throwable {

        Map<String, List<Integer>> map = getMap();

        Executable nullKeyTest = () -> {

            assertNull( map.put( null, Arrays.asList( 1, 2 ) ), "Inserting key for first time should return null." );
            assertTrue( map.containsKey( null ), "Should contain put key." );
            assertTrue( map.containsValue( Arrays.asList( 1, 2 ) ), "Should contain put value." );
            assertEquals( Arrays.asList( 1, 2 ), map.get( null ), "Incorrect value retrieved for put key." );
            assertEquals( Arrays.asList( 1, 2 ), map.remove( null ), "Incorrect value retrieved for remove key." );
            assertTrue( map.isEmpty(), "Map should have been cleared." );

        };

        Executable nullValueTest = () -> {

            assertNull( map.put( "null value", null ), "Inserting key for first time should return null." );
            assertTrue( map.containsKey( "null value" ), "Should contain put key." );
            assertTrue( map.containsValue( null ), "Should contain put value." );
            assertNull( map.get( "null value" ), "Incorrect value retrieved for put key." );
            assertNull( map.remove( "null value" ), "Incorrect value retrieved for remove key." );
            assertTrue( map.isEmpty(), "Map should have been cleared." );

        };

        Executable nullKeyAndValueTest = () -> {

            assertNull( map.put( null, null ), "Inserting key for first time should return null." );
            assertTrue( map.containsKey( null ), "Should contain put key." );
            assertTrue( map.containsValue( null ), "Should contain put value." );
            assertNull( map.get( null ), "Incorrect value retrieved for put key." );
            assertNull( map.remove( null ), "Incorrect value retrieved for remove key." );
            assertTrue( map.isEmpty(), "Map should have been cleared." );

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
     * Test for {@link Map#putAll(Map)}.
     */
    @Test
    @DisplayName( "putAll(Map<? extends K,? extends V>) test" )
    public void testPutAll() {

        Map<String, List<Integer>> map = getMap();

        assertTrue( map.isEmpty(), "Map should be empty at first." ); // Ensure map is empty.

        map.putAll( TEST_MAPPINGS ); // Put all mappings.

        /* Check for inserted values */

        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

            assertEquals( entry.getValue(), map.get( entry.getKey() ), "Did not put correct value." );

        }

    }

    /**
     * Test for {@link Map#clear()}.
     */
    @Test
    @DisplayName( "clear() test" )
    public void testClear() {

        Map<String, List<Integer>> map = getMap();

        assertTrue( map.isEmpty(), "Map should be empty at first." ); // Ensure map is empty.

        map.putAll( TEST_MAPPINGS ); // Insert mappings.

        assertFalse( map.isEmpty(), "Map should not be empty after insertion." ); // Check if map is now not empty.

        map.clear(); // Clear mappings.

        assertTrue( map.isEmpty(), "Map should be empty after clear." ); // Check if map is empty again.

    }

    /**
     * Test for {@link Map#equals(Object)}.
     */
    @Test
    @DisplayName( "equals(Object) test" )
    @SuppressWarnings( "unlikely-arg-type" )
    public void testEquals() {

        Map<String, List<Integer>> map = getMap();

        Map<String, List<Integer>> emptyMap = new HashMap<>();
        Map<String, List<Integer>> otherMap = new HashMap<>();
        otherMap.put( "one", Arrays.asList( 1 ) );

        /* Test while empty */

        // Check itself.
        assertTrue( map.equals( map ) );

        // Check non-map.
        assertFalse( map.equals( "str" ), "Should not be equal to non-map." );
        assertFalse( "str".equals( map ), "Should not be equal to non-map." );

        // Check empty map.
        assertTrue( map.equals( emptyMap ), "Should be equal to empty map." );
        assertTrue( emptyMap.equals( map ), "Should be equal to empty map." );

        // Check test mappings.
        assertFalse( map.equals( TEST_MAPPINGS ), "Should not be equal to test map." );
        assertFalse( TEST_MAPPINGS.equals( map ), "Should not be equal to test map." );

        // Check other map.
        assertFalse( map.equals( otherMap ), "Should not be equal to other map." );
        assertFalse( otherMap.equals( map ), "Should not be equal to other map." );

        /* Test with test mappings */

        map.putAll( TEST_MAPPINGS );

        // Check itself.
        assertTrue( map.equals( map ) );

        // Check non-map.
        assertFalse( map.equals( "str" ), "Should not be equal to non-map." );
        assertFalse( "str".equals( map ), "Should not be equal to non-map." );

        // Check empty map.
        assertFalse( map.equals( emptyMap ), "Should not be equal to empty map." );
        assertFalse( emptyMap.equals( map ), "Should not be equal to empty map." );

        // Check test mappings.
        assertTrue( map.equals( TEST_MAPPINGS ), "Should be equal to test map." );
        assertTrue( TEST_MAPPINGS.equals( map ), "Should be equal to test map." );

        // Check other map.
        assertFalse( map.equals( otherMap ), "Should not be equal to other map." );
        assertFalse( otherMap.equals( map ), "Should not be equal to other map." );

    }

    /**
     * Test for {@link Map#hashCode()}.
     */
    @Test
    @DisplayName( "hashCode() test" )
    public void testHashCode() {

        Map<String, List<Integer>> map = getMap();
        assertEquals( new HashMap<>().hashCode(), map.hashCode(), "Did not match empty hash code." );

        map.putAll( TEST_MAPPINGS );
        assertEquals( TEST_MAPPINGS.hashCode(), map.hashCode(), "Did not match filled hash code." );

    }

    /**
     * Tests for the {@link Map#keySet() key set} view.
     */
    @Nested
    @DisplayName( "Key set tests" )
    public class KeySetTest {

        /**
         * Test for {@link Set#size()}.
         */
        @Test
        @DisplayName( "size() test" )
        public void testSize() {

            Map<String, List<Integer>> map = getMap();
            assertEquals( 0, map.keySet().size(), "Wrong size of key set of empty map." );

            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), map.keySet().size(), "Wrong size of key set of filled map." );

        }

        /**
         * Test for {@link Set#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Map<String, List<Integer>> map = getMap();
            assertTrue( map.keySet().isEmpty(), "Key set of empty map should be empty." );

            map.putAll( TEST_MAPPINGS );
            assertFalse( map.keySet().isEmpty(), "Key set of filled map should not be empty." );

        }

        /**
         * Test for {@link Set#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<String> keys = map.keySet();

            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( keys.contains( key ), "Should contain key." );

            }

            assertFalse( keys.contains( new Integer( 42 ) ), "Should not contain key of wrong type." );
            assertFalse( keys.contains( "lololol" ), "Should not contain inexistent key." );
            assertFalse( keys.contains( "wroooooooong" ), "Should not contain inexistent key." );

        }

        /**
         * Test for {@link Set#iterator()}.
         */
        @Test
        @DisplayName( "iterator() test" )
        public void testIterator() {

            Map<String, List<Integer>> map = getMap();
            assertFalse( map.keySet().iterator().hasNext(), "Iterator on empty key set shouldn't have a next." );

            map.putAll( TEST_MAPPINGS );
            Iterator<String> iter = map.keySet().iterator();
            Set<String> expected = new HashSet<>( TEST_MAPPINGS.keySet() );

            while ( iter.hasNext() ) { // Check if iterator only returns expected keys.

                assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected key." );

            }

            assertTrue( expected.isEmpty(), "Not all expected keys were found." ); // Ensure all expected keys were
                                                                                   // returned.

        }

        /**
         * Test for {@link Iterator#remove()}.
         */
        @Test
        @DisplayName( "iterator().remove() test" )
        public void testIteratorRemove() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );

            Set<String> toFind = new HashSet<>( map.keySet() );
            Iterator<String> iter = map.keySet().iterator();

            // Try removing before calling next.
            assertThrows( IllegalStateException.class, () -> iter.remove(),
                    "Should throw an exception when removing before next." );

            while ( iter.hasNext() ) {

                String next = iter.next();
                assertTrue( toFind.remove( next ), "Found unexpected key." );
                if ( next.equals( "one" ) ) {
                    iter.remove(); // Remove one element.
                    assertThrows( IllegalStateException.class, () -> iter.remove(),
                            "Should throw an exception when removing twice." );
                }

            }

            assertTrue( toFind.isEmpty(), "Did not iterate through all keys." ); // Ensure iterated over everything.

            assertFalse( map.containsKey( "one" ), "Did not remove key." ); // Check removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated key was removed." );

            }
            assertTrue( map.containsKey( "two" ), "Unrelated key was removed." );

        }

        /**
         * Test for {@link Set#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new Object[0], map.keySet().toArray(),
                    "Empty map's key set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( map.keySet().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.keySet().toArray() );

            assertEquals( expected.size(), actual.size(), "Map key set array has incorrect size." );
            assertTrue( actual.containsAll( expected ), "Map key set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new String[arrSize], map.keySet().toArray( new String[arrSize] ),
                    "Empty map's key set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            String[] actual = map.keySet().toArray( new String[arrSize] );

            assertEquals( arrSize, actual.length, "Map key set array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue( Arrays.asList( actual ).containsAll( TEST_MAPPINGS.keySet() ),
                    "Map key set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#add(Object)}.
         */
        @Test
        @DisplayName( "add(K) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class, () -> getMap().keySet().add( "fail" ),
                    "Attempting to add to key set should throw an exception." );

        }

        /**
         * Test for {@link Set#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );

            assertTrue( map.keySet().remove( "one" ), "Key could not be removed." );

            assertFalse( map.containsKey( "one" ), "Did not remove key." ); // Check removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated key was removed." );

            }
            assertTrue( map.containsKey( "two" ), "Unrelated key was removed." );

        }

        /**
         * Test for {@link Set#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Map<String, List<Integer>> emptyMap = new HashMap<>();
            Map<String, List<Integer>> otherMap = new HashMap<>( TEST_MAPPINGS );
            otherMap.put( "plane", Arrays.asList( 99 ) );

            Map<String, List<Integer>> map = getMap();

            assertTrue( map.keySet().containsAll( emptyMap.keySet() ), "Should contain all elements." );
            assertFalse( map.keySet().containsAll( TEST_MAPPINGS.keySet() ), "Should not contain all elements." );
            assertFalse( map.keySet().containsAll( otherMap.keySet() ), "Should not contain all elements." );

            map.putAll( TEST_MAPPINGS );

            assertTrue( map.keySet().containsAll( emptyMap.keySet() ), "Should contain all elements." );
            assertTrue( map.keySet().containsAll( TEST_MAPPINGS.keySet() ), "Should contain all elements." );
            assertFalse( map.keySet().containsAll( otherMap.keySet() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Set#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends K>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class, () -> getMap().keySet().addAll( TEST_MAPPINGS.keySet() ),
                    "Attempting to add to key set should throw an exception." );

        }

        /**
         * Test for {@link Set#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<String> keys = map.keySet();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( keys.removeAll( toRemove.keySet() ), "Should have removed something." ); // Remove.

            for ( String key : toRemove.keySet() ) { // Check removed.

                assertFalse( keys.contains( key ), "One of the keys was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( keys.containsAll( toRetain.keySet() ), "An unexpected key was removed." );

            assertFalse( keys.removeAll( toRemove.keySet() ), "Should have nothing to remove." ); // Try removing again.

        }

        /**
         * Test for {@link Set#retainAll(Collection)}.
         */
        @Test
        @DisplayName( "retainAll(Collection<?>) test" )
        public void testRetainAll() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<String> keys = map.keySet();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( keys.retainAll( toRetain.keySet() ), "Should have removed something." );

            for ( String key : toRemove.keySet() ) { // Check removed.

                assertFalse( keys.contains( key ), "One of the keys was not removed." );

            }

            // Check nothing else was removed.
            assertTrue( keys.containsAll( toRetain.keySet() ), "An unexpected key was removed." );

            assertFalse( keys.removeAll( toRemove.keySet() ), "Should have nothing to remove." ); // Try removing again.

        }

        /**
         * Test for {@link Set#clear()}.
         */
        @Test
        @DisplayName( "clear() test" )
        public void testClear() {

            Map<String, List<Integer>> map = getMap();

            map.keySet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.putAll( TEST_MAPPINGS );

            assertFalse( map.isEmpty(), "Should not be empty after adding." );

            map.keySet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.keySet().clear(); // Try doing it again.
            assertTrue( map.isEmpty(), "Should still be empty after clearing twice." );

        }

        /**
         * Test for {@link Set#equals(Object)}.
         */
        @Test
        @DisplayName( "equals(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testEquals() {

            Map<String, Data> emptyMap = new HashMap<>();
            Map<String, Data> otherMap = new HashMap<>();
            otherMap.put( "one", Data.stringData( "haha" ) );

            Map<String, List<Integer>> map = getMap();
            Set<String> keys = map.keySet();

            /* Test with empty map */

            // Check itself.
            assertTrue( keys.equals( keys ) );

            // Check the map itself.
            assertFalse( keys.equals( map ) );
            assertFalse( map.equals( keys ) );

            // Check right map but the map itself.
            assertFalse( keys.equals( emptyMap ) );
            assertFalse( emptyMap.equals( keys ) );

            // Check non-map.
            assertFalse( keys.equals( "str" ) );
            assertFalse( "str".equals( keys ) );

            // Check empty map.
            assertTrue( keys.equals( emptyMap.keySet() ) );
            assertTrue( emptyMap.keySet().equals( keys ) );

            // Check test map.
            assertFalse( keys.equals( TEST_MAPPINGS.keySet() ) );
            assertFalse( TEST_MAPPINGS.keySet().equals( keys ) );

            // Check other map.
            assertFalse( keys.equals( otherMap.keySet() ) );
            assertFalse( otherMap.keySet().equals( keys ) );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );

            // Check itself.
            assertTrue( keys.equals( keys ) );

            // Check the map itself.
            assertFalse( keys.equals( map ) );
            assertFalse( map.equals( keys ) );

            // Check right map but the map itself.
            assertFalse( keys.equals( TEST_MAPPINGS ) );
            assertFalse( TEST_MAPPINGS.equals( keys ) );

            // Check non-map.
            assertFalse( keys.equals( "str" ) );
            assertFalse( "str".equals( keys ) );

            // Check empty map.
            assertFalse( keys.equals( emptyMap.keySet() ) );
            assertFalse( emptyMap.keySet().equals( keys ) );

            // Check test map.
            assertTrue( keys.equals( TEST_MAPPINGS.keySet() ) );
            assertTrue( TEST_MAPPINGS.keySet().equals( keys ) );

            // Check other map.
            assertFalse( keys.equals( otherMap.keySet() ) );
            assertFalse( otherMap.keySet().equals( keys ) );

        }

        /**
         * Test for {@link Set#hashCode()}.
         */
        @Test
        @DisplayName( "hashCode() test" )
        public void testHashCode() {

            Map<String, List<Integer>> map = getMap();
            assertEquals( new HashMap<>().keySet().hashCode(), map.keySet().hashCode(),
                    "Empty map key set hash code not correct." );
            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.keySet().hashCode(), map.keySet().hashCode(),
                    "Filled map key set hash code not correct." );

        }

    }

    /**
     * Tests for the {@link Map#values() value collection} view.
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

            Map<String, List<Integer>> map = getMap();
            assertEquals( 0, map.values().size(), "Wrong size of value collection of empty map." );

            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), map.values().size(), "Wrong size of value collection of filled map." );

        }

        /**
         * Test for {@link Collection#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Map<String, List<Integer>> map = getMap();
            assertTrue( map.values().isEmpty(), "Value collection of empty map should be empty." );

            map.putAll( TEST_MAPPINGS );
            assertFalse( map.values().isEmpty(), "Value collection of filled map should not be empty." );

        }

        /**
         * Test for {@link Collection#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = map.values();

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

            Map<String, List<Integer>> map = getMap();
            assertFalse( map.values().iterator().hasNext(),
                    "Iterator on empty value collection shouldn't have a next." );

            map.putAll( TEST_MAPPINGS );
            Iterator<List<Integer>> iter = map.values().iterator();
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

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );
            map.put( "two.2", Arrays.asList( 1, 2 ) );
            map.put( "three", Arrays.asList( 1, 2, 3 ) );

            Collection<List<Integer>> toFind = new ArrayList<>( map.values() );
            Iterator<List<Integer>> iter = map.values().iterator();

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

            assertFalse( map.containsKey( "two" ) && map.containsKey( "two.2" ), "Did not remove value." ); // Check
                                                                                                            // removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated value was removed." );

            }
            assertTrue( map.containsKey( "two" ) || map.containsKey( "two.2" ),
                    "Should have removed only one matching value." );
            assertTrue( map.containsKey( "one" ), "Unrelated value was removed." );
            assertTrue( map.containsKey( "three" ), "Unrelated value was removed." );

        }

        /**
         * Test for {@link Collection#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new Object[0], map.values().toArray(),
                    "Empty map's value collection should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( map.values().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.values().toArray() );

            assertEquals( expected.size(), actual.size(), "Map value collection array has incorrect size." );
            assertTrue( actual.containsAll( expected ),
                    "Map value collection array does not have all expected elements." );

        }

        /**
         * Test for {@link Collection#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new List[arrSize], map.values().toArray( new List[arrSize] ),
                    "Empty map's value collection should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            @SuppressWarnings( "unchecked" )
            List<Integer>[] actual = map.values().toArray( (List<Integer>[]) new List[arrSize] );

            assertEquals( arrSize, actual.length, "Map value collection array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue( Arrays.asList( actual ).containsAll( TEST_MAPPINGS.values() ),
                    "Map value collection array does not have all expected elements." );

        }

        /**
         * Test for {@link Collection#add(Object)}.
         */
        @Test
        @DisplayName( "add(V) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class, () -> getMap().values().add( Arrays.asList( 101 ) ),
                    "Attempting to add to value collection should throw an exception." );

        }

        /**
         * Test for {@link Collection#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );
            map.put( "two.2", Arrays.asList( 1, 2 ) );
            map.put( "three", Arrays.asList( 1, 2, 3 ) );

            assertTrue( map.values().remove( Arrays.asList( 1, 2 ) ), "Value could not be removed." );

            assertFalse( map.containsKey( "two" ) && map.containsKey( "two.2" ), "Did not remove value." ); // Check
                                                                                                            // removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated value was removed." );

            }
            assertTrue( map.containsKey( "two" ) || map.containsKey( "two.2" ),
                    "Should have removed only one matching value." );
            assertTrue( map.containsKey( "one" ), "Unrelated value was removed." );
            assertTrue( map.containsKey( "three" ), "Unrelated value was removed." );

        }

        /**
         * Test for {@link Collection#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Map<String, List<Integer>> emptyMap = new HashMap<>();
            Map<String, List<Integer>> otherMap = new HashMap<>( TEST_MAPPINGS );
            otherMap.put( "plane", Arrays.asList( 99 ) );

            Map<String, List<Integer>> map = getMap();

            assertTrue( map.values().containsAll( emptyMap.values() ), "Should contain all elements." );
            assertFalse( map.values().containsAll( TEST_MAPPINGS.values() ), "Should not contain all elements." );
            assertFalse( map.values().containsAll( otherMap.values() ), "Should not contain all elements." );

            map.putAll( TEST_MAPPINGS );

            assertTrue( map.values().containsAll( emptyMap.values() ), "Should contain all elements." );
            assertTrue( map.values().containsAll( TEST_MAPPINGS.values() ), "Should contain all elements." );
            assertFalse( map.values().containsAll( otherMap.values() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Collection#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends V>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class, () -> getMap().values().addAll( TEST_MAPPINGS.values() ),
                    "Attempting to add to value collection should throw an exception." );

        }

        /**
         * Test for {@link Collection#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = map.values();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine values to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine values to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
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

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Collection<List<Integer>> values = map.values();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine values to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine values to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
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

            Map<String, List<Integer>> map = getMap();

            map.values().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.putAll( TEST_MAPPINGS );

            assertFalse( map.isEmpty(), "Should not be empty after adding." );

            map.values().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.values().clear(); // Try doing it again.
            assertTrue( map.isEmpty(), "Should still be empty after clearing twice." );

        }

    }

    /**
     * Tests for the {@link Map#entrySet() entry set} view.
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

            Map<String, List<Integer>> map = getMap();
            assertEquals( 0, map.entrySet().size(), "Wrong size of entry set of empty map." );

            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.size(), map.entrySet().size(), "Wrong size of entry set of filled map." );

        }

        /**
         * Test for {@link Set#isEmpty()}.
         */
        @Test
        @DisplayName( "isEmpty() test" )
        public void testIsEmpty() {

            Map<String, List<Integer>> map = getMap();
            assertTrue( map.entrySet().isEmpty(), "Entry set of empty map should be empty." );

            map.putAll( TEST_MAPPINGS );
            assertFalse( map.entrySet().isEmpty(), "Entry set of filled map should not be empty." );

        }

        /**
         * Test for {@link Set#contains(Object)}.
         */
        @Test
        @DisplayName( "contains(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testContains() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<Map.Entry<String, List<Integer>>> entries = map.entrySet();

            for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {

                assertTrue( entries.contains( entry ), "Should contain entry." );

            }

            assertFalse( entries.contains( new Integer( 42 ) ), "Should not contain entry of wrong type." );
            assertFalse(
                    entries.contains(
                            new AbstractMap.SimpleEntry<String, List<Integer>>( "lololol", Arrays.asList( 0 ) ) ),
                    "Should not contain inexistent entry." );
            assertFalse( entries.contains(
                    new AbstractMap.SimpleEntry<String, List<Integer>>( "wroooooooong", Arrays.asList( -1, 0 ) ) ),
                    "Should not contain inexistent entry." );

        }

        /**
         * Test for {@link Set#iterator()}.
         */
        @Test
        @DisplayName( "iterator() test" )
        public void testIterator() {

            Map<String, List<Integer>> map = getMap();
            assertFalse( map.entrySet().iterator().hasNext(), "Iterator on empty entry set shouldn't have a next." );

            map.putAll( TEST_MAPPINGS );
            Iterator<Map.Entry<String, List<Integer>>> iter = map.entrySet().iterator();
            Set<Map.Entry<String, List<Integer>>> expected = new HashSet<>( TEST_MAPPINGS.entrySet() );

            while ( iter.hasNext() ) { // Check if iterator only returns expected keys.

                assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected entry." );

            }

            assertTrue( expected.isEmpty(), "Not all expected entries were found." ); // Ensure all expected keys were
                                                                                      // returned.

        }

        /**
         * Test for {@link Iterator#remove()}.
         */
        @Test
        @DisplayName( "iterator().remove() test" )
        public void testIteratorRemove() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );

            Set<Map.Entry<String, List<Integer>>> toFind = new HashSet<>( map.entrySet() );
            Iterator<Map.Entry<String, List<Integer>>> iter = map.entrySet().iterator();

            // Try removing before calling next.
            assertThrows( IllegalStateException.class, () -> iter.remove(),
                    "Should throw an exception when removing before next." );

            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                assertTrue( toFind.remove( next ), "Found unexpected entry." );
                if ( next.getKey().equals( "one" ) ) {
                    iter.remove(); // Remove one element.
                    assertThrows( IllegalStateException.class, () -> iter.remove(),
                            "Should throw an exception when removing twice." );
                }

            }

            assertTrue( toFind.isEmpty(), "Did not iterate through all entries." ); // Ensure iterated over everything.

            assertFalse( map.containsKey( "one" ), "Did not remove entry." ); // Check removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated entry was removed." );

            }
            assertTrue( map.containsKey( "two" ), "Unrelated entry was removed." );

        }

        /**
         * Tests for {@link java.util.Map.Entry Map.Entry}.
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

                Map<String, List<Integer>> map = getMap();
                map.put( "one", Arrays.asList( 1 ) );
                map.put( "two", Arrays.asList( 1, 2 ) );
                map.put( "three", Arrays.asList( 1, 2, 3 ) );

                for ( Map.Entry<String, List<Integer>> entry : map.entrySet() ) {

                    switch ( entry.getKey() ) {

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
                            fail( "Unexpected key returned" );

                    }

                }

                assertEquals( 3, map.size(), "Map does not have expected size." );
                assertEquals( Arrays.asList( 1 ), map.get( "one" ), "Entry does not have expected value." );
                assertEquals( Arrays.asList( 2 ), map.get( "two" ), "Entry does not have expected value." );
                assertEquals( Arrays.asList( 1, 2, 3 ), map.get( "three" ), "Entry does not have expected value." );

            }

        }

        /**
         * Test for {@link Set#toArray()}.
         */
        @Test
        @DisplayName( "toArray() test" )
        public void testToArrayObj() {

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new Object[0], map.entrySet().toArray(),
                    "Empty map's key set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            List<Object> actual = Arrays.asList( map.entrySet().toArray() );
            List<Object> expected = Arrays.asList( TEST_MAPPINGS.entrySet().toArray() );

            assertEquals( expected.size(), actual.size(), "Map entry set array has incorrect size." );
            assertTrue( actual.containsAll( expected ), "Map entry set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#toArray(Object[])}.
         */
        @Test
        @DisplayName( "toArray(T[]) test" )
        public void testToArray() {

            final int arrSize = TEST_MAPPINGS.size() + 10;

            Map<String, List<Integer>> map = getMap();

            /* Test with empty map */

            assertArrayEquals( new Map.Entry[arrSize], map.entrySet().toArray( new Map.Entry[arrSize] ),
                    "Empty map's entry set should become empty array." );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );
            @SuppressWarnings( "unchecked" )
            Map.Entry<String, List<Integer>>[] actual = map.entrySet()
                    .toArray( (Map.Entry<String, List<Integer>>[]) new Map.Entry[arrSize] );

            assertEquals( arrSize, actual.length, "Map entry set array has incorrect size." );
            assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
            actual = Arrays.copyOf( actual, TEST_MAPPINGS.size() ); // Cut off extra spaces.
            assertTrue( Arrays.asList( actual ).containsAll( TEST_MAPPINGS.entrySet() ),
                    "Map entry set array does not have all expected elements." );

        }

        /**
         * Test for {@link Set#add(Object)}.
         */
        @Test
        @DisplayName( "add(Map.Entry<K,V>) test" )
        public void testAdd() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getMap().entrySet().add( new AbstractMap.SimpleEntry<>( "a", Arrays.asList( 1 ) ) ),
                    "Attempting to add to entry set should throw an exception." );

        }

        /**
         * Test for {@link Set#remove(Object)}.
         */
        @Test
        @DisplayName( "remove(Object) test" )
        public void testRemove() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            map.put( "one", Arrays.asList( 1 ) );
            map.put( "two", Arrays.asList( 1, 2 ) );

            assertTrue( map.entrySet().remove( new AbstractMap.SimpleEntry<>( "one", Arrays.asList( 1 ) ) ),
                    "Entry could not be removed." );

            assertFalse( map.containsKey( "one" ), "Did not remove entry." ); // Check removed.

            // Check that only the right one got removed.
            for ( String key : TEST_MAPPINGS.keySet() ) {

                assertTrue( map.containsKey( key ), "Unrelated entry was removed." );

            }
            assertTrue( map.containsKey( "two" ), "Unrelated entry was removed." );

        }

        /**
         * Test for {@link Set#containsAll(Collection)}.
         */
        @Test
        @DisplayName( "containsAll(Collection<?>) test" )
        public void testContainsAll() {

            Map<String, List<Integer>> emptyMap = new HashMap<>();
            Map<String, List<Integer>> otherMap = new HashMap<>( TEST_MAPPINGS );
            otherMap.put( "plane", Arrays.asList( 99 ) );

            Map<String, List<Integer>> map = getMap();

            assertTrue( map.entrySet().containsAll( emptyMap.entrySet() ), "Should contain all elements." );
            assertFalse( map.entrySet().containsAll( TEST_MAPPINGS.entrySet() ), "Should not contain all elements." );
            assertFalse( map.entrySet().containsAll( otherMap.entrySet() ), "Should not contain all elements." );

            map.putAll( TEST_MAPPINGS );

            assertTrue( map.entrySet().containsAll( emptyMap.entrySet() ), "Should contain all elements." );
            assertTrue( map.entrySet().containsAll( TEST_MAPPINGS.entrySet() ), "Should contain all elements." );
            assertFalse( map.entrySet().containsAll( otherMap.entrySet() ), "Should not contain all elements." );

        }

        /**
         * Test for {@link Set#addAll(Collection)}.
         */
        @Test
        @DisplayName( "addAll(Collection<? extends Map.Entry<K,V>>) test" )
        public void testAddAll() {

            assertThrows( UnsupportedOperationException.class,
                    () -> getMap().entrySet().addAll( TEST_MAPPINGS.entrySet() ),
                    "Attempting to add to entry set should throw an exception." );

        }

        /**
         * Test for {@link Set#removeAll(Collection)}.
         */
        @Test
        @DisplayName( "removeAll(Collection<?>) test" )
        public void testRemoveAll() {

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<Map.Entry<String, List<Integer>>> entries = map.entrySet();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( entries.removeAll( toRemove.entrySet() ), "Should have removed something." ); // Remove.

            for ( Map.Entry<String, List<Integer>> entry : toRemove.entrySet() ) { // Check removed.

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

            Map<String, List<Integer>> map = getMap();
            map.putAll( TEST_MAPPINGS );
            Set<Map.Entry<String, List<Integer>>> entries = map.entrySet();

            Iterator<Map.Entry<String, List<Integer>>> iter = TEST_MAPPINGS.entrySet().iterator();

            /* Determine keys to remove */

            Map<String, List<Integer>> toRemove = new HashMap<>();
            for ( int i = 0; i <= TEST_MAPPINGS.size() / 2; i++ ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRemove.put( next.getKey(), next.getValue() );

            }

            /* Determine keys to maintain */

            Map<String, List<Integer>> toRetain = new HashMap<>();
            while ( iter.hasNext() ) {

                Map.Entry<String, List<Integer>> next = iter.next();
                toRetain.put( next.getKey(), next.getValue() );

            }

            /* Remove */

            assertTrue( entries.retainAll( toRetain.entrySet() ), "Should have removed something." ); // Remove.

            for ( Map.Entry<String, List<Integer>> entry : toRemove.entrySet() ) { // Check removed.

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

            Map<String, List<Integer>> map = getMap();

            map.entrySet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.putAll( TEST_MAPPINGS );

            assertFalse( map.isEmpty(), "Should not be empty after adding." );

            map.entrySet().clear();
            assertTrue( map.isEmpty(), "Should be empty after clearing." );

            map.entrySet().clear(); // Try doing it again.
            assertTrue( map.isEmpty(), "Should still be empty after clearing twice." );

        }

        /**
         * Test for {@link Set#equals(Object)}.
         */
        @Test
        @DisplayName( "equals(Object) test" )
        @SuppressWarnings( "unlikely-arg-type" )
        public void testEquals() {

            Map<String, Data> emptyMap = new HashMap<>();
            Map<String, Data> otherMap = new HashMap<>();
            otherMap.put( "one", Data.stringData( "haha" ) );

            Map<String, List<Integer>> map = getMap();
            Set<Map.Entry<String, List<Integer>>> entries = map.entrySet();

            /* Test with empty map */

            // Check itself.
            assertTrue( entries.equals( entries ) );

            // Check the map itself.
            assertFalse( entries.equals( map ) );
            assertFalse( map.equals( entries ) );

            // Check right map but the map itself.
            assertFalse( entries.equals( emptyMap ) );
            assertFalse( emptyMap.equals( entries ) );

            // Check non-map.
            assertFalse( entries.equals( "str" ) );
            assertFalse( "str".equals( entries ) );

            // Check empty map.
            assertTrue( entries.equals( emptyMap.entrySet() ) );
            assertTrue( emptyMap.entrySet().equals( entries ) );

            // Check test map.
            assertFalse( entries.equals( TEST_MAPPINGS.entrySet() ) );
            assertFalse( TEST_MAPPINGS.entrySet().equals( entries ) );

            // Check other map.
            assertFalse( entries.equals( otherMap.entrySet() ) );
            assertFalse( otherMap.entrySet().equals( entries ) );

            /* Test with filled map */

            map.putAll( TEST_MAPPINGS );

            // Check itself.
            assertTrue( entries.equals( entries ) );

            // Check the map itself.
            assertFalse( entries.equals( map ) );
            assertFalse( map.equals( entries ) );

            // Check right map but the map itself.
            assertFalse( entries.equals( TEST_MAPPINGS ) );
            assertFalse( TEST_MAPPINGS.equals( entries ) );

            // Check non-map.
            assertFalse( entries.equals( "str" ) );
            assertFalse( "str".equals( entries ) );

            // Check empty map.
            assertFalse( entries.equals( emptyMap.entrySet() ) );
            assertFalse( emptyMap.entrySet().equals( entries ) );

            // Check test map.
            assertTrue( entries.equals( TEST_MAPPINGS.entrySet() ) );
            assertTrue( TEST_MAPPINGS.entrySet().equals( entries ) );

            // Check other map.
            assertFalse( entries.equals( otherMap.entrySet() ) );
            assertFalse( otherMap.entrySet().equals( entries ) );

        }

        /**
         * Test for {@link Set#hashCode()}.
         */
        @Test
        @DisplayName( "hashCode() test" )
        public void testHashCode() {

            Map<String, List<Integer>> map = getMap();
            assertEquals( new HashMap<>().entrySet().hashCode(), map.entrySet().hashCode(),
                    "Empty map entry set hash code not correct." );
            map.putAll( TEST_MAPPINGS );
            assertEquals( TEST_MAPPINGS.entrySet().hashCode(), map.entrySet().hashCode(),
                    "Filled map entry set hash code not correct." );

        }

    }

}
