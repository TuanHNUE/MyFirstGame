import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Shop extends JPanel{
    private ArrayList<ShipShopping> ships;
    private int playerCoints;
    private JLabel lblCoints;
    private Font pixelFont;
    private Image background;
    private SoundPlayer backgroundMusic;
    private ReadAndAddFile purchedShip, usedShip, coinsFile;
    public Shop(JFrame frame, SoundPlayer backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
        this.purchedShip = new ReadAndAddFile("./purchased_ship.txt");
        this.usedShip = new ReadAndAddFile("./used_ship.txt");
        this.coinsFile = new ReadAndAddFile("./coins.txt");
        this.playerCoints = coinsFile.readCoins();
        if (this.usedShip.isEmpty_UsedShip()) {
            this.usedShip.setActiveShip("./ship_flying.gif");
        }
        this.background = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        this.ships = new ArrayList<ShipShopping>();
        this.ships.add(new ShipShopping(0, "./ship_flying.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_1_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_2_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_4_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_5_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_6_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_7_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_8_transparent.gif"));
        this.ships.add(new ShipShopping(50000, "./ShipPictures/ship_9_transparent.gif"));
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./font_words/PressStart2P-Regular.ttf")).deriveFont(20f);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }    
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc_outside = new GridBagConstraints();
        gbc_outside.gridx = 0;

        this.lblCoints = new JLabel("SHOP", SwingConstants.CENTER);
        this.lblCoints.setFont(pixelFont.deriveFont(50f));
        this.lblCoints.setForeground(Color.WHITE);
        gbc_outside.gridy = 0;
        gbc_outside.insets = new Insets(30, 0, 20, 0);
        this.add(lblCoints, gbc_outside);

        this.lblCoints = new JLabel("Coins: " + this.playerCoints, SwingConstants.CENTER);
        this.lblCoints.setFont(pixelFont.deriveFont(20f));
        this.lblCoints.setForeground(Color.WHITE);
        gbc_outside.gridy = 1;
        gbc_outside.insets = new Insets(0, 0, 30, 0);
        this.add(lblCoints, gbc_outside);

        JPanel shipsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        shipsPanel.setOpaque(false);
        shipsPanel.setBorder(new RoundedBorder(30));
        shipsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));    
        shipsPanel.setBackground(new Color(0, 0, 0, 150));
        
        for (ShipShopping ship : ships){
            JPanel shipPanel = new JPanel(new GridBagLayout());
            shipPanel.setOpaque(false);
            shipPanel.setBorder(new RoundedBorder(30));

            shipPanel.setBackground(new Color(50, 50, 50, 200)); 
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel imageLabelShip = new JLabel();
            imageLabelShip.setPreferredSize(new Dimension(100, 100)); 
            imageLabelShip.setIcon(new ImageIcon(ship.getImage().getImage().getScaledInstance(70, 60, Image.SCALE_DEFAULT)));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.WEST;
            shipPanel.add(imageLabelShip, gbc);

            
            boolean isPurchased = purchedShip.isShipPurchased(ship.getImagePath());
            boolean isUsed = usedShip.isShipPurchased(ship.getImagePath());

            if (ship.getPrice() > 0){
                JLabel lblShip = new JLabel(ship.getPrice() + " Coins");
                lblShip.setFont(pixelFont.deriveFont(15f));
                lblShip.setHorizontalAlignment(SwingConstants.CENTER);
                lblShip.setForeground(Color.WHITE);
                // lblShip.setPreferredSize(new Dimension(100, 50));

                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                shipPanel.add(lblShip, gbc);
                if (isUsed) {
                    JLabel lblUsing = new JLabel("Using");
                    lblUsing.setFont(pixelFont.deriveFont(15f));
                    lblUsing.setForeground(Color.GREEN);
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    shipPanel.add(lblUsing, gbc);
                } else if (isPurchased) {
                    JButton btnUse = createButton("Use");
                    btnUse.addActionListener(e -> setActiveShip(ship));
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    shipPanel.add(btnUse, gbc);
                } else {
                    JButton btnBuy = createButton("Buy");
                    btnBuy.addActionListener(e -> buyShip(ship, btnBuy, shipPanel));
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    shipPanel.add(btnBuy, gbc);
                }
            }
            else{
                JLabel lblShip = new JLabel("Default");
                lblShip.setFont(pixelFont.deriveFont(15f));
                lblShip.setHorizontalAlignment(SwingConstants.CENTER);
                lblShip.setForeground(Color.WHITE);
                // lblShip.setPreferredSize(new Dimension(100, 50));

                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                shipPanel.add(lblShip, gbc);

                if (isUsed) {
                    JLabel lblUsing = new JLabel("Using");
                    lblUsing.setFont(pixelFont.deriveFont(15f));
                    lblUsing.setForeground(Color.GREEN);
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    shipPanel.add(lblUsing, gbc);
                } else {
                    JButton btnUse = createButton("Use");
                    btnUse.addActionListener(e -> setActiveShip(ship));
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    shipPanel.add(btnUse, gbc);
                }
            }

            shipsPanel.add(shipPanel);
        }

        gbc_outside.gridy = 2;
        gbc_outside.insets = new Insets(0, 0, 0, 0);
        this.add(shipsPanel, gbc_outside);

        JButton btnExit = createButton("BACK");
        btnExit.setFont(pixelFont.deriveFont(20f));
        btnExit.setPreferredSize(new Dimension(100, 50));
        btnExit.addActionListener(e -> BackToMenu(frame));
        gbc_outside.gridy = 3;
         
        this.add(btnExit, gbc_outside);
    }
    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    private void buyShip(ShipShopping ship, JButton btnBuy, JPanel shipPanel) {
        if (this.playerCoints >= ship.getPrice()) {
            this.playerCoints -= ship.getPrice();
            this.coinsFile.saveCoins(playerCoints);
            purchedShip.savePurchasedShip(ship.getImagePath());
            this.lblCoints.setText("Coins: " + this.playerCoints);

            shipPanel.remove(btnBuy);
            JButton btnUse = createButton("Use");
            btnUse.addActionListener(e -> setActiveShip(ship));

            // Add the "Use" button with proper GridBagConstraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1; // Same column as the "Buy" button
            gbc.gridy = 1; // Same row as the "Buy" button
            gbc.insets = new Insets(5, 5, 5, 5); // Same insets as other components
            shipPanel.add(btnUse, gbc);

            shipPanel.revalidate();
            shipPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Không đủ tiền!");
        }
    }
    private void setActiveShip(ShipShopping ship){
        usedShip.setActiveShip(ship.getImagePath());
        
        // Cập nhật lại giao diện
        this.removeAll();
        this.revalidate();
        this.repaint();
    
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        frame.getContentPane().removeAll(); // Xóa tất cả component cũ
        Shop newShop = new Shop(frame, backgroundMusic); // Tạo Shop mới
        frame.add(newShop); // Thêm Shop mới vào JFrame
        frame.revalidate(); // Cập nhật giao diện
        frame.repaint(); // Vẽ lại mọi thứ
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 80));
        button.setFont(pixelFont.deriveFont(15f));
        button.setBackground(new Color(0, 0, 0, 100));
        button.setForeground(Color.white);
        button.setBorder(new CompoundBorder(new RoundedBorder(20), new EmptyBorder(5, 10, 5, 10)));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false); // Ẩn hiệu ứng viền khi nhấn
        
        return button;
    }
    private void BackToMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        Menu menu = new Menu(frame, backgroundMusic);
        frame.add(menu);
        frame.pack();
        frame.setVisible(true);
    }
}
