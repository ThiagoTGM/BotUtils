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

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * General implementation of the {@link Tree} interface. The root node (empty
 * path) is accessible and <tt>null</tt> values are allowed.
 * <p>
 * When the set or collection views are iterated over, it performs a depth-first
 * search, which means that, for each node, it will visit the node itself, then
 * recurse on each of its children. The behavior that is left for subclasses is
 * how nodes will store their children, which will affect the order that a
 * node's children are visited.
 * <p>
 * Can only be serialized properly if all the values stored are also
 * Serializable.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-16
 * @param <K>
 *            The type of the keys that define connections on the graph.
 * @param <V>
 *            The type of the values to be stored.
 */
public abstract class AbstractTree<K, V> extends AbstractGraph<K, V> implements Tree<K, V>, Serializable {

    /**
     * UID that represents the class.
     */
    private static final long serialVersionUID = -8738939021703848608L;

    /**
     * Node that is the root of the tree.
     */
    protected Node root;

    /**
     * How many mappings are stored in this graph.
     */
    protected int nMappings;

    /**
     * Initializes an empty tree.
     */
    public AbstractTree() {

        this.root = makeRoot();
        this.nMappings = 0;

    }

    /**
     * Initializes a tree with the mappings from the given graph.
     *
     * @param g
     *            The graph to initialize this with.
     * @throws NullPointerException
     *             if the specified graph is <tt>null</tt>, or if this tree does not
     *             permit <tt>null</tt> keys or values, and the specified graph
     *             contains <tt>null</tt> keys or values.
     */
    public AbstractTree( Graph<? extends K, ? extends V> g ) throws NullPointerException {

        this();
        putAll( g );

    }

    /**
     * Initializes a tree with the mappings from the given map.
     *
     * @param m
     *            The map to initialize this with.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, or if the specified map
     *             contains <tt>null</tt> keys, or if this tree does not permit
     *             <tt>null</tt> path elements or values, and the specified map
     *             contains <tt>null</tt> path elements or values.
     */
    public AbstractTree( Map<? extends List<? extends K>, ? extends V> m ) throws NullPointerException {

        this();
        putAll( m );

    }

    /**
     * Creates the root of the tree. It should contain no value or children.
     * 
     * @return The root.
     */
    protected abstract Node makeRoot();

    @Override
    public boolean containsValue( Object value ) {

        return root.findValue( value, new Stack<>() ) != null;

    }

    /**
     * Retrieves the descendant of a given element that represent the given sequence
     * of keys.
     *
     * @param parent
     *            The parent element.
     * @param path
     *            The sequence of keys that represent the descendants.
     * @return The descendant. If no keys given, the parent.<br>
     *         If there is not an element that corresponds to the given path, null.
     */
    protected Node getDescendant( Node parent, List<?> path ) {

        Node element = parent;
        for ( Object next : path ) {

            element = element.getChild( next );
            if ( element == null ) {
                return null;
            }

        }
        return element;

    }

    /**
     * Retrieves the descendant of the root element that represent the given
     * sequence of keys.
     *
     * @param path
     *            The sequence of keys that represent the descendants.
     * @return The descendant. If no keys given, the root.<br>
     *         If there is not an element that corresponds to the given path, null.
     */
    protected Node getDescendant( List<?> path ) {

        return getDescendant( root, path );

    }

    /**
     * Retrieves the descendant of a given element that represent the given sequence
     * of keys.<br>
     * If there is no descendant that corresponds to the full path, retrieves the
     * one that corresponds to as much of it as possible (without skipping parts of
     * the path). May be the given element itself.
     *
     * @param parent
     *            The parent element.
     * @param path
     *            The sequence of keys that represent the descendants.
     * @return The farthest descendant found. If no keys given, the parent.
     */
    protected Node getMaxDescendant( Node parent, List<?> path ) {

        Node element = parent;
        for ( Object obj : path ) {

            Node child = element.getChild( obj );
            if ( child == null ) {
                return element;
            }
            element = child;

        }
        return element;

    }

