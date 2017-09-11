package animator.phantom.gui.timeline;

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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import animator.phantom.controller.DarkTheme;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.GUIResources;

public class TCDisplay extends JPanel
{
	private String timecode;
	private Color tcColor = new Color( 180, 180, 180 );//180, 131, 20 );
	private static final Dimension RECT_SIZE = new Dimension( 80, 36 );
	private static final int TEXT_X = 3;
	private static final int TEXT_Y = 22;
	private static final int LINE_X = 75;	
	private static final int LINE_Y = TEXT_Y + 2;
	private static Font font = GUIResources.TC_FONT;

	public TCDisplay()
	{
		this( TimeLineDisplayPanel.parseTimeCodeString( 0, 6,
						 ProjectController.getFramesPerSecond() ));
	}

	public TCDisplay( String openingTc )
	{
		timecode = openingTc;
		setPreferredSize( RECT_SIZE );
		setMaximumSize( RECT_SIZE );
	}

	public void paintComponent( Graphics g )
	{
		timecode = TimeLineDisplayPanel.parseTimeCodeString( TimeLineController.getCurrentFrame(), 6,
						 ProjectController.getFramesPerSecond() );
						 
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor( DarkTheme.dark );
		g2.fillRect( 0, 0, getWidth(), getHeight() );

		g2.setColor( tcColor );
		g2.setFont( font );
		g2.drawString( timecode, TEXT_X, TEXT_Y );
		g2.drawLine(TEXT_X, LINE_Y, LINE_X, LINE_Y );
	}

}//end class