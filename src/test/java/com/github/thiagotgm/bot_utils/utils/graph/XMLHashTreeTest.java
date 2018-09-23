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

import java.util.List;

import org.junit.jupiter.api.DisplayName;

import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLInteger;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLList;
import com.github.thiagotgm.bot_utils.storage.xml.translate.XMLString;


/**
 * Unit tests for {@link XMLHashTree}.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-22
 */
@DisplayName( "XMLHashTree tests" )
public class XMLHashTreeTest extends TreeTest {

    @Override
    protected Tree<String, List<Integer>> getTree() {

        return new XMLHashTree<>( new XMLString(), new XMLList<>( new XMLInteger() ) );
        
    }

    @Override
    protected boolean acceptsNullKeys() {

        return true;
        
    }

    @Override
    protected boolean acceptsNullValues() {

        return true;
        
    }

}
