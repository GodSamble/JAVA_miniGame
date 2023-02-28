package game_Frame;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

class game_Frame extends JFrame implements KeyListener, Runnable{
 
int f_width ;
int f_height ;
 
int x, y;

int[] cx ={0, 0, 0}; // 배경 스크롤 속도 제어용 변수 ->구글링 통해 
int bx = 0; // 전체 배경 스크롤 용 변수 ->구글링 통해

boolean KeyUp = false;
boolean KeyDown = false;
boolean KeyLeft = false;
boolean KeyRight = false;
boolean KeySpace = false;

int cnt;

int player_Speed; // 유저의 캐릭터가 움직이는 속도를 조절할 변수
int missile_Speed; // 공격 날라가는 속도 조절할 변수
int fire_Speed; // 공격 연사 속도 조절할 변수
int enemy_speed; // 적(우측에서 날라오는 표창)의 속도 조절하는 변수
int player_Status = 0;//★★★★★★★★★★★★★★★★★★★★★★★//★★★★★★★★★★★★★★★★★★★★★★★
// 유저 캐릭터 상태 체크 변수 0 : 평상시 가만히 있을 때 1: 공격 날리는 중 2: 공격이 사물과 충돌 시
int game_Score; // 게임 점수 계산
int player_Hitpoint; // 플레이어 캐릭터의 체력

Thread th; // ->노션에 스레드 정리해놓음
Toolkit tk = Toolkit.getDefaultToolkit(); // ->노션에 툴킷 관련 궁금증 해결 정리 해놓음

Image[] Player_img;
//플레이어 애니메이션 표현을 위해 이미지를 배열로 받음
Image BackGround_img; //배경화면 이미지
Image[] Cloud_img; //움직이는 배경용 이미지배열//★★★★★★★★★★★★★★★★★★★★★★★//★★★★★★★★★★★★★★★★★★★★★★★
Image[] Explo_img; //폭발이펙트용 이미지배열//★★★★★★★★★★★★★★★★★★★★★★★//★★★★★★★★★★★★★★★★★★★★★★★

Image Missile_img;
Image Enemy_img;
 
ArrayList Missile_List = new ArrayList();
ArrayList Enemy_List = new ArrayList();
ArrayList Explosion_List = new ArrayList();//★★★★★★★★★★★★★★★★★★★★★★★//★★★★★★★★★★★★★★★★★★★★★★★
//다수의 폭발 이펙트를 처리하기 위한 배열

Image buffImage;
Graphics buffg;

Missile ms;
Enemy en;

Explosion ex; //폭발 이펙트용 클래스 접근 키 //★★★★★★★★★★★★★★★★★★★★★★★//★★★★★★★★★★★★★★★★★★★★★★★

game_Frame(){
 init();
 start();
 
 setTitle("슈팅 게임 만들기");
 setSize(f_width, f_height);
 
 Dimension screen = tk.getScreenSize();

 int f_xpos = (int)(screen.getWidth() / 2 - f_width / 2);
 int f_ypos = (int)(screen.getHeight() / 2 - f_height / 2);

 setLocation(f_xpos, f_ypos);
 setResizable(false);
 setVisible(true);
}
public void init(){
x = 250;
y = 600;
f_width = 600;
f_height = 800;

Missile_img = tk.getImage(getClass().getResource("Missile.png"));
Enemy_img = tk.getImage(getClass().getResource("AttackMotion2.png"));
//이미지 만드는 방식을 ImageIcon으로 변경.

Player_img = new Image[5];
for(int i = 0 ; i < Player_img.length ; ++i){
//Player_img[i] =

Player_img[0]=tk.getImage(getClass().getResource("player.png"));
Player_img[1]=tk.getImage(getClass().getResource("player.png"));//★★★★★★★★★★★★★★★★★★★★★★★
Player_img[2]=tk.getImage(getClass().getResource("player.png"));//★★★★★★★★★★★★★★★★★★★★★★★
Player_img[3]=tk.getImage(getClass().getResource("player.png"));//★★★★★★★★★★★★★★★★★★★★★★★
Player_img[4]=tk.getImage(getClass().getResource("player.png"));//★★★★★★★★★★★★★★★★★★★★★★★
}
//플레이어 애니메이션을 코드작성 시에는 4면을 다방면에서 보여주려 했으나 png 유료라서 무료인 정면만 사용

BackGround_img = tk.getImage(getClass().getResource("background4.png"));
//배경화면 이미지

Cloud_img = new Image[3];//★★★★★★★★★★★★★★★★★★★★★★★
for(int i = 0 ; i <Cloud_img.length ; ++i){ 
Cloud_img[i] =//★★★★★★★★★★★★★★★★★★★★★★★
new ImageIcon("cloud_" + i + ".png").getImage(); //★★★★★★★★★★★★★★★★★★★★★★★
}//★★★★★★★★★★★★★★★★★★★★★★★
//구름을 3개 동시에 그리는데 편의상 배열로 3개를 동시에 받는다.----------------------------------------------------------------

Explo_img = new Image[3];//★★★★★★★★★★★★★★★★★★★★★★★
for (int i = 0; i < Explo_img.length ; ++i ){//★★★★★★★★★★★★★★★★★★★★★★★
Explo_img[i] =//★★★★★★★★★★★★★★★★★★★★★★★
new ImageIcon("explo_" + i + ".png").getImage();//★★★★★★★★★★★★★★★★★★★★★★★
}//★★★★★★★★★★★★★★★★★★★★★★★
//폭발 애니메이션 표현을 위해
//파일이름을 넘버마다 나눠 배열로 담는다.
//모든 이미지는 Swing의 ImageIcon으로 받아 이미지 넓이,높이 // 값을 바로 얻을 수 있게 한다.

game_Score = 0;//게임 스코어 초기화
player_Hitpoint = 3;//최초 플레이어 체력
 
player_Speed = 5; //유저 캐릭터 움직이는 속도 설정
missile_Speed = 11; //미사일 움직임 속도 설정
fire_Speed = 15; //미사일 연사 속도 설정
enemy_speed = 7;//적이 날라오는 속도 설정

}
public void start(){
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
addKeyListener(this);

th = new Thread(this);
th.start();

}

public void run(){
try{
while(true){
KeyProcess();
EnemyProcess();
MissileProcess();

ExplosionProcess();//폭파처리 메소드 실행//★★★★★★★★★★★★★★★★★★★★★★★

repaint();

Thread.sleep(20);
cnt ++;
}
}catch (Exception e){}
}

public void MissileProcess(){
if ( KeySpace ){
player_Status = 1;//★★★★★★★★★★★★★★★★★★★★★★★
//미사일을 발사하면 플레이어 캐릭터 상태를 1로 변경.

if( ( cnt % fire_Speed ) == 0){
//플레이어의 미사일 연사속도를 조절한다.

ms = new Missile(x+75, y+40, missile_Speed);
//미사일 이동 속도 값을 추가로 받는다
Missile_List.add(ms);
}
}

for ( int i = 0 ; i < Missile_List.size() ; ++i){
ms = (Missile) Missile_List.get(i);
ms.move();
if ( ms.x > f_width - 20 ){
Missile_List.remove(i);
}

for (int j = 0 ; j < Enemy_List.size(); ++ j){
en = (Enemy) Enemy_List.get(j);

if (Crash(ms.x, ms.y, en.x, en.y, Missile_img, Enemy_img)) {
//기본공격(총알)의 좌표 및 이미지파일,
//적의 좌표및 이미지 파일을 받아
//충돌판정 메소드로 넘기고 true,false값을
//리턴 받아 true면 아래를 실행

Missile_List.remove(i);
Enemy_List.remove(j);

game_Score += 10; //게임 점수를 +10점.

ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2 , 0); //★★★★★★★
//적이 위치해있는 곳의 중심 좌표 x,y 값과
//폭발 설정을 받은 값 ( 0 또는 1 )을 받습니다.
//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격

Explosion_List.add(ex);//★★★★★★★★★★★★★★★★★★★★★★★
//충돌판정으로 사라진 적의 위치에
//이펙트를 추가한다.

}
}
}
}

