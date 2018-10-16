# Keeper of the Seven Keys - Full stack coding challenge

Locked in a maze, you must find the seven hidden keys in order to escape. Unlock the gate to complete the challenge!

**Use Java 8+ to code some logic capable of finding the keys to escape any random maze.**

# Framework

Some Java objects (**Maze**, **Position**), some interfaces (**Keeper**, **Observable**) and some enumerations (**Action**, **Cell**) will be provided to you. ***These classes cannot be modified***.

![Framework](https://i.imgur.com/JBbQtZJl.png)

The maze is randomly generated and is represented by an array of 40x40 cells. Each Cell can be ***EMPTY***, a ***WALL***, a ***KEY*** or the ***DOOR***.

```java
package tws.keeper.model;

/**
 * Represent the contents of a Cell
 */
public enum Cell {

    WALL("wall"), PATH("path"), KEY("key"), DOOR("door");

    private String stringValue;

    Cell(String theValue) {
        stringValue = theValue;
    }

    public String toString() {
        return stringValue;
    }

}
```

The keys, the door and the keeper are randomly placed. The maze is always solvable.

```java
package tws.keeper.model;

public class Position {

    private final int vertical, horizontal;

    /**
     * Construct an immutable instance
     */
    public Position(int vert, int horz) {
        vertical = vert;
        horizontal = horz;
    }

    /**
     * Getter
     */
    public int getVertical() {
        return vertical;
    }

    /**
     * Getter
     */
    public int getHorizontal() {
        return horizontal;
    }

}
```

The **Maze** is **Observable**, which means that we can look up, down, left or right from our current Position, which will return a **Cell** value. We can also retrieve the total number of keys (it would be always 7), the keys that have already been found, and our current **Position**. We can also know if the maze have been completed.

```java
package tws.keeper.model;

public interface Observable {

    Cell lookUp();
    Cell lookDown();
    Cell lookLeft();
    Cell lookRight();
    int getKeysFound();
    int getTotalNumberOfKeys();
    boolean isMazeCompleted();
    Position getKeeperPosition();
    
}
```

The logic of the keeper should implement the **Keeper** interface. The system should create a new **Maze** instance passing an instance of the keeper logic in the constructor. The system should then call the *act* method of the keeper, each time passing in an observable instance of the maze. 

```java
package tws.keeper.model;

/**
 * Keeper interface
 */
public interface Keeper {
    Action act(Observable maze);
}
```

The keeper logic should each time decide what to do next and return an **Action**.

```java
package tws.keeper.model;

/**
 * This is what a keeper can do
 */
public enum Action {

    GO_UP,
    GO_DOWN,
    GO_LEFT,
    GO_RIGHT,
    DO_NOTHING

}
```

# Website

A basic website that can display the maze is also provided to you. You can *start*/*stop* the Keeper and *reset* the system (creating a new random maze).

![Keeper of the Seven Keys](https://i.imgur.com/AuG6Xpy.png)

When you click *start*, the system will keep calling the *act* method until the maze is solved or the user wants to stop the game.

# Starting the server

You can start the web server with the **mvn spring-boot:run** command. 

Navigate to [http://localhost:8080](http://localhost:8080) to display the website.

*You need **Java 8** and **maven** installed in your system!*

# The solution

Int the */src/main/java/tws/keeper/solution* folder there is a *KeeperAI.java* class that implements the *Keeper* interface. This Artificial Intelligence is not so intelligent - it only takes random actions! Please rewrite this class so the Keeper is smart enough to find the seven keys and then go for the door, optimizing the route as much as possible.

![Solution](https://i.imgur.com/yu6u34a.png)

# Considerations

Please upload your solution to GitHub or any file hosting system. It must have a readme.md file  that contains, at least:

* Your full name, email address, phone number & Passport or ID Card Number
* Basic description of your solution

# Evaluation criteria

Different aspects of the solution will be taken into consideration for the final score:
#### Correctness 
The solution should solve the problem in the most practical way possible, ideally with no bugs or unhandled edge cases.
#### Design
OOP principles should be present throughout the solution. It should be simple, intuitive and easy to understand.
#### Performance
The solution should make optimal use of resources such as CPU, memory or disk.
#### Tests
Everything should be reasonably unit tested. Tests should guarantee correctness. Edge cases or negative flows should be also tested.
#### Documentation
Your code should be documented.
#### Maintainability
The solution should be maintainable by a development team. This means, among other things, that it should be ready to accommodate future requirements with minimum effort.
#### Reusability
Whenever possible, parts of the solution should be ready to be reused in other similar problems. 
#### Simplicity
Solve the challenge with as less code as possible without sacrificing any of the above.

# Hints

Separation of concerns principles are always helpful.

Java sources should be documented with Javadoc. 

Test coverage reports would be a plus.

Functional programming is powerful! And Java supports some of it.

Recursiveness is fun but could get you in trouble here. Use at your own risk.

At first, the keeper does not know anything about the maze. It could start walking using any strategies and keeping track of the visited cells...

**Good luck!**
