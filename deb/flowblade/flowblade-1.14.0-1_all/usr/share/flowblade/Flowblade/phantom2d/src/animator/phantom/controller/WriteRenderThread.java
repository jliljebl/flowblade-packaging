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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

//--- To keep GUI alive while rendering.
public class WriteRenderThread extends Thread
{
	private MovieRenderer movieRenderer;
	private int start;
	private int end;
	private File targetFolder;
	private String frameName;
	private int zeroPadDigits;
	private boolean updateRenderWindow;

	public WriteRenderThread( int start, int end, String frameName )
	{
		this.start = start;
		this.end = end;
		this.frameName = frameName;
		this.targetFolder = RenderModeController.getWriteFolder();
		this.zeroPadDigits = RenderModeController.getZeroPadding();
		this.updateRenderWindow = true;
	}

	public void setUpdateRenderWindow( boolean update ){ updateRenderWindow = update; }
	
	//--- Render, set and display bg image.
	public void run()
	{
		movieRenderer = new MovieRenderer( ProjectController.getFlow(), MovieRenderer.FULL_SIZE, RenderModeController.getRenderThreadsCount() );
		movieRenderer.setWriter( this );
		movieRenderer.setUpdateRenderWindow( updateRenderWindow );

		// Blocks until all frames written out or aborted, Returned frames is empty 
		// because we set writer which writes out frames using writeFrame( ) immediately instead of saving them.
		@SuppressWarnings("unused")
		Vector <BufferedImage > frames = movieRenderer.renderFrameRangeToVector( start, end );

		// Only needed when render window displayed
		if ( updateRenderWindow == true ) RenderModeController.closeWriteRender();
	}

	public void writeFrame( NumberedFrame frame )
	{
		writeFile( frame.getFrameImage(), frame.getFrameNumber());
	}

	//--- Writes file with name
	public void writeFile( BufferedImage img, int frame )
	{
		try
		{
			File f = getFileForFrame( frame, ".png", zeroPadDigits );

			if( img == null )
				System.out.println( "img == null" );

			ImageIO.write( img, "png", f );
		}
		catch( IOException e )
		{
			System.out.println( "ImagewWrite failed in PNG writer" );
		}
	}

	//--- Returns file object for frame. Use temp name if frame name not set
	private File getFileForFrame( int frame, String fileExtension, int zeroPadDigits )
	{
		if( frameName == null || frameName.length() == 0 )
			frameName = "frame";

		String frameString = ( new Integer(frame) ).toString();
		String filePath;
		if( zeroPadDigits > 2 )
		{
			int xtraZeros = zeroPadDigits - frameString.length();
			for( int i = 0; i < xtraZeros; i++ ) frameString = "0" + frameString;

			System.out.println(targetFolder + "/" + frameName + frameString + fileExtension );
			filePath = targetFolder.getPath() +  "/" + frameName + frameString + fileExtension;
		}
		else 
			filePath = targetFolder.getPath() + "/" + frameName + frameString + fileExtension;

		return new File( filePath );
	}

}//end class
