package uk.ac.bradford.dungeongame;

import java.awt.EventQueue;

public class Launcher {
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
        

            @Override
            public void run() {
                GameGUI gui = new GameGUI();           
                gui.setVisible(true);                   
                GameEngine eng = new GameEngine(gui);  
                DungeonInputHandler i = new DungeonInputHandler(eng);   
                gui.registerKeyHandler(i);             
                eng.startGame();                       
            }
        });
    }
    
}
