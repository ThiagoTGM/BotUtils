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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides static methods for wrapping graph types to have certain
 * functionality details.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-15
 */
public class Graphs {

    /* Special wrappers for Graphs */

    /**
     * Entry that wraps another entry.
     * <p>
     * All methods in this class simply pass through to the backing entry.
     * Subclasses may tweak some or all methods (disabling some methods, adding
     * synchronization, etc) by overriding the desired method.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <K>
     *            The type of keys that make the path.
     * @param <V>
     *            The type of values stored in the entry.
     */
    private static class EntryWrapper<K, V> implements Graph.Entry<K, V> {

        private final Graph.Entry<K, V> backing;

        /**
         * Instantiates an entry backed by the given entry.
         *
         * @param backing
         *            The entry to back this.
         */
        public EntryWrapper( Graph.Entry<K, V> backing ) {

            this.backing = backing;

        }

        @Override
        public List<K> getPath() {

            return backing.getPath();

        }

        @Override
        public K[] getPathArray() {

            return backing.getPathArray();

        }

        @Override
        public V getValue() {

            return backing.getValue();

        }

        @Override
        public V setValue( V value ) throws NullPointerException, UnsupportedOperationException {

            return backing.setValue( value );

        }

        @Override
        public boolean equals( Object obj ) {

            return backing.equals( obj );

        }

        @Override
        public int hashCode() {

            return backing.hashCode();

        }

    }

    /**
     * Entry wrapper that synchronizes all methods.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <K>
     *            The type of keys that make the path.
     * @param <V>
     *            The type of values stored in the entry.
     */
    private static class SynchronizedEntryWrapper<K, V> extends EntryWrapper<K, V> {

        /**
         * Instantiates an entry backed by the given entry.
         *
         * @param backing
         *            The entry to back this.
         */
        public SynchronizedEntryWrapper( Graph.Entry<K, V> backing ) {

            super( backing );

        }

        @Override
        public synchronized List<K> getPath() {

            return super.getPath();

        }

        @Override
        public synchronized K[] getPathArray() {

            return super.getPathArray();

        }

        @Override
        public synchronized V getValue() {

            return super.getValue();

        }

        @Override
        public synchronized V setValue( V value ) throws NullPointerException, UnsupportedOperationException {

            return super.setValue( value );

        }

        @Override
        public synchronized boolean equals( Object obj ) {

            return super.equals( obj );

        }

        @Override
        public synchronized int hashCode() {

            return super.hashCode();

        }

    }

    /**
     * Entry wrapper that synchronizes all methods.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <K>
     *            The type of keys that make the path.
     * @param <V>
     *            The type of values stored in the entry.
     */
    private static class UnmodifiableEntryWrapper<K, V> extends EntryWrapper<K, V> {

        /**
         * Instantiates an entry backed by the given entry.
         *
         * @param backing
         *            The entry to back this.
         */
        public UnmodifiableEntryWrapper( Graph.Entry<K, V> backing ) {

            super( backing );

        }

        @Override
        public V setValue( V value ) throws NullPointerException, UnsupportedOperationException {

            throw new UnsupportedOperationException( "Graph does not support the put operation." );

        }

    }

    /**
     * Set of wrapped entries, backed by the given entry set. Almost all methods of
     * this class simply delegate to the backing set, with the only change being
     * that the Iterator of this set will wrap the entries using the given entry
     * wrapper before returning them.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <K>
     *            The type of keys that make the path.
     * @param <V>
     *            The type of values stored in the entry.
     * @param <V>
     *            The type of entries being stored.
     */
    private static class WrappedEntrySet<K, V, E extends Graph.Entry<K, V>> implements Set<E> {

        private final Set<E> backing;
        private final Function<E, E> entryWrapper;

        /**
         * Creates a new instance backed by the given set.
         *
         * @param backing
         *            The set to back this.
         * @param entryWrapper
         *            The function that creates an entry that wraps by the given entry.
         */
        public WrappedEntrySet( Set<E> backing, Function<E, E> entryWrapper ) {

            this.backing = backing;
            this.entryWrapper = entryWrapper;

        }

