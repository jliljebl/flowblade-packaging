package animator.phantom.renderer;

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

import giotto2D.blending.BigImageStaticBlender;
import giotto2D.blending.ImageBlender;
import giotto2D.filters.merge.AlphaReplace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import animator.phantom.blender.Blender;
import animator.phantom.controller.AppData;
import animator.phantom.controller.Application;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.ProjectController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.PHScrollUI;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimationParentPanel;
import animator.phantom.paramedit.FilterStackPanel;
import animator.phantom.paramedit.MaskStackPanel;
import animator.phantom.paramedit.MaskSwitchPanel;
import animator.phantom.paramedit.OnOffPanel;
import animator.phantom.paramedit.ParamEditResources;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.StackEditExitPanel;
import animator.phantom.paramedit.SwitchPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;
import animator.phantom.renderer.parent.AbstractParentMover;

//--- Base class for all operations that are combined in render flow to create output.
public abstract class ImageOperation implements Comparable<Object>
{
	//--- Image operations that have maximum length: movie clips, frame sequences, audio clips
	public static final int NOT_FREE_LENGTH = 0;
	//--- Image operations that can be arbitrarely long: graphics, procedural sources, filters
	public static final int FREE_LENGTH = 1;
	//--- Inoput mask op types
	public static final int INPUT_MASK_BLEND_MASK = 0;
	public static final int INPUT_MASK_OUTPUT_ALPHA = 1;
	//--- Used to determine if scrollpane should be used or not
	private static final int SCROLL_HEIGHT_PAD = 125;
	//private static final int SCROLL_TEST_PAD = 30;
	//--- Looping modes
	public static final int NO_LOOPING = 0;
	public static final int LOOP = 1;
	public static final int PING_PONG = 2;
	//--- Default interpolation, Used if switches not created.
	public static int DEFAULT_INTERPOLATION = SwitchData.NEAREST_NEIGHBOR;
	//--- Backgroundtypes for source images.
	public static final int BLACK_BACKGROUND = 0;
	public static final int TRANSPARENT_BACKGROUND = 1;
	//--- Filter tack size
	public static final int STACK_MAX_SIZE = 7;
	//--- If this is a plugin iop, plugin sets reference of self here
	protected PhantomPlugin plugin = null;
	//--- Clip type. FREE_LENGTH  or NOT_FREE_LENGTH
	protected int clipType = FREE_LENGTH;
	//--- The default number of sources for operation.
	//--- Extending may set different value in constructor.
	//--- Number of sources is final after instantation.
	protected int DEFAULT_SOURCES_COUNT = 2;
	//--- The default number of targets for operation.
	//--- Extending may set different value in constructor
	//--- Number of outputs can be later changed by user.
	protected int DEFAULT_TARGETS_COUNT = 1;
	//--- NO_INPUT_MEANS_NO_OP is true for all non-source ImageOperations.
	//--- It means that ImageOperation needs input to be meaningfull.
	//--- if true, sourceImages.elementAt( 0 ) == null means iop will not be rendered
	protected boolean NO_INPUT_MEANS_NO_OP = true;
	//--- Name displayd to user.
	protected String name = "name not set";
	//--- Incremented ID for registered parameters.
	private int nextParamId = 0;
	//--- All parameters used to define rendering and animation operations.
	protected Vector<Param> parameters = new Vector<Param>();
	//--- All parameters that have keyframes. Subset of parameters.
	protected Vector <KeyFrameParam> kfParams = new Vector<KeyFrameParam>();
	//--- Vector (set) of keyframes in extending object, no duplicates, Used by GUI.
	protected Vector <AnimationKeyFrame> drawKeyFrames = new Vector<AnimationKeyFrame>();
	//--- Basic 2D animation parameters.
	private AnimatedImageCoordinates animatedCoordinates = null;
	//--- The blend this ImageOperation uses when rendered.
	public IntegerParam blendMode = new IntegerParam( ImageBlender.NORMAL );
	//---  NOT USED. The operation used when applying input mask.
	public IntegerParam inputMaskOp = new IntegerParam( INPUT_MASK_BLEND_MASK );
	//--- If this flag is true and inputMaskOp.get() == IntegerParam( INPUT_MASK_BLEND_MASK
	//--- do combine blend between received and rendered image.
 	public boolean doInputMaskCombineBlend = false;
	//--- Opacity, may not be used at all or if animatedCoordinates used, then opacity defined there.
	public AnimatedValue opacity = null;
	//--- Flag for on / off state of this ImageOperation. Off means that this is skipped when rendering
	protected boolean isOn = true;
	//--- Backgroud type created for source images
	public IntegerParam backgroundType = new IntegerParam( BLACK_BACKGROUND );
	//--- File source of this instance. Only certain type of iops use this.
	private FileSource fileSource = null;
	//--- Switches data of this instance.
	public SwitchData switches = null;
	//--- Extending classes perform their render operations on this. This is the image received from above in the rendering flow.
	protected BufferedImage renderedImage;
	//--- Received source images. renderedImage == sourceImgs.elementAt( 0 )
	private Vector<BufferedImage> sourceImgs;
	//--- Copy of received image for alpha operations using inputMask. This is copy of renderedImage before rendering in this iop is done.
	private BufferedImage receivedCopy;
	//--- Mask used when rendering this iop
	private BufferedImage inputMask;
	//--- Filters that are applied before doing affine transform.
	private Vector<ImageOperation> filterStack = new  Vector<ImageOperation>();
	//--- True if iop is in Filter stack
	//--- Filter stack iops are not part of flow and thus cannot have some attributes.
	protected boolean isFilterStackIop = false;
	//--- If set true this is made available to be added to filter stack.
	//--- PhantomPlugin.FILTER are always available with automated exceptions and don't need this, this is used for
	//--- special casing.
	public boolean makeAvailableInFilterStack = false;
	//--- If set true this is made available to be added layer mask stack.
	public boolean makeAvailableForLayerMasks = false;
	//--- If true only visible and motionblur checkboxes displayed
	private boolean reducedSwitches = false;
	//--- if true, a button enabling centering anchor point is presented to user
	private boolean centerable = false;
	//--- First frame of ImageOperation in the timeline.
	protected int beginFrame = 0;
	//--- Maximum length of program. Is discarded if FREE_LENGTH. Default value not meaningfull.
	protected int maxLength = 20;
	//--- Clip is a range of frames given in timeline frames in the area defined by beginFrame + maxLength.
	protected int clipStartFrame = 0;
	//--- Clip is a range of frames given in timeline frames in the area defined by beginFrame + maxLength.
	protected int clipEndFrame = 19;
	//--- Looping mode
	private int looping = NO_LOOPING;
	//--- Timeline edit lock.
	private boolean locked = false;
	//--- Type of parent.
	public int parentMoverType = -1;
	//--- Node id of parent.
	public int parentNodeID = -1;
	//--- Iop in movement parent node
	public ImageOperation parentIOP = null;
	//---
	private AbstractParentMover parentMover = null;

