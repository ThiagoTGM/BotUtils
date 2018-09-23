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

package com.github.thiagotgm.bot_utils.storage.impl;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.github.thiagotgm.bot_utils.storage.Database;
import com.github.thiagotgm.bot_utils.storage.translate.IntegerTranslator;
import com.github.thiagotgm.bot_utils.storage.translate.ListTranslator;
import com.github.thiagotgm.bot_utils.storage.translate.StringTranslator;
import com.github.thiagotgm.bot_utils.utils.graph.Tree;
import com.github.thiagotgm.bot_utils.utils.graph.TreeTest;

/**
 * Unit tests for an implementation of the {@link Database} interface.
 * 
 * @version 1.0
 * @author ThiagoTGM
 * @since 2018-09-21
 */
@Execution( ExecutionMode.SAME_THREAD )
@DisplayName( "Database test" )
public abstract class DatabaseTest {

    /**
     * The database being used in a test.
     */
    protected Database db;

    /**
     * Retrieves the (already loaded) database to be used for testing.
     * <p>
     * Implementations are free to load the database once and always return that
     * instance, as it will be cleared after each test.
     *
     * @return The database.
     */
    protected abstract Database getDatabase();

    /**
     * Prepares the environment before a test.
     */
    @BeforeEach
    public void setUp() {

        db = getDatabase();

    }

    /**
     * Determines whether the database received from {@link #getDatabase()} should
     * be closed at the end of a test. If <tt>false</tt>, it will be cleared after
     * each test, but will not be closed. This would usually be necessary if a
     * single static instance of the database is to be used for all tests.
     *
     * @return Whether the database should be closed after each test.
     */
    protected abstract boolean closeAfterTest();

    /**
     * Resets the environment after a test.
     */
    @AfterEach
    public void cleanUp() {

        db.clear(); // Delete all used data.
        if ( closeAfterTest() ) {
            db.close();
        }
        db = null;

    }

    /**
     * Tests for a data map in the database.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-21
     */
    @Nested
    @DisplayName( "Data map test" )
    public class DataMapTest extends MapTest {

        int mapCount = 0;

        @Override
        protected Map<String, List<Integer>> getMap() {

            return db.getDataMap( "map-" + mapCount++, new StringTranslator(),
                    new ListTranslator<>( new IntegerTranslator() ) );

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
    
    /**
     * Tests for a data tree in the database.
     * 
     * @version 1.0
     * @author ThiagoTGM
     * @since 2018-09-22
     */
    @Nested
    @DisplayName( "Data tree test" )
    public class DataTreeTest extends TreeTest {

        int treeCount = 0;

        @Override
        protected Tree<String, List<Integer>> getTree() {

            return db.getDataTree( "tree-" + treeCount++, new StringTranslator(),
                    new ListTranslator<>( new IntegerTranslator() ) );

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

}
