import { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import PlayingCard from './PlayingCard';

interface Card {
  suit: 'hearts' | 'diamonds' | 'clubs' | 'spades';
  rank: string;
  value: number;
}

interface BaccaratGameProps {
  username: string;
  deckCount: number;
  isSolo: boolean;
}

type BetType = 'player' | 'banker' | 'tie';

export default function BaccaratGame({ username, deckCount }: BaccaratGameProps) {
  const [deck, setDeck] = useState<Card[]>([]);
  const [playerHand, setPlayerHand] = useState<Card[]>([]);
  const [bankerHand, setBankerHand] = useState<Card[]>([]);
  const [gameStatus, setGameStatus] = useState<'betting' | 'dealing' | 'finished'>('betting');
  const [result, setResult] = useState<string>('');
  const [chips, setChips] = useState(1000);
  const [bet, setBet] = useState(0);
  const [betOn, setBetOn] = useState<BetType | null>(null);

  useEffect(() => {
    initializeDeck();
  }, [deckCount]);

  const initializeDeck = () => {
    const suits: Array<'hearts' | 'diamonds' | 'clubs' | 'spades'> = ['hearts', 'diamonds', 'clubs', 'spades'];
    const ranks = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];
    const newDeck: Card[] = [];

    for (let i = 0; i < deckCount; i++) {
      for (const suit of suits) {
        for (const rank of ranks) {
          let value = parseInt(rank);
          if (rank === 'A') value = 1;
          else if (['J', 'Q', 'K', '10'].includes(rank)) value = 0;
          
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

  const calculateTotal = (hand: Card[]) => {
    const total = hand.reduce((sum, card) => sum + card.value, 0);
    return total % 10;
  };

  const placeBet = (amount: number, betType: BetType) => {
    if (amount <= chips) {
      setBet(amount);
      setBetOn(betType);
      setChips(chips - amount);
      dealCards();
    }
  };

  const dealCards = () => {
    const newDeck = [...deck];
    const newPlayerHand = [newDeck.pop()!, newDeck.pop()!];
    const newBankerHand = [newDeck.pop()!, newDeck.pop()!];

    setDeck(newDeck);
    setPlayerHand(newPlayerHand);
    setBankerHand(newBankerHand);
    setGameStatus('dealing');

    setTimeout(() => {
      checkNatural(newPlayerHand, newBankerHand, newDeck);
    }, 1500);
  };

  const checkNatural = (pHand: Card[], bHand: Card[], currentDeck: Card[]) => {
    const pTotal = calculateTotal(pHand);
    const bTotal = calculateTotal(bHand);

    if (pTotal >= 8 || bTotal >= 8) {
      endGame(pHand, bHand);
    } else {
      drawThirdCards(pHand, bHand, currentDeck);
    }
  };

  const drawThirdCards = (pHand: Card[], bHand: Card[], currentDeck: Card[]) => {
    let newPlayerHand = [...pHand];
    let newBankerHand = [...bHand];
    let newDeck = [...currentDeck];

    const playerTotal = calculateTotal(newPlayerHand);
    const bankerTotal = calculateTotal(newBankerHand);

    let playerThirdCard: Card | null = null;

    // Player draws third card
    if (playerTotal <= 5) {
      playerThirdCard = newDeck.pop()!;
      newPlayerHand.push(playerThirdCard);
      setPlayerHand(newPlayerHand);
    }

    setTimeout(() => {
      // Banker draws based on rules
      if (playerThirdCard === null) {
        if (bankerTotal <= 5) {
          newBankerHand.push(newDeck.pop()!);
        }
      } else {
        const thirdValue = playerThirdCard.value;
        if (
          (bankerTotal <= 2) ||
          (bankerTotal === 3 && thirdValue !== 8) ||
          (bankerTotal === 4 && thirdValue >= 2 && thirdValue <= 7) ||
          (bankerTotal === 5 && thirdValue >= 4 && thirdValue <= 7) ||
          (bankerTotal === 6 && (thirdValue === 6 || thirdValue === 7))
        ) {
          newBankerHand.push(newDeck.pop()!);
        }
      }

      setBankerHand(newBankerHand);
      setDeck(newDeck);

      setTimeout(() => {
        endGame(newPlayerHand, newBankerHand);
      }, 1000);
    }, 1000);
  };

  const endGame = (finalPlayerHand: Card[], finalBankerHand: Card[]) => {
    const playerTotal = calculateTotal(finalPlayerHand);
    const bankerTotal = calculateTotal(finalBankerHand);

    setGameStatus('finished');

    let resultText = '';
    let winnings = 0;

    if (playerTotal > bankerTotal) {
      resultText = '플레이어 승리!';
      if (betOn === 'player') {
        winnings = bet * 2;
        setChips(chips + winnings);
      }
    } else if (bankerTotal > playerTotal) {
      resultText = '뱅커 승리!';
      if (betOn === 'banker') {
        winnings = Math.floor(bet * 1.95); // 5% commission
        setChips(chips + bet + winnings);
      }
    } else {
      resultText = '무승부!';
      if (betOn === 'tie') {
        winnings = bet * 8;
        setChips(chips + winnings);
      } else {
        setChips(chips + bet);
      }
    }

    setResult(resultText);
  };

  const newRound = () => {
    setPlayerHand([]);
    setBankerHand([]);
    setGameStatus('betting');
    setResult('');
    setBet(0);
    setBetOn(null);
  };

  const playerTotal = calculateTotal(playerHand);
  const bankerTotal = calculateTotal(bankerHand);

  return (
    <div className="p-6 space-y-6">
      {/* Chips Display */}
      <div className="flex justify-between items-center text-white">
        <div>
          <p className="text-sm text-white/70">보유 칩</p>
          <p className="text-2xl">{chips}</p>
        </div>
        {bet > 0 && betOn && (
          <div>
            <p className="text-sm text-white/70">베팅</p>
            <p className="text-2xl text-amber-400">
              {betOn === 'player' ? '플레이어' : betOn === 'banker' ? '뱅커' : '타이'} - {bet}
            </p>
          </div>
        )}
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        {/* Player Hand */}
        <div className="space-y-2 p-4 bg-blue-900/30 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-white text-xl">플레이어</h3>
            <span className="text-white text-2xl">{playerHand.length > 0 ? playerTotal : '-'}</span>
          </div>
          <div className="flex gap-2 flex-wrap justify-center min-h-32 items-center">
            {playerHand.map((card, index) => (
              <PlayingCard
                key={index}
                suit={card.suit}
                rank={card.rank}
              />
            ))}
          </div>
        </div>

        {/* Banker Hand */}
        <div className="space-y-2 p-4 bg-red-900/30 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-white text-xl">뱅커</h3>
            <span className="text-white text-2xl">{bankerHand.length > 0 ? bankerTotal : '-'}</span>
          </div>
          <div className="flex gap-2 flex-wrap justify-center min-h-32 items-center">
            {bankerHand.map((card, index) => (
              <PlayingCard
                key={index}
                suit={card.suit}
                rank={card.rank}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Result Message */}
      {result && (
        <div className="text-center p-4 bg-white/10 rounded-lg">
          <p className="text-white text-xl">{result}</p>
        </div>
      )}

      {/* Betting Actions */}
      {gameStatus === 'betting' && (
        <div className="space-y-4">
          <p className="text-white text-center">베팅할 대상과 금액을 선택하세요</p>
          <div className="grid grid-cols-3 gap-4">
            <div className="space-y-2">
              <p className="text-white text-center">플레이어 (1:1)</p>
              <div className="flex flex-col gap-2">
                <Button onClick={() => placeBet(10, 'player')} disabled={chips < 10} className="bg-blue-600 hover:bg-blue-700">
                  10
                </Button>
                <Button onClick={() => placeBet(50, 'player')} disabled={chips < 50} className="bg-blue-600 hover:bg-blue-700">
                  50
                </Button>
                <Button onClick={() => placeBet(100, 'player')} disabled={chips < 100} className="bg-blue-600 hover:bg-blue-700">
                  100
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <p className="text-white text-center">뱅커 (0.95:1)</p>
              <div className="flex flex-col gap-2">
                <Button onClick={() => placeBet(10, 'banker')} disabled={chips < 10} className="bg-red-600 hover:bg-red-700">
                  10
                </Button>
                <Button onClick={() => placeBet(50, 'banker')} disabled={chips < 50} className="bg-red-600 hover:bg-red-700">
                  50
                </Button>
                <Button onClick={() => placeBet(100, 'banker')} disabled={chips < 100} className="bg-red-600 hover:bg-red-700">
                  100
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <p className="text-white text-center">타이 (8:1)</p>
              <div className="flex flex-col gap-2">
                <Button onClick={() => placeBet(10, 'tie')} disabled={chips < 10} className="bg-green-600 hover:bg-green-700">
                  10
                </Button>
                <Button onClick={() => placeBet(50, 'tie')} disabled={chips < 50} className="bg-green-600 hover:bg-green-700">
                  50
                </Button>
                <Button onClick={() => placeBet(100, 'tie')} disabled={chips < 100} className="bg-green-600 hover:bg-green-700">
                  100
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}

      {gameStatus === 'dealing' && (
        <div className="text-center text-white text-lg">
          카드를 나누는 중...
        </div>
      )}

      {gameStatus === 'finished' && (
        <Button
          onClick={newRound}
          className="w-full bg-amber-600 hover:bg-amber-700"
        >
          새 라운드
        </Button>
      )}
    </div>
  );
}
