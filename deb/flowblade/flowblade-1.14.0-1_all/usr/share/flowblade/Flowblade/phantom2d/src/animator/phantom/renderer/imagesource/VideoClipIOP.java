package animator.phantom.renderer.imagesource;

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.view.editlayer.MovieSourceEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.imagesource.VideoClipEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.VideoClipSource;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

public class VideoClipIOP extends MovingBlendedIOP 
{
	public VideoClipIOP()
	{
		this( null );
	}
	
	public VideoClipIOP( VideoClipSource videoClip )
	{
		clipType = NOT_FREE_LENGTH;
	
		if( videoClip != null )
			name = videoClip.getName();
		else
			name = "VideoClip";
	
		registerCoords( new AnimatedImageCoordinates( this ) );
		registerMovingBlendParams();
	
		if( videoClip != null )
			setProgramLength( videoClip.getProgramLength() );
	
		setAsSource();
		setIOPToHaveSwitches();
	
		registerFileSource( videoClip );//this may have caused issues on save and load?!?
	}
	
	public ParamEditPanel getEditPanelInstance()
	{
		return new VideoClipEditPanel( this );
	}
	
	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		int clipFrame = getClipFrame( frame );
	
		VideoClipSource videoClip = (VideoClipSource) getFileSource();
		BufferedImage img  = videoClip.getClipImage( clipFrame );
	
		renderMovingBlendedImage( frame, img );
	}
	
	public ViewEditorLayer getEditorlayer()
	{
		return  new MovieSourceEditLayer( this );
	}

}//end class