    /**
     * Retrieves the descendant of the root element that represent the given
     * sequence of keys.<br>
     * If there is no descendant that corresponds to the full path, retrieves the
     * one that corresponds to as much of it as possible (without skipping parts of
     * the path). May be the root iself.
     *
     * @param path
     *            The sequence of keys that represent the descendants.
     * @return The farthest descendant found. If no keys given, the root.
     */
    protected Node getMaxDescendant( List<?> path ) {

        return getMaxDescendant( root, path );

    }

    /**
     * Retrieves the descendant of a given element that represent the given sequence
     * of keys.<br>
     * If the child doesn't exist, creates it (as well as intermediate descendants
     * as necessary).
     *
     * @param parent
     *            The parent element.
     * @param path
     *            The sequence of objects that represent the descendants.
     * @return The descendant. If no objects given, the parent.
     */
    protected Node getOrCreateDescendant( Node parent, List<? extends K> path ) {

        Node element = parent;
        for ( K obj : path ) {

            element = element.getOrCreateChild( obj );

        }
        return element;

    }

    /**
     * Retrieves the descendant of the root element that represent the given
     * sequence of objects.<br>
     * If the child doesn't exist, creates it (as well as intermediate descendants
     * as necessary).
     *
     * @param path
     *            The sequence of objects that represent the descendants.
     * @return The descendant. If no objects given, the root.
     */
    protected Node getOrCreateDescendant( List<? extends K> path ) {

        return getOrCreateDescendant( root, path );

    }

    @Override
    public boolean containsPath( List<?> path ) {

        Node node = getDescendant( path );
        return ( node == null ) ? false : node.hasValue();

    }

    @Override
    public V get( List<?> path ) {

        Node node = getDescendant( path );
        return ( node == null ) ? null : node.getValue();

    }

    @Override
    public List<V> getAll( List<?> path ) {

        Node cur = root;
        List<V> values = new LinkedList<>();
        for ( Object key : path ) {

            cur = cur.getChild( key );
            if ( cur != null ) { // Add child's value, if there is one.
                if ( cur.getValue() != null ) {
                    values.add( cur.getValue() );
                }
            } else {
                break; // No child for this key.
            }

        }
        return values;

    }

    @Override
    public V put( List<K> path, V value ) {

        V old = getOrCreateDescendant( path ).setValue( value );
        if ( old == null ) { // There wasn't a mapping to this path yet,
            nMappings++; // so a new mapping was added.
        }
        return old;

    }

    @Override
    public V putIfAbsent( List<K> path, V value ) {

        Node node = getOrCreateDescendant( path );
        V curValue = node.getValue();
        if ( curValue == null ) {
            node.setValue( value );
            nMappings++; // A mapping was added.
        }
        return curValue;

    }

    @Override
    public V remove( List<?> path ) {

        Stack<Node> nodes = new Stack<>();
        Node cur = root;
        for ( Object key : path ) {

            if ( cur != null ) {
                nodes.add( cur );
            } else {
                return null; // Path has no mapping.
            }
            cur = cur.getChild( key );

        }

        if ( ( cur == null ) || !cur.hasValue() ) {
            return null; // There is already no value for this path.
        }
        V value = cur.removeValue(); // Delete its value.

        for ( int i = path.size() - 1; i >= 0; i-- ) { // Cleans up any nodes that became irrelevant.

            if ( !cur.hasValue() && cur.getChildren().isEmpty() ) {
                cur = nodes.pop(); // Node has no value or children now, so delete it.
                cur.removeChild( path.get( i ) );
            } else {
                break; // Found a node that can't be deleted.
            }

        }

        nMappings--; // A mapping was removed.
        return value; // Retrieve deleted value.

    }

