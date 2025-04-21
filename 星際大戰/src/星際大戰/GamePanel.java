package �P�ڤj��;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private int playerX = 400, playerY = 500; // ���a�����m
    private int playerSpeed = 10; // ���a���ʳt��
    private ArrayList<Enemy> enemies; // �ĤH�C��
    private ArrayList<Laser> lasers; // �p�g�C��
    private int score = 0; // ����
    private int level = 1; // ���d
    private int health = 100; // �ͩR�ȡ]��l100�^
    private boolean running = true;
    private boolean canShoot = true; // ����g���N�o
    private long lastShootTime = 0; // �W���g�����ɶ�
    private final long shootCooldown = 200; // �g���N�o�ɶ��]�@��^

    public GamePanel() {
        enemies = new ArrayList<>();
        lasers = new ArrayList<>();
        setFocusable(true);
        addKeyListener(this);
        new Thread(this).start(); // �ҰʹC���D�`��
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK); // �I���]���¦�

        // �e�P�P
        g.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            g.fillRect(x, y, 2, 2);
        }

        // �e���a���� (²��ίx�Ϊ��TIE�԰���)
        g.setColor(Color.GRAY);
        g.fillRect(playerX - 20, playerY - 10, 40, 20); // ����
        g.fillRect(playerX - 40, playerY, 20, 10); // ���l
        g.fillRect(playerX + 20, playerY, 20, 10); // �k�l

        // �e�ĤH
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, 20, 20);
        }

        // �e�p�g
        g.setColor(Color.GREEN);
        for (Laser laser : lasers) {
            g.fillRect(laser.x, laser.y, 4, 10);
        }

        // �e���ƩM���d
        g.setColor(Color.WHITE);
        g.drawString("SCORE: " + score, 10, 20);
        g.drawString("LEVEL: " + level, 10, 40);

        // �e�ͩR�ȱ�
        g.setColor(Color.GREEN);
        g.fillRect(playerX - 20, playerY + 20, health / 2, 5); // �ͩR�ȱ��]�ھ�health�Y��^
        g.setColor(Color.RED);
        g.fillRect(playerX - 20 + health / 2, playerY + 20, (100 - health) / 2, 5); // �l�����ͩR��
    }

    @Override
    public void run() {
        while (running) {
            // �ͦ��ĤH
            if (Math.random() < 0.02) {
                enemies.add(new Enemy((int) (Math.random() * getWidth()), 0));
            }

            // ��s�ĤH��m
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                enemy.y += 3; // �ĤH�V�U����
                if (enemy.y > getHeight()) {
                    enemies.remove(i); // �����W�X�ù����ĤH
                } else {
                    // ²��I���˴��]���a�P�ĤH�^
                    if (Math.abs(enemy.x - playerX) < 30 && Math.abs(enemy.y - playerY) < 30) {
                        health -= 20; // �C���I�����20�ͩR��
                        enemies.remove(i); // �ĤH����
                        if (health <= 0) {
                            running = false; // �ͩR�Ȭ�0�A�C������
                        }
                    }
                }
            }

            // ��s�p�g��m
            for (int i = lasers.size() - 1; i >= 0; i--) {
                Laser laser = lasers.get(i);
                laser.y -= 5; // �p�g�V�W����
                if (laser.y < 0) {
                    lasers.remove(i); // �����W�X�ù����p�g
                } else {
                    // �p�g�P�ĤH�I���˴�
                    for (int j = enemies.size() - 1; j >= 0; j--) {
                        Enemy enemy = enemies.get(j);
                        if (Math.abs(laser.x - enemy.x) < 15 && Math.abs(laser.y - enemy.y) < 15) {
                            enemies.remove(j); // �ĤH�Q��������
                            lasers.remove(i); // �p�g����
                            score += 10; // ���ѼĤH�[10��
                            break;
                        }
                    }
                }
            }

            repaint();
            try {
                Thread.sleep(16); // ��60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && playerX > 40) {
            playerX -= playerSpeed;
        }
        if (key == KeyEvent.VK_RIGHT && playerX < getWidth() - 40) {
            playerX += playerSpeed;
        }
        if (key == KeyEvent.VK_SPACE) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= shootCooldown) {
                lasers.add(new Laser(playerX, playerY - 10)); // ���Ů���o�g�p�g
                lastShootTime = currentTime;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}