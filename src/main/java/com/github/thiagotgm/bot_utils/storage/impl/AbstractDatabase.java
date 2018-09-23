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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.thiagotgm.bot_utils.Settings;
import com.github.thiagotgm.bot_utils.storage.Cache;
import com.github.thiagotgm.bot_utils.storage.Database;
import com.github.thiagotgm.bot_utils.storage.DatabaseStats;
import com.github.thiagotgm.bot_utils.storage.Translator;
import com.github.thiagotgm.bot_utils.utils.graph.Graph;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;

/**
 * Provides implementation of behavior that is common to all Database
 * implementations.
 * <p>
 * Subclasses are expected to set {@link #loaded} to <tt>true</tt> upon a
 * successful execution of {@link #load(List)}, and to set {@link #closed} to
 * <tt>true</tt> when {@link #close()} is called ({@link #loaded} should
 * <b>not</b> be set back to <tt>false</tt> after {@link #close()} is
 * called).<br>
 * All methods implemented here will check the value of those variables to
 * ensure proper database state, and trees and maps received from
 * {@link #newMap(String, Translator, Translator)} and
 * {@link #newTree(String, Translator, Translator)} will be wrapped so that they
 * will automatically stop working after {@link #closed} is set to <tt>true</tt>
 * (all operations will then throw an {@link IllegalStateException}).
 * <p>
 * The tree and map wrappers also automatically provide buffering for the get()
 * operation, using the cache size specified by the settings at start time (if
 * the size is changed, it will not take effect until the next time the program
 * is started). No other operations are buffered, although subclasses are free
 * to use their own internal caches for other operations.<br>
 * OBS: Using operations in the Set views of wrapped maps and trees that change
 * the associated map/tree will immediately invalidate the entire cache for that
 * map/tree.
 * <p>
 * The wrappers used for trees and maps are not thread-safe, and as a result
 * trees and maps obtained from the methods implemented here are not thread-safe
 * even if the underlying implementation of the database is.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-07-26
 * @see Cache
 */
public abstract class AbstractDatabase implements Database {

    /**
     * Name of the setting that determines the size of the used caches.
     */
    public static final String CACHE_SETTING = "Cache size";
    /**
     * Size of the caches, based on the {@link #CACHE_SETTING size setting}.
     */
    public static final int CACHE_SIZE = Settings.getIntSetting( CACHE_SETTING );

    /**
     * Trees currently managed by the database.
     */
    private final Map<String, TreeEntry<?, ?>> trees;
    /**
     * Maps currently managed by the database.
     */
    private final Map<String, MapEntry<?, ?>> maps;

    /**
     * Whether the database is currently loaded.
     */
    protected volatile boolean loaded;
    /**
     * Whether the database is currently closed.
     */
    protected volatile boolean closed;

    /**
     * Initializes the database.
     */
    public AbstractDatabase() {

        trees = new HashMap<>();
        maps = new HashMap<>();

        loaded = false;
        closed = false;

    }

    /**
     * Checks that the database is already loaded and not closed yet, throwing an
     * exception otherwise.
     * 
     * @throws IllegalStateException
     *             if the database is not loaded yet or already closed.
     */
    private void checkState() throws IllegalStateException {

        if ( !loaded ) {
            throw new IllegalStateException( "Database not loaded yet." );
        }

        if ( closed ) {
            throw new IllegalStateException( "Database already closed." );
        }

    }

    /**
     * Creates a new data tree backed by the storage system.
     * <p>
     * The arguments are guaranteed to not be <tt>null</tt>.
     * 
     * @param dataName
     *            The name that identifies the data set.
     * @param keyTranslator
     *            The translator to use to convert keys in the path to Strings.
     * @param valueTranslator
     *            The translator to use to convert values to Strings.
     * @param <K>
     *            The type of keys in the tree paths.
     * @param <V>
     *            The type of values to be stored in the tree.
     * @return The data tree.
     * @throws DatabaseException
     *             if an error occurred while obtaining the tree.
     */
    protected abstract <K, V> Tree<K, V> newTree( String dataName, Translator<K> keyTranslator,
            Translator<V> valueTranslator ) throws DatabaseException;