    @Override
    public Set<List<K>> pathSet() {

        return new AbstractSet<List<K>>() {

            @Override
            public int size() {

                return AbstractTree.this.size();

            }

            @Override
            public boolean isEmpty() {

                return AbstractTree.this.isEmpty();

            }

            @Override
            @SuppressWarnings( "unchecked" )
            public boolean contains( Object o ) {

                if ( o instanceof List ) {
                    return containsPath( (List<K>) o );
                } else {
                    return false; // Wrong type.
                }

            }

            @Override
            public Iterator<List<K>> iterator() {

                final Iterator<Entry<K, V>> backing = entrySet().iterator();
                return new Iterator<List<K>>() {

                    @Override
                    public boolean hasNext() {

                        return backing.hasNext();

                    }

                    @Override
                    public List<K> next() {

                        return backing.next().getPath();

                    }

                    @Override
                    public void remove() {

                        backing.remove();

                    }

                };

            }

            @Override
            public Object[] toArray() {

                return toArray( new Object[0] );

            }

            @Override
            public <T> T[] toArray( T[] a ) {

                List<List<K>> paths = new LinkedList<>();
                for ( List<K> path : this ) {
                    
                    paths.add( path );
                    
                }
                return paths.toArray( a );

            }

            @Override
            public boolean add( List<K> e ) {

                throw new UnsupportedOperationException( "Set view does not support adding." );

            }

            @Override
            public boolean remove( Object o ) {

                if ( contains( o ) ) {
                    @SuppressWarnings( "unchecked" )
                    List<K> path = (List<K>) o; // Being in graph implies right type.
                    AbstractTree.this.remove( path );
                    return true; // Removed entry.
                } else {
                    return false; // Not contained in graph.
                }

            }

            @Override
            public boolean containsAll( Collection<?> c ) {

                for ( Object o : c ) {

                    if ( !contains( o ) ) {
                        return false; // Found element that is not contained.
                    }

                }
                return true; // All contained.

            }

            @Override
            public boolean addAll( Collection<? extends List<K>> c ) {

                throw new UnsupportedOperationException( "Set view does not support adding." );

            }

            @Override
            public boolean retainAll( Collection<?> c ) {

                List<List<? extends K>> toRemove = new LinkedList<>();
                for ( List<? extends K> path : this ) {

                    if ( !c.contains( path ) ) { // Not in given collection.
                        toRemove.add( path ); // Mark for deletion.
                    }

                }

                for ( List<? extends K> path : toRemove ) {

                    remove( path ); // Remove each marked entry.

                }

                return !toRemove.isEmpty();

            }

            @Override
            public boolean removeAll( Collection<?> c ) {

                boolean changed = false;
                for ( Object o : c ) {

                    if ( remove( o ) ) { // Try to remove element.
                        changed = true;
                    }

                }
                return changed;

            }

            @Override
            public void clear() {

                AbstractTree.this.clear();

            }

        };

    }

    @Override
    public Collection<V> values() {

        return new AbstractCollection<V>() {

            @Override
            public int size() {

                return AbstractTree.this.size();

            }

            @Override
            public boolean isEmpty() {

                return AbstractTree.this.isEmpty();

            }

            @Override
            @SuppressWarnings( "unchecked" )
            public boolean contains( Object o ) {

                try {
                    return containsValue( (V) o );
                } catch ( ClassCastException e ) {
                    return false; // Wrong type.
                }

            }

            @Override
            public Iterator<V> iterator() {

                final Iterator<Entry<K, V>> backing = entrySet().iterator();
                return new Iterator<V>() {

                    @Override
                    public boolean hasNext() {

                        return backing.hasNext();

                    }

                    @Override
                    public V next() {

                        return backing.next().getValue();

                    }

                    @Override
                    public void remove() {

                        backing.remove();

                    }

                };

            }

            @Override
            public Object[] toArray() {

                return toArray( new Object[0] );

            }

            @Override
            public <T> T[] toArray( T[] a ) {

                List<V> values = new LinkedList<>();
                for ( V value : this ) {
                    
                    values.add( value );
                    
                }
                return values.toArray( a );

            }

            @Override
            public boolean add( V e ) {

                throw new UnsupportedOperationException( "Collection view does not support adding." );

            }

            @Override
            public boolean remove( Object o ) {

                if ( contains( o ) ) {
                    @SuppressWarnings( "unchecked" )
                    V value = (V) o; // Being in graph implies right type.
                    AbstractTree.this.remove( root.findValue( value, new Stack<K>() ) );
                    return true; // Removed entry.
                } else {
                    return false; // Not contained in graph.
                }

            }

            @Override
            public boolean containsAll( Collection<?> c ) {

                for ( Object o : c ) {

                    if ( !contains( o ) ) {
                        return false; // Found element that is not contained.
                    }

                }
                return true; // All contained.

            }

            @Override
            public boolean addAll( Collection<? extends V> c ) {

                throw new UnsupportedOperationException( "Collection view does not support adding." );

            }

            @Override
            public boolean retainAll( Collection<?> c ) {

                List<V> toRemove = new LinkedList<>();
                for ( V value : this ) {

                    if ( !c.contains( value ) ) { // Not in given collection.
                        toRemove.add( value ); // Mark for deletion.
                    }

                }

                for ( V value : toRemove ) {

                    remove( value ); // Remove each marked entry.

                }

                return !toRemove.isEmpty();

            }

            @Override
            public boolean removeAll( Collection<?> c ) {

                boolean changed = false;
                for ( Object o : c ) {

                    if ( remove( o ) ) { // Try to remove element.
                        changed = true;
                    }

                }
                return changed;

            }

            @Override
            public void clear() {

                AbstractTree.this.clear();

            }

        };

    }

