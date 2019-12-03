import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Rock extends GameObject
{
    protected int HP;
    public Rock(int x, int y)
    {
        super(x, y);
        this.HP = 4;
    }


    @Override
    public void draw(Canvas canvas)
    {
        if(this.HP == 4) {
            ImageView im = new ImageView();
            im.setImage(new Image("images/rock4.png"));
            GraphicsContext gc = canvas.getGraphicsContext2D();
            canvas.setUserData("Rock");
            gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
        }
        else if(this.HP == 3)
        {
            ImageView im = new ImageView();
            im.setImage(new Image("images/rock3.png"));
            GraphicsContext gc = canvas.getGraphicsContext2D();
            canvas.setUserData("Rock");
            gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
        }
        else if(this.HP == 2)
        {
            ImageView im = new ImageView();
            im.setImage(new Image("images/rock2.png"));
            GraphicsContext gc = canvas.getGraphicsContext2D();
            canvas.setUserData("Rock");
            gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
        }
        else if(this.HP == 1)
        {
            ImageView im = new ImageView();
            im.setImage(new Image("images/rock1.png"));
            GraphicsContext gc = canvas.getGraphicsContext2D();
            canvas.setUserData("Rock");
            gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

}
