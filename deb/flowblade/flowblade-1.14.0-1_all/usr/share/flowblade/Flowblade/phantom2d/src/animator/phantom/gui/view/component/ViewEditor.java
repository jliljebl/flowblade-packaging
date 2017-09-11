package animator.phantom.gui.view.component;

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.MemoryManager;
import animator.phantom.controller.MovieRenderer;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.renderer.ImageOperation;

public class ViewEditor extends JPanel implements MouseListener, MouseMotionListener, ComponentListener, MouseWheelListener
{
	//--- Size of viewable area in real coordinates.
	private Dimension screenSize;
	//--- Minimum size of component.
	private Dimension scaledScreensize;
	//--- Value is MovieRenderer constant like MovieRenderer.DOUBLE_SIZE
	private int size;
	//--- Current size of panel.
	private Dimension componentSize;
	//--- Location of origo in panel coordinates.
	private Point2D.Float origo = null;
	//--- unscaled background image.
	private BufferedImage bgImg;
	//--- scaled background image.
	private BufferedImage scaledBgImg;
	//--- Layer currently being edited.
	private ViewEditorLayer editLayer;
	//--- All layers that can edited.
	private Vector<ViewEditorLayer> layers = new Vector<ViewEditorLayer>();
	//--- current scale of view
	private float scale;
	//--- Flag for mouse acation
	private boolean mouseActionNow = false;
	//--- Flag to differentiate left and right mouse press on request.
	private boolean lastMousePressWasLeftButton = true;
	//--- Edit mode
	private int editMode = ViewEditorLayer.MOVE_MODE;
	//--- Flag for drawing non active layers, user settable.
	public static boolean drawNonActiveLayers = false;
	//--- Flag to stop rapid selction change when loading
	private static boolean beingFilled = false;
	//--- Flag for full render
	private boolean fullRender = true;
	//--- small area around view with scroll bars view, sort of border pad
	public static final int BIG_VIEW_PAD = 10;
	//--- Flag preview display mode
	private boolean previewDisplay = false;
	//--- Flag for first frame of preview display
	private boolean firstPreviewFrame = false;
	//---
	private boolean displayWaitIcon = true;



	public static ViewEditor getInstance()
	{
		return GUIComponents.viewEditor;
	}

	//---------------------------------------------- CONSTRUCTOR
	public ViewEditor( Dimension screenSize )
	{
		System.out.print("INIATILIZING VIEW EDITOR..." );
		this.screenSize = screenSize;

		//--- Listeners
		addMouseListener( this );
		addMouseMotionListener( this );
		addComponentListener( this );
		addMouseWheelListener(this);

		//--- Key actions
		setFocusable( true );

		System.out.println("DONE" );
	}

