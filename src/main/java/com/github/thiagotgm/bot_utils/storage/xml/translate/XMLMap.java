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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.github.thiagotgm.bot_utils.storage.xml.XMLElement;
import com.github.thiagotgm.bot_utils.storage.xml.XMLTranslator;

/**
 * Translator that stores and loads maps to/from an XML format.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-08-08
 * @param <K> The type of keys in the map.
 * @param <V> The type of the values to be stored.
 */
public class XMLMap<K,V> extends AbstractXMLTranslator<Map<K,V>> {
	
	/**
	 * UID that represents this class.
	 */
	private static final long serialVersionUID = -903749802183805231L;

	/**
     * Local name of the XML element.
     */
    public static final String TAG = "map";
	
    private final Supplier<? extends Map<K,V>> instanceSupplier;
	private final XMLTranslator<Map.Entry<K,V>> entryTranslator;
	
	/**
     * Instantiates an map translator that uses instances obtained from the given supplier and
     * uses the given translators for keys and values.
     *
     * @param instanceSupplier The supplier to get map instances from.
     * @param keyTranslator The translator to use for the map keys.
     * @param valueTranslator The translator to use for the map values.
     * @throws IllegalArgumentException if the given class does not have an accessible no-arg constructor.
     * @throws NullPointerException if any of the arguments is <tt>null</tt>.
     */
	public XMLMap( Supplier<? extends Map<K,V>> instanceSupplier, XMLTranslator<K> keyTranslator,
			XMLTranslator<V> valueTranslator ) throws NullPointerException {
		
		if ( ( instanceSupplier == null ) || ( keyTranslator == null ) || ( valueTranslator == null ) ) {
			throw new NullPointerException( "Arguments cannot be null." );
		}
		
		this.instanceSupplier = instanceSupplier;
		this.entryTranslator = new XMLMapEntry( keyTranslator, valueTranslator );
		
	}
	
	/**
     * Instantiates an map translator that uses instances of the given map class and
     * uses the given translators for keys and values.
     *
     * @param mapClass The class of map to instantiate.
     * @param keyTranslator The translator to use for the map keys.
     * @param valueTranslator The translator to use for the map values.
     * @throws IllegalArgumentException if the given class does not have an accessible no-arg constructor.
     * @throws NullPointerException if any of the arguments is <tt>null</tt>.
     */
	public XMLMap( Class<? extends Map<K,V>> mapClass, XMLTranslator<K> keyTranslator,
			XMLTranslator<V> valueTranslator ) throws IllegalArgumentException, NullPointerException {
		
		if ( ( mapClass == null ) || ( keyTranslator == null ) || ( valueTranslator == null ) ) {
			throw new NullPointerException( "Arguments cannot be null." );
		}
		
		Constructor<? extends Map<K,V>> mapCtor;
		try { // Get collection ctor.
			mapCtor = mapClass.getConstructor();
		} catch ( NoSuchMethodException | SecurityException e ) {
			throw new IllegalArgumentException( "Map class does not have a public no-args constructor.", e );
		}
        
        try { // Check that ctor works.
			mapCtor.newInstance();
		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e ) {
			throw new IllegalArgumentException(
					"Map class cannot be initialized using no-arg constructor.", e );
		}
        
        this.instanceSupplier = () -> {
        	
        	try {
    			return mapCtor.newInstance();
    		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
    				| InvocationTargetException e ) {
    			throw new IllegalArgumentException( "Map class could be initialized using no-arg "
    					+ "constructor.", e );
    		}
        	
        };
		
		entryTranslator = new XMLMapEntry( keyTranslator, valueTranslator );
		
	}
	
	/**
     * Instantiates an map translator that uses the given translators for keys and values.
     * <p>
     * The Map implementation to be used is chosen by this class, and no guarantees are
     * made about it.
     *
     * @param keyTranslator The translator to use for the map keys.
     * @param valueTranslator The translator to use for the map values.
     * @throws IllegalArgumentException if the given class does not have an accessible no-arg constructor.
     * @throws NullPointerException if any of the arguments is <tt>null</tt>.
     */
	@SuppressWarnings("unchecked")
	public XMLMap( XMLTranslator<K> keyTranslator, XMLTranslator<V> valueTranslator )
			throws IllegalArgumentException, NullPointerException {
		
		this( (Class<Map<K,V>>) (Class<?>) HashMap.class, keyTranslator, valueTranslator );
		
	}
	
