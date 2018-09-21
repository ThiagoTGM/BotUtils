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
import static org.junit.jupiter.api.Assumptions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.github.thiagotgm.bot_utils.storage.Data;
import com.github.thiagotgm.bot_utils.storage.Translator;
import com.github.thiagotgm.bot_utils.storage.impl.DynamoDBDatabase;
import com.github.thiagotgm.bot_utils.storage.translate.DataTranslator;
import com.github.thiagotgm.bot_utils.storage.translate.StringTranslator;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;

/**
 * Unit tests for an implementation of the {@link Map} interface.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-20
 */
public interface MapTest {

    static final Map<String, List<Integer>> TEST_MAPPINGS = new HashMap<>();

    /**
     * Initializes the map with test mappings.
     */
    @BeforeAll
    static void setUpTestMappings() {

        TEST_MAPPINGS.put( "foobar", Arrays.asList( 20, 43, -10 ) );
        TEST_MAPPINGS.put( "tryOne", Arrays.asList( 42 ) );
        TEST_MAPPINGS.put( "Boop", Arrays.asList( -1000, 420, 0, 111 ) );
        TEST_MAPPINGS.put( "empty", Arrays.asList() );

    }

    /**
     * Creates a blank map.
     * 
     * @return The map.
     */
    Map<String, List<Integer>> getMap();
    
    /**
     * Determines whether the map accepts <tt>null</tt> keys.
     *
     * @return <tt>true</tt> if the map accepts <tt>null</tt> keys.
     */
    boolean acceptsNullKeys();
    
    /**
     * Determines whether the map accepts <tt>null</tt> values.
     *
     * @return <tt>true</tt> if the map accepts <tt>null</tt> values.
     */
    boolean acceptsNullValues();

