
![Logo](logo.png)

# BlackDuck - Blackjack Game

A feature-rich, full-screen Blackjack game built with Java Swing, featuring persistent game data, customizable settings, and an immersive gaming experience.

## Features

### 🎮 Core Gameplay
- **Classic Blackjack Rules** - Play traditional Blackjack against the dealer
- **Full Betting System** - Place bets with virtual chips ($5, $10, $25, $50)
- **Double Down** - Double your bet and take exactly one more card
- **Win Streaks** - Track your consecutive wins
- **Smart Dealer AI** - Dealer behavior adapts to difficulty settings

### 💰 Virtual Economy
- **Persistent Chip Balance** - Your chips are automatically saved and restored between sessions
- **Virtual Chip Purchases** - Buy more chips with test payment system
- **Multiple Chip Packages** - Choose from 6 different chip packages (50 to 5000 chips)
- **Starting Balance** - Begin with 100 chips

### 🎨 Customization
- **Background Themes**
  - Default Dark Theme (free)
  - Green Poker Table (purchasable with 150 chips)
- **Custom Card Images** - High-quality PNG card graphics
- **Animated Menu** - Dynamic background with moving gradients
- **Full-Screen Mode** - Immersive, distraction-free gaming

### ⚙️ Game Settings
- **Difficulty Levels**
  - Easy: Dealer hits on 16 or less
  - Medium: Standard blackjack rules (hits on soft 17)
  - Hard: Dealer plays optimally with soft hands

- **Luck Levels**
  - Normal: Standard blackjack odds
  - Lucky: 10% bonus on wins, 15% chance to win on push
  - Very Lucky: 25% bonus on wins, 25% chance to win on push, 10% chance to recover losses

### 💾 Data Persistence
- **Automatic Save System** - Chips, purchases, and settings are saved automatically
- **Database Storage** - Uses properties file (`gamedata.properties`) to store:
  - Current chip balance
  - Owned backgrounds/themes
  - Selected background

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

### 🔊 Audio (Optional)
- Click sound effects for button interactions

### ⌨️ Keyboard Shortcuts
- **ESC** - Exit the application

## How to Play

1. **Start the Game** - Click "Start Game" from the main menu
2. **Place Your Bet** - Use the betting buttons ($5, $10, $25, $50) to place your bet
3. **Deal Cards** - Click "Deal Cards" to start the round
4. **Make Your Move**
   - **Hit** - Take another card
   - **Stand** - Keep your current hand
   - **Double Down** - Double your bet and take one final card (available only with 2 cards)
5. **Win or Lose** - Beat the dealer without going over 21
6. **Repeat** - Place a new bet and play another round

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
│   ├── Card.java          - Card data model
│   ├── Rank.java          - Card rank enum with values
│   ├── Suit.java          - Card suit enum
│   └── ChipsDatabase.java - Persistent storage handler
├── logic/
│   ├── Deck.java          - Deck management and shuffling
│   └── Hand.java          - Hand calculation and blackjack logic
└── ui/
    ├── BlackjackGUI.java           - Main game interface
    ├── RedButton.java              - Custom styled button
    ├── AnimatedBackgroundPanel.java - Animated menu background
    ├── CardImages.java             - Card image loading and caching
    ├── PaymentDialog.java          - Virtual chip purchase interface
    ├── UIConstants.java            - Centralized UI constants
    └── GraphicsUtil.java           - Graphics utility methods
```

## Technical Highlights

### Clean Code Architecture
- **Separation of Concerns** - Data, logic, and UI are separated into distinct packages
- **No Code Repetition** - Utility classes eliminate duplicate code
- **Enum-Based Design** - Card ranks and suits use enums with display methods
- **Helper Methods** - Dialog helpers and graphics utilities reduce boilerplate

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
# Compile
javac -d bin src/data/*.java src/logic/*.java src/ui/*.java

# Run
java -cp bin ui.BlackjackGUI
```

### From IDE
Open the project in your favorite Java IDE and run `BlackjackGUI.java`

## Credits

Developed as a final project for CS course.

## License

Educational project - see your course guidelines for usage restrictions.

---

**Note:** This game uses test payment cards for demonstration purposes. No real money or payment processing is involved.
