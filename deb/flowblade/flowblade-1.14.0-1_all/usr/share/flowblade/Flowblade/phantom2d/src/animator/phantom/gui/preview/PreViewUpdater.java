package animator.phantom.gui.preview;

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
import java.awt.Image;
//import java.awt.Toolkit;
//import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

//import animator.phantom.controller.Application;
import animator.phantom.controller.ProjectController;
//import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIResources;

//--- Preview updates handler ( used to be display JPanel, code still looks like it).
public class PreViewUpdater
{
	private float scale;

	private Dimension scaledScreensize;
	private Dimension screenSize;

	private BufferedImage noPreview;
	private BufferedImage noPreviewScaled;

	public PreViewUpdater()
	{
		screenSize = ProjectController.getScreenSize();
		
		noPreview = GUIResources.getResourceBufferedImage( GUIResources.noPreview );
		noPreviewScaled = noPreview;

		setScale( 1.0f );
	}

	public void setFrame( BufferedImage frame_ )
	{

	}

	public void setScale( float newScale )
	{
		this.scale = newScale;
		scaledScreensize = new Dimension( (int)(screenSize.width  *  scale), (int)(screenSize.height  * scale) );
		
		createNoPreviewImage( scaledScreensize.width, scaledScreensize.height );
	}

	private void createNoPreviewImage( int screenWidth, int screenHeight )
	{
		//--- Create smaller image if needed
		if( screenWidth < noPreview.getWidth() ||
			screenHeight < noPreview.getHeight() )
		{
			//--- Select scale 
			float wt = (float)screenWidth / noPreview.getWidth();
			float ht = (float)screenHeight / noPreview.getHeight();

			float tscale;
			if( wt < ht ) tscale = wt;
			else tscale = ht;
			int nw = (int)(tscale * noPreview.getWidth());
			int nh = (int)(tscale * noPreview.getHeight());

			Image scaled = noPreview.getScaledInstance( nw, nh, Image.SCALE_FAST );
			noPreviewScaled = new BufferedImage( nw, nh, BufferedImage.TYPE_INT_ARGB );
			Graphics g = noPreviewScaled.getGraphics();
			g.drawImage( scaled, 0, 0, null );
		}
		else
		{
			noPreviewScaled = noPreview;//scaled version will garbage collect

		}
	}

}//end class