	@Override
	public String getTag() {
		
		return TAG;
		
	}
	
	@Override
	public Map<K,V> readContent( XMLStreamReader in ) throws XMLStreamException {
		
		Map<K,V> map = instanceSupplier.get(); // Make map instance.
		
		while ( in.hasNext() ) { // Read each mapping.
            
            switch ( in.next() ) {
                
                case XMLStreamConstants.START_ELEMENT:
                	if ( in.getLocalName().equals( XMLMapEntry.TAG ) ) { // Read mapping.
                		Map.Entry<K,V> entry = entryTranslator.read( in );
                		map.put( entry.getKey(), entry.getValue() ); // Insert mapping.
                	} else {
                		throw new XMLStreamException( "Unexpected start element." );
                	}
                	break;
                    
                case XMLStreamConstants.END_ELEMENT:
                	if ( in.getLocalName().equals( TAG ) ) {
                		return map; // Finished reading.
                	} else {
                		throw new XMLStreamException( "Unexpected end element." );
                	}
	                
            }
            
        }
        throw new XMLStreamException( "Unexpected end of document." );
		
	}

	@Override
	public void writeContent( XMLStreamWriter out, Map<K,V> instance ) throws XMLStreamException {

		for ( Map.Entry<K,V> entry : instance.entrySet() ) {
			
			entryTranslator.write( out, entry ); // Write each mapping.
			
		}
		
	}
	
	/* Delegate for writing mappings */
	
	/**
	 * Translator for converting mappings in the map.
	 * <p>
	 * Does not accept <tt>null</tt> instances.
	 * 
	 * @version 1.0
	 * @author ThiagoTGM
	 * @since 2018-08-08
	 */
	protected class XMLMapEntry implements XMLTranslator<Map.Entry<K,V>> {
		
		/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -522271733189736282L;
		
		/**
	     * Local name of the XML element.
	     */
	    public static final String TAG = "entry";
	    /**
	     * Local name of the key element.
	     */
	    protected static final String KEY_TAG = "key";
	    /**
	     * Local name of the value element.
	     */
	    protected static final String VALUE_TAG = "value";
		
	    /**
	     * Translator used to translate mapping keys.
	     */
	    protected final XMLTranslator<K> keyTranslator;
	    /**
	     * Translator used to translate mapping values.
	     */
	    protected final XMLTranslator<V> valueTranslator;
		
		/**
		 * Instantiates a mapping translator that uses the given translators for keys and
		 * values.
		 * 
		 * @param keyTranslator Translator to use to translate mapping keys.
		 * @param valueTranslator Translator to use to translate mapping values.
		 */
		public XMLMapEntry( XMLTranslator<K> keyTranslator, XMLTranslator<V> valueTranslator ) {
			
			this.keyTranslator = keyTranslator;
			this.valueTranslator = valueTranslator;
			
		}

