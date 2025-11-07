import { useState } from 'react';
import LoginPage from './components/LoginPage';
import MainMenu from './components/MainMenu';
import MultiplayerLobby from './components/MultiplayerLobby';
import SoloGameSelect from './components/SoloGameSelect';
import GameRoom from './components/GameRoom';

export type GameType = 'blackjack' | 'baccarat' | 'sevenpoker';

export interface Room {
  id: string;
  name: string;
  gameType: GameType;
  deckCount: number;
  players: string[];
  maxPlayers: number;
  hostId: string;
}

type Screen = 'login' | 'menu' | 'multiplayer' | 'solo' | 'game';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('login');
  const [username, setUsername] = useState('');
  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);
  const [soloGame, setSoloGame] = useState<{ gameType: GameType; deckCount: number } | null>(null);

  const handleLogin = (name: string) => {
    setUsername(name);
    setCurrentScreen('menu');
  };

  const handleMultiplayer = () => {
    setCurrentScreen('multiplayer');
  };

  const handleSolo = () => {
    setCurrentScreen('solo');
  };

  const handleJoinRoom = (room: Room) => {
    setSelectedRoom(room);
    setCurrentScreen('game');
  };

  const handleStartSoloGame = (gameType: GameType, deckCount: number) => {
    setSoloGame({ gameType, deckCount });
    setCurrentScreen('game');
  };

  const handleBackToMenu = () => {
    setSelectedRoom(null);
    setSoloGame(null);
    setCurrentScreen('menu');
  };

  const handleBackToLobby = () => {
    setCurrentScreen('multiplayer');
  };

  const handleLeaveGame = () => {
    setSelectedRoom(null);
    setSoloGame(null);
    if (soloGame) {
      setCurrentScreen('solo');
    } else {
      setCurrentScreen('multiplayer');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-900 via-green-800 to-emerald-900">
      {currentScreen === 'login' && (
        <LoginPage onLogin={handleLogin} />
      )}
      {currentScreen === 'menu' && (
        <MainMenu
          username={username}
          onMultiplayer={handleMultiplayer}
          onSolo={handleSolo}
        />
      )}
      {currentScreen === 'multiplayer' && (
        <MultiplayerLobby
          username={username}
          onJoinRoom={handleJoinRoom}
          onBack={handleBackToMenu}
        />
      )}
      {currentScreen === 'solo' && (
        <SoloGameSelect
          onStartGame={handleStartSoloGame}
          onBack={handleBackToMenu}
        />
      )}
      {currentScreen === 'game' && (
        <GameRoom
          username={username}
          room={selectedRoom}
          soloGame={soloGame}
          onLeave={handleLeaveGame}
        />
      )}
    </div>
  );
}
