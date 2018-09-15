# BotUtils [![](https://jitpack.io/v/ThiagoTGM/BotUtils.svg)](https://jitpack.io/#ThiagoTGM/BotUtils)
Assorted tools and utilities for Discord bots that use the Discord4J library.

This library provides a bunch of tools and frameworks that help avoid the need to program large and complex backend systems, do the same code over and over again, or just overall make the code look nicer.

The javadocs are available at https://jitpack.io/com/github/ThiagoTGM/BotUtils/@VERSION@/javadoc/, where `@VERSION@` should be replaced by the desired version. This README can't possibly explain everything in full detail, so definitely check them for more detailed information. [latest](https://jitpack.io/com/github/ThiagoTGM/BotUtils/1.0.1/javadoc/)

## How to Use
There are 2 ways to use this library in your bot:

1. Download the .jar and add it as a dependency through the IDE you are using;
2. Add the dependency to the project manager.
   If using Maven, add the following to your `pom.xml`:
   ```xml
   ...
   <dependencies>
       ...
        <dependency>
            <groupId>com.github.ThiagoTGM</groupId>
            <artifactId>BotUtils</artifactId>
            <version>@VERSION@</version>
        </dependency>
    </dependencies>
    ...
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
        ...
    </repositories>
    ...
    ```

    Or, if using Gradle, add the following to your `build.gradle`:
    ```groovy
    ...
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    ...
    dependencies {
        ...
        compile 'com.github.ThiagoTGM:BotUtils:@VERSION@'
    }
    ...
    ```
    Where `@VERSION@` should be replaced with the desired version.

## Provided Functionality
The functionality provided by this library includes:
### User-changeable Settings
The class `Settings` keeps track of settings (key-value pairs) used by the bot, reading them at startup and later saving changes in a file called `settings.xml`, that may be edited directly by the user (before starting the program) or programatically by the bot. If the file doesn't exist (usually due to it being the first time the program is ran), the file is created automatically.

To avoid issues with necessary settings being missing without needing extra logic, the bot may provide default values through a resource file (e.g. a file inside the jar) named `defaultSettings.xml`, which is searched when a setting isn't found in the user-controlled settings. This library provides its own defaults in the file `defaultLibSettings.xml` (found [here](https://github.com/ThiagoTGM/BotUtils/blob/master/src/main/resources/defaultLibSettings.xml)), which can be reffered to as an example of the syntax of the settings file. 

Setting values specified in `defaultLibSettings.xml` are overriden by values specified in `defaultSettings.xml`, which are in turn overriden by values specified in `settings.xml`. Thus, the bot-default settings may provide different default values for the library settings if desired.

### Auto-saving
Listeners may be declared to be `Saveable`, and then registered into the `SaveManager`. Then, at regular intervals (defined by a setting), all registered listeners will receive a call to their `#save()` method. This is useful for classes that need to flush data to a file regularly to avoid data loss in case of an unexpected issue (crashes, power loss, etc). The listeners are also invoked right before the program closes, if the `ExitManager` is used (see below).

### Automated Logout Process
The `LogoutManager` class provides a simple way to perform tasks at certain steps in the logout process. In particular, listeners may be registered to the manager itself, and they will be called *before* the client is logged out (the client is not logged out before all listeners have finished), so you have the chance to perform any necessary cleanup while the connection is still up. After the logout, a custom event will be dispatched through the client's dispatcher, in case you need to know precisely when a logout happened (as opposed to a generic disconnect).

For this system to work properly, logout must be always performed through `LogoutManager#logout()` instead of through the client directly.

### Automated Program-closing Process
Similarly to the `LogoutManager`, the `ExitManager` provides a way to easily perform cleanup tasks right before the program closes. When the exit is requested, all registered listeners are activated (and waited for) before the exit actually happens. The listeners registered to the `SaveManager` are also activated at this time.

For this system to work properly, program exit must be always performed through `ExitManager#exit()` instead of being done manually.

**OBS:** The actual termination is done using `System#exit()`, so any threads still running after all the listeners are done *will* be interrupted.

### Persistent Data Storage
The `Database` class provides an abstraction for arbitrary forms of storing data. The API allows obtaining `Map`s or `Tree`s that are backed by the database in use, which can then be simply used as any regular `Map` or `Tree` without worrying about the pesky details of how each specific backend does things.

