import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

    // board
    boolean bossActive = false;
    int tileSize = 32;
    int rows = 20;
    int columns = 20;
    int boardWidth = tileSize * columns; // 32 * 16
    int boardHeight = tileSize * rows; // 32 * 16

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    Image bulletImg;
    ArrayList<Image> alienImgArray;
    Image backgroundImg;
    Image enemyBulletImg;

    class Boss {
        int x, y;
        int width, height;
        int velocityX = 2; // Tốc độ di chuyển của boss
        int health = 10; // Máu của boss
        Image img;
    
        Boss(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    
        void move() {
            x += velocityX;
    
            // Đổi hướng khi chạm biên
            if (x + width >= boardWidth || x <= 0) {
                velocityX *= -1;
            }
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; // used for aliens
        boolean used = false; // used for bullets
        int shipVelocityY = tileSize; // tốc độ di chuyển theo trục Y

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    class EnemyBullet {
        int x, y;
        int width = tileSize;
        int height = tileSize;
        int velocityY = 5; // Bullet moves downward
        Image img; // biến lưu hình ảnh

        EnemyBullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void move() {
            y += velocityY; // Move the bullet downward
        }
    }

    // Tàu
    int shipWidth = 124/2;  // Thay đổi chiều rộng tàu thành 124 pixel
    int shipHeight = 98/2;  // Thay đổi chiều cao tàu thành 98 pixel
    int shipX = (boardWidth - shipWidth) / 2;  // Đặt tàu vào giữa màn hình
    int shipY = boardHeight - shipHeight - tileSize;  // Đặt tàu xuống dưới cùng màn hình, cách một hàng tile
    int shipVelocityX = tileSize; // Tốc độ di chuyển theo chiều X
    int shipVelocityY = tileSize; // Tốc độ di chuyển theo chiều Y
    Block ship;
    Boss boss;

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

    // bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 2;
    int bulletHeight = tileSize;
    int bulletVelocityY = -10; // bullet moving speed

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // load images
        boss = new Boss(boardWidth / 2 - 64, 50, 128, 128, new ImageIcon(getClass().getResource("./boss.png")).getImage());
        shipImg = new ImageIcon(getClass().getResource("./ship_en.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien_red.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien_red.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien_red.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien_red.png")).getImage();
        bulletImg = new ImageIcon(getClass().getResource("./fire.png")).getImage();
        backgroundImg = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        enemyBulletImg = new ImageIcon(getClass().getResource("./shit.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

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
        if (bossActive && boss != null) {
            g.drawImage(boss.img, boss.x, boss.y, boss.width, boss.height, null);
            g.setColor(Color.RED);
            g.fillRect(boss.x, boss.y - 20, boss.width, 10);
            g.setColor(Color.GREEN);
            g.fillRect(boss.x, boss.y - 20, (int) (boss.width * ((double) boss.health / 10)), 10);
        }
        
    
        // Vẽ aliens
        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }
    
        // Vẽ đạn của kẻ địch (đạn của boss và alien)
        for (EnemyBullet enemyBullet : enemyBullets) {
            g.drawImage(enemyBulletImg, enemyBullet.x, enemyBullet.y, enemyBullet.width, enemyBullet.height, null);
        }
    
        // Vẽ đạn của người chơi
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.drawImage(bullet.img, bullet.x, bullet.y, bullet.width, bullet.height, null);
            }
        }
    
        // Vẽ điểm số
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + score, 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }
    

    public void move() {
        if (!bossActive && score >= 1000) { // Tăng điểm yêu cầu
            bossActive = true;
            boss = new Boss(boardWidth / 2 - 64, 50, 128, 128, 
                    new ImageIcon(getClass().getResource("./boss.png")).getImage());
        }
        
    
        if (bossActive && boss != null) {
            alienArray.clear();
            boss.move();
        
            // Boss bắn đạn ngẫu nhiên
            if (Math.random() < 0.01) { // 1% xác suất bắn đạn mỗi khung hình
                enemyBullets.add(new EnemyBullet(boss.x + boss.width / 2, boss.y + boss.height));
            }
        
            // Kiểm tra va chạm giữa đạn của người chơi và boss
            for (int i = 0; i < bulletArray.size(); i++) {
                Block bullet = bulletArray.get(i);
                if (!bullet.used && detectCollision(bullet, new Block(boss.x, boss.y, boss.width, boss.height, null))) {
                    bullet.used = true;
                    boss.health--; // Giảm máu của boss
                    if (boss.health <= 0) {
                        bossActive = false;
                        boss = null;
                        score += 1000;
                        
                        alienArray.clear();
                        bulletArray.clear();
                        enemyBullets.clear();
                        
                        alienColumns = 3; // Reset số cột alien
                        alienRows = 2; // Reset số hàng alien
                        
                        createAliens(); // Tạo lại alien bình thường
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
                        enemyBullets.add(new EnemyBullet(alien.x + alienWidth / 2, alien.y + alienHeight));
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
                    }
                }
            }
        }
    
        // Di chuyển đạn của kẻ địch (giữ nguyên như cũ)
        for (int i = 0; i < enemyBullets.size(); i++) {
            EnemyBullet enemyBullet = enemyBullets.get(i);
            enemyBullet.move();
    
            if (enemyBullet.y > boardHeight) {
                enemyBullets.remove(i);
                i--;
            }
    
            if (detectCollision(enemyBullet, ship)) {
                gameOver = true;
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
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
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
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c * alienWidth,
                    alienY + r * alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
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

    public boolean detectCollision(EnemyBullet bullet, Block ship) {
        return bullet.x < ship.x + ship.width &&
                bullet.x + bullet.width > ship.x &&
                bullet.y < ship.y + ship.height &&
                bullet.y + bullet.height > ship.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        Bulletmove();
        repaint();
        if (gameOver) {
            gameLoop.stop();
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
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        bossActive = false; // Reset trạng thái boss
        createAliens();
        gameLoop.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) { // Nếu game kết thúc, bất kỳ phím nào cũng có thể restart
            restartGame();
        } else {
            // Xử lý di chuyển tàu theo các phím mũi tên
            if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
                ship.x -= shipVelocityX;  // Di chuyển sang trái
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocityX + ship.width <= boardWidth) {
                ship.x += shipVelocityX;  // Di chuyển sang phải
            } else if (e.getKeyCode() == KeyEvent.VK_UP && ship.y - shipVelocityY >= boardHeight / 2) {
                ship.y -= shipVelocityY;  // Di chuyển lên (giới hạn không cho bay lên quá nửa màn hình)
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && ship.y + shipVelocityY + ship.height <= boardHeight) {
                ship.y += shipVelocityY;  // Di chuyển xuống
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                // Bắn đạn
                Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, bulletImg);
                bulletArray.add(bullet);
            }
        }
    }
}