	//--- The panel used to edit parameters
	protected ParamEditPanel editPanel;
	//--- The gui holding all edit components, edit panel + switch panel + name label
	private JPanel editFrame;
	//--- Panel with on/aff switch and type specific other swiches.
	private JPanel switchPanel;
	//--- Some IOPs do not have mask inputs.
	//--- RenderNode and FlowBox must know if that is the case.
	private boolean hasMaskInput = true;

	//--- animatedCoordinates access lock. Used to prevent bugs caused by the way that parenting is implemented.
	private Object acLock = new Object();
	//--- Used by FrameRenderer.
	private boolean isLeaf = false;

	//--- GUI stuff
	private static ImageIcon viewEditIcon = GUIResources.getIcon( GUIResources.viewEditorLabel );
	private static ImageIcon noViewEditIcon = GUIResources.getIcon( GUIResources.noViewEditorLabel );
	private static ImageIcon parentIcon = GUIResources.getIcon( GUIResources.parentLabel );
	private static ImageIcon filterStackIcon = GUIResources.getIcon( GUIResources.filterStackLabel );

	//--- BlendModes. These come from Blender.
	public static String[] blendModes = null;

	//--- Get blend modes to avoid hardcoding.
	static
	{
		blendModes = Blender.getBlendModes();
	}

	//--- FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK
	//--- FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK
	//--- FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK, FILTER STACK
	//--- Applies filter stack to copy of source image.
	//--- change or improve with thred info for multithreaded rendering
	private boolean renderingFilterStack = false;
	private Dimension filterStackDimension = null;

	public BufferedImage applyFilterStack( int frame, ImageOperation iop, BufferedImage img )
	{
		if( iop.getFilterStack().size() == 0 )
			return img;


		BufferedImage imgCopy = PluginUtils.getImageClone( img );
		Vector<ImageOperation> filterStack = iop.getFilterStack();

		BigImageStaticBlender blender = null;

		for( int i = 0; i < filterStack.size(); i++ )
		{
			ImageOperation stackIop = filterStack.elementAt( i );
			stackIop.renderingFilterStack = true;
			stackIop.filterStackDimension = new Dimension( img.getWidth(), img.getHeight() );
			Vector<BufferedImage> sourceImages = new Vector<BufferedImage>();
			sourceImages.add( imgCopy );

			// NOTE: this hard codes all raw iops as filters in filter stack.
			if( stackIop.getPlugin() == null || stackIop.getPlugin().getType() == PhantomPlugin.FILTER )
			{
				stackIop.renderImage( frame, sourceImages );
			}
			else // this hard codes all others as type STATIC_SOURCE plugins.
			{
				PluginStaticSourceIOP pSource = (PluginStaticSourceIOP) stackIop;
				pSource.renderImage( frame, sourceImages );
				BufferedImage result = pSource.consumeFilterStackImage();
				if( blender == null )
				{
					blender = new BigImageStaticBlender( new Dimension( imgCopy.getWidth(), imgCopy.getHeight() ));
				}
				blender.blendImages( 	imgCopy,
							result,
							pSource.opacity.get( frame ) / 100.0f,
							pSource.blendMode.get() );
			}
		}

		return imgCopy;
	}

	public boolean renderingFilterStack(){ return renderingFilterStack; }

	public Dimension getFilterStackDimension(){ return filterStackDimension; }


