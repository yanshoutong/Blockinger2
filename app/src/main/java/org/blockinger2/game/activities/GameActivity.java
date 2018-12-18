package org.blockinger2.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.Window;

import org.blockinger2.game.views.BlockBoardView;
import org.blockinger2.game.R;
import org.blockinger2.game.engine.WorkThread;
import org.blockinger2.game.components.Controls;
import org.blockinger2.game.components.Display;
import org.blockinger2.game.components.GameState;
import org.blockinger2.game.engine.Sound;
import org.blockinger2.game.fragments.DefeatDialogFragment;

public class GameActivity extends FragmentActivity
{
    public Sound sound;
    public Controls controls;
    public Display display;
    public GameState game;
    private WorkThread mainThread;
    private DefeatDialogFragment dialog;
    private boolean layoutSwap;

    public static final int NEW_GAME = 0;
    public static final int RESUME_GAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false)) {
            setContentView(R.layout.activity_game_alt);
            layoutSwap = true;
        } else {
            setContentView(R.layout.activity_game);
            layoutSwap = false;
        }

        /* Read Starting Arguments */
        Bundle b = getIntent().getExtras();
        int value = NEW_GAME;

        /* Create Components */
        game = (GameState) getLastCustomNonConfigurationInstance();
        if (game == null) {
            /* Check for Resuming (or Resumption?) */
            if (b != null) {
                value = b.getInt("mode");
            }

            if ((value == NEW_GAME)) {
                game = GameState.getNewInstance(this);
                game.setLevel(b.getInt("level"));
            } else {
                game = GameState.getInstance(this);
            }
        }
        game.reconnect(this);
        dialog = new DefeatDialogFragment();
        controls = new Controls(this);
        display = new Display(this);
        sound = new Sound(this);

        /* Init Components */
        if (game.isResumable()) {
            sound.startMusic(Sound.GAME_MUSIC, game.getSongtime());
        }
        sound.loadEffects();
        if (b != null) {
            value = b.getInt("mode");
            if (b.getString("playername") != null) {
                game.setPlayerName(b.getString("playername"));
            }
        } else {
            game.setPlayerName(getResources().getString(R.string.anonymous));
        }
        dialog.setCancelable(false);
        if (!game.isResumable()) {
            gameOver(game.getScore(), game.getTimeString(), game.getAPM());
        }

        /* Register Button callback Methods */
        findViewById(R.id.pausebutton_1).setOnClickListener(arg0 -> GameActivity.this.finish());
        findViewById(R.id.boardView).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.boardPressed(event.getX(), event.getY());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.boardReleased();
            }
            return true;
        });

        findViewById(R.id.rightButton).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.rightButtonPressed();
                findViewById(R.id.rightButton).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.rightButtonReleased();
                findViewById(R.id.rightButton).setPressed(false);
            }
            return true;
        });

        findViewById(R.id.leftButton).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.leftButtonPressed();
                findViewById(R.id.leftButton).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.leftButtonReleased();
                findViewById(R.id.leftButton).setPressed(false);
            }
            return true;
        });

        findViewById(R.id.softDropButton).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.downButtonPressed();
                (findViewById(R.id.softDropButton)).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.downButtonReleased();
                (findViewById(R.id.softDropButton)).setPressed(false);
            }
            return true;
        });

        (findViewById(R.id.hardDropButton)).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.dropButtonPressed();
                (findViewById(R.id.hardDropButton)).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.dropButtonReleased();
                (findViewById(R.id.hardDropButton)).setPressed(false);
            }
            return true;
        });

        (findViewById(R.id.rotateRightButton)).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.rotateRightPressed();
                (findViewById(R.id.rotateRightButton)).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.rotateRightReleased();
                (findViewById(R.id.rotateRightButton)).setPressed(false);
            }
            return true;
        });

        (findViewById(R.id.rotateLeftButton)).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                controls.rotateLeftPressed();
                (findViewById(R.id.rotateLeftButton)).setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                controls.rotateLeftReleased();
                (findViewById(R.id.rotateLeftButton)).setPressed(false);
            }
            return true;
        });

        ((BlockBoardView) findViewById(R.id.boardView)).init();
        ((BlockBoardView) findViewById(R.id.boardView)).setHost(this);
    }

    /**
     * Called by BlockBoardView upon completed creation
     *
     * @param caller
     */
    public void startGame(BlockBoardView caller)
    {
        mainThread = new WorkThread(this, caller.getHolder());
        mainThread.setFirstTime(false);
        game.setRunning(true);
        mainThread.setRunning(true);
        mainThread.start();
    }

    /**
     * Called by BlockBoardView upon destruction
     */
    public void destroyWorkThread()
    {
        boolean retry = true;
        mainThread.setRunning(false);
        while (retry) {
            try {
                mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

    /**
     * Called by GameState upon Defeat
     *
     * @param score
     */
    public void putScore(long score)
    {
        String playerName = game.getPlayerName();
        if (playerName == null || playerName.equals("")) {
            playerName = getResources().getString(R.string.anonymous);//"Anonymous";
        }

        Intent data = new Intent();
        data.putExtra(MainActivity.PLAYERNAME_KEY, playerName);
        data.putExtra(MainActivity.SCORE_KEY, score);
        setResult(MainActivity.RESULT_OK, data);

        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sound.pause();
        sound.setInactive(true);
        game.setRunning(false);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        sound.pause();
        sound.setInactive(true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        game.setSongtime(sound.getSongtime());
        sound.release();
        sound = null;
        game.disconnect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sound.resume();
        sound.setInactive(false);

        /* Check for changed Layout */
        boolean tempswap = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false);
        if (layoutSwap != tempswap) {
            layoutSwap = tempswap;
            if (layoutSwap) {
                setContentView(R.layout.activity_game_alt);
            } else {
                setContentView(R.layout.activity_game);
            }
        }
        game.setRunning(true);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance()
    {
        return game;
    }

    public void gameOver(long score, String gameTime, int apm)
    {
        dialog.setData(score, gameTime, apm);
        dialog.show(getSupportFragmentManager(), "hamster");
    }
}
