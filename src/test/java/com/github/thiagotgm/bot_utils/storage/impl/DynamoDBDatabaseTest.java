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

import static org.junit.jupiter.api.Assumptions.*;

import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import com.github.thiagotgm.bot_utils.storage.Database;
import com.github.thiagotgm.bot_utils.storage.impl.DynamoDBDatabase;

/**
 * Unit tests for {@link DynamoDBDatabase}.
 *
 * @version 2.0
 * @author ThiagoTGM
 * @since 2018-08-30
 */
@DisplayName( "DynamoDB database test" )
public class DynamoDBDatabaseTest extends DatabaseTest {

    private static Database db;

    /**
     * Loads the database before testing starts.
     */
    @BeforeAll
    public static void load() {

        Database db = new DynamoDBDatabase();
        assumeTrue( db.load( Arrays.asList( "yes", "8000", "", "" ) ) );
        DynamoDBDatabaseTest.db = db;

    }

    /**
     * Closes the database after all tests are done.
     */
    @AfterAll
    public static void close() {

        if ( db != null ) {
            db.close();
            db = null;
        }

    }

    @Override
    protected Database getDatabase() {

        return db;
        
    }

    @Override
    protected boolean closeAfterTest() {

        return false;
        
    }

}