	//--- PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING
	//--- PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING
	//--- PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING, PLUGIN HANDLING
	//--- If this iop is a part of a plugin then
	//--- plugin places reference for its self using this.
	public void setPlugin( PhantomPlugin plugin )
	{
		this.plugin = plugin;
	}
	public PhantomPlugin getPlugin()
	{
		return plugin;
	}
	public boolean isPluginIOP(){ return plugin != null; }

	public boolean isOutput(){ return false; }

	//--- PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING
	//--- PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING
	//--- PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING, PARAMETERS HANDLING
	//--- Registers parameter of extending class for saving and keyframe gui handling.
	public void registerParameter( Param p )
	{
		parameters.add( p );
		if( p instanceof KeyFrameParam ) kfParams.add( (KeyFrameParam) p );
		p.setIOP( this );//--- for undo
		setParamId( p );//--- for persistance
	}
	/*
	public void registerParamVector( Vector<Param> vec )
	{
		for( int i = 0; i < vec.size(); i++ )
			registerParameter( (Param)vec.elementAt( i ) );
	}
	*/
	//--- Registers 2D animation parameters.
	public void registerCoords( AnimatedImageCoordinates coords )
	{
		animatedCoordinates = coords;
		Vector<AnimatedValue> params =  animatedCoordinates.getParamsVector();
		for( int i = 0; i < params.size(); i++ )
		{
			Param p = (Param) params.elementAt( i );
			registerParameter( p );
		}
	}
	//--- Gives newly registered Param a id String for persistence
	private void setParamId( Param p )
	{
		p.setID( Integer.toString( nextParamId ));
		nextParamId++;
	}
	//--- Returns animatedCoordinates object which holds basic 2d params.
	//--- synchronized access because of parent replacing it when rendering
	public AnimatedImageCoordinates getCoords()
	{
		synchronized( acLock )
		{
			return animatedCoordinates;
		}
	}
	public void setCoords( AnimatedImageCoordinates coords )
	{
		animatedCoordinates = coords;
	}
	//---
	public void registerFileSource( FileSource fs )
	{
		fileSource = fs;
	}
	//---
	public FileSource getFileSource(){ return fileSource; }
	//--- SwitchData is needed for iops with moving blended source images
	protected void setIOPToHaveSwitches()
	{
		setIOPToHaveSwitches( false );
	}
	protected void setIOPToHaveSwitches( boolean useReducedSwitchesDisplay )
	{
		switches = new SwitchData();
		reducedSwitches = useReducedSwitchesDisplay;
	}
	public void setCenterable(){ centerable = true; }
	public boolean getCenterable(){ return centerable; }
	public SwitchData getSwitches(){ return switches; }
	//---
	public Vector<KeyFrameParam> getKeyFrameParams(){ return kfParams; }
	//--- Returns all key frame params as params
	public Vector<Param> getkeyFrameParamsAsParams()
	{
		Vector<Param>  rVec = new Vector<Param> ();
		for( Param p : parameters )
			if( p instanceof KeyFrameParam ) rVec.add(0, p );
		return rVec;
	}
	//--- Creates Vector to be used when keyframes drawn in timeline editor gui.
	public void createKeyFramesDrawVector()
	{
		drawKeyFrames.clear();
		for( int i = 0; i < kfParams.size(); i++ )
		{
			KeyFrameParam p = kfParams.elementAt( i );
			Vector <AnimationKeyFrame> testVec = p.getKeyFrames();
			for( int j = 0; j < testVec.size(); j++ )
			{
				AnimationKeyFrame kf = testVec.elementAt( j );
				if( !keyFrameInFrameExists( kf.getFrame() ) ) drawKeyFrames.add( kf );
			}
		}
	}
	//--- Tests if iop has keyframe in specified frame.
	private boolean keyFrameInFrameExists( int frame )
	{
		for( AnimationKeyFrame keyFrame: drawKeyFrames )
			if( keyFrame.getFrame() == frame ) return true;

		return false;
	}
	//--- Used by GUI
	public Vector <AnimationKeyFrame> getDrawKeyFrames(){ return drawKeyFrames; }
	//--- I/O time access
	public Vector<Param> getParameters()
	{
		return parameters;
	}
	//--- I/O time access
	public Param getParam( String name )
	{
		for( int i = 0; i < parameters.size(); i++ )
		{
			Param p = parameters.elementAt( i );
			if( p.getID().equals( name ) ) return p;
		}
		return null;
	}
	//--- debug
	/*
	public void printParams()
	{
		System.out.println( "parameters.size()" + parameters.size() );

		for( int i = 0; i < parameters.size(); i++ )
		{
			Param p = parameters.elementAt( i );
			System.out.println( p.getParamName() );
		}
	}
	*/

	//--- MOVEMENT RENDERING, MOVEMENT RENDERING, MOVEMENT RENDERING, MOVEMENT RENDERING
	//--- MOVEMENT RENDERING, MOVEMENT RENDERING, MOVEMENT RENDERING, MOVEMENT RENDERING
	//--- Called when rendered. Parent-child transformers use this.
	public void renderMoves( int frame )
	{
		//--- Frame has to be in clip area for any animation to be attempted.
		if( !frameInClipArea( frame ) ) return;
		//--- IOP has to active to render.
		if( !isOn ) return;
		doAnimation( frame );
	}
	//--- Extending classes MAY overload to perform animations.
	//--- This is the method for creating animation functionality.
	protected void doAnimation( int frame ){}


