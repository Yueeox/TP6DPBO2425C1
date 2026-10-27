import javax.swing.*;

public class App {
    public static void main(String[] args) {

        // membuat frame dan memanggil semua kelas yang dibuuhkan untuk dijalankan
        JFrame frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        frame.setSize(360, 640);
        frame.setLocationRelativeTo((null));
        frame.setResizable(false);

        Logic logika = new Logic();
        View tampilan = new View(logika);
        logika.setView(tampilan);

        frame.add(tampilan);
        frame.pack();
        frame.setVisible(true);
        tampilan.requestFocusInWindow();

    }
}

