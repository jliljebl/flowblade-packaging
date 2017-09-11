package animator.phantom.controller;

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

import java.awt.image.BufferedImage;

import animator.phantom.gui.view.component.ViewEditor;

//--- Renders ViewEditor bg image.
//--- To keep GUI alive while rendering.
public class ViewEditorRenderThread extends Thread
{
	private MovieRenderer movieRenderer;
	private ViewEditor viewEditor;
	private int frame;
	private static Object viewRenderLock = new Object();

	public ViewEditorRenderThread( MovieRenderer movieRenderer, ViewEditor viewEditor, int frame )
	{
		this.movieRenderer = movieRenderer;
		this.viewEditor = viewEditor;
		this.frame = frame;
	}

	//--- Render, set and display bg image.
	public void run()
	{
		System.out.println("//------------------------------- VIEW EDITOR RENDER ----------------------------//" );
		synchronized( viewRenderLock ) //??!!??
		{
			long start = System.currentTimeMillis();
			RenderModeController.setGlobalRenderMode( RenderModeController.DRAFT );
			EditorsController.displayRenderClock( true );
			BufferedImage frameImg = movieRenderer.renderSingleFrame( frame );

			if( EditorsController.displayAlpha() )
				AlphaToImage.filter( frameImg, frameImg );

			if( frameImg == null )
			{
				//display some broken flow icon thing?
			}	

			viewEditor.setBGImage( frameImg );
			viewEditor.repaint();
			EditorsController.displayRenderClock( false );
			long end = System.currentTimeMillis();
			GUIComponents.previewControls.updatePreviewRenderInfo( (int) (end - start), frame );
		}
	}

}//end class
