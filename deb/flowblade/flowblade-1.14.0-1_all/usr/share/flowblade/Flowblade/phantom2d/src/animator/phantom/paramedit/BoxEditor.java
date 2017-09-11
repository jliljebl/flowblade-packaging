package animator.phantom.paramedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
* Base class for creating GUI editors that have a box value edit area with 25%, 50% and 75% values lines on both axis.
* <p>
* Typical use is the standard curves GUI editor. Please read source code if you widh to create editor component
* extending this.
*/
public abstract class BoxEditor extends JPanel implements MouseListener, MouseMotionListener
{
	protected static final Color BG_COLOR = Color.white;
	protected static final Color LINE_COLOR = Color.darkGray;

	protected int pixSize;//size of edit area, minus line frame.
	protected int valueSize;// values from 0 to valueSize
	protected int offX = 1;//to account for line frame
	protected int offY = 1;
	protected float pixPerVal = 1;
	protected BoxEditorListener listener;

	public BoxEditor( int pixSize, int valueSize, BoxEditorListener listener )
	{
		this.pixSize = pixSize;
		this.valueSize = valueSize;
		this.pixPerVal = (float)valueSize / (float)pixSize;
		this.listener = listener;

		addMouseListener( this );
		addMouseMotionListener( this );

		Dimension size = new Dimension( pixSize + 2, pixSize + 2 );
		setPreferredSize( size );
		setMaximumSize( size );
	}

	public Point getBoxValPoint( int x, int y )
	{
		//--- calculate value
		Point p = new Point( (int) ((x - offX) * pixPerVal),
					(int) (( (pixSize ) - (y - offY) ) * pixPerVal ));
		//--- force range
		if( p.x < 0 ) p.x = 0;
		if( p.y < 0 ) p.y = 0;
		if( p.x >= valueSize ) p.x = valueSize - 1;
		if( p.y >= valueSize ) p.y = valueSize - 1;

		return p;
	}

	public void getBoxPanelPoint( Point p, int x, int y )
	{
		p.x = (int) (x / pixPerVal) + offX;
		p.y = pixSize - (int) (y / pixPerVal) + offY;
	}

	public abstract void paint( Graphics g );

	//--------------------------------------- MOUSE EVENTS
	public void mousePressed(MouseEvent e)
	{
		Point p = getBoxValPoint( e.getX(), e.getY() );
		listener.boxMousePress( p );
	}

	public void mouseDragged(MouseEvent e)
	{
		Point p = getBoxValPoint( e.getX(), e.getY() );
		listener.boxMouseDrag( p );
	}

	public void mouseReleased(MouseEvent e)
	{
		System.out.println( "ey:" + e.getY() );
		Point p = getBoxValPoint( e.getX(), e.getY() );
		listener.boxMouseRelease( p );
	}
	//--- Mouse events that are not handled.
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

	public void paintBG(  Graphics g )
	{
		//--- clear
		g.setColor( BG_COLOR );
		g.fillRect( 0, 0, pixSize + 1, pixSize + 1 );

		//--- Draw box
		g.setColor( LINE_COLOR );
		g.drawRect(  offX - 1, offY - 1, pixSize + 1, pixSize + 1 );

		//--- Draw value lines
		int step = pixSize / 4;
		for( int i = 1; i < 4; i++ )
			g.drawLine( step * i, 0, step * i, pixSize );
		for( int i = 1; i < 4; i++ )
			g.drawLine( 0, step * i,  pixSize, step * i );

	}
	
}//end class