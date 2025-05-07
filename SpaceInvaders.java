import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    ReadAndAddFile readAndAddFile, usedShip, coinsFile, soundFile, musicFightingBossFile;

    JButton retryButton, homeButton, continueButton;
    JLabel pausedLabel;
    // Font
    private Font pixelFont;
    // board
    boolean bossActive = false;
    int tileSize = 32;
    int rows = 20;
    int columns = 20;
    int boardWidth = tileSize * columns; // 32 * 16
    int boardHeight = tileSize * rows; // 32 * 16

    // sound
    SoundPlayer shootSound;
    SoundPlayer defeatEnemySound;
    SoundPlayer gameOverSound;
    SoundPlayer fightingBossMusic;
    SoundPlayer bossLazerSound;
    SoundPlayer evilLaughSound;
    SoundPlayer bossdeathSound;
    SoundPlayer bossPhase2Sound;
    SoundPlayer bossWinSound;

    Image shipImg;
    Image alienImg;
    Image alienExplosionImage;
    Image bulletImg;
    Image backgroundImg;
    Image enemyBulletImg;
    Image bossImg;
    Image bossLazerImage;
    ArrayList<Image> bossImages;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; // used for aliens
        boolean used = false; // used for bullets
        int shipVelocityY = tileSize; // tốc độ di chuyển theo trục Y
        int explosionTimer = 0;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // Tàu
    int shipWidth = 160/2;  // Thay đổi chiều rộng tàu thành 124 pixel
    int shipHeight = 130/2;  // Thay đổi chiều cao tàu thành 98 pixel
    int shipX = (boardWidth - shipWidth) / 2;  // Đặt tàu vào giữa màn hình
    int shipY = boardHeight - shipHeight - tileSize;  // Đặt tàu xuống dưới cùng màn hình, cách một hàng tile
    int shipVelocityX = tileSize; // Tốc độ di chuyển theo chiều X
    int shipVelocityY = tileSize; // Tốc độ di chuyển theo chiều Y
    Block ship;

    // boss
    int bossWidth = 128;
    int bossHeight = 128;
    int bossX = boardWidth / 2 - 64;
    int bossY = 50;
    int bossVelocityX = 2;
    int bossHealth = 20;
    Block boss;

    // aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0; // number of aliens to defeat
    int alienVelocityX = 1; // alien moving speed

    // enemy's bullets
    ArrayList<Block> enemyBullets;
    int enemyBulletWidth = tileSize;
    int enemyBulletHeight = tileSize;
    int enemyBulletVelocityY = 5;

    // Boss Lazers
    ArrayList<Image> bossLazers;
    int bossLazerWidth = tileSize * 2;
    int bossLazerHeight = boardHeight - bossY - bossHeight;
    boolean isBossPhase2 = false; // Trạng thái phase 2 của boss
    Block bossLazer;
    boolean bossFiringLaser = false; // true if boss is firing lazers
    int bossLazerCooldown = 420; // cooldown period for firing lazers
    int bossLazerCooldownTimer = 0;
    int bossStationaryTimer = 0; // timer for boss to stay stationary while firing
    int bossStationaryDuration = 100; // duration for boss to stay stationary
    boolean bossPreparingLaser = false; // Boss đang chuẩn bị bắn laser
    int bossPrepareTimer = 0; // Bộ đếm thời gian chuẩn bị bắn laser
    int bossPrepareDuration = 60; // Thời gian chuẩn bị bắn laser (60 frame ~ 1 giây)

    // bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 2;
    int bulletHeight = tileSize;
    int bulletVelocityY = -10; // bullet moving speed

    // old score to get a boss
    int oldBossScore = 0;

    Timer gameLoop;
    boolean gameOver = false;
    boolean paused = false;
    int score = 0;

    SoundPlayer backgroundMusic;

    SpaceInvaders(SoundPlayer backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // setFont 
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./font_words/PressStart2P-Regular.ttf")).deriveFont(20f);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }    
        // Read and Add file
        usedShip = new ReadAndAddFile("used_ship.txt");
        readAndAddFile = new ReadAndAddFile("HighestScores.txt");
        this.coinsFile = new ReadAndAddFile("./coins.txt");
        this.soundFile = new ReadAndAddFile("./sound.txt");
        this.musicFightingBossFile = new ReadAndAddFile("./music.txt");
        if (this.usedShip.isEmpty_UsedShip()) {
            this.usedShip.savePurchasedShip("ship_flying.gif");
        }
        else{
            shipImg = new ImageIcon(getClass().getResource(usedShip.getActiveShip())).getImage();
        }
        bossImages = new ArrayList<Image>();
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss1.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss2.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss3.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss4.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss5.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss6.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss7.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss8.gif")).getImage());
        bossImages.add(new ImageIcon(getClass().getResource("./BossPictures/boss9.gif")).getImage());

        bossLazers = new ArrayList<Image>();
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/WhiteBlueLazer_Boss1.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/LightningLazer_Boss2.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/FireLazer_Boss3.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/WhiteRedLazer_Boss4.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/BoldPurpleLazer_Boss5.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/RedLazer_Boss6.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/BlueLazer_Boss7.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/WhiteGreenLazer_Boss8.gif")).getImage());
        bossLazers.add(new ImageIcon(getClass().getResource("./LazerPictures/GreenLazer_Boss9.gif")).getImage());

        
        // load images
        Random random = new Random();
        int randomIndex = random.nextInt(bossImages.size());
        // bossImg = bossImages.get(randomIndex);
        // bossImg = new ImageIcon(getClass().getResource("./boss.png")).getImage();
        // shipImg = new ImageIcon(getClass().getResource("./ship_flying.gif")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien_red.png")).getImage();
        alienExplosionImage = new ImageIcon(getClass().getResource("./alien_explosion.gif")).getImage();
        bulletImg = new ImageIcon(getClass().getResource("./fire.png")).getImage();
        backgroundImg = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        enemyBulletImg = new ImageIcon(getClass().getResource("./shit.png")).getImage();

        shootSound = new SoundPlayer("./shotting.wav");
        defeatEnemySound = new SoundPlayer("./defeated-sigh.wav");
        gameOverSound = new SoundPlayer("./game-over.wav");
        fightingBossMusic = new SoundPlayer("fighting-boss.wav");
        evilLaughSound = new SoundPlayer("intro-boss-sound.wav");
        bossLazerSound = new SoundPlayer("beamLazer-effect.wav");
        bossdeathSound = new SoundPlayer("boss-death-sound.wav");
        bossPhase2Sound = new SoundPlayer("boss-phase2-sound.wav");
        bossWinSound = new SoundPlayer("evil-laugh2.wav");
        if (musicFightingBossFile.readStateMusic()) {
            fightingBossMusic.setVolume(0);
        }
        else{
            fightingBossMusic.setVolume(musicFightingBossFile.readVolumeMusic());
        }

        if (soundFile.readStateSound()){
            shootSound.setVolume(0);
            defeatEnemySound.setVolume(0);
            gameOverSound.setVolume(0);
            evilLaughSound.setVolume(0);
            bossLazerSound.setVolume(0);
            bossdeathSound.setVolume(0);
            bossPhase2Sound.setVolume(0);
            bossWinSound.setVolume(0);
        }
        else{
            shootSound.setVolume(soundFile.readVolumeSound());
            defeatEnemySound.setVolume(soundFile.readVolumeSound());
            gameOverSound.setVolume(soundFile.readVolumeSound());
            evilLaughSound.setVolume(soundFile.readVolumeSound());
            bossLazerSound.setVolume(soundFile.readVolumeSound());
            bossdeathSound.setVolume(soundFile.readVolumeSound());
            bossPhase2Sound.setVolume(soundFile.readVolumeSound());
            bossWinSound.setVolume(soundFile.readVolumeSound());
        }

        //Button for gameover
        this.retryButton = new JButton("Play again");
        retryButton.setBounds(boardWidth / 2 - 150, boardHeight / 2 + 20, 300, 40);
        retryButton.setFont(pixelFont.deriveFont(20f));
        retryButton.setForeground(Color.WHITE);
        retryButton.setBorder(new RoundedBorder(20));
        retryButton.setContentAreaFilled(false);
        retryButton.setFocusPainted(false);
        retryButton.setVisible(false);
        retryButton.addActionListener(e -> {
            restartGame();
        });

        //Button for continuing the game
        this.continueButton = new JButton("Continue");
        continueButton.setBounds(boardWidth / 2 - 150, boardHeight / 2 + 20, 300, 40);
        continueButton.setFont(pixelFont.deriveFont(20f));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBorder(new RoundedBorder(20));
        continueButton.setContentAreaFilled(false);
        continueButton.setFocusPainted(false);
        continueButton.setVisible(false);
        continueButton.addActionListener(e -> {
            paused = false;
            continueButton.setVisible(false);
            homeButton.setVisible(false);
            pausedLabel.setVisible(false);
            gameLoop.start(); // Resume the game
        });
        //Button for going back to menu
        this.homeButton = new JButton("Menu");
        homeButton.setFont(pixelFont.deriveFont(20f));
        homeButton.setForeground(Color.WHITE);
        homeButton.setBorder(new RoundedBorder(20));
        homeButton.setContentAreaFilled(false);
        homeButton.setFocusPainted(false);
        homeButton.setBounds(boardWidth / 2 - 150, boardHeight / 2 + 80, 300, 40);
        homeButton.setVisible(false);
        homeButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            BackToMenu(frame);
        });

        // Title when paused
        pausedLabel = new JLabel("PAUSED");
        pausedLabel.setFont(pixelFont.deriveFont(50f));
        pausedLabel.setForeground(Color.WHITE);
        pausedLabel.setBounds(boardWidth / 2 - 150, boardHeight / 2 - 80, 500, 50);
        pausedLabel.setVisible(false); // Initially hidden
    
        // add buttons to the panel
        this.setLayout(null);
        this.add(retryButton);
        this.add(continueButton);
        this.add(homeButton);
        this.add(pausedLabel);

        boss = new Block(bossX, bossY, bossWidth, bossHeight, bossImg);
        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();
        enemyBullets = new ArrayList<Block>();
        
        // game timer
        gameLoop = new Timer(1000 / 60, this); // 1000/60 = 16.6
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null); // Vẽ nền ở vị trí (0, 0) với kích thước của
                                                                         // bảng trò chơi
        draw(g);
    }

    public void draw(Graphics g) {
        // Vẽ nền
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
    
        // Vẽ tàu
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        // Vẽ boss nếu đang hoạt động
        if (bossActive) {
            g.drawImage(boss.img, boss.x, boss.y, boss.width, boss.height, null);
            g.setColor(Color.RED);
            g.fillRect(boss.x, boss.y - 20, boss.width, 10);
            g.setColor(Color.GREEN);
            g.fillRect(boss.x, boss.y - 20, (int) (boss.width * ((double) bossHealth / 20)), 10);
            
        }
        if (bossLazer != null) {
            g.drawImage(bossLazer.img, bossLazer.x, bossLazer.y, bossLazer.width, bossLazer.height, null);
        }
    
        // Vẽ aliens
        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
            else if (alien.explosionTimer > 0){
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
                alien.explosionTimer--;
            }
        }
    
        // Vẽ đạn của kẻ địch (đạn của boss và alien)
        for (Block enemyBullet : enemyBullets) {
            g.drawImage(enemyBulletImg, enemyBullet.x, enemyBullet.y, enemyBullet.width, enemyBullet.height, null);
        }
    
        // Vẽ đạn của người chơi
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.drawImage(bullet.img, bullet.x, bullet.y, bullet.width, bullet.height, null);
            }
        }
        
        // Vẽ điểm số   
        if (gameOver) {
            g.setFont(pixelFont.deriveFont(50f));
            g.setColor(Color.WHITE);
            int gameOverX = (boardWidth - g.getFontMetrics().stringWidth("Game Over"))/2;
            g.drawString("Game Over", gameOverX, boardHeight/2);
            if (!this.retryButton.isVisible()) {
                retryButton.setVisible(true);
                homeButton.setVisible(true);
                int lastcoin = coinsFile.readCoins();
                coinsFile.addCoins(score + lastcoin);
                readAndAddFile.AddFile_highestScore(score);
            }
        } else {
            // Đọc điểm cao nhất từ file
            String highScore = readAndAddFile.ReadFile_highestScore(); 

            // Font pixel
            Font pixelFont = new Font("Monospaced", Font.BOLD, 14);
            g.setFont(pixelFont);
            FontMetrics metrics = g.getFontMetrics(pixelFont);

            // Xác định kích thước chữ
            int padding = 8;
            int textWidth = Math.max(metrics.stringWidth("CURRENT SCORE: "), metrics.stringWidth("HIGHEST SCORE: " + highScore));
            int boxWidth = textWidth + 2 * padding;
            int boxHeight = 50; // Chiều cao tổng cộng của khung
            int scoreBoxX = 10;
            int scoreBoxY = 10;
            // Vẽ nền mờ (đen trong suốt)
            g.setColor(new Color(0, 0, 0, 150)); 
            g.fillRect(scoreBoxX, scoreBoxY, boxWidth, boxHeight);

            // Vẽ khung cyan
            g.setColor(new Color(0, 255, 255)); 
            g.drawRect(scoreBoxX, scoreBoxY, boxWidth, boxHeight);
            g.drawRect(scoreBoxX - 2, scoreBoxY - 2, boxWidth + 4, boxHeight + 4);

            // Vẽ chữ "CURRENT SCORE"
            g.setColor(new Color(255, 180, 50)); 
            g.drawString("CURRENT SCORE", scoreBoxX + padding, scoreBoxY + 15);

            // Vẽ điểm hiện tại (SC + số)
            g.setColor(new Color(255, 80, 150)); 
            g.drawString("SC:", scoreBoxX + padding, scoreBoxY + 30);
            g.setColor(new Color(255, 200, 50)); 
            g.drawString(String.valueOf(score), scoreBoxX + padding + metrics.stringWidth("SC:"), scoreBoxY + 30);

            // Vẽ "HIGH SCORE" bên dưới
            g.setColor(new Color(255, 180, 50)); 
            g.drawString("HIGHEST SCORE: ", scoreBoxX + padding, scoreBoxY + 45);

            g.setColor(new Color(255, 200, 50)); 
            g.drawString(highScore, scoreBoxX + padding + metrics.stringWidth("HIGHEST SCORE: "), scoreBoxY + 45);
        }
    }
    

    public void move() {
        if (!bossActive && score - oldBossScore >= 4000) { // Tăng điểm yêu cầu
            oldBossScore += 4000;
            createBoss(); // Tạo boss mới
            bossActive = true;
            evilLaughSound.play();
        }
        
        if (bossActive) {
            alienArray.clear();
            fightingBossMusic.loop();
            backgroundMusic.stop();
            if (!isBossPhase2 && bossHealth < 10) { // Phase 2 bắt đầu khi máu boss < 10
                isBossPhase2 = true; // Đánh dấu boss đã vào phase 2
                bossPhase2Sound.play(); // Phát âm thanh phase 2
            }
            if (bossPreparingLaser) {
                bossLazerSound.play();
                bossPrepareTimer++;
                if (bossPrepareTimer >= bossPrepareDuration) {
                    bossPreparingLaser = false;
                    bossFiringLaser = true;
                    bossPrepareTimer = 0;
                    bossLazer = new Block(
                        boss.x + boss.width / 2 - bossLazerWidth / 2,
                        boss.y + 2*boss.height/5,
                        bossLazerWidth,
                        bossLazerHeight + boss.height * 3/5,
                        bossLazerImage
                    );
                }
            } else if (bossFiringLaser) {
                bossStationaryTimer++;
                int currentBossVelocityX = 1;
                boss.x += bossVelocityX > 0 ? currentBossVelocityX : -currentBossVelocityX;
                if (boss.x + boss.width >= boardWidth || boss.x <= 0) {
                    bossVelocityX *= -1; // Đổi hướng khi chạm biên
                }
                if (bossLazer != null) {
                    bossLazer.x = boss.x + boss.width / 2 - bossLazerWidth / 2; // Cập nhật vị trí của laser
                }
                if (bossStationaryTimer >= bossStationaryDuration) {
                    bossFiringLaser = false;
                    bossLazerSound.stop();
                    bossStationaryTimer = 0;
                    bossLazer = null;
                    bossLazerCooldownTimer = 0; // Reset bộ đếm thời gian bắn laser
                    // Di chuyển boss
                    boolean isPhase2 = bossHealth < 10; // Kiểm tra xem boss đã vào giai đoạn 2 chưa
                    currentBossVelocityX = isPhase2 ? 4 : 2;
                    boss.x += bossVelocityX > 0 ? currentBossVelocityX : -currentBossVelocityX;
                    // Đổi hướng khi chạm biên
                    if (boss.x + boss.width >= boardWidth || boss.x <= 0) {
                        bossVelocityX *= -1;
                    }
                    double shootChance = isPhase2 ? 0.02 : 0.01; // Tăng xác suất bắn đạn khi boss vào giai đoạn 2
                    // Boss bắn đạn ngẫu nhiên
                    if (Math.random() < shootChance) { // 1% xác suất bắn đạn mỗi khung hình
                        if (isPhase2) {
                            enemyBullets.add(new Block(boss.x + boss.width * 2 / 5, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                            enemyBullets.add(new Block(boss.x + boss.width * 3 / 5, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                        }else {
                            enemyBullets.add(new Block(boss.x + boss.width / 2, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                        }
                    }
                }
            } else {
                bossLazerCooldownTimer++;
                if (bossLazerCooldownTimer >= bossLazerCooldown) {
                    if (Math.random() < 0.05){
                        bossPreparingLaser = true; // Bắt đầu chuẩn
                        bossPrepareTimer = 0; // Reset bộ đếm thời gian chuẩn bị bắn laser              
                    }
                }
                // Di chuyển boss
                boolean isPhase2 = bossHealth < 10; // Kiểm tra xem boss đã vào giai đoạn 2 chưa
                int currentBossVelocityX = isPhase2 ? 4 : 2;
                boss.x += bossVelocityX > 0 ? currentBossVelocityX : -currentBossVelocityX;
                // Đổi hướng khi chạm biên
                if (boss.x + boss.width >= boardWidth || boss.x <= 0) {
                    bossVelocityX *= -1;
                }
                double shootChance = isPhase2 ? 0.02 : 0.01; // Tăng xác suất bắn đạn khi boss vào giai đoạn 2
                // Boss bắn đạn ngẫu nhiên
                if (Math.random() < shootChance) { // 1% xác suất bắn đạn mỗi khung hình
                    if (isPhase2) {
                        enemyBullets.add(new Block(boss.x + boss.width * 2 / 4, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                        enemyBullets.add(new Block(boss.x + boss.width * 3 / 4, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                    }else {
                        enemyBullets.add(new Block(boss.x + boss.width / 2, boss.y + boss.height, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                    }
                }
            }
            
        } else {
            // Alien movement logic (giữ nguyên như cũ)
            for (int i = 0; i < alienArray.size(); i++) {
                Block alien = alienArray.get(i);
                if (alien.alive) {
                    alien.x += alienVelocityX;
    
                    if (Math.random() < 0.003) {
                        enemyBullets.add(new Block(alien.x + alienWidth / 2, alien.y + alienHeight, enemyBulletWidth, enemyBulletHeight, enemyBulletImg));
                    }
    
                    if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                        alienVelocityX *= -1;
                        alien.x += alienVelocityX * 2;
    
                        for (int j = 0; j < alienArray.size(); j++) {
                            alienArray.get(j).y += alienHeight;
                        }
                    }
    
                    if (alien.y >= ship.y) {
                        gameOver = true;
                        gameOverSound.play();
                    }
                }
            }
        }
    }

    public void Bulletmove() {
        // bullets
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            // bullet collision with aliens
            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    defeatEnemySound.play();
                    alien.img = alienExplosionImage;
                    bullet.used = true;
                    alien.alive = false;
                    score += 100;
                    alienCount--;
                    alien.explosionTimer = 15;
                }
            }
            if (!bullet.used && bossActive && detectCollision(bullet, boss)) {
                bullet.used = true;
                bossHealth--; // Giảm máu của boss
            }
        }

        // Boss's lazers
        if (bossLazer != null) {
            if (detectCollision(bossLazer, ship)) {
                gameOver = true;
                bossWinSound.play();
                bossLazerSound.stop();
                gameOverSound.play();
            }
        }
        // enemy bullets
        // Di chuyển đạn của kẻ địch (giữ nguyên như cũ)
        for (int i = 0; i < enemyBullets.size(); i++) {
            Block enemyBullet = enemyBullets.get(i);
            enemyBullet.y += enemyBulletVelocityY;
    
            if (enemyBullet.y > boardHeight) {
                enemyBullets.remove(i);
                i--;
            }
    
            if (detectCollision(enemyBullet, ship)) {
                gameOver = true;
                if (bossActive) {
                    bossWinSound.play();
                }
                bossLazerSound.stop();
                gameOverSound.play();
            }
        }

        // clear bullets
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0); // removes the first element of the array
        }

        // next level
        if (alienCount == 0) {
            // increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100; // bonus points :)
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2); // cap at 16/2 -2 = 6
            alienRows = Math.min(alienRows + 1, rows - 6); // cap at 16-6 = 10
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
        if (bossHealth <= 0) {
            bossActive = false;
            score += 500;
            bossHealth = 20;
            alienArray.clear();
            bulletArray.clear();
            enemyBullets.clear();
            bossdeathSound.play();
            fightingBossMusic.stop();
            backgroundMusic.loop();
            createAliens(); // Tạo lại alien bình thường
            alienCount = alienArray.size();
        }
    }

    public void createBoss() {
        Random random = new Random();
        int randomIndex = random.nextInt(bossImages.size());
        bossImg = bossImages.get(randomIndex);
        boss = new Block(bossX, bossY, bossWidth, bossHeight, bossImg);
        bossLazerImage = bossLazers.get(randomIndex);
        bossLazer = null;
    }
    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                Block alien = new Block(
                    alienX + c * alienWidth,
                    alienY + r * alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImg
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
        System.out.println("Aliens created: " + alienCount);
    }
    

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            move();
            Bulletmove();
            repaint();
            if (gameOver) {
                gameLoop.stop();
            }
        }
    }

    public void restartGame() {
        ship.x = shipX;
        ship.y = shipY;
        bulletArray.clear();
        alienArray.clear();
        enemyBullets.clear();
        gameOver = false;
        score = 0;
        oldBossScore = 0;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        bossActive = false; // Reset trạng thái boss
        bossHealth = 20; // Reset máu boss
        bossLazer = null; // Reset bossLazer
        bossFiringLaser = false; // Reset trạng thái bắn laser của boss
        bossPreparingLaser = false; // Reset trạng thái chuẩn bị bắn laser của boss
        bossLazerCooldownTimer = 0; // Reset bộ đếm thời gian bắn laser
        bossPrepareTimer = 0; // Reset bộ đếm thời gian chuẩn bị bắn laser
        bossStationaryTimer = 0; // Reset bộ đếm thời gian đứng yên của boss

        fightingBossMusic.stop(); // Dừng nhạc boss
        backgroundMusic.loop(); // Bật nhạc nền
        bossLazerSound.stop(); // Dừng âm thanh bắn laser của boss
        bossdeathSound.stop(); // Dừng âm thanh chết boss
        bossWinSound.stop(); // Dừng âm thanh thắng boss
        bossPhase2Sound.stop(); // Dừng âm thanh giai đoạn 2 của boss

        createAliens();
        retryButton.setVisible(false);
        homeButton.setVisible(false);
        continueButton.setVisible(false);
        paused = false; // Reset paused state
        gameLoop.start();

    }
    private void BackToMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        Menu menu = new Menu(frame, backgroundMusic);
        frame.add(menu);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!paused) {
                paused = true;
                gameLoop.stop(); // Dừng vòng lặp trò chơi
                continueButton.setVisible(true);
                homeButton.setVisible(true);
                pausedLabel.setVisible(true); // Hiện thị nhãn "PAUSED"
            } else {
                paused = false;
                continueButton.setVisible(false);
                homeButton.setVisible(false);
                pausedLabel.setVisible(false); // Ẩn nhãn "PAUSED"
                gameLoop.start(); // Resume the game
            }
            
        }
        if (!paused){
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship.x - shipVelocityX >= 0) {
                ship.x -= shipVelocityX;
                // ship.img = shipMoveLeftImg;
            } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship.x + shipVelocityX + ship.width <= boardWidth) {
                ship.x += shipVelocityX;
                // ship.img = shipMoveRightImg;
            } else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship.y - shipVelocityY >= boardHeight / 2) {
                ship.y -= shipVelocityY;
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && ship.y + shipVelocityY + ship.height <= boardHeight) {
                ship.y += shipVelocityY;
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE){
                shootSound.play();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            restartGame();
        } else {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A ||
                e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D ||
                e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                ship.img = shipImg;
            }
            
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, bulletImg);
                bulletArray.add(bullet);
            }
        }
    }
    
}