	//--- IMAGE RENDERING, IMAGE RENDERING, IMAGE RENDERING, IMAGE RENDERING
	//--- IMAGE RENDERING, IMAGE RENDERING, IMAGE RENDERING, IMAGE RENDERING
	//--- Gets frame and Vector of source images.
	//--- Calls doImageRendering() overridden by extending classes to render image
	//--- Rendered image is passed on when getRenderedImage() is called.in rendering prosess.
	//--- ImageOperations that are not sources do nothing when sourceImages.elementAt( 0 ) == null.
	//--- ImageOperations tha are sources can create renderedImage.
	public final void renderImage( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- Get rendered image. It may be null, but sourceImages.size() > 0, always.
		renderedImage = sourceImages.elementAt( 0 );
		sourceImgs = sourceImages;

		//--- If this is not in timeline clip area do nothing.
		if( !frameInClipArea( frame ) && looping == NO_LOOPING )
			return;
		//--- IOP has to be active to render.
		if( !isOn ) return;
		//--- Do nothing if renderedImage is null and this needs input to be meaningful.
		if( renderedImage == null && NO_INPUT_MEANS_NO_OP ) return;
		//--- Copy received image if needed to do blend between rendered and unrendered image.
		if( doReceivedCopyCombine() && inputMask != null )
		{
			receivedCopy = RenderNode.getImageClone( renderedImage );
		}
		//--- if were looping (out of clip area at this point) get the looped frame that
		//--- corresponds to a frame in clip area.
		int loopedFrame = getLoopedFrame( frame );
		//--- AnimCoords are accessed elsewhere too so access to them needs to be synchronized
		//--- because they are being temporarily replaced with a clone here.
		synchronized( acLock )
		{
			//--- Now we have meaningful input for all types of iops.
			//--- Do coordinate transform.
			//--- Looped is transformed with both looped and unlooped coords
			if( looping == NO_LOOPING )
				transformCoordinates( frame );
			else
				transformLoopedCoordinates( frame, loopedFrame );
			//--- Render image. Looped iop uses looped frame
			if( looping != NO_LOOPING )
				frame = loopedFrame;

			doImageRendering( frame, sourceImages );//--- ALL IMAGE MANIPULATION HAPPENS HERE

			//--- Reset coordinate transform.
			resetOriginalCoordinates();
		}
		//--- Do blend between rendered and unrendered image using input mask id needed.
		if( doReceivedCopyCombine() && inputMask != null )
		{
			applyInputMask( renderedImage );
			combineFilterResult( renderedImage, receivedCopy );
			renderedImage = receivedCopy;
		}
		//--- Release resources
		receivedCopy = null;
		inputMask = null;
		sourceImgs = null;
	}
	//--- Overridden by extending classes to do rendering
	//--- This is the most important method of extending classes.
	protected void doImageRendering( int frame, Vector<BufferedImage> sourceImages ){}
	//--- Replaces imges alpha channel with input mask if we have one.
	protected void applyInputMask( BufferedImage maskTarget )
	{
		if( inputMask != null ) AlphaReplace.filter( maskTarget, inputMask );
	}
	//--- Combines non-source filtered image with unfiltered image.
	private void combineFilterResult( BufferedImage source, BufferedImage dest )
	{
		Graphics g = dest.getGraphics();
		g.drawImage( source, 0, 0, null );
	}
	//--- Passes on the rendered image.
	public  BufferedImage getRenderedImage(){ return renderedImage; }
	//--- Used by plugins
	public void setRenderedImage( BufferedImage img ){ renderedImage = img; }
	//--- Used by merge plugins.
	public Vector<BufferedImage> getSourceImages(){ return sourceImgs; }
	//--- Help method for filtering renderedImage.
	public void filterRenderedImage( BufferedImageOp filter )
	{
		Graphics2D gc = renderedImage.createGraphics();
		gc.drawImage( renderedImage, filter, 0, 0 );
		gc.dispose();
	}
	/*
	protected BufferedImage getFileSourceImage()
	{
		BufferedImage rImage = fileSource.getBufferedImage();
		return rImage;
	}
	*/
	//--- Makes sure that no dimension of rendered image is bigger then screen size
	//--- and crop into screen size if needed
	public void cropRenderedImageToScreenSize()
	{
		Dimension screenSize = ProjectController.getScreenSize();
		if( 	screenSize.width >= renderedImage.getWidth()
			&& screenSize.height >= renderedImage.getHeight() )
			return;

		int w = Math.min( screenSize.width, renderedImage.getWidth()  );
		int h = Math.min( screenSize.height, renderedImage.getHeight()  );
		BufferedImage newImg = renderedImage.getSubimage( 0, 0, w, h);
		renderedImage = newImg;
	}


