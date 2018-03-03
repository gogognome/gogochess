# gogochess
A chess game

## Introduction

Since the late 1990's I have tried to implement the game of chess with artificial intelligence. This implementation in Java finally 
resulted in a computer player that comes up with reasonble moves in a reasonable time (at least, on my modern, fast laptop).

To make the application easy to use, I created a graphical user interface, which shows a large chess board. Currently, the white player
is the human player and the black player is the computer player. The human player can enter moves by dragging pieces.

## Build instructions

Build with

    mvn clean install
    
Start with 

    java -jar target/gogochess-0.1.jar

## To be implemented

The following features will be implemented soon, so keep an eye on this project:

* When human player promotes a pawn, let the human player select the new type of piece
