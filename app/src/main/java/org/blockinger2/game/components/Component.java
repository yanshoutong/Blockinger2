package org.blockinger2.game.components;

import org.blockinger2.game.activities.GameActivity;

public abstract class Component {

    protected GameActivity host;

    public Component(GameActivity ga) {
        host = ga;
    }

    public void reconnect(GameActivity ga) {
        host = ga;
    }

    public void disconnect() {
        host = null;
    }

}
