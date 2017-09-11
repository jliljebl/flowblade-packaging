package animator.phantom.plugin;

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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Vector;

import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.CoordsEditComponents;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.PluginFullScreenMovingSourceIOP;
import animator.phantom.renderer.PluginIOP;
import animator.phantom.renderer.PluginMaskIOP;
import animator.phantom.renderer.PluginMergeIOP;
import animator.phantom.renderer.PluginMovingSourceIOP;
import animator.phantom.renderer.PluginStaticSourceIOP;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.Param;

/**
* New functionality is added to Phantom2D by creating <b>plugins</b> that <b>extend this class</b>.
* <p>
* <h2>Basic functionality</h2>
* Plugins define functionality by overriding at least three abstract methods. Another method can be overridden to provide a view edit layer.
* <p>
* <b>Datamodel and other properties</b> of plugins are defined by 
* overriding method <code>buildDataModel()</code>. Parameters are created using classes such as 
* <code> AnimatedValue</code> and <code>ColorParam</code> and then registering them for user GUI access and persistance. 
* Other plugin properties include user visible name and number of inputs.
* <p>
* <b>Edit panel layout</b> is defined in method <code>buildEditPanel()</code>. Parameters are fed into constructors of
* editor components and editors are then added to edit panel using method <code>addEditor(Component comp)</code>.
* <p>
* <b>Image rendering</b> is done by overriding one of several possible methods, depending on the type of the plugin. Some plugins have also 
* method used to send created / manipulated image along. 
* <p>
* <h2>Plugin types</h2>
* Plugins have type whitch determines their: rendering behaviour, method to override for rendering,
* formced type parameters and default inputs. First method call in the 
* constructor of any plugin must be <code>initPlugin( int type )</code> or <code>initPlugin( int type, int inputsMode )</code>.
* <p>
* <b>FILTER</b>	Image filter plugin that produces new image and alpha channel from single image input.
* <b>Render method:</b> <code>doImageRendering( int frame )</code> <b>Send method:</b> <code>sendFilteredImage( img, frame )</code>.
* <p>
* Examples: GaussianBlur, Curves, Marble, ImageToAlpha.
* <p>
* <b>STATIC_SOURCE</b> Plugin that produces a screen size image that is static or animated with its own plugin specific parameters and blends it 
* on single input image exists.  All plugins of this type have Opacity and Blend mode parameters that
* have their effects rendered when plugin calls <code></code>. 
* <b>Render method:</b> <code>doImageRendering( int frame )</code> <b>Send method:</b> <code>sendStaticSource( BufferedImage img, int frame )</code>.
* <p>
* Examples: Plasma, Gradient, Caustics.
* <p> 
* <b>MOVING_SOURCE</b>Plugin that animates a rectangular source image and blends it on a single input image if it exists. 
* All plugins of this type have position, scale, rotation, flip, composition rule, as canvas, opacity and blend mode 
* parameters that have their effects rendered when plugin calls.
* <b>Render method:</b> <code>doImageRendering( int frame )</code> <b>Send method:</b> <code> sendMovingSource( BufferedImage source, int frame )</code>.
* <p>
* Examples: TestBall.
* <p>
* <b>FULL_SCREEN_MOVING_SOURCE</b> Plugin that always renders a full screen image with animarted content and blends it on a single input image if it exists.
* <b>Render method:</b> <code>renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height ) </code>
* <b>Send method:</b> Does not have one,
* <p>
* Examples Shape, PolyLineShape, PoyCurveShape	
* <p>
* <b>MERGE</b> Plugin that merges two input images into single output image.
* All plugins of this type have opacity, blend mode and blur parameters. 
* <b>Render method:</b> <code>renderMask( float frame, Graphics2D gc, int width, int height )</code>
* <b>Send method:</b> Does not have one,
* <p>
* Examples: ShapeMerge ShapeGridMerge.
* <p>
* <b>MASK</b> Plugin that creates new alpha mask and combines it with alpha channel of single input image.. All plugins of this type have opacity, blend mode
* and blur parameters. 
* <b>Render method:</b> <code>renderMask( float frame, Graphics2D gc, int width, int height )</code>
* <b>Send method:</b> Does not have one,
* <p>
* Examples: GradientMask, PolyCurveMask, ShapeMask, PolyLineMask.
*/
public abstract class PhantomPlugin implements Comparable<Object>
{
	private ImageOperation iop = null;
	private ParamEditPanel editPanel = null;

