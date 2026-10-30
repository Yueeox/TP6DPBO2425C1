import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {
    // membuat variabel variabel yang dibutuhkan

    // ukuran frame
    int frameWidth = 360;
    int frameHeight = 640;

    // player
    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    // pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // Timer dan kecepatan pipa muncul juga gravitasi
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1 ;

    // logika permainan
    boolean gameOver = false;
    boolean isStarted = false;
    int score = 0;
    int pipeVelocityX = -2;

    // Variabel untuk background bergerak
    int bgX1 = 0;
    int bgX2 = frameWidth;
    int bgSpeed = -1; // Kecepatan background (negatif = bergerak ke kiri)


    View view;
    Sound sound;

    // variabel asset
    Image birdImage;
    Image background;
    Player player;
    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    // logic
    public Logic(){
        // melakukan new pada semua asset yang dibutuhkan
        sound = new Sound();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        background = new ImageIcon(getClass().getResource("assets/background.png")).getImage();

        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        pipes = new ArrayList<Pipe>();

        // kecepatan [pipa yang akan muncul
        pipesCooldown = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent actionEvent){
                //System.out.println("Pipa");
                placePipes();
            }
        });

        pipesCooldown.start();
        // memanggil setup game yang dibawah
        setupGame();
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    // menentukan posisi player ketika game dimulai
    public void setupGame() {

        bgX1 = 0;
        bgX2 = frameWidth;
        // semuanya direset ke awal
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocity(0);
        pipes.clear();
        score = 0;
        gameOver = false;
        isStarted = false;

        //reset
        openingSpace = 200;
        pipesCooldown.setDelay(3500);
    }
    // getter setter
    public void setView(View view){
        this.view = view;
    }

    public Player getPlayer(){
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public Image getBackground() {
        return background;
    }

    public int getBgX1() { return bgX1; }
    public int getBgX2() { return bgX2; }

    // jarak antara pipa atas dan bawah
    int openingSpace = 200;
    public void placePipes() {
        if (gameOver || !isStarted) return;
        // 1. Tentukan di mana "Pusat Celah" akan muncul secara acak.
        // Kita batasi agar pusat celah berada di antara Y=150 sampai Y=450 (area tengah layar).
        int minCenterY = 150;
        int maxCenterY = 450;
        int randomCenterY = minCenterY + (int)(Math.random() * (maxCenterY - minCenterY));

        // 2. Hitung posisi Y untuk pipa atas.
        // Pipa atas ditarik ke atas sejauh setengah dari openingSpace.
        int upperPipeY = randomCenterY - (openingSpace / 2) - pipeHeight;

        // 3. Hitung posisi Y untuk pipa bawah.
        // Pipa bawah ditarik ke bawah sejauh setengah dari openingSpace.
        int lowerPipeY = randomCenterY + (openingSpace / 2);

        // 4. Masukkan ke dalam list
        Pipe upperPipe = new Pipe(pipeStartPosX, upperPipeY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, lowerPipeY, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    // mengupdate dificulty (wave) yang ada pada game
    public void updateDifficulty() {
        // 1. Celah Pipa Semakin Sempit (Minimal 100px agar masih bisa dilewati)
        if (score > 0 && score % 5 == 0) { // Setiap skor kelipatan 5
            if (openingSpace > 100) {
                openingSpace -= 20; // Kurangi celah sebanyak 20 pixel
            }
        }
        // 2. Jarak Pipa Semakin Rapat (Cooldown Timer dipercepat)
        int newDelay = 3500 - (((score / 5) + 1) * 500);
        if (newDelay < 900) {
            newDelay = 900;
        }
        pipesCooldown.setDelay(newDelay);
    }

    // untuk gerakan player
    public void move(){

        // Background bergerak JIKA game belum dimulai DAN belum game over
        if (!gameOver) {
            bgX1 += bgSpeed;
            bgX2 += bgSpeed;

            // Jika gambar pertama sudah habis di layar kiri, pindahkan ke kanan
            if (bgX1 <= -frameWidth) {
                bgX1 = frameWidth;
            }
            // Jika gambar kedua sudah habis di layar kiri, pindahkan ke kanan
            if (bgX2 <= -frameWidth) {
                bgX2 = frameWidth;
            }
        }

        if (gameOver) {
            playerFalling();
            return;
        }
        if (!isStarted) return;

        player.setVelocity(player.getVelocity() + gravity);
        player.setPosY(player.getPosY() + player.getVelocity());

        // Cek jatuh
        if(player.getPosY() > frameHeight || player.getPosY() < 0){
            gameOver = true;
            sound.stopBGM();
            return;
        }

        player.setPosY(Math.max(player.getPosY(), 0));
        if(player.getPosY() > frameHeight){
            // game over / reset
            player.setPosY(playerStartPosY);
            player.setVelocity(0);
        }
        for(int i = pipes.size() - 1; i >= 0; i--){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            //mendetek trabrakan jeder
            if(player.getBounds().intersects(pipe.getBounds())){
                gameOver = true;
                sound.stopBGM();
            }

            if (!pipe.getPassed() && pipe.getPosX() + pipe.getWidth() < player.getPosX()) {
                pipe.setPassed(true);
                // Hanya hitung skor untuk pipa bawah
                // Pipa bawah posisinya lebih besar dari 0
                if (pipe.getPosY() > 0) {
                    score++;
                    sound.playPoinSound();
                    updateDifficulty();
                }
            }

            //hapus pipa yang udah lewat
            if (pipe.getPosX() + pipe.getWidth() < 0) {
                pipes.remove(i);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        if(view != null){
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e){
        // keyPressed untuk spasi untuk lompat
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if (!gameOver){
                if(!isStarted){
                    sound.playBGM();
                }

                isStarted = true;
                player.setVelocity(-10);
                sound.playJumpSound();
            }
        }

        // restart game
        if(e.getKeyCode() == KeyEvent.VK_R && gameOver){
            setupGame();
            sound.stopBGM();
        }
    }
    public void keyReleased(KeyEvent e){}

    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }

    public void playerFalling() {
        if (player.getPosY() < frameHeight) { // Jika belum menyentuh tanah
            player.setVelocity(player.getVelocity() + gravity);
            player.setPosY(player.getPosY() + player.getVelocity());

            // Opsional: rotasi burung atau batasi kecepatan jatuh
            if (player.getVelocity() > 15) {
                player.setVelocity(15); // Batas kecepatan jatuh maksimal
            }
        }
    }
}
