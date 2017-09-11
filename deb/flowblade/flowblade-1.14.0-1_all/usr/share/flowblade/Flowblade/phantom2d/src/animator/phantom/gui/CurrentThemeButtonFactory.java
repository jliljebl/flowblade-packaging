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

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JToggleButton;

public class CurrentThemeButtonFactory implements ButtonFactoryImpl
{

	private static final int WIDTH_PAD = 20;

	public CurrentThemeButtonFactory(){}

	public JButton getButton( String text, int width )
	{
		JButton button = new JButton( text );
		setSize( button, width );
		return button;
	}
	public JToggleButton getToggleButton( String text, int width )
	{
		JToggleButton button = new JToggleButton( text );
		setSize( button, width );
		return button;
	}

	public int getPreferredWidth( String text )
	{
		JButton b = new JButton( text );
		int prefW = b.getFontMetrics( b.getFont() ).stringWidth( text ) + 2 * WIDTH_PAD;
		return prefW;
	}

	private void setSize( AbstractButton b, int width )
	{
		Dimension d = b.getPreferredSize();
		b.setPreferredSize( new Dimension( width, d.height ) );
		b.setMaximumSize(  new Dimension( width, d.height ) );
	}

}//end class