		@Override
		public Entry<K,V> read( XMLStreamReader in ) throws XMLStreamException {

			if ( ( in.getEventType() != XMLStreamConstants.START_ELEMENT ) ||
		              !in.getLocalName().equals( TAG ) ) {
				throw new XMLStreamException( "Did not find element start." );
		    }
		        
	        K key = null;
	        V value = null;
	        boolean readingKey = false;
	        boolean readingValue = false;
	        while ( in.hasNext() ) { // Read each element.
	            
	            switch ( in.next() ) {
	                
	                case XMLStreamConstants.START_ELEMENT:
	                	if ( readingKey ) { // Is reading a key.
	                		key = keyTranslator.read( in );
	                	} else if ( readingValue ) { // Is reading a value.
	                		value = valueTranslator.read( in );
	                	} else { // Not currently reading anything.
		                    switch ( in.getLocalName() ) {
		                    
			                    case KEY_TAG: // Found key element.
			                    	if ( key != null ) {
			                    		throw new XMLStreamException( "Mapping has multiple keys." );
			                    	}
			                    	readingKey = true;
			                    	break;
			                    	
			                    case VALUE_TAG: // Found value element.
			                    	if ( value != null ) {
			                    		throw new XMLStreamException( "Mapping has multiple values." );
			                    	}
			                    	readingValue = true;
			                    	break;
			                    	
		                    	default: // Unrecognized element.
		                    		throw new XMLStreamException( "Unexpected XML element." );
		                 
		                    }
	                	}
	                	break;
	                    
	                case XMLStreamConstants.END_ELEMENT:
	                	switch ( in.getLocalName() ) {
	                    
		                    case KEY_TAG: // Finished reading key.
		                    	readingKey = false;
		                    	break;
		                    	
		                    case VALUE_TAG: // Finished reading value.
		                    	readingValue = false;
		                    	break;
		                    	
		                    case TAG: // Finished reading entry.
		                    	if ( readingKey || readingValue ) {
		                    		throw new XMLStreamException( "Element end while still reading key or value" );
		                    	}
		                    	if ( ( key == null ) || ( value == null ) ) {
		                    		throw new XMLStreamException( "Mapping missing key or value." );
		                    	}
		                    	return new EntryImpl( key, value );
		                    	
	                    	default: // Unrecognized tag.
	                    		throw new XMLStreamException( "Unexpected end element." );
	                 
	                    }
	                	break;
		                
	            }
	            
	        }
	        throw new XMLStreamException( "Unexpected end of document." );
			
		}

		@Override
		public void write( XMLStreamWriter out, Entry<K,V> instance ) throws XMLStreamException {

			out.writeStartElement( TAG );
			
			out.writeStartElement( KEY_TAG ); // Write key.
			keyTranslator.write( out, instance.getKey() );
			out.writeEndElement();
			
			out.writeStartElement( VALUE_TAG ); // Write value.
			valueTranslator.write( out, instance.getValue() );
			out.writeEndElement();
			
			out.writeEndElement();
			
		}
		
		/**
		 * A mapping read from the XML stream.
		 * 
		 * @version 1.0
		 * @author ThiagoTGM
		 * @since 2018-08-08
		 */
		private class EntryImpl implements Map.Entry<K,V> {
			
			private final K key;
			private final V value;
			
			/**
			 * Instantiates an entry that represents the given key mapped to the
			 * given value.
			 * 
			 * @param key The key of the mapping.
			 * @param value The value of the mapping.
			 */
			public EntryImpl( K key, V value ) {
				
				this.key = key;
				this.value = value;
				
			}

			@Override
			public K getKey() {

				return key;
				
			}

			@Override
			public V getValue() {

				return value;
				
			}

			@Override
			public V setValue( V value ) throws UnsupportedOperationException {

				throw new UnsupportedOperationException();
				
			}
			
		}
		
	}

	/* Wrapper for maps */
	
	/**
	 * Wrapper that allows the use of any Map class as an XMLElement, using a backing
	 * instance of the desired class and an {@link XMLMap} translator.
	 * 
	 * @version 1.0
	 * @author ThiagoTGM
	 * @since 2018-08-08
	 * @param <K> The type of keys in the map.
	 * @param <V> The type of the values in the map.
	 */
	public static class WrappedMap<K,V> implements XMLElement, Map<K,V> {
		
		/**
		 * UID that represents this class.
		 */
		private static final long serialVersionUID = -6400842899888638846L;
		
		private final Map<K,V> backing;
		private final XMLMap<K,V> translator;
		
		/**
		 * Instantiates a wrapper using an instance of the given class as backing map.
		 * <p>
		 * The given class must have a no-args constructor available for instancing.
		 * 
		 * @param mapClass The class to use for the backing map.
		 * @param keyTranslator The translator to use for keys.
		 * @param valueTranslator The translator to use for values.
		 * @throws IllegalArgumentException if the given class does not have an accessible no-arg constructor.
		 * @throws NullPointerException if any of the arguments is <tt>null</tt>.
		 */
		public WrappedMap( Class<? extends Map<K,V>> mapClass, XMLTranslator<K> keyTranslator,
				XMLTranslator<V> valueTranslator ) throws IllegalArgumentException, NullPointerException {
			
			this.translator = new XMLMap<>( mapClass, keyTranslator, valueTranslator );
			
			this.backing = translator.instanceSupplier.get();
			
		}
		
