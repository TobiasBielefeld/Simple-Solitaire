[![GitHub version](https://badge.fury.io/gh/tobiasBielefeld%2FSimple-Solitaire.svg)](https://badge.fury.io/gh/tobiasBielefeld%2FSimple-Solitaire)
![license](http://img.shields.io/badge/license-GPLv3+-brightgreen.svg)
![platform](http://img.shields.io/badge/platform-Android-blue.svg)

Contact address: tobias.bielefeld [at] gmail.com

[<img alt="Get it on Google Play" height="80" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png">](https://play.google.com/store/apps/details?id=de.tobiasbielefeld.solitaire) [<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/app/de.tobiasbielefeld.solitaire) 


## Help translating here: [<img src="https://poeditor.com/public/images/logo_small.png" alt="Help translating this project" height="30">](https://poeditor.com/join/project/PYX4vcwTjA)


#### Now contains 17 different Solitaire games!
AcesUp, Calculation, Canfield, Forty&Eight, FreeCell, Golf, Grandfather's Clock, Gypsy, Klondike, Mod3, Napoleon's Tomb, Pyramid, SimpleSimon, Spider, TriPeaks, Vegas and Yukon!

# Simple Solitaire

<img src="https://github.com/TobiasBielefeld/Simple-Solitaire/blob/master/pictures/screenshots/1.png" width=200 height=356> <img src="https://github.com/TobiasBielefeld/Simple-Solitaire/blob/master/pictures/screenshots/2.png" width=200 height=356> <img src="https://github.com/TobiasBielefeld/Simple-Solitaire/blob/master/pictures/screenshots/8.png" width=200 height=356> <img src="https://github.com/TobiasBielefeld/Simple-Solitaire/blob/master/pictures/screenshots/4.png" width=200 height=356> 


You can find more screenshots for phones and tablets and the uses card themes as .svg files [here](./pictures/)

There is a DummyGame class with a lot of comments, if you want to add a new game. I hope it's good enough for that!

I learned Android and Java by myself using e-books and browsing Stack Overflow for solutions, so please don't expect too much from my source code :D

It is a very simple game, but customizable and with some neat functions like hints. Here is my Google Play description:

Highly customizable:
There are 6 different looking card sets in this game, 12 different card backgrounds and 6 different background colours. So you have a large choice to customize your experience!

Set up difficulty:
You can set up the difficulty for Klondike, Spider and Golf in the settings!

Automatic saving:
The current game will be saved every time you pause or close the app. So you can continue your game where you left it!

High Score list:
When winning a game, your score will be saved in a list of up to 10 high scores.

Game features:
This game has a undo function to take back up to 20 card movements. A hint function shows you up to 3 possible card movements at once.

Left handed mode: 
There is a option for left handed people to mirror the card positions to the left side of the screen.

No ads:
This game is completely free without ads, tracking or something else. Just have fun :)

Landscape and tablet support: 
You can switch to landscape mode, this is better for larger screens. It is also possible to lock the orientation in the settings

## Hall of shame
Reintroduce the hall of shame, where I will list blatant copies of this app! Every single one of people who like to steal open source software to make profit by including ads! Here they are:

- https://play.google.com/store/apps/details?id=com.atechnos.solitaire
- https://play.google.com/store/apps/details?id=de.classicsolitaire.solitairegame
- https://play.google.com/store/apps/details?id=de.collectionof.solitairegame
- https://play.google.com/store/apps/details?id=de.allinone.solitairegame
- https://play.google.com/store/apps/details?id=de.collectionof.newsolitairegame
- https://play.google.com/store/apps/details?id=startandroid.ru.solitairepack
- https://play.google.com/store/apps/details?id=com.ponglos.solitaire
- https://play.google.com/store/apps/details?id=net.sekmetech.solitaire
- https://play.google.com/store/apps/details?id=com.generic.solitaireallinone

## Installation Guide
The instructions to compile this app are very easy. Just download the project and open Android Studio. Then go to File -> Open -> Navigate to the download location -> Choose the folder -> If you try to start the app, Android Studio should install all necessary dependencies and you are ready to go.

Or alternatively, follow this guide to compile the app on the command line: https://developer.android.com/studio/build/building-cmdline.html

## Usage
If you want to use my work for your own project (which means in most cases: Put ads in it and publish on Google Play Store) YOU HAVE TO FOLLOW THE GPL LICENSE! This means, your project MUST be open source under a GPLv3+ compatible license and MUST contain attribution for the original work! I already found a lot of copies which simply removed my "About Game" screen and changed some graphics.

So please follow the rules! Use this attribution text (or a similar one)

Simple Solitaire Collection - https://github.com/TobiasBielefeld/Simple-Solitaire -
Copyright 2016 - Tobias Bielefeld - tobias.bielefeld@gmail.com -
Licensed under GPLv3+ https://www.gnu.org/licenses/gpl-3.0

And state the changes you made! For example: "Modified to add Google Play Games and ads"

Also think of the other aspects of the GPL license!

## To-Do List

- [X] Add more games (frecell, spider and yukon)
- [X] Improve the settings to use fragments
- [X] Add even more games (Simple Simon, Golf, etc)
- [ ] ~~Add the card themes from Kpatience, if their license is compatible to mine.~~ (I asked the main dev from Kpatience, the licenses of the themes aren't clear, and there are no links to the authors of them...)
- [X] Add a color chooser for background color
- [X] Improve the Highscores to a Statistics Activity (including date stamps)
- [X] Find the reason why the game activity gets created two times when changing the screen orientation in game
- [X] Add a button to mix the cards if no movement can be done
- [X] Add 'Grandfathers Clock'
- [ ] Add a Custom game maker, maybe
- [X] Add a 'poker standard' card theme and 4 color themes
- [X] Add a option to Yukon to play on same suits, instead of different color (like Russian Solitaire)
- [X] Also save high scores when canceling a game
- [ ] Add an option to set up appearance for each game indivially
- [X] Implement "Tap to select/move cards"
- [ ] ~~Add custom images for backgrounds~~ (Would require additional android permissions, I don't really want that)
- [X] Add drag and drop feature to change the order of the games in the main menu

## Translations 
Thanks to the following persons for providing translations :D (Help translating here: https://poeditor.com/join/project/PYX4vcwTjA)
- Esperanto and Polish: verdulo
- French: romainhk and cicithesquirrel
- Japanese: naofum
- Finnish: winjar
- Turkish: sekmenhuseyin
- Italian: imko92 (on POEditor.com)
- Portuguese: Alexandre Parente (on POEditor.com)
- Ukranian: olexn (on POEditor.com)

## Licenses

Poker card theme: Vectorized Playing Cards 2.0 - http://sourceforge.net/projects/vector-cards/
Copyright 2015 - Chris Aguilar - conjurenation@gmail.com
Licensed under LGPL 3 - www.gnu.org/copyleft/lesser.html

The other card themes used in this project were originally published on https://pixabay.com/ under the [CC0 license!](https://creativecommons.org/share-your-work/public-domain/cc0/)

Every card theme was modified by me to add a four color theme! You can download the used pictures as .svg files from my [pictures directory](./pictures/cards)

The Material Design Symbols used for the menu icons are made by Google Inc and are published under the [Apache 2.0 license!](https://www.apache.org/licenses/LICENSE-2.0.txt)

Custom color picker: Uses the [AmbilWarna library](https://github.com/yukuku/ambilwarna) published under the [Apache 2.0 license!](https://www.apache.org/licenses/LICENSE-2.0.txt)

Sliding Tabs: Uses the [PagerSlidingTabStrip library](https://github.com/astuetz/PagerSlidingTabStrip#license) published under the [Apache 2.0 license!](https://www.apache.org/licenses/LICENSE-2.0.txt)

All sounds used in this project were originally published on https://freesound.org under the [CC0 license!](https://creativecommons.org/share-your-work/public-domain/cc0/)

This project is licensed under the GPLv3+ license! Full license text can be found [here](./LICENSE.txt)

```
Copyright (C) 2016  Tobias Bielefeld
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
```