	//--- COORDINATE TRANSFORMING, COORDINATE TRANSFORMING, COORDINATE TRANSFORMING
	//--- COORDINATE TRANSFORMING, COORDINATE TRANSFORMING, COORDINATE TRANSFORMING
	//--- COORDINATE TRANSFORMING, COORDINATE TRANSFORMING, COORDINATE TRANSFORMING
	//--- Transforms 2D coordinates for drawing. Transform set by parent mover.
	//--- Called before image is rendered.
	protected void transformCoordinates( int frame )
	{
		//--- Tests if parent transform set.
		if( parentMoverType == -1 ) return;
		parentMover = AbstractParentMover.getMover( parentMoverType );
		parentMover.setParent( parentIOP );
		//--- Do transform and save original coordinates in parent mover for later use.
		animatedCoordinates = parentMover.doTransform( this, frame );
	}
	//--- Transforms 2D coordinates for drawing. Transform set by parent mover.
	//--- Called before image is rendered.
	//--- When looping child coords are looped, parents coords are not.
	protected void transformLoopedCoordinates( int realFrame, int loopedFrame )
	{
		//--- Tests if parent transform set.
		if( parentMoverType == -1 ) return;
		parentMover = AbstractParentMover.getMover( parentMoverType );
		parentMover.setParent( parentIOP );
		//--- Do transform and save original coordinates in parent mover for later use.
		animatedCoordinates = parentMover.doLoopedTransform( this, realFrame, loopedFrame );
	}
	//--- Puts animatedCoordinates reference back to images own, untransformed coordinates.
	//--- Called after rendering of ImageOperation instance done.
	//--- Also clears parent mover which needs to be created for every frame.
	protected void resetOriginalCoordinates()
	{
		//--- Test if transform set.
		if( parentMoverType == -1 ) return;
		//--- Set original untransformed coordinates.
		animatedCoordinates = parentMover.getUntransformed();
	}
	//---
	public void setParentMover( int parentMoverType, int parentNodeID, ImageOperation pIOP )
	{
		this.parentMoverType = parentMoverType;
 		this.parentNodeID = parentNodeID;
		parentIOP = pIOP;
	}
	//---
	public ImageOperation getParent(){ return parentIOP; }
	//--- Called when parent node changed or at load time
	public void loadParentIOP( RenderFlow flow )
	{
		if( parentNodeID == -1 ) return;
		parentIOP = ((RenderNode) flow.getNode( parentNodeID )).getImageOperation();
	}


	//--- FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING
	//--- FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING
	//--- FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING, FLOW CHANGE LISTENING
	//--- Called when flow changed.
	//--- Extending classes may override if they need to update GUI when flow has changed.
	//public void flowChanged(){}


	//--- BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE
	//--- BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE
	//--- BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE, BASIC INTERFACE
	public String getName(){ return name; }
	public void setName( String name_ ){ this.name = name_; }
	public String getClassIDName() // plugins and raw IOPs have different identyfying class names.
	{
		if (plugin == null)
			return getClass().getName();
		else
			return plugin.getClass().getName();
	}

	//---
	public int getDefaultNumberOfSources(){ return DEFAULT_SOURCES_COUNT; }
	//---
	public int getDefaultNumberOfTargets(){ return DEFAULT_TARGETS_COUNT; }
	//---
	public Vector<ImageOperation> getFilterStack(){ return filterStack; }
	public void setFilterStack( Vector<ImageOperation> newStack ){ filterStack = newStack; }
	//---
	//protected void setEditPanel( ParamEditPanel editPanel){ this.editPanel = editPanel; }
	//--- This is called after iop add and project load.
	//--- Extending MUST override to provide edit panels.
	public abstract ParamEditPanel getEditPanelInstance();
	//--- Returns edit panel and creates new if necessary.
	public synchronized ParamEditPanel getEditPanel()
	{
		if( editPanel == null ) editPanel = this.getEditPanelInstance();
		return editPanel;
	}
	//--- Called after user rename
	public void reCreateEditPanel()
	{
		editPanel = null;
		getEditFrame( true );
	}
	//--- If isOn true, this iop is rendered.
	public void setOnOffState( boolean isOnNow )
	{
		isOn = isOnNow;

		if( switchPanel == null )
			return;

		//--- Update switches gui
		if( switches == null )
			(( OnOffPanel) switchPanel).setOnOff( isOn );
		else if( reducedSwitches )
			(( MaskSwitchPanel) switchPanel).setOnOff( isOn );
		else
			(( SwitchPanel) switchPanel).setOnOff( isOn );
	}
	public boolean isOn(){ return isOn; }
	//--- Get and set for instance being a leaf in the render flow.
	public boolean isLeaf(){ return isLeaf; }
	public void setIsLeaf( boolean value ){ isLeaf = value; }
	//--- POssible values: bicubic, bilinear, nearest neighbour.
	public int getInterpolation()
	{
		if( switches == null ) return DEFAULT_INTERPOLATION;
		else return switches.interpolation;
	}
	//--- normal, hardlight, add ...
	public int getBlendMode(){ return blendMode.get(); }
	//--- SwitchData.fineEdges property, default false
	public boolean getFineEdges()
	{
		if( switches == null ) return false;
		else return switches.fineEdges;
	}
	//--- Motion blur, default false
	public boolean getMotionBlur()
	{
		if( switches == null ) return false;
		else return switches.motionBlur;
	}
	//---
	public boolean doReceivedCopyCombine()
	{
		// do for filters
		if( NO_INPUT_MEANS_NO_OP )
			return true;

		// do if flag set by extending class like all extending MovingBlendedIOP
		if( doInputMaskCombineBlend )
			return true;

		return false;
	}
	//--- Sets iop to be source that will be rendered even without input
	public void setAsSource()
	{
		NO_INPUT_MEANS_NO_OP = false;
	}
	//--- Sets number of inputs, INCLUDING mask input.
	public void setInputsCount( int val ){ DEFAULT_SOURCES_COUNT = val; }
	public int getInputsCount(){ return DEFAULT_SOURCES_COUNT; }
	//---
	public boolean hasMaskInput(){ return hasMaskInput; }
	public void setMaskInput( boolean val ){ hasMaskInput = val; }
	//--- Mask inout is always last source input
	public int getMaskInputIndex(){ return DEFAULT_SOURCES_COUNT - 1; }
	public BufferedImage getInputMask(){ return inputMask; }
	public void setMaskInputImage( BufferedImage mimg ){ inputMask = mimg; }
	//---
	public int getLooping(){ return looping; }
	public void setLooping( int val ){ looping = val; }
	//---
	public boolean getLocked(){ return locked; }
	public void setLocked( boolean val ){ locked = val; }
	//---
	public boolean isFilterStackIOP(){ return isFilterStackIop; }
	public void setFilterStackIOP( boolean val ){ isFilterStackIop = val; }


