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

import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

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

    @Before
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
        assertEquals( "Incorrect value retrieved.", "value 1", graph.get( "hi", "i" ) );
        assertEquals( "Incorrect value retrieved.", "value 2", graph.get( "hi" ) );
        assertEquals( "Incorrect value retrieved.", "value 3", graph.get( "hi", "i", "am", "here" ) );
        assertEquals( "Incorrect value retrieved.", null, graph.get( "potato" ) );
        assertEquals( "Incorrect value retrieved.", null, graph.get( "potato", "salad" ) );
        assertEquals( "Incorrect value retrieved.", null, graph.get( "potato", "salad", "with dressing" ) );
        
        assertEquals( "Incorrect value retrieved.", null, graph2.get( 1990L, 420L ) );
        assertEquals( "Incorrect value retrieved.", null, graph2.get( 0L, -19L ) );
        
        assertEquals( "Incorrect value retrieved.", null, graph3.get( "a", "number" ) );
        assertEquals( "Incorrect value retrieved.", null, graph3.get( "another", "number" ) );
        assertEquals( "Incorrect value retrieved.", null, graph3.get( "a", "nother", "number" ) );

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
        assertEquals( "Incorrect value retrieved.", "value 1", graph.get( "hi", "i" ) );
        assertEquals( "Incorrect value retrieved.", "value 2", graph.get( "hi" ) );
        assertEquals( "Incorrect value retrieved.", "value 3", graph.get( "hi", "i", "am", "here" ) );
        assertEquals( "Incorrect value retrieved.", "value 4", graph.get( "potato" ) );
        assertEquals( "Incorrect value retrieved.", "value 5", graph.get( "potato", "salad" ) );
        assertEquals( "Incorrect value retrieved.", "value 6", graph.get( "potato", "salad", "with dressing" ) );
        
        assertEquals( "Incorrect value retrieved.", "value 1", graph2.get( 1990L, 420L ) );
        assertEquals( "Incorrect value retrieved.", "value 2", graph2.get( 0L, -19L ) );
        
        assertEquals( "Incorrect value retrieved.", new Integer( 90 ), graph3.get( "a", "number" ) );
        assertEquals( "Incorrect value retrieved.", new Integer( 404 ), graph3.get( "another", "number" ) );
        assertEquals( "Incorrect value retrieved.", new Integer( -1 ), graph3.get( "a", "nother", "number" ) );
        
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
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "hi", "i" ) );
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "hi" ) );
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "hi", "i", "am", "here" ) );
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "potato" ) );
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "potato", "salad" ) );
        assertEquals( "Incorrect value retrieved.", "other", graph.get( "potato", "salad", "with dressing" ) );
        
        assertEquals( "Incorrect value retrieved.", "other", graph2.get( 1990L, 420L ) );
        assertEquals( "Incorrect value retrieved.", "other", graph2.get( 0L, -19L ) );
        
        assertEquals( "Incorrect value retrieved.", new Integer( 0 ), graph3.get( "a", "number" ) );
        assertEquals( "Incorrect value retrieved.", new Integer( 0 ), graph3.get( "another", "number" ) );
        assertEquals( "Incorrect value retrieved.", new Integer( 0 ), graph3.get( "a", "nother", "number" ) );
        
    }
    
    @Test
    public void testGetAll() {
        
        String[] expected = { "value 2", "value 1", "value 3" };
        assertEquals( "Incorrect list returned.", Arrays.asList( expected ),
                graph.getAll( "hi", "i", "am", "here" ) );
        
        String[] expected2 = { "value 2", "value 1" };
        assertEquals( "Incorrect list returned.", Arrays.asList( expected2 ),
                graph.getAll( "hi", "i" ) );
        
    }
    
    @Test
    public void testRemove() {
        
        assertEquals( "Wrong value returned by remove.", "value 1", graph.remove( "hi", "i" ) );
        assertNull( "Value was not deleted.", graph.get( "hi", "i" ) );
        assertNull( "Delete succeeded in deleted path.", graph.remove( "hi", "i" ) );
        assertNull( "Deleted a value from an inexistent path.", graph.remove( "does", "not", "exist" ) );
        
    }
    
    @Test
    public void testSize() {
        
        assertEquals( "Incorrect graph size.", 3, graph.size() );
        graph.remove( "hi" );
        assertEquals( "Incorrect graph size.", 2, graph.size() );
        assertEquals( "Incorrect graph size.", 0,
                new HashTree<String,String>().size() );
        
    }
    
    @Test
    public void testIsEmpty() {
        
        assertFalse( "Graph should not be empty.", graph.isEmpty() );
        assertTrue( "Graph should be empty.",
                new HashTree<String,String>().isEmpty() );
        
    }
    
    @Test
    public void testClear() {
        
        graph.clear();
        assertTrue( "Graph should become empty.", graph.isEmpty() );
        
    }
    
    @Test
    public void testRoot() {

        assertNull( "Graph root should be empty.", graph.get() );
        
        graph.put( "string" );
        assertEquals( "Incorrect root value.", "string", graph.get() );
        
    }
    
    @Test
    public void testSerialize() {

        String encoded = Utils.serializableToString( graph );
        Tree<String,String> decoded = Utils.stringToSerializable( encoded );
        assertEquals( "Decoded graph not equal to original.", graph, decoded );
        
    }
    
    @Test
    public void testEquals() {
        
        /* Test an equal graph */
        Tree<String,String> equalGraph = new HashTree<>();
        equalGraph.put( "value 1", "hi", "i" );
        equalGraph.put( "value 2", "hi" );
        equalGraph.put( "value 3", "hi", "i", "am", "here" );
        
        assertTrue( "Graphs should be equal.", graph.equals( equalGraph ) );
        assertTrue( "Graphs should be equal.", equalGraph.equals( graph ) );
        
        /* Test a different graph */
        Tree<String,String> differentGraph1 = new HashTree<>();
        differentGraph1.put( "other", "noob" );
        
        assertFalse( "Graphs should not be equal.", graph.equals( differentGraph1 ) );
        assertFalse( "Graphs should not be equal.", differentGraph1.equals( graph ) );
        
        /* Test a different graph with the same keys */
        Tree<String,String> differentGraph2 = new HashTree<>();
        differentGraph2.put( "other 1", "hi", "i" );
        differentGraph2.put( "other 2", "hi" );
        differentGraph2.put( "other 3", "hi", "i", "am", "here" );
        
        assertFalse( "Graphs should not be equal.", graph.equals( differentGraph2 ) );
        assertFalse( "Graphs should not be equal.", differentGraph2.equals( graph ) );
        
        /* Test a different graph with the same values */
        Tree<String,String> differentGraph3 = new HashTree<>();
        differentGraph3.put( "value 1", "other" );
        differentGraph3.put( "value 2", "other", "key" );
        differentGraph3.put( "value 3", "other", "key", "here" );
        
        assertFalse( "Graphs should not be equal.", graph.equals( differentGraph3 ) );
        assertFalse( "Graphs should not be equal.", differentGraph3.equals( graph ) );
        
        /* Test a different graph with a single equal mapping */
        Tree<String,String> differentGraph4 = new HashTree<>();
        differentGraph4.put( "value 1", "hi", "i" );
        
        assertFalse( "Graphs should not be equal.", graph.equals( differentGraph4 ) );
        assertFalse( "Graphs should not be equal.", differentGraph4.equals( graph ) );
        
        /* Test a different graph with different types */
        Tree<Long,Integer> differentGraphTypes = new HashTree<>();
        differentGraphTypes.put( 90, 12L, 100L, 1L );
        
        assertFalse( "Graphs should not be equal.", graph.equals( differentGraphTypes ) );
        assertFalse( "Graphs should not be equal.", differentGraphTypes.equals( graph ) );
        
    }

}
