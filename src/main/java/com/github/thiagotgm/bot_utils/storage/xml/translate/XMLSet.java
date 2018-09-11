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

package com.github.thiagotgm.bot_utils.storage.xml.translate;

import java.util.HashSet;
import java.util.Set;

import com.github.thiagotgm.bot_utils.storage.xml.XMLElement;
import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Translator for generalized Set objects.
 *
 * @version 2.0
 * @author ThiagoTGM
 * @since 2017-09-10
 * @param <E> The type of elements being stored in the set.
 */
public class XMLSet<E extends XMLElement> extends AbstractXMLCollection<E,Set<E>> {

    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -7391823040267850261L;

    /**
     * Local name of the XML element.
     */
    public static final String TAG = "set";
    
    /**
     * Instantiates an set translator that uses instances of the given Set class and
     * uses the given translator for the elements.
     *
     * @param setClass The class of set to instantiate.
     * @param translator The translator to use for the set elements.
     */
    public XMLSet( Class<? extends Set<E>> setClass, XMLTranslator<E> translator )
    		throws IllegalArgumentException {
    	
    	super( setClass, translator );
    	
    }
    
    /**
     * Instantiates an set translator that uses the given translator for the elements.
     * <p>
     * The Set implementation to be used is chosen by this class, and no guarantees are
     * made about it.
     *
     * @param translator The translator to use for the set elements.
     */
    @SuppressWarnings("unchecked")
	public XMLSet( XMLTranslator<E> translator ) throws IllegalArgumentException {
    	
    	this( (Class<Set<E>>) (Class<?>) HashSet.class, translator );
    	
    }
	
	@Override
	public String getTag() {
        
        return TAG;
        
    }

}