    @Override
    public synchronized <K, V> Tree<K, V> getDataTree( String treeName, Translator<K> keyTranslator,
            Translator<V> valueTranslator )
            throws NullPointerException, IllegalStateException, IllegalArgumentException, DatabaseException {

        checkState();

        if ( ( treeName == null ) || ( keyTranslator == null ) || ( valueTranslator == null ) ) {
            throw new NullPointerException( "Arguments cannot be null." );
        }

        Tree<K, V> tree;
        TreeEntry<?, ?> entry = trees.get( treeName ); // Check if there is already an entry.
        if ( entry == null ) { // No entry.
            if ( maps.containsKey( treeName ) ) { // Check if map name.
                throw new IllegalArgumentException( "Given name is assigned to a map." );
            }

            // Create and record new tree, within a wrapper.
            tree = new DatabaseTree<>( newTree( treeName, keyTranslator, valueTranslator ) );
            trees.put( treeName, new TreeEntryImpl<>( treeName, tree, keyTranslator, valueTranslator ) );
        } else { // Found entry.
            if ( keyTranslator.getClass() != entry.getKeyTranslator().getClass() ) {
                throw new IllegalArgumentException(
                        "Given key translator is of a different class " + "than the existing key translator." );
            } // Check that translators match.

            if ( valueTranslator.getClass() != entry.getValueTranslator().getClass() ) {
                throw new IllegalArgumentException(
                        "Given value translator is of a different class " + "than the existing value translator." );
            }

            @SuppressWarnings( "unchecked" )
            Tree<K, V> t = (Tree<K, V>) entry.getTree(); // Translators match - tree is the correct type.
            tree = t;
        }

        return tree;

    }

    /**
     * Deletes a data tree from the backing storage system.
     *
     * @param treeName
     *            The name that identifies the data set.
     */
    protected abstract void deleteTree( String treeName );

    @Override
    public synchronized boolean deleteDataTree( String treeName ) {

        if ( trees.remove( treeName ) != null ) { // Found tree.
            deleteTree( treeName );
            return true;
        } else { // No tree found.
            return false;
        }

    }

    /**
     * Creates a new data map backed by the storage system.
     * <p>
     * The arguments are guaranteed to not be <tt>null</tt>.
     * 
     * @param dataName
     *            The name that identifies the data set.
     * @param keyTranslator
     *            The translator to use to convert keys to Strings.
     * @param valueTranslator
     *            The translator to use to convert values to Strings.
     * @param <K>
     *            The type of keys in the map.
     * @param <V>
     *            The type of values to be stored in the map.
     * @return The data map.
     * @throws DatabaseException
     *             if an error occurred while obtaining the map.
     */
    protected abstract <K, V> Map<K, V> newMap( String dataName, Translator<K> keyTranslator,
            Translator<V> valueTranslator ) throws DatabaseException;

    @Override
    public synchronized <K, V> Map<K, V> getDataMap( String mapName, Translator<K> keyTranslator,
            Translator<V> valueTranslator )
            throws NullPointerException, IllegalStateException, IllegalArgumentException, DatabaseException {

        checkState();

        if ( ( mapName == null ) || ( keyTranslator == null ) || ( valueTranslator == null ) ) {
            throw new NullPointerException( "Arguments cannot be null." );
        }

        Map<K, V> map;
        MapEntry<?, ?> entry = maps.get( mapName ); // Check if there is already an entry.
        if ( entry == null ) { // No entry.
            if ( trees.containsKey( mapName ) ) { // Check if tree name.
                throw new IllegalArgumentException( "Given name is assigned to a tree." );
            }

            // Create and record new map, within a wrapper.
            map = new DatabaseMap<>( newMap( mapName, keyTranslator, valueTranslator ) );
            maps.put( mapName, new MapEntryImpl<>( mapName, map, keyTranslator, valueTranslator ) );
        } else { // Found entry.
            if ( keyTranslator.getClass() != entry.getKeyTranslator().getClass() ) {
                throw new IllegalArgumentException(
                        "Given key translator is of a different class " + "than the existing key translator." );
            } // Check that translators match.

            if ( valueTranslator.getClass() != entry.getValueTranslator().getClass() ) {
                throw new IllegalArgumentException(
                        "Given value translator is of a different class " + "than the existing value translator." );
            }

            @SuppressWarnings( "unchecked" )
            Map<K, V> m = (Map<K, V>) entry.getMap(); // Translators match - map is the correct type.
            map = m;
        }

        return map;

    }
    
