package animator.phantom.renderer.plugin;

import giotto2D.core.GeometricFunctions;

import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import animator.phantom.blender.Blender;
import animator.phantom.gui.view.editlayer.PerspectiveEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.plugin.PluginUtils;

import com.jhlabs.image.PerspectiveFilter;

public class PerspectivePlugin extends PolyLinePlugin
{
	public PerspectivePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Perspective" );
		
		registerPathParams();

		addPoint( new Point2D.Float( 0, 0  ) );
		addPoint( new Point2D.Float( 100, 0 ) );
		addPoint( new Point2D.Float( 100, 100 ) );
		addPoint( new Point2D.Float( 0, 100 ) );

		closed.set( true );
	}

	public void buildEditPanel()
	{
		//addEditor( new NoParamsPanel("Perspective") );
	}

	public void doImageRendering( int frame )
	{		
		PerspectiveFilter f = new PerspectiveFilter();
		f.setCorners(  	getPoint( 0, frame ).x,
				getPoint( 0, frame ).y,
				getPoint( 1, frame ).x,
				getPoint( 1, frame ).y,
				getPoint( 2, frame ).x,
				getPoint( 2, frame ).y,
				getPoint( 3, frame ).x,
				getPoint( 3, frame ).y );

		BufferedImage  dst = PluginUtils.createTransparentScreenCanvas();
		BufferedImage flowImag = getFlowImage();
		f.filter( flowImag, dst );

		BufferedImage  out = PluginUtils.createTransparentScreenCanvas();
		Point2D.Float[] points = { getPoint( 0, frame ), getPoint( 1, frame ), getPoint( 2, frame ), getPoint( 3, frame ) };
		float x = GeometricFunctions.getMinX( points );
		float y = GeometricFunctions.getMinY( points );
		Blender.doCoordsBlend(  frame,
					out,
					dst,
					x,
					y,
					1,
					1,
					0,
					0,
					0,
					100,
					0, //blendmode normal
					AffineTransformOp.TYPE_BILINEAR,
					null,
					true );
	
		sendFilteredImage( out, frame );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PerspectiveEditLayer( this );
	}

}//end class
