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
//import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
//import javax.swing.border.EtchedBorder;

//--- Custom progressbar component. Used in RenderWindow
public class PHProgressBar extends JPanel
{
	private float progress;
	private int piecesCount;
	private float pieceShare;

	private Color bgColor = GUIColors.bgColor;
	private Color fgColor = Color.black;

	public PHProgressBar()
	{
		this.piecesCount = 10;
		progress = 0.0f;
		pieceShare = 1.0f / (float) piecesCount;

		setBorder( BorderFactory.createLineBorder( GUIColors.selectedColor));
	}

	public void setPiecesCount( int pieces )
	{
		progress = 0.0f;
		this.piecesCount = pieces;
		pieceShare = 1.0f / (float) piecesCount;
	}

	public void setProgress( float prog )
	{
		progress = prog;
		repaint();
	}

	public void advanceOne()
	{ 
		progress += pieceShare;
		repaint();
	}

	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		g.setColor( bgColor );
		g.fillRect( 2, 2, getWidth() - 4, getHeight() - 4 );
		g.setColor( fgColor );
		g.fillRect( 2, 2, (int)((getWidth() - 4) * progress), getHeight() - 4 );
	}

}//end class
