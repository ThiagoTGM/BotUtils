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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a graph that links sequences of keys to values. The empty path may
 * or may not be valid depending on implementation. It is also up to the
 * implementation whether <tt>null</tt> values or keys (elements in a path) are
 * allowed.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-16
 * @param <K>
 *            The type of the keys that define connections on the graph.
 * @param <V>
 *            The type of the values to be stored.
 */
@SuppressWarnings( "unchecked" )
public interface Graph<K, V> {

    /**
     * Determines whether there is a mapping in this graph with the given path.
     * <p>
     * A path will be matched if, for the corresponding values <tt>p</tt> and
     * <tt>m</tt> at each step in the path and the given path,
     * <tt>p==null ? m==null : p.equals(m)</tt>.
     * 
     * @param path
     *            The path to be checked.
     * @return <tt>true</tt> if there is a mapping in the graph with such a path.
     *         <tt>false</tt> otherwise.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys (optional).
     */
    boolean containsPath( List<?> path ) throws NullPointerException;

    /**
     * Varargs version of {@link #containsPath(List)}.
     * 
     * @param path
     *            The path to be checked.
     * @return <tt>true</tt> if there is a mapping in the graph with such a path.
     *         <tt>false</tt> otherwise.
     * @throws NullPointerException
     *             if some element of the path is <tt>null</tt> and this graph does
     *             not permit <tt>null</tt> keys (optional).
     */
    default boolean containsPath( Object... path ) throws NullPointerException {

        return containsPath( Arrays.asList( path ) );

    }

    /**
     * Determines whether there is a mapping in this graph with the given value.
     * <p>
     * A value <tt>v</tt> is matched if
     * <tt>value==null ? v==null : value.equals(v)</tt>.
     * 
     * @param value
     *            The value to be checked.
     * @return <tt>true</tt> if there is a mapping in the graph with such a value.
     *         <tt>false</tt> otherwise.
     * @throws NullPointerException
     *             if the value is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> values (optional).
     */
    boolean containsValue( Object value ) throws NullPointerException;

    /**
     * Retrieves the value mapped to the given sequence of keys.
     * <p>
     * A mapping will be matched if, for the corresponding values <tt>p</tt> and
     * <tt>m</tt> at each step in the path and mapping,
     * <tt>p==null ? m==null : p.equals(m)</tt>.
     * <p>
     * The return value will be <tt>null</tt> if no mapping exists for the given
     * path. However, if the implementation being used allows <tt>null</tt> values,
     * then a return value of <tt>null</tt> does not necessarily indicate that there
     * is no mapping, it may just be the value <tt>null</tt>. In that situation,
     * {@link #containsPath(List)} should be used to differentiate those cases.
     *
     * @param path
     *            The sequence of keys that map to the value.
     * @return The value linked to the given path, or <tt>null</tt> if there is
     *         none.
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys (optional).
     */
    V get( List<?> path ) throws IllegalArgumentException, NullPointerException;

    /**
     * Varargs version of {@link #get(List)}.
     * 
     * @param path
     *            The sequence of keys that map to the value.
     * @return The value linked to the given path, or <tt>null</tt> if there is
     *         none.
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if some element of the path is <tt>null</tt> and this graph does
     *             not permit <tt>null</tt> keys (optional).
     */
    default V get( Object... path ) throws IllegalArgumentException, NullPointerException {

        return get( Arrays.asList( path ) );

    }

    /**
     * Retrieves the values mapped to each step of the given sequence of keys.<br>
     * Steps that do not exist or have no mapping are ignored.
     * 
     * @param path
     *            The sequence of keys that map to the values.
     * @return The values linked to each step of the given path, in the order that
     *         the path is traversed (same order that the keys are given).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys (optional).
     */
    default List<V> getAll( List<?> path ) throws IllegalArgumentException, NullPointerException {

        List<V> values = new ArrayList<>( path.size() );
        try {
            values.add( get() );
        } catch ( IllegalArgumentException e ) { // Empty path not valid.
            if ( path.size() == 0 ) { // Given path was empty.
                throw e;
            }
        }

        for ( int i = 1; i <= path.size(); i++ ) {

            values.add( get( path.subList( 0, i ) ) );

        }

        return values;

    }

    /**
     * Varargs version of {@link #getAll(List)}.
     *
     * @param path
     *            The sequence of keys that map to the values.
     * @return The values linked to each step of the given path, in the order that
     *         the path is traversed (same order that the keys are given).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if some element of the path is <tt>null</tt> and this graph does
     *             not permit <tt>null</tt> keys (optional).
     */
    default List<V> getAll( Object... path ) throws IllegalArgumentException, NullPointerException {

        return getAll( Arrays.asList( path ) );

    }

