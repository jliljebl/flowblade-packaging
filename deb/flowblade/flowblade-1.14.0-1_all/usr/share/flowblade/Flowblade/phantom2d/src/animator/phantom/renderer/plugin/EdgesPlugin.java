package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.EdgeFilter;

public class EdgesPlugin extends PhantomPlugin
{
	public IntegerParam kernel;

	//--- Kernel types
	private static final int SOBEL = 0;
	private static final int ROBERTS = 1;
	private static final int PREWITT = 2;
	private static final int FREI_CHEN = 3;

	private static String[] kernelOptions = { "Sobel","Roberts","Prewitt","Frei-Chen" };

	public EdgesPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Edges" );

		kernel = new IntegerParam( SOBEL );
		registerParameter( kernel );
	}

	public void buildEditPanel()
	{
		IntegerComboBox kernelSelect = new IntegerComboBox( kernel, "Kernel", kernelOptions );
		addEditor( kernelSelect );
	}

	public void doImageRendering( int frame )
	{
		EdgeFilter edgeFilter = new EdgeFilter();
		setKernel( kernel.get(), edgeFilter );

		applyFilter( edgeFilter );
	}

	private void setKernel( int kernel, EdgeFilter edgeFilter )
	{
		switch( kernel )
		{	
			case SOBEL:
				edgeFilter.setVEdgeMatrix( EdgeFilter.SOBEL_V  );
				edgeFilter.setHEdgeMatrix( EdgeFilter.SOBEL_H );
				break;

			case ROBERTS:
				edgeFilter.setVEdgeMatrix( EdgeFilter.ROBERTS_V );
				edgeFilter.setHEdgeMatrix( EdgeFilter.ROBERTS_H );
				break;

			case PREWITT:
				edgeFilter.setVEdgeMatrix( EdgeFilter.PREWITT_V );
				edgeFilter.setHEdgeMatrix( EdgeFilter.PREWITT_H );
				break;

			case FREI_CHEN:
				edgeFilter.setVEdgeMatrix( EdgeFilter.FREI_CHEN_V );
				edgeFilter.setHEdgeMatrix( EdgeFilter.FREI_CHEN_H );
				break;

			default:
				edgeFilter.setVEdgeMatrix( EdgeFilter.SOBEL_V  );
				edgeFilter.setHEdgeMatrix( EdgeFilter.SOBEL_H );
				break;
		}
	}

}//end class