	/**
	* Image filter plugin that produces new image and alpha channel from single image input. Examples: GaussianBlur, Curves, Marble, ImageToAlpha.   
	*/
	public static final int FILTER = 0;
	/**
	* Plugin that produces a screen size image that is static or animated with its own plugin specific parameters and blends it 
	* on single input image exists.  All plugins of this type have Opacity and Blend mode parameters that
	* have their effects rendered when plugin calls <code>sendStaticSource( BufferedImage img, int frame )</code>. Examples: Plasma, Gradient, Caustics.
	*/
	public static final int STATIC_SOURCE = 1;
	/**
	* Plugin that animates a rectangular source image and blends it on a single input image if it exists. 
	* All plugins of this type have position, scale, rotation, flip, composition rule, as canvas, opacity and blend mode 
        * paramters that have their effects rendered when plugin calls <code> sendMovingSource( BufferedImage source, int frame )</code>. Examples: TestBall.
	*/
	public static final int MOVING_SOURCE = 3;
	/**
	* Plugin that animates a rectangular source image or full screen source image and blends it on a single input image if it exists. 
	* All plugins of this type have position, scale, rotation, flip, composition rule, as canvas, opacity and blend mode 
        * parameters. Override different call method <code> renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height ) </code>
	* This is generally user for vector images that are rendered into fullscreen image before blending topleft matched images. 
	* Examples Shape, PolyLineShape, PoyCurveShape
	*/
	public static final int FULL_SCREEN_MOVING_SOURCE = 4;
	/**
	* A image source plugin with undefined chanracteristics.
	*/
	public static final int FREE_SOURCE = 5;
	/**
	* Plugin that merges two input images into single output image. All plugins of this type have opacity, blend mode
        * and blur parameters. Examples: ShapeMerge ShapeGridMerge.
	*/
	public static final int MERGE = 6;
	/**
	* Plugin that creates new alpha mask and combines it with alpha channel of single input image.. All plugins of this type have opacity, blend mode
        * and blur parameters. Examples: GradientMask, PolyCurveMask, ShapeMask, PolyLineMask.
	*/
	public static final int MASK = 7;

	private int type = FILTER;//default plugin type


	/**
	* One input, one mask input. Default inputs for a node.
	*/
	public static final int DEFAULT_INPUTS = 0;
	/**
	* One input, no mask input. Used for nodes that do filtering operations that do not benefit from 
	* masking functionality.
	*/
	public static final int SINGLE_INPUT = 1;
	/**
	* Two inputs, no mask input. Used for nodes that are meat to combine images from two sources.
	*/
	public static final int MERGE_INPUTS = 2;

	//----------------------------------------------------- INIT METHODS
	/**
	* Initializes plugin of given type with default inputs for plugin type. Other types then <b>FILTER</b> add parameters and user interface components.
	* Use these types when appropriate for added consistency. 
	* @param type Type of plugon created.
	*/ 
	public void initPlugin( int type )
	{
		initPlugin( type, DEFAULT_INPUTS );
	}
	/**
	* Initializes plugin of given type with given inputs. Other types then <b>FILTER</b> add parameters and user interface components.
	* Use these types when appropriate for added consistency. 
	* @param type Type of plugon created.
	*/ 
	public void initPlugin( int type, int inputsMode )
	{
		this.type = type;

		if( type == FILTER )
			initFilter();
		else if( type == STATIC_SOURCE )
			initStaticSource();
		else if( type == MOVING_SOURCE )
			initMovingSource();
		else if( type == FREE_SOURCE )
			initFreeSource();
		else if( type == FULL_SCREEN_MOVING_SOURCE )
			initFullScreenMovingSource();
		else if( type == MASK )
			initMask();
		else if( type == MERGE )
			initMerge();
		//else if( type == THREE_DEE )
		//	initThreeDee();
		else
			throw new PhantomPluginInitError( "Unrecognized plugin type in initPlugin()." );

		if( inputsMode == MERGE_INPUTS || type == MERGE )
		{
			iop.setMaskInput( false );
		}
		if( inputsMode == SINGLE_INPUT )
		{
			iop.setMaskInput( false );
			iop.setInputsCount( 1 );
		}		
	}

