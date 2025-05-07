import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        ReadAndAddFile musicFile = new ReadAndAddFile("music.txt");

        //window variables
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; // 32 * 16 = 512px     
        int boardHeight = tileSize * rows; // 32 * 16 = 512px
        
        SoundPlayer backgroundMusic = new SoundPlayer("space_invaders.wav");
        JFrame frame = new JFrame("Space Invaders");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        backgroundMusic.setVolume(musicFile.readVolumeMusic());
        if (musicFile.readStateMusic()) {
            backgroundMusic.stop();
        }
        else{
            backgroundMusic.loop();
        }
            
        Menu menu = new Menu(frame, backgroundMusic);
        frame.add(menu);
        frame.pack();
        frame.setVisible(true);
    }
}