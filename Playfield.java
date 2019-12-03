import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import java.util.ArrayList;

public class Playfield extends GridPane
{
    private AudioClip clip = new AudioClip(getClass().getResource("sounds/rockhit.wav").toString());
    private AudioClip clip2 = new AudioClip(getClass().getResource("sounds/seghit.wav").toString());
    protected Canvas[][] field;
    private Projectile cannon = null;
    private Player player = new Player(23, 11);
    private ArrayList<Rock> rockArra;
    private ArrayList<Orc> orcArra;
    private int segments, rocks;
    protected int score, lives;
    protected boolean isAlive = true;

    public Playfield(int s, int r, Canvas canvas)
    {
        super();
        this.setWidth(canvas.getWidth());
        this.setHeight(canvas.getHeight());
        this.segments = s;
        this.rocks = r;
        this.field = initalizePlayField();
        this.rockArra = new ArrayList<>();
        this.orcArra = new ArrayList<>();
        this.score = 0;
        this.lives = 1;
    }

    public void playFieldSetUp()
    {
        this.setRocks();
        this.setPlayer();
        this.setOrcs();
    }

    public Canvas[][] initalizePlayField()
    {
        int rows = 25;
        int cols = 25;

        Canvas[][] arra = new Canvas[rows][cols];
        Canvas canvas;

        for (int i = 0; i < rows - 1; i++)
        {
            for (int j = 0; j < cols - 1; j++)
            {
                canvas = new Canvas(this.getWidth() / rows, this.getHeight() / cols);
                this.add(canvas, j, i, 1, 1);
                arra[i][j] = canvas;
                arra[i][j].setUserData("Empty");
            }
        }
        return arra;
    }

    public void clearPlayField()
    {
        Canvas canvas;

        for(int i = 0; i < field.length - 1; i++)
        {
            for(int j = 0; j < field.length - 1; j++)
            {
                canvas = getCanvas(i, j);
                clearCanvas(canvas);
            }
        }

        this.segments = settingsData.numSegments;
        this.rocks = settingsData.rockCount;
        this.rockArra = new ArrayList<>();
        this.orcArra = new ArrayList<>();
        playFieldSetUp();
    }
    public void setRocks()
    {
        Canvas canvas;
        Rock o;

        if(!rockArra.isEmpty())
        {
            for(int i = 0; i < rockArra.size(); i++) {
                o = rockArra.get(i);
                canvas = this.field[o.xVal][o.yVal];
                o.draw(canvas);
            }
        }
        else {
            //gets X and Y locations for Rocks
            Integer[] rockLocationsX = getRockValues();
            Integer[] rockLocationsY = getRockValues();

            //places rocks at random
            for (int i = 0; i < rockLocationsX.length; i++) {
                int a = rockLocationsX[i];
                int b = rockLocationsY[i];
                canvas = this.field[a][b];
                this.field[a][b].setUserData("Rock");
                o = new Rock(a, b);
                rockArra.add(o);
                o.draw(canvas);
            }
        }
    }
    private Integer[] getRockValues()
    {
        Integer[] arra = new Integer[rocks];

        for(int i = 0; i < arra.length; i++)
        {
            int xVal = (int) (Math.random() * 22 + 1);
            arra[i] = xVal;
        }

        return arra;
    }

    public void setPlayer()
    {
        Canvas canvas = this.field[player.xVal][player.yVal];
        player.draw(canvas);
    }


    //NEEDS WORK-- NOT IN MIDDLE OF CANVAS
    public void setOrcs()
    {
        Canvas canvas;
        Orc o;

        if(!orcArra.isEmpty())
        {

            for(int i = 0; i < orcArra.size(); i++)
            {
                o = orcArra.get(i);
                canvas = this.field[o.xVal][o.yVal];
                o.draw(canvas);
            }
        }
        else {
            int middle = 11;


            for (int i = 0; i < segments; i++) {
                canvas = this.field[0][middle];
                o = new Orc(0, middle);
                orcArra.add(o);
                o.draw(canvas);
                middle--;
            }
        }
    }

