import { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Spade, Heart, Diamond, Club } from 'lucide-react';

interface LoginPageProps {
  onLogin: (username: string) => void;
}

export default function LoginPage({ onLogin }: LoginPageProps) {
  const [username, setUsername] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (username.trim()) {
      onLogin(username.trim());
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Card className="w-full max-w-md bg-white/10 backdrop-blur-sm border-white/20">
        <CardHeader className="text-center">
          <div className="flex justify-center gap-4 mb-4">
            <Spade className="w-12 h-12 text-white" />
            <Heart className="w-12 h-12 text-red-500" />
            <Diamond className="w-12 h-12 text-red-500" />
            <Club className="w-12 h-12 text-white" />
          </div>
          <CardTitle className="text-white text-3xl">온라인 카드게임</CardTitle>
          <CardDescription className="text-white/80">
            아이디를 입력하여 로그인하세요
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              type="text"
              placeholder="아이디 입력"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="bg-white/20 border-white/30 text-white placeholder:text-white/50"
              autoFocus
            />
            <Button 
              type="submit" 
              className="w-full bg-amber-600 hover:bg-amber-700 text-white"
              disabled={!username.trim()}
            >
              로그인
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
