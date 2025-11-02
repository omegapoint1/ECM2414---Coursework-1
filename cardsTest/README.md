Cardgame Project – README
======================

Environment / Versions
----------------------
1. Java version:
   - Java 17 or later
2. Build tool: 
   - Maven 3.9.11 or later
3. Junit Version:
   - Junit 5.14.0

--------------------------------------------
   How to run the test suite (cardsTest.zip)
--------------------------------------------

1. Extract cardsTest.zip into a directory.

2. Open a terminal in the cardsTest directory that contains `pom.xml`.

3. Make sure Maven is installed. Check with:
   `mvn -v`

4. To compile and run all tests:
   `mvn clean test`

5. To run a specific test class:
   `mvn -Dtest=cardgame.FILENAME test`

6. Any test results will appear in the terminal and are saved in:
   `target/surefire-reports`


--------------------------------------------------
   How to run the main game program (cards.jar) 
--------------------------------------------------

1. Open a terminal in the directory that contains cards.jar

2. Run the program by entering:
   `java -jar cards.jar`

3. The program will prompt for:
   - The number of players
   - Location of the card pack file

   Note: 3 sample packs: 
   - `4players.txt`
   - `8players.txt`
   - `20players.txt` 
   are available inside: 
   `src/main/resources/`

   Example input:
   ```
   Please enter the number of players: 2
   Please enter location of pack to load: src/main/resources/pack4.txt
   ```
   
4. The player logs and deck logs will be saved as text files in the same directory.


--------------------------------------------
   How to run the test suite (cardsTest.zip)
--------------------------------------------

1. Extract cardsTest.zip into a directory.

3. Make sure Maven is installed. Check with:
   `mvn -v`

4. Open a terminal in the extracted directory containing pom.xml .

5. To compile and run all tests:
   `mvn clean test`

6. To run a specific test class:
   `mvn -Dtest=cardgame.FILENAME test`

7. Any test results will appear in the terminal and are saved in:
   `target/surefire-reports`


--------------------------------------------
   Source and Class files locations
--------------------------------------------
 (starting in directory directly containing this README)

 Source files for main program are located in : 
 `src/main/java/cardgame/`
 Source files for the test classes are located in :
 `src/test/java/cardgame/`
 Bytecode class files for the main program are located in :
 `target/classes/cardgame/`
 Bytecode class files for the test classes are located in :
 `target/test-classes/cardgame/`