    private void clearPlayfield()
    {
        Canvas canvas;
        GraphicsContext g;
        for(int i = 0; i < this.field.length - 1; i++)
        {
            for(int j = 0; j < this.field.length - 1; j++)
            {
                canvas = field[i][j];
                g = canvas.getGraphicsContext2D();
                g.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
            }
        }
    }

    public void updatePlayer(String s)
    {
        Canvas canvas;

        if(s.equals("left") && this.player.yVal != 0)
        {
            canvas = getCanvas(this.player.xVal, this.player.yVal);
            clearCanvas(canvas);

            canvas = this.field[this.player.xVal][this.player.yVal - 1];
            canvas.setUserData("Player");

            this.player.yVal = this.player.yVal -1;
            updatePlayField();
        }
        else if(s.equals("right") && this.player.yVal < this.field.length - 2)
        {
            canvas = getCanvas(this.player.xVal, this.player.yVal);
            clearCanvas(canvas);

            canvas = this.field[this.player.xVal][this.player.yVal + 1];
            canvas.setUserData("Player");

            this.player.yVal = this.player.yVal + 1;
            updatePlayField();
        }
        else if(s.equals("space") || s.equals("click") && cannon == null)
        {
            canvas = getCanvas(this.player.xVal -1, this.player.yVal);

            if(canvas.getUserData().equals("Empty") && this.cannon == null)
            {
                this.cannon = new Projectile(this.player.xVal - 1, this.player.yVal);
                canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
                this.cannon.draw(canvas);
            }
            else if(canvas.getUserData().equals("Rock"))
            {
                projectileHit();
            }
        }

    }

    public Canvas getCanvas(int x, int y)
    {

        return this.field[x][y];

    }

    public void clearCanvas(Canvas canvas)
    {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        canvas.setUserData("Empty");
    }