    /**
     * Retrieves the set of path-value mappings <i>currently</i> stored in this
     * graph. Changes to the graph are <i>not</i> reflected in the set and
     * vice-versa, except for the {@link Graph.Entry#setValue(Object)
     * Entry.setValue(V)} method.
     * 
     * @return The current entry set.
     */
    protected Set<Entry<K, V>> getEntries() {

        Set<Entry<K, V>> entries = new HashSet<>();
        root.getEntries( entries, new Stack<>() ); // Get entries.
        return entries;

    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        return new AbstractSet<Entry<K, V>>() {

            @Override
            public int size() {

                return AbstractTree.this.size();

            }

            @Override
            public boolean isEmpty() {

                return AbstractTree.this.isEmpty();

            }

            @Override
            @SuppressWarnings( "unchecked" )
            public boolean contains( Object o ) {

                if ( o instanceof Entry ) {
                    Entry<K, V> entry = (Entry<K, V>) o;
                    V mapped = AbstractTree.this.get( entry.getPath() );
                    // Return null means no entry for the path.
                    return mapped == null ? false : mapped.equals( entry.getValue() );
                } else {
                    return false;
                }

            }

            @Override
            public Iterator<Entry<K, V>> iterator() {

                Set<Entry<K, V>> entries = getEntries();
                final Iterator<Entry<K, V>> backing = entries.iterator();
                return new Iterator<Entry<K, V>>() {

                    private Entry<K, V> last = null;

                    @Override
                    public boolean hasNext() {

                        return backing.hasNext();

                    }

                    @Override
                    public Entry<K, V> next() {

                        Entry<K, V> next = backing.next();
                        last = next;
                        return next;

                    }

                    @Override
                    public void remove() {

                        backing.remove(); // Delegate validity checks.
                        AbstractTree.this.remove( last.getPath() );

                    }

                };

            }

            @Override
            public Object[] toArray() {

                return toArray( new Object[0] );

            }

            @Override
            public <T> T[] toArray( T[] a ) throws ArrayStoreException {

                List<Entry<K,V>> entries = new LinkedList<>();
                for ( Entry<K,V> entry : this ) {
                    
                    entries.add( entry );
                    
                }
                return entries.toArray( a );

            }

            @Override
            public boolean add( Entry<K, V> e ) {

                throw new UnsupportedOperationException( "Set view does not support adding." );

            }

            @Override
            public boolean remove( Object o ) {

                if ( contains( o ) ) {
                    @SuppressWarnings( "unchecked" )
                    Entry<K, V> entry = (Entry<K, V>) o; // Being in graph implies right type.
                    AbstractTree.this.remove( entry.getPath() );
                    return true; // Removed entry.
                } else {
                    return false; // Not contained in graph.
                }

            }

            @Override
            public boolean containsAll( Collection<?> c ) {

                for ( Object o : c ) {

                    if ( !contains( o ) ) {
                        return false; // Found element that is not contained.
                    }

                }
                return true; // All contained.

            }

            @Override
            public boolean addAll( Collection<? extends Entry<K, V>> c ) {

                throw new UnsupportedOperationException( "Set view does not support adding." );

            }

            @Override
            public boolean retainAll( Collection<?> c ) {

                List<Entry<K, V>> toRemove = new LinkedList<>();
                for ( Entry<K, V> entry : this ) {

                    if ( !c.contains( entry ) ) { // Not in given collection.
                        toRemove.add( entry ); // Mark for deletion.
                    }

                }

                for ( Entry<K, V> entry : toRemove ) {

                    remove( entry ); // Remove each marked entry.

                }

                return !toRemove.isEmpty();

            }

            @Override
            public boolean removeAll( Collection<?> c ) {

                boolean changed = false;
                for ( Object o : c ) {

                    if ( remove( o ) ) { // Try to remove element.
                        changed = true;
                    }

                }
                return changed;

            }

            @Override
            public void clear() {

                AbstractTree.this.clear();

            }

        };

    }

