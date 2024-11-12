package game;

import cms.util.maybe.Maybe;
import gui.GUI;

import java.util.concurrent.locks.ReentrantLock;

/** Methods for controlling the interaction between the main thread and the GUI, if a GUI is present. */
public class GUIControl {
    private GUIControl(){}

    public static void startAnimation(Maybe<GUI> gui) {
        gui.thenDo(g -> g.startAnimating());
    }

    public static void waitForAnimation(Maybe<GUI> gui) {
        gui.thenDo(g -> {
            synchronized (g){
            while(g.isAnimating())
                {
                    try {
                        g.wait();
                    } catch (InterruptedException e) {
                        // throw new RuntimeException(e);
                    }
                }
            };
        });
    }
}
