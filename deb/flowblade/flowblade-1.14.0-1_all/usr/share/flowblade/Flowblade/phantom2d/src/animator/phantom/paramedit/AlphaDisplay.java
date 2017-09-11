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

import giotto2D.filters.merge.AlphaToImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.plugin.PluginUtils;

/**
* A GUI editor component for setting the value of a <code>CRCurveParam</code> parameter. 
*/
public class AlphaDisplay extends JPanel
{
	private static final int DEFAULT_WIDTH = 180;
	private static final int DEFAULT_HEIGHT = 135;
	private static final int HEIGHT_PAD = 40;
	private static final int NAME_DRAW_X = 10;
	private static final int NAME_DRAW_Y = 10;

	private BufferedImage display;
	private int dispX;
	private int dispY;

	private String text;

	public AlphaDisplay( String text )
	{
		this( text, DEFAULT_WIDTH, DEFAULT_HEIGHT );
	}
	public AlphaDisplay( String text, int displayWidth, int displayHeight )
	{
		this.text = text;
		display = PluginUtils.createCanvas( displayWidth, displayHeight );
		Dimension psize =  new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, displayHeight + HEIGHT_PAD );
		dispX = (psize.width - displayWidth) / 2;
		dispY = HEIGHT_PAD / 2;
		setPreferredSize( psize );
		setMaximumSize( psize );
	}

	public void displayAlpha( BufferedImage img )
	{
		BufferedImage alphaImage = PluginUtils.createCanvas( img.getWidth(), img.getHeight() );
		AlphaToImage.filter( img, alphaImage );
		Image scaled = alphaImage.getScaledInstance( display.getWidth(), display.getHeight(), Image.SCALE_FAST );
		Graphics g = display.getGraphics();
		g.drawImage( scaled, 0, 0, null );
		g.dispose();
		repaint();
	}

	public void paintComponent( Graphics g )
	{
		g.setColor( GUIColors.bgColor );
		g.fillRect(0,0, getWidth(), getHeight() );
		g.drawImage( display, dispX, dispY, null);
		g.setFont( GUIResources.BASIC_FONT_12 );
		g.setColor( Color.black );
		g.drawString( text, NAME_DRAW_X, NAME_DRAW_Y );
	}
	

}//end class