    @Override
    public int size() {

        return nMappings;

    }

    @Override
    public void clear() {

        this.root = makeRoot(); // Delete all nodes.
        this.nMappings = 0; // Reset counter.

    }

    /**
     * Writes the state of this instance to a stream.
     *
     * @param out
     *            The stream to write data to.
     * @throws IOException
     *             if there is an error while writing the state.
     */
    private void writeObject( java.io.ObjectOutputStream out ) throws IOException {

        out.writeObject( root );
        out.writeInt( nMappings );

    }

    /**
     * Reads the state of this instance from a stream.
     *
     * @param in
     *            The stream to read data from.
     * @throws IOException
     *             if there is an error while reading the state.
     * @throws ClassNotFoundException
     *             if the class of a serialized value object cannot be found.
     * @see #writeObject(java.io.ObjectOutputStream)
     */
    @SuppressWarnings( "unchecked" )
    private void readObject( java.io.ObjectInputStream in ) throws IOException, ClassNotFoundException {

        try { // Make a copy so the outer reference is set correctly.
            this.root = makeRoot();
            this.root.copy( (Node) in.readObject() );
        } catch ( ClassCastException e ) {
            throw new IOException( "Deserialized node value is not of the expected type.", e );
        }
        this.nMappings = in.readInt();

    }

    /**
     * Initializes instance data when this class is needed for deserialization but
     * there is no data available.
     * <p>
     * Key and value are initialized to null, and the map of children is empty.
     *
     * @throws ObjectStreamException
     *             if an error occurred.
     */
    @SuppressWarnings( "unused" )
    private void readObjectNoData() throws ObjectStreamException {

        this.root = makeRoot();
        this.nMappings = 0;

    }

    /**
     * A node in the tree.
     * <p>
     * Can only be serialized properly if the value stored is also Serializable.
     *
     * @version 1.2
     * @author ThiagoTGM
     * @since 2017-08-17
     */
    protected abstract class Node implements Serializable {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -3843170009565470940L;

        /**
         * Copies all the data in the given node to this node. That is, after this
         * method returns, this node will have the same value (if any) and the same
         * children (if any) as the given node. Any value or children currently existing
         * in this node is erased.
         * <p>
         * <b>NOTE:</b> The values and keys that will be stored in this node and its
         * children are the same instances as the ones in the given node and its
         * children, but the child nodes added to this node will be <b>new</b>
         * instances, that copy the children of the given node. That is, this copy is a
         * deep copy.
         * 
         * @param n
         *            The node to copy.
         */
        public abstract void copy( Node n );

        /**
         * Retrieves the key that identifies this node.
         *
         * @return The key of the node.
         */
        public abstract K getKey();

        /**
         * Retrieves the value of the node.
         * <p>
         * <b>NOTE:</b> A return value of <tt>null</tt> does not necessarily indicate
         * the lack of a value, it can just be the value <tt>null</tt>.
         * {@link #hasValue()} should be used to determine if this node has a value.
         *
         * @return The value of the node, or <tt>null</tt> if none.
         */
        public abstract V getValue();

        /**
         * Determines whether this node currently stores a value.
         * 
         * @return <tt>true</tt> if this node is storing a value.
         */
        public abstract boolean hasValue();