	//--------------------------------------------- INTERFACE
	//--- This only updates positions of layer gui ( handles, bounding boxes etc.. )
	public void frameChanged()
	{
		for( ViewEditorLayer layer : layers )
			layer.frameChanged();

		repaint();
	}
	//--- Set bg image. Must be same size as screensize.
	//--- BIG side effect of chnaging view scale
	public void setBGImage( BufferedImage newBgImage )
	{
		bgImg = newBgImage;
		if( bgImg == null )
			scaledBgImg = null;
		else
			scaledBgImg = getScaledVersion( newBgImage );

		//--- Because img size may change, we use the already set scale to calculate draw params
		setScale( scale );
	}
	public void setBeingFilled( boolean b ){ beingFilled = b; }
	//--- Scale handling. Scale in normalized space. Scale 1.0: 1 panel pix = 1 movie screen pix.
	public void setScreenSize( int size_ )
	{
		size = size_;
		float newScale = getFloatSize( size_ );
		setScale( newScale );
	}
	public int getScreenSize()
	{
		return size;
	}
	private float getFloatSize( int size )
	{
		float newScale = 1.0f;

		if( size == MovieRenderer.DOUBLE_SIZE ) newScale = 2.0f;
		if( size == MovieRenderer.ONE_HALF_SIZE ) newScale = 1.5f;
		if( size == MovieRenderer.FULL_SIZE ) newScale = 1.0f;
		if( size == MovieRenderer.THREE_QUARTER_SIZE ) newScale = 0.75f;
		if( size == MovieRenderer.HALF_SIZE ) newScale = 0.5f;
		if( size == MovieRenderer.QUARTER_SIZE ) newScale = 0.25f;
		if( size == MovieRenderer.ONE_THREE_QUARTER_SIZE ) newScale = 1.75f;
		if( size == MovieRenderer.ONE_QUARTER_SIZE ) newScale = 1.25f;
		if( size == MovieRenderer.THIRD_SIZE ) newScale = 0.33f;
		return newScale;
	}
	//--- Quick change size it is then rendered to eliminate timing bugs (very wtf comment, but this is still probably needed)
	public void quickChangeSize( int size_ )
	{
		if( bgImg == null )
			return;
		size = size_;
		scaledBgImg = getScaledVersion( bgImg );
		setScreenSize( size );
		repaint();
	}
	//--- scale is internally set using float value
	private void setScale( float newScale )
	{
		scale = newScale;
		scaledScreensize = getScaledMovieScreenSize();

		//--- Get panel size.
		int panelWidth = getWidth();
		int panelHeight = getHeight();

		componentSize = new Dimension( panelWidth, panelHeight );// wtf global state
		setEditorSize( componentSize );

		//--- If scaled screen smaller then component size center it and set origo
		if( scaledScreensize.width < componentSize.width &&
			scaledScreensize.height < componentSize.height )
		{
			int origoX = ( componentSize.width - scaledScreensize.width ) / 2;
			int origoY = ( componentSize.height - scaledScreensize.height ) / 2;
			origo = new Point2D.Float( (float) origoX, (float) origoY );
		}
		//--- If scaled screen larger then component size set component size to it.
		else if( scaledScreensize.width >= componentSize.width ||
			scaledScreensize.height >= componentSize.height )
		{
			int newW = scaledScreensize.width > componentSize.width ? scaledScreensize.width + (BIG_VIEW_PAD * 2) : componentSize.width;
			int newH = scaledScreensize.height > componentSize.height ? scaledScreensize.height + (BIG_VIEW_PAD * 2)  : componentSize.height;
			componentSize = new Dimension( newW, newH );
			setEditorSize( componentSize );
			int newOX = scaledScreensize.width > componentSize.width ? BIG_VIEW_PAD : (( componentSize.width - scaledScreensize.width ) / 2);
			int newOY = scaledScreensize.height > componentSize.height ? BIG_VIEW_PAD : (( componentSize.height - scaledScreensize.height ) / 2);
			origo = new Point2D.Float( newOX, newOY );
		}
	}

	public Dimension getScalesCenterPosition(Dimension portSize)
	{
		int horizPos = 0;
		int vertPos = 0;

		//--- If scaled screen smaller then component keep default values

		//--- If scaled screen larger then component size set component size to it.
		if( componentSize.width >= portSize.width ||
				componentSize.height >= portSize.height )
		{
			horizPos = (componentSize.width - portSize.width) / 2;
			vertPos =  (componentSize.height - portSize.height) / 2;
		}

		return new Dimension( horizPos, vertPos );
	}

