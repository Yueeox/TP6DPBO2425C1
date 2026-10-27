import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.Font;

public class View extends JPanel{

    int width = 360;
    int height = 640;

    private JLabel scoreLabel;
    private JLabel waveLabel;
    private Logic logic;

    public View(Logic logic){
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);

        setFocusable(true);
        addKeyListener(logic);

        // Setup Label Skor
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(10, 10, 200, 30); // Posisi pojok kiri atas
        this.setLayout(null); // Gunakan absolute layout

        waveLabel = new JLabel("Wave: 0");
        waveLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        waveLabel.setForeground(Color.WHITE);
        waveLabel.setBounds(10, 40, 200, 30); // Posisi pojok kiri atas
        this.setLayout(null); // Gunakan absolute layout
        this.add(waveLabel);
        this.add(scoreLabel);

    }

    @Override public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (logic.getBackground() != null) {
            g.drawImage(logic.getBackground(), 0, 0, width, height, null);
        } else {
            // Jika gambar gagal dimuat, fallback ke warna cyan
            g.setColor(Color.cyan);
            g.fillRect(0, 0, width, height);
        }
        // menggambar player
        Player player = logic.getPlayer();
        if(player != null){
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        }

        // gambar pipa
        ArrayList<Pipe> pipes = logic.getPipes();
        if(pipes != null){
            for(int i = 0; i < pipes.size(); i++){
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
            }
        }
        // 3. Update Label Skor
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + logic.getScore());
        }

        if (waveLabel != null) {
            waveLabel.setText("Wave: " + logic.score / 5);
        }

        // Menampilkan teks sebelum game dimulai
        if (!logic.isStarted && !logic.isGameOver()) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            g.drawString("Press SPACE to Start", 40, height / 2);
        }

        // 4. Gambar Layar Game Over
        if (logic.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 150)); // Background gelap transparan
            g.fillRect(0, 0, width, height);

            g.setColor(Color.RED);
            g.setFont(new Font("Monospaced", Font.BOLD, 36));
            g.drawString("GAME OVER", 80, height / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.drawString("Press 'R' to Restart", 55, height / 2 + 30);
        }
    }
}