    /**
     * Maps a value to a sequence of keys, replacing the value currently mapped to
     * the path, if any.<br>
     * Optional operation.
     * <p>
     * The return value will be <tt>null</tt> if no mapping existed for the given
     * path. However, if the implementation being used allows <tt>null</tt> values,
     * then a return value of <tt>null</tt> does not necessarily indicate that there
     * was no mapping, it may just be the value <tt>null</tt>. In that situation,
     * {@link #containsPath(List)} should be used beforehand to differentiate those
     * cases.
     *
     * @param path
     *            The sequence of keys that map to the value.
     * @param value
     *            The value to be stored on the path.
     * @return The value previously mapped to that path, or <tt>null</tt> if there
     *         was none.
     * @throws UnsupportedOperationException
     *             if the set operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path or the value is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys or values. (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    V put( List<K> path, V value ) throws UnsupportedOperationException, NullPointerException, IllegalArgumentException;

    /**
     * Varargs version of {@link #put(List, Object) put(List, V)}.
     *
     * @param value
     *            The value to be stored on the path.
     * @param path
     *            The sequence of keys that map to the value.
     * @return The value previously mapped to that path, or <tt>null</tt> if there
     *         was none.
     * @throws UnsupportedOperationException
     *             if the set operation is not supported by this graph.
     * @throws NullPointerException
     *             if some element of the path or the value given is <tt>null</tt>,
     *             and this graph does not allow <tt>null</tt> keys or values
     *             (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default V put( V value, K... path )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        return put( Arrays.asList( path ), value );

    }

    /**
     * If the specified path is not already associated with a value (or is mapped to
     * <tt>null</tt>), associates it with the given value and returns <tt>null</tt>,
     * else returns the current value.
     *
     * @param path
     *            The sequence of keys that map to the value.
     * @param value
     *            The value to be stored on the path.
     * @return The previous value associated with the specified path, or
     *         <tt>null</tt> if there was no mapping for the key. (A <tt>null</tt>
     *         return can also indicate that the graph previously associated
     *         <tt>null</tt> with the path, if the implementation supports
     *         <tt>null</tt> values).
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path or the value is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys or values. (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default V putIfAbsent( List<K> path, V value )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        V v = get( path );
        if ( v == null ) {
            v = put( path, value );
        }

        return v;

    }

    /**
     * Varargs version of {@link #putIfAbsent(List, Object) add(List, V)}.
     *
     * @param value
     *            The value to be stored on the path.
     * @param path
     *            The sequence of keys that map to the value.
     * @return The previous value associated with the specified path, or
     *         <tt>null</tt> if there was no mapping for the path. (A <tt>null</tt>
     *         return can also indicate that the graph previously associated
     *         <tt>null</tt> with the path, if the implementation supports
     *         <tt>null</tt> values).
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if some element of the path or the value given is <tt>null</tt>,
     *             and this graph does not allow <tt>null</tt> keys or values
     *             (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default V putIfAbsent( V value, K... path )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        return putIfAbsent( Arrays.asList( path ), value );

    }

    /**
     * Copies all of the mappings from the specified graph to this graph. The effect
     * of this call is equivalent to that of calling {@link #put(List, Object)
     * put(p, v)} on this graph once for each mapping from path <tt>p</tt> to value
     * <tt>v</tt> in the specified graph. The behavior of this operation is
     * undefined if the specified graph is modified while the operation is in
     * progress.
     * 
     * @param g
     *            Mappings to be stored in this graph.
     * @throws UnsupportedOperationException
     *             if the <tt>putAll</tt> operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified graph is <tt>null</tt>, or if this graph does
     *             not permit <tt>null</tt> keys or values, and the specified graph
     *             contains <tt>null</tt> keys or values.
     */
    default void putAll( Graph<? extends K, ? extends V> g )
            throws UnsupportedOperationException, NullPointerException {

        if ( g == null ) {
            throw new NullPointerException( "Given graph cannot be null." );
        }

        for ( Entry<? extends K, ? extends V> entry : g.entrySet() ) {

            put( new ArrayList<>( entry.getPath() ), entry.getValue() );

        }

    }

