import javax.swing.*;
import java.util.ArrayList;

public class Snake {
    public int headX;
    public int headY;
    public int time;
    public ArrayList<JLabel> snakeparts;
    public byte life = 0;
    public int xplus = 0;
    public int yplus = 0;
    public int direction;

    Snake(int x, int y, int time, int partAmount) {
        this.headX = x;
        this.headY = y;
        this.time = time;
        snakeparts = new ArrayList<>();
        snakeparts.add(new JLabel(new ImageIcon("snake.jpg")));
        snakeparts.get(0).setBounds(x,y,30,30);
        Main.window.add(snakeparts.get(0));
        for (int i = 1; i < partAmount; i++) {
            snakeparts.add(new JLabel(new ImageIcon("snake_body.jpg")));
            snakeparts.get(i).setBounds(x,y+(i*30),30,30);
            Main.window.add(snakeparts.get(i));
        }
    }

    public void setHeadLoc(int x, int y){
        snakeparts.get(0).setLocation(x,y);
        headX = x; headY = y;
    }

    public void changeIcon(int index, String iconName){
        snakeparts.get(index).setIcon(new ImageIcon(iconName));
    }


    public void changePlus(int xplus, int yplus, int direction) {
        if (life == 0) {
            life = (byte) 2;
        }
        this.xplus = xplus;
        this.yplus = yplus;
        this.direction = direction;
    }

    public ArrayList makeLocationsArray() {
        ArrayList<Location> locations = new ArrayList<>();
        for (int snakepartIndex = 0; snakepartIndex < snakeparts.size(); snakepartIndex++) {
            locations.add(new Location(snakeparts.get(snakepartIndex).getX(),snakeparts.get(snakepartIndex).getY()));
        }
        return locations;
    }
}
