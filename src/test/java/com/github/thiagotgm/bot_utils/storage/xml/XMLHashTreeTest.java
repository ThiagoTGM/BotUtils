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

package com.github.thiagotgm.bot_utils.storage.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;

import com.github.thiagotgm.bot_utils.storage.xml.XMLElement;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLInteger;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLString;
import com.github.thiagotgm.bot_utils.utils.Utils;
import com.github.thiagotgm.bot_utils.utils.graph.XMLHashTree;

/**
 * Unit tests for {@link XMLHashTree}.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-09-11
 */
public class XMLHashTreeTest {
    
    private static final XMLHashTree<String,Integer> EXPECTED;
    
    static {
        
        EXPECTED = new XMLHashTree<>( new XMLString(), new XMLInteger() );
        EXPECTED.put( 0 );
        EXPECTED.put( 34, "hi" );
        EXPECTED.put( 420, "hi", "I" );
        EXPECTED.put( 90, "hi", "I", "am" );
        EXPECTED.put( -29, "hi", "I", "am", "here" );
        
    }
    
    private static final XMLElement.Translator<XMLHashTree<String,Integer>> TRANSLATOR = () -> {
    	
    	return new XMLHashTree<>( new XMLString(), new XMLInteger() );
    	
    };

    @Test
    public void testRead() throws XMLStreamException, FactoryConfigurationError {

        InputStream in = this.getClass().getResourceAsStream( "/storage/xml/TreeGraph.xml" );
        XMLHashTree<String,Integer> actual = Utils.readXMLDocument( in, TRANSLATOR );
        
        assertEquals( EXPECTED, actual, "Read graph is not correct." );
        
    }
    
    @Test
    public void testWrite() throws XMLStreamException, IOException {
     
        XMLTestHelper.testReadWrite( EXPECTED, TRANSLATOR );
        
    }

}
