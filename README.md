# Hangman Game

## Objective
The objective of this assignment is to create a classic Hangman game using a client-server architecture. The game supports both single-player and multiplayer modes and is implemented using socket programming and multithreading.

## Features
- Supported Clients
  - The application supports multiple users playing concurrently (at least 4).
- User Authentication
  - Login: Existing users can log in using their username and password.
  - Registration: New users need to register by providing their name, username, and password.
- Error Handling:
  - If the password is incorrect, a 401 Unauthorized error is returned.
  - If the username is not found, a 404 Not Found error is returned.
  - If the username is already taken during registration, a custom error message is displayed.
- Game Setup
  - The server loads the following files on startup:
  - User login credentials.
  - Score history for each user.
  - Game configuration (number of incorrect guesses allowed, maximum and minimum number of players per team or game room, etc.).
  - A lookup file containing phrases to be guessed (one phrase per line).
- Game Options
  - After logging in, users can choose from the following menu options:
  - Play as a single player.
  - Play with multiple players (multiplayer mode).
  - Users can quit the game anytime by pressing the ‘-’ character.
- Teams
  - Users can form teams to play against each other.
  - Team names must be unique.
  - The server checks that the number of players in both teams is equal before starting the game. If not, an error message is displayed.
  - Team formation criteria are specified by the developer.
- The Game
  - The phrase to be guessed is displayed as underscores (_ _ _ _ _ _ _ _ _ _ _ _).
  - Word guessing is case-insensitive.
  - The game is turn-based, and the server displays the current player’s name for each turn.
  - Correct guesses are shown to all users in the game, and the user's score is updated and displayed.
- Number of Attempts
  - The game ends when users are out of attempts, even if the phrase is not fully guessed.
  - The correct phrase is displayed at the end of the game.
  - The number of remaining attempts is updated and displayed for each user after an incorrect guess.
- Scores
  - Each user has a score history for their last single/multiplayer games.
  - Score calculation criteria are specified by the developer and apply to all game types.
 
## Technologies
  - JAVA
