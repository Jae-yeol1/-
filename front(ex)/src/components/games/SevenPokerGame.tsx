import { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import PlayingCard from './PlayingCard';

interface Card {
  suit: 'hearts' | 'diamonds' | 'clubs' | 'spades';
  rank: string;
  value: number;
}

interface SevenPokerGameProps {
  username: string;
  deckCount: number;
  isSolo: boolean;
  players: string[];
}

interface PlayerState {
  name: string;
  cards: Card[];
  visibleCards: Card[];
  folded: boolean;
  chips: number;
  currentBet: number;
}

export default function SevenPokerGame({ username, deckCount, isSolo, players }: SevenPokerGameProps) {
  const [deck, setDeck] = useState<Card[]>([]);
  const [playerStates, setPlayerStates] = useState<PlayerState[]>([]);
  const [gameStatus, setGameStatus] = useState<'betting' | 'dealing' | 'finished'>('betting');
  const [currentRound, setCurrentRound] = useState(0);
  const [pot, setPot] = useState(0);
  const [result, setResult] = useState<string>('');

  useEffect(() => {
    initializeDeck();
    initializePlayers();
  }, [deckCount]);

  const initializeDeck = () => {
    const suits: Array<'hearts' | 'diamonds' | 'clubs' | 'spades'> = ['hearts', 'diamonds', 'clubs', 'spades'];
    const ranks = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];
    const newDeck: Card[] = [];

    for (let i = 0; i < deckCount; i++) {
      for (const suit of suits) {
        for (const rank of ranks) {
          let value = parseInt(rank);
          if (rank === 'A') value = 14;
          else if (rank === 'K') value = 13;
          else if (rank === 'Q') value = 12;
          else if (rank === 'J') value = 11;
          
          newDeck.push({ suit, rank, value });
        }
      }
    }

    // Shuffle
    for (let i = newDeck.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [newDeck[i], newDeck[j]] = [newDeck[j], newDeck[i]];
    }

    setDeck(newDeck);
  };

  const initializePlayers = () => {
    const initialPlayers: PlayerState[] = players.map(name => ({
      name,
      cards: [],
      visibleCards: [],
      folded: false,
      chips: 1000,
      currentBet: 0
    }));
    setPlayerStates(initialPlayers);
  };

  const startGame = () => {
    const newDeck = [...deck];
    const updatedPlayers = playerStates.map(player => {
      const cards: Card[] = [];
      for (let i = 0; i < 7; i++) {
        cards.push(newDeck.pop()!);
      }
      return {
        ...player,
        cards,
        visibleCards: cards.slice(2), // First 2 cards are hidden
        folded: false,
        currentBet: 0
      };
    });

    setDeck(newDeck);
    setPlayerStates(updatedPlayers);
    setGameStatus('dealing');
    setCurrentRound(1);
    setPot(0);
    setResult('');
  };

  const evaluateHand = (cards: Card[]): { rank: number; name: string } => {
    // Simplified hand evaluation
    const sortedCards = [...cards].sort((a, b) => b.value - a.value);
    const ranks = sortedCards.map(c => c.value);
    const suits = sortedCards.map(c => c.suit);

    const rankCounts = ranks.reduce((acc, rank) => {
      acc[rank] = (acc[rank] || 0) + 1;
      return acc;
    }, {} as Record<number, number>);

    const counts = Object.values(rankCounts).sort((a, b) => b - a);
    const isFlush = suits.every(suit => suit === suits[0]);
    const isStraight = ranks.every((rank, i) => i === 0 || ranks[i - 1] === rank + 1);

    if (isStraight && isFlush) return { rank: 9, name: '스트레이트 플러시' };
    if (counts[0] === 4) return { rank: 8, name: '포카드' };
    if (counts[0] === 3 && counts[1] === 2) return { rank: 7, name: '풀하우스' };
    if (isFlush) return { rank: 6, name: '플러시' };
    if (isStraight) return { rank: 5, name: '스트레이트' };
    if (counts[0] === 3) return { rank: 4, name: '트리플' };
    if (counts[0] === 2 && counts[1] === 2) return { rank: 3, name: '투페어' };
    if (counts[0] === 2) return { rank: 2, name: '원페어' };
    return { rank: 1, name: '하이카드' };
  };

  const bet = (amount: number) => {
    const playerIndex = playerStates.findIndex(p => p.name === username);
    if (playerIndex === -1) return;

    const player = playerStates[playerIndex];
    if (player.chips >= amount) {
      const updatedPlayers = [...playerStates];
      updatedPlayers[playerIndex] = {
        ...player,
        chips: player.chips - amount,
        currentBet: player.currentBet + amount
      };
      setPlayerStates(updatedPlayers);
      setPot(pot + amount);
    }
  };

  const fold = () => {
    const playerIndex = playerStates.findIndex(p => p.name === username);
    if (playerIndex === -1) return;

    const updatedPlayers = [...playerStates];
    updatedPlayers[playerIndex] = {
      ...updatedPlayers[playerIndex],
      folded: true
    };
    setPlayerStates(updatedPlayers);
  };

  const showdown = () => {
    const activePlayers = playerStates.filter(p => !p.folded);
    
    const evaluations = activePlayers.map(player => ({
      player,
      hand: evaluateHand(player.cards)
    }));

    evaluations.sort((a, b) => b.hand.rank - a.hand.rank);
    const winner = evaluations[0];

    setResult(`${winner.player.name} 승리! (${winner.hand.name})`);
    
    const updatedPlayers = playerStates.map(p => 
      p.name === winner.player.name 
        ? { ...p, chips: p.chips + pot }
        : p
    );
    setPlayerStates(updatedPlayers);
    setGameStatus('finished');
  };

  const newRound = () => {
    setGameStatus('betting');
    setResult('');
    setPot(0);
    setCurrentRound(0);
    const resetPlayers = playerStates.map(p => ({
      ...p,
      cards: [],
      visibleCards: [],
      folded: false,
      currentBet: 0
    }));
    setPlayerStates(resetPlayers);
  };

  const currentPlayer = playerStates.find(p => p.name === username);

  return (
    <div className="p-6 space-y-6">
      {/* Game Info */}
      <div className="flex justify-between items-center text-white">
        <div>
          <p className="text-sm text-white/70">팟</p>
          <p className="text-2xl text-amber-400">{pot}</p>
        </div>
        {currentPlayer && (
          <div>
            <p className="text-sm text-white/70">내 칩</p>
            <p className="text-2xl">{currentPlayer.chips}</p>
          </div>
        )}
      </div>

      {/* Other Players */}
      {gameStatus !== 'betting' && (
        <div className="space-y-4">
          {playerStates.filter(p => p.name !== username).map((player, index) => (
            <div key={index} className={`p-4 rounded-lg ${player.folded ? 'bg-gray-800/50' : 'bg-white/5'}`}>
              <div className="flex justify-between items-center mb-2">
                <h3 className="text-white">{player.name} {player.folded && '(폴드)'}</h3>
                <div className="text-white/70 text-sm">
                  칩: {player.chips} | 베팅: {player.currentBet}
                </div>
              </div>
              <div className="flex gap-2 flex-wrap">
                {player.visibleCards.map((card, idx) => (
                  <PlayingCard
                    key={idx}
                    suit={card.suit}
                    rank={card.rank}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Current Player's Hand */}
      {currentPlayer && gameStatus !== 'betting' && (
        <div className="space-y-2 p-4 bg-blue-900/30 rounded-lg">
          <div className="flex justify-between items-center">
            <h3 className="text-white text-xl">내 카드</h3>
            <div className="text-white/70 text-sm">
              베팅: {currentPlayer.currentBet}
            </div>
          </div>
          <div className="flex gap-2 flex-wrap justify-center">
            {currentPlayer.cards.map((card, index) => (
              <PlayingCard
                key={index}
                suit={card.suit}
                rank={card.rank}
                faceDown={index < 2}
              />
            ))}
          </div>
        </div>
      )}

      {/* Result Message */}
      {result && (
        <div className="text-center p-4 bg-white/10 rounded-lg">
          <p className="text-white text-xl">{result}</p>
        </div>
      )}

      {/* Actions */}
      <div className="flex gap-2 flex-wrap justify-center">
        {gameStatus === 'betting' && (
          <Button
            onClick={startGame}
            className="bg-green-600 hover:bg-green-700 text-lg px-8 py-6"
          >
            게임 시작
          </Button>
        )}

        {gameStatus === 'dealing' && currentPlayer && !currentPlayer.folded && (
          <>
            <Button
              onClick={() => bet(10)}
              disabled={currentPlayer.chips < 10}
              className="bg-amber-600 hover:bg-amber-700"
            >
              10 베팅
            </Button>
            <Button
              onClick={() => bet(50)}
              disabled={currentPlayer.chips < 50}
              className="bg-amber-600 hover:bg-amber-700"
            >
              50 베팅
            </Button>
            <Button
              onClick={() => bet(100)}
              disabled={currentPlayer.chips < 100}
              className="bg-amber-600 hover:bg-amber-700"
            >
              100 베팅
            </Button>
            <Button
              onClick={fold}
              className="bg-red-600 hover:bg-red-700"
            >
              폴드
            </Button>
            <Button
              onClick={showdown}
              className="bg-blue-600 hover:bg-blue-700"
            >
              쇼다운
            </Button>
          </>
        )}

        {gameStatus === 'finished' && (
          <Button
            onClick={newRound}
            className="bg-green-600 hover:bg-green-700"
          >
            새 라운드
          </Button>
        )}
      </div>
    </div>
  );
}