    /**
     * Copies all of the mappings from the specified map to this graph. The effect
     * of this call is equivalent to that of calling {@link #put(List, Object)
     * put(k, v)} on this graph once for each mapping from key <tt>k</tt> to value
     * <tt>v</tt> in the specified map. The behavior of this operation is undefined
     * if the specified map is modified while the operation is in progress.
     * 
     * @param m
     *            Mappings to be stored in this graph.
     * @throws UnsupportedOperationException
     *             if the <tt>putAll</tt> operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, or if the specified map
     *             contains <tt>null</tt> keys, or if this graph does not permit
     *             <tt>null</tt> path elements or values, and the specified map
     *             contains <tt>null</tt> path elements or values.
     */
    default void putAll( Map<? extends List<? extends K>, ? extends V> m )
            throws UnsupportedOperationException, NullPointerException {

        if ( m == null ) {
            throw new NullPointerException( "Given map cannot be null." );
        }

        for ( Map.Entry<? extends List<? extends K>, ? extends V> entry : m.entrySet() ) {

            put( new ArrayList<>( entry.getKey() ), entry.getValue() );

        }

    }

    /**
     * Copies all of the mappings from this graph to specified map. The effect of
     * this call is equivalent to that of adding each entry in this graph into an
     * intermediate map <tt>m</tt>, then calling {@link Map#putAll(Map)
     * Map.putAll(m)} on the given map. The behavior of this operation is undefined
     * if this graph is modified while the operation is in progress.
     * 
     * @param <T>
     *            The type of the map.
     * @param m
     *            Map to store mappings into.
     * @return The map.
     * @throws UnsupportedOperationException
     *             if the <tt>putAll</tt> operation is not supported by the given
     *             map.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, or if the specified map
     *             does not permit <tt>null</tt> values and this graph contains
     *             <tt>null</tt> values.
     */
    default <T extends Map<? super List<K>, ? super V>> T toMap( T m )
            throws UnsupportedOperationException, NullPointerException {

        if ( m == null ) {
            throw new NullPointerException( "Given map cannot be null." );
        }

        Map<List<K>, V> mappings = new HashMap<>();
        for ( Entry<K, V> entry : entrySet() ) { // Collect the mappings.

            mappings.put( new ArrayList<>( entry.getPath() ), entry.getValue() );

        }

        m.putAll( mappings ); // Put all mappings.
        return m;

    }