    public void updateProjectile()
    {
        Canvas canvas;

        if(this.cannon.xVal -1 < 0)
        {
            canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
            clearCanvas(canvas);
            this.cannon = null;
        }
        else if(checkCollision(this.cannon, "up")) {
            if (this.cannon.xVal - 1 < 0) {
                canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
                clearCanvas(canvas);
                this.cannon = null;
            } else {
                canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
                clearCanvas(canvas);
                this.cannon.xVal = this.cannon.xVal - 1;
                canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
                this.cannon.draw(canvas);
            }
        }
        else
        {
            projectileHit();
            canvas = getCanvas(this.cannon.xVal, this.cannon.yVal);
            clearCanvas(canvas);
            this.cannon = null;
        }
    }
    public void updateOrcs()
    {

        Canvas canvas;

        for (Orc o : orcArra)
        {

            if(o.xVal == this.player.xVal)
            {
                if(o.yVal + 1 == this.player.yVal)
                {
                    this.lives--;
                    this.isAlive = false;
                }
                else if(o.xVal - 1 == this.player.yVal)
                {
                    this.lives--;
                    this.isAlive = false;
                }
                if(o.yVal == this.field.length - 2)
                {
                    o.leftOrRight = true;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal - 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else if(o.yVal == 0)
                {
                    o.leftOrRight = false;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else if(!o.leftOrRight)
                {
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else if(o.leftOrRight)
                {
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal - 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
            }
            else if (o.yVal == this.field.length - 2 && !o.leftOrRight)
            {

                if(checkCollision(o, "down")) //moves obj down while moving right
                {
                    o.leftOrRight = true;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else
                {
                    o.leftOrRight = true;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    o.yVal = o.yVal - 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }

            }
            else if (o.yVal == 0 && o.leftOrRight) //moves obj down while moving left
            {

                if(checkCollision(o, "down"))
                {
                    o.leftOrRight = false;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else {
                    o.leftOrRight = false;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    o.yVal = o.yVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
            }
            else if (!o.leftOrRight) // moves obj right
            {
                if(checkCollision(o, "right"))
                {
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else
                {
                    o.leftOrRight = true;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }

            }
            else if (o.leftOrRight) // moves obj left
            {

                if(checkCollision(o, "left")) {
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.yVal = o.yVal - 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
                else
                {
                    o.leftOrRight = false;
                    canvas = getCanvas(o.xVal, o.yVal);
                    clearCanvas(canvas);
                    o.xVal = o.xVal + 1;
                    canvas = getCanvas(o.xVal, o.yVal);
                    o.draw(canvas);
                }
            }
        }

    }
    public void updatePlayField()
    {
        clearPlayfield();
        playFieldSetUp();
        if(cannon != null)
            updateProjectile();
    }

    private boolean checkCollision(GameObject o, String pos)
    {
        //returns true if place to move is empty, returns false if
        // an object already occupies that space

        Canvas canvas;
            if (pos.equals("left")) {
                canvas = getCanvas(o.xVal, o.yVal - 1);

                if (canvas.getUserData().equals("Empty"))
                    return true;
                else
                    return false;
            } else if (pos.equals("right")) {
                canvas = getCanvas(o.xVal, o.yVal + 1);

                if (canvas.getUserData().equals("Empty"))
                    return true;
                else
                    return false;
            } else if (pos.equals("up")) {
                canvas = getCanvas(o.xVal - 1, o.yVal);

                if (canvas.getUserData().equals("Empty"))
                    return true;
                else
                    return false;
            } else {
                canvas = getCanvas(o.xVal + 1, o.yVal);

                if (canvas.getUserData().equals("Empty"))
                    return true;
                else
                    return false;
            }



    }

    public void projectileHit()
    {
        Canvas canvas;

        if(this.cannon == null)
        {
            canvas = getCanvas(this.player.xVal -1, this.player.yVal);

            if (canvas.getUserData().equals("Rock"))
            {
                updateScore("rock");
                clip.play(0.1);
                for (int i = 0; i < rockArra.size(); i++)
                {
                    Rock r = rockArra.get(i);
                    if (r.xVal == this.player.xVal - 1 && r.yVal == this.player.yVal) {
                        if (r.HP > 1) {
                            r.HP = r.HP - 1;
                            canvas = getCanvas(r.xVal, r.yVal);
                            r.draw(canvas);
                        } else {
                            canvas = getCanvas(r.xVal, r.yVal);
                            clearCanvas(canvas);
                            rockArra.remove(r);
                        }
                    }
                }


            } else if (canvas.getUserData().equals("Orc"))
            {
                updateScore("orc");
                clip2.play(0.1);

                for (int i = 0; i < orcArra.size(); i++) {
                    Orc o = orcArra.get(i);
                    if (o.xVal == this.cannon.xVal - 1 && o.yVal == this.cannon.yVal) {
                        canvas = getCanvas(o.xVal, o.yVal);
                        clearCanvas(canvas);
                        Rock r = new Rock(o.xVal, o.yVal);
                        rockArra.add(r);
                        orcArra.remove(o);
                        r.draw(canvas);
                    }
                }
            }
        }
        else {
            canvas = getCanvas(this.cannon.xVal - 1, this.cannon.yVal);

            if (canvas.getUserData().equals("Rock"))
            {
                updateScore("rock");
                clip.play(0.1);
                for (int i = 0; i < rockArra.size(); i++) {
                    Rock r = rockArra.get(i);
                    if (r.xVal == this.cannon.xVal - 1 && r.yVal == this.cannon.yVal) {
                        if (r.HP > 1) {
                            r.HP = r.HP - 1;
                            canvas = getCanvas(r.xVal, r.yVal);
                            r.draw(canvas);
                        } else {
                            canvas = getCanvas(r.xVal, r.yVal);
                            clearCanvas(canvas);
                            rockArra.remove(r);
                        }
                    }
                }

            } else if (canvas.getUserData().equals("Orc"))
            {
                updateScore("orc");
                clip2.play(0.1);

                for (int i = 0; i < orcArra.size(); i++) {
                    Orc o = orcArra.get(i);
                    if (o.xVal == this.cannon.xVal - 1 && o.yVal == this.cannon.yVal) {
                        canvas = getCanvas(o.xVal, o.yVal);
                        clearCanvas(canvas);
                        Rock r = new Rock(o.xVal, o.yVal);
                        rockArra.add(r);
                        orcArra.remove(o);
                        r.draw(canvas);
                    }
                }
            }
        }
    }

    public void updateScore(String s)
    {
        if(s.equals("orc"))
            this.score = this.score + 10;
        else
            this.score = this.score + 5;
    }

}
