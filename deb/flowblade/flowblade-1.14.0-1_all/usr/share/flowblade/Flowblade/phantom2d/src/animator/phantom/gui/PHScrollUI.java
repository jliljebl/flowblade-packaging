package animator.phantom.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class PHScrollUI extends MetalScrollBarUI 
{
	private Color thumbColor = new Color( 164, 169, 173 );
	private Color trackColor = new Color( 46, 52, 54 );
	
	private int THUMB_WIDTH = 5;
	private int IN_OFF_THUMB = 8;
	private int TRACK_WIDTH = 3;
	private int IN_OFF_TRACK = 9;
	private int IN_PAD = 2;
	
    private JButton b = new JButton() {

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }

    };

    public PHScrollUI() {}

	@Override
	protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds ) 
	{
		g.setColor( thumbColor );
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL)
		{
			g.fillRect(	thumbBounds.x,
					thumbBounds.y + IN_OFF_THUMB,
					thumbBounds.width - IN_PAD,
					THUMB_WIDTH );
		}
		else
		{
			g.fillRect(	thumbBounds.x + IN_OFF_THUMB,
					thumbBounds.y,
					THUMB_WIDTH,
					thumbBounds.height - IN_PAD );
		}
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) 
	{        
		g.setColor( trackColor );
		if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL)
		{
			g.setColor( trackColor );
			g.fillRect(	trackBounds.x,
					trackBounds.y + IN_OFF_TRACK,
					trackBounds.width - IN_PAD,
					TRACK_WIDTH );
		}
		else
		{
			g.setColor( trackColor );
			g.fillRect(	trackBounds.x + IN_OFF_TRACK,
					trackBounds.y,
					TRACK_WIDTH,
					trackBounds.height - IN_PAD );
		}
	}

    @Override
    protected JButton createDecreaseButton(int orientation) 
    {
	return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) 
    {
	return b;
    }

}//end class

