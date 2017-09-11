package animator.phantom.paramedit;

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
import java.awt.Graphics;

import javax.swing.JPanel;

import animator.phantom.gui.GUIColors;

/**
* A GUI component that draws a separator line between components.
*/
public class RowSeparator extends JPanel
{
	private int SEPARATOR_HEIGHT = 6;
	private int SEPARATOR_WIDTH = 2 * 160;//HUOM, korjaa pitää olla PARAM_COLUMN_WIDTH
	private int LINE_Y = 2;
	/**
	* No parameters constructor.
	*/
	public RowSeparator()
	{
		setPreferredSize( new Dimension( SEPARATOR_WIDTH, SEPARATOR_HEIGHT ) );
		setMaximumSize( new Dimension( SEPARATOR_WIDTH, SEPARATOR_HEIGHT ) );
	}
	/**
	* Overridden paint method.
	*/
	public void paintComponent( Graphics g )
	{
		g.setColor( GUIColors.SEPARATOR_BG );
		g.fillRect( 0,0,SEPARATOR_WIDTH, SEPARATOR_HEIGHT );
		g.setColor( GUIColors.SEPARATOR_LINE );
		g.drawLine( 5, LINE_Y, SEPARATOR_WIDTH - 25, LINE_Y );
	}

}//end class