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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree implementation that uses hashmaps to link keys in the path to nodes.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-14
 * @param <K>
 *            The type of the keys that define connections on the graph.
 * @param <V>
 *            The type of the values to be stored.
 */
public class HashTree<K, V> extends AbstractTree<K, V> {

    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = 8979422862524487918L;
    
    /**
     * Initializes an empty tree.
     */
    public HashTree() {}

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
    public HashTree( Graph<? extends K, ? extends V> g ) throws NullPointerException {

        super( g );

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
    public HashTree( Map<? extends List<? extends K>, ? extends V> m ) throws NullPointerException {

        super( m );

    }

    @Override
    protected Node makeRoot() {

        return new HashNode( null );

    }

    /**
     * Node that uses hashmaps to link keys to children.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-14
     */
    protected class HashNode extends AbstractNode<Map<K, Node>> {

        /**
         * UID that represents this class.
         */
        private static final long serialVersionUID = -4249872204471526081L;

        /**
         * Constructs a Node with the given key, and no value or chilren.
         *
         * @param key
         *            The key of the node.
         */
        public HashNode( K key ) {

            super( key );

        }

        /**
         * Constructs a Node with the given key and children, and no value.
         *
         * @param key
         *            The key of the node.
         * @param children
         *            The children of the node.
         */
        public HashNode( K key, Collection<Node> children ) {

            super( key, children );

        }

        @Override
        protected Map<K, Node> makeChildMap() {

            return new HashMap<>();

        }

        @Override
        protected Node makeInstance( K key ) {

            return new HashNode( key );

        }

    }

}
