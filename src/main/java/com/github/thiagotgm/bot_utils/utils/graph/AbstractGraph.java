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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of common methods in a graph.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-31
 * @param <K>
 *            The type of the keys that define connections on the graph.
 * @param <V>
 *            The type of the values to be stored.
 */
public abstract class AbstractGraph<K, V> implements Graph<K, V> {

    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof Graph ) ) {
            return false; // Not a Graph instance.
        }

        Graph<?, ?> graph = (Graph<?, ?>) obj;
        return this.entrySet().equals( graph.entrySet() );

    }

    @Override
    public int hashCode() {

        int hash = 0;
        for ( Entry<K, V> entry : entrySet() ) {
            // Adds the hash of each entry.
            hash += entry.hashCode();

        }
        return hash;

    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder( entrySet().toString() );
        builder.setCharAt( 0, '{' ); // Set ends of mapping list.
        builder.setCharAt( builder.length() - 1, '}' );
        return builder.toString();

    }

    /**
     * Shared implementation of an entry in the graph.
     *
     * @version 1.0
     * @author ThiagoTGM
     * @since 2017-08-20
     * @param <K>
     *            Type of keys in the path.
     * @param <V>
     *            Type of values being stored.
     */
    protected static abstract class AbstractEntry<K, V> implements Entry<K, V> {

        private final List<K> path;

        /**
         * Constructs a new entry with no path.
         * <p>
         * Should only be used by subclasses that override the {@link #getPath()}
         * method.
         */
        protected AbstractEntry() {

            this.path = null;

        }

        /**
         * Constructs a new entry for the given path.
         *
         * @param path
         *            The path of this entry.
         */
        public AbstractEntry( List<? extends K> path ) {

            this.path = Collections.unmodifiableList( new ArrayList<>( path ) );

        }

        @Override
        public List<K> getPath() {

            return path;

        }

        @Override
        public boolean equals( Object obj ) {

            if ( !( obj instanceof Entry ) ) {
                return false; // Not an Entry instance.
            }

            Entry<?, ?> entry = (Entry<?, ?>) obj;
            return Objects.equals( this.getPath(), entry.getPath() )
                    && Objects.equals( this.getValue(), entry.getValue() );

        }

        @Override
        public int hashCode() {

            return Objects.hashCode( getPath() ) ^ Objects.hashCode( getValue() );

        }

        @Override
        public String toString() {

            return String.format( "%s=%s", Objects.toString( getPath() ), Objects.toString( getValue() ) );

        }

    }

    /**
     * An Entry maintaining a path and a value. The value may be changed using the
     * <tt>setValue</tt> method.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-22
     * @param <K>
     *            Type of keys in the path.
     * @param <V>
     *            Type of values being stored.
     */
    public static class SimpleEntry<K, V> extends AbstractEntry<K, V> implements Serializable {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = 1514888613884891268L;
        private V value;

        /**
         * Creates an entry representing a mapping from the specified path to the
         * specified value.
         *
         * @param path
         *            The path represented by this entry.
         * @param value
         *            The value represented by this entry.
         */
        public SimpleEntry( List<? extends K> path, V value ) {

            super( path );
            this.value = value;

        }

        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry
         *            The entry to copy.
         */
        public SimpleEntry( Graph.Entry<? extends K, ? extends V> entry ) {

            this( entry.getPath(), entry.getValue() );

        }

        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry
         *            The entry to copy.
         */
        public SimpleEntry( Map.Entry<? extends List<? extends K>, ? extends V> entry ) {

            this( entry.getKey(), entry.getValue() );

        }

        @Override
        public V getValue() {

            return value;

        }

        @Override
        public V setValue( V value ) {

            V old = this.value;
            this.value = value;
            return old;

        }

    }

    /**
     * An Entry maintaining an immutable key and value. This class does not support
     * method <tt>setValue</tt>.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-22
     * @param <K>
     *            Type of keys in the path.
     * @param <V>
     *            Type of values being stored.
     */
    public static class SimpleImmutableEntry<K, V> extends SimpleEntry<K, V> {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -7374205275784152408L;

        /**
         * Creates an entry representing a mapping from the specified path to the
         * specified value.
         *
         * @param path
         *            The path represented by this entry.
         * @param value
         *            The value represented by this entry.
         */
        public SimpleImmutableEntry( List<? extends K> path, V value ) {

            super( path, value );

        }

        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry
         *            The entry to copy.
         */
        public SimpleImmutableEntry( Graph.Entry<? extends K, ? extends V> entry ) {

            super( entry );

        }

        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry
         *            The entry to copy.
         */
        public SimpleImmutableEntry( Map.Entry<? extends List<? extends K>, ? extends V> entry ) {

            super( entry );

        }

        @Override
        public V setValue( V value ) throws UnsupportedOperationException {

            throw new UnsupportedOperationException( "Immutable entry cannot be modified." );

        }

    }

}
