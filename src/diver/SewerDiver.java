package diver;

import game.ScramState;
import game.SeekState;

public interface SewerDiver {
    void seek(SeekState state);

    void scram(ScramState state);
}
