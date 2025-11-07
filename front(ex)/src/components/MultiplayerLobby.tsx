import { useState } from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { Label } from './ui/label';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { ArrowLeft, Plus, Users } from 'lucide-react';
import type { Room, GameType } from '../App';

interface MultiplayerLobbyProps {
  username: string;
  onJoinRoom: (room: Room) => void;
  onBack: () => void;
}

const GAME_NAMES = {
  blackjack: '블랙잭',
  baccarat: '바카라',
  sevenpoker: '세븐포커'
};

export default function MultiplayerLobby({ username, onJoinRoom, onBack }: MultiplayerLobbyProps) {
  const [rooms, setRooms] = useState<Room[]>([
    {
      id: '1',
      name: '초보자 방',
      gameType: 'blackjack',
      deckCount: 1,
      players: ['Player1', 'Player2'],
      maxPlayers: 4,
      hostId: 'Player1'
    },
    {
      id: '2',
      name: '고수의 방',
      gameType: 'sevenpoker',
      deckCount: 2,
      players: ['ProGamer'],
      maxPlayers: 6,
      hostId: 'ProGamer'
    }
  ]);

  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [newRoomName, setNewRoomName] = useState('');
  const [selectedGame, setSelectedGame] = useState<GameType>('blackjack');
  const [deckCount, setDeckCount] = useState('1');
  const [maxPlayers, setMaxPlayers] = useState('4');

  const handleCreateRoom = () => {
    if (newRoomName.trim()) {
      const newRoom: Room = {
        id: Date.now().toString(),
        name: newRoomName.trim(),
        gameType: selectedGame,
        deckCount: parseInt(deckCount),
        players: [username],
        maxPlayers: parseInt(maxPlayers),
        hostId: username
      };
      setRooms([...rooms, newRoom]);
      setNewRoomName('');
      setIsCreateDialogOpen(false);
      onJoinRoom(newRoom);
    }
  };

  const handleJoinRoom = (room: Room) => {
    if (room.players.length < room.maxPlayers) {
      const updatedRoom = {
        ...room,
        players: [...room.players, username]
      };
      setRooms(rooms.map(r => r.id === room.id ? updatedRoom : r));
      onJoinRoom(updatedRoom);
    }
  };

  return (
    <div className="min-h-screen p-4">
      <div className="max-w-6xl mx-auto">
        <div className="mb-6 flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={onBack}
            className="text-white hover:bg-white/10"
          >
            <ArrowLeft className="w-5 h-5 mr-2" />
            메인 메뉴로
          </Button>

          <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
            <DialogTrigger asChild>
              <Button className="bg-amber-600 hover:bg-amber-700 text-white">
                <Plus className="w-5 h-5 mr-2" />
                방 만들기
              </Button>
            </DialogTrigger>
            <DialogContent className="bg-slate-900 border-slate-700 text-white">
              <DialogHeader>
                <DialogTitle className="text-white">새 방 만들기</DialogTitle>
                <DialogDescription className="text-slate-400">
                  게임 설정을 선택하고 방을 생성하세요
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="roomName" className="text-white">방 이름</Label>
                  <Input
                    id="roomName"
                    value={newRoomName}
                    onChange={(e) => setNewRoomName(e.target.value)}
                    placeholder="방 이름 입력"
                    className="bg-slate-800 border-slate-700 text-white"
                  />
                </div>

                <div>
                  <Label htmlFor="gameType" className="text-white">게임 선택</Label>
                  <Select value={selectedGame} onValueChange={(value) => setSelectedGame(value as GameType)}>
                    <SelectTrigger className="bg-slate-800 border-slate-700 text-white">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent className="bg-slate-800 border-slate-700">
                      <SelectItem value="blackjack" className="text-white">블랙잭</SelectItem>
                      <SelectItem value="baccarat" className="text-white">바카라</SelectItem>
                      <SelectItem value="sevenpoker" className="text-white">세븐포커</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div>
                  <Label htmlFor="deckCount" className="text-white">덱 개수</Label>
                  <Select value={deckCount} onValueChange={setDeckCount}>
                    <SelectTrigger className="bg-slate-800 border-slate-700 text-white">
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

                <div>
                  <Label htmlFor="maxPlayers" className="text-white">최대 인원</Label>
                  <Select value={maxPlayers} onValueChange={setMaxPlayers}>
                    <SelectTrigger className="bg-slate-800 border-slate-700 text-white">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent className="bg-slate-800 border-slate-700">
                      {[2, 3, 4, 5, 6, 7, 8].map(num => (
                        <SelectItem key={num} value={num.toString()} className="text-white">
                          {num}명
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <Button
                  onClick={handleCreateRoom}
                  className="w-full bg-amber-600 hover:bg-amber-700"
                  disabled={!newRoomName.trim()}
                >
                  방 만들기
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        <Card className="bg-white/10 backdrop-blur-sm border-white/20">
          <CardHeader>
            <CardTitle className="text-white">게임 로비</CardTitle>
            <CardDescription className="text-white/80">
              참여할 방을 선택하세요
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {rooms.length === 0 ? (
                <div className="text-center py-12 text-white/60">
                  아직 생성된 방이 없습니다
                </div>
              ) : (
                rooms.map(room => (
                  <div
                    key={room.id}
                    className="bg-white/5 rounded-lg p-4 flex items-center justify-between hover:bg-white/10 transition-colors"
                  >
                    <div className="flex-1">
                      <h3 className="text-white text-lg">{room.name}</h3>
                      <div className="flex gap-4 mt-1 text-sm text-white/70">
                        <span>{GAME_NAMES[room.gameType]}</span>
                        <span>·</span>
                        <span>{room.deckCount}덱</span>
                        <span>·</span>
                        <span className="flex items-center gap-1">
                          <Users className="w-4 h-4" />
                          {room.players.length}/{room.maxPlayers}
                        </span>
                      </div>
                    </div>
                    <Button
                      onClick={() => handleJoinRoom(room)}
                      disabled={room.players.length >= room.maxPlayers || room.players.includes(username)}
                      className="bg-green-600 hover:bg-green-700 disabled:opacity-50"
                    >
                      {room.players.includes(username) ? '참여 중' : room.players.length >= room.maxPlayers ? '인원 마감' : '입장'}
                    </Button>
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
