import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends GameObject
{
    public Player(int x, int y)
    {
        super(x, y);
    }

    @Override
    public void draw(Canvas canvas)
    {
        ImageView im = new ImageView();
        im.setImage(new Image("/images/ship.png"));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setUserData("Player");
        gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
