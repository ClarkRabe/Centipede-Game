import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public abstract class GameObject
{
    protected int xVal, yVal;

    public GameObject(int x, int y)
    {
        this.xVal = x;
        this.yVal = y;
    }
    public abstract void draw(Canvas canvas);

}
