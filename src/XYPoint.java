import java.awt.geom.Point2D;

public class XYPoint extends Point2D {
	
	protected Point2D.Double data = new Point2D.Double();
	
// Constructors:
	public XYPoint() {}
	
	public XYPoint(double x, double y) {
		data.setLocation(x, y);
	}
	
	public XYPoint(XYPoint other) {
		data.setLocation(other);
	}
	
	public XYPoint(Point2D.Double other) {
		data.setLocation(other);
	}
	
// Arithmetic operations:
	public XYPoint added(XYPoint other)
	{
		return new XYPoint(data.getX() + other.getX(),
						   data.getY() + other.getY());
	}
	
	public void add(XYPoint other) {
		this.setLocation(data.getX() + other.getX(),
						 data.getY() + other.getY());
	}
	
	public XYPoint subtracted(XYPoint other)
	{
		return new XYPoint(data.getX() - other.getX(),
						   data.getY() - other.getY());
	}
	
	public void subtract(XYPoint other) {
		this.setLocation(data.getX() - other.getX(),
						 data.getY() - other.getY());
	}
	
	public XYPoint multipliedByScalar(double s) {
		return new XYPoint(s * data.getX(),
						   s * data.getY());
	}
	
	public void multiplyByScalar(double s) {
		this.setLocation(s * data.getX(),
						 s * data.getY());
	}
	
	public XYPoint dividedByScalar(double s) {
		return new XYPoint(data.getX() / s,
						   data.getY() / s);
	}
	
	public void divideByScalar(double s) {
		this.setLocation(data.getX() / s,
						 data.getY() / s);
	}
	
// Get & set:	
	@Override
	public double getX() {
		return data.getX();
	}

	@Override
	public double getY() {
		return data.getY();
	}
	
	public void setX(double x) {
		data.x = x;
	}
	
	public void setY(double y) {
		data.y = y;
	}

	@Override
	public void setLocation(double x, double y) {
		data.setLocation(x, y);
	}

	public void setLocation(XYPoint p) {
		Point2D.Double tmp = this.toPoint2D(); // = new Point2D.Double(p.getX(), p.getY());
		data.setLocation(tmp);
	}

// Conversions:
	public Point2D.Double toPoint2D() {
		Point2D.Double tmp = new Point2D.Double(data.getX(), data.getY());
		return tmp;
	}
}
