import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile extends GameObject
{

    public Projectile(int x, int y)
    {
        super(x, y);
    }


    @Override
    public void draw(Canvas canvas)
    {

        ImageView im = new ImageView();
        im.setImage(new Image("images/cannonball.png"));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setUserData("cannonball");
        gc.drawImage(im.getImage(), canvas.getWidth()/4, canvas.getHeight()/4, canvas.getWidth()/2, canvas.getHeight()/2);

    }

}