public void EnemyProcess(){

for (int i = 0 ; i < Enemy_List.size() ; ++i ){
en = (Enemy)(Enemy_List.get(i));
en.move();
if(en.x < -200){
Enemy_List.remove(i);
}

if(Crash(x, y, en.x, en.y, Player_img[0], Enemy_img)){
//플레이어와 적의 충돌을 판정하여
//boolean값을 리턴 받아 true면 아래를 실행합니다.

player_Hitpoint --; //플레이어 체력을 1깍습니다.
Enemy_List.remove(i); //적을 제거합니다.
game_Score += 10;
//제거된 적으로 게임스코어를 10 증가시킵니다.

ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2, 0 );//★★★★★★★★★★
//적이 위치해있는 곳의 중심 좌표 x,y 값과
//폭발 설정을 받은 값 ( 0 또는 1 )을 받습니다.
//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격

Explosion_List.add(ex);//★★★★★★★★★★★★★★★★★★★★★★★ enemy 좌표에 폭발이펙트 얹는 것.
//제거된 적위치에 폭발 이펙트를 추가합니다.

ex = new Explosion(x, y, 1 );//★★★★★★★★★★★★★★★★★★★★★★★
//적이 위치해있는 곳의 중심 좌표 x,y 값과
//폭발 설정을 받은 값 ( 0 또는 1 )을 받습니다.
//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격

Explosion_List.add(ex);
//충돌시 플레이어의 위치에 충돌용 이펙트를 추가.

}
}
if ( cnt % 200 == 0 ){
en = new Enemy(f_width + 110 , 100, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width +220 , 200, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 330 , 300, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 440 , 400, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 550 , 500, enemy_speed);
Enemy_List.add(en);
//적 움직임 속도를 추가로 받아 적을 생성한다.

}
}

 public void ExplosionProcess(){//★★★★★★
  // 폭발 이펙트 처리용 메소드
 
  for (int i = 0 ;  i < Explosion_List.size(); ++i){//★★★★★★★★★★★★★★★★★★★★★★★
   ex = (Explosion) Explosion_List.get(i);//★★★★★★★★★★★★★★★★★★★★★★★
   ex.effect();//★★★★★★★★★★★★★★★★★★★★★★★
   //이펙트 애니메이션을 나타내기위해
   //이펙트 처리 추가가 발생하면 해당 메소드를 호출.
   
  }
 }


