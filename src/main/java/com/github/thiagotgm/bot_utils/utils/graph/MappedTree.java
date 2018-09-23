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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tree that uses a map as backing storage, by using the path list as a key.
 * <p>
 * This wrapper is thread-safe if the backing map is, with the exception of the
 * entry set view (which is not thread-safe).
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-28
 * @param <K>
 *            The type of keys in the path.
 * @param <V>
 *            The type of values being stored.
 */
class MappedTree<K, V> extends AbstractGraph<K, V> implements Tree<K, V> {

    private final Map<List<K>, V> backing;

    /**
     * Instantiates a graph that is backed by the given map.
     * 
     * @param backing
     *            The backing map.
     */
    public MappedTree( Map<List<K>, V> backing ) {

        this.backing = backing;

    }

    @Override
    public boolean containsPath( List<?> path ) throws NullPointerException {

        if ( path == null ) {
            throw new NullPointerException( "Path cannot be null." );
        }

        return backing.containsKey( path );

    }

    @Override
    public boolean containsValue( Object value ) {

        return backing.containsValue( value );

    }

    @Override
    public V get( List<?> path ) throws IllegalArgumentException {

        if ( path == null ) {
            throw new NullPointerException( "Path cannot be null." );
        }

        return backing.get( path );

    }

    @Override
    public List<V> getAll( List<?> path ) throws IllegalArgumentException {

        List<V> result = new LinkedList<>();
        for ( int i = 0; i <= path.size(); i++ ) { // Check each step.

            V value = get( path.subList( 0, i ) );
            if ( value != null ) { // Found a value for this step.
                result.add( value );
            }

        }
        return new ArrayList<>( result );

    }

    @Override
    public V put( List<K> path, V value )
            throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {

        if ( path == null ) {
            throw new NullPointerException( "Path cannot be null." );
        }

        return backing.put( path, value );

    }

    @Override
    public V remove( List<?> path ) throws UnsupportedOperationException, IllegalArgumentException {

        return backing.remove( path );

    }

    @Override
    public Set<List<K>> pathSet() {

        return this.backing.keySet();

    }

    @Override
    public Collection<V> values() {

        return backing.values();

    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        final Set<Map.Entry<List<K>, V>> backing = this.backing.entrySet();
        return new AbstractSet<Entry<K, V>>() {

            /**
             * Graph entry backed by a Map entry.
             * 
             * @version 1.0
             * @author ThiagoTGM
             * @since 2018-08-10
             */
            final class BackedEntry extends AbstractEntry<K, V> {

                private final Map.Entry<List<K>, V> backing;

                /**
                 * Instantiates an entry backed by the given Map entry.
                 * 
                 * @param backing
                 *            The backing entry.
                 */
                public BackedEntry( Map.Entry<List<K>, V> backing ) {

                    this.backing = backing;

                }

                @Override
                public List<K> getPath() {

                    return backing.getKey();

                }

                @Override
                public V getValue() {

                    return backing.getValue();

                }

                @Override
                public V setValue( V value ) throws NullPointerException {

                    return backing.setValue( value );

                }

            }

            /**
             * Map entry backed by a Graph entry.
             * 
             * @version 1.0
             * @author ThiagoTGM
             * @since 2018-08-10
             * @param <MK>
             *            Type of the keys in the map.
             * @param <MV>
             *            Type of values stored in the map.
             */
            final class ReverseBackedEntry<MK, MV> implements Map.Entry<List<MK>, MV> {

                private final Entry<MK, MV> backing;

                /**
                 * Instantiates an entry backed by the given Graph entry.
                 * 
                 * @param backing
                 *            The backing entry.
                 */
                public ReverseBackedEntry( Entry<MK, MV> backing ) {

                    this.backing = backing;

                }

                @Override
                public List<MK> getKey() {

                    return backing.getPath();

                }

                @Override
                public MV getValue() {

                    return backing.getValue();

                }

                @Override
                public MV setValue( MV value ) {

                    return backing.setValue( value );

                }

                @Override
                public boolean equals( Object o ) {

                    return ( o instanceof Map.Entry ) && getKey().equals( ( (Map.Entry<?, ?>) o ).getKey() )
                            && getValue().equals( ( (Map.Entry<?, ?>) o ).getValue() );

                }

                @Override
                public int hashCode() {

                    return Objects.hashCode( getKey() ) ^ Objects.hashCode( getValue() );

                }

                @Override
                public String toString() {

                    return String.format( "%s=%s", Objects.toString( getKey() ), Objects.toString( getValue() ) );

                }

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
            public boolean contains( Object o ) {

                if ( !( o instanceof Entry ) ) {
                    return false; // Not an entry.
                }

                return backing.contains( new ReverseBackedEntry<>( (Entry<?, ?>) o ) );

            }

            @Override
            public Iterator<Entry<K, V>> iterator() {

                final Iterator<Map.Entry<List<K>, V>> backingIter = backing.iterator();
                return new Iterator<Entry<K, V>>() {

                    @Override
                    public boolean hasNext() {

                        return backingIter.hasNext();

                    }

                    @Override
                    public Entry<K, V> next() {

                        return new BackedEntry( backingIter.next() );

                    }

                    @Override
                    public void remove() {

                        backingIter.remove();

                    }

                };

            }

            @Override
            public Object[] toArray() {

                return toArray( new Object[0] );

            }

            @Override
            public <T> T[] toArray( T[] a ) {

                return backing.stream().map( e -> new BackedEntry( e ) ).collect( Collectors.toSet() ).toArray( a );

            }

            @Override
            public boolean add( Entry<K, V> e ) {

                throw new UnsupportedOperationException();

            }

            @Override
            public boolean remove( Object o ) {

                if ( !( o instanceof Entry ) ) {
                    return false; // Not an entry.
                }

                return backing.remove( new ReverseBackedEntry<>( (Entry<?, ?>) o ) );

            }

            @Override
            public boolean containsAll( Collection<?> c ) {

                for ( Object o : c ) {

                    if ( !contains( o ) ) {
                        return false;
                    }

                }
                return true;

            }

            @Override
            public boolean addAll( Collection<? extends Entry<K, V>> c ) {

                throw new UnsupportedOperationException();

            }

            @Override
            public boolean retainAll( Collection<?> c ) {

                Collection<Entry<K, V>> removalList = new LinkedList<>();
                for ( Entry<K, V> e : this ) {

                    if ( !c.contains( e ) ) {
                        removalList.add( e );
                    }

                }
                for ( Entry<K, V> e : removalList ) {

                    remove( e );

                }
                return !removalList.isEmpty();

            }

            @Override
            public boolean removeAll( Collection<?> c ) {

                boolean changed = false;
                for ( Object o : c ) {

                    if ( remove( o ) ) {
                        changed = true;
                    }

                }
                return changed;

            }

            @Override
            public void clear() {

                backing.clear();

            }

        };

    }

    @Override
    public int size() {

        return backing.size();

    }

    @Override
    public void clear() {

        backing.clear();

    }

}