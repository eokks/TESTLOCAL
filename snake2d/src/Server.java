import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<Integer> direction;
    static ArrayList<Socket> clientSocket; //сокет для общения
    static ServerSocket server; // серверсокет
    static ArrayList<BufferedReader> in; // поток чтения из сокета
    static ArrayList<BufferedWriter> out; // поток записи в сокет

    public static void main(String[] args) {

        Thread playerListener1 = new Thread(() -> {
            try {
                direction.set(0, Integer.valueOf(in.get(0).readLine())); // ждём пока клиент что-нибудь нам напишет
                for(int i = 0; i <2; i++) {
                    // не долго думая отвечает клиенту
                    out.get(i).write(direction.get(i) + " " + direction.get((i+1)%2) + "\n");
                    out.get(i).flush(); // выталкиваем все из буфера
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        Thread playerListener2 = new Thread(() -> {
                try {
                    direction.set(1, Integer.valueOf(in.get(1).readLine())); // ждём пока клиент что-нибудь нам напишет
                    // не долго думая отвечает клиенту
                    for(int i = 0; i <2; i++) {
                        out.get(i).write(direction.get(i) + " " + direction.get((i+1)%2) + "\n");
                        out.get(i).flush(); // выталкиваем все из буфера
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        try {
            try  {
                int port =80;
                server = new ServerSocket(port); // серверсокет прослушивает порт 4004
                System.out.println(port); // хорошо бы серверу
                //   объявить о своем запуске
                for(int i = 0; i < 2; i++) {
                    clientSocket.add(server.accept()) ; // accept() будет ждать пока кто-нибудь не захочет подключиться
                    direction.add(0);
                    in.add(new BufferedReader(new InputStreamReader(clientSocket.get(i).getInputStream())));
                    // и отправлять
                    out.add(new BufferedWriter(new OutputStreamWriter(clientSocket.get(i).getOutputStream())));
                     // установив связь и воссоздав сокет для общения с клиентом можно перейти
                    // к созданию потоков ввода/вывода.
                    // теперь мы можем принимать сообщения
                }for(int i = 0; i < 2; i++) {
                    out.get(i).write("done\n");
                    out.get(i).flush(); // выталкиваем все из буфера
                }
                playerListener1.start();
                playerListener2.start();
            } finally {
                for(int i = 0; i< 2;i++) {
                    clientSocket.get(i).close();
                    // потоки тоже хорошо бы закрыть
                    in.get(i).close();
                    out.get(i).close();
                }
                System.out.println("Сервер закрыт!");
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}