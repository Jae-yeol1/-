import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Users, User } from 'lucide-react';

interface MainMenuProps {
  username: string;
  onMultiplayer: () => void;
  onSolo: () => void;
}

export default function MainMenu({ username, onMultiplayer, onSolo }: MainMenuProps) {
  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Card className="w-full max-w-2xl bg-white/10 backdrop-blur-sm border-white/20">
        <CardHeader className="text-center">
          <CardTitle className="text-white text-3xl">환영합니다, {username}님!</CardTitle>
          <CardDescription className="text-white/80">
            플레이 모드를 선택하세요
          </CardDescription>
        </CardHeader>
        <CardContent className="grid md:grid-cols-2 gap-6">
          <button
            onClick={onMultiplayer}
            className="group relative overflow-hidden rounded-lg bg-gradient-to-br from-blue-600 to-blue-800 p-8 hover:from-blue-500 hover:to-blue-700 transition-all duration-300 transform hover:scale-105"
          >
            <div className="flex flex-col items-center gap-4">
              <Users className="w-20 h-20 text-white" />
              <h3 className="text-white text-2xl">멀티플레이</h3>
              <p className="text-white/80 text-center">
                다른 플레이어와 함께 게임을 즐기세요
              </p>
            </div>
          </button>

          <button
            onClick={onSolo}
            className="group relative overflow-hidden rounded-lg bg-gradient-to-br from-purple-600 to-purple-800 p-8 hover:from-purple-500 hover:to-purple-700 transition-all duration-300 transform hover:scale-105"
          >
            <div className="flex flex-col items-center gap-4">
              <User className="w-20 h-20 text-white" />
              <h3 className="text-white text-2xl">솔로 플레이</h3>
              <p className="text-white/80 text-center">
                혼자서 게임을 연습하세요
              </p>
            </div>
          </button>
        </CardContent>
      </Card>
    </div>
  );
}