public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2){
//기존 충돌 판정 소스를 변경합니다.
//이제 이미지 변수를 바로 받아 해당 이미지의 넓이, 높이값을
//바로 계산합니다.

boolean check = false;

if ( Math.abs( ( x1 + img1.getWidth(null) / 2 )  
- ( x2 + img2.getWidth(null) / 2 ))  
< ( img2.getWidth(null) / 2 + img1.getWidth(null) / 2 )
 && Math.abs( ( y1 + img1.getHeight(null) / 2 )  
- ( y2 + img2.getHeight(null) / 2 ))  
< ( img2.getHeight(null)/2 + img1.getHeight(null)/2 ) ){
//이미지 넓이, 높이값을 바로 받아 계산합니다.


check = true;//위 값이 true면 check에 true를 전달합니다.
}else{ check = false;}

return check; //check의 값을 메소드에 리턴 시킵니다.

 }


public void paint(Graphics g){
buffImage = createImage(f_width, f_height);
buffg = buffImage.getGraphics();

update(g);
}

public void update(Graphics g){

Draw_Background(); //배경 이미지 그리기 메소드 실행
Draw_Player(); //플레이어를 그리는 메소드 이름 변경

Draw_Enemy();
Draw_Missile();

Draw_Explosion();//폭발이펙트그리기 메소드 실행//★★★★★★★★★★★★★★★★★★★★★★★
Draw_StatusText();//상태 표시 텍스트를 그리는 메소드 실행//★★★★★★★★★★★★★★★★★★★★★★★

g.drawImage(buffImage, 0, 0, this);
}
//----------------------------------------------------------------------------------------------------------------
public void Draw_Background(){//★★★★★★★★★★★★★★★★★★★★★★★
//배경 이미지를 그리는 부분입니다.

buffg.clearRect(0, 0, f_width, f_height);//★★★★★★★★★★★★★★★★★★★★★★★
//화면 지우기 명령은 이제 여기서 실행합니다.

if ( bx > -3500){//★★★★★★★★★★★★★★★★★★★★★★★
//기본 값이 0인 bx가 -3500 보다 크면 실행

buffg.drawImage(BackGround_img, bx, 0, this);//★★★★★★★★★★★★★★★★★★★★★★★
//bx -= 1;
//bx를 0에서 -1만큼 계속 줄이므로 배경이미지의 x좌표는
//계속 좌측으로 이동한다. 그러므로 전체 배경은 천천히
//좌측으로 움직이게 된다.
   
}else { bx = 0; }//★★★★★★★★★★★★★★★★★★★★★★★


for (int i = 0;  i < cx.length ; ++i){//★★★★★★★★★★★★★★★★★★★★★★★

if ( cx[i] < 1400){//★★★★★★★★★★★★★★★★★★★★★★★
cx[i] += 5 + i * 3 ;//★★★★★★★★★★★★★★★★★★★★★★★
} else { cx[i] = 0; }//★★★★★★★★★★★★★★★★★★★★★★★

buffg.drawImage(Cloud_img[i], 1200 - cx[i], 50+i*200,  this);//★★★★★★★★★★★★★★★★★★★★★★★
//3개의 구름 이미지를 각기 다른 속도 값으로 좌측으로 움직임.
}
}//------------------------------------

