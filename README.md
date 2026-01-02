
![Logo](src/data/images/logo.png)

# BlackDuck - Blackjack Game

A feature-rich, full-screen Blackjack game built with Java Swing, featuring persistent game data, customizable settings, and an immersive gaming experience.

## Features

### 🎮 Core Gameplay
- **Classic Blackjack Rules** - Play traditional Blackjack against the dealer
- **Full Betting System** - Place bets with virtual chips ($5, $10, $25, $50)
- **Clear Bet** - Clear your current bet before dealing to adjust your wager
- **Double Down** - Double your bet and take exactly one more card
- **Win Streaks** - Track your consecutive wins
- **Smart Dealer AI** - Dealer behavior adapts to difficulty settings
- **🌐 Multiplayer (Beta)** - Play dealer vs player on the same PC using multiple instances

### 💰 Virtual Economy
- **Persistent Chip Balance** - Your chips are automatically saved and restored between sessions
- **Virtual Chip Purchases** - Buy more chips with test payment system
- **Multiple Chip Packages**:
  - 50 chips - $5
  - 100 chips - $10
  - 250 chips - $20
  - 500 chips - $35
  - 1000 chips - $60
  - 5000 chips - $250
- **Test Card Support** - Use the "Use Test Card" button to auto-fill valid test card details
- **Starting Balance** - Begin with 100 chips
- **Out of Chips** - When balance reaches $0, betting is disabled until chips are purchased

### 🎨 Customization
- **Background Themes**
  - Default Dark Theme (free)
  - Green Poker Table (purchasable with 150 chips)
  - Animated Menu Background (purchasable with 200 chips)
- **Custom Card Images** - High-quality PNG card graphics
- **Full-Screen Mode** - Immersive, distraction-free gaming

### ⚙️ Game Settings
- **Difficulty Levels**
  - Easy: Dealer hits on 16 or less
  - Medium: Standard blackjack rules (hits on soft 17)
  - Hard: Dealer plays optimally with soft hands

- **Luck Levels**
  - Normal: Standard blackjack odds
  - Lucky: 10% bonus on wins, 15% chance to win on push
  - Very Lucky: 25% bonus on wins, 25% chance to win on push, 10% chance bet is returned on loss

- **Audio Settings**
  - Sound Volume: Off / Low / Medium / High
  - Background Music: On / Off

### 💾 Data Persistence
- **Automatic Save System** - Chips, purchases, and settings are saved automatically
- **Database Storage** - Uses properties file (`gamedata.properties`) to store:
  - Current chip balance
  - Owned backgrounds/themes
  - Selected background
  - Volume level
  - Music on/off preference
  - Difficulty level
  - Luck level

### 🎯 User Interface
- **Intuitive Controls**
  - Click-based betting system
  - Clear status messages
  - Real-time chip/bet/streak display

- **Visual Feedback**
  - Animated buttons with hover effects
  - Color-coded stats (gold for chips, green for wins, red for bets)
  - Card value display for both player and dealer

- **Menu System**
  - Main menu with game start, settings, and quit options
  - Settings panel with scrollable options
  - In-game menu button for quick navigation

### 🔊 Audio System
- **Sound Effects** - Button clicks and game events with adjustable volume
  - Button click sounds
  - Bet placement sound
  - Win celebration sound
  - Loss sound
  - Push/tie sound
- **Background Music** - Looping background music (toggle on/off)
- **Volume Control** - Four levels: Off, Low, Medium, High
- **Persistent Settings** - Audio preferences saved between sessions
- **Audio Files** (located in `src/data/audio/`):
  - `click.wav` - Button click sound effect
  - `bet.wav` - Bet placement sound effect
  - `win.wav` - Win celebration sound effect
  - `lose.wav` - Loss sound effect
  - `push.wav` - Push/tie sound effect
  - `background_music.wav` - Background music loop

### ⌨️ Keyboard Shortcuts
- **ESC** - Exit the application

### 🌐 Multiplayer Mode (BETA)
- **Host Game (Dealer)** - Start a server and act as the dealer for another player
- **Join Game (Player)** - Connect to a dealer's server and play as a remote player
- **Same-PC Testing** - Currently works by running two instances on the same computer (use `localhost` or `127.0.0.1` to connect)
- **Real-time Gameplay** - All actions synchronized between dealer and player
- **Server Info Display** - Dealer can see their IP address for connection sharing

> **Note:** Multiplayer is currently in beta. Chip balances in multiplayer mode are session-only and not saved to the database. For persistent chip tracking, use single-player mode.

## How to Play

### Single Player Mode

1. **Start the Game** - Click "Start Game" from the main menu
2. **Place Your Bet** - Use the betting buttons ($5, $10, $25, $50) to place your bet
3. **Deal Cards** - Click "Deal Cards" to start the round
4. **Make Your Move**
   - **Hit** - Take another card
   - **Stand** - Keep your current hand
   - **Double Down** - Double your bet and take one final card (available only with 2 cards)
5. **Win or Lose** - Beat the dealer without going over 21
6. **Repeat** - Place a new bet and play another round

### Multiplayer Mode (Beta)

> **Current Limitation:** Multiplayer currently works best when running two instances on the same computer. Use `localhost` or `127.0.0.1` as the IP address to connect.

