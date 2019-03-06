# Gogo Chess

A computer chess game written in Java, based on a game from the 1970s.

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

## Download

You can download Gogo Chess 1.4 from my website by clicking
[this link](https://gogognome.nl/downloads/gogochess-1.4.jar).
Double click the downloaded file to start Gogo Chess.

To run Gogo Chess you need a Java Runtime (version 8 or newer). Most modern computers have a Java Runtime
installed. If your computer does not have a Java Runtime, then download one from
[the website of Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).

## Manual

The following sections explain how to determine which player is played by a human and which by the computer
and how long the computer will think. You can change these settings before the start of the game and also
during the game.

### Human player or computer player?

Use the buttons at the bottom of the page to setup whether the white player is a human or computer player and
whtether the black player is a human or computer player. For example in the image above, you can see the outline
of a human on the white button, indicating that the white player is a human player. The outline of a computer
on the black button indicates that the black player is a computer player. Click on the white or black button
to toggle between human or computer player. Beware: if you first toggle the white player, then both players
will be computer players and the computer starts playing the game. If you want to play as human player with
black against the computer with white, first toggle the black button to human and then toggle the white button
to computer.

### How long does the computer player think?

The computer will think ahead a number of moves ahead. In certain situations, many moves need to be investiaged,
which might take a lot more time than when just a couple of moves need to be investigated. The computer player
has two possible modes to control how long the computer player thinks for a move:

1. The computer thinks a configurable maximum number of seconds
2. The computer thinks ahead a configurable number of moves

In mode 1 the computer will think ahead a number of moves, but the computer will change this number to limit
the thinking time to the configured maximum time.

At the bottom right you can control how long the computer will think. The hourglass button indicates that the
computer thinks for a maximum number of seconds (mode 1). By default the computer will
think for 15 seconds. Click on the arrow up or down buttons to let the computer think longer or shorter.

Click on the hourglass to change the mode to mode 2, where the computer thinks ahead a number of moves.
By default the computer will think ahead 3 moves in this mode. Click on the arrow up or down buttons to change
the number of moves the computer will think ahead.

### Undo and redo moves

Click on the button with the arrow to the left to undo the last move. It is possible to undo as far as the beginning 
of the game and redo moves. Click on the button with the arrow to the right to redo a move.

The currently selected move is shown bold in the moves panel. When undoing moves, both players will be controlled 
by humans as a way to pause the computer from thinking up new moves again. 

If instead of redoing moves a new move is made (either by a human or computer player),
all moves following the current move are removed and the new move is added as last move.

## Details for software developers

This section contains information for software developers. If you want to figure out how this chess game
works under the hood, or if you want to build it yourself, then continue reading. If you are not a software
developer and just want to play the game, then stop reading and start playing.

### Design principles

This chess implementation was build to make it easy to change the artifical intelligence algorithm. The code is implemented
for optimal readability, not for performance.

A first attempt to implement my own artificial intelligence algorithm resulted in a comptuer player that was way too slow
and playing weakly. After reading a large part of David Levy, "Computer Chess Compendium", 1988, I decided to implement
the articial intelligence described in James J. Gillogly, "The Technology Chess Program" (Chapter 2.5 of Levy's book, which
is a reprint of a publication of Artificial Intelligence, volum 3, 1972, pp. 145-163).

### Build instructions

Build with

    mvn clean install
    
Start with 

    java -jar target/gogochess-1.1.jar