        /**
         * Sets the value of the node.
         * <p>
         * <b>NOTE:</b> A return value of <tt>null</tt> does not necessarily indicate
         * that no value existed, it can just be the value <tt>null</tt>.
         * {@link #hasValue()} should be used beforehand to determine if this node has a
         * value.
         *
         * @param value
         *            The new value of the node.
         * @return The previous value of the node, or <tt>null</tt> if none.
         */
        public abstract V setValue( V value );

        /**
         * Removes the value of the node.
         * <p>
         * <b>NOTE:</b> A return value of <tt>null</tt> does not necessarily indicate
         * that no value existed, it can just be the value <tt>null</tt>.
         * {@link #hasValue()} should be used beforehand to determine if this node has a
         * value.
         *
         * @return The previous value of the node, or <tt>null</tt> if none.
         */
        public abstract V removeValue();

        /**
         * Gets the child of this node that corresponds to the given key.
         *
         * @param key
         *            The key to get the child for.
         * @return The child that corresponds to the given key, or <tt>null</tt> if
         *         there is no such child.
         */
        public abstract Node getChild( Object key );

        /**
         * Retrieves all the children of this node.
         *
         * @return The children of this node.
         */
        public abstract Collection<Node> getChildren();

        /**
         * Gets the child of this node that corresponds to the given key.<br>
         * Creates it if it does not exist.
         *
         * @param key
         *            The key to get the child for.
         * @return The child that corresponds to the given key.
         */
        public abstract Node getOrCreateChild( K key );

        /**
         * Sets the value of the child node that corresponds to the given key to the
         * given value. If there is no node that corresponds to the given key, creates
         * one.
         *
         * @param key
         *            The key to get the child for.
         * @param value
         *            The value to set for that child.
         * @return The previous value in the child, or <tt>null</tt> if none.
         */
        public abstract V setChild( K key, V value );

        /**
         * Adds a child node that corresponds to the given key and has the given
         * value.<br>
         * If there is already a child for that key, does nothing.
         *
         * @param key
         *            The key the child corresponds to.
         * @param value
         *            The value of the child.
         * @return true if the child was added. false if there is already a child that
         *         corresponds to the given key.
         */
        public abstract boolean addChild( K key, V value );

        /**
         * Removes the child node that corresponds to the given key.
         *
         * @param key
         *            The key the child corresponds to.
         * @return The deleted child, or null if there is no child for that key.
         */
        public abstract Node removeChild( Object key );

        /**
         * Attempts to find a value in the subtree rooted by this node, returning the
         * total path to the node that contains that value.
         * 
         * @param value
         *            The value to find.
         * @param path
         *            The path to this node's parent (should be empty if this is the
         *            root of the full tree).
         * @return The path to the node that contains the given value, or <tt>null</tt>
         *         if there is no node in this subtree with that value.
         */
        public abstract List<K> findValue( Object value, Stack<K> path );

        /**
         * Retrieves the path-value mapping entries for this node and its children,
         * placing them into the given entry set.
         *
         * @param entries
         *            The set to place the entries in.
         * @param path
         *            The path that maps to this node, where the bottom of the stack is
         *            the beginning of the path.
         */
        public abstract void getEntries( Set<Entry<K, V>> entries, Stack<K> path );

        @Override
        public String toString() {

            return String.format( "Key: \"%s\"; Value: %s; Children: \"%s\"", Objects.toString( getKey() ),
                    hasValue() ? "\"" + Objects.toString( getValue() ) + "\"" : "none",
                    Objects.toString( getChildren() ) );

        }

    }

    /**
     * Overall implementation of a node. Child nodes will be obtained using
     * {@link #makeInstance(Object) makeInstance(K)}, so their type will be of
     * whatever type that method returns.
     * <p>
     * Can only be serialized properly if the value stored is also Serializable.
     *
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     * @param <T>
     *            The type of map that should be used to store children.
     */
    protected abstract class AbstractNode<T extends Map<K, Node>> extends Node {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = 8085251836873812411L;

        /**
         * Key that identifies this node in its parent.
         */
        protected K key;