	/**
	* Override to define datamodel and plugin properties.
	*/
	public abstract void buildDataModel();

	//--- init plugin type FILTER
	private void initFilter()
	{
		checkInit();
		PluginIOP pluginIop = new PluginIOP( this );
		pluginIop.makeAvailableInFilterStack = true;
		innerInit( pluginIop );
	}
	//--- init plugin type STATIC_SOURCE
	private void initStaticSource()
	{
		checkInit();
		PluginStaticSourceIOP siop = new PluginStaticSourceIOP( this );
		innerInit( siop );
		addStaticSourceEditors();
	}
	//--- init plugin type MOVING_SOURCE
	private void initMovingSource()
	{
		checkInit();
		PluginMovingSourceIOP siop = new PluginMovingSourceIOP( this );
		this.iop = siop;
		editPanel = new ParamEditPanel();
		editPanel.initParamEditPanel();
		buildDataModel();
		addMovingSourceEditors();
		buildEditPanel();
	}
	//--- init plugin type 
	private void initFreeSource()
	{
		checkInit();
		PluginIOP pliop = new PluginIOP( this );
		pliop.setAsSource();
		innerInit( pliop );
	}
	//--- init plugin type FULL_SCREEN_MOVING_SOURCE
	private void initFullScreenMovingSource()
	{
		checkInit();
		PluginFullScreenMovingSourceIOP pliop = new PluginFullScreenMovingSourceIOP( this );
		innerInit( pliop );
		addFullScreenMovingSourceEditors();
	}
	//--- init plugin type MASK
	private void initMask()
	{
		checkInit();
		PluginMaskIOP piop = new PluginMaskIOP( this );
		innerInit( piop );
		addMaskEditors();
	}
	//--- init plugin type MERGE
	private void initMerge()
	{
		checkInit();
		PluginMergeIOP piop = new PluginMergeIOP( this );
		innerInit( piop );
		addMergeEditors();
	}
	//--- Cretaes iop edit panel and 
	private void innerInit( ImageOperation  iop )
	{
		this.iop = iop;
		editPanel = new ParamEditPanel();
		editPanel.initParamEditPanel();
		buildDataModel();
		buildEditPanel();
	}
	//--- Checks for double init.
	private void checkInit()
	{
		if( iop != null )
		{
			PhantomPluginInitError e = new PhantomPluginInitError( "Plugin already initialized." );
			throw e;
		}
	}

	//--------------------------------------------- DATA MODEL
	/**
	* Sets user visible name. User can chnage this later.
	* @param name Plugin name
	*/
	protected void setName( String name )
	{
		iop.setName( name );
	}
	/**
	* Returns user visible name. User can chnage this later.
	* @param name Plugin name
	*/
	public String getName()
	{
		return iop.getName();
	}
	/**
	* Returns type of plugin.
	* @param name Plugin type.
	*/	
	public int getType()
	{
		return type;
	}
	/**
	* Registers parameter. Places a parameter in proper inner data structures. 
	* Persistance and GUI functionality work only on parameters that are registered.
	* @param p Parameter to be regeistered
	*/
	protected void registerParameter( Param p )
	{
		iop.registerParameter( p );
		//--- When initializing in plugin, you are not supposed to put this in your self
		if( p instanceof AnimatedValue )
			((AnimatedValue) p).setIOP( iop );
	}
	/**
	* Registers AnimatedImageCoordinates object to control animated affine transforms.
	* <code>AnimatedImageCoordinates</code> has number of parameter parameters (x,y, XScale,...) that are placed in proper inner data structures. 
	* @throws PhantomPluginInitError If called twice or called on plugin that is not of type FREE_SOURCE, FULL_SCREEN_MOVING_SOURCE.
	*/
	protected void registerCoords()
	{
		if( iop.getCoords() != null )
		{
			PhantomPluginInitError e = new PhantomPluginInitError( "Coords registered twice." );
			throw e;
		}	
		iop.registerCoords( new AnimatedImageCoordinates( iop ) );
	}
	/**
	* Makes static source available as filter stack iop to be used for texturing source image before it is 
	* transformed and blended. Must be called in constructor *after* initialization.
	* @param PhantomPlugin of type STATIC_SOURCE. Will throw error if any other type plugin
	* attemtets to use this method.
	*/
	protected void makeAvailableInFilterStack( PhantomPlugin plugin )
	{
		if( plugin.getType() != STATIC_SOURCE )
		{
			PhantomPluginInitError e = new PhantomPluginInitError( "Only PhantomPlugin of type STATIC_SOURCE can use this method." );
			throw e;
		}
		getIOP().makeAvailableInFilterStack = true;
	}

