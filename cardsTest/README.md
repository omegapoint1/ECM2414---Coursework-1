Cardgame Project – README
======================

Environment / Versions
----------------------
1. Java version:
   - Java 17 or later
2. Build tool: 
   - Maven 3.9.11 or later


--------------------------------------------------
   How to run the main game program (cards.jar) 
--------------------------------------------------

1. Open a terminal in the directory containing cards.jar.

2. Run the program by entering:
   `java -jar cards.jar`

3. The program will prompt for:
   - The number of players
   - Location of the card pack file

Example input:
   ```
   Please enter the number of players: 2
   Please enter location of pack to load: src/main/resources/pack4.txt
   ```
   
4. The player logs and deck logs will be saved as text files in the same directory.


--------------------------------------------
   Running the test suite (cardsTest.zip)
--------------------------------------------

1. Extract cardsTest.zip into a directory.

2. Make sure Maven is installed. Check with:
   `mvn -v`

3. Open a terminal in the extracted directory containing pom.xml .

4a. To compile and run all tests:
   `mvn clean test`

4b. To run a specific test class:
   `mvn -Dtest=cardgame.FILENAME test`

5. Test results will appear in the terminal and are saved in:
   `target/surefire-reports`
