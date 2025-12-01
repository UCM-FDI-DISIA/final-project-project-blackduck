# BlackDuck Multiplayer Guide

## Overview

BlackDuck now supports LAN multiplayer with a dealer vs player setup. One person hosts as the dealer (server), and another person connects as the player (client) to play blackjack together over a local network.

## Quick Start

### Option 1: Through Main Menu (Recommended)
1. Launch the game normally
2. Click **"Multiplayer"** button
3. Choose **"Host Game"** (Dealer) or **"Join Game"** (Player)

### Option 2: Direct Launch via Command Line

**As Dealer:**
```bash
java -cp bin ui.MultiplayerServerGUI
```

**As Player:**
```bash
java -cp bin ui.MultiplayerClientGUI <server-ip>
```

## Network Setup

### Finding Your IP Address

**On Windows:**
```cmd
ipconfig
```
Look for "IPv4 Address" (e.g., 192.168.1.100)

**On macOS/Linux:**
```bash
ifconfig
# or
ip addr show
```
Look for your local IP address (e.g., 192.168.1.100)

### Port Configuration
- Default port: **7777**
- Make sure this port is not blocked by firewall
- Both machines must be on the same network (or have direct connection)

### Firewall Setup

**Windows:**
1. Open Windows Defender Firewall
2. Click "Allow an app through firewall"
3. Allow Java or add exception for port 7777

**macOS:**
1. System Preferences > Security & Privacy > Firewall
2. Click Firewall Options
3. Allow incoming connections for Java

**Linux:**
```bash
sudo ufw allow 7777/tcp
```

## How to Play

### As the Dealer (Server/Host)

1. **Launch Server**
   - Click "Multiplayer" → "Host Game" from main menu
   - Or run: `java -cp bin ui.MultiplayerServerGUI`

2. **Share Your IP**
   - Your IP address is displayed at the top of the screen
   - Share this IP with the player who wants to join

3. **Wait for Connection**
   - Server will display "Waiting for player to connect..."
   - Once player connects, you'll see their IP address

4. **Wait for Player's Bet**
   - Player must place their bet first
   - You'll see "Player placed bet: $XX" in the log
   - "Start Game" button will become enabled

5. **Start the Game**
   - Click "Start Game" button when ready
   - Cards are dealt to both you and the player
   - You'll see your two cards (fully visible)
   - You'll see the player's two cards
   - Wait for player to complete their turn

6. **Play Your Turn**
   - When it's your turn, the status will show "YOUR TURN!"
   - "Hit" and "Stand" buttons will become enabled
   - Make your decision:
     - **Hit** - Draw another card
     - **Stand** - End your turn and calculate results
   - Your cards and hand value are visible at all times

7. **View Results**
   - Results are calculated and shown to both players
   - Wait for next round (player places new bet)

### As the Player (Client)

1. **Get Dealer's IP**
   - Ask the dealer for their IP address

2. **Connect to Server**
   - Click "Multiplayer" → "Join Game" from main menu
   - Enter the dealer's IP address
   - Click "Join Game"

3. **Place Your Bet**
   - Place bets using the betting buttons ($5, $10, $25, $50)
   - Click "Deal Cards" to send your bet
   - Status will show "Waiting for dealer to start game..."

4. **Play Your Turn**
   - When cards are dealt, you'll see your two cards
   - You'll see one of the dealer's cards
   - Status shows "Your turn! Hit or Stand?"
   - Make your decision:
     - **Hit** - Draw another card
     - **Stand** - End your turn
     - **Double Down** - Double your bet and draw one card

5. **Wait for Dealer**
   - After you stand, status shows "Waiting for dealer's move..."
   - Watch as the dealer plays their hand
   - All dealer cards become visible during their turn

6. **View Results**
   - Results are calculated and chips updated automatically
   - Place a new bet to start another round

7. **Connection Status**
   - Green "Connected" indicator when connected
   - Red "Disconnected" if connection is lost

## Game Protocol

### Message Types

The game uses a comprehensive message protocol:

- **CONNECT_REQUEST/ACCEPT** - Connection handshake
- **PLACE_BET** - Player places a bet
- **DEAL_CARDS** - Request initial cards
- **PLAYER_ACTION** - Hit, Stand, or Double Down
- **UPDATE_GAME_STATE** - Sync cards and values
- **CARD_DEALT** - New card dealt to player
- **ROUND_END** - Round complete with results

