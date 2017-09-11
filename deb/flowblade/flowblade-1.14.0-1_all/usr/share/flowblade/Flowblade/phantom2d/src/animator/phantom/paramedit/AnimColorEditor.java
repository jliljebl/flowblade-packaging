package animator.phantom.paramedit;

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.ColorSelectTarget;
import animator.phantom.renderer.param.ParamKeyFrameInfo;

/**
* 
*/
public class AnimColorEditor extends JPanel implements ColorSelectTarget, FrameChangeListener, ActionListener
{
	//--- AnimatedValue edited with this component
	private AnimatedValueVectorParam editValue;
	//--- GUI component used to edit value.
	private JButton colorButton;
	//--- Keyframe editor component
	private ParamVectorKeyFramesEditor kfEdit;

	private static int COLOR_DISPLAY_WIDTH = 30;
	private static int COLOR_DISPLAY_HEIGHT = ParamEditResources.PARAM_ROW_HEIGHT;

	private static int RED = PluginUtils.ANIM_COLOR_RED;
	private static int GREEN = PluginUtils.ANIM_COLOR_GREEN;
	private static int BLUE = PluginUtils.ANIM_COLOR_BLUE;

	/**
	*/
	public AnimColorEditor( String text, AnimatedValueVectorParam editValue )
	{
		this.editValue = editValue;
		editValue.setParamName( text );
		int currentFrame = EditorInterface.getCurrentFrame();

		//--- Text field
		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		textLabel.setPreferredSize( new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width - 17, ParamEditResources.EDIT_ROW_HALF_SIZE.height ) );
		//--- Keyframe editor
		ParamKeyFrameInfo kfInfo = 
			editValue.elem( 0 ).getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit = new ParamVectorKeyFramesEditor( editValue, kfInfo );
		
		//--- Color select
		ImageIcon colorIcon = getColorIcon( getColor( currentFrame ) );
		colorButton = new JButton( colorIcon );
		colorButton.setPreferredSize( new Dimension( COLOR_DISPLAY_WIDTH, COLOR_DISPLAY_HEIGHT ) );
		colorButton.addActionListener(this);

		//--- Create layout
		//--- Left side
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );
// 
		//--- Right side
		JPanel rightPanel = new JPanel();		
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( kfEdit );
		rightPanel.add( colorButton );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
 		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );

	}	
	/**
	* Called after current frame has been changed to display correct value.
	*/
	public void frameChanged()
	{
		colorButton.setIcon( getColorIcon( getColor( EditorInterface.getCurrentFrame() ) ) );

		ParamKeyFrameInfo kfInfo = 
			editValue.elem(0).getKeyFrameInfo( EditorInterface.getCurrentFrame() );
		kfEdit.setStateAndDisplay( kfInfo );
	}

	/**
	* Handles button presses.
	*/
	public void actionPerformed(ActionEvent e)
	{
		DialogUtils.displayColorSelect( this, getColor( EditorInterface.getCurrentFrame() ) );
	}

	private Color getColor( int frame )
	{
		AnimatedValue red = editValue.elem( RED );
		AnimatedValue green = editValue.elem( GREEN );
		AnimatedValue blue = editValue.elem( BLUE );
		return new Color( (int) red.get( frame ), (int) green.get( frame ), (int) blue.get( frame ) );
	}

	private ImageIcon getColorIcon( Color c )
	{
		BufferedImage iconImg = new BufferedImage( COLOR_DISPLAY_WIDTH, COLOR_DISPLAY_HEIGHT, BufferedImage.TYPE_INT_ARGB );
		Graphics g = iconImg.getGraphics();
		g.setColor( c );
		g.fillRect( 0,0,COLOR_DISPLAY_WIDTH,COLOR_DISPLAY_HEIGHT);
		g.dispose();
		return new ImageIcon( iconImg );
	}

	public void colorSelected( Color newColor )
	{
		int frame = EditorInterface.getCurrentFrame();
		if( newColor == null ) 
			return;
		else{

			editValue.elem( RED ).setValue( frame, (float) newColor.getRed()  );
			editValue.elem( RED ).getIOP().createKeyFramesDrawVector();
			editValue.elem( RED ).registerUndo( true );
			editValue.elem( GREEN ).setValue( frame, (float) newColor.getGreen()  );
			editValue.elem( RED ).getIOP().createKeyFramesDrawVector();
			editValue.elem( RED ).registerUndo( false );
			editValue.elem( BLUE ).setValue( frame, (float) newColor.getBlue()  );
			editValue.elem( RED ).getIOP().createKeyFramesDrawVector();
			editValue.elem( RED ).registerUndo( false );

			ParamKeyFrameInfo kfInfo = 
				editValue.elem( RED ).getKeyFrameInfo( EditorInterface.getCurrentFrame() );
			kfEdit.setStateAndDisplay( kfInfo );
		}

		colorButton.setIcon( getColorIcon( getColor( frame ) ) );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
	}

}//end class
