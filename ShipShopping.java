import java.awt.Image;

import javax.swing.ImageIcon;

public class ShipShopping {
    private int price;
    private ImageIcon image;
    private String imagePath;
    public ShipShopping(int price, String imagePath) {
        this.price = price;
        this.image = new ImageIcon(getClass().getResource(imagePath));
        this.imagePath = imagePath;
    }
    public String getImagePath() { return imagePath; }
    public int getPrice() { return price; }
    public ImageIcon getImage() { return image; }
}
