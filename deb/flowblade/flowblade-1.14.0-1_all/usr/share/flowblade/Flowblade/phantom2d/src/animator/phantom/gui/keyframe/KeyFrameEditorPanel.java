package animator.phantom.gui.keyframe;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import animator.phantom.bezier.BezierSegment;
import animator.phantom.controller.EditorsController;
import animator.phantom.controller.KeyStatus;
import animator.phantom.controller.LayerCompositorMenuActions;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIUtils;
import animator.phantom.renderer.DummyImageOperation;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.IntegerAnimatedValue;
import animator.phantom.renderer.param.KeyFrameParam;

//--- GUI component for editing keyframes values and places. Value splines are displayed between keyframes.
public class KeyFrameEditorPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
{
	//--- JEEBUS...Quite enough (class) global state...
	//--- ... try to fix this.

	//--- ImageOperation being edited.
	private ImageOperation iop;
	//--- Editmode
	private KFEditMode editMode = null;
	//--- Draw params after last scale or pos change
	private float pixPerFrame;
	private int timeLinePos;
	private int xOFF;
	//--- Animated Value being edited.
	private KeyFrameParam editValue;
	private KeyFrameParam dummyValue = (KeyFrameParam) new AnimatedValue( new DummyImageOperation() );//dummy value to kill null pointers.
	//--- range -50 -> 50
	private int verticalPos = 0;
	private float vOFF = 0.0f;
	//--- Panel display arae between these frames.
	private int startDrawFrame;
	private int endDrawFrame;
	//--- Draw scale for value -> y mapping
	private float pixForValueOne;
	//--- Current value draw step
	private float valueDrawStep;
	private MouseEvent editEvent = null;


	//---------------------------------------------------- GUI PARAMS
	//--- KF handle draw
	private final int KF_OFF = 3;
	private final int KF_SIDE = 6;
	//--- Possible value distances between scale lines.
	private static final float[] VALUE_DRAW_STEPS = { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f, 0.25f,
						0.5f, 1.0f, 2.5f, 5.0f, 10.0f, 25.0f,
						50.0f, 100.0f, 250.0f, 500.0f, 1000.0f };
	//--- pixForValueOne is multiplied by this (or inverse) when zooming
	private static final float VALUE_SCALE_STEP = 0.66f;
	//--- Auto zoom sets pixForValueOne this much smaller then would produce
	//--- maxVal * pixForValueOne to be full panel height.
	//--- Provides padding for auto zoom
	private static final float AUTO_MAX_MULTIPLIER = 1.5f;
	//--- Number of pixels displayed boynd max value when scrolling
	private float VERT_MAX_PAD = 50;
	//--- Param to get enough lines drawn for smooth bez line but no more.
	private final int PIX_PER_BEZ_LINE = 4;


	//--- keyframe popupmenu
	private JPopupMenu keyframeMenu;
	private JMenuItem setInterpolation;
	private JMenuItem freezeAll;
	private JMenuItem selectFollowing;
	private JMenuItem selectPrevious;

	public KeyFrameEditorPanel()
	{		
		//--- To kill null pointers
		editValue = dummyValue;
		editValue.setValue( 0, 9.0f );

		//--- Listeners
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );

		autoZoom();
		setValueDrawScaleStep();
		scaleOrPositionChanged();
	}

	public void initEditor( KeyFrameParam editValue, ImageOperation iop )
	{
		this.editValue = editValue;
		if( this.editValue == null ) this.editValue = dummyValue;//no iop to be edited
		this.iop = iop;
		EditorsController.setCurrentKFParam( editValue );

		autoZoom();
		setValueDrawScaleStep();
		scaleOrPositionChanged();
	}

	public ImageOperation getIOP(){ return iop; }
	public KeyFrameParam getEditValue(){ return editValue; }
	public float getPixPerFrame(){ return pixPerFrame; }

	//---------------------------------------- 
	//--- Updates view when timeline scale or pos changed.
	public void scaleOrPositionChanged()
	{
		setTimeLineParams();
		repaint();
	}

	//--- Set timeline params needed to draw and handle mouse events to current situation.
	private void setTimeLineParams()
	{
		//--- scale and position
		timeLinePos = TimeLineController.getTimeLinePosition();
		pixPerFrame = TimeLineController.getCurrentScaleMultiplier();

		//--- Set draw range.
		startDrawFrame = timeLinePos;
		int columnWidth = AnimFrameGUIParams.getTimeEditRightColWidth();
		int framesInPanel = Math.round( columnWidth / pixPerFrame );
		endDrawFrame = startDrawFrame + framesInPanel;

		//--- Pix X offset from unmoved panel 0 to viewpanel 0
		xOFF = Math.round( pixPerFrame * timeLinePos );
	}
	//-------------------------------------------------- VALUE <-->
	//--- Makes values bigger in height.
	public void zoomIn()
	{
		pixForValueOne = pixForValueOne * ( 1 / VALUE_SCALE_STEP );
		setValueDrawScaleStep();
		calculateVOff();
		repaint();
	}
	//--- Makes values smaller in height.
	public void zoomOut()
	{
		pixForValueOne = pixForValueOne * VALUE_SCALE_STEP;
		setValueDrawScaleStep();
		calculateVOff();
		repaint();
	}
	//--- Sets pixForValueOne using max value for AnimatedValue
	public void autoZoom()
	{
		//--- get min, max and absmax values of keyframes.
		float max = editValue.getMaxKeyValue();
		float min = editValue.getMinKeyValue();
		float absMax;
		if( Math.abs( max ) > Math.abs( min ) ) absMax = Math.abs( max );
		else absMax = Math.abs( min );

		//--- Get panel height
		float panelHeight = getPanelHeight();

		//--- Handle case in which both are > 0;
		if( max > 0 && min > 0 )
		{
			float absRange = absMax * 2 * AUTO_MAX_MULTIPLIER;
			pixForValueOne = panelHeight / absRange;
		}
		//--- Handle case in whicth both are < 0
		else if(  max < 0 && min < 0 )
		{
			float absRange = absMax * 2 * AUTO_MAX_MULTIPLIER;
			pixForValueOne = panelHeight / absRange;
		}
		//--- Handle case in whicth both are == 0
		else if( max == 0 && min == 0 )
		{
			float absRange = 1;
			pixForValueOne = panelHeight / absRange;
		}
		//--- Handle in which min < 0 and max > 0
		else
		{
			float absRange = absMax * 2 * AUTO_MAX_MULTIPLIER;
			pixForValueOne = panelHeight / absRange;
		}

		setValueDrawScaleStep();
		calculateVOff();
		repaint();
	}
	//--- Sets value distance betwen scale lines for value display based on abs max value.
	private void setValueDrawScaleStep()
	{
		//--- float value of half panel height is used to pick scale line distance. 
		float valueOfHalf = ( getPanelHeight() / 2 ) / pixForValueOne;

		int scaleIndex = -1;
		for( int i = 0; i < VALUE_DRAW_STEPS.length - 1; i++ )
		{
			if( valueOfHalf >= VALUE_DRAW_STEPS[ i ] &&
				valueOfHalf < VALUE_DRAW_STEPS[ i + 1 ] ) scaleIndex = i;
		}
		if( scaleIndex == -1 ) scaleIndex = VALUE_DRAW_STEPS.length - 1;

		valueDrawStep = VALUE_DRAW_STEPS[ scaleIndex ];
	}
	//--- Returns pixel y-coord for given value.
	private float getPixYForValue( float val )
	{
		//--- Get zero position
		float zeroPos = getZeroPos();
		//--- return pos, - because more up, less down.
		return zeroPos - (val * pixForValueOne);
	}
	//--- Returns param value for screen position
	public float getValueForY( int y )
	{
		float ydelta = getZeroPos() - (float) y;
		return ydelta / pixForValueOne;
	}

	//--- returns vertical offset based on slider position, pixper valueOne and maxdelta
	private void calculateVOff()
	{
		//--- get min, max and absmax values of keyframes.
		float max = editValue.getMaxKeyValue();
		float min = editValue.getMinKeyValue();
		float absMax;
		if( Math.abs( max ) > Math.abs( min ) ) absMax = Math.abs( max );
		else absMax = Math.abs( min );

		float valueOfHalf = ( getPanelHeight() / 2 ) / pixForValueOne;
		//-- if in full screen then donot set pos
		if( pixForValueOne * absMax < valueOfHalf )
		{
			vOFF = 0;
			verticalPos = 0;
			return;
		}
		float fullRange = absMax * pixForValueOne;
		float maxOff = fullRange - valueOfHalf - VERT_MAX_PAD;

		vOFF = (maxOff * verticalPos ) / 50.0f; // 50: range -50 -> 50
	}

	//--- returns position of zero-line in pix y-coords.
	private float getZeroPos()
	{
		return (getPanelHeight() / 2) + vOFF;
	}
	//--- Returns panel draw area height in float.
	private float getPanelHeight()
	{
		float h = (float)getSize().height;
		if( h == 0 ) h = 150.0f;
		return h;
	}
	private int getFrameX( int f )
	{
		return (int) (f * pixPerFrame) - xOFF;
	}
	//------------------------------------------------- MOUSE EVENTS
	public void mousePressed(MouseEvent e)
	{
		editMode = null;
		
		requestFocusInWindow();
		PreviewController.stopPlaybackRequest();

		//--- Test if key frames hit.
		Vector <AnimationKeyFrame> keyFrames = editValue.getKeyFrames();
		AnimationKeyFrame kfHIT = null;
		for( int i = 0; i< keyFrames.size(); i++ )
		{
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			if( kfHit( kf, e.getX(), e.getY() ) )
			{
				kfHIT = kf;
			}
		}

		//--- Move mode
		if( kfHIT != null && e.getButton() == MouseEvent.BUTTON1 && !KeyStatus.ctrlIsPressed()  )
		{
			editMode = new MoveKFMode();
			EditorsController.setCurrentKeyFrame( kfHIT );
			editMode.mousePressed( e, this, kfHIT, iop.getBeginFrame(), iop );
		}
		else if( kfHIT != null && e.getButton() == MouseEvent.BUTTON1 && KeyStatus.ctrlIsPressed()  )
		{
			
			EditorsController.addSelectedKeyFrame( kfHIT );
			editMode = new MultiMoveKFMode();
			editMode.mousePressed( e, this, kfHIT, iop.getBeginFrame(), iop );
		}
		//--- select frame
		else if( kfHIT != null && e.getButton() == MouseEvent.BUTTON3 )
		{
			EditorsController.setCurrentKeyFrame( kfHIT );
			showPopup( e );
		}
		//--- deselect frame
		else
		{
			EditorsController.setCurrentKeyFrame( null );
		}
		editEvent = e;
		repaint();
	}
	
	public void mouseDragged(MouseEvent e)
	{
		forceRange( e );
		if( editMode != null ) editMode.mouseDragged(e);
		editEvent = e;
		repaint();
	}

	public void mouseReleased(MouseEvent e)
	{
		forceRange( e );
		if( editMode != null ) editMode.mouseReleased(e);
		if( editMode != null ) EditorsController.getCurrentKFEditorParam().registerUndo();
		editMode = null;
		editEvent = null;
		repaint();
	}

	public void mouseWheelMoved( MouseWheelEvent e )
	{
		int notches = e.getWheelRotation();
		
		// no ctrl, zoom
		if((e.getModifiers() & InputEvent.CTRL_MASK) != InputEvent.CTRL_MASK) 
		{
			if( notches < 0 )
			{
				TimeLineController.zoomIn();
				TimeLineController.scaleOrPosChanged();
			}
			else
			{
				TimeLineController.zoomOut();
				TimeLineController.scaleOrPosChanged();
			}
		}
		else //ctrl down, vert scroll 
		{
			if( notches < 0 )
			{
				zoomOut();

			}
			else
			{
				zoomIn();
			}
			calculateVOff();
			repaint();
		}
	}

	private void forceRange( MouseEvent e )
	{
		int dx = 0;
		int dy = 0;
		if( e.getX() < 0 ) dx = - e.getX();
		if( e.getY() < 0 ) dy = - e.getY();
		if( e.getX() > getWidth() ) dx = getWidth() - e.getX();
		if( e.getY() > getHeight() ) dy = getHeight() - e.getY();
		e.translatePoint( dx, dy );
	}

	//--- Mouse events that are not handled.
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	private boolean kfHit( AnimationKeyFrame kf, int x, int y )
	{
		if( iop == null ) return false;
		int kfX = kfX( kf );
		int kfY = kfY( kf );
		if( 	x >= kfX - KF_OFF &&
			x <= kfX + KF_OFF &&
			y >= kfY - KF_OFF &&
			y <= kfY + KF_OFF ) return true;
		else return false;
	}

	private int kfX( AnimationKeyFrame kf )
	{
		int kfMovieFrame = iop.getBeginFrame() + kf.getFrame();
		return Math.round( ( kfMovieFrame - timeLinePos ) * pixPerFrame );
	}

	private int kfY( AnimationKeyFrame kf )
	{
		return Math.round( getPixYForValue(  kf.getValue() ) );
	}
	private int getPixForClipFrame( float clipFrame )
	{
		float kfMovieFrame = (float)iop.getBeginFrame() + clipFrame;
		return Math.round( ( kfMovieFrame - (float) timeLinePos ) * pixPerFrame );
	}
	//--------------------------------------------------- PAINT
	public void paintComponent( Graphics g )
	{
		//--- Set params needed to draw to current values.
		setTimeLineParams();
		//--- Upcast graphics
		Graphics2D g2 = (Graphics2D) g;
		//--- Draw bg
		Dimension panelSize = getSize();
		if( iop != null )
		{
			int clipStart = iop.getClipStartFrame();
			int clipEnd = iop.getClipEndFrame() + 1;
			
			//--- case: all panel in clip
			if( clipStart <= startDrawFrame && 
				clipEnd >= endDrawFrame )
			{
				g2.setColor( GUIColors.darkBgColor );
				g2.fillRect(0,0,panelSize.width,panelSize.height);
			}
			//--- case: panel left not in clip
			else if(  clipStart > startDrawFrame && 
				clipEnd >= endDrawFrame )
			{
				int cX = getFrameX( clipStart );
				g2.setColor( GUIColors.KF_NON_CLIP_COLOR );
				g2.fillRect( 0, 0, cX , panelSize.height );
				g2.setColor( GUIColors.darkBgColor );
				g2.fillRect( cX, 0, panelSize.width - cX, panelSize.height );
			}
			//---  case: panel right not in clip
			else if(  clipStart <= startDrawFrame && 
				clipEnd < endDrawFrame )
			{
				int cX = getFrameX( clipEnd );
				g2.setColor( GUIColors.darkBgColor );
				g2.fillRect( 0, 0, cX , panelSize.height );
				g2.setColor( GUIColors.KF_NON_CLIP_COLOR );
				g2.fillRect( cX, 0, panelSize.width - cX, panelSize.height );
			}
			//--- case: panel left and right not in clip
			else
			{
				int cX = getFrameX( clipStart );
				int ceX = getFrameX( clipEnd );
				g2.setColor( GUIColors.KF_NON_CLIP_COLOR );
				g2.fillRect( 0, 0, cX , panelSize.height );
				g2.setColor( GUIColors.darkBgColor );
				g2.fillRect( cX, 0, ceX - cX, panelSize.height );
				g2.setColor( GUIColors.KF_NON_CLIP_COLOR );
				g2.fillRect( ceX, 0, panelSize.width - ceX, panelSize.height );
			}
		}
		else
		{
			g2.setColor( GUIColors.darkBgColor );
			g2.fillRect(0,0,panelSize.width,panelSize.height);	
		}
		//--- Draw frame lines.
		g2.setColor( GUIColors.KF_LINES_COLOR );
		GUIUtils.drawFrameLines( g2, getHeight() );
		//--- Draw value scale
		drawValueScaleLines( g2 );
		//--- noa value drawing for dummy
		if( editValue == dummyValue ) return;
		//--- Draw value graph
		drawSegments( g2 );
		//--- Draws key frames
		drawKeyFrames( g2 );
		//--- Draw kf value
		drawKFValue( g2 );
	}

	//--- Draws value scale lines.
	private void drawValueScaleLines( Graphics2D g )
	{
		//--- Draw 0 line.
		drawValueLine( g, 0 );

		//--- Leave if lines cannot be correctly drawn.
		if( editValue == null ) return;
		if( valueDrawStep == 0 ) return;
		if( pixForValueOne == 0 ) return;

		//--- Draw scale lines.
		boolean more = true;
		float drawVal = 0;
		int iter = 0;

		while( more )
		{
			drawVal = drawVal + valueDrawStep;

			if( getPixYForValue( drawVal ) < 0 &&
				getPixYForValue( -drawVal ) >  getPanelHeight() )
			{
				more = false;
			}
			else
			{
				drawValueLine( g, drawVal );
				drawValueLine( g, -drawVal );
			}
			iter++;
			if( iter > 200 ) //--- We must have enough by now
			{
				System.out.println("MAXED OUT in drawValueScaleLines" );
				return;
			}
		}
	}

	//--- Draws horizontal scale line for given value.
	private void drawValueLine( Graphics2D g, float val )
	{
		//--- Get y pix pos 
		float drawY = getPixYForValue( val );
		//--- Draw line.
		Line2D.Float line = new Line2D.Float();
		line.setLine( 0, drawY, getSize().width, drawY );
		g.setColor( GUIColors.KF_LINES_COLOR );
		g.draw( line );
		//--- Draw label
		String valStr = (new Float( val )).toString();
		g.setColor(  GUIColors.KF_NUMBERS_COLOR );
		g.drawString( valStr, 0.0f, drawY);
	}

	//--- Draws key frames
	private void drawKeyFrames( Graphics2D g )
	{
		if( iop == null ) return;
		Vector <AnimationKeyFrame> keyFrames = editValue.getKeyFrames();
		int[] focusFrames = EditorsController.getFocusKeyFrames( iop );
		for( AnimationKeyFrame kf: keyFrames )
		{
			int kfMovieFrame = iop.getBeginFrame() + kf.getFrame();
			if( iop.frameInClipArea( kfMovieFrame ) )
			{
				int kfX = Math.round( ( kfMovieFrame - timeLinePos ) * pixPerFrame );
				float yval =  kf.getValue();
				if( editValue instanceof IntegerAnimatedValue )
				{
					yval = (float) Math.round( (double) yval );
				}
				int kfY = Math.round( getPixYForValue( yval ) );
				
				g.setColor( GUIColors.KF_COLOR );				
				for ( int focusFrame : focusFrames)
					if( focusFrame == kfMovieFrame ) g.setColor( GUIColors.KF_FOCUS_COLOR );

				g.fillRect( kfX - KF_OFF , kfY - KF_OFF, KF_SIDE, KF_SIDE );
			}
		}
	}

	private void drawSegments( Graphics2D g )
	{
		if( iop == null ) return;
		if( editValue instanceof IntegerAnimatedValue )
		{
			drawIntSegments( g );
			return;
		}

		Vector <AnimationKeyFrame> keyFrames = editValue.getKeyFrames();
		for( int i = 0; i< keyFrames.size(); i++ )
		{
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			int kfX = kfX( kf );
			int kfY = kfY( kf );
			int endX;
			int endY;
			//--- Last keyframe
			if( i == keyFrames.size() - 1 )
			{
				endX = getSize().width;
				endY = kfY;
				g.setColor( GUIColors.KF_VALUE_COLOR );
				g.drawLine( kfX, kfY, endX, endY );
			}
			//--- others
			else
			{	
				AnimationKeyFrame kf2 =  keyFrames.elementAt( i + 1 );
				g.setColor( GUIColors.KF_VALUE_COLOR );

				if ( editValue.getStepped() == true )
				{
					endX = kfX( kf2 );
					g.drawLine( kfX, kfY, endX, kfY );
				}				
				else if( isLinearSeq( kf, kf2 ) )
				{
					endX = kfX( kf2 );
					endY = kfY( kf2 );
					g.drawLine( kfX, kfY, endX, endY );
				}
				else
				{
					endX = kfX( kf2 );
					int lines = ( ( endX - kfX ) / PIX_PER_BEZ_LINE );
					if( lines < 1 ) lines = 1;
					BezierSegment curve = new BezierSegment( kf, kf2 );
					Point2D.Float[] drawPoints = curve.bezPoints( lines + 1 );
					g.setColor( GUIColors.KF_BEZ_VALUE_COLOR );
					for( int j = 0; j < lines; j++ )
						g.drawLine( 
								getPixForClipFrame( drawPoints[ j ].x ),
								Math.round( getPixYForValue( drawPoints[ j ].y ) ),
								getPixForClipFrame( drawPoints[ j + 1 ].x ),
								Math.round( getPixYForValue( drawPoints[ j + 1 ].y ) )
							   );
				}
			
			}
		}
	}
	
	//--- Called when drawing AnimatedIntegerParam values
	private void drawIntSegments( Graphics2D g )
	{
		Vector <AnimationKeyFrame> keyFrames = editValue.getKeyFrames();
		for( int i = 0; i< keyFrames.size(); i++ )
		{
			AnimationKeyFrame kf = keyFrames.elementAt( i );
			int kfX = kfX( kf );
			int startX;
			int endX;
			int endY;
			//--- Last keyframe
			if( i == keyFrames.size() - 1 )
			{
				endX = getSize().width;
				int yVal = (int) Math.round( (double) editValue.getValue( kf.getFrame() ) );
				endY = (int) Math.round( getPixYForValue( (float)yVal ) );
				g.setColor( GUIColors.KF_VALUE_COLOR );
				g.drawLine( kfX, endY, endX, endY );
			}
			//--- others
			else
			{	
				AnimationKeyFrame kf2 =  keyFrames.elementAt( i + 1 );
				g.setColor( GUIColors.KF_VALUE_COLOR );
				//--- draw line per frame.
				for( int j = kf.getFrame(); j < kf2.getFrame(); j++ )
				{
					startX = getFrameX( j );
					endX = getFrameX( j + 1 );
					int yVal = (int) Math.round( (double) editValue.getValue( j ) );
					endY = (int) Math.round( getPixYForValue( (float)yVal ) );
					g.drawLine( startX, endY, endX, endY );
				}
			}
		}
	}

	//--- Draws value for selected key frame.
	public void drawKFValue( Graphics2D g )
	{
		if( editEvent == null || EditorsController.getCurrentKeyFrame() == null || editMode == null ) return;
		float val = EditorsController.getCurrentKeyFrame().getValue();
		if( editValue instanceof IntegerAnimatedValue )
		{
			val = (float) Math.round( (double) val );
		}
		String vStr = Float.toString( val );
		String dstr = null;
		int dot = vStr.indexOf(".");
		//--- Cut too many decimals.
		if( dot != -1 && ( vStr.length() > dot + 3 ) ) dstr = vStr.substring( 0, dot + 4 );
		else dstr = vStr;
		g.setColor( Color.white );
		g.drawString( dstr, editEvent.getX() + 4, editEvent.getY() - 4 ) ;
	}

	//--- Returns true if segment between two keyframes is linear.
	private boolean isLinearSeq( AnimationKeyFrame kf, AnimationKeyFrame kf2 )
	{
		if( kf.getTrailingInterpolation() == AnimationKeyFrame.LINEAR &&
			kf2.getLeadingInterpolation() == AnimationKeyFrame.LINEAR ) return true;
		else return false;
	}


	//-------------------------------------------------------------- Popup menu
	private void showPopup(MouseEvent e) 
	{
		keyframeMenu = new JPopupMenu();
		setInterpolation = new JMenuItem("Set Interpolation");
		setInterpolation.addActionListener(this);
		keyframeMenu.add( setInterpolation );

		keyframeMenu.addSeparator();

		freezeAll = new JMenuItem("Freeze Value To Current");
		freezeAll.addActionListener(this);
		keyframeMenu.add( freezeAll );

		keyframeMenu.addSeparator();

		selectFollowing = new JMenuItem("Select Following Keyframes");
		selectFollowing.addActionListener(this);
		keyframeMenu.add( selectFollowing );

		selectPrevious = new JMenuItem("Select Previous Keyframes");
		selectPrevious.addActionListener(this);
		keyframeMenu.add( selectPrevious );

		keyframeMenu.show( e.getComponent(), e.getX(), e.getY() );
	}

	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == setInterpolation ) LayerCompositorMenuActions.setInterpolation();
		if( e.getSource() == freezeAll ) LayerCompositorMenuActions.freezeAllToCurrent();
		if( e.getSource() == selectFollowing ) EditorsController.selectFollowing();
		if( e.getSource() == selectPrevious ) EditorsController.selectPrevious();
	}

}//end class
