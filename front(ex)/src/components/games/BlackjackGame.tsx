import { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import { Card as UICard } from '../ui/card';
import PlayingCard from './PlayingCard';

interface Card {
  suit: 'hearts' | 'diamonds' | 'clubs' | 'spades';
  rank: string;
  value: number;
}

interface BlackjackGameProps {
  username: string;
  deckCount: number;
  isSolo: boolean;
}

export default function BlackjackGame({ username, deckCount }: BlackjackGameProps) {
  const [deck, setDeck] = useState<Card[]>([]);
  const [playerHand, setPlayerHand] = useState<Card[]>([]);
  const [dealerHand, setDealerHand] = useState<Card[]>([]);
  const [gameStatus, setGameStatus] = useState<'betting' | 'playing' | 'dealer' | 'finished'>('betting');
  const [result, setResult] = useState<string>('');
  const [showDealerCard, setShowDealerCard] = useState(false);
  const [chips, setChips] = useState(1000);
  const [bet, setBet] = useState(0);

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
          if (rank === 'A') value = 11;
          else if (['J', 'Q', 'K'].includes(rank)) value = 10;
          
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
    let total = hand.reduce((sum, card) => sum + card.value, 0);
    let aces = hand.filter(card => card.rank === 'A').length;

    while (total > 21 && aces > 0) {
      total -= 10;
      aces--;
    }

    return total;
  };

  const placeBet = (amount: number) => {
    if (amount <= chips) {
      setBet(amount);
      setChips(chips - amount);
      dealInitialCards();
    }
  };

  const dealInitialCards = () => {
    const newDeck = [...deck];
    const newPlayerHand = [newDeck.pop()!, newDeck.pop()!];
    const newDealerHand = [newDeck.pop()!, newDeck.pop()!];

    setDeck(newDeck);
    setPlayerHand(newPlayerHand);
    setDealerHand(newDealerHand);
    setGameStatus('playing');
    setShowDealerCard(false);
    setResult('');
  };

  const hit = () => {
    const newDeck = [...deck];
    const newCard = newDeck.pop()!;
    const newPlayerHand = [...playerHand, newCard];
    
    setDeck(newDeck);
    setPlayerHand(newPlayerHand);

    if (calculateTotal(newPlayerHand) > 21) {
      endGame(newPlayerHand, dealerHand);
    }
  };

  const stand = () => {
    setGameStatus('dealer');
    setShowDealerCard(true);
    dealerPlay();
  };

  const dealerPlay = () => {
    let newDeck = [...deck];
    let newDealerHand = [...dealerHand];

    while (calculateTotal(newDealerHand) < 17) {
      const newCard = newDeck.pop()!;
      newDealerHand.push(newCard);
    }

    setDeck(newDeck);
    setDealerHand(newDealerHand);
    setShowDealerCard(true);
    
    setTimeout(() => {
      endGame(playerHand, newDealerHand);
    }, 1000);
  };

  const endGame = (finalPlayerHand: Card[], finalDealerHand: Card[]) => {
    const playerTotal = calculateTotal(finalPlayerHand);
    const dealerTotal = calculateTotal(finalDealerHand);

    setShowDealerCard(true);
    setGameStatus('finished');

    if (playerTotal > 21) {
      setResult('버스트! 패배했습니다');
    } else if (dealerTotal > 21) {
      setResult('딜러 버스트! 승리했습니다');
      setChips(chips + bet * 2);
    } else if (playerTotal > dealerTotal) {
      setResult('승리했습니다!');
      setChips(chips + bet * 2);
    } else if (playerTotal < dealerTotal) {
      setResult('패배했습니다');
    } else {
      setResult('무승부');
      setChips(chips + bet);
    }
  };

  const newRound = () => {
    setPlayerHand([]);
    setDealerHand([]);
    setGameStatus('betting');
    setResult('');
    setBet(0);
    setShowDealerCard(false);
  };

  const playerTotal = calculateTotal(playerHand);
  const dealerTotal = calculateTotal(dealerHand);

  return (
    <div className="p-6 space-y-6">
      {/* Chips Display */}
      <div className="flex justify-between items-center text-white">
        <div>
          <p className="text-sm text-white/70">보유 칩</p>
          <p className="text-2xl">{chips}</p>
        </div>
        {bet > 0 && (
          <div>
            <p className="text-sm text-white/70">베팅 금액</p>
            <p className="text-2xl text-amber-400">{bet}</p>
          </div>
        )}
      </div>

      {/* Dealer Hand */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-white">딜러</h3>
          <span className="text-white/70">
            {showDealerCard ? dealerTotal : '?'}
          </span>
        </div>
        <div className="flex gap-2 flex-wrap">
          {dealerHand.map((card, index) => (
            <PlayingCard
              key={index}
              suit={card.suit}
              rank={card.rank}
              faceDown={index === 1 && !showDealerCard}
            />
          ))}
        </div>
      </div>

      {/* Player Hand */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-white">{username}</h3>
          <span className="text-white/70">{playerTotal}</span>
        </div>
        <div className="flex gap-2 flex-wrap">
          {playerHand.map((card, index) => (
            <PlayingCard
              key={index}
              suit={card.suit}
              rank={card.rank}
            />
          ))}
        </div>
      </div>

      {/* Result Message */}
      {result && (
        <div className="text-center p-4 bg-white/10 rounded-lg">
          <p className="text-white text-xl">{result}</p>
        </div>
      )}

      {/* Actions */}
      <div className="flex gap-2 flex-wrap justify-center">
        {gameStatus === 'betting' && (
          <>
            <Button
              onClick={() => placeBet(10)}
              disabled={chips < 10}
              className="bg-amber-600 hover:bg-amber-700"
            >
              10 베팅
            </Button>
            <Button
              onClick={() => placeBet(50)}
              disabled={chips < 50}
              className="bg-amber-600 hover:bg-amber-700"
            >
              50 베팅
            </Button>
            <Button
              onClick={() => placeBet(100)}
              disabled={chips < 100}
              className="bg-amber-600 hover:bg-amber-700"
            >
              100 베팅
            </Button>
            <Button
              onClick={() => placeBet(chips)}
              disabled={chips === 0}
              className="bg-red-600 hover:bg-red-700"
            >
              올인
            </Button>
          </>
        )}

        {gameStatus === 'playing' && (
          <>
            <Button
              onClick={hit}
              className="bg-green-600 hover:bg-green-700"
            >
              히트
            </Button>
            <Button
              onClick={stand}
              className="bg-blue-600 hover:bg-blue-700"
            >
              스탠드
            </Button>
          </>
        )}

        {gameStatus === 'finished' && (
          <Button
            onClick={newRound}
            className="bg-amber-600 hover:bg-amber-700"
          >
            새 라운드
          </Button>
        )}
      </div>
    </div>
  );
}
