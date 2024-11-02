/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package game;

/**
 *
 * @author Asus
 */
import javax.swing.*;
public class Game {

    public static void main(String[] args) throws Exception{
        int boardWidth=360;
        int boardHeight=640;
        JFrame frame = new JFrame("Flaapy Bird");
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FlappyBird flappyBird= new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();// Yêu cầu focus để nhận sự kiện bàn phím
        frame.setVisible(true);
    }
    
}
