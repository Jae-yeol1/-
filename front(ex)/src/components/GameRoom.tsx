import { ArrowLeft } from 'lucide-react';
import { Button } from './ui/button';
import type { Room, GameType } from '../App';
import BlackjackGame from './games/BlackjackGame';
import BaccaratGame from './games/BaccaratGame';
import SevenPokerGame from './games/SevenPokerGame';

interface GameRoomProps {
  username: string;
  room: Room | null;
  soloGame: { gameType: GameType; deckCount: number } | null;
  onLeave: () => void;
}

const GAME_NAMES = {
  blackjack: '블랙잭',
  baccarat: '바카라',
  sevenpoker: '세븐포커'
};

export default function GameRoom({ username, room, soloGame, onLeave }: GameRoomProps) {
  const gameType = room?.gameType || soloGame?.gameType;
  const deckCount = room?.deckCount || soloGame?.deckCount || 1;
  const isSolo = !!soloGame;

  if (!gameType) return null;

  return (
    <div className="min-h-screen p-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              onClick={onLeave}
              className="text-white hover:bg-white/10"
            >
              <ArrowLeft className="w-5 h-5 mr-2" />
              나가기
            </Button>
            <div className="text-white">
              <h2 className="text-xl">{GAME_NAMES[gameType]}</h2>
              <p className="text-sm text-white/70">
                {isSolo ? '솔로 플레이' : room?.name} · {deckCount}덱
              </p>
            </div>
          </div>
          {!isSolo && room && (
            <div className="text-white/70 text-sm">
              플레이어: {room.players.join(', ')}
            </div>
          )}
        </div>

        {/* Game Content */}
        <div className="bg-white/5 backdrop-blur-sm rounded-lg border border-white/20">
          {gameType === 'blackjack' && (
            <BlackjackGame username={username} deckCount={deckCount} isSolo={isSolo} />
          )}
          {gameType === 'baccarat' && (
            <BaccaratGame username={username} deckCount={deckCount} isSolo={isSolo} />
          )}
          {gameType === 'sevenpoker' && (
            <SevenPokerGame username={username} deckCount={deckCount} isSolo={isSolo} players={room?.players || [username]} />
          )}
        </div>
      </div>
    </div>
  );
}