	//--- CREATING PANEL, CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL
	//--- CREATING PANEL, CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL
	//--- CREATING PANEL, CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL CREATING PANEL
	//--- Returns and builds edit frame panel for iop.
	public synchronized JPanel getEditFrame( boolean destroyFirst )
	{
		if ( destroyFirst == true )
			editFrame = null;

		if( editFrame == null )
		{
			boolean scrollPaneNeeded = true;

			editFrame = new JPanel();
			editFrame.setLayout(new BoxLayout( editFrame, BoxLayout.Y_AXIS));
			editFrame.add( getNamePanel() );
			editFrame.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

			//--- Must have switches or OnOffPanel
			AnimationParentPanel animParentPanel = null;
			FilterStackPanel filterStackPanel = null;

			if( switches == null && isFilterStackIop == true )
				switchPanel = new OnOffPanel( this );
			else if( switches == null && isFilterStackIop == false )
				switchPanel = null;
			else if( reducedSwitches )
				switchPanel = new MaskSwitchPanel( this );
			else
			{
				if( isFilterStackIop == false )
				{
					animParentPanel = new AnimationParentPanel( this );
					filterStackPanel = new FilterStackPanel( this );
				}
				switchPanel = new SwitchPanel( this );
			}

			if( switchPanel != null )
				editFrame.add( switchPanel );

			JPanel addPanel = editFrame;
			if( scrollPaneNeeded )
			{
				addPanel = new JPanel();
				addPanel.setLayout(new BoxLayout( addPanel, BoxLayout.Y_AXIS));
			}
			addPanel.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
			addPanel.add( getEditPanel() );
			addPanel.add( new RowSeparator() );



			if( filterStackPanel != null )
			{
				addPanel.add( Box.createRigidArea( new Dimension( 0, 24 ) ) );
				addPanel.add( filterStackPanel );
			}

			if( isFilterStackIop == false )
			{
				addPanel.add( Box.createRigidArea( new Dimension( 0, 24 ) ) );
				MaskStackPanel maskStackPanel = new MaskStackPanel( this );
				addPanel.add( maskStackPanel );
			}
			
			if ( animParentPanel != null )
			{
				addPanel.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
				addPanel.add( animParentPanel );
			}
			
			if( isFilterStackIop == true )
			{
				addPanel.add( Box.createRigidArea( new Dimension( 0, 24 ) ) );
				addPanel.add( new StackEditExitPanel() );
			}

			if( scrollPaneNeeded )
			{
				JScrollPane scrollPane = new JScrollPane( addPanel,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

				scrollPane.setPreferredSize(
					new Dimension( ParamEditResources.PARAM_COLUMN_WIDTH * 2,
							Application.getParamEditHeight() - SCROLL_HEIGHT_PAD ) );
				JScrollBar vsb = scrollPane.getVerticalScrollBar();
				vsb.setUI( new PHScrollUI() );
				editFrame.add( scrollPane );
			}
		}

		return editFrame;
	}
	//--- Buils and returns name panel.
	public JPanel getNamePanel()
	{
		JLabel name = new JLabel();
		Color bgColor = GUIColors.MEDIA_ITEM_SELECTED_BG;

		if( !isFilterStackIop )
			name.setText( getName() );
		else
		{
			name.setText( getName() + " <Stack>" );
			bgColor = GUIColors.filterStackColor;
			name.setForeground( new Color( 50, 50, 50 ) );
		}

		String idStr = "";
		if( !isFilterStackIop )
		{
			RenderNode node = AppData.getFlow().getNode( this );
			idStr =  "#" + Integer.toString( node.getID() );
		}	
		JLabel idLabel = new JLabel( idStr );
		idLabel.setForeground(new Color(140, 140, 140));
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout( namePanel, BoxLayout.X_AXIS));
		namePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
		namePanel.add( idLabel );
		namePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
		namePanel.add( name );
		namePanel.add( Box.createHorizontalGlue() );
		namePanel.setBackground( bgColor );

		JLabel viewLabel = null;
		if( getEditorlayer() != null )
		{
			viewLabel = new JLabel( viewEditIcon );
			viewLabel.setToolTipText("ImageOperation can be edited in ViewEditor" );
		}
		else
		{
			viewLabel = new JLabel( noViewEditIcon );//to keep heights equal in both

		}
		namePanel.add( viewLabel );
		if( parentNodeID != -1 )
			namePanel.add( new JLabel( parentIcon ) );
		if( filterStack.size() > 0 )
			namePanel.add( new JLabel( filterStackIcon ) );

		namePanel.add( Box.createRigidArea( new Dimension( 3, 0 ) ) );

		Border empty = BorderFactory.createEmptyBorder(3,1,3,0);
		namePanel.setBorder(empty);

		JPanel paddedPanel = new JPanel();
		paddedPanel.setLayout(new BoxLayout( paddedPanel, BoxLayout.Y_AXIS));
		paddedPanel.add( namePanel );
		paddedPanel.setBackground( bgColor );

		return paddedPanel;
	}

