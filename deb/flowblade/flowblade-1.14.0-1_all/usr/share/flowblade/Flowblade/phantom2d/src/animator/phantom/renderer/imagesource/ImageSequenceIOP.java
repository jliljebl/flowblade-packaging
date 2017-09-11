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

import animator.phantom.gui.view.editlayer.MovieSourceEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.imagesource.ImageSequenceEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

public class ImageSequenceIOP extends MovingBlendedIOP
{

	public ImageSequenceIOP()
	{
		this( null );
	}
	
	public ImageSequenceIOP( FileSequenceSource fileSequence )
	{
		clipType = NOT_FREE_LENGTH;

		if( fileSequence != null )
			name = fileSequence.getName();
		else
			name = "ImageSequenceSource";

		registerCoords( new AnimatedImageCoordinates( this ) );
		registerMovingBlendParams();

		if( fileSequence != null )
			setProgramLength( fileSequence.getProgramLength() );

		setAsSource();
		setIOPToHaveSwitches();

		registerFileSource( fileSequence );//tää voi bugaa pahasti save ja load hommeleissa.
	}
	
	public ParamEditPanel getEditPanelInstance()
	{
		return new ImageSequenceEditPanel( this );
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		int clipFrame = getClipFrame( frame );

		FileSequenceSource fileSequence = (FileSequenceSource) getFileSource();
		BufferedImage img  = fileSequence.getClipImage( clipFrame );

		renderMovingBlendedImage( frame, img );
	}

	public ViewEditorLayer getEditorlayer()
	{
		return  new MovieSourceEditLayer( this );
	}

}//end class
