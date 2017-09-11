package animator.phantom.gui.timeline;

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
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
//import animator.phantom.gui.GUIResources;
import animator.phantom.gui.GUIUtils;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.undo.TimeLineUndoEdit;

public class TimeLineEditorPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
{	
	//--- ImageOperations currently in timeline editing.
	private Vector <TimeLineEditorIOPClip> iopClips = new Vector<TimeLineEditorIOPClip>();

	private TimeLineEditorIOPClip currentClipBeingEdited = null;
	
	private int verticalPos = 0;// we're using real slider here now, look to remove
	private int sliderPos = 50;// this is not a real slider

	private JPopupMenu clipMenu;
	private JMenuItem moveUp;
	private JMenuItem moveDown;
	private JMenuItem clipOutToCurrent;
	private JMenuItem clipInToCurrent;
	private JMenuItem moveClipTailToCurrent;
	private JMenuItem moveClipHeadToCurrent;
	
	//---------------------------------------------- CONSTRUCTOR
	public TimeLineEditorPanel()
	{
		System.out.print("INITIALIZING TIMELINE EDITOR..." );

		//--- Add listeners.
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );


		//--- Set Bg color
		setBackground( GUIColors.flowBGColor );

		System.out.println("...DONE" );
	}
	
	//---------------------------------------------- INTERFACE
	public void initGUI()
	{
		iopClips = new Vector<TimeLineEditorIOPClip>();
		Vector<ImageOperation> clips = TimeLineController.getClips();
		for( int i = 0; i < clips.size(); i++ )
		{
			TimeLineEditorIOPClip addClip = new TimeLineEditorIOPClip(  clips.elementAt( i ) );
			iopClips.addElement( addClip );
		}
		//GUIComponents.clipVertSlider.setValue( 50 );
		repaint();
	}

	public void scaleOrPositionChanged(){ repaint(); }

	//---------------------------------------------- MOUSE EVENTS
	public void mousePressed(MouseEvent e)
	{
		PreviewController.stopPlaybackRequest();

		//--- Send press event to clip that corresponds to y. 
		int y = e.getY() - verticalPos;
		int rowHeight = AnimFrameGUIParams.TE_ROW_HEIGHT;
		//--- if press below all clips, leave.
		if( y >=  (iopClips.size() * rowHeight ) )
		{
			TimeLineController.unselectAllClips();
			TimeLineController.clipEditorRepaint();
			return;
		}
		//--- Get clip index.
		int clipIndex = y / rowHeight;
		//--- forward event
		TimeLineEditorIOPClip clip = iopClips.elementAt( clipIndex );
		if(e.getButton() == MouseEvent.BUTTON3 )
		{
			TimeLineController.setAsSingleSelectedClip( clip.getIOP() );
			showPopup(e);
			return;
		}
			
		if( clip.getIOP().getLocked() )
			return;
		
		clip.mousePressed( e );
		
		if( clip.isBeingEdited()   )
		{
			currentClipBeingEdited = clip;
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if( currentClipBeingEdited == null ) return;
		currentClipBeingEdited.mouseDragged( e );
		repaint();
	}

	public void mouseReleased(MouseEvent e)
	{
		if( currentClipBeingEdited == null ) return;

		TimeLineUndoEdit undoEdit = new TimeLineUndoEdit( currentClipBeingEdited.getIOP() );
		currentClipBeingEdited.mouseReleased( e );
		undoEdit.setAfterState( currentClipBeingEdited.getIOP() );
		PhantomUndoManager.addUndoEdit( undoEdit );

		repaint();
		currentClipBeingEdited  = null;
	}
	//--- Double click opens iop in ParamEditor
	public void mouseClicked(MouseEvent e)
	{
		if( e.getClickCount() == 2 )
		{
			int y = e.getY() - verticalPos;
			int rowHeight = AnimFrameGUIParams.TE_ROW_HEIGHT;
			//--- Get clip inbdex.
			int clipIndex = y / rowHeight;
			//--- forward event and repaint if causes edit to start;
			TimeLineEditorIOPClip clip = iopClips.elementAt( clipIndex );
			if( clip != null )
				ParamEditController.displayEditFrame( clip.getIOP() );
		}
	}
	//--- Mouse events that are not handled.
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

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
				sliderPos = sliderPos - 20;
				if(sliderPos < 0) sliderPos = 0;

			}
			else
			{
				sliderPos = sliderPos + 20;
				if( sliderPos > 100 ) sliderPos = 100;
			}
			calculateVPos();
			GUIComponents.timeLineIOPColumnPanel.repaint();
			repaint();
		}		
	}

	public void calculateVPos()
	{
		Dimension size = getSize( null );
		int panelHeight = size.height;
		int clipsHeight = iopClips.size() *  AnimFrameGUIParams.TE_ROW_HEIGHT;
		if( panelHeight >= clipsHeight  )
		{
			verticalPos = 0;
			return;
		}
		int offScreenArea = clipsHeight - panelHeight;
		verticalPos = -(( offScreenArea * sliderPos ) / 100 );
	}

	public int getVerticalPos()
	{
		calculateVPos();
		return verticalPos;
	}

	//---------------------------------------------- GRAPHICS
	public void paintComponent( Graphics g )
	{
		//--- Erase bg
		g.setColor( GUIColors.clipEditorBGColor );
		g.fillRect( 0,0,getWidth(), getHeight() );
		//--- Draw tracks
		int tracksCount = ( getHeight() / AnimFrameGUIParams.TE_ROW_HEIGHT) + 1;
		int rowHeight = AnimFrameGUIParams.TE_ROW_HEIGHT;
		g.setColor( GUIColors.trackLineColor );
		for( int i = 0; i < tracksCount; i++ )
		{	
			g.drawLine( 0, i * rowHeight, getWidth(), i * rowHeight + verticalPos );
		}
		//--- Draw frame lines
		GUIUtils.drawFrameLines( (Graphics2D) g, getHeight() );

		//--- Draw clips
		TimeLineEditorIOPClip clip;
		for( int i = 0; i < iopClips.size(); i++ )
		{
			clip = iopClips.elementAt( i );
			clip.paintClip( g, (float) i * rowHeight + (float)verticalPos );
			g.setColor( GUIColors.trackLineColor );
			g.drawLine( 0, i * rowHeight + verticalPos, getWidth(), i * rowHeight + verticalPos );
		}
	}

	//----------------------------------------- popup menu items
	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == clipInToCurrent ) TimeLineController.trimSelectedStartToCurrent();
		if( e.getSource() == clipOutToCurrent ) TimeLineController.trimSelectedEndToCurrent();
		if( e.getSource() == moveClipHeadToCurrent ) TimeLineController.moveClipStartToCurrent();
		if( e.getSource() == moveClipTailToCurrent ) TimeLineController.moveClipEndToCurrent();
		if( e.getSource() == moveUp )TimeLineController.moveSelectedClipsUp();
		if( e.getSource() == moveDown ) TimeLineController.moveSelectedClipsDown();
	}
	//-------------------------------------------------------------- Popup menu
	private void showPopup(MouseEvent e) 
	{
		clipMenu = new JPopupMenu();
		
		moveUp = new JMenuItem("Move Up");
		moveUp.addActionListener(this);
		clipMenu.add( moveUp );

		moveDown = new JMenuItem("Move Down");
		moveDown.addActionListener(this);
		clipMenu.add( moveDown );

		clipMenu.addSeparator();
		
		clipOutToCurrent = new JMenuItem("Strecth clip out to current frame" );
		clipOutToCurrent.addActionListener(this);
		clipMenu.add( clipOutToCurrent );
		
		clipInToCurrent = new JMenuItem("Strecth clip in to current frame" );
		clipInToCurrent.addActionListener(this);
		clipMenu.add( clipInToCurrent );
		
		moveClipTailToCurrent = new JMenuItem("Move clip to end in current frame" );
		moveClipTailToCurrent.addActionListener(this);
		clipMenu.add( moveClipTailToCurrent );
		
		moveClipHeadToCurrent = new JMenuItem("Move clip to start in current frame" );
		moveClipHeadToCurrent.addActionListener(this);
		clipMenu.add( moveClipHeadToCurrent );
		
		clipMenu.show( e.getComponent(), e.getX(), e.getY() );
	}

}//end class
