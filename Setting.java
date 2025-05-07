import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Setting extends JPanel {
    private JSlider musicSlider, soundSlider;
    private int musicVolume, soundVolume = 75;
    private boolean isMusicMuted, isSoundMuted = false;
    private boolean isSaved = false;
    private JToggleButton muteMusicButton, muteSoundButton;
    private JButton backButton, saveButton;
    private Image background;
    private Font pixelFont;
    private SoundPlayer soundPlayer;
    private ReadAndAddFile musicFile, soundFile;
    public Setting(JFrame jFrame, JPanel menu, SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        musicFile = new ReadAndAddFile("./music.txt");
        soundFile = new ReadAndAddFile("./sound.txt");
        
        setLayout(new GridBagLayout());
        this.background = new ImageIcon(getClass().getResource("./background.jpg")).getImage();


        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./font_words/PressStart2P-Regular.ttf")).deriveFont(20f);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }    

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 150)); // Set transparent background
        panel.setBorder(new RoundedBorder(20)); // Set border color and thickness
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new java.awt.Dimension(400, 500)); // Set preferred size for the panel
        panel.setOpaque(false); // Make the panel transparent
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(pixelFont.deriveFont(40f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new java.awt.Dimension(400, 40));
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 2;
        titleGbc.insets = new Insets(20, 10, 50, 10);
        titleGbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, titleGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // MUSIC
        JLabel musicLabel = new JLabel("Music:");
        musicLabel.setFont(pixelFont.deriveFont(20f));
        musicLabel.setForeground(Color.WHITE);
        musicLabel.setHorizontalAlignment(SwingConstants.LEFT);
        musicLabel.setPreferredSize(new java.awt.Dimension(150, 40));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(musicLabel, gbc);

        JPanel musicPanel = new JPanel();
        musicPanel.setBorder(new RoundedBorder(20)); // Set border color and thickness
        musicPanel.setLayout(new GridBagLayout());
        musicPanel.setPreferredSize(new java.awt.Dimension(400, 100)); // Set preferred size for the panel
        musicPanel.setOpaque(false); // Make the panel transparent
        GridBagConstraints musicGbc = new GridBagConstraints();
        musicGbc.insets = new Insets(10, 10, 10, 10);

        // Label for volume slider
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(pixelFont.deriveFont(15f));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        volumeLabel.setPreferredSize(new java.awt.Dimension(150, 40));
        musicGbc.gridx = 0;
        musicGbc.gridy = 0;
        musicPanel.add(volumeLabel, musicGbc);

        // Volume slider
        this.musicSlider = new JSlider(50, 100); // Default volume set to 50%
        musicSlider.setOpaque(false); // Make the slider background transparent
        musicSlider.setForeground(Color.WHITE); // Set the slider's foreground color
        musicSlider.setValue(musicFile.readVolumeMusic()); // Set the slider to the saved volume value
        musicVolume = musicSlider.getValue();

        musicGbc.gridx = 1;
        musicGbc.gridy = 0;
        musicGbc.weightx = 1.0; // Allow the slider to grow horizontally
        musicGbc.fill = GridBagConstraints.HORIZONTAL; // Make the slider fill the available space
        musicPanel.add(musicSlider, musicGbc);
        musicSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = musicSlider.getValue();
                musicVolume = value; // Assign the value to the musicVolume field
                System.out.println("Volume: " + value);
                soundPlayer.setVolume(value); // Set the volume in the SoundPlayer class
            }
        });
        
        // Mute button
        this.muteMusicButton = new JToggleButton("Mute");
        muteMusicButton.setFont(pixelFont.deriveFont(20f));
        muteMusicButton.setForeground(Color.WHITE);
        muteMusicButton.setFocusable(false);
        muteMusicButton.setBorder(new RoundedBorder(20));
        muteMusicButton.setContentAreaFilled(false);
        muteMusicButton.setFocusPainted(false);
        muteMusicButton.setPreferredSize(new java.awt.Dimension(150, 40));
        if (musicFile.readStateMusic()) {
            muteMusicButton.setSelected(true);
            muteMusicButton.setText("Unmute");
            soundPlayer.stop(); // Stop the music if it is muted
            musicSlider.setEnabled(false); // Disable the slider if muted
        } else {
            muteMusicButton.setText("Mute");
            soundPlayer.setVolume(musicVolume); // Set the volume in the SoundPlayer class
        }
        muteMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (muteMusicButton.isSelected()){
                        muteMusicButton.setText("Unmute");
                        isMusicMuted = true;
                        soundPlayer.stop();
                        musicSlider.setEnabled(false);
                } else {
                        
                        muteMusicButton.setText("Mute");
                        isMusicMuted = false;
                        soundPlayer.setVolume(musicVolume); // Set the volume in the SoundPlayer class
                        soundPlayer.loop(); // Start playing the music again
                        musicSlider.setEnabled(true);
                }
            }
        });
        musicGbc.gridx = 0;
        musicGbc.gridy = 1;
        musicGbc.gridwidth = 2;
        musicGbc.fill = GridBagConstraints.BOTH; // Allow expansion in both directions
        musicGbc.weighty = 0.1; // Allow vertical expansion
        musicPanel.add(muteMusicButton, musicGbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1;  // Thay đổi từ 1 thành 0
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(musicPanel, gbc);

        //SOUND
        JLabel soundLabel = new JLabel("Sound:");
        soundLabel.setFont(pixelFont.deriveFont(20f));
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setHorizontalAlignment(SwingConstants.LEFT);
        soundLabel.setPreferredSize(new java.awt.Dimension(150, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(soundLabel, gbc);

        JPanel soundPanel = new JPanel();
        soundPanel.setBorder(new RoundedBorder(20)); // Set border color and thickness
        soundPanel.setLayout(new GridBagLayout());
        soundPanel.setPreferredSize(new java.awt.Dimension(400, 100)); // Set preferred size for the panel
        soundPanel.setOpaque(false); // Make the panel transparent
        GridBagConstraints soundGbc = new GridBagConstraints();
        soundGbc.insets = new Insets(10, 10, 10, 10);

        // Label for volume slider
        volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(pixelFont.deriveFont(15f));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        volumeLabel.setPreferredSize(new java.awt.Dimension(150, 40));
        soundGbc.gridx = 0;
        soundGbc.gridy = 0;
        soundPanel.add(volumeLabel, soundGbc);

        // Volume slider
        this.soundSlider = new JSlider(50, 100); // Default volume set to 50%
        soundSlider.setOpaque(false); // Make the slider background transparent
        soundSlider.setForeground(Color.WHITE); // Set the slider's foreground color
        soundSlider.setValue(soundFile.readVolumeSound()); // Set the slider to the saved volume value

        soundGbc.gridx = 1;
        soundGbc.gridy = 0;
        soundGbc.weightx = 1.0; // Allow the slider to grow horizontally
        soundGbc.fill = GridBagConstraints.HORIZONTAL; // Make the slider fill the available space
        soundPanel.add(soundSlider, soundGbc);
        soundSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = soundSlider.getValue();
                soundVolume = value;
            }
        });
        
        // Mute button
        this.muteSoundButton = new JToggleButton("Mute");
        muteSoundButton.setFont(pixelFont.deriveFont(20f));
        muteSoundButton.setForeground(Color.WHITE);
        muteSoundButton.setFocusable(false);
        muteSoundButton.setBorder(new RoundedBorder(20));
        muteSoundButton.setContentAreaFilled(false);
        muteSoundButton.setFocusPainted(false);
        muteSoundButton.setPreferredSize(new java.awt.Dimension(150, 40));
        if (soundFile.readStateSound()) {
            muteSoundButton.setSelected(true);
            muteSoundButton.setText("Unmute");
            soundSlider.setEnabled(false); // Disable the slider if muted
        } else {
            muteSoundButton.setText("Mute");
            soundSlider.setEnabled(true); 
        }
        muteSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (muteSoundButton.isSelected()){
                        muteSoundButton.setText("Unmute");
                        isSoundMuted = true;
                        soundSlider.setEnabled(false);
                    } else {
                        muteSoundButton.setText("Mute");
                        isSoundMuted = false;
                        soundSlider.setEnabled(true);
                }
            }
        });
        soundGbc.gridx = 0;
        soundGbc.gridy = 1;
        soundGbc.gridwidth = 2;
        soundGbc.fill = GridBagConstraints.BOTH; // Allow expansion in both directions
        soundGbc.weighty = 0.1; // Allow vertical expansion
        soundPanel.add(muteSoundButton, soundGbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1;  // Thay đổi từ 1 thành 0
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(soundPanel, gbc);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(pixelFont.deriveFont(20f));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusable(false);
        saveButton.setBorder(new RoundedBorder(20));
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSaved = true;
                musicFile.saveVolumeMusic(musicVolume, isMusicMuted); // Save the volume and mute state
                soundFile.saveVolumeSound(soundVolume, isSoundMuted); // Save the volume and mute state
                System.out.println("Settings saved!");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(saveButton, gbc);

        // Back button
        this.backButton = new JButton("Back");
        backButton.setFont(pixelFont.deriveFont(20f));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusable(false);
        backButton.setBorder(new RoundedBorder(20));
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        this.backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSaved) {
                    jFrame.getContentPane().removeAll();
                    jFrame.add(menu);
                    jFrame.revalidate();
                    jFrame.repaint();
                } else {
                    int result = JOptionPane.showConfirmDialog(null, "Do you want to save the settings?", "System", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        musicFile.saveVolumeMusic(musicVolume, isMusicMuted); // Save the volume and mute state
                        soundFile.saveVolumeSound(soundVolume, isSoundMuted); // Save the volume and mute state
                        System.out.println("Settings saved!");
                        jFrame.getContentPane().removeAll();
                        jFrame.add(menu);
                        jFrame.revalidate();
                        jFrame.repaint();
                    } else if (result == JOptionPane.NO_OPTION) {
                        soundPlayer.setVolume(musicFile.readVolumeMusic());
                        if (musicFile.readStateMusic()) {
                            soundPlayer.stop(); // Stop the music if it is muted
                        } else {
                            soundPlayer.loop(); // Start playing the music again
                        }
                        jFrame.getContentPane().removeAll();
                        jFrame.add(menu);
                        jFrame.revalidate();
                        jFrame.repaint();
                    }
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(this.backButton, gbc);
        this.add(panel);
    }
    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);

    }
}