#### As Dealer (Host):
1. **Click "Multiplayer"** from the main menu
2. **Select "Host Game"** - Server starts automatically on port 7777
3. **Note Your IP** - Your IP address is displayed on screen (use `localhost` for same-PC testing)
4. **Wait for Player** - Game begins when a player connects
5. **Monitor Game** - Watch the player's actions and dealer responses in real-time
6. **View Logs** - Server activity logged in the bottom panel

#### As Player (Client):
1. **Click "Multiplayer"** from the main menu
2. **Select "Join Game"** and enter the dealer's IP address (use `localhost` for same-PC testing)
3. **Connect** - Wait for connection confirmation
4. **Place Bets** - Use betting buttons like in single-player mode
5. **Play Cards** - Hit, Stand, or Double Down as usual
6. **Results** - Chips updated based on game outcome (session-only, not saved)

## Winning Conditions

- **Blackjack** - Get 21 with your first two cards (pays 3:2 + luck bonus)
- **Higher Hand** - Beat the dealer's hand without busting (pays 2:1 + luck bonus)
- **Dealer Bust** - Dealer goes over 21 (pays 2:1 + luck bonus)
- **Push** - Tie with the dealer (bet returned, possible luck bonus)

## System Requirements

- Java 8 or higher
- Display resolution: 1024x768 or higher (recommended for full-screen)
- Operating System: Windows, macOS, or Linux

## Project Structure

```
src/
├── data/
│   ├── Card.java          - Card data model (Serializable)
│   ├── Rank.java          - Card rank enum with values
│   ├── Suit.java          - Card suit enum
│   ├── ChipsDatabase.java - Persistent storage handler
│   ├── audio/             - Audio files folder
│   │   ├── click.wav      - Button click sound
│   │   ├── bet.wav        - Bet placement sound
│   │   ├── win.wav        - Win celebration sound
│   │   ├── lose.wav       - Loss sound
│   │   ├── push.wav       - Push/tie sound
│   │   └── background_music.wav - Background music
│   └── images/            - Image files folder
│       ├── logo.png       - Game logo
│       ├── PNG-cards-1.3/ - Playing card images
│       ├── First_Background.png
│       ├── Second_Background.webp
│       └── Third_Background.png
├── logic/
│   ├── Deck.java          - Deck management and shuffling
│   └── Hand.java          - Hand calculation and blackjack logic
├── network/
│   ├── GameMessage.java   - Network message protocol (Serializable)
│   ├── GameServer.java    - Server (dealer) networking logic
│   └── GameClient.java    - Client (player) networking logic
└── ui/
    ├── BlackjackGUI.java           - Main game interface
    ├── MultiplayerDialog.java      - Multiplayer mode selection
    ├── MultiplayerServerGUI.java   - Dealer server interface
    ├── MultiplayerClientGUI.java   - Player client interface
    ├── RedButton.java              - Custom styled button
    ├── AnimatedBackgroundPanel.java - Animated menu background
    ├── CardImages.java             - Card image loading and caching
    ├── PaymentDialog.java          - Virtual chip purchase interface
    ├── AudioManager.java           - Audio playback and volume control
    ├── UIConstants.java            - Centralized UI constants
    └── GraphicsUtil.java           - Graphics utility methods
```

## Technical Highlights

### Clean Code Architecture
- **Separation of Concerns** - Data, logic, and UI are separated into distinct packages
- **No Code Repetition** - Utility classes eliminate duplicate code
- **Enum-Based Design** - Card ranks and suits use enums with display methods
- **Helper Methods** - Dialog helpers and graphics utilities reduce boilerplate

### Multiplayer Networking (Beta)
- **TCP Sockets** - Reliable connection between dealer and player
- **Object Serialization** - Game state transmitted as serializable objects
- **Message Protocol** - Comprehensive GameMessage class handles all game events
- **Threaded I/O** - Non-blocking network communication
- **Real-time Sync** - Card deals, bets, and results synchronized instantly
- **Default Port** - Uses port 7777
- **Current Status** - Beta feature, tested with same-PC instances using localhost

### Performance Optimizations
- **Image Caching** - Card images are cached to prevent redundant loading
- **Efficient Rendering** - Graphics2D with antialiasing for smooth visuals
- **Lazy Loading** - Resources loaded only when needed

### User Experience
- **Immediate Feedback** - Real-time updates to chips, bets, and game state
- **Error Prevention** - Buttons disabled when actions aren't available
- **Persistent State** - Game state automatically saved after every change

## Running the Application

### From Source
```bash
# Compile all files
javac -d bin src/**/*.java

# Run single player game
java -cp bin ui.BlackjackGUI

# Run multiplayer server (dealer)
java -cp bin ui.MultiplayerServerGUI

# Run multiplayer client (player) - replace <server-ip> with dealer's IP
java -cp bin ui.MultiplayerClientGUI <server-ip>
```

### From IDE
Open the project in your favorite Java IDE and run `BlackjackGUI.java`

## Credits

Developed as a final project for CS course.

## License

Educational project - see your course guidelines for usage restrictions.

---

**Note:** This game uses test payment cards for demonstration purposes. No real money or payment processing is involved.
