import { useState } from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { ArrowLeft, Play } from 'lucide-react';
import type { GameType } from '../App';

interface SoloGameSelectProps {
  onStartGame: (gameType: GameType, deckCount: number) => void;
  onBack: () => void;
}

const GAME_INFO = {
  blackjack: {
    name: '블랙잭',
    description: '딜러와 1:1로 21에 가까운 숫자를 만드는 게임',
    icon: '🃏'
  },
  baccarat: {
    name: '바카라',
    description: '플레이어와 뱅커 중 9에 가까운 쪽을 예측하는 게임',
    icon: '🎴'
  },
  sevenpoker: {
    name: '세븐포커',
    description: '7장의 카드로 가장 높은 족보를 만드는 게임',
    icon: '🎰'
  }
};

export default function SoloGameSelect({ onStartGame, onBack }: SoloGameSelectProps) {
  const [selectedGame, setSelectedGame] = useState<GameType>('blackjack');
  const [deckCount, setDeckCount] = useState('1');

  const handleStartGame = () => {
    onStartGame(selectedGame, parseInt(deckCount));
  };

  return (
    <div className="min-h-screen p-4">
      <div className="max-w-4xl mx-auto">
        <div className="mb-6">
          <Button
            variant="ghost"
            onClick={onBack}
            className="text-white hover:bg-white/10"
          >
            <ArrowLeft className="w-5 h-5 mr-2" />
            메인 메뉴로
          </Button>
        </div>

        <Card className="bg-white/10 backdrop-blur-sm border-white/20">
          <CardHeader>
            <CardTitle className="text-white text-2xl">솔로 플레이</CardTitle>
            <CardDescription className="text-white/80">
              게임과 덱 개수를 선택하세요
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* 게임 선택 */}
            <div className="space-y-3">
              <Label className="text-white">게임 선택</Label>
              <div className="grid gap-3">
                {(Object.keys(GAME_INFO) as GameType[]).map((gameType) => {
                  const game = GAME_INFO[gameType];
                  return (
                    <button
                      key={gameType}
                      onClick={() => setSelectedGame(gameType)}
                      className={`p-4 rounded-lg border-2 transition-all text-left ${
                        selectedGame === gameType
                          ? 'bg-amber-600/30 border-amber-500'
                          : 'bg-white/5 border-white/20 hover:bg-white/10'
                      }`}
                    >
                      <div className="flex items-center gap-4">
                        <span className="text-4xl">{game.icon}</span>
                        <div className="flex-1">
                          <h3 className="text-white text-lg">{game.name}</h3>
                          <p className="text-white/70 text-sm">{game.description}</p>
                        </div>
                        {selectedGame === gameType && (
                          <div className="w-6 h-6 rounded-full bg-amber-500 flex items-center justify-center">
                            <div className="w-3 h-3 rounded-full bg-white" />
                          </div>
                        )}
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* 덱 개수 선택 */}
            <div className="space-y-3">
              <Label htmlFor="deckCount" className="text-white">덱 개수</Label>
              <Select value={deckCount} onValueChange={setDeckCount}>
                <SelectTrigger className="bg-white/10 border-white/30 text-white">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="bg-slate-800 border-slate-700">
                  {[1, 2, 3, 4, 5, 6, 8].map(num => (
                    <SelectItem key={num} value={num.toString()} className="text-white">
                      {num}덱
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 시작 버튼 */}
            <Button
              onClick={handleStartGame}
              className="w-full bg-green-600 hover:bg-green-700 text-white py-6 text-lg"
            >
              <Play className="w-5 h-5 mr-2" />
              게임 시작
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