	//---------------------------------------------- EDIT PANEL
	/**
	* Override to define edit panel displayed in ParamEditor. Some plugin types add editors before
	* or after the user defined editors.
	*/
	public abstract void buildEditPanel();

	/**
	* Adds an editor for a parameter into edit panel. Editors are defined as java <code>Components</code>
	* so they can be created and distributed if those available are not sufficient.
	* Note that editors must take care of providing parameters with names for KeyFrameEditor
	* and they must handle <b> undo registering </b> when parameter values have changed. 
	* See source code in editors for details for details.
	* @param comp Parameter editor that extends jawa.awt.Component. Is almoust always a JPanel extending class.
	*/
	public void addEditor(Component comp)
	{
		editPanel.add( comp );
	}
	/**
	* Adds editors for parameters created by calling <code>registerCoords()</code>.
	*/
	public void addCoordsEditors()
	{
		if( iop.getCoords() == null )
		{
			PhantomPluginInitError e = new PhantomPluginInitError( "Added editors for non existing coords" );
			throw e;
		}
		CoordsEditComponents coords = new CoordsEditComponents( iop );
		editPanel.addComponentsVector( coords.getEditComponents() );
	}
	/**
	* Adds a separator between editors. Basically this is always called between adding editors.
	*/ 
	protected void addRowSeparator()
	{
		editPanel.add( new RowSeparator() );
	}

	/** 
	* Sets panel to use tabbed pane as holder for components.
	* @param height Height of tabbed pane.
	* @param paneNames <code>Vector</code> of strings used as panel titles AND as identifiers when editor components are added.
	*/
	protected void setTabbedPanel( int height, Vector<String> paneNames )
	{
		editPanel.setTabbedPanel( height, paneNames );
	}

	/**
	* Adds component to specified panel.
	*/
	public void addToTab(String tab, Component comp)
	{
		Component c = editPanel.addToTab(tab, comp);
		if( c == null )
		{
			PhantomPluginInitError e = new PhantomPluginInitError( "tabbs not initialized." );
			throw e;
		}
	}
	//-- Adds defauklt editor components for filter type STATIC SOURCE
	private void addStaticSourceEditors()
	{
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iop.opacity );
		IntegerComboBox blendSelect = new IntegerComboBox( 	iop.blendMode,
									"Blend mode",
									ImageOperation.blendModes );
		blendSelect.setMaxComboRows( ImageOperation.blendModes.length );

