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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Translator for generalized List objects.
 *
 * @version 2.0
 * @author ThiagoTGM
 * @since 2017-09-10
 * @param <E> The type of elements being stored in the list.
 */
public class XMLList<E> extends AbstractXMLCollection<E,List<E>> {
    
    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -4591024804259782530L;
    
    /**
     * Local name of the XML element.
     */
    public static final String TAG = "list";
    
    /**
     * Instantiates a list translator that uses instances given by the given supplier and
     * uses the given translator for the elements.
     *
     * @param instanceSupplier The supplier to use to get instances.
     * @param translator The translator to use for the list elements.
     * @throws NullPointerException if either argument is <tt>null</tt>.
     */
    public XMLList( Supplier<? extends List<E>> instanceSupplier, XMLTranslator<E> translator )
    		throws NullPointerException {
    	
    	super( instanceSupplier, translator );
        
    }
    
    /**
     * Instantiates an list translator that uses instances of the given List class and
     * uses the given translator for the elements.
     *
     * @param listClass The class of list to instantiate.
     * @param translator The translator to use for the list elements.
     * @throws IllegalArgumentException if the given class does not have a functioning no-args
     *                                  constructor.
     */
    public XMLList( Class<? extends List<E>> listClass, XMLTranslator<E> translator )
    		throws IllegalArgumentException {
    	
    	super( listClass, translator );
    	
    }
    
    /**
     * Instantiates an list translator that uses the given translator for the elements.
     * <p>
     * The List implementation to be used is chosen by this class, and no guarantees are
     * made about it.
     *
     * @param translator The translator to use for the list elements.
     */
    @SuppressWarnings("unchecked")
	public XMLList( XMLTranslator<E> translator ) {
    	
    	this( (Class<List<E>>) (Class<?>) LinkedList.class, translator );
    	
    }
	
	@Override
	public String getTag() {
        
        return TAG;
        
    }

}
