package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.LumaTableKey;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.SingleCurveEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.CRCurveParam;

public class LumaKeyPlugin extends PhantomPlugin
{
	private CRCurveParam lumaCurve = new CRCurveParam("alpha");
	private LumaTableKey lKey = new LumaTableKey();

	public LumaKeyPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "LumaKey"  );
		registerParameter( lumaCurve );

	}

	public void buildEditPanel()
	{
		SingleCurveEditor cedit = new SingleCurveEditor( "Luma to Alpha", lumaCurve );
		addEditor( cedit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();

		int[] lumatable = lumaCurve.curve.getCurveCopy( true );
		lKey.setTable( lumatable );
		lKey.filter( img );

		sendFilteredImage( img, frame );
	}

}//end class
