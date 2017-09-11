package animator.phantom.paramedit;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorSelectTarget;

public class AnimColorRGBEditor extends JPanel implements ColorSelectTarget, FrameChangeListener, ActionListener
{
	//--- AnimatedValue edited with this component
	private AnimatedValue red;
	private AnimatedValue green;
	private AnimatedValue blue;

	//--- GUI component used to edit value.
	private JButton colorButton;
	private JCheckBox steppedBox;
	private ImageIcon stepped =  GUIResources.getIcon( GUIResources.stepped );

	private static int COLOR_DISPLAY_WIDTH = 30;
	private static int COLOR_DISPLAY_HEIGHT = ParamEditResources.PARAM_ROW_HEIGHT;

	/**
	*/
	public AnimColorRGBEditor( String text, AnimatedValue red, AnimatedValue green, AnimatedValue blue )
	{
		this.red = red;
		this.green = green;
		this.blue = blue;

		int currentFrame = EditorInterface.getCurrentFrame();

		//--- Text field
		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		textLabel.setPreferredSize( new Dimension( ParamEditResources.EDIT_ROW_HALF_SIZE.width - 17, ParamEditResources.EDIT_ROW_HALF_SIZE.height ) );
		
		//--- Color select
		ImageIcon colorIcon = getColorIcon( getColor( currentFrame ) );
		colorButton = new JButton( colorIcon );
		colorButton.setPreferredSize( new Dimension( COLOR_DISPLAY_WIDTH, COLOR_DISPLAY_HEIGHT ) );
		colorButton.addActionListener(this);

		JLabel steppedLabel = new JLabel();
		steppedLabel.setIcon(stepped);
		
		steppedBox = new JCheckBox();
		//KeyFrameParam kfParam = EditorsController.getCurrentKFParam();
		steppedBox.setSelected( false );
		steppedBox.addActionListener( this );
		steppedBox.setPreferredSize( new Dimension( 20, 20 ));
		
		//--- Create layout
		//--- Left side
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );

		//--- Right side
		JPanel rightPanel = new JPanel();		
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( colorButton );
		rightPanel.add( Box.createRigidArea( new Dimension( 12, 0 ) ) );
		rightPanel.add( steppedLabel );
		rightPanel.add( steppedBox );
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
	}

	/**
	* Handles button presses.
	*/
	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == steppedBox )
		{
			red.setStepped( steppedBox.isSelected() );
			green.setStepped( steppedBox.isSelected() );
			blue.setStepped( steppedBox.isSelected() );

			EditorsController.updateKFForValueChange();
		}
		else
		{
			DialogUtils.displayColorSelect( this, getColor( EditorInterface.getCurrentFrame() ) );
		}
	}

	private Color getColor( int frame )
	{
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

			red.setValue( frame, (float) newColor.getRed()  );
			red.getIOP().createKeyFramesDrawVector();
			red.registerUndo( true );
			green.setValue( frame, (float) newColor.getGreen()  );
			green.getIOP().createKeyFramesDrawVector();
			green.registerUndo( false );
			blue.setValue( frame, (float) newColor.getBlue()  );
			blue.getIOP().createKeyFramesDrawVector();
			blue.registerUndo( false );
		}

		colorButton.setIcon( getColorIcon( getColor( frame ) ) );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
	}

}//end class
