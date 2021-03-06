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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.function.Supplier;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Shared implementation for translators of Collection classes.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-09-10
 * @param <E> The type of elements being stored in the collection.
 * @param <T> The specific collection type.
 */
public abstract class AbstractXMLCollection<E,T extends Collection<E>> extends AbstractXMLTranslator<T> {

    /**
     * UID that represents this class.
     */
    private static final long serialVersionUID = -5857045540745890723L;
    
    private final Supplier<? extends T> instanceSupplier;
    private final XMLTranslator<E> translator;
    
    /**
     * Instantiates an collection translator that uses instances given by the given supplier and
     * uses the given translator for the elements.
     *
     * @param instanceSupplier The supplier to use to get instances.
     * @param translator The translator to use for the collection elements.
     * @throws NullPointerException if either argument is <tt>null</tt>.
     */
    public AbstractXMLCollection( Supplier<? extends T> instanceSupplier, XMLTranslator<E> translator )
    		throws NullPointerException {
    	
    	if ( instanceSupplier == null ) {
    		throw new NullPointerException( "Instance supplier cannot be null." );
    	}
    	if ( translator == null ) {
    		throw new NullPointerException( "Translator cannot be null." );
    	}
        
    	this.instanceSupplier = instanceSupplier;
        this.translator = translator;
        
    }

    /**
     * Instantiates an collection translator that uses instances of the given colletion class and
     * uses the given translator for the elements.
     *
     * @param collectionClass The class of collection to instantiate.
     * @param translator The translator to use for the collection elements.
     * @throws IllegalArgumentException if the given class does not have a functioning no-args
     *                                  constructor.
     * @throws NullPointerException if either argument is <tt>null</tt>.
     */
    public AbstractXMLCollection( Class<? extends T> collectionClass, XMLTranslator<E> translator )
    		throws IllegalArgumentException, NullPointerException {
    	
    	if ( collectionClass == null ) {
    		throw new NullPointerException( "Collection class cannot be null." );
    	}
    	if ( translator == null ) {
    		throw new NullPointerException( "Translator cannot be null." );
    	}
        
    	Constructor<? extends T> collectionCtor;
    	
        try { // Get collection ctor.
			collectionCtor = collectionClass.getConstructor();
		} catch ( NoSuchMethodException | SecurityException e ) {
			throw new IllegalArgumentException(
					"Collection class does not have a public no-args constructor.", e );
		}
        
        try { // Check that ctor works.
			collectionCtor.newInstance();
		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e ) {
			throw new IllegalArgumentException(
					"Collection class cannot be initialized using no-arg constructor.", e );
		}
        
        this.instanceSupplier = () -> {
        	
        	try { // Use the ctor.
    			return collectionCtor.newInstance();
    		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
    				| InvocationTargetException e ) {
    			throw new IllegalArgumentException(
    					"Collection class cannot be initialized using no-arg constructor.", e );
    		}
        	
        };
        this.translator = translator;
        
    }
    
    @Override
    public T readContent( XMLStreamReader in ) throws XMLStreamException {

        T collection = instanceSupplier.get();
        while ( in.hasNext() ) { // Read each element.
            
            switch ( in.next() ) {
                
                case XMLStreamConstants.START_ELEMENT:
                    collection.add( translator.read( in ) );
                    break;
                    
                case XMLStreamConstants.END_ELEMENT:
                    if ( in.getLocalName().equals( getTag() ) ) {
                        return collection; // Done reading.
                    } else {
                        throw new XMLStreamException( "Unexpected end element." );
                    }
                
            }
            
        }
        throw new XMLStreamException( "Unexpected end of document." );

    }

    @Override
    public void writeContent( XMLStreamWriter out, T instance ) throws XMLStreamException {

    	for ( E elem : instance ) { // Write each element.
            
            translator.write( out, elem );
            
        }

    }

}