	//--- Returns current scle
	public float getScale(){ return scale; }
	//--- Number of layers
	public int getLayersSize(){ return layers.size(); }
	//--- Set edit mode and repaint. Any reactions for changed mode done in layers.
	public void setMode( int newMode )
	{
		editMode = newMode;
		for( ViewEditorLayer layer : layers )
			layer.modeChanged();
		repaint();
	}
	//--- If set true all layers are drawn
	public void drawAllLayers( boolean val )
	{
		drawNonActiveLayers = val;
		repaint();
	}
	//--- Returns edit mode.
	public int getMode(){ return editMode; }
	//--- Lengths in screen pixel coordinates multiplied with current scale.
	public float getScaledLength( float originalLength )
	{
		return scale * originalLength;
	}
	//--- Returns IOP of currently edited layer
	public ImageOperation getCurrentLayerIOP()
	{
		if( editLayer == null ) return null;
		return editLayer.getIOP();
	}
	//--- Movie screen not
	private Dimension getScaledMovieScreenSize()
	{
		int w;
		int h;
		//--- screensize for drawing is bg img size but if dont have we'll
		//--- take movie screen size
		if( bgImg == null )
		{
			w = screenSize.width;
			h = screenSize.height;
		}
		else
		{
			w = bgImg.getWidth();
			h = bgImg.getHeight();
		}
		float scaledScreenWidth = scale * w;
		float scaledScreenHeight = scale * h;

		return new Dimension( (int) Math.round( scaledScreenWidth ),
					(int) Math.round( scaledScreenHeight ));
	}

	//--- Sets component size.
	private void setEditorSize( Dimension newSize )
	{
		setSize( newSize );
		setPreferredSize( newSize );
		setMinimumSize( newSize );
		setMaximumSize( newSize );
	}


	public void setDisplayWaitIcon( boolean b ){ displayWaitIcon = b; }

	//--------------------------------------------------------------- SPACE CONVERSIONS
	//--- Converts panel( screen coordinate ) into real ( movie screen pix ) coordinate.
	public Point2D.Float getRealPoint( Point2D.Float panelPoint )
	{
		float panelXCoord = panelPoint.x - origo.x;
		float panelYCoord = panelPoint.y - origo.y;

		float conversionMultiplier = 1 / scale;

		float realXCoord = conversionMultiplier * panelXCoord;
		float realYCoord = conversionMultiplier * panelYCoord;

		return new Point2D.Float( realXCoord, realYCoord );
	}

	//--- Converts real(movie screen) point into float panel point ( screen coordinate ) .
	public  Point2D.Float getScaledPanelPoint( Point2D.Float realPoint )
	{
		float panelXCoord = realPoint.x * scale + origo.x;
		float panelYCoord = realPoint.y * scale + origo.y;

		return new Point2D.Float( panelXCoord, panelYCoord );
	}

	//--- Converts real space bounding box to panel space bounding box
	public Rectangle2D.Float getBoundingPanelCoordsRect( Rectangle2D.Float r )
	{
		Point2D.Float topleft = getScaledPanelPoint( new Point2D.Float( r.x, r.y ) );
		Point2D.Float bottomright = getScaledPanelPoint( new Point2D.Float( r.x + r.width, r.y + r.height ) );
		return new Rectangle2D.Float(  	topleft.x,
						topleft.y,
						bottomright.x - topleft.x,
						bottomright.y - topleft.y );
	}

