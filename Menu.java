import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Menu extends JPanel{
    private JFrame frame;
    private Image background;
    private Font pixelFont;
    private SoundPlayer backgroundMusic;
    private ReadAndAddFile usedShip;
    private Image usedShipImage;
    public Menu (JFrame frame, SoundPlayer backgroundMusic) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(20*32, 20*32));
        this.background = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        // Dùng GridBagLayout để căn giữa các nút.
        this.setLayout(new GridBagLayout());
        // Dùng GridBagConstraints để căn chỉnh khoảng cách giữa các nút (insets).
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        
        this.usedShip = new ReadAndAddFile("used_ship.txt");
        if (this.usedShip.isEmpty_UsedShip()) {
            this.usedShip.savePurchasedShip("ship_flying.gif");
        }
        else{
            usedShipImage = new ImageIcon(getClass().getResource(usedShip.getActiveShip())).getImage();
        }

        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./font_words/PressStart2P-Regular.ttf")).deriveFont(20f);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }    

        this.backgroundMusic = backgroundMusic;

        JLabel lblTitle = new JLabel("SPACE INVADERS");
        lblTitle.setFont(pixelFont.deriveFont(40f));
        lblTitle.setForeground(Color.WHITE);
        
        JButton btnPlay = createButton("START GAME");
        JButton btnShop = createButton("SHOP");
        JButton btnSetting = createButton("SETTING");
        JButton btnExit = createButton("EXIT");

        JPanel shipPanel = new JPanel();
        shipPanel.setPreferredSize(new Dimension(250, 250));
        shipPanel.setOpaque(false);
        shipPanel.setBorder(new RoundedBorder(30));
        
        JLabel shipLabel = new JLabel();
        shipLabel.setIcon(new ImageIcon(usedShipImage)); // Đặt ảnh vào JLabel
        shipLabel.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa ảnh
        shipLabel.setVerticalAlignment(SwingConstants.CENTER);   // Căn giữa ảnh

        shipPanel.setLayout(new GridBagLayout());
        shipPanel.add(shipLabel);

        btnPlay.addActionListener(e -> startGame());
        btnShop.addActionListener(e -> openShop());
        btnSetting.addActionListener(e -> openSettings());
        btnExit.addActionListener(e -> exitGame());
        

        gbc.insets = new Insets(10, 0, 60, 0);
        gbc.gridwidth = 2; // Chiếm 2 cột
        gbc.gridy = 0; this.add(lblTitle, gbc);
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1; this.add(btnPlay, gbc);
        gbc.gridy = 2; this.add(btnShop, gbc);
        gbc.gridy = 3; this.add(btnSetting, gbc);
        gbc.gridy = 4; this.add(btnExit, gbc);
        
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 4; 
        gbc.insets = new Insets(0, 0, 0, 0);
        this.add(shipPanel, gbc);
    }
    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(pixelFont);
        button.setForeground(Color.white);
        button.setBorder(new RoundedBorder(20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false); // Ẩn hiệu ứng viền khi nhấn
        
        return button;
    }
    private void startGame() {
        frame.getContentPane().removeAll();
        SpaceInvaders game = new SpaceInvaders(backgroundMusic);
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);
    }

    private void openShop() {
        frame.getContentPane().removeAll(); 
        frame.add(new Shop(frame, backgroundMusic));  
        frame.revalidate();  // Cập nhật layout mới
        frame.repaint();     // Vẽ lại giao diện mới
    }

    private void openSettings() {
        Setting setting = new Setting(frame, this, this.backgroundMusic);
        frame.getContentPane().removeAll(); // Xóa tất cả các thành phần hiện tại
        frame.add(setting); // Thêm thành phần mới vào frame
        frame.revalidate(); // Cập nhật layout mới
        frame.repaint(); // Vẽ lại giao diện mới
    }

    private void exitGame(){
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
