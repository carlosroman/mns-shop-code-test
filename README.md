M&S Code Puzzle
===============

### Building

To build run:
```bash
mvn clean package
```

### Running app

As this is a library rather than an app, I've added a main class which will
prompt for some input.

```bash
java target/coding-puzzle-0.1-SNAPSHOT.jar
```

It will display:
```bash
Welcome to the shop:
L - list items.
A - add item
B - show basket
T - show basket total
S - get current shipping cost
F - get final cost (inc shipping)
C - clear basket
```

This shows off the functionality of the library. 
It shows that:
* you can load up a catalog of items (the CSV file `catalog.csv`)
* you can load promotions (only By One Get One Half Price) from the file `promotions.json`
* has a shopping basket that can handle promotions and a simple shipping calculator