		addRowSeparator();
		addEditor( opacityEdit );
		addRowSeparator();
		addEditor( blendSelect );
		//addRowSeparator();
		//addEditor( maskOpSelect );
	}
	//-- Adds default editor components for filter type MOVING SOURCE
	private void addMovingSourceEditors()
	{
		CoordsEditComponents coords = new CoordsEditComponents( iop );
		IntegerComboBox blendSelect = 
			new IntegerComboBox( iop.blendMode,
						"Blend mode",
						ImageOperation.blendModes );//so that blendmodes can be changed without recompilation.
		blendSelect.setMaxComboRows( ImageOperation.blendModes.length );
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iop.opacity );

		CheckBoxEditor aOverB = new CheckBoxEditor( ((PluginMovingSourceIOP)iop).useOverRule, "Alpha combine", true );

		editPanel.addComponentsVector( coords.getEditComponents() );
		addRowSeparator();
		addEditor( opacityEdit );
		addRowSeparator();
		addEditor( blendSelect );
		addRowSeparator();
		addEditor( aOverB );

	}
	//-- Adds default editor components for filter type FULL_SCREEN_MOVING_SOURCE
	private void addFullScreenMovingSourceEditors()
	{
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iop.opacity );
		IntegerComboBox blendSelect = 
			new IntegerComboBox( iop.blendMode,
						"Blend mode",
						ImageOperation.blendModes );
		blendSelect.setMaxComboRows( ImageOperation.blendModes.length );
		String[] bgTypes = { "black", "transparent" };
		IntegerComboBox bgSelect = new IntegerComboBox( 	iop.backgroundType,
									"Background type",
									bgTypes );
		CheckBoxEditor aOverB = new CheckBoxEditor( ((PluginFullScreenMovingSourceIOP)iop).useOverRule, "Alpha combine", true );
		addRowSeparator();
		addEditor( opacityEdit );
		addRowSeparator();
		addEditor( blendSelect );
		addRowSeparator();
		addEditor( aOverB );
		addRowSeparator();
		addEditor( bgSelect );
	}
	//-- Adds default editor components for filter type MASK
	private void addMaskEditors()
	{
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iop.opacity );
		CheckBoxEditor invertEdit = new CheckBoxEditor( ((PluginMaskIOP) iop).invert, "Invert", true );
		IntegerNumberEditor blurEdit = new IntegerNumberEditor( "Blur", ((PluginMaskIOP) iop).blur );
		String[] maskInputOps = { "mask", "union", "intersection", "exclusion","difference" };
		IntegerComboBox maskOpSelect = new IntegerComboBox( 	((PluginMaskIOP) iop).maskOp,
									"Alpha",
									 maskInputOps );//so that blenders can br changed.
		addRowSeparator();
		addEditor( opacityEdit );
		addRowSeparator();
		addEditor( invertEdit );
		addRowSeparator();
		addEditor( blurEdit );
		addRowSeparator();
		addEditor( maskOpSelect );
	}
	//-- Adds default editor components for filter type MERGE
	private void addMergeEditors()
	{
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iop.opacity );
		IntegerComboBox blendSelect = 
			new IntegerComboBox( iop.blendMode,
						"Blend mode",
						ImageOperation.blendModes );
		IntegerNumberEditor blurEdit = new IntegerNumberEditor( "Blur", ((PluginMergeIOP) iop).blur );

		addRowSeparator();
		addEditor( opacityEdit );
		addRowSeparator();
		addEditor( blendSelect );
		addRowSeparator();
		addEditor( blurEdit );
	}

	//---------------------------------------------- RENDER METHODS
	/**
	* Override to implement plugin functionality for plugins of type FILTER, STATIC_SOURCE, MOVING_SOURCE, 
	* and FREE_SOURCE.
	* Called when output images are rendered.
	*/
	public void doImageRendering( int frame ){}
	/**
	* Returns the image that has been rendered in flow above of null if node is a leaf.
	*/
	public BufferedImage getFlowImage()
	{
		return iop.getRenderedImage();
	}
	/**
	* Returns the image that has been rendered in flow above from the right hand side input, if 
	* such input exists, or null if input does not exist or is not connected to source.
	*/
	public BufferedImage getMergeImage()
	{
		Vector<BufferedImage> imgs = iop.getSourceImages();
		if( imgs.size() < 2 )
			return null;
		return (BufferedImage) imgs.elementAt( 1 );
	}
	/**
	* Returns BufferedImage of registered FileSource for frame. Will crash if file source not registered using.
	* <code>getIOP().registerFileSource( fs )</code>.
	* Getting a file source object currently not possible at plugin level, FIX ME.
	*/
	public BufferedImage getFileSourceImage( int frame )
	{
		FileSource fs = getIOP().getFileSource();

		if( fs.getType() == FileSource.IMAGE_FILE )
		{
			FileSingleImageSource fsis = (FileSingleImageSource) fs;
			if( !fsis.dataInMemory() ) 
				fsis.loadData();
			return  fsis.getBufferedImage();
		}
		if( fs.getType() == FileSource.IMAGE_SEQUENCE ) 
		{
			return null;
			//return new ImageSequenceIOP( (FileSequenceSource) fs );
		}

		
		return null;// if we ever get here, this should crash.
	}
	/**
	* Returns AnimatedImageCoordinates object that controls animation affine transforms of plugin.
	* @return AnimatedImageCoordinates object or <code>null</code> if plugin is of type that does not have this object. 
	*/
	public AnimatedImageCoordinates getCoords()
	{
		return iop.getCoords();
	}
	/**
	* Returns clip frame for current timeline frame.
	* @param frame Current frame that is provided.  
	* @return Clip frame.
	*/
	public int getClipFrame( int frame )
	{
		return iop.getClipFrame( frame );
	}
	/**
	* Type FULL_SCREEN_MOVING_SOURCE plugins override this to draw step images for motionblur or a single image.
	* Time is given in float frame time and drawing must use <code>AnimatedValue.getValue( float movieFrame )</code>
	* to get slightly different step images for motion blur.
	*/
	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height ){}
	/**
	* Type MASK and MERGE plugins override this to draw step images for motionblur or a single image.
	* Time is given in float frame time and drawing must use <code>AnimatedValue.getValue( float movieFrame )</code>
	* to get slightly different step images for motion blur.
	*/
	public void renderMask( float frame, Graphics2D g, int width, int height ){}
	/**
	* Sends filtered image forward in flow. Applies input mask if exists.
	* Use this method tohether with  <code>getFlowImage()</code> method, apply filtering to received image and
	* then send forward in flow using this method.
	* Filtering plugins also can be created using method <code>applyFilter( BufferedImageOp filter )</code>.
.	* This should called as last method in <code>doImageRendering( int frame, Vector sourceImages )</code>.
	* @param source Filtered Image that is send down the render flow.
	* @param frame Current frame that is provided  
	*/
	public void sendFilteredImage( BufferedImage source, int frame )
	{
		iop.setRenderedImage( source );//mask combine done ImageOperation.renderImage(..)
	}
	/**
	* Sends static source image forward in flow. Combines image with image from above
	* in the flow (using input mask if exists) or sends it as is if plugin is in a leaf node..
.	* This should called as last method in <code>doImageRendering( int frame, Vector sourceImages )</code>.
	* @param source Source image that is send down the render flow.
	* @param frame Current frame that is provided.
	*/
	public void sendStaticSource( BufferedImage source, int frame )
	{
		((PluginStaticSourceIOP) iop).processStaticSource( source, frame );
	}
	/**
	* Sends image forward in flow. This should called as last method in 
	*  <code>doImageRendering( int frame, Vector sourceImages )</code>.
	* With <b>FILTER</b> plugins <code>applyFilter( BufferedImageOp filter )</code>.can be used instead.
	* @param source Image that is send down the render flow.
	* @param frame Current frame that is provided. 
	*/
	/*
	public void sendMovingSource( BufferedImage source, int frame )
	{
		((PluginMovingSourceIOP)iop).renderMovingBlendedImage( frame, source );
	}
	*/
	/**
	* Applies a filter on image received from flow. Image is then passed on.  Use with <b>FILTER</b> plugins.
	* If plugin has a mask input and it is connected to aonother node then filtering is not applied on masked area.
	* @param filter Bitmap filter
	*/
	public void applyFilter( BufferedImageOp filter )
	{
		if( !(iop instanceof PluginIOP) )
			System.out.println( this.getClass().getName() + ": Trying to apply filter on non-filter plugin." );
		iop.filterRenderedImage( filter );
	}

	//---------------------------------------------------- EDIT LAYER
	/**
	* Returns editor layer for plugin. Overriding this to create edit layers directly is deprecated, override <code>getPluginEditLayer()</code>.
	*/
	public ViewEditorLayer getEditorLayer()
	{
		AbstractPluginEditLayer editLayer = getPluginEditLayer();
		if( editLayer == null )
			return null;

		return editLayer.getLayerObject();
	}

	/**
	* Returns <code>PluginEditLayer</code> for plugin. Override to return custom edit layers for plugin.
	*/
	public AbstractPluginEditLayer getPluginEditLayer()
	{
		return null;
	}
	/**
	* Returns editor panel for plugin. Used internally by phantom2D. 
	*/
	public ParamEditPanel getEditPanel(){ return editPanel; }
	
	/**
	* Returns ImageOperation object that is the functional part of plugin. 
	* Needed when creating editor layers which need this. 
	*/
	public ImageOperation getIOP(){ return iop; }

	//------------------------------------------------- FOR INTERNAL USE
	/**
	* Called when aplhabetizing plugins for editor menus.
	*/
	public int compareTo( Object anotherObj )
	{
		ImageOperation compIOP = null;
		if( anotherObj instanceof ImageOperation )	
			compIOP = (ImageOperation) anotherObj;
		else
			compIOP = ((PhantomPlugin) anotherObj).getIOP();

		return iop.getName().compareTo( compIOP.getName() );
	}

}//end class
