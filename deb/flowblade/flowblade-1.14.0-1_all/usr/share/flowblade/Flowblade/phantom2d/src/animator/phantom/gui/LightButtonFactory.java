package animator.phantom.gui;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

// These button USED to be light, not anymore.
public class LightButtonFactory implements ButtonFactoryImpl
{
	private static Color TEXT_COLOR = Color.black;
	private static final int BUTTON_HEIGHT = 27;
	private static final int IMG_HEIGHT = 24;
	private static final int SLICE_WIDTH = 5;
	private static final int WIDTH_PAD = 10;
	private static final int BASE_LINE = 16;
	
	private static FontMetrics fontMetrics;

	static
	{
		JPanel p = new JPanel();
		fontMetrics = p.getFontMetrics( p.getFont() );
	}

	public LightButtonFactory(){}

	public JButton getButton( String text, int width )
	{
		int slices = getSlices( width );
		ImageIcon upIcon = getUpIcon( text, slices );
		JButton button = new JButton( upIcon );
		setSize( button, slices );
		return button;
	}
	public JToggleButton getToggleButton( String text, int width )
	{
		int slices = getSlices( width );
		ImageIcon upIcon = getUpIcon( text, slices );
		JToggleButton button = new JToggleButton( upIcon );
		button.setSelectedIcon( getDownIcon( text, slices) );
		setSize( button, slices );
		return button;
	}

	public int getPreferredWidth( String text )
	{
		return  fontMetrics.stringWidth( text ) + 2 * WIDTH_PAD;
	}

	private int getSlices( int width )
	{
		return (int) Math.round( (double) width / (double) SLICE_WIDTH );
	}

	
	private ImageIcon getUpIcon( String text, int slices )
	{
		BufferedImage img = new BufferedImage(  slices * SLICE_WIDTH,
							IMG_HEIGHT,
							BufferedImage.TYPE_INT_ARGB );
		Graphics g = img.getGraphics();

		int textWidth = fontMetrics.stringWidth( text );
		int x = ( slices * SLICE_WIDTH / 2 ) - ( textWidth / 2 );
		g.setColor( TEXT_COLOR );
		g.drawString( text, x, BASE_LINE );

		return new ImageIcon( img );
	}

	private ImageIcon getDownIcon( String text, int slices )
	{
		BufferedImage img = new BufferedImage(  slices * SLICE_WIDTH,
							IMG_HEIGHT,
							BufferedImage.TYPE_INT_ARGB );
		Graphics g = img.getGraphics();

		int textWidth = fontMetrics.stringWidth( text );
		int x = ( slices * SLICE_WIDTH / 2 ) - ( textWidth / 2 );
		g.setColor( TEXT_COLOR );
		g.drawString( text, x, BASE_LINE );
		g.dispose();

		Graphics2D g2 = img.createGraphics();
		Color c = new Color(100, 100, 200, 50); 
		g2.setPaint( c );
		Rectangle r = new Rectangle(slices * SLICE_WIDTH, BUTTON_HEIGHT);
		g2.fill( r );

		return new ImageIcon( img );
	}

	private void setSize( AbstractButton b, int slices )
	{
		b.setPreferredSize( new Dimension( slices * SLICE_WIDTH, BUTTON_HEIGHT ) );
		b.setMaximumSize(  new Dimension( slices * SLICE_WIDTH, BUTTON_HEIGHT ) );
	}

}//end class