        /**
         * Value stored inside this Node.
         */
        protected V value;

        /**
         * Whether this node has a value.
         */
        protected boolean hasValue;

        /**
         * Children nodes of this Node.
         */
        protected T children;

        /**
         * Constructs a Node with the given key, and no value or chilren.
         *
         * @param key
         *            The key of the node.
         */
        public AbstractNode( K key ) {

            this.key = key;
            this.value = null;
            this.hasValue = false;
            this.children = makeChildMap();

        }

        /**
         * Constructs a Node with the given key and children, and no value.
         *
         * @param key
         *            The key of the node.
         * @param children
         *            The children of the node.
         */
        public AbstractNode( K key, Collection<Node> children ) {

            this( key );
            for ( Node child : children ) {

                this.children.put( child.getKey(), child );

            }

        }

        /**
         * Creates the map to store children in.
         * 
         * @return The map.
         */
        protected abstract T makeChildMap();

        /**
         * Creates a new instance to use as a child.
         * 
         * @param key
         *            The key of the instance.
         * @return The new instance.
         */
        protected abstract Node makeInstance( K key );

        @Override
        public void copy( Node n ) {

            this.key = n.getKey();
            this.value = n.getValue();
            this.hasValue = n.hasValue();

            this.children.clear();
            for ( Node child : n.getChildren() ) { // Copy each child.

                Node newChild = makeInstance( null );
                newChild.copy( child );
                this.children.put( newChild.getKey(), newChild );

            }

        }

        @Override
        public K getKey() {

            return key;

        }

        @Override
        public V getValue() {

            return value;

        }

        @Override
        public boolean hasValue() {

            return hasValue;

        }

        @Override
        public V setValue( V value ) {

            V oldValue = this.value;
            this.value = value;
            this.hasValue = true; // Has a value now.
            return oldValue;

        }

        @Override
        public V removeValue() {

            V oldValue = this.value;
            this.value = null;
            this.hasValue = false; // Does not have a value now.
            return oldValue;

        }

        @Override
        public Node getChild( Object key ) {

            return children.get( key );

        }

        @Override
        public Collection<Node> getChildren() {

            return children.values();

        }

        @Override
        public Node getOrCreateChild( K key ) {

            Node child = children.get( key );
            if ( child == null ) {
                child = makeInstance( key );
                children.put( key, child );
            }
            return child;

        }

        @Override
        public final V setChild( K key, V value ) {

            return getOrCreateChild( key ).setValue( value );

        }

        @Override
        public final boolean addChild( K key, V value ) {

            Node child = getChild( key );
            if ( child != null ) {
                return false; // Child with that key already exists.
            } else {
                setChild( key, value );
                return true;
            }

        }

        @Override
        public Node removeChild( Object key ) {

            return children.remove( key );

        }

        @Override
        public List<K> findValue( Object value, Stack<K> path ) {

            if ( this != root ) { // Root doesn't have a path.
                path.push( getKey() ); // Add this node's path.
            }

            // This node has the value.
            if ( hasValue && ( value == null ? getValue() == null : value.equals( getValue() ) ) ) {
                return new ArrayList<>( path ); // Return current path.
            }

            /* Recursively search each child */
            for ( Node child : getChildren() ) {

                List<K> result = child.findValue( value, path );
                if ( result != null ) {
                    return result; // Found in a subtree.
                }

            }

            if ( this != root ) {
                path.pop(); // Remove this node's path.
            }

            return null; // Not found.

        }

        @Override
        public void getEntries( Set<Entry<K, V>> entries, Stack<K> path ) {

            if ( this != root ) { // Root doesn't have a path.
                path.push( getKey() ); // Add this node's path.
            }

            if ( hasValue() ) { // This node represents a mapping.
                entries.add( new TreeGraphEntry( path, this ) );
            }

            /* Recursively gets entries for each child */
            for ( Node child : getChildren() ) {

                child.getEntries( entries, path );

            }

            if ( this != root ) {
                path.pop(); // Remove this node's path.
            }

        }