### Gameplay Flow

```
1. Player connects to Dealer
2. Player places bet and clicks "Deal Cards"
3. Dealer clicks "Start Game" button
4. Cards dealt to both Dealer and Player (2 cards each)
5. Player's turn:
   - Player sees their cards and one dealer card
   - Player makes moves (Hit/Stand/Double)
   - Dealer waits (sees "Waiting for player...")
6. Dealer's turn:
   - After player stands, dealer's turn begins
   - Player waits (sees "Waiting for dealer's move...")
   - Dealer clicks Hit or Stand manually
   - Dealer sees all their cards and decides strategy
7. Results calculated and shown to both players
8. Chips updated automatically for player
9. Repeat from step 2
```

## Troubleshooting

### Player Can't Connect

**Check Network:**
- Are both computers on the same network?
- Try pinging the dealer's IP: `ping <dealer-ip>`

**Check Firewall:**
- Is port 7777 open on both machines?
- Try temporarily disabling firewall to test

**Check IP Address:**
- Make sure you're using the correct local IP (not 127.0.0.1)
- IP should start with 192.168.x.x or 10.x.x.x

### Connection Lost During Game

- Check network stability
- Restart both server and client
- Ensure no other application is using port 7777

### Cards Not Displaying

- Ensure card images are in correct location: `src/data/images/PNG-cards-1.3/`
- Check that CardImages class can load resources

### Game Freezes

- Network connection may be unstable
- Check system resources (CPU, memory)
- Restart both applications

## Advanced Configuration

### Changing the Port

Edit `GameServer.java` and `GameClient.java`:

```java
private static final int DEFAULT_PORT = 7777; // Change this
```

Recompile after making changes.

### Playing Over Internet (Advanced)

⚠️ **Not recommended for production use** - This is an educational project.

1. Setup port forwarding on router (port 7777)
2. Use dealer's public IP address
3. Ensure proper security measures

## Technical Details

### Architecture

```
┌─────────────────┐         ┌─────────────────┐
│  Game Server    │◄───────►│  Game Client    │
│  (Dealer)       │  TCP    │  (Player)       │
└─────────────────┘ Socket  └─────────────────┘
        │                            │
        │                            │
    ┌───▼───┐                   ┌───▼───┐
    │ Deck  │                   │ Cards │
    │ Hand  │                   │ Chips │
    └───────┘                   └───────┘
```

### Classes

- **GameServer** - Handles server logic, deck management, dealer AI
- **GameClient** - Manages client connection and sends player actions
- **GameMessage** - Serializable message protocol
- **MultiplayerServerGUI** - Server user interface
- **MultiplayerClientGUI** - Client user interface
- **MultiplayerDialog** - Mode selection dialog

### Data Flow

1. All game state managed on server
2. Client sends actions (bets, hit, stand, etc.)
3. Server processes and responds with updated state
4. Card objects serialized and transmitted
5. Client updates UI based on server messages

## Known Limitations

- Only one player can connect to a server at a time
- No reconnection support (if disconnected, must restart)
- Server IP must be shared manually
- No game state persistence in multiplayer mode
- Single-player chips/settings separate from multiplayer
- Dealer must manually play their hand (no AI assistance)

## Best Practices

### For Dealers:
- Keep server window open during game
- Monitor the log panel for any issues
- Share correct IP address (not localhost)
- Wait for player to place bet before starting game
- Pay attention when it's your turn (status shows "YOUR TURN!")
- Standard blackjack strategy: Hit on 16 or less, Stand on 17 or more

### For Players:
- Verify connection before starting
- Don't close window during active round
- Ensure stable network connection

### For Both:
- Be on same network for best performance
- Close other network-intensive applications
- Use wired connection if possible for stability

## Future Enhancements

Potential improvements for this multiplayer system:
- Multiple players at one table
- Chat functionality
- Reconnection support
- Game history/statistics
- Server browser/discovery
- Spectator mode
- Tournament mode

## Support

For issues or questions:
1. Check this guide
2. Review main [README.md](README.md)
3. Check network connectivity
4. Verify firewall settings

---

**Remember:** This is a LAN multiplayer implementation designed for educational purposes. Always ensure proper network security when opening ports or sharing IP addresses.