Before using it, however, it is necessary to load/connect the database). The `load( List<String> )` is used for that effect, taking the arguments to make the connection. Since each `Database` implementation will usually need different parameters depending on the backend it uses. The documentation of each `Database` implementation may be consulted for details on what parameters it needs. It should also be stopped using `Database#close()` before closing the program.

It is possible to simply create an instance of a specific database type and use it, but this library also provides a `DatabaseManager` class that takes care of that too. Upon calling `DatabaseManager#startup()`, a database is automatically loaded as specified in the settings. The running database may then be retrieved at any time using `DatabaseManager#getDatabase()`. At the end of the program, `DatabaseManager#shutdown()` should be called to stop the database, although this is handled automatically if the `ExitManager` is used. Another feature is that the database type can be easily switched by using `DatabaseManager#requestDatabaseChange( DatabaseType, List<String> )`. This method will take the type to switch to and the arguments to use and, if the arguments are valid (it tries to load with those arguments to check), then it will record the request and, when `shutdown()` is called (it cannot be done in the spot due to there being no guaranteed way to update the maps and trees already in use), it will automatically transfer all the data into the new database and update the settings. The next time it starts, the new database will be used and all the data will be ready. Easy! 

In order to allow systems where the user chooses any database, without needing to manually code a menu for each database, the `Database#getLoadParams()` method (or `DatabaseType#getLoadParams()`, when using the manager) can be used to provide the name of each parameter and, in some cases, what the acceptable values are (multiple-choice parameters). Do note that a parameter may have different names based on the value of the last multiple-choice argument (for example the `DynamoDBDatabase` has the first parameter "Local", with possible arguments "yes" or "no". Then, the second parameter is called either "Port" or "Access key", depending on what the argument to the first parameter is). An example of how to use this and the manager to make a database-picker GUI can be seen [here](https://github.com/ThiagoTGM/BlakeBot/blob/master/blakebot-core/src/main/java/com/github/thiagotgm/blakebot/console/DatabaseChangeDialog.java).

Currently supported backends are:
- Locally-stored XML files
- DynamoDB (either a local instance or the AWS service)

**OBS:** The `com.github.thiagotgm.bot_utils.storage.xml` package, which is used for the XML backend, can be used separately if you want to manually save some stuff in XML format.

### Assorted Utilites
The `com.github.thiagotgm.bot_utils.utils` package provides an assortment of simpler utilities. They include:
- A Graph interface where nodes store data and edges are keys, so a sequence of keys maps to a value (similar to a Map). Also its Tree specialization (used in Databases!). Provided implementations are a proper tree structure, and a tree that actually uses a map as backing;
- An extension of Java's `ExecutorService`, called `KeyedExecutorService`, which takes an Object key along with sumbitted requests and guarantees that all requests with the same key (as per `equals()`) will be executed synchronously in relation to each other. This is particularly useful when parallelizing operations where specific subsets of calls to that operation need to be synchronized, but not the operation as a whole. For example, if you have a command that should not be running twice at the same time for the same user, but its ok to run it for different users at the same time, you can send it as a request to a `KeyedExecutorService` using the user's String ID as key, and it makes sure of that;
- Assorted convenience methods for using threading/Async stuff, like creating thread factories, executors, etc;
- The `Utils` class, which has a collection of useful methods, like encoding `Serializable` objects into a string (and back), write XML files, sanitizing a String to avoid edge cases (`null` and empty strings) and reserve two characters for special uses, turning a list of Strings into a single string that can always be turned back using the companion method (also a sample use of the sanitization method), unmodifiable/synchronized views for Graphs and Trees, getting an IUser from a \[Name]#\[Discriminator] String, etc.

## 3rd-party Libraries Used

This library uses other libraries including:
- [Discord4J](https://github.com/austinv11/Discord4J), licensed under the [LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.en.html)
- [SLF4J](https://www.slf4j.org/), licensed under the [MIT License](https://www.slf4j.org/license.html)
- [AWS Java SDK](https://aws.amazon.com/sdk-for-java/), licensed unter the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
- [Woodstox](https://github.com/FasterXML/woodstox), licensed unter the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
- [Guava](https://github.com/google/guava), licensed unter the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
- [GSON](https://github.com/google/gson), licensed unter the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)

For testing:
- [Logback-classic](https://logback.qos.ch/), licensed under the [LGPL 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html)