    /**
     * Test for {@link Map#size()}.
     */
    @Test
    default void testSize() {

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
    default void testIsEmpty() {

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
    @SuppressWarnings( "unlikely-arg-type" )
    default void testContainsKey() {
        
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
    @SuppressWarnings( "unlikely-arg-type" )
    default void testContainsValue() {
        
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
    @SuppressWarnings( "unlikely-arg-type" )
    default void testPutAndGet() {
        
        Map<String, List<Integer>> map = getMap();

        /* Test getting before inserting */
        
        for ( String key : TEST_MAPPINGS.keySet() ) {
            
            assertNull( map.get( key ), "Should return null before inserting." );
            
        }
        
        /* Insert and check again */
        
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            
            assertNull( map.put( entry.getKey(), entry.getValue() ), "Inserting key for first time should return null." );
            
        }
        
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            
            assertEquals( entry.getValue(), map.get( entry.getKey() ), "Returned value does not match put value." );
            
        }
        
        /* Insert over same keys */
        
        int i = 0;
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            
            assertEquals( entry.getValue(), map.put( entry.getKey(), Arrays.asList( i++ ) ), "Re-inserting key returned wrong old value." );
            
        }
        
        i = 0;
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            
            assertEquals( Arrays.asList( i++ ), map.get( entry.getKey() ), "Returned value does not match updated value." );
            
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
    @SuppressWarnings( "unlikely-arg-type" )
    default void testRemove() {

        Map<String, List<Integer>> map = getMap();
        
        // Add test values
        for ( Map.Entry<String, List<Integer>> entry : TEST_MAPPINGS.entrySet() ) {
            
            map.put( entry.getKey(), entry.getValue() );
            
        }
        String extraKey = "extra";
        List<Integer> extraValue = Arrays.asList( 1, 2 , 3, 4, 5 );
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
     * Test for {@link Map#put(Object, Object)}, {@link Map#get(Object)}, and {@link Map#remove(Object)}
     * on <tt>null</tt> keys and/or values. If the map does not allow such keys and/or values, expects an
     * exception on put().
     * 
     * @throws Throwable if an unexpected exception was thrown.
     */
    @Test
    default void testNullObjects() throws Throwable {

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
            assertThrows( NullPointerException.class, nullKeyAndValueTest, "Null keys and values should throw an exception." );
        }

    }

    /**
     * Test for {@link Map#putAll(Map)}.
     */
    @Test
    default void testPutAll() {

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
    default void testClear() {

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
    @SuppressWarnings( "unlikely-arg-type" )
    default void testEquals() {
        
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
    default void testHashCode() {
        
        Map<String, List<Integer>> map = getMap();
        assertEquals( new HashMap<>().hashCode(), map.hashCode(), "Did not match empty hash code." );

        map.putAll( TEST_MAPPINGS );
        assertEquals( TEST_MAPPINGS.hashCode(), map.hashCode(), "Did not match filled hash code." );

    }

    /* Tests for key set view */

    /**
     * Test for {@link Set#size()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetSize() {
        
        Map<String, List<Integer>> map = getMap();
        assertEquals( 0, map.keySet().size(), "Wrong size of key set of empty map." );

        map.putAll( TEST_MAPPINGS );
        assertEquals( TEST_MAPPINGS.size(), map.keySet().size(), "Wrong size of key set of filled map." );

    }

    /**
     * Test for {@link Set#isEmpty()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetIsEmpty() {
        
        Map<String, List<Integer>> map = getMap();
        assertTrue( map.keySet().isEmpty(), "Key set of empty map should be empty." );

        map.putAll( TEST_MAPPINGS );
        assertFalse( map.keySet().isEmpty(), "Key set of filled map should not be empty." );

    }

    /**
     * Test for {@link Set#contains(Object)} of {@link Map#keySet()}.
     */
    @Test
    @SuppressWarnings( "unlikely-arg-type" )
    default void testKeySetContains() {

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
     * Test for {@link Set#iterator()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetIterator() {

        Map<String, List<Integer>> map = getMap();
        assertFalse( map.keySet().iterator().hasNext(), "Iterator on empty key set shouldn't have a next." );
        
        map.putAll( TEST_MAPPINGS );
        Iterator<String> iter = map.keySet().iterator();
        Set<String> expected = new HashSet<>( TEST_MAPPINGS.keySet() );

        while ( iter.hasNext() ) { // Check if iterator only returns expected values.

            assertTrue( expected.remove( iter.next() ), "Iterator returned unexpected value." );

        }

        assertTrue( expected.isEmpty(), "Not all expected values were found." ); // Ensure all expected values were returned.


    }
    
    /**
     * Test for {@link Iterator#remove()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetIteratorRemove() {

        Map<String, List<Integer>> map = getMap();
        map.putAll( TEST_MAPPINGS );
        map.put( "one", Arrays.asList( 1 ) );
        map.put( "two", Arrays.asList( 1, 2 ) );
        
        Set<String> toFind = new HashSet<>( map.keySet() );
        Iterator<String> iter = map.keySet().iterator();

        // Try removing before calling next.
        assertThrows( IllegalStateException.class, () -> iter.remove(), "Should throw an exception when removing before next." );

        while ( iter.hasNext() ) {

            String next = iter.next();
            assertTrue( toFind.remove( next ), "Found unexpected key." );
            if ( next.equals( "one" ) ) {
                iter.remove(); // Remove one element.
                assertThrows( IllegalStateException.class, () -> iter.remove(), "Should throw an exception when removing twice." );
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
     * Test for {@link Set#toArray()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetToArrayObj() {
        
        Map<String, List<Integer>> map = getMap();
        
        /* Test with empty map */

        assertArrayEquals( new Object[0], map.keySet().toArray(), "Empty map's key set should become empty array." );

        /* Test with filled map */

        map.putAll( TEST_MAPPINGS );
        List<Object> actual = Arrays.asList( map.keySet().toArray() );
        List<Object> expected = Arrays.asList( TEST_MAPPINGS.keySet().toArray() );

        assertEquals( expected.size(), actual.size(), "Map key set array has incorrect size." );
        assertTrue( actual.containsAll( expected ), "Map key set array does not have all expected elements." );

    }

    /**
     * Test for {@link Set#toArray(Object[])} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetToArray() {

        final int arrSize = TEST_MAPPINGS.size() + 10;
        
        Map<String, List<Integer>> map = getMap();
        
        /* Test with empty map */

        assertArrayEquals( new String[arrSize], map.keySet().toArray( new String[arrSize] ), "Empty map's key set should become empty array." );

        /* Test with filled map */

        map.putAll( TEST_MAPPINGS );
        String[] actual = map.keySet().toArray( new String[arrSize] );
        List<String> expected = Arrays.asList( TEST_MAPPINGS.keySet().toArray( new String[arrSize] ) );
        
        assertEquals( arrSize, actual.length, "Map key set array has incorrect size." );
        assertEquals( null, actual[TEST_MAPPINGS.size()], "Element after last in array should be null." );
        assertTrue( Arrays.asList( actual ).containsAll( expected ), "Map key set array does not have all expected elements." );

    }

    /**
     * Test for {@link Set#add(Object)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetAdd() {

        assertThrows( UnsupportedOperationException.class, () -> getMap().keySet().add( "fail" ), "Attempting to add to key set should throw an exception." );

    }
    
    /**
     * Test for {@link Set#remove(Object)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetRemove() {

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
     * Test for {@link Set#containsAll(Collection)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetContainsAll() {
        
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
     * Test for {@link Set#addAll(Collection)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetAddAll() {

        assertThrows( UnsupportedOperationException.class,
                () -> getMap().keySet().addAll( TEST_MAPPINGS.keySet() ), "Attempting to add to key set should throw an exception." );

    }

    /**
     * Test for {@link Set#removeAll(Collection)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetRemoveAll() {

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
     * Test for {@link Set#retainAll(Collection)} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetRetainAll() {

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
     * Test for {@link Set#clear()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetClear() {

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
     * Test for {@link Set#equals(Object)} of {@link Map#keySet()}.
     */
    @Test
    @SuppressWarnings( "unlikely-arg-type" )
    default void testKeySetEquals() {
        
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
     * Test for {@link Set#hashCode()} of {@link Map#keySet()}.
     */
    @Test
    default void testKeySetHashCode() {

        Map<String, List<Integer>> map = getMap();
        assertEquals( new HashMap<>().keySet().hashCode(), map.keySet().hashCode() );
        map.putAll( TEST_MAPPINGS );
        assertEquals( TEST_MAPPINGS.keySet().hashCode(), map.keySet().hashCode() );

    }

    /* Tests for value collection view /

    @Test
    default void testValueCollectionSize() {

        assertEquals( TEST_DB_MAPPINGS.size(), map.values().size() );
        assertEquals( 0, getTempTable().values().size() );

    }

    @Test
    default void testValueCollectionIsEmpty() {

        assertFalse( map.values().isEmpty() );
        assertTrue( getTempTable().values().isEmpty() );

    }

    @Test
    @SuppressWarnings( "unlikely-arg-type" )
    default void testValueCollectionContains() {

        Collection<Data> values = map.values();

        for ( Data value : TEST_DB_MAPPINGS.values() ) {

            assertTrue( values.contains( value ) );

        }

        assertFalse( values.contains( null ) );
        assertFalse( values.contains( new Integer( 42 ) ) );
        assertFalse( values.contains( Data.nullData() ) );
        assertFalse( values.contains( Data.stringData( "thinkTak" ) ) );

    }

    @Test
    default void testValueCollectionIterator() {

        Iterator<Data> iter = map.values().iterator();
        Collection<Data> expected = new ArrayList<>( TEST_DB_MAPPINGS.values() );

        while ( iter.hasNext() ) { // Check if iterator only returns expected values.

            assertTrue( expected.remove( iter.next() ) );

        }

        assertTrue( expected.isEmpty() ); // Ensure all expected values were returned.

        assertFalse( getTempTable().values().iterator().hasNext() );

    }

    @Test
    default void testValueCollectionIteratorRemove() {

        Map<String, Data> map = getTempTable();

        map.put( "one", Data.numberData( 1 ) );
        map.put( "two", Data.numberData( 2 ) );
        map.put( "two.2", Data.numberData( 2 ) );
        map.put( "three", Data.numberData( 3 ) );

        Collection<Data> toFind = new ArrayList<>( map.values() );

        boolean shouldDelete = true;
        Iterator<Data> iter = map.values().iterator();

        try { // Try removing before calling next.
            iter.remove();
            fail( "Should have thrown an exception." );
        } catch ( IllegalStateException e ) {
            // Normal.
        }

        while ( iter.hasNext() ) {

            Data next = iter.next();
            toFind.remove( next );
            if ( shouldDelete && next.equals( Data.numberData( 2 ) ) ) {
                iter.remove(); // Remove one of 2 possible elements.
                shouldDelete = false;

                try { // Try removing twice.
                    iter.remove();
                    fail( "Should have thrown an exception." );
                } catch ( IllegalStateException e ) {
                    // Normal.
                }
            }

        }

        assertTrue( toFind.isEmpty() ); // Ensure iterated over everything.

        assertTrue( map.containsKey( "one" ) ); // Check that exactly one of the possible elements
        assertFalse( map.containsKey( "two" ) && map.containsKey( "two.2" ) ); // got removed, and
        assertTrue( map.containsKey( "two" ) || map.containsKey( "two.2" ) ); // nothing else.
        assertTrue( map.containsKey( "three" ) );

    }

    @Test
    default void testValueCollectionToArrayObj() {

        List<Object> expected = Arrays.asList( TEST_DB_MAPPINGS.values().toArray() );
        List<Object> actual = Arrays.asList( map.values().toArray() );

        assertEquals( expected.size(), actual.size() );
        assertFalse( expected.retainAll( actual ) );

        /* Test with empty map /

        expected = Arrays.asList( new HashMap<>().values().toArray() );
        actual = Arrays.asList( getTempTable().values().toArray() );

        assertEquals( expected.size(), actual.size() );
        assertFalse( expected.retainAll( actual ) );

        assertTrue( Arrays.deepEquals( getTempTable().values().toArray(), new HashMap<>().values().toArray() ) );

    }

    @Test
    default void testValueCollectionToArray() {

        List<Object> expected = Arrays.asList( TEST_DB_MAPPINGS.values().toArray( new Object[15] ) );
        List<Object> actual = Arrays.asList( map.values().toArray( new Object[15] ) );
        int size = TEST_DB_MAPPINGS.size();

        assertEquals( expected.size(), actual.size() );
        assertEquals( expected.subList( size, expected.size() ), actual.subList( size, expected.size() ) );
        assertFalse( expected.retainAll( actual ) );

        /* Test with empty map /

        expected = Arrays.asList( new HashMap<>().values().toArray( new Object[15] ) );
        actual = Arrays.asList( getTempTable().values().toArray( new Object[15] ) );
        size = 0;

        assertEquals( expected.size(), actual.size() );
        assertEquals( expected.subList( size, expected.size() ), actual.subList( size, expected.size() ) );
        assertFalse( expected.retainAll( actual ) );

    }

    @Test
    default void testValueCollectionAdd() {

        assertThrows( UnsupportedOperationException.class, () -> getTempTable().keySet().add( "fail" ) );

    }

    @Test
    default void testValueCollectionRemove() {

        Map<String, Data> map = getTempTable();

        map.put( "testing 1", Data.stringData( "toRemove" ) );
        map.put( "testing 2", Data.nullData() );
        map.put( "testing 2.5", Data.nullData() );
        map.put( "testing 3", Data.numberData( 5 ) );

        assertTrue( map.values().remove( Data.nullData() ) );

        assertEquals( Data.stringData( "toRemove" ), map.get( "testing 1" ) );
        assertTrue( ( map.get( "testing 2" ) == null ) || // Ensure exactly one got removed.
                ( map.get( "testing 2.5" ) == null ) );
        assertTrue( Data.nullData().equals( map.get( "testing 2" ) )
                || Data.nullData().equals( map.get( "testing 2.5" ) ) );
        assertEquals( Data.numberData( 5 ), map.get( "testing 3" ) );

    }

    @Test
    default void testValueCollectionContainsAll() {

        assertTrue( map.values().containsAll( TEST_DB_MAPPINGS.values() ) );

        Map<String, Data> otherMap = new HashMap<>( TEST_DB_MAPPINGS );
        otherMap.put( "plane", Data.booleanData( false ) );

        assertFalse( map.values().containsAll( otherMap.values() ) );

        otherMap = new HashMap<>( TEST_DB_MAPPINGS );
        otherMap.put( "plane", map.values().iterator().next() );

        assertTrue( map.values().containsAll( otherMap.values() ) );

    }

    @Test
    default void testValueCollectionAddAll() {

        assertThrows( UnsupportedOperationException.class,
                () -> getTempTable().values().addAll( TEST_DB_MAPPINGS.values() ) );

    }

    @Test
    default void testValueCollectionRemoveAll() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );
        Collection<Data> values = map.values();

        Iterator<Map.Entry<String, Data>> iter = TEST_DB_MAPPINGS.entrySet().iterator();

        Map<String, Data> toRemove = new HashMap<>();
        for ( int i = 0; i <= TEST_DB_MAPPINGS.size() / 2; i++ ) {

            Map.Entry<String, Data> next = iter.next();
            toRemove.put( next.getKey(), next.getValue() );

        }

        Map<String, Data> toRetain = new HashMap<>();
        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            toRetain.put( next.getKey(), next.getValue() );

        }

        assertTrue( values.removeAll( toRemove.values() ) ); // Remove.

        for ( Data value : toRemove.values() ) { // Check removed.

            assertFalse( values.contains( value ) );

        }

        // Check nothing else was removed.
        assertTrue( values.containsAll( toRetain.values() ) );

        assertFalse( values.removeAll( toRemove.values() ) ); // Try removing again.

    }

    @Test
    default void testValueCollectionRetainAll() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );
        Collection<Data> values = map.values();

        Iterator<Map.Entry<String, Data>> iter = TEST_DB_MAPPINGS.entrySet().iterator();

        Map<String, Data> toRemove = new HashMap<>();
        for ( int i = 0; i <= TEST_DB_MAPPINGS.size() / 2; i++ ) {

            Map.Entry<String, Data> next = iter.next();
            toRemove.put( next.getKey(), next.getValue() );

        }

        Map<String, Data> toRetain = new HashMap<>();
        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            toRetain.put( next.getKey(), next.getValue() );

        }

        assertTrue( values.retainAll( toRetain.values() ) ); // Remove.

        for ( Data value : toRemove.values() ) { // Check removed.

            assertFalse( values.contains( value ) );

        }

        // Check nothing else was removed.
        assertTrue( values.containsAll( toRetain.values() ) );

        assertFalse( values.removeAll( toRemove.values() ) ); // Try removing again.

    }

    @Test
    default void testValueCollectionClear() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );

        assertFalse( map.isEmpty() );

        map.values().clear();

        assertTrue( map.isEmpty() );

    }

    /* Tests for entry set view /

    @Test
    default void testEntrySetSize() {

        assertEquals( TEST_DB_MAPPINGS.size(), map.entrySet().size() );
        assertEquals( 0, getTempTable().entrySet().size() );

    }

    @Test
    default void testEntrySetIsEmpty() {

        assertFalse( map.entrySet().isEmpty() );
        assertTrue( getTempTable().entrySet().isEmpty() );

    }

    @Test
    @SuppressWarnings( "unlikely-arg-type" )
    default void testEntrySetContains() {

        Set<Map.Entry<String, Data>> entries = map.entrySet();

        for ( Map.Entry<String, Data> entry : TEST_DB_MAPPINGS.entrySet() ) {

            assertTrue( entries.contains( entry ) );

        }

        assertFalse( entries.contains( null ) );
        assertFalse( entries.contains( new Integer( 42 ) ) );
        assertFalse( entries.contains( "lololol" ) );

        Map<String, Data> otherMap = new HashMap<>();
        otherMap.put( "wolololo", Data.nullData() );
        otherMap.put( "trollface", Data.numberData( "4.999" ) );

        for ( Map.Entry<String, Data> entry : otherMap.entrySet() ) {

            assertFalse( entries.contains( entry ) );

        }

    }

    @Test
    default void testEntrySetIterator() {

        Iterator<Map.Entry<String, Data>> iter = map.entrySet().iterator();
        Set<Map.Entry<String, Data>> expected = new HashSet<>( TEST_DB_MAPPINGS.entrySet() );

        while ( iter.hasNext() ) { // Check if iterator only returns expected values.

            assertTrue( expected.remove( iter.next() ) );

        }

        assertTrue( expected.isEmpty() ); // Ensure all expected values were returned.

        assertFalse( getTempTable().entrySet().iterator().hasNext() );

    }

    @Test
    default void testEntrySetIteratorRemove() {

        Map<String, Data> map = getTempTable();

        map.put( "one", Data.numberData( 1 ) );
        map.put( "two", Data.numberData( 2 ) );
        map.put( "three", Data.numberData( 3 ) );

        Set<Map.Entry<String, Data>> toFind = new HashSet<>( map.entrySet() );
        Iterator<Map.Entry<String, Data>> iter = map.entrySet().iterator();

        try { // Try removing before calling next.
            iter.remove();
            fail( "Should have thrown an exception." );
        } catch ( IllegalStateException e ) {
            // Normal.
        }

        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            toFind.remove( next );
            if ( next.getKey().equals( "two" ) ) {
                iter.remove(); // Remove one element.

                try { // Try removing twice.
                    iter.remove();
                    fail( "Should have thrown an exception." );
                } catch ( IllegalStateException e ) {
                    // Normal.
                }
            }

        }

        assertTrue( toFind.isEmpty() ); // Ensure iterated over everything.

        assertTrue( map.containsKey( "one" ) ); // Check that only the right one
        assertFalse( map.containsKey( "two" ) ); // got removed.
        assertTrue( map.containsKey( "three" ) );

    }

    @Test
    default void testEntry() {

        Map<String, Data> map = getTempTable();
        map.put( "one", Data.numberData( 1 ) );
        map.put( "two", Data.numberData( 2 ) );
        map.put( "three", Data.numberData( 3 ) );

        for ( Map.Entry<String, Data> entry : map.entrySet() ) {

            switch ( entry.getKey() ) {

                case "one":
                    assertEquals( Data.numberData( 1 ), entry.getValue() );
                    break;

                case "two":
                    assertEquals( Data.numberData( 2 ), entry.getValue() );
                    assertEquals( Data.numberData( 2 ), entry.setValue( Data.numberData( 4 ) ) ); // Try setting value.
                    assertEquals( Data.numberData( 4 ), entry.getValue() );
                    break;

                case "three":
                    assertEquals( Data.numberData( 3 ), entry.getValue() );
                    break;

                default:
                    fail( "Unexpected key returned" );

            }

        }

        assertEquals( 3, map.size() );
        assertEquals( Data.numberData( 1 ), map.get( "one" ) );
        assertEquals( Data.numberData( 4 ), map.get( "two" ) );
        assertEquals( Data.numberData( 3 ), map.get( "three" ) );

    }

    @Test
    default void testEntrySetToArrayObj() {

        List<Object> expected = Arrays.asList( TEST_DB_MAPPINGS.entrySet().toArray() );
        List<Object> actual = Arrays.asList( map.entrySet().toArray() );

        assertEquals( expected.size(), actual.size() );
        assertFalse( expected.retainAll( actual ) );

        /* Test with empty map /

        expected = Arrays.asList( new HashMap<>().entrySet().toArray() );
        actual = Arrays.asList( getTempTable().entrySet().toArray() );

        assertEquals( expected.size(), actual.size() );
        assertFalse( expected.retainAll( actual ) );

        assertTrue( Arrays.deepEquals( getTempTable().entrySet().toArray(), new HashMap<>().entrySet().toArray() ) );

    }

    @Test
    default void testEntrySetToArray() {

        List<Object> expected = Arrays.asList( TEST_DB_MAPPINGS.entrySet().toArray( new Object[15] ) );
        List<Object> actual = Arrays.asList( map.entrySet().toArray( new Object[15] ) );
        int size = TEST_DB_MAPPINGS.size();

        assertEquals( expected.size(), actual.size() );
        assertEquals( expected.subList( size, expected.size() ), actual.subList( size, expected.size() ) );
        assertFalse( expected.retainAll( actual ) );

        /* Test with empty map /

        expected = Arrays.asList( new HashMap<>().entrySet().toArray( new Object[15] ) );
        actual = Arrays.asList( getTempTable().entrySet().toArray( new Object[15] ) );
        size = 0;

        assertEquals( expected.size(), actual.size() );
        assertEquals( expected.subList( size, expected.size() ), actual.subList( size, expected.size() ) );
        assertFalse( expected.retainAll( actual ) );

    }

    @Test
    default void testEntrySetAdd() {

        assertThrows( UnsupportedOperationException.class, () -> getTempTable().keySet().add( "fail" ) );

    }

    @Test
    default void testEntrySetRemove() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );

        Iterator<Map.Entry<String, Data>> iter = TEST_DB_MAPPINGS.entrySet().iterator();
        Map.Entry<String, Data> toDelete = iter.next(); // Get a mapping to delete.

        assertTrue( map.entrySet().remove( toDelete ) ); // Delete the mapping.

        assertFalse( map.containsKey( toDelete.getKey() ) );

        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            assertEquals( next.getValue(), map.get( next.getKey() ) );

        }

        assertFalse( map.entrySet().remove( toDelete ) ); // Try deleting twice.

        map.put( "aTest", Data.numberData( -54 ) );

        Map<String, Data> otherMap = new HashMap<>();
        otherMap.put( "aTest", Data.nullData() );

        // Try entry with the right key but wrong value.
        assertFalse( map.entrySet().remove( otherMap.entrySet().iterator().next() ) );

        // Now with right value.
        otherMap.put( "aTest", Data.numberData( -54 ) );
        assertTrue( map.entrySet().remove( otherMap.entrySet().iterator().next() ) );

        assertFalse( map.containsKey( "aTest" ) );

        assertEquals( TEST_DB_MAPPINGS.size() - 1, map.size() );

    }

    @Test
    default void testEntrySetContainsAll() {

        assertTrue( map.entrySet().containsAll( TEST_DB_MAPPINGS.entrySet() ) );

        Map<String, Data> otherMap = new HashMap<>( TEST_DB_MAPPINGS );
        otherMap.put( "plane", Data.booleanData( false ) );

        assertFalse( map.entrySet().containsAll( otherMap.entrySet() ) );

    }

    @Test
    default void testEntrySetAddAll() {

        assertThrows( UnsupportedOperationException.class,
                () -> getTempTable().entrySet().addAll( TEST_DB_MAPPINGS.entrySet() ) );

    }

    @Test
    default void testEntrySetRemoveAll() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );
        Set<Map.Entry<String, Data>> entries = map.entrySet();

        Iterator<Map.Entry<String, Data>> iter = TEST_DB_MAPPINGS.entrySet().iterator();

        Map<String, Data> toRemove = new HashMap<>();
        for ( int i = 0; i <= TEST_DB_MAPPINGS.size() / 2; i++ ) {

            Map.Entry<String, Data> next = iter.next();
            toRemove.put( next.getKey(), next.getValue() );

        }

        Map<String, Data> toRetain = new HashMap<>();
        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            toRetain.put( next.getKey(), next.getValue() );

        }

        assertTrue( entries.removeAll( toRemove.entrySet() ) ); // Remove.

        for ( Map.Entry<String, Data> entry : toRemove.entrySet() ) { // Check removed.

            assertFalse( entries.contains( entry ) );

        }

        // Check nothing else was removed.
        assertTrue( entries.containsAll( toRetain.entrySet() ) );

        assertFalse( entries.removeAll( toRemove.entrySet() ) ); // Try removing again.

    }

    @Test
    default void testEntrySetRetainAll() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );
        Set<Map.Entry<String, Data>> entries = map.entrySet();

        Iterator<Map.Entry<String, Data>> iter = TEST_DB_MAPPINGS.entrySet().iterator();

        Map<String, Data> toRemove = new HashMap<>();
        for ( int i = 0; i <= TEST_DB_MAPPINGS.size() / 2; i++ ) {

            Map.Entry<String, Data> next = iter.next();
            toRemove.put( next.getKey(), next.getValue() );

        }

        Map<String, Data> toRetain = new HashMap<>();
        while ( iter.hasNext() ) {

            Map.Entry<String, Data> next = iter.next();
            toRetain.put( next.getKey(), next.getValue() );

        }

        assertTrue( entries.retainAll( toRetain.entrySet() ) ); // Remove.

        for ( Map.Entry<String, Data> entry : toRemove.entrySet() ) { // Check removed.

            assertFalse( entries.contains( entry ) );

        }

        // Check nothing else was removed.
        assertTrue( entries.containsAll( toRetain.entrySet() ) );

        assertFalse( entries.removeAll( toRemove.entrySet() ) ); // Try removing again.

    }

    @Test
    default void testEntrySetClear() {

        Map<String, Data> map = getTempTable();
        map.putAll( TEST_DB_MAPPINGS );

        assertFalse( map.isEmpty() );

        map.entrySet().clear();

        assertTrue( map.isEmpty() );

    }

    @Test
    @SuppressWarnings( "unlikely-arg-type" )
    default void testEntrySetEquals() {

        Set<Map.Entry<String, Data>> entries = map.entrySet();

        // Check correct map.
        assertTrue( entries.equals( TEST_DB_MAPPINGS.entrySet() ) );
        assertTrue( TEST_DB_MAPPINGS.entrySet().equals( entries ) );

        // Check correct map but the map itself.
        assertFalse( entries.equals( TEST_DB_MAPPINGS ) );
        assertFalse( TEST_DB_MAPPINGS.equals( entries ) );

        // Check non-map.
        assertFalse( entries.equals( "str" ) );
        assertFalse( "str".equals( entries ) );

        // Check empty map.
        Map<String, Data> emptyMap = new HashMap<>();

        assertFalse( entries.equals( emptyMap.entrySet() ) );
        assertFalse( emptyMap.entrySet().equals( entries ) );

        // Check other map.
        Map<String, Data> otherMap = new HashMap<>();
        otherMap.put( "one", Data.stringData( "haha" ) );

        assertFalse( entries.equals( otherMap.entrySet() ) );
        assertFalse( otherMap.entrySet().equals( entries ) );

        /* Test with empty map /

        entries = getTempTable().entrySet();

        // Check correct map.
        assertTrue( entries.equals( emptyMap.entrySet() ) );
        assertTrue( emptyMap.entrySet().equals( entries ) );

        // Check correct map but the map itself.
        assertFalse( entries.equals( emptyMap ) );
        assertFalse( emptyMap.equals( entries ) );

        // Check non-map.
        assertFalse( entries.equals( "str" ) );
        assertFalse( "str".equals( entries ) );

        // Check wrong map.
        assertFalse( entries.equals( TEST_DB_MAPPINGS.entrySet() ) );
        assertFalse( TEST_DB_MAPPINGS.entrySet().equals( entries ) );

        // Check other map.
        assertFalse( entries.equals( otherMap.entrySet() ) );
        assertFalse( otherMap.entrySet().equals( entries ) );

    }

    @Test
    default void testEntrySetHashCode() {

        assertEquals( TEST_DB_MAPPINGS.entrySet().hashCode(), map.entrySet().hashCode() );
        assertEquals( new HashMap<>().entrySet().hashCode(), getTempTable().entrySet().hashCode() );

    }
    
    */

}