        @Override
        public boolean add( E e ) {

            return backing.add( e );

        }

        @Override
        public boolean addAll( Collection<? extends E> c ) {

            return backing.addAll( c );

        }

        @Override
        public void clear() {

            backing.clear();

        }

        @Override
        public boolean contains( Object o ) {

            return backing.contains( o );

        }

        @Override
        public boolean containsAll( Collection<?> c ) {

            return backing.containsAll( c );

        }

        @Override
        public boolean equals( Object o ) {

            return backing.equals( o );

        }

        @Override
        public int hashCode() {

            return backing.hashCode();

        }

        @Override
        public boolean isEmpty() {

            return backing.isEmpty();

        }

        @Override
        public Iterator<E> iterator() {

            final Iterator<E> backing = this.backing.iterator();
            return new Iterator<E>() {

                @Override
                public boolean hasNext() {

                    return backing.hasNext();

                }

                @Override
                public E next() {

                    return entryWrapper.apply( backing.next() );

                }

                @Override
                public void remove() {

                    backing.remove();

                }

            }; // End of anonymous Iterator<T>.

        }

        @Override
        public boolean remove( Object o ) {

            return backing.remove( o );

        }

        @Override
        public boolean removeAll( Collection<?> c ) {

            return backing.removeAll( c );

        }

        @Override
        public boolean removeIf( Predicate<? super E> filter ) {

            return backing.removeIf( filter );

        }

        @Override
        public boolean retainAll( Collection<?> c ) {

            return backing.retainAll( c );

        }

        @Override
        public int size() {

            return backing.size();

        }

        @Override
        public Object[] toArray() {

            return backing.toArray();

        }

        @Override
        public <T> T[] toArray( T[] a ) {

            return backing.toArray( a );

        }

        @Override
        public void forEach( Consumer<? super E> action ) {

            backing.forEach( action );

        }

        @Override
        public Stream<E> parallelStream() {

            return backing.parallelStream();

        }

        @Override
        public Spliterator<E> spliterator() {

            return backing.spliterator();

        }

        @Override
        public Stream<E> stream() {

            return backing.stream();

        }

    }

    // Unmodifiable wrappers.

    /**
     * Wrapper for a graph that passes through all methods, but throws an exception
     * on any method that would change the graph.
     * 
     * @author ThiagoTGM
     * @version 1.0
     * @since 2018-09-07
     * @param <K>
     *            Type of keys in a path of the graph.
     * @param <V>
     *            Type of values stored in the graph.
     */
    private static class UnmodifiableGraph<K, V> implements Graph<K, V>, Serializable {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -6974332055825955444L;

        private final Graph<K, V> backing;

        /**
         * Creates an instance that wraps the given graph.
         * 
         * @param backing
         *            The graph to be wrapped.
         */
        public UnmodifiableGraph( Graph<K, V> backing ) {

            this.backing = backing;

        }

        @Override
        public boolean containsPath( List<?> path ) throws NullPointerException {

            return backing.containsPath( path );

        }

        @Override
        public boolean containsValue( Object value ) throws NullPointerException {

            return backing.containsValue( value );

        }

        @Override
        public V get( List<?> path ) throws IllegalArgumentException, NullPointerException {

            return backing.get( path );

        }

        @Override
        public List<V> getAll( List<?> path ) throws IllegalArgumentException, NullPointerException {

            return backing.getAll( path );

        }

        @Override
        public V put( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            throw new UnsupportedOperationException( "Graph does not support the put operation." );

        }

        @Override
        public V putIfAbsent( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            throw new UnsupportedOperationException( "Graph does not support the put operation." );

        }

        @Override
        public void putAll( Graph<? extends K, ? extends V> g )
                throws UnsupportedOperationException, NullPointerException {

            throw new UnsupportedOperationException( "Graph does not support the putAll operation." );

        }