public void Draw_Player(){//★★★★여기('상태에 따른 드로우') 코드 수정할 것 

switch (player_Status){

case 0 : // 평상시
if((cnt / 5 %2) == 0){
buffg.drawImage(Player_img[1], x, y, this);
}else { buffg.drawImage(Player_img[2], x, y, this); }
// 피터팬 공격모션을 취하는 애니메이션 넣을 예산이었음 애니메이션

break;

case 1 : // 미사일발사
if((cnt / 5 % 2) == 0){
buffg.drawImage(Player_img[3], x, y, this);
}else { buffg.drawImage(Player_img[4], x, y, this); }
// 피터팬이 공격을 행하는 애니메이션 넣을 예산 했었음.

player_Status = 0;
//  공격이 끝나면 플레이어 상태를 0으로 돌린다.

break;

case 2 : // 충돌
break;

}

}

public void Draw_Missile(){
for (int i = 0 ; i < Missile_List.size()  ; ++i){
ms = (Missile) (Missile_List.get(i));
buffg.drawImage(Missile_img, ms.x, ms.y, this);
}
}

public void Draw_Enemy(){
for (int i = 0 ; i < Enemy_List.size() ; ++i ){
en = (Enemy)(Enemy_List.get(i));
buffg.drawImage(Enemy_img, en.x, en.y, this);
}
}

public void Draw_Explosion(){//★★★★★★★★★★★★★★★★★★★★★★★★여기 폭발모션정의 부분인데 사용시 대비하여 일단 냅둠
//폭발 이펙트를 그리는 부분 입니다.

for (int i = 0 ; i < Explosion_List.size() ; ++i ){
ex = (Explosion)Explosion_List.get(i);
//폭발 이펙트의 존재 유무를 체크하여 리스트를 받음.

if (ex.damage == 0){
// 설정값이 0 이면 폭발용 이미지 그리기

if ( ex.ex_cnt < 7  ) {
buffg.drawImage( Explo_img[0], ex.x -
Explo_img[0].getWidth(null) / 2, ex.y -
Explo_img[0].getHeight(null) / 2, this);
}else if ( ex.ex_cnt < 14 ) {
buffg.drawImage(Explo_img[1], ex.x -
Explo_img[1].getWidth(null) / 2, ex.y -
Explo_img[1].getHeight(null) / 2, this);
}else if ( ex.ex_cnt < 21 ) {
buffg.drawImage(Explo_img[2], ex.x -
Explo_img[2].getWidth(null) / 2, ex.y -
Explo_img[2].getHeight(null) / 2, this);
}else if( ex.ex_cnt > 21 ) {
Explosion_List.remove(i);
ex.ex_cnt = 0;
//폭발은 따로 카운터를 계산하여
//이미지를 순차적으로 그림.
}
}else { //설정값이 1이면 단순 피격용 이미지 그리기
if ( ex.ex_cnt < 7  ) {
buffg.drawImage(Explo_img[0], ex.x + 120,
ex.y + 15, this);
}else if ( ex.ex_cnt < 14 ) {
buffg.drawImage(Explo_img[1], ex.x + 60,
ex.y + 5, this);
}else if ( ex.ex_cnt < 21 ) {
buffg.drawImage(Explo_img[0], ex.x + 5,
ex.y + 10, this);
}else if( ex.ex_cnt > 21 ) {
Explosion_List.remove(i);
ex.ex_cnt = 0;
//단순 피격 또한 순차적으로 이미지를 그리지만
//구분을 위해 약간 다른 방식으로 그립니다.

}
}
}
}

