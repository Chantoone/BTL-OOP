/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author Asus
 */
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    
    int boardWidth = 360;
    int boardHeight = 640;
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image[] backgrounds;
    Image[] bottomPipes;
    Image[] topPipes;
    int backgroundIndex = 0;
    int toppipeIndex = 0;
    int bottompipeIndex = 0;
    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {

        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {

        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean pass = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    class Bonus {

        int x;
        int y;
        int width = 34; // Kích thước bonus, có thể thay đổi nếu cần
        int height = 34;
        Image img;

        Bonus(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }
    }

    Bird bird;
    int velocityX = -4;

    int velocityY = 0;
    int gravity = 1;
    Image bonusImg;
    Bonus bonus;
    ArrayList<Pipe> pipes;
    Random random = new Random();
    boolean gameOver = false;
    double score = 0;
    double lastScoreCheckpoint = 0;
    Timer gameLoop;
    Timer placePipesTimer;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        backgrounds = new Image[]{
            new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage(),
            new ImageIcon(getClass().getResource("./flappybirdbgnight.png")).getImage(),};
        backgroundImg = backgrounds[backgroundIndex]; // Đặt hình nền ban đầu

        bottomPipes = new Image[]{
            new ImageIcon(getClass().getResource("./bottompipe.png")).getImage(),
            new ImageIcon(getClass().getResource("./bottompipeB.png")).getImage(),};
        bottomPipeImg = bottomPipes[bottompipeIndex];
        topPipes = new Image[]{
            new ImageIcon(getClass().getResource("./toppipe.png")).getImage(),
            new ImageIcon(getClass().getResource("./toppipeB.png")).getImage(),};
        topPipeImg = topPipes[toppipeIndex];

        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bird = new Bird(birdImg);

        bonusImg = new ImageIcon(getClass().getResource("./bonus.png")).getImage();

        pipes = new ArrayList<Pipe>();
        //place pipes timer
        placePipesTimer = new Timer((1500), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }
    public void playSound(String soundFileName) {
    try {
        // Tải tài nguyên âm thanh từ classpath
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(soundFileName));
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        e.printStackTrace();
    }
}

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

        double x = random.nextDouble();
        if (x < 0.5) {

            int bonusY = topPipe.y + pipeHeight + openingSpace / 2-10;
            bonus = new Bonus(pipeX, bonusY, bonusImg);
        } else {
            bonus = null; // Không có bonus lần này
        }

    }

    // painCommonent luôn tự động được gọi đến
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        if (bonus != null) {
            g.drawImage(bonus.img, bonus.x, bonus.y, bonus.width, bonus.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            
            g.drawString("GAME OVER:" + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipe
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (!pipe.pass && bird.x > pipe.x + pipe.width) {
                pipe.pass = true;
                score += 0.5;
                playSound("./ting.wav");
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bonus != null) {
            bonus.x += velocityX;

            // Kiểm tra va chạm giữa chim và bonus
            if (collisionBB(bird, bonus)) {
                score += 2; // Cộng điểm bonus
                bonus = null; // Xóa bonus sau khi nhận
            }

            // Xóa bonus nếu ra khỏi màn hình
            if (bonus != null && bonus.x + bonus.width < 0) {
                bonus = null;
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }

    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width
                && // kiểm tra góc phải của pipe có đè lên bird hay k
                a.x + a.width > b.x
                && //  kiểm tra cạnh trái của pipe có đè lên bird hay không
                a.y < b.y + b.height
                && // kiểm tra cạnh dưới của pipe có đè lên cạnh trên của bird hay không
                a.y + a.height > b.y; // kiêm tra cạnh trên của Pipe có đè lên cạnh dưới của Bird hay không

    }
    public boolean collisionBB(Bird bird, Bonus bonus) {
    // Kiểm tra va chạm theo chiều ngang
    boolean xOverlap = bird.x + bird.width == bonus.x || bird.x == bonus.x + bonus.width;
    
    // Kiểm tra va chạm theo chiều dọc
    boolean yOverlap = bird.y + bird.height == bonus.y || bird.y == bonus.y + bonus.height;
    
    // Nếu có sự chồng chéo trên cả hai chiều, tức là có va chạm
    return xOverlap && yOverlap;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if ((int) score % 5 == 0 && score != lastScoreCheckpoint) {
            backgroundIndex = (backgroundIndex + 1) % backgrounds.length;
            backgroundImg = backgrounds[backgroundIndex];
            toppipeIndex = (toppipeIndex + 1) % topPipes.length;
            topPipeImg = topPipes[toppipeIndex];
            bottompipeIndex = (bottompipeIndex + 1) % bottomPipes.length;
            bottomPipeImg = bottomPipes[bottompipeIndex];
            lastScoreCheckpoint = score;
        }

        if (gameOver) {
            
            placePipesTimer.stop();
            gameLoop.stop();
        } else {
            // Tăng độ khó khi điểm số tăng lên
            int newDelay = Math.max(800, (int) (1500 - score * 50));
            placePipesTimer.setDelay(newDelay);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
             playSound("./space.wav");
            if (gameOver) {
                // restart the game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