        /**
         * Writes the state of this instance to a stream.
         * <p>
         * Writes the key and value if they exist (using a boolean for each to identify
         * whether they do or not exist), then the number of children, then each child
         * node. In this way, the internal Map of children is not serialized, reducing
         * the space overhead.
         *
         * @param out
         *            The stream to write data to.
         * @throws IOException
         *             if there is an error while writing the state.
         */
        private void writeObject( java.io.ObjectOutputStream out ) throws IOException {

            if ( this.key != null ) {
                out.writeBoolean( true ); // Mark that node has a non-null key.
                out.writeObject( key ); // Write the key.
            } else {
                out.writeBoolean( false ); // Mark that node has a null key.
            }
            out.writeBoolean( this.hasValue ); // Mark whether node has a value.
            if ( this.hasValue ) {
                if ( this.value != null ) {
                    out.writeBoolean( true ); // Mark that node has a non-null value.
                    out.writeObject( this.value ); // Write the value.
                } else {
                    out.writeBoolean( false ); // Mark that node has a null value.
                }
            }

            /* Write children */
            Collection<Node> children = getChildren();
            out.writeInt( children.size() ); // Write amount of children.
            for ( Node child : children ) {

                out.writeObject( child ); // Write child.

            }

        }

        /**
         * Reads the state of this instance from a stream.
         *
         * @param in
         *            The stream to read data from.
         * @throws IOException
         *             if there is an error while reading the state.
         * @throws ClassNotFoundException
         *             if the class of a serialized value object cannot be found.
         * @see #writeObject(java.io.ObjectOutputStream)
         */
        private void readObject( java.io.ObjectInputStream in ) throws IOException, ClassNotFoundException {

            if ( in.readBoolean() ) { // Non-null key.
                try {
                    @SuppressWarnings( "unchecked" )
                    K key = (K) in.readObject();
                    this.key = key;
                } catch ( ClassCastException e ) {
                    throw new IOException( "Deserialized node key is not of the expected type.", e );
                }
            } else { // Null key.
                this.key = null;
            }
            this.hasValue = in.readBoolean(); // Check if a value is stored.
            if ( this.hasValue ) {
                if ( in.readBoolean() ) { // Non-null value.
                    try {
                        @SuppressWarnings( "unchecked" )
                        V value = (V) in.readObject();
                        this.value = value;
                    } catch ( ClassCastException e ) {
                        throw new IOException( "Deserialized node value is not of the expected type.", e );
                    }
                } else { // Null value.
                    this.value = null;
                }
            }

            /* Read children */
            int childNum = in.readInt(); // Retrieve amount of children.
            this.children = makeChildMap();
            for ( int i = 0; i < childNum; i++ ) { // Read each child.

                Node child;
                try { // Read child.
                    @SuppressWarnings( "unchecked" )
                    Node tempChild = (Node) in.readObject();
                    child = tempChild;
                } catch ( ClassCastException e ) {
                    throw new IOException( "Deserialized child node is not of the expected type.", e );
                }
                children.put( child.getKey(), child ); // Store child.

            }

        }

        /**
         * Initializes instance data when this class is needed for deserialization but
         * there is no data available.
         * <p>
         * Key and value are initialized to null, and the map of children is empty.
         *
         * @throws ObjectStreamException
         *             if an error occurred.
         */
        @SuppressWarnings( "unused" )
        private void readObjectNoData() throws ObjectStreamException {

            this.children = makeChildMap();
            this.key = null;
            this.value = null;
            this.hasValue = false;

        }

    }

    /**
     * Represents an entry in the TreeGraph.
     *
     * @version 1.0
     * @author ThiagoTGM
     * @since 2017-08-20
     */
    protected class TreeGraphEntry extends AbstractEntry {

        private final Node node;

        /**
         * Constructs a new entry for the given path that is linked to the given node.
         *
         * @param path
         *            The path of this entry.
         * @param node
         *            The node that represents this entry.
         */
        public TreeGraphEntry( List<K> path, Node node ) {

            super( path );
            this.node = node;

        }

        @Override
        public V getValue() {

            return node.getValue();

        }

        @Override
        public V setValue( V value ) throws NullPointerException {

            return node.setValue( value );

        }

    }

}