public void Draw_StatusText(){ //상태 체크용  텍스트를 그립니다.

buffg.setFont(new Font("Defualt", Font.BOLD, 20));
//폰트 설정하는 코드:  기본폰트, 굵게, 사이즈 20

buffg.drawString("SCORE : " + game_Score, 350, 70);
//좌표 x,y에 스코어를 표시합니다. 크기 가려질 시 유동적으로 변경할 것.

buffg.drawString("생명 : " + player_Hitpoint, 350, 90);
//좌표 x,y에 플레이어 체력을 표시합니다.

}
public void KeyProcess(){
if(KeyUp == true) {
if( y > 20 ) y -= 5;
//캐릭터가 보여지는 화면 위로 못 넘어가게 합니다.

player_Status = 0;
//이동키가 눌려지면 플레이어 상태를 0으로 돌립니다.
}

if(KeyDown == true) {
if( y+ Player_img[0].getHeight(null) < f_height ) y += 5;
//캐릭터가 보여지는 화면 아래로 못 넘어가게 합니다.

player_Status = 0;
//이동키가 눌려지면 플레이어 상태를 0으로 돌립니다.
}

if(KeyLeft == true) {
if ( x > 0 ) x -= 5;
//캐릭터가 보여지는 화면 왼쪽으로 못 넘어가게 합니다.

player_Status = 0;
//이동키가 눌려지면 플레이어 상태를 0으로 돌립니다.
}

if(KeyRight == true) {
if ( x + Player_img[0].getWidth(null) < f_width ) x += 5;
//캐릭터가 보여지는 화면 오른쪽으로 못 넘어가게 합니다.

player_Status = 0;
//이동키가 눌려지면 플레이어 상태를 0으로 돌립니다.
}
}

public void keyPressed(KeyEvent e){
 
switch(e.getKeyCode()){
case KeyEvent.VK_UP :
KeyUp = true;
break;
case KeyEvent.VK_DOWN :
KeyDown = true;
break;
case KeyEvent.VK_LEFT :
KeyLeft = true;
break;
case KeyEvent.VK_RIGHT :
KeyRight = true;
break;

case KeyEvent.VK_SPACE :
KeySpace = true;
break;
}
}
public void keyReleased(KeyEvent e){
 
switch(e.getKeyCode()){
case KeyEvent.VK_UP :
KeyUp = false;
break;
case KeyEvent.VK_DOWN :
KeyDown = false;
break;
case KeyEvent.VK_LEFT :
KeyLeft = false;
break;
case KeyEvent.VK_RIGHT :
KeyRight = false;
break;

case KeyEvent.VK_SPACE :
KeySpace = false;
break;

}
}
public void keyTyped(KeyEvent e){}
}

class Missile{
int x;
int y;

int speed; // 공격(미사일) 스피드 변수를 추가.
 
Missile(int x, int y, int speed) {
this.x = x;
this.y = y;

this.speed = speed;
// 객체 생성시 속도 값을 추가로 받습니다.

}
public void move(){
y -= speed; // 공격 스피드 속도 만큼 이동
}
}

class Enemy{
int x;
int y;

int speed; // 적 이동 속도 변수를 추가

Enemy(int x, int y, int speed ) {
this.x = x;
this.y = y;

this.speed = speed;
// 객체 생성시 속도 값을 추가로 받습니다.

}
public void move(){
x -= speed;// 적이동속도만큼 이동
}
}

class Explosion{ //★★★★★★★★★★★★★★★★★ 폭발클래스
// 여러개의 폭발 이미지를 그리기위해 클래스를 추가하여 객체관리

int x; //이미지를 그릴 x 좌표
int y; //이미지를 그릴 y 좌표
int ex_cnt; //이미지를 순차적으로 그리기 위한 카운터
int damage; //이미지 종류를 구분하기 위한 변수값

Explosion(int x, int y, int damage){
this.x = x;
this.y = y;
this.damage = damage;
ex_cnt = 0;
}
public void effect(){
ex_cnt ++; //해당 메소드 호출 시 카운터를 +1 시킨다.
}
}