        @Override
        public void putAll( Map<? extends List<? extends K>, ? extends V> m )
                throws UnsupportedOperationException, NullPointerException {

            throw new UnsupportedOperationException( "Graph does not support the putAll operation." );

        }

        @Override
        public <T extends Map<? super List<K>, ? super V>> T toMap( T m )
                throws UnsupportedOperationException, NullPointerException {

            return backing.toMap( m );

        }

        @Override
        public boolean replace( List<K> path, V oldValue, V newValue )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            throw new UnsupportedOperationException( "Graph does not support the replace operation." );

        }

        @Override
        public V replace( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            throw new UnsupportedOperationException( "Graph does not support the replace operation." );

        }

        @Override
        public V remove( List<?> path )
                throws UnsupportedOperationException, IllegalArgumentException, NullPointerException {

            throw new UnsupportedOperationException( "Graph does not support the remove operation." );

        }

        @Override
        public boolean remove( List<?> path, Object value )
                throws UnsupportedOperationException, IllegalArgumentException {

            throw new UnsupportedOperationException( "Graph does not support the remove operation." );

        }

        @Override
        public Set<List<K>> pathSet() {

            return Collections.unmodifiableSet( backing.pathSet() );

        }

        @Override
        public Collection<V> values() {

            return Collections.unmodifiableCollection( backing.values() );

        }

        @Override
        public Set<Entry<K, V>> entrySet() {

            return Collections.unmodifiableSet(
                    new WrappedEntrySet<>( backing.entrySet(), entry -> new UnmodifiableEntryWrapper<>( entry ) ) );

        }

        @Override
        public int size() {

            return backing.size();

        }

        @Override
        public boolean isEmpty() {

            return backing.isEmpty();

        }

        @Override
        public void clear() {

            throw new UnsupportedOperationException( "Graph does not support the clear operation." );

        }

        @Override
        public boolean equals( Object obj ) {

            return backing.equals( obj );

        }

