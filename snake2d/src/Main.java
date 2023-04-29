import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

class Main extends JFrame{

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    static ArrayList<Snake> snakes = new ArrayList<>();
    public static JFrame window = new JFrame();
    static JLabel apple =new JLabel(new ImageIcon("snake.jpg"));
    static HashMap<Integer, String> sprites_head = new HashMap<>();
    static HashMap<Integer, String> sprites_body = new HashMap<>();
    static int applex = 450; static int appley = 450;//стартовые координаты яблока
    static ArrayList<JLabel> ochko = new ArrayList<>();
    public static void placeApple(int snakeindex, int lastx,int lasty){
            applex = (int)(Math.random() * 900);
            appley = (int)(Math.random() * 690);
            applex = applex - applex % 30;
            appley = appley - (appley % 30);
            apple.setLocation(applex,appley);
            snakes.get(snakeindex).snakeparts.add(new JLabel(new ImageIcon("snake_body.jpg")));
            window.add(snakes.get(snakeindex).snakeparts.get(snakes.get(snakeindex).snakeparts.size()-1));
            snakes.get(snakeindex).snakeparts.get(snakes.get(snakeindex).snakeparts.size()-1).setBounds( lastx, lasty,30,30);
            snakes.get(snakeindex).time = snakes.get(snakeindex).time * 19 / 20;
    }

    public static void moveCharacter(int snakeindex){

        if(snakes.get(snakeindex).life == 2){
            int xp = snakes.get(snakeindex).xplus;
            int yp = snakes.get(snakeindex).yplus;
            ArrayList<Location> locations = snakes.get(snakeindex).makeLocationsArray();

            for(int timer = 1; timer < 11; timer++){
                for(int snakepart = snakes.get(snakeindex).snakeparts.size()-1; snakepart > 0; snakepart--){
                    int myX =locations.get(snakepart).x;
                    int myY =locations.get(snakepart).y;
                    int needX =locations.get(snakepart-1).x;
                    int needY =locations.get(snakepart-1).y;
                    snakes.get(snakeindex).snakeparts.get(snakepart).setLocation((myX/10*(10-timer)) + needX/10*timer,(myY/10*(10-timer)) + needY/10*timer);
                }

                snakes.get(snakeindex).setHeadLoc(locations.get(0).x/10*(10-timer) +(locations.get(0).x+xp)/10*timer,locations.get(0).y/10*(10-timer) +(locations.get(0).y+yp)/10*timer);
                try {
                    Thread.sleep(snakes.get(snakeindex).time/10);
                } catch (InterruptedException ignored) {}
            }

//            for (int i = snakes.get(snakeindex).snakeparts.size()-2;i>0;i--){
//                int[] loc = new int[2];
//                for(int ind = 0;ind<2;ind++) {
//                    if (snakes.get(snakeindex).snakeparts.get(i).getX() - snakes.get(snakeindex).snakeparts.get(i - 1 +(ind*2)).getX() == 0) {
//                        if (snakes.get(snakeindex).snakeparts.get(i).getY() - snakes.get(snakeindex).snakeparts.get(i - 1 + (ind*2)).getY() == 30) {
//                            loc[ind] = 1;
//                        } else {
//                            loc[ind] = 3;
//                        }
//                    } else {
//                        if (snakes.get(snakeindex).snakeparts.get(i).getX() - snakes.get(snakeindex).snakeparts.get(i - 1 + (ind*2)).getX() == 30) {
//                            loc[ind] = 2;
//                        } else {
//                            loc[ind] = 4;
//                        }
//                    }
//                }
//                if (loc[0]>loc[1]){
//                    int dop = loc[0];
//                    loc[0] = loc[1];
//                    loc[1] = dop;
//                }
//                snakes.get(snakeindex).changeIcon(i+1,sprites_body.get(loc[0]*10+loc[1]));
//                snakes.get(snakeindex).changeIcon(i,sprites_body.get(loc[0]*10+loc[1]));
//            }

            if(snakes.get(snakeindex).headX >= applex - 60 &&snakes.get(snakeindex).headX <= applex + 60 && snakes.get(snakeindex).headY >= appley - 60 && snakes.get(snakeindex).headY <= appley + 60 ){
                snakes.get(snakeindex).changeIcon(0, sprites_head.get(snakes.get(snakeindex).direction * 10 + 1));
                if(snakes.get(snakeindex).headY == appley && snakes.get(snakeindex).headX == applex){
                    ochko.get(snakeindex).setText(String.valueOf(snakes.get(snakeindex).snakeparts.size()));
                    placeApple(snakeindex,locations.get(locations.size() - 1).x,locations.get(locations.size() - 1).y);
                }
            } else {
                System.out.println(sprites_head.get(snakes.get(snakeindex).direction * 10));
                snakes.get(snakeindex).changeIcon(0, sprites_head.get(snakes.get(snakeindex).direction * 10));
            }
            if(snakes.get(snakeindex).headX <0 || snakes.get(snakeindex).headX > 870 || snakes.get(snakeindex).headY < 0 || snakes.get(snakeindex).headY > 690){
//                System.out.println(snakes.get(snakeindex).time);
                for (JLabel snakepart : snakes.get(snakeindex).snakeparts) {
                    snakepart.setIcon(new ImageIcon("snake_dead.jpg"));
                }
                snakes.get(snakeindex).life = 1;
            }
        }
    }

