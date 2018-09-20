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

package com.github.thiagotgm.bot_utils.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.thiagotgm.bot_utils.utils.Utils;
import com.github.thiagotgm.bot_utils.utils.graph.HashTree;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;

/**
 * Tester class for {@link HashTree}.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-08-19
 */
public class HashTreeTest {
    
    private HashTree<String,String> graph;

    @BeforeEach
    public void setUp() {
        
        graph = new HashTree<>();
        graph.put( "value 1", "hi", "i" );
        graph.put( "value 2", "hi" );
        graph.put( "value 3", "hi", "i", "am", "here" );

    }

    @Test
    public void testPutAndGet() {
        
        Tree<Long,String> graph2 = new HashTree<>();
        Tree<String,Integer> graph3 = new HashTree<>();
        
        /* Test getting */
        assertEquals( "value 1", graph.get( "hi", "i" ), "Incorrect value retrieved." );
        assertEquals( "value 2", graph.get( "hi" ), "Incorrect value retrieved." );
        assertEquals( "value 3", graph.get( "hi", "i", "am", "here" ), "Incorrect value retrieved." );
        assertEquals( null, graph.get( "potato" ), "Incorrect value retrieved." );
        assertEquals( null, graph.get( "potato", "salad" ), "Incorrect value retrieved." );
        assertEquals( null, graph.get( "potato", "salad", "with dressing" ), "Incorrect value retrieved." );
        
        assertEquals( null, graph2.get( 1990L, 420L ), "Incorrect value retrieved." );
        assertEquals( null, graph2.get( 0L, -19L ), "Incorrect value retrieved." );
        
        assertEquals( null, graph3.get( "a", "number" ), "Incorrect value retrieved." );
        assertEquals( null, graph3.get( "another", "number" ), "Incorrect value retrieved." );
        assertEquals( null, graph3.get( "a", "nother", "number" ), "Incorrect value retrieved." );

        /* Test putting */
        assertNull( graph.put( "value 4", "potato" ) );
        assertNull( graph.put( "value 5", "potato", "salad" ) );
        assertNull( graph.put( "value 6", "potato", "salad", "with dressing" ) );
        
        assertNull( graph2.put( "value 1", 1990L, 420L ) );
        assertNull( graph2.put( "value 2", 0L, -19L ) );
        
        assertNull( graph3.put( 90, "a", "number" ) );
        assertNull( graph3.put( 404, "another", "number" ) );
        assertNull( graph3.put( -1, "a", "nother", "number" ) );
        
        /* Test getting again */
        assertEquals( "value 1", graph.get( "hi", "i" ), "Incorrect value retrieved." );
        assertEquals( "value 2", graph.get( "hi" ), "Incorrect value retrieved." );
        assertEquals( "value 3", graph.get( "hi", "i", "am", "here" ), "Incorrect value retrieved." );
        assertEquals( "value 4", graph.get( "potato" ), "Incorrect value retrieved." );
        assertEquals( "value 5", graph.get( "potato", "salad" ), "Incorrect value retrieved." );
        assertEquals( "value 6", graph.get( "potato", "salad", "with dressing" ), "Incorrect value retrieved." );
        
        assertEquals( "value 1", graph2.get( 1990L, 420L ), "Incorrect value retrieved." );
        assertEquals( "value 2", graph2.get( 0L, -19L ), "Incorrect value retrieved." );
        
        assertEquals( new Integer( 90 ), graph3.get( "a", "number" ), "Incorrect value retrieved." );
        assertEquals( new Integer( 404 ), graph3.get( "another", "number" ), "Incorrect value retrieved." );
        assertEquals( new Integer( -1 ), graph3.get( "a", "nother", "number" ), "Incorrect value retrieved." );
        
        /* Test putting repeated */
        assertEquals( "value 1", graph.put( "other", "hi", "i" ) );
        assertEquals( "value 2", graph.put( "other", "hi" ) );
        assertEquals( "value 3", graph.put( "other", "hi", "i", "am", "here" ) );
        assertEquals( "value 4", graph.put( "other", "potato" ) );
        assertEquals( "value 5", graph.put( "other", "potato", "salad" ) );
        assertEquals( "value 6", graph.put( "other", "potato", "salad", "with dressing" ) );
        
        assertEquals( "value 1", graph2.put( "other", 1990L, 420L ) );
        assertEquals( "value 2", graph2.put( "other", 0L, -19L ) );
        
        assertEquals( new Integer( 90 ), graph3.put( 0, "a", "number" ) );
        assertEquals( new Integer( 404 ), graph3.put( 0, "another", "number" ) );
        assertEquals( new Integer( -1 ), graph3.put( 0, "a", "nother", "number" ) );
        
        /* Test getting new values */
        assertEquals( "other", graph.get( "hi", "i" ), "Incorrect value retrieved." );
        assertEquals( "other", graph.get( "hi" ), "Incorrect value retrieved." );
        assertEquals( "other", graph.get( "hi", "i", "am", "here" ), "Incorrect value retrieved." );
        assertEquals( "other", graph.get( "potato" ), "Incorrect value retrieved." );
        assertEquals( "other", graph.get( "potato", "salad" ), "Incorrect value retrieved." );
        assertEquals( "other", graph.get( "potato", "salad", "with dressing" ), "Incorrect value retrieved." );
        
        assertEquals( "other", graph2.get( 1990L, 420L ), "Incorrect value retrieved." );
        assertEquals( "other", graph2.get( 0L, -19L ), "Incorrect value retrieved." );
        
        assertEquals( new Integer( 0 ), graph3.get( "a", "number" ), "Incorrect value retrieved." );
        assertEquals( new Integer( 0 ), graph3.get( "another", "number" ), "Incorrect value retrieved." );
        assertEquals( new Integer( 0 ), graph3.get( "a", "nother", "number" ), "Incorrect value retrieved." );
        
    }
    
    @Test
    public void testGetAll() {
        
        String[] expected = { "value 2", "value 1", "value 3" };
        assertEquals( Arrays.asList( expected ),
                graph.getAll( "hi", "i", "am", "here" ), "Incorrect list returned." );
        
        String[] expected2 = { "value 2", "value 1" };
        assertEquals( Arrays.asList( expected2 ),
                graph.getAll( "hi", "i" ), "Incorrect list returned." );
        
    }
    
    @Test
    public void testRemove() {
        
        assertEquals( "value 1", graph.remove( "hi", "i" ), "Wrong value returned by remove." );
        assertNull( graph.get( "hi", "i" ), "Value was not deleted." );
        assertNull( graph.remove( "hi", "i" ), "Delete succeeded in deleted path." );
        assertNull( graph.remove( "does", "not", "exist" ), "Deleted a value from an inexistent path." );
        
    }
    
    @Test
    public void testSize() {
        
        assertEquals( 3, graph.size(), "Incorrect graph size." );
        graph.remove( "hi" );
        assertEquals( 2, graph.size(), "Incorrect graph size." );
        assertEquals( 0,
                new HashTree<String,String>().size(), "Incorrect graph size." );
        
    }
    
    @Test
    public void testIsEmpty() {
        
        assertFalse( graph.isEmpty(), "Graph should not be empty." );
        assertTrue( new HashTree<String,String>().isEmpty(), "Graph should be empty." );
        
    }
    
    @Test
    public void testClear() {
        
        graph.clear();
        assertTrue( graph.isEmpty(), "Graph should become empty." );
        
    }
    
    @Test
    public void testRoot() {

        assertNull( graph.get(), "Graph root should be empty." );
        
        graph.put( "string" );
        assertEquals( "string", graph.get(), "Incorrect root value." );
        
    }
    
    @Test
    public void testSerialize() {

        String encoded = Utils.serializableToString( graph );
        Tree<String,String> decoded = Utils.stringToSerializable( encoded );
        assertEquals( graph, decoded, "Decoded graph not equal to original." );
        
    }
    
    @Test
    public void testEquals() {
        
        /* Test an equal graph */
        Tree<String,String> equalGraph = new HashTree<>();
        equalGraph.put( "value 1", "hi", "i" );
        equalGraph.put( "value 2", "hi" );
        equalGraph.put( "value 3", "hi", "i", "am", "here" );
        
        assertTrue( graph.equals( equalGraph ), "Graphs should be equal." );
        assertTrue( equalGraph.equals( graph ), "Graphs should be equal." );
        
        /* Test a different graph */
        Tree<String,String> differentGraph1 = new HashTree<>();
        differentGraph1.put( "other", "noob" );
        
        assertFalse( graph.equals( differentGraph1 ), "Graphs should not be equal." );
        assertFalse( differentGraph1.equals( graph ), "Graphs should not be equal." );
        
        /* Test a different graph with the same keys */
        Tree<String,String> differentGraph2 = new HashTree<>();
        differentGraph2.put( "other 1", "hi", "i" );
        differentGraph2.put( "other 2", "hi" );
        differentGraph2.put( "other 3", "hi", "i", "am", "here" );
        
        assertFalse( graph.equals( differentGraph2 ), "Graphs should not be equal." );
        assertFalse( differentGraph2.equals( graph ), "Graphs should not be equal." );
        
        /* Test a different graph with the same values */
        Tree<String,String> differentGraph3 = new HashTree<>();
        differentGraph3.put( "value 1", "other" );
        differentGraph3.put( "value 2", "other", "key" );
        differentGraph3.put( "value 3", "other", "key", "here" );
        
        assertFalse( graph.equals( differentGraph3 ), "Graphs should not be equal." );
        assertFalse( differentGraph3.equals( graph ), "Graphs should not be equal." );
        
        /* Test a different graph with a single equal mapping */
        Tree<String,String> differentGraph4 = new HashTree<>();
        differentGraph4.put( "value 1", "hi", "i" );
        
        assertFalse( graph.equals( differentGraph4 ), "Graphs should not be equal." );
        assertFalse( differentGraph4.equals( graph ), "Graphs should not be equal." );
        
        /* Test a different graph with different types */
        Tree<Long,Integer> differentGraphTypes = new HashTree<>();
        differentGraphTypes.put( 90, 12L, 100L, 1L );
        
        assertFalse( graph.equals( differentGraphTypes ), "Graphs should not be equal." );
        assertFalse( differentGraphTypes.equals( graph ), "Graphs should not be equal." );
        
    }

}
