package animator.phantom.renderer.imagesource;

/*
    Copyright Janne Liljeblad

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
import java.util.Vector;

import animator.phantom.gui.view.editlayer.FileSourceEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.imagesource.FileImageSourceEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

public class FileImageSource extends MovingBlendedIOP
{
	//public FileSourceEditLayer editLayer = null;

	public FileImageSource()
	{
		this( null );
	}
	
	public FileImageSource( FileSingleImageSource fileSource )
	{
		if( fileSource != null )
		{
			registerFileSource( fileSource );
			name = fileSource.getName();
		}
		else
		{
			name = "FileImageSource";
		}
 		registerCoords( new AnimatedImageCoordinates( this ) );
		registerMovingBlendParams();
		setAsSource();
		setIOPToHaveSwitches();
		setCenterable();
	}
	
	public ParamEditPanel getEditPanelInstance()
	{
		return new FileImageSourceEditPanel( this );
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- Get image. ImageOperation takes care of alpha edges + memory management in getFileSource()
		FileSingleImageSource fs = (FileSingleImageSource) getFileSource();
		if( !fs.dataInMemory() ) fs.loadData();

		BufferedImage img = fs.getBufferedImage();

		//--- Render using super class MovingBlendedIOP method.
		renderMovingBlendedImage( frame, img );
	}
	
	public ViewEditorLayer getEditorlayer()
	{
		return  new FileSourceEditLayer( this );
	}

}//end class
