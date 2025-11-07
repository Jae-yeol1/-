import { Heart, Diamond, Club, Spade } from 'lucide-react';

interface PlayingCardProps {
  suit: 'hearts' | 'diamonds' | 'clubs' | 'spades';
  rank: string;
  faceDown?: boolean;
}

export default function PlayingCard({ suit, rank, faceDown = false }: PlayingCardProps) {
  const isRed = suit === 'hearts' || suit === 'diamonds';

  const SuitIcon = {
    hearts: Heart,
    diamonds: Diamond,
    clubs: Club,
    spades: Spade
  }[suit];

  if (faceDown) {
    return (
      <div className="w-16 h-24 bg-gradient-to-br from-blue-700 to-blue-900 rounded-lg border-2 border-blue-600 flex items-center justify-center shadow-lg">
        <div className="text-blue-400 text-xs transform rotate-45">
          <div className="grid grid-cols-3 gap-1">
            {Array.from({ length: 9 }).map((_, i) => (
              <div key={i} className="w-1 h-1 bg-blue-400 rounded-full" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="w-16 h-24 bg-white rounded-lg border-2 border-gray-300 flex flex-col items-center justify-between p-2 shadow-lg">
      <div className={`flex flex-col items-center ${isRed ? 'text-red-600' : 'text-gray-900'}`}>
        <span className="text-lg leading-none">{rank}</span>
        <SuitIcon className="w-4 h-4" fill="currentColor" />
      </div>
      <SuitIcon className={`w-8 h-8 ${isRed ? 'text-red-600' : 'text-gray-900'}`} fill="currentColor" />
      <div className={`flex flex-col items-center transform rotate-180 ${isRed ? 'text-red-600' : 'text-gray-900'}`}>
        <span className="text-lg leading-none">{rank}</span>
        <SuitIcon className="w-4 h-4" fill="currentColor" />
      </div>
    </div>
  );
}