    /**
     * Replaces the entry for the specified path only if currently mapped to the
     * specified value.
     * 
     * @param path
     *            Path with which the specified value is associated.
     * @param oldValue
     *            Value expected to be associated with the specified key.
     * @param newValue
     *            Value to be associated with the specified key.
     * @return <tt>true</tt> if the value was replaced.
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path or the new value is <tt>null</tt> and this graph does not
     *             permit <tt>null</tt> keys or values. (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default boolean replace( List<K> path, V oldValue, V newValue )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        if ( containsPath( path ) && Objects.equals( get( path ), oldValue ) ) {
            put( path, newValue );
            return true;
        } else {
            return false;
        }

    }

    /**
     * Varargs version of {@link #replace(List, Object, Object) replace(List, V,
     * V)}.
     * 
     * @param path
     *            Path with which the specified value is associated.
     * @param oldValue
     *            Value expected to be associated with the specified key.
     * @param newValue
     *            Value to be associated with the specified key.
     * @return <tt>true</tt> if the value was replaced.
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if some element of the path or the new value given is
     *             <tt>null</tt>, and this graph does not allow <tt>null</tt> keys
     *             or values (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default boolean replace( V oldValue, V newValue, K... path )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        return replace( Arrays.asList( path ), oldValue, newValue );

    }

    /**
     * Replaces the entry for the specified path only if it is currently mapped to
     * some value.
     * 
     * @param path
     *            Path with which the specified value is associated.
     * @param value
     *            Value to be associated with the specified key.
     * @return The previous value associated with the specified path, or
     *         <tt>null</tt> if there was no mapping for the path. (A <tt>null</tt>
     *         return can also indicate that the graph previously associated
     *         <tt>null</tt> with the path, if the implementation supports
     *         <tt>null</tt> values).
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path or the value is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys or values. (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default V replace( List<K> path, V value )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        if ( containsPath( path ) ) {
            return put( path, value );
        } else {
            return null;
        }

    }

    /**
     * Varargs version of {@link #replace(List, Object) replace(List, V)}.
     * 
     * @param path
     *            Path with which the specified value is associated.
     * @param value
     *            Value to be associated with the specified key.
     * @return The previous value associated with the specified path, or
     *         <tt>null</tt> if there was no mapping for the path. (A <tt>null</tt>
     *         return can also indicate that the graph previously associated
     *         <tt>null</tt> with the path, if the implementation supports
     *         <tt>null</tt> values).
     * @throws UnsupportedOperationException
     *             if the add operation is not supported by this graph.
     * @throws NullPointerException
     *             if some element of the path or the value given is <tt>null</tt>,
     *             and this graph does not allow <tt>null</tt> keys or values
     *             (optional).
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     */
    default V replace( V value, K... path )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        return replace( Arrays.asList( path ), value );

    }

    /**
     * Removes a mapping from this graph.<br>
     * Optional operation.
     * <p>
     * The return value will be <tt>null</tt> if no mapping existed for the given
     * path. However, if the implementation being used allows <tt>null</tt> values,
     * then a return value of <tt>null</tt> does not necessarily indicate that there
     * was no mapping, it may just be the value <tt>null</tt>. In that situation,
     * {@link #containsPath(List)} should be used beforehand to differentiate those
     * cases.
     *
     * @param path
     *            The sequence of keys that map to the value to be removed.
     * @return The removed value, or <tt>null</tt> if there is no mapping for the
     *         given path.
     * @throws UnsupportedOperationException
     *             if the remove operation is not supported by this graph.
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys (optional).
     */
    V remove( List<?> path ) throws UnsupportedOperationException, IllegalArgumentException, NullPointerException;

    /**
     * Varargs version of {@link #remove(List)}.
     *
     * @param path
     *            The sequence of keys that map to the value to be removed.
     * @return The removed value, or <tt>null</tt> if there is no mapping for the
     *         given path.
     * @throws UnsupportedOperationException
     *             if the remove operation is not supported by this graph.
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if some element of the path is <tt>null</tt> and this graph does
     *             not permit <tt>null</tt> keys (optional).
     */
    default V remove( Object... path )
            throws UnsupportedOperationException, IllegalArgumentException, NullPointerException {

        return remove( Arrays.asList( path ) );

    }

    /**
     * Removes the entry for the specified path only if it is currently mapped to
     * the specified value.
     * 
     * @param path
     *            Path with which the specified value is associated.
     * @param value
     *            Value expected to be associated with the specified path.
     * @return <tt>true</tt> if the value was removed.
     * @throws UnsupportedOperationException
     *             if the remove operation is not supported by this graph.
     * @throws IllegalArgumentException
     *             if the path is empty but such a path is not valid under the
     *             current implementation.
     * @throws NullPointerException
     *             if the specified path is <tt>null</tt>, or if some element of the
     *             path is <tt>null</tt> and this graph does not permit
     *             <tt>null</tt> keys (optional).
     */
    default boolean remove( List<?> path, Object value )
            throws UnsupportedOperationException, IllegalArgumentException {

        if ( containsPath( path ) && Objects.equals( get( path ), value ) ) {
            remove( path );
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns a {@link Set} view of the paths contained in this graph. The set is
     * backed by the graph, so changes to the graph are reflected in the set, and
     * vice-versa. If the graph is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation), the
     * results of the iteration are undefined. The set supports element removal,
     * which removes the corresponding mapping from the graph, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return A set view of the paths contained in this graph.
     */
    Set<List<K>> pathSet();

    /**
     * Returns a {@link Collection} view of the values contained in this graph. The
     * collection is backed by the graph, so changes to the graph are reflected in
     * the collection, and vice-versa. If the graph is modified while an iteration
     * over the collection is in progress (except through the iterator's own
     * <tt>remove</tt> operation), the results of the iteration are undefined. The
     * collection supports element removal, which removes the corresponding mapping
     * from the graph, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does
     * not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return A collection view of the values contained in this graph.
     */
    Collection<V> values();

    /**
     * Returns a {@link Set} view of the mappings contained in this graph. The set
     * is backed by the graph, so changes to the graph are reflected in the set, and
     * vice-versa. If the graph is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation, or
     * through the <tt>setValue</tt> operation on a graph entry returned by the
     * iterator) the results of the iteration are undefined. The set supports
     * element removal, which removes the corresponding mapping from the graph, via
     * the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return A set view of the mappings contained in this graph.
     */
    Set<Entry<K, V>> entrySet();

    /**
     * Retrieves the amount of path-value mappings that are stored in this graph.
     *
     * @return The amount of mappings in this graph.
     */
    int size();

    /**
     * Determines whether the graph is empty.
     *
     * @return <tt>true</tt> if this map contains no path-value mappings.
     *         <tt>false</tt> otherwise.
     */
    default boolean isEmpty() {

        return size() == 0;

    }

    /**
     * Removes all mappings from this graph.
     */
    void clear();

    /**
     * Compares this graph with the specified object for equality. Returns
     * <tt>true</tt> if the specified object is also a Graph, that contains the same
     * values mapped to the same paths.<br>
     * More formally, two graphs <tt>g1</tt> and <tt>g2</tt> are equal if
     * <tt>g1.entrySet().equals(g2.entrySet())</tt>.
     *
     * @param obj
     *            The object to compare to.
     * @return <tt>true</tt> if the specified object is equal to this graph,
     *         <tt>false</tt> otherwise.
     */
    @Override
    boolean equals( Object obj );

    /**
     * Calculates the hash code of the graph. The hash code of a graph is defined to
     * be the sum of the hash codes of each entry in the graph's <tt>entrySet()</tt>
     * view. This ensures that <tt>g1.equals(g2)</tt> implies that
     * <tt>g1.hashCode()==g2.hashCode()</tt> for any two graphs <tt>g1</tt> and
     * <tt>g2</tt>.
     *
     * @return The hash code of this Graph.
     */
    @Override
    int hashCode();

    /**
     * A path-value entry in a Graph. The {@link Graph#entrySet()} method returns a
     * Set view of the graph with members of this class. The only way to obtain an
     * entry is through the iterator of that set.<br>
     * If the backing graph is modified in any way other than the
     * {@link #setValue(Object) setValue} method of an Entry, the behavior is
     * undefined.
     *
     * @version 1.0
     * @author ThiagoTGM
     * @since 2017-08-20
     * @param <K>
     *            Type of the keys that form the path.
     * @param <V>
     *            Type of the values stored in the graph.
     */
    static interface Entry<K, V> {

        /**
         * Retrieves the path represented by this Entry.<br>
         * The returned list is unmodifiable.
         *
         * @return The path of this entry. Will never be null, but may be empty.
         */
        List<K> getPath();

        /**
         * Retrieves the path represented by this Entry, stored in an array.<br>
         * Changes to the returned array are not reflected on the entry.
         * <p>
         * Convenience method for using the path directly into a Graph's vararg methods.
         *
         * @return An array containing the path of this entry. Will never be null, but
         *         may be empty.
         * @see #getPath()
         */
        default K[] getPathArray() {

            List<? extends K> path = getPath();
            K[] array = (K[]) new Object[path.size()];
            return path.toArray( array );

        }

        /**
         * Retrieves the value corresponding to this entry.
         * <p>
         * If the mapping has been removed from the backing map (by the iterator's
         * <tt>remove</tt> operation), the results of this call are undefined.
         *
         * @return The value of this entry.
         */
        V getValue();

        /**
         * Sets the value of this entry (reflects on the backing Graph).
         * <p>
         * If the mapping has been removed from the backing map (by the iterator's
         * <tt>remove</tt> operation), the results of this call are undefined.
         *
         * @param value
         *            The value to set for this entry.
         * @return The previous value.
         * @throws NullPointerException
         *             if the value given is <tt>null</tt>, and the current
         *             implementation does not allow <tt>null</tt> values.
         * @throws UnsupportedOperationException
         *             if the put operation is not supported by the backing graph.
         */
        V setValue( V value ) throws NullPointerException, UnsupportedOperationException;

        /**
         * Compares the specified object with this entry for equality. Returns
         * <tt>true</tt> if the given object is also an Entry and both entries represent
         * the same mapping. Two entries <tt>e1</tt> and <tt>e2</tt> represent the same
         * mapping if they have both the same path and the same value:
         * <p>
         * <code>
         * e1.getPath().equals(e2.getPath()) &amp;&amp; e1.getValue().equals(e2.getValue())
         * </code>
         *
         * @param obj
         *            The object to compare to.
         * @return <tt>true</tt> if this and the given object are entries that
         *         correspond to the same mapping. <tt>false</tt> otherwise.
         */
        @Override
        boolean equals( Object obj );

        /**
         * Generates the hash code of this entry.<br>
         * The hash code of a graph entry <tt>e</tt> is defined to be:
         * <p>
         * <code>
         * e.getPath().hashCode() ^ e.getValue().hashCode()
         * </code>
         * <p>
         * This ensures that <tt>e1.equals(e2)</tt> implies
         * <tt>e1.hashCode()==e2.hashCode()</tt> for any two Entries <tt>e1</tt> and
         * <tt>e2</tt>.
         *
         * @return The hash code of this entry.
         */
        @Override
        int hashCode();

    }

}
