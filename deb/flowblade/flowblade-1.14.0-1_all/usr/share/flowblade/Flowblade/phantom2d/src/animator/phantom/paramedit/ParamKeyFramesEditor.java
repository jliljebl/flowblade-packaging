package animator.phantom.paramedit;

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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;
import animator.phantom.renderer.param.ParamKeyFrameInfo;

/**
* A secondary GUI editor component used to move current frame no next or previous key frame
* and to create and delete keyframes. 
* <p>
* Component is placed inside <code>AnimValueNumberEditor</code> or
* <code>AnimValueNumberEditor</code>.
*/
public class ParamKeyFramesEditor extends JPanel implements MouseListener
{
	//---KFParam being navigated and edited.
	private KeyFrameParam kfParam;
	//--- Same param as param for undo	
	private Param param;

	private JLabel leftTriangle;
	private JLabel rightTriangle;
	private JLabel kfButton;
		
	private boolean leftActive = false;
	private boolean kfActive = false;
	private boolean rightActive = false;
	
	private static ImageIcon lTriActive = GUIResources.getIcon(GUIResources.lTriActiveTheme );
	private static ImageIcon lTriNotActive = GUIResources.getIcon( GUIResources.lTriNotActive );
	private static ImageIcon rTriActive = GUIResources.getIcon( GUIResources.rTriActiveTheme );
	private static ImageIcon rTriNotActive = GUIResources.getIcon( GUIResources.rTriNotActive );
	private static ImageIcon kfOn = GUIResources.getIcon( GUIResources.kfOn );
	private static ImageIcon kfOff = GUIResources.getIcon( GUIResources.kfOffTheme );

	private static Dimension size = new Dimension( 48, ParamEditResources.PARAM_ROW_HEIGHT );
	/**
	* A constructor with same parameter cast into two different classes and info of initial state.
	* @param kfParam Edited parameter as <code>KeyFrameParam</code>.
	* @param param Edited parameter as <code>Param</code>.
	* @param kfInfo Info object of editors initial state.
	*/
	public ParamKeyFramesEditor( KeyFrameParam kfParam, Param param, ParamKeyFrameInfo kfInfo )
	{
		//--- Capture kfParam.
		this.kfParam = kfParam;
		this.param = param;

		//--- Create components and add listeners.
		leftTriangle = new  JLabel( lTriNotActive );
		rightTriangle = new JLabel( rTriNotActive );
		kfButton = new JLabel( kfOff );
		leftTriangle.addMouseListener( this );
		rightTriangle.addMouseListener( this );
		kfButton.addMouseListener( this );

		//--- Put gui together.
		setLayout(  new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftTriangle );
		add( kfButton );
		add( rightTriangle );
	
		//--- Set size
		setPreferredSize( size );
		setMaximumSize( size );
	
		//--- Set state.
		setStateAndDisplay( kfInfo );
	}
	/**
	* Set triangles and diamond into desired states and display.
	* @param kfInfo Info object of triangles and diamond state.
	*/
	public void setStateAndDisplay( ParamKeyFrameInfo kfInfo )
	{
		rightActive = kfInfo.keyFramesAfter();
		leftActive = kfInfo.keyFramesBefore();
		kfActive = kfInfo.onKeyFrame();
	
		displayGui();
	}
	//--- Repaint to current state.
	private void displayGui()
	{
		if( leftActive ) leftTriangle.setIcon( lTriActive );
			else leftTriangle.setIcon( lTriNotActive );

		if( kfActive ) kfButton.setIcon( kfOn );
			else kfButton.setIcon( kfOff );

		if( rightActive ) rightTriangle.setIcon( rTriActive );
			else rightTriangle.setIcon( rTriNotActive );
	
		repaint();
	}

	//------------------------------------------ MOUSE EVENTS
	/**
	* Handles mouse event.
	*/
	public void mouseClicked( MouseEvent e)
	{		
		if( e.getSource() == leftTriangle && leftActive  )
			EditorInterface.moveCurrentFrameToPreviousKeyFrame( kfParam );

		if( e.getSource() == rightTriangle && rightActive )
			EditorInterface.moveCurrentFrameToNextKeyFrame( kfParam );
		
		if( e.getSource() == kfButton )
		{
			int currentFrame = EditorInterface.getCurrentFrame();

			//--- Has keyframe in current frame, remove it
			if( kfActive )
			{
				kfParam.removeKeyFrame( currentFrame );
				kfParam.getIOP().createKeyFramesDrawVector();

				UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
				//--- Redraw timeline to updatekeyframe diamonds.
				TimeLineController.initClipsGUI();
				UpdateController.updateCurrentFrameDisplayers( false );

				param.registerUndo();
			}
			//--- Does not have keyframe in current frame.
			//--- Set keyframe with current value
			else
			{
				kfParam.addKeyFrame( currentFrame, kfParam.getValue( currentFrame ) );
				kfParam.getIOP().createKeyFramesDrawVector();

				UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
				TimeLineController.initClipsGUI();
				UpdateController.updateCurrentFrameDisplayers( false );

				param.registerUndo();
			}
		}
	}

	/**
	* Ignored mouse event.
	*/
	public void mouseEntered(MouseEvent e){}
	/**
	* Ignored mouse event.
	*/
	public void mouseExited(MouseEvent e){}
	/**
	* Ignored mouse event.
	*/
	//public void mouseMoved(MouseEvent e){}
	/**
	* Ignored mouse event.
	*/
	public void mousePressed(MouseEvent e){}
	/**
	* Ignored mouse event.
	*/
	public void mouseReleased(MouseEvent e){}

}//end class
