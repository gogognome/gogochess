# Gogo Chess
A chess game

## Introduction

Since the late 1990's I have tried to implement the game of chess with artificial intelligence.
This implementation in Java finally  resulted in a computer player that comes up with reasonable moves in a reasonable 
time (at least, on my modern, fast laptop). The goal to implement Gogo Chess is to build a chess game that can beat me. 
Admittedly, I am not a strong chess player at all. All chess education I received consists of reading one book explaining
the rules of the game and perusing David Levy's Computer Chess Compendium.

To make the application easy to use, I created a graphical user interface, which shows a large chess board. Currently, the white player
is the human player and the black player is the computer player. Use the buttons at the bottom right to toggle between humand
and computer player. The human player can enter moves by dragging pieces.

![Image of Gogo Chess](https://gogognome.nl/images/gogochess.png)

You can download Gogo Chess 0.3 from my website by clicking
[this link](https://gogognome.nl/downloads/gogochess-0.3.jar).

## Design principles

This chess implementation was build to make it easy to change the artifical intelligence algorithm. The code is implemented
for optimal readability, not for performance.

A first attempt to implement my own artificial intelligence algorithm resulted in a comptuer player that was way too slow
and playing weakly. After reading a large part of David Levy, "Computer Chess Compendium", 1988, I decided to implement
the articial intelligence described in James J. Gillogly, "The Technology Chess Program" (Chapter 2.5 of Levy's book, which
is a reprint of a publication of Artificial Intelligence, volum 3, 1972, pp. 145-163).

## Build instructions

Build with

    mvn clean install
    
Start with 

    java -jar target/gogochess-0.1.jar

## To be implemented

The following features will be implemented soon, so keep an eye on this project:

* Finish the positional analysis of the endgame