	//--- Called when parent or filter stack possibly updated.
	public JPanel reGetEditFrame()
	{
		return getEditFrame( true );
	}

	//--- VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER
	//--- VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER
	//--- VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER, VIEW EDITOR LAYER
	//---- IOPs that can be edited in ViewEditor override this to create a ViewEditorLayer
	public ViewEditorLayer getEditorlayer()
	{
		return null;
	}


	//--- TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP
	//--- TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP
	//--- TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP, TIMELINE CLIP

	//--- Clip area is between clipStartFrame and clipEndFrame,
	//--- Iop is only renderer inside clip area.
	//--- if not FREE_LENGTH clip, clip area must be in area by defined beginFrame and beginFrame + maxLength.
	//--- if FREE_LENGTH clip, clip area is feely user settable
	//--- If iop has a program it starts from beginFrame.
	//--- Minimum length of clip is 1 frame.
	//--- Maximum length of clip is beginFrame + maxLength
	//--- When clipStartFrame == clipEndFrame, clip length = 1.
	//--- clipStartFrame is inclusive.
	//--- clipEndFrame is inclusive.

	//--- Translates movie frame to clip frame used by key frames
	public int getClipFrame( int frame ){ return frame - beginFrame; }
	//--- Translates clip frame to movie frame
	public int getMovieFrame( int frame ){ return beginFrame + frame; }
	//--- Inits values so clips begin at the start of timeline and with type appropriate length.
	public void initIOPTimelineValues()
	{
		if( clipType == NOT_FREE_LENGTH )
		{
			beginFrame = 0;
			clipStartFrame = 0;

			//--- Max length has been set when in constructor of
			//--- NOT_FREE_LENGTH type IOP using info provided by FileSource.

			//--- Set clip end frame, - 1 = we start from 0.
			int movielength = EditorRendererInterface.getMovieLength();
			if( maxLength > movielength ) clipEndFrame = movielength - 1;
			else clipEndFrame = maxLength - 1;
		}
		//--- clipType == FREE_LENGTH
		else
		{
			beginFrame = 0;
			clipStartFrame = 0;

			int movielength = EditorRendererInterface.getMovieLength();
			maxLength = movielength;
			clipEndFrame = maxLength - 1;// -1 = we start from 0.
		}
	}
	//---
	public void loadClipValues( int newMaxLength, int newBeginFrame, int newclipStartFrame, int newClipEndFrame )
	{
		maxLength = newMaxLength;
		beginFrame = newBeginFrame;
		clipStartFrame = newclipStartFrame;
		clipEndFrame = newClipEndFrame;
	}