    /**
     * Deletes a data map from the backing storage system.
     *
     * @param mapName
     *            The name that identifies the data set.
     */
    protected abstract void deleteMap( String mapName );

    @Override
    public synchronized boolean deleteDataMap( String mapName ) {

        if ( maps.remove( mapName ) != null ) { // Found map.
            deleteTree( mapName );
            return true;
        } else { // No map found.
            return false;
        }

    }

    @Override
    public synchronized int size() throws IllegalStateException {

        checkState();

        return trees.size() + maps.size();

    }

    @Override
    public synchronized Collection<TreeEntry<?, ?>> getDataTrees() throws IllegalStateException {

        checkState();

        return Collections.unmodifiableCollection( trees.values() );

    }

    @Override
    public synchronized Collection<MapEntry<?, ?>> getDataMaps() throws IllegalStateException {

        checkState();

        return Collections.unmodifiableCollection( maps.values() );

    }

    /* Entry implementations */

    /**
     * Implementation of a database entry.
     * 
     * @since 2018-07-26
     * @param <K>
     *            The type of key used by the storage.
     * @param <V>
     *            The type of value used by the storage.
     * @param <T>
     *            The type of the storage unit.
     */
    protected abstract class DatabaseEntryImpl<K, V, T> implements DatabaseEntry<K, V, T> {

        private final String name;
        private final T storage;
        private final Translator<K> keyTranslator;
        private final Translator<V> valueTranslator;

        /**
         * Initializes a database entry for the given storage unit, under the given
         * name, with the given translators.
         * 
         * @param name
         *            The name that the storage unit is registered under.
         * @param storage
         *            The storage unit.
         * @param keyTranslator
         *            The translator for keys.
         * @param valueTranslator
         *            The translator for values.
         * @throws NullPointerException
         *             If any of the arguments is <tt>null</tt>.
         */
        public DatabaseEntryImpl( String name, T storage, Translator<K> keyTranslator, Translator<V> valueTranslator )
                throws NullPointerException {

            if ( ( name == null ) || ( storage == null ) || ( keyTranslator == null ) || ( valueTranslator == null ) ) {
                throw new NullPointerException( "Arguments can't be null." );
            }

            this.name = name;
            this.storage = storage;
            this.keyTranslator = keyTranslator;
            this.valueTranslator = valueTranslator;

        }

        @Override
        public String getName() {

            return name;

        }

        @Override
        public T getStorage() {

            return storage;

        }

        @Override
        public Translator<K> getKeyTranslator() {

            return keyTranslator;

        }

        @Override
        public Translator<V> getValueTranslator() {

            return valueTranslator;

        }

    }

    /**
     * Implementation of a database tree entry.
     * 
     * @since 2018-07-26
     * @param <K>
     *            The type of key used by the tree.
     * @param <V>
     *            The type of value used by the tree.
     */
    protected class TreeEntryImpl<K, V> extends DatabaseEntryImpl<K, V, Tree<K, V>> implements TreeEntry<K, V> {