	//-------------------------------------------------------- COMPNENT RESIZE HANDLING
	public void componentHidden(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentResized(ComponentEvent e)
	{
		setScale( scale );
		repaint();
	}

	//----------------------------------- COLOR DETECTION
	public Color getColor(  Point2D.Float realPoint )
	{
		float imgX = realPoint.x * scale;
		float imgY = realPoint.y * scale;
		if( scaledBgImg == null ) return null;
		int x = (int)imgX;
		int y = (int)imgY;
		if( x < 0 || x >= scaledBgImg.getWidth() ||
			y < 0 || y >= scaledBgImg.getHeight() )
		{
			System.out.println("color pick out of img range");
			return null;
		}
		int color = scaledBgImg.getRGB( x, y );
		return new Color(color);
	}

	//---------------------------------------------------------------- LAYER HANDLING
	//--- Adds new edit layer to editor. Places it according its hit area size.
	//--- Smaller are layers are on the top.
	//--- NOTE: Layer order does not affect display, only hit detection.
	//--- NOTE: hit detection. functionality disabled
	public void addEditlayer( ViewEditorLayer newEditLayer )
	{
		layers.add( newEditLayer );

		if( !beingFilled ) setEditLayer( newEditLayer );
	}
	//--- Removes layer for iop
	public void removeLayer( ImageOperation iop )
	{
		int removeIndex = -1;
		for( int i = 0; i < layers.size(); i++ )
		{
			ViewEditorLayer layer = layers.elementAt( i );
			if( layer.getIOP() == iop ) removeIndex = i;
		}
		if( removeIndex != -1 )
		{
			if( editLayer == layers.elementAt( removeIndex ) ) editLayer = null;
			layers.remove( removeIndex );
		}
		updateLayerSelector();
	}
	//--- Replace a layer with another and sets it edit layer if removed was
	public void replaceEditlayer( ViewEditorLayer newEditLayer )
	{
		int removeIndex = -1;
		boolean wasActive = false;
		for( int i = 0; i < layers.size(); i++ )
		{
			ViewEditorLayer layer = layers.elementAt( i );
			if( layer.getIOP() == newEditLayer.getIOP() )
			{
				removeIndex = i;
				if( layer == editLayer )
					wasActive = true;
			}
		}
		if( removeIndex != -1 )
		{
			if( editLayer == layers.elementAt( removeIndex ) ) editLayer = null;
			layers.remove( removeIndex );

		}
		layers.add( newEditLayer );
		if( wasActive ) setEditLayer( newEditLayer );
	}

	public void setEditLayer( ViewEditorLayer newEditLayer )
	{
		 setEditLayer( newEditLayer, true );
	}
	//--- Sets the layer being edited, changes buttons, caches filesource and updates layer selector
	private void setEditLayer( ViewEditorLayer newEditLayer, boolean updateOthers )
	{
		if( editLayer != null )
			editLayer.setActiveState( false );
		editLayer = newEditLayer;
		if( editLayer != null )
		{
			editLayer.setLayerButtons( GUIComponents.viewControlButtons );
			editLayer.setActiveState( true );
			GUIComponents.viewControlButtons.clickSelected();//to set mode to match selected button.
			MemoryManager.cacheViewEditFileSource( editLayer.getFileSource() );// possible fileSources for set layer is cached for faster editing.
			if( updateOthers ) UpdateController.editTargetIOPChangedFromViewEditor( editLayer.getIOP() );
		}
		updateLayerSelector();
	}
	//---
	public void rotateNextLayer( boolean up )
	{
		if( layers.size() == 0 ) return;

		int index = layers.indexOf( editLayer );
		if( index == -1 ) index = 0;
		else if( up ) index++;
		else index--;

		if( index == layers.size() ) index = 0;
		if( index < 0 ) index = layers.size() - 1;

		setEditLayer( layers.elementAt( index ) );
		repaint();
	}
	public void setEditorLayerForIop( ImageOperation iop )
	{
		for( ViewEditorLayer layer : layers )
			if( layer.getIOP() == iop )
			{
				setEditLayer( layer, false );
				repaint();
				return;
			}
		setEditLayer( null );
		//--- No layer, no buttons
		GUIComponents.viewControlButtons.setModeButtons( new Vector<Integer>() );
		repaint();
	}
	//---
	public void setLayerControlsEnabled( ImageOperation iop, boolean val )
	{
		for( ViewEditorLayer layer : layers )
			if( layer.getIOP() == iop )
				layer.setControlsActive( val );
	}

	public void setLayerDrawingEnabled( ImageOperation iop, boolean val )
	{
		for( ViewEditorLayer layer : layers )
			if( layer.getIOP() == iop )
				layer.setDrawingEnabled( val );
	}

	public Vector<ViewEditorLayer> getLayers(){ return layers; }

	//--- Returns scaled version of image
	private BufferedImage getScaledVersion( BufferedImage img )
	{
		if( size == MovieRenderer.FULL_SIZE ) return img;
		else return MovieRenderer.getImageInSize( img, size );
	}

	public void updateLayerSelector()
	{
		GUIComponents.viewControlButtons.updateLayerSelector( layers, editLayer );
	}

	//------------------------------------------------ MOUSE EVENTS HANDLING
	//--- Translates mouse event point to real space and passes it to layer
	public void mousePressed(MouseEvent e)
	{
		if( e.getButton() == MouseEvent.BUTTON3 )
			lastMousePressWasLeftButton = false;
		else
			lastMousePressWasLeftButton = true;

		if( SwingUtilities.isMiddleMouseButton( e ) )
		{
			GUIComponents.animatorFrame.centerViewEditor();
			return;
		}

		if( editLayer != null
			&& !editLayer.getIOP().
				frameInClipArea( TimeLineController.getCurrentFrame() ) )
			return;

		mouseActionNow = true;
		Point2D.Float realPoint =
			getRealPoint( new Point2D.Float( (float)e.getX(), (float)e.getY() ));

		//--- Check editLayer hit first if exists.
		if( editLayer != null )
		{
			if( editLayer.hit( realPoint ) )
			{
				editLayer.handleMousePress( realPoint );
				return;
			}
		}
	}

	//--- Translates mouse event point to real space and passes it to layer
	public void mouseDragged(MouseEvent e)
	{
		if( editLayer != null
			&& !editLayer.getIOP().
				frameInClipArea( TimeLineController.getCurrentFrame() ) )
			return;

		Point2D.Float realPoint =
			getRealPoint( new Point2D.Float( (float)e.getX(), (float)e.getY() ));

		if( editLayer != null )
			editLayer.handleMouseDrag( realPoint );

		fullRender = false;//for some extra speed when dragging
		repaint();
	}

	//--- Translates mouse event point to real space and passes it to layer
	public void mouseReleased(MouseEvent e)
	{
		if( editLayer != null
			&& !editLayer.getIOP().
				frameInClipArea( TimeLineController.getCurrentFrame() ) )
			return;

		mouseActionNow = false;

		Point2D.Float realPoint =
			getRealPoint( new Point2D.Float( (float)e.getX(), (float)e.getY() ));

		if( editLayer != null )
			editLayer.handleMouseRelease( realPoint );

		fullRender = false;//for some extra speed when dragging
		repaint();
	}

	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( mouseActionNow ) return;
		int notches = e.getWheelRotation();
		if( notches < 0 )
		{
			GUIComponents.viewControlButtons.zoomIn();
		}
		else
		{
			GUIComponents.viewControlButtons.zoomOut();
		}
	}

