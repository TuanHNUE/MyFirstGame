import java.awt.List;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadAndAddFile {
    private String filePath;
    private int maxScore;
    public ReadAndAddFile (){}

    public ReadAndAddFile (String filePath){
        this.filePath = filePath;
    }
    public void addCoins(int coins) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(String.valueOf(coins)); // Ghi số xu hiện tại vào file
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveCoins(int coins) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(String.valueOf(coins)); // Ghi số xu vào file
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int readCoins() {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            int totalCoins = 0;
            
            if (scanner.hasNextLine()) {
                String coin = scanner.nextLine();
                System.out.println(coin);
                totalCoins += Integer.parseInt(coin);
            }
            scanner.close();
            return totalCoins;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public String ReadFile_highestScore(){
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            int max = 0;
            while (scanner.hasNextLine()){
                max = Integer.parseInt(scanner.nextLine());
            }
            this.maxScore = max;
            scanner.close();
            String maxString = "" + max;
            return maxString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "" + 0;
        }
    }
    public void AddFile_highestScore(int highestScore){
        try {
            FileWriter writer = new FileWriter(filePath, true);
            if (highestScore > maxScore) {
                writer.write(highestScore + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void savePurchasedShip (String filePathShip){
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(filePathShip + "\n");
            writer.close();
            System.out.println("Đã lưu tàu: " + filePathShip);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public ArrayList<String> getPurchasedShip(){
        ArrayList purchasedShips = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                purchasedShips.add(line);   
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return purchasedShips;
    }
    // Kiểm tra xem tàu đã mua chưa
    public boolean isShipPurchased(String shipName) {
        return readFile_nature().contains(shipName);
    }
    public void setActiveShip(String shipName){
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println(shipName); // Ghi đè file với tên tàu mới
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    public String getActiveShip(){
        try {
            Scanner scanner = new Scanner(new File(filePath));
            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return null;
        }
        
    }
    // Kiểm tra tàu đang sử dụng
    public boolean isShipUsed(String shipName) {
        ArrayList<String> data = readFile_nature();
        return !data.isEmpty() && data.get(0).equals(shipName);
    }
    public boolean isEmpty_UsedShip(){
        ArrayList<String> data = readFile_nature();
        return data.isEmpty();
    }
    private ArrayList<String> readFile_nature(){
        ArrayList<String> data = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                data.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    //Music & Sound
    public void saveVolumeMusic(int volume, boolean state) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(volume + " - " + state); // Ghi âm lượng và trạng thái tắt tiếng vào file
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void saveVolumeSound(int volume, boolean state) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(volume + " - " + state); // Ghi âm lượng và trạng thái tắt tiếng vào file
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public int readVolumeMusic() {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                return 75; // File không có dòng nào → trả về mặc định
            }
            String line = scanner.nextLine();
            String[] parts = line.split(" - ");
            int volume = Integer.parseInt(parts[0]);
            return volume;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 75; // Trả về giá trị mặc định nếu không tìm thấy file
        }
    }
    public int readVolumeSound() {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                return 75; // File không có dòng nào → trả về mặc định
            }
            String line = scanner.nextLine();
            String[] parts = line.split(" - ");
            int volume = Integer.parseInt(parts[0]);
            return volume;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 75; // Trả về giá trị mặc định nếu không tìm thấy file
        }
    }
    public boolean readStateMusic() {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                return false; // File không có dòng nào → trả về mặc định
            }
            String line = scanner.nextLine();
            String[] parts = line.split(" - ");
            boolean state = Boolean.parseBoolean(parts[1]);
            return state;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false; // Trả về giá trị mặc định nếu không tìm thấy file
        }
    }
    public boolean readStateSound() {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                return false; // File không có dòng nào → trả về mặc định
            }
            String line = scanner.nextLine();
            String[] parts = line.split(" - ");
            boolean state = Boolean.parseBoolean(parts[1]);
            return state;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false; // Trả về giá trị mặc định nếu không tìm thấy file
        }
    }
}