        /**
         * Initializes a tree entry for the given storage tree, under the given name,
         * with the given translators.
         * 
         * @param name
         *            The name that the storage tree is registered under.
         * @param tree
         *            The storage tree.
         * @param keyTranslator
         *            The translator for keys.
         * @param valueTranslator
         *            The translator for values.
         * @throws NullPointerException
         *             If any of the arguments is <tt>null</tt>.
         */
        public TreeEntryImpl( String name, Tree<K, V> tree, Translator<K> keyTranslator, Translator<V> valueTranslator )
                throws NullPointerException {

            super( name, tree, keyTranslator, valueTranslator );

        }

    }

    /**
     * Implementation of a database map entry.
     * 
     * @since 2018-07-26
     * @param <K>
     *            The type of key used by the map.
     * @param <V>
     *            The type of value used by the map.
     */
    protected class MapEntryImpl<K, V> extends DatabaseEntryImpl<K, V, Map<K, V>> implements MapEntry<K, V> {

        /**
         * Initializes a map entry for the given storage map, under the given name, with
         * the given translators.
         * 
         * @param name
         *            The name that the storage unit is registered under.
         * @param map
         *            The storage map.
         * @param keyTranslator
         *            The translator for keys.
         * @param valueTranslator
         *            The translator for values.
         * @throws NullPointerException
         *             If any of the arguments is <tt>null</tt>.
         */
        public MapEntryImpl( String name, Map<K, V> map, Translator<K> keyTranslator, Translator<V> valueTranslator )
                throws NullPointerException {

            super( name, map, keyTranslator, valueTranslator );

        }

    }

    /* Special cache that keeps track of stats */

    /**
     * Cache that uses the {@link AbstractDatabase#CACHE_SIZE set size}, and
     * provides a {@link #fetch(Object,Function,Predicate)} method that
     * automatically fetches a value from the database if it is not cached (and
     * caches it), and keeps statistics about the performance of fetch calls through
     * {@link DatabaseStats}.
     * <p>
     * This extension of the cache class is also thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <K>
     *            Type of keys in the cache.
     * @param <V>
     *            Type of values stored in the cache.
     */
    private class DatabaseCache<K, V> extends Cache<K, V> {

        /**
         * Instantiates a cache.
         */
        public DatabaseCache() {

            super( CACHE_SIZE );

        }

        /**
         * Fetches a value. If the given key is present on this cache, returns the value
         * associated with it. Else, calls the given Function to fetch the value from
         * the database, and caches the mapping. If the Function returns <tt>null</tt>,
         * uses the given Predicate to determine whether the key has no mapping in the
         * database or if the value is actually <tt>null</tt>.
         *
         * @param key
         *            The key to search for.
         * @param fetcher
         *            The function to use for searching the database for the key if it
         *            is not cached.
         * @param existenceCheck
         *            The predicate to use to determine if the key has a mapping in the
         *            database (only used if the <tt>fetcher</tt> returns
         *            <tt>null</tt>).
         * @return The value associated with the given key, or <tt>null</tt> if there is
         *         no such value. Note that a return of <tt>null</tt> does not
         *         necessarily mean there is no mapping for the key, it may just be
         *         mapped to the value <tt>null</tt>.
         */
        public synchronized V fetch( Object key, Function<Object, V> fetcher, Predicate<Object> existenceCheck ) {

            V value = get( key ); // Look in cache.

            if ( value == null ) { // Not in cache.
                long start = System.currentTimeMillis();
                value = fetcher.apply( key ); // Request fetch.
                long elapsed = System.currentTimeMillis() - start;

                if ( ( value == null ) && !existenceCheck.test( key ) ) { // Fetch fail.
                    DatabaseStats.addDbFetchFailure( elapsed );
                } else { // Fetch success.
                    DatabaseStats.addDbFetchSuccess( elapsed );
                    DatabaseStats.addCacheMiss(); // Value exists, just wasn't in cache.
                    @SuppressWarnings( "unchecked" ) // If it exists, assume proper type.
                    K theKey = (K) key;
                    put( theKey, value ); // Cache found value.
                }
            } else { // Found in cache.
                DatabaseStats.addCacheHit();
            }

            return value;

        }

    }

    /* Pass-through wrappers that check for the database being closed */

    /**
     * Iterator that iterates over data in the database.
     * <p>
     * When a call is made to the iterator, it checks if the database is already
     * closed. If it is, the call fails with a {@link IllegalStateException}. Else,
     * the call is passed through to the backing iterator.
     * <p>
     * This wrapper is <b>not</b> thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-07-27
     * @param <E>
     *            The type of object that the iterator retrieves.
     */
    private class DatabaseIterator<E> implements Iterator<E> {

        private final Iterator<E> backing;
        private final Cache<?, ?> cache;

        /**
         * Instantiates an iterator backed by the given database iterator.
         * 
         * @param backing
         *            The iterator that backs this.
         * @param cache
         *            The cache being used by the data.
         */
        public DatabaseIterator( Iterator<E> backing, Cache<?, ?> cache ) {

            this.backing = backing;
            this.cache = cache;

        }

        @Override
        public boolean hasNext() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.hasNext();

        }

        @Override
        public E next() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.next();

        }

        @Override
        public void remove() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            backing.remove();
            cache.clear(); // Invalidate cache.

        }

        @Override
        public boolean equals( Object o ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.equals( o );

        }

        @Override
        public int hashCode() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.hashCode();

        }

    }

    /**
     * Collection that represents data in the database.
     * <p>
     * When a call is made to the collection, it checks if the database is already
     * closed. If it is, the call fails with a {@link IllegalStateException}. Else,
     * the call is passed through to the backing collection.
     * <p>
     * This wrapper is <b>not</b> thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-07-27
     * @param <E>
     *            The type of object in the collection.
     */
    private class DatabaseCollection<E> implements Collection<E> {

        private final Collection<E> backing;
        private final Cache<?, ?> cache;

        /**
         * Instantiates a collection backed by the given database collection.
         * 
         * @param backing
         *            The collection that backs this.
         * @param cache
         *            The cache being used by the data.
         */
        public DatabaseCollection( Collection<E> backing, Cache<?, ?> cache ) {

            this.backing = backing;
            this.cache = cache;

        }

        @Override
        public int size() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.size();

        }

        @Override
        public boolean isEmpty() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.isEmpty();

        }

        @Override
        public boolean contains( Object o ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.contains( o );

        }

        @Override
        public Iterator<E> iterator() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseIterator<>( backing.iterator(), cache );

        }

        @Override
        public Object[] toArray() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.toArray();

        }

        @Override
        public <T> T[] toArray( T[] a ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.toArray( a );

        }

        @Override
        public boolean add( E e ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.add( e );

        }

        @Override
        public boolean remove( Object o ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear(); // Invalidate cache.
            return backing.remove( o );

        }

        @Override
        public boolean containsAll( Collection<?> c ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.containsAll( c );

        }

        @Override
        public boolean addAll( Collection<? extends E> c ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.addAll( c );

        }

        @Override
        public boolean removeAll( Collection<?> c ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear(); // Invalidate cache.
            return backing.removeAll( c );

        }

        @Override
        public boolean retainAll( Collection<?> c ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear(); // Invalidate cache.
            return backing.retainAll( c );

        }

        @Override
        public void clear() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear(); // Invalidate cache.
            backing.clear();

        }

        @Override
        public boolean equals( Object o ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.equals( o );

        }

        @Override
        public int hashCode() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.hashCode();

        }

        @Override
        public String toString() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.toString();

        }

    }

    /**
     * Set that represents data in the database.
     * <p>
     * When a call is made to the set, it checks if the database is already closed.
     * If it is, the call fails with a {@link IllegalStateException}. Else, the call
     * is passed through to the backing set.
     * <p>
     * This wrapper is <b>not</b> thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-07-27
     * @param <E>
     *            The type of object in the set.
     */
    private class DatabaseSet<E> extends DatabaseCollection<E> implements Set<E> {

        /**
         * Instantiates a set backed by the given database set.
         * 
         * @param backing
         *            The set that backs this.
         * @param cache
         *            The cache being used by the data.
         */
        public DatabaseSet( Set<E> backing, Cache<?, ?> cache ) {

            super( backing, cache );

        }

    }

    /*
     * Wrappers for trees and maps that provides common functionality, such as
     * checking if the database is open and caching data.
     */

    /**
     * Tree that represents data in the database.
     * <p>
     * When a call is made to the tree, it checks if the database is already closed.
     * If it is, the call fails with a {@link IllegalStateException}. Else, the call
     * is passed through to the backing tree.
     * <p>
     * This wrapper is <b>not</b> thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-07-27
     * @param <K>
     *            The type of key in the paths used by the tree.
     * @param <V>
     *            The type of values stored in the tree.
     */
    private class DatabaseTree<K, V> implements Tree<K, V> {

        private final Tree<K, V> backing;
        private final DatabaseCache<List<? extends K>, V> cache;

        /**
         * Instantiates a tree backed by the given database tree.
         * 
         * @param backing
         *            The tree that backs this.
         */
        public DatabaseTree( Tree<K, V> backing ) {

            this.backing = backing;
            this.cache = new DatabaseCache<>();

        }

        @Override
        public boolean containsPath( List<?> path ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.containsKey( path ) || backing.containsPath( path );

        }

        @Override
        public boolean containsValue( Object value ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.containsValue( value ) || backing.containsValue( value );

        }

        @Override
        public V get( List<?> path ) throws IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.fetch( path, p -> backing.get( p ), p -> backing.containsPath( p ) );

        }

        @Override
        public List<V> getAll( List<?> path ) throws IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.getAll( path );

        }

        @Override
        public V put( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.update( path, value ); // Updates previously cached value, if any.

            return backing.put( path, value );

        }

        @Override
        public V putIfAbsent( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            V previous = backing.putIfAbsent( path, value );
            if ( previous == null ) {
                cache.update( path, value );
            }
            return previous;

        }

        @Override
        public V remove( List<?> path ) throws UnsupportedOperationException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.remove( path ); // Remove previously cached value, if any.

            return backing.remove( path );

        }

        @Override
        public Set<List<K>> pathSet() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseSet<>( backing.pathSet(), cache );

        }

        @Override
        public Collection<V> values() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseCollection<>( backing.values(), cache );

        }

        @Override
        public Set<Entry<K, V>> entrySet() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseSet<>( backing.entrySet(), cache );

        }

        @Override
        public int size() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.size();

        }

        @Override
        public boolean isEmpty() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.isEmpty() && backing.isEmpty(); // Use cache as a possible shortcut.

        }

        @Override
        public void clear() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear();
            backing.clear();

        }

        @Override
        public void putAll( Graph<? extends K, ? extends V> g )
                throws UnsupportedOperationException, NullPointerException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            for ( Graph.Entry<? extends K, ? extends V> entry : g.entrySet() ) {
                // Update each entry in the cache.
                cache.update( entry.getPath(), entry.getValue() );

            }
            backing.putAll( g );

        }

        @Override
        public boolean replace( List<K> path, V oldValue, V newValue )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            if ( cache.containsKey( path ) ) { // Mapping is cached.
                if ( Objects.equals( cache.get( path ), oldValue ) ) {
                    cache.update( path, newValue ); // Value matched.
                } else {
                    return false; // Value did not match. Abort.
                }
            }
            return backing.replace( path, oldValue, newValue );

        }

        @Override
        public V replace( List<K> path, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.update( path, value ); // Update cached value, if any.
            return backing.replace( path, value );

        }

        @Override
        public boolean remove( List<?> path, Object value )
                throws UnsupportedOperationException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            if ( cache.containsKey( path ) ) { // Mapping is cached.
                if ( Objects.equals( cache.get( path ), value ) ) {
                    cache.remove( path ); // Value matched.
                } else {
                    return false; // Value did not match. Abort.
                }
            }
            return backing.remove( path, value ); // Delegate to database.

        }

        @Override
        public boolean equals( Object obj ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.equals( obj );

        }

        @Override
        public int hashCode() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.hashCode();

        }

        @Override
        public String toString() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.toString();

        }

    }

    /**
     * Map that represents data in the database.
     * <p>
     * When a call is made to the map, it checks if the database is already closed.
     * If it is, the call fails with a {@link IllegalStateException}. Else, the call
     * is passed through to the backing map.
     * <p>
     * This wrapper is <b>not</b> thread-safe.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-07-27
     * @param <K>
     *            The type of keys used by the map.
     * @param <V>
     *            The type of values stored in the map.
     */
    private class DatabaseMap<K, V> implements Map<K, V> {

        private final Map<K, V> backing;
        private final DatabaseCache<K, V> cache;

        /**
         * Instantiates a map backed by the given database map.
         * 
         * @param backing
         *            The map that backs this.
         */
        public DatabaseMap( Map<K, V> backing ) {

            this.backing = backing;
            this.cache = new DatabaseCache<>();

        }

        @Override
        public int size() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.size();

        }

        @Override
        public boolean isEmpty() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.isEmpty() && backing.isEmpty(); // Use cache as a possible shortcut.

        }

        @Override
        public boolean containsKey( Object key ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.containsKey( key );

        }

        @Override
        public boolean containsValue( Object value ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.containsValue( value );

        }

        @Override
        public V get( Object key ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return cache.fetch( key, k -> backing.get( k ), k -> backing.containsKey( k ) );

        }

        @Override
        public V put( K key, V value ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.update( key, value ); // Update previously cached value, if any.

            return backing.put( key, value );

        }

        @Override
        public V remove( Object key ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.remove( key ); // Remove previously cached value, if any.

            return backing.remove( key );

        }

        @Override
        public void putAll( Map<? extends K, ? extends V> m ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            for ( Map.Entry<? extends K, ? extends V> entry : m.entrySet() ) {
                // Update each entry in the cache.
                cache.update( entry.getKey(), entry.getValue() );

            }
            backing.putAll( m );

        }

        @Override
        public void clear() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.clear();
            backing.clear();

        }

        @Override
        public Set<K> keySet() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseSet<>( backing.keySet(), cache );

        }

        @Override
        public Collection<V> values() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseCollection<>( backing.values(), cache );

        }

        @Override
        public Set<Entry<K, V>> entrySet() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return new DatabaseSet<>( backing.entrySet(), cache );

        }

        @Override
        public boolean replace( K key, V oldValue, V newValue )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            if ( cache.containsKey( key ) ) { // Mapping is cached.
                if ( Objects.equals( cache.get( key ), oldValue ) ) {
                    cache.update( key, newValue ); // Value matched.
                } else {
                    return false; // Value did not match. Abort.
                }
            }
            return backing.replace( key, oldValue, newValue );

        }

        @Override
        public V replace( K key, V value )
                throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            cache.update( key, value ); // Update cached value, if any.
            return backing.replace( key, value );

        }

        @Override
        public boolean remove( Object key, Object value )
                throws UnsupportedOperationException, IllegalArgumentException {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            if ( cache.containsKey( key ) ) { // Mapping is cached.
                if ( Objects.equals( cache.get( key ), value ) ) {
                    cache.remove( key ); // Value matched.
                } else {
                    return false; // Value did not match. Abort.
                }
            }
            return backing.remove( key, value ); // Delegate to database.

        }

        @Override
        public boolean equals( Object o ) {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.equals( o );

        }

        @Override
        public int hashCode() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.hashCode();

        }

        @Override
        public String toString() {

            if ( closed ) {
                throw new IllegalStateException( "The backing database is already closed." );
            }

            return backing.toString();

        }

    }

}