        @Override
        public int hashCode() {

            return backing.hashCode();

        }

    }

    /**
     * Wrapper for a tree that passes through most operations, but throws an
     * exception on operations that modify the tree.
     * 
     * @author ThiagoTGM
     * @version 1.0
     * @since 2018-09-07
     * @param <K>
     *            Type of keys in a path of the tree.
     * @param <V>
     *            Type of values stored in the tree.
     */
    private static class UnmodifiableTree<K, V> extends UnmodifiableGraph<K, V> implements Tree<K, V> {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -6137572584756055579L;

        /**
         * Creates an instance that wraps the given tree.
         * 
         * @param backing
         *            The tree to be wrapped.
         */
        public UnmodifiableTree( Tree<K, V> backing ) {

            super( backing );

        }

    }

    /**
     * Returns an unmodifiable view of the specified graph. This method allows
     * modules to provide users with "read-only" access to internal graphs. Query
     * operations on the returned graph "read through" to the specified graph, and
     * attempts to modify the returned graph, whether direct or via its collection
     * views, result in an UnsupportedOperationException.
     * <p>
     * The returned graph will be serializable if the specified graph is
     * serializable.
     * 
     * @param <K>
     *            The type of keys that make a path in the graph.
     * @param <V>
     *            The type of values stored in the graph.
     * @param g
     *            The graph for which an unmodifiable view is to be returned.
     * @return An unmodifiable view of the specified graph.
     * @throws NullPointerException
     *             if the given graph is <tt>null</tt>.
     */
    public static <K, V> Graph<K, V> unmodifiableGraph( Graph<K, V> g ) throws NullPointerException {

        if ( g == null ) {
            throw new NullPointerException( "Argument cannot be null." );
        }

        return new UnmodifiableGraph<>( g );

    }

    /**
     * Returns an unmodifiable view of the specified tree. This method allows
     * modules to provide users with "read-only" access to internal trees. Query
     * operations on the returned tree "read through" to the specified tree, and
     * attempts to modify the returned tree, whether direct or via its collection
     * views, result in an UnsupportedOperationException.
     * <p>
     * The returned tree will be serializable if the specified tree is serializable.
     * 
     * @param <K>
     *            The type of keys that make a path in the tree.
     * @param <V>
     *            The type of values stored in the tree.
     * @param t
     *            The tree for which an unmodifiable view is to be returned.
     * @return An unmodifiable view of the specified tree.
     * @throws NullPointerException
     *             if the given tree is <tt>null</tt>.
     */
    public static <K, V> Tree<K, V> unmodifiableTree( Tree<K, V> t ) throws NullPointerException {

        if ( t == null ) {
            throw new NullPointerException( "Argument cannot be null." );
        }

        return new UnmodifiableTree<>( t );

    }

    // Synchronized wrappers.

    /**
     * Wrapper for a graph that passes through all operations, and synchronizes all
     * method calls.
     * 
     * @author ThiagoTGM
     * @version 1.0
     * @since 2018-09-07
     * @param <K>
     *            Type of keys in a path of the graph.
     * @param <V>
     *            Type of values stored in the graph.
     */
    private static class SynchronizedGraph<K, V> implements Graph<K, V>, Serializable {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -6618016289702403440L;

        private final Graph<K, V> backing;

        /**
         * Creates an instance that wraps the given graph.
         * 
         * @param backing
         *            The graph to be wrapped.
         */
        public SynchronizedGraph( Graph<K, V> backing ) {

            this.backing = backing;

        }

        @Override
        public synchronized boolean containsPath( List<?> path ) throws NullPointerException {

            return backing.containsPath( path );

        }

        @Override
        public synchronized boolean containsValue( Object value ) throws NullPointerException {

            return backing.containsValue( value );

        }

        @Override
        public synchronized V get( List<?> path ) throws IllegalArgumentException, NullPointerException {

            return backing.get( path );

        }

        @Override
        public synchronized List<V> getAll( List<?> path ) throws IllegalArgumentException, NullPointerException {

            return backing.getAll( path );

        }

        @Override
        public synchronized V put( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            return backing.put( path, value );

        }

        @Override
        public synchronized V putIfAbsent( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            return backing.putIfAbsent( path, value );

        }

        @Override
        public synchronized void putAll( Graph<? extends K, ? extends V> g )
                throws UnsupportedOperationException, NullPointerException {

            backing.putAll( g );

        }

        @Override
        public synchronized void putAll( Map<? extends List<? extends K>, ? extends V> m )
                throws UnsupportedOperationException, NullPointerException {

            backing.putAll( m );

        }

        @Override
        public synchronized <T extends Map<? super List<K>, ? super V>> T toMap( T m )
                throws UnsupportedOperationException, NullPointerException {

            return backing.toMap( m );

        }

        @Override
        public synchronized boolean replace( List<K> path, V oldValue, V newValue )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            return backing.replace( path, oldValue, newValue );

        }

        @Override
        public synchronized V replace( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            return backing.replace( path, value );

        }

        @Override
        public synchronized V remove( List<?> path )
                throws UnsupportedOperationException, IllegalArgumentException, NullPointerException {

            return backing.remove( path );
        }

        @Override
        public synchronized boolean remove( List<?> path, Object value )
                throws UnsupportedOperationException, IllegalArgumentException {

            return backing.remove( path, value );

        }

        @Override
        public synchronized boolean isEmpty() {

            return backing.isEmpty();

        }

        @Override
        public synchronized boolean equals( Object obj ) {

            return backing.equals( obj );

        }

        @Override
        public synchronized int hashCode() {

            return backing.hashCode();

        }

        @Override
        public synchronized int size() {

            return backing.size();

        }

        @Override
        public synchronized void clear() {

            backing.clear();

        }

        @Override
        public synchronized Set<List<K>> pathSet() {

            return Collections.synchronizedSet( backing.pathSet() );

        }

        @Override
        public synchronized Collection<V> values() {

            return Collections.synchronizedCollection( backing.values() );

        }

        @Override
        public synchronized Set<Entry<K, V>> entrySet() {

            return Collections.synchronizedSet(
                    new WrappedEntrySet<>( backing.entrySet(), entry -> new SynchronizedEntryWrapper<>( entry ) ) );

        }

    }

    /**
     * Wrapper for a tree that passes through all operations, and synchronizes all
     * method calls.
     * 
     * @author ThiagoTGM
     * @version 1.0
     * @since 2018-09-07
     * @param <K>
     *            Type of keys in a path of the tree.
     * @param <V>
     *            Type of values stored in the tree.
     */
    private static class SynchronizedTree<K, V> extends SynchronizedGraph<K, V> implements Tree<K, V> {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -4615164564699994857L;

        /**
         * Creates an instance that wraps the given tree.
         * 
         * @param backing
         *            The tree to be wrapped.
         */
        public SynchronizedTree( Tree<K, V> backing ) {

            super( backing );

        }

    }

    /**
     * Returns a synchronized (thread-safe) graph backed by the specified graph. In
     * order to guarantee serial access, it is critical that <b>all</b> access to
     * the backing graph is accomplished through the returned graph.
     * <p>
     * It is imperative that the user manually synchronize on the returned graph
     * when iterating over any of its collection views:
     * 
     * <pre>
     *  Graph g = Utils.synchronizedGraph(new TreeGraph());
     *      ...
     *  Set s = g.pathSet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (g) {  // Synchronizing on g, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * 
     * Failure to follow this advice may result in non-deterministic behavior.
     * <p>
     * The returned graph will be serializable if the specified graph is
     * serializable.
     * 
     * @param <K>
     *            The type of keys that make a path in the graph.
     * @param <V>
     *            The type of values stored in the graph.
     * @param g
     *            The graph to be "wrapped" in a synchronized graph.
     * @return A synchronized view of the specified graph.
     * @throws NullPointerException
     *             if the given graph is <tt>null</tt>.
     */
    public static <K, V> Graph<K, V> synchronizedGraph( Graph<K, V> g ) throws NullPointerException {

        if ( g == null ) {
            throw new NullPointerException( "Argument cannot be null." );
        }

        return new SynchronizedGraph<>( g );

    }

    /**
     * Returns a synchronized (thread-safe) tree backed by the specified tree. In
     * order to guarantee serial access, it is critical that <b>all</b> access to
     * the backing tree is accomplished through the returned tree.
     * <p>
     * It is imperative that the user manually synchronize on the returned tree when
     * iterating over any of its collection views:
     * 
     * <pre>
     *  Tree t = Utils.synchronizedTree(new TreeGraph());
     *      ...
     *  Set s = t.pathSet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (t) {  // Synchronizing on t, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * 
     * Failure to follow this advice may result in non-deterministic behavior.
     * <p>
     * The returned tree will be serializable if the specified tree is serializable.
     * 
     * @param <K>
     *            The type of keys that make a path in the tree.
     * @param <V>
     *            The type of values stored in the tree.
     * @param t
     *            The tree to be "wrapped" in a synchronized tree.
     * @return A synchronized view of the specified tree.
     * @throws NullPointerException
     *             if the given tree is <tt>null</tt>.
     */
    public static <K, V> Tree<K, V> synchronizedTree( Tree<K, V> t ) throws NullPointerException {

        if ( t == null ) {
            throw new NullPointerException( "Argument cannot be null." );
        }

        return new SynchronizedTree<>( t );

    }

    /**
     * Obtains a tree that is backed by the given map, by using the path list of
     * each mapping as a key into the given map.
     * <p>
     * In other words, obtains a tree view of a map that is keyed by lists.
     *
     * @param <K>
     *            The type of keys in the map key list/tree path.
     * @param <V>
     *            The type of values being stored.
     * @param backing
     *            The backing map.
     * @return A tree view of the given map.
     */
    public static <K, V> Tree<K, V> mappedTree( Map<List<K>, V> backing ) {

        return new MappedTree<>( backing );

    }

}