	//--- Noop mouse events.
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

	//--- Returns true if mouse action  is happening.
	public boolean mouseActionUnderway(){ return mouseActionNow; }

	public boolean lastPressWasLeftMouse(){ return lastMousePressWasLeftButton; }

	//----------------------------------------------- PREVIEW
	public void setPreviewDisplay()
	{
		this.previewDisplay = true;
		this.firstPreviewFrame = true;
	}

	public void setViewEditorDisplay()
	{
		this.previewDisplay = false;
		this.firstPreviewFrame = false;
		repaint();
	}

	public void setPreviewFrame( BufferedImage frame )
	{
		scaledBgImg = frame;
	}

	//------------------------------------------------ PAINT
	public void paintComponent( Graphics g )
	{

		if (displayWaitIcon)
		{
		    drawWaitIcon( (Graphics2D) g );
			return;
		}

		if( previewDisplay )
		{
			drawPreviewFrame( (Graphics2D) g );
			return;
		}

		//--- Convert graphics object.
		Graphics2D g2 = (Graphics2D) g;

		//--- Paint BG
		Rectangle drawRect = null;
		Rectangle2D.Float eraseRectF = null;
		Rectangle eraseRect = null;
		if( editLayer != null )
		{
			eraseRectF = editLayer.getLastDrawRect();

			if( eraseRectF != null )
			{
				//--- Convert real space rect to panel space rect
				eraseRectF = getBoundingPanelCoordsRect( eraseRectF );

				//--- Convert to int
				//--- Add some for handles.
				eraseRect = new Rectangle( 	(int) eraseRectF.x - 10,
								(int) eraseRectF.y - 10,
								(int) eraseRectF.width + 20,
								(int) eraseRectF.height + 20 );
			}
		}

		//--- Erase, set gray paint
		g2.setColor( GUIColors.viewEditorBGColor );

		//--- top
		drawRect = getDrawRect( new Rectangle(  0,
							0,
							componentSize.width,
							(int) origo.y ),
					eraseRect );
		if( !drawRect.isEmpty() )
			g2.fillRect( drawRect.x, drawRect.y, drawRect.width, drawRect.height );

		//--- bottom
		drawRect = getDrawRect( new Rectangle( 	0,
							(int) origo.y + scaledScreensize.height,
							componentSize.width,
							componentSize.height ),
					eraseRect );
		if( !drawRect.isEmpty() )
			g2.fillRect( drawRect.x, drawRect.y, drawRect.width, drawRect.height );

		//--- left
		drawRect = getDrawRect( new Rectangle( 	0,
							(int) origo.y,
							(int) origo.x,
							scaledScreensize.height  ),
					eraseRect );
		if( !drawRect.isEmpty() )
			g2.fillRect( drawRect.x, drawRect.y, drawRect.width, drawRect.height );

		//--- right
		drawRect = getDrawRect( new Rectangle(  (int) origo.x + scaledScreensize.width,
							(int) origo.y,
							componentSize.width - (int) origo.x - scaledScreensize.width,
							scaledScreensize.height ),
					eraseRect );
		if( !drawRect.isEmpty() )
			g2.fillRect( drawRect.x, drawRect.y, drawRect.width, drawRect.height );

		//--- bg has to be always painted because sometimes bg img is partially transparent
		//--- e.g. when masks are used
		g2.setColor( Color.lightGray );
		g2.fillRect( 	(int)Math.round(origo.x),
				(int)Math.round(origo.y),
				scaledScreensize.width,
				scaledScreensize.height );

		//--- If bg image exists paint it
		if( scaledBgImg != null )
		{
			g2.drawImage( scaledBgImg,
					(int)Math.round(origo.x),
					(int)Math.round(origo.y),
					null );
		}

		//--- Draw all layers if drawNonActiveLayers set
		if( drawNonActiveLayers )
			for( ViewEditorLayer layer :  layers )
				layer.paint( g2 );


		//--- Paint current layer on top if exists.
		if( editLayer != null )
			editLayer.paint( g2 );

		//--- Paint decorative frame around screen. Visible only if something displayable.
		g2.setColor( Color.black );
		g2.drawRect( (int)Math.round(origo.x),
					(int)Math.round(origo.y), scaledScreensize.width, scaledScreensize.height );

		g2.dispose();

		fullRender = true;
	}

	private void drawPreviewFrame( Graphics2D g )
	{
		if( firstPreviewFrame )
		{
			g.setColor( GUIColors.viewEditorBGColor );
			g.fillRect( 0, 0, componentSize.width, componentSize.height );
			firstPreviewFrame = false;
		}
		g.drawImage( scaledBgImg, (int) Math.round(origo.x), (int) Math.round(origo.y), null );
	}

	private Rectangle getDrawRect( Rectangle updateRect,  Rectangle eraseRect )
	{
		if( fullRender ) return updateRect;
		if( drawNonActiveLayers ) return updateRect;
		if( eraseRect == null ) return updateRect;

		return updateRect.intersection( eraseRect );
	}

        private void drawWaitIcon( Graphics2D g )
        {
				if (g == null) return;
				if (componentSize == null) return;
                g.setColor( Color.darkGray );
                g.fillRect( 0, 0, componentSize.width, componentSize.height );
        }

}//end class