		/**
		 * Instantiates a wrapper using the given map as backing map.
		 * 
		 * @param backing The backing map to use.
		 * @param keyTranslator The translator to use for keys.
		 * @param valueTranslator The translator to use for values.
		 * @throws IllegalArgumentException if the given class does not have an accessible no-arg constructor.
		 * @throws NullPointerException if any of the arguments is <tt>null</tt>.
		 */
		@SuppressWarnings("unchecked")
		public WrappedMap( Map<K,V> backing, XMLTranslator<K> keyTranslator,
				XMLTranslator<V> valueTranslator ) throws IllegalArgumentException, NullPointerException {
			
			this.translator = new XMLMap<>( (Class<? extends Map<K,V>>) backing.getClass(),
					keyTranslator, valueTranslator );
			
			this.backing = backing;
			
		}

		@Override
		public int size() {

			return backing.size();
			
		}

		@Override
		public boolean isEmpty() {

			return backing.isEmpty();
			
		}

		@Override
		public boolean containsKey( Object key ) {

			return backing.containsKey( key );
			
		}

		@Override
		public boolean containsValue( Object value ) {

			return backing.containsValue( value );
			
		}

		@Override
		public V get( Object key ) {

			return backing.get( key );
			
		}

		@Override
		public V put( K key, V value ) {

			return backing.put( key, value );
			
		}

		@Override
		public V remove(Object key) {
			
			return backing.remove( key );
			
		}

		@Override
		public void putAll( Map<? extends K, ? extends V> m ) {

			backing.putAll( m );
			
		}

		@Override
		public void clear() {

			backing.clear();
			
		}

		@Override
		public Set<K> keySet() {

			return backing.keySet();
			
		}

		@Override
		public Collection<V> values() {

			return backing.values();
			
		}

		@Override
		public Set<Entry<K,V>> entrySet() {
			
			return backing.entrySet();
			
		}

		@Override
        public boolean equals( Object o ) {

            return backing.equals( o );
            
        }

        @Override
        public int hashCode() {

            return backing.hashCode();
            
        }

        @Override
        public V getOrDefault( Object key, V defaultValue ) {

            return backing.getOrDefault( key, defaultValue );
            
        }

        @Override
        public void forEach( BiConsumer<? super K, ? super V> action ) {

            backing.forEach( action );
            
        }

        @Override
        public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function ) {

            backing.replaceAll( function );
            
        }

        @Override
        public V putIfAbsent( K key, V value ) {

            return backing.putIfAbsent( key, value );
            
        }

        @Override
        public boolean remove( Object key, Object value ) {

            return backing.remove( key, value );
            
        }

        @Override
        public boolean replace( K key, V oldValue, V newValue ) {

            return backing.replace( key, oldValue, newValue );
            
        }

        @Override
        public V replace( K key, V value ) {

            return backing.replace( key, value );
            
        }

        @Override
        public V computeIfAbsent( K key, Function<? super K, ? extends V> mappingFunction ) {

            return backing.computeIfAbsent( key, mappingFunction );
            
        }

        @Override
        public V computeIfPresent( K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction ) {

            return backing.computeIfPresent( key, remappingFunction );
            
        }

        @Override
        public V compute( K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction ) {

            return backing.compute( key, remappingFunction );
            
        }

        @Override
        public V merge( K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction ) {

            return backing.merge( key, value, remappingFunction );
            
        }

        @Override
		public void read( XMLStreamReader in ) throws XMLStreamException {

			backing.clear();
			backing.putAll( translator.read( in ) );
			
		}

		@Override
		public void write( XMLStreamWriter out ) throws XMLStreamException {

			translator.write( out, backing );
			
		}
		
	}
	
}