	//---
	public int getBeginFrame(){ return beginFrame; }
	//--- Sets the beginFrame and MOVES clipStartFrame and clipEndFrame accordingly.
	public void setBeginFrame( int newBeginFrame )
	{
		int delta = newBeginFrame - beginFrame;
		beginFrame = newBeginFrame;

		//--- Move clip.
		clipStartFrame = clipStartFrame + delta;
		clipEndFrame = clipEndFrame + delta;

		updateStackFilterTimeParams();
	}
	//--- Calulates new beginFrame and MOVES clip
	public void setEndFrame( int newEndFrame )
	{
		int newBF = newEndFrame - maxLength + 1;
		setBeginFrame( newBF );
	}
	//---
	public int getEndFrame(){ return beginFrame + maxLength - 1; }
	//---
	public int getMaxLength(){ return maxLength; }
	//---
	public void setProgramLength( int len ){ setMaxLength( len ); }
	//---
	public void setMaxLength( int newLength )
	{
		if( newLength < 1 ) maxLength = 1;
		else maxLength = newLength;
	}
	//--- Clip length must be at least 1 frame.
	//--- Clip must stay in range defined by beginframe and maxLength.
	public void setClipStartFrame( int newStart )
	{
		if( clipType == NOT_FREE_LENGTH )
		{
			//--- Check bounds and correct if needed.
			if( newStart < beginFrame ) newStart = beginFrame;
			if( newStart > ( beginFrame + maxLength - 1 ) )
						newStart = beginFrame + maxLength - 1;
			if( newStart > clipEndFrame ) newStart = clipEndFrame;

			clipStartFrame = newStart;
		}
		//--- clipType == FREE_LENGTH
		else
		{
			//--- Start must be before end.
			if( newStart > ( beginFrame + maxLength - 1 ) )
				newStart = beginFrame + maxLength - 1;
			if( newStart > clipEndFrame )
				newStart = clipEndFrame;

			clipStartFrame = newStart;
		}

		updateStackFilterTimeParams();
	}
	//---
	public int getClipStartFrame(){ return clipStartFrame; }
	//--- Clip length must be at least 1 frame.
	//--- Clip must stay in range defined by beginframe and maxLength.
	public void setClipEndFrame( int newEnd )
	{
		if( clipType == NOT_FREE_LENGTH )
		{
			//--- Check bounds set by beginFrame, length, clipEndFrame and
			//--- correct if needed.
			if( newEnd < beginFrame ) newEnd = beginFrame;
			if( newEnd > ( beginFrame + maxLength - 1 ) )
						newEnd = beginFrame + maxLength - 1;
			if( newEnd < clipStartFrame ) newEnd = clipStartFrame;

			clipEndFrame = newEnd;
		}
		//--- clipType == FREE_LENGTH
		else
		{
			//--- End can not be before start.
			if( newEnd < beginFrame ) newEnd = beginFrame;
			if( newEnd < clipStartFrame ) newEnd = clipStartFrame;

			//--- Set clip end length to new value.
			clipEndFrame = newEnd;
			maxLength = newEnd - beginFrame + 1;
		}

		updateStackFilterTimeParams();
	}
	//---
	public int getClipEndFrame(){ return clipEndFrame; }
	//---
	public boolean frameInClipArea( int frame )
	{
		if( frame >=  clipStartFrame && frame <= clipEndFrame ) return true;
		else return false;
	}
	//---
	public boolean frameInProgramArea( int frame )
	{
		if( clipType == FREE_LENGTH ) return true;
		if( frame >= beginFrame && frame < beginFrame + maxLength ) return true;

		return false;
	}
	//--- True if clip has no set length program
	public boolean isFreeLength(){ return clipType == FREE_LENGTH; }
	//--- Returns looped frame correspomding to frame inside clip srea
	private int getLoopedFrame( int frame )
	{
		int delta = frame - clipStartFrame;
		int clipLength = clipEndFrame - clipStartFrame + 1;//+1 because end frame inclusive
		int mod = delta % clipLength;
		int loopedFrame = -1;
		if( delta > 0 )
			loopedFrame = clipStartFrame + mod;
		else if( delta < 0 )
			loopedFrame = clipStartFrame + ( clipLength + mod );//mod is always negative
		else
			loopedFrame = clipStartFrame;

		if( looping == LOOP )
			return loopedFrame;

		//--- PING PONG
		int clipNum = Math.abs( delta / clipLength );
		//---
		if( delta > 0 )
		{
			if( clipNum % 2 == 0 )
				return loopedFrame;
			else
				return clipStartFrame + ( clipLength - mod );
		}
		//--- delta < 0
		if( clipNum % 2 == 1 )
			return loopedFrame;
		return  clipStartFrame - mod;
	}
	//--- Sets time parameters of filter stack iops equal to time parameters of this object.
	public void updateStackFilterTimeParams()
	{
		for( ImageOperation filter : filterStack )
			filter.copyTimeParams( this );
	}
	//--- Copies time parameters from another ImageOperation
	//--- Does NOT copy max length because clips might be of different type.
	//--- This method will be not work predictably if source iop is FREE_LENGTH and
	//--- this object is not.
	public void copyTimeParams( ImageOperation iop )
	{
		setBeginFrame( iop.getBeginFrame() );
		setClipStartFrame( iop.getClipStartFrame() );
		setClipEndFrame( iop.getClipEndFrame() );
	}

	//-------------------------------------------------------------------- DEBUG

	public void printDebugInfo()
	{
		System.out.println( 	"iop:" + getClass().getName() +
					", blendMode:" + blendMode.get() +
					", on/off: " + isOn +
					", beginFrame:" + beginFrame +
					", maxLength:" + maxLength +
					", clipStartFrame:" + clipStartFrame +
					", clipEndFrame:" + clipEndFrame );
	}

	protected void debugSave( BufferedImage image, String path )
	{
		try
		{
			File f = new File( path );
			ImageIO.write( image, "png", f );
		}
		catch( IOException e )
		{
			System.out.println( "debugSave() write failed" );
		}
	}

	//----------------------------------------------------------- ALPHABETIC SORTING FOR DISPLAY
	public int compareTo( Object anotherObj )
	{
		ImageOperation compIOP = null;
		if( anotherObj instanceof ImageOperation )
			compIOP = (ImageOperation) anotherObj;
		else
			compIOP = ((PhantomPlugin) anotherObj).getIOP();

		return name.compareTo( compIOP.getName() );
	}

}//end class