    public static void main(String[] args){
        Thread changeplus = new Thread(() -> {
            try {
                String a =  in.readLine();
                String directions[]  =a.split(" "); // ждём, что скажет сервер
                for(int i = 0; i<2 ; i++){
                    int dir = Integer.parseInt(directions[i]);
                    if(dir % 2 == 0){
                        snakes.get(i).changePlus((dir-3)*30,0,dir);
                    }else{
                        snakes.get(i).changePlus(0,(dir-2)*30,dir);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        try {
            try {
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                clientSocket = new Socket("localhost", 80); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));// читать соообщения с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));// писать туда же
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String done =  in.readLine();
                changeplus.start();
            } finally { // в любом случае необходимо закрыть сокет и потоки
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }


        Thread thread1 = new Thread(() -> {
            while (true){
                moveCharacter(0);
            }
        });
        Thread thread2 = new Thread(() -> {
            while (true){
                moveCharacter(1);
            }
        });


        Thread snakeListener1 = new Thread(() -> window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W -> {
                        try {
                            out.write("1"+"\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    case KeyEvent.VK_A -> {
                        try {
                            out.write("2"+"\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    case KeyEvent.VK_S -> {
                        try {
                            out.write("3"+"\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    case KeyEvent.VK_D -> {
                        try {
                            out.write("4"+"\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }));

        window.setSize(900,750);
        window.setLayout(null);
        window.setLocationRelativeTo(null);
        for(int i =0;i<2;i++){
            snakes.add(new Snake(i*210,0,200,5));

//            play.add((byte) 0);
            ochko.add(new JLabel(String.valueOf(snakes.get(i).snakeparts.size())));
            window.add(ochko.get(i));
            ochko.get(i).setBounds(i * 210, 210,30,30);
//            time.add(200);
//            xplus.add(0);
//            yplus.add(0);
//            characters.add(new ArrayList<>());
//            characters.get(i).add(new JLabel(new ImageIcon("snake.jpg")));
//            int basex = 210 * i;
//            characters.get(i).get(0).setBounds(basex,basex,30,30);
//            for(int f = 1; f < 5; f++){
//                int basey = basex + f * 30;
//                characters.get(i).add(new JLabel(new ImageIcon("snake_body.jpg")));
//                window.add(characters.get(i).get(f));
//                characters.get(i).get(f).setBounds(basex,basey,30,30);
//                characters.get(i).get(f).setLocation(basex,basey);
//            }
//            window.add(characters.get(i).get(0));
        }
        apple.setIcon(new ImageIcon("apple.jpg"));
        window.add(apple);
        apple.setBounds(applex,appley,30,30);


        sprites_head.put(10, "snake_up_noapple.jpg");
        sprites_head.put(11, "snake_up_apple.jpg");
        sprites_head.put(20, "snake_left_noapple.jpg");
        sprites_head.put(21, "snake_left_apple.jpg");
        sprites_head.put(30, "snake_down_noapple.jpg");
        sprites_head.put(31, "snake_down_apple.jpg");
        sprites_head.put(40, "snake_right_noapple.jpg");
        sprites_head.put(41, "snake_right_apple.jpg");



        sprites_body.put(12, "snake_body_ul.jpg");
        sprites_body.put(13, "snake_body_ud.jpg");
        sprites_body.put(14, "snake_body_ur.jpg");
        sprites_body.put(23, "snake_body_ld.jpg");
        sprites_body.put(24, "snake_body_lr.jpg");
        sprites_body.put(34, "snake_body_dr.jpg");


        window.setTitle("snake");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(new JLabel(""));
        thread1.start();
        thread2.start();
        snakeListener1.start();
        window.setFocusable(true);
        window.setVisible(true);
    }
}