# Scacchi_Java

A Java chess application with a graphical user interface.

## Features

- Complete chess game implementation with all standard rules
- Graphical board using Java Swing
- Move validation and legal move highlighting
- Check and checkmate detection
- Stalemate detection
- Special moves: castling, en passant, pawn promotion
- Move history tracking
- Visual indicators for:
  - Selected piece
  - Valid moves
  - Last move
  - King in check

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building

```bash
mvn clean compile
```

## Testing

```bash
mvn test
```

## Running

```bash
mvn exec:java -Dexec.mainClass="com.chess.Main"
```

Or build a JAR and run:

```bash
mvn package
java -jar target/scacchi-java-1.0-SNAPSHOT.jar
```

## How to Play

1. Click on a piece to select it
2. Valid moves will be highlighted on the board
3. Click on a highlighted square to move the piece
4. The game alternates between white and black turns
5. Use the menu to start a new game or exit

## Controls

- **Left-click**: Select a piece or make a move
- **Game Menu**: New Game, Exit
- **Help Menu**: Rules, About