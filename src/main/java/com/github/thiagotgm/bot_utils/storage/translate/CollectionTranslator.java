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

package com.github.thiagotgm.bot_utils.storage.translate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.github.thiagotgm.bot_utils.storage.Data;
import com.github.thiagotgm.bot_utils.storage.Translator;

/**
 * Base implementation for collections of objects, that converts the Collection
 * into (and from) a list-typed data.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-30
 * @param <E> The type of objects in the collections to be translated.
 * @param <T> The specific collection type.
 */
public abstract class CollectionTranslator<E,T extends Collection<E>> implements Translator<T> {
	
	private final Supplier<? extends T> instanceSupplier;
	private final Translator<E> elementTranslator;
	
	/**
	 * Initializes a collection translator that uses the given translator for
	 * each of the elements in the collection, and uses the given supplier
	 * to create collection instances.
	 * 
	 * @param instanceSupplier Supplier to get instances from when decoding.
	 * @param elementTranslator The translator to be used for the elements
	 *                          in the collection.
	 * @throws NullPointerException if either argument is <tt>null</tt>.
	 */
	public CollectionTranslator( Supplier<? extends T> instanceSupplier,
			Translator<E> elementTranslator ) throws NullPointerException {
		
		if ( instanceSupplier == null ) {
			throw new NullPointerException( "Supplier cannot be null." );
		}
		
		if ( elementTranslator == null ) {
			throw new NullPointerException( "Translator cannot be null." );
		}
		
		this.instanceSupplier = instanceSupplier;
		this.elementTranslator = elementTranslator;
		
	}
	
	/**
	 * Initializes a collection translator that uses the given translator for
	 * each of the elements in the collection, and instances of the given class
	 * for decoded collections. The given class must have a public no-arg
	 * constructor.
	 * 
	 * @param clazz Class to make instances of when decoding.
	 * @param elementTranslator The translator to be used for the elements
	 *                          in the collection.
	 * @throws NullPointerException if either argument is <tt>null</tt>.
	 * @throws IllegalArgumentException if the given class does not have
	 *                                  a public no-arg constructor.
	 */
	public CollectionTranslator( Class<? extends T> clazz, Translator<E> elementTranslator )
			throws NullPointerException, IllegalArgumentException {
		
		if ( clazz == null ) {
			throw new NullPointerException( "Class cannot be null." );
		}
		
		if ( elementTranslator == null ) {
			throw new NullPointerException( "Translator cannot be null." );
		}
		
		Constructor<? extends T> ctor;
		try {
			ctor = clazz.getConstructor();
		} catch ( NoSuchMethodException | SecurityException e ) {
			throw new IllegalArgumentException( "No avaliable no-arg ctor." );
		}
		
		if ( !Modifier.isPublic( ctor.getModifiers() ) ) {
			throw new IllegalArgumentException( "No-arg ctor is not public." );
		}
		
		instanceSupplier = () -> {
			
			try {
				return ctor.newInstance();
			} catch ( Exception e ) {
				throw new TranslationException( "Could not create instance.", e );
			}
			
		};
		this.elementTranslator = elementTranslator;
		
	}

	/**
	 * Encodes the collection into a list-typed Data instance.
	 * <p>
	 * The elements are inserted into the data list in the order
	 * they are provided by the given collection's iterator.
	 */
	@Override
	public Data toData( T obj ) throws TranslationException {

		List<Data> list = new ArrayList<>( obj.size() );
		for ( E elem : obj ) { // Translate each element.
			
			list.add( elementTranslator.toData( elem ) );
			
		}
		return Data.listData( list );
		
	}

	/**
	 * Decodes the collection from a list-typed Data instance.
	 * <p>
	 * The elements are inserted into an instance obtained from the instance supplier
	 * specified at construction, in the order that they appear in the data list, using the
	 * {@link Collection#add(Object) add(E)} method of the collection. If the specific
	 * collection type being used has some restriction that prevents one or more elements
	 * from being inserted (i.e. the add(E) call returns <tt>false</tt>), those values
	 * are ignored.<br>
	 * For example, if the specific collection being used is a Set, but the given data
	 * list has elements that are duplicates, the returned Set will contain the first
	 * of those that appeared in the data list.
	 */
	@Override
	public T fromData( Data data ) throws TranslationException {

		if ( !data.isList() ) {
			throw new TranslationException( "Given data is not a list." );
		}
		
		T obj = instanceSupplier.get();
		for ( Data elem : data.getList() ) { // Translate each element.
			
			obj.add( elementTranslator.fromData( elem ) );
			
		}
		return obj;
		
	}

}
