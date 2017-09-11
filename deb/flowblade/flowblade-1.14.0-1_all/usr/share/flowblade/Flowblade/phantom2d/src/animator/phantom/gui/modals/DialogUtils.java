package animator.phantom.gui.modals;

/*
    Copyright Janne Liljeblad.

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.GUIResources;
import animator.phantom.project.Project;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.SequencePlaybackSource;
import animator.phantom.renderer.param.ColorSelectTarget;

//--- A collection class to create one-liner modal windows.
public class DialogUtils
{
	public static final int OK_OPTION = 1;

	public static final int  WARNING_MESSAGE = PHDialog.WARNING_MESSAGE;

	private static SimpleAttributeSet HELVETICA_12 = new SimpleAttributeSet();

	static
	{
		StyleConstants.setForeground(HELVETICA_12, Color.black);
		StyleConstants.setFontFamily(HELVETICA_12, "Helvetica");
		StyleConstants.setFontSize(HELVETICA_12, 12);
	}

	private static Component currentParent = null;

	private static ColorSelect colorSelect = new ColorSelect();
	private static ColorSelectTarget selectTarget = null;

	public static void setDialogParent( Component parent ){ currentParent = parent; }

	public static String getTextInput( 	String title,
					String text,
					String defaultText,
					int LEFT,
					int RIGHT)
	{

		MTextField textInput = new MTextField( text, LEFT, RIGHT, defaultText );
		textInput.setTextFieldSize( RIGHT - 12 );

		MInputArea area = new MInputArea( "" );
		area.add( textInput );

		MInputPanel panel = new MInputPanel( title );
		panel.add( area );

		int retVal = DialogUtils.showMultiInput( panel, 450, 110 );
		if( retVal != DialogUtils.OK_OPTION ) return null;

		return textInput.getStringValue();
	}

	public static int showMultiInput( MInputPanel ip)
	{
		return showMultiInput( ip, 500, 500 );
	}
	//---
	public static int showMultiInput( MInputPanel ip, int width, int height )
	{
		return showMultiInput( ip, width, height, true );
	}

	public static int showMultiInput( MInputPanel ip, int width, int height, boolean showCancel )
	{
		MultiInputDialogPanel p = new MultiInputDialogPanel( ip );
		ip.setPanel( p );

		Component parent = currentParent;
		if( parent == null ) parent = GUIComponents.getAnimatorFrame();

		MDialog dialog = new MDialog( (Frame) parent,  ip.getTitle(), p,  width, height, showCancel );

		dialog.setVisible( true );//this blocks until button pressed

		int selectedValue = dialog.getResponseValue();

		//--- With this button order return values correspond to CLOSED_OPTION, OK_OPTION, CANCEL_OPTION
		//--- as defined here.
		return selectedValue;
	}

	public static MDialog getMultiInputDialog( MInputPanel ip, int width, int height, boolean showCancel )
	{
		MultiInputDialogPanel p = new MultiInputDialogPanel( ip );
		ip.setPanel( p );

		Component parent = currentParent;
		if( parent == null ) parent = GUIComponents.getAnimatorFrame();

		MDialog dialog = new MDialog( (Frame) parent,  ip.getTitle(), p,  width, height, showCancel );

		return dialog;
	}

	public static void showPanelOKDialog( JPanel panel, String title, int width, int height )
	{
		String[] options = { "Ok" };
		panel.setPreferredSize( new Dimension( width, height ) );
		PHDialog dialog = new PHDialog( GUIComponents.getAnimatorFrame(), title, options, panel, PHDialog.PLAIN_MESSAGE );
		dialog.setVisible( true );//blocks
	}

	public static void showTwoStyleInfo( String boldText, String text, int type )
	{
		String[] tLines = { text };
		showTwoStyleInfo( boldText, tLines, type );
	}

	public static void showTwoStyleInfo( String boldText, String[] tLines, int type )
	{
		String[] bLines = { boldText };
		String[] options = { "Ok" };

		showTwoTextStyleDialog( type,
					null,
					options,
					bLines,
					tLines,
					GUIComponents.getAnimatorFrame() );
	}

	public static int showTwoTextStyleDialog( int type, String title, String[] options, String boldText, String text )
	{
		String[] bLines = { boldText };
		String[] tLines = { text };
		return showTwoTextStyleDialog( type, title, options, bLines, tLines );
	}

	public static int showTwoTextStyleDialog( int type, String title, String[] options, String[] bLines, String[] tLines )
	{
		return showTwoTextStyleDialog( type, title, options, bLines, tLines, GUIComponents.getAnimatorFrame() );
	}

	public static int showTwoTextStyleDialog( 	int type,
							String title,
							String[] options,
							String[] bLines,
							String[] tLines,
							Component parent )
	{
		JPanel pane = new JPanel();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ));

		for( int i = 0; i < bLines.length; i++ )
		{
			JLabel textL = new JLabel( bLines[ i ] );
			textL.setFont( GUIResources.BOLD_FONT_14 );
			pane.add( textL );
		}
		pane.add( Box.createRigidArea( new Dimension(0,8) ) );
		for( int i = 0; i < tLines.length; i++ )
		{
			JLabel textL = new JLabel( tLines[ i ] );
			textL.setFont( GUIResources.BASIC_FONT_12 );
			pane.add( textL );
		}

		PHDialog dialog = new PHDialog((Frame)parent, title, options, pane, type );

		dialog.setVisible( true );//this blocks

		int selectedValue = dialog.getResponseValue();
		dialog = null;
		if( selectedValue == -1 ) return JOptionPane.CLOSED_OPTION;

		return selectedValue;
	}

	public static void displayColorSelect( ColorSelectTarget newTarget, Color c )
	{
		selectTarget = newTarget;
		colorSelect.setColor( c );
		colorSelect.setVisible( true );
	}

	public static void colorSelected( Color c )
	{
		if( selectTarget != null )
			selectTarget.colorSelected( c );
		colorSelect.setVisible( false );
		selectTarget = null;
	}

	public static void showProjectInfoDialog( Project project )
	{
		JPanel panel = new JPanel();
		JPanel left = new JPanel();
		JPanel right = new JPanel();

		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS) );
		left.setLayout( new BoxLayout( left, BoxLayout.Y_AXIS) );
		right.setLayout( new BoxLayout( right, BoxLayout.Y_AXIS) );

		panel.add( left );
		panel.add( right );

		left.add( new JLabel( "Name:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( project.getName() ) );

		left.add( new JLabel( "Path:" ) );
		right.add( getSmallRigid() );
		if( project.getSaveFile() != null )
			right.add( getRightLabel( project.getSaveFile().getAbsolutePath() ) );
		else
			right.add( getRightLabel( "not saved" ) );

		left.add( new JLabel( "Format:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( project.getFormatName() ) );

		Dimension sized = project.getScreenDimensions();
		String screenSize = ( new Integer( sized.width ) ).toString() + " x " + ( new Integer( sized.height ) ).toString();
		left.add( new JLabel( "Screen size:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( screenSize ) );

		left.add( new JLabel( "Length:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( ( new Integer( project.getLength() ) ).toString() + " frames" ) );

		left.add( new JLabel( "Frames per second:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( ( new Integer( project.getFramesPerSecond() ) ).toString()  ) );

		left.add( new JLabel( "Nodes in flow:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( ( new Integer( project.getRenderFlow().getSize() ) ).toString()  ) );

		left.add( new JLabel( "File sources:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( ( new Integer( project.getFileSources().size() ) ).toString()  ) );

		left.add( new JLabel( "Bins:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( ( new Integer( project.getBins().size() ) ).toString()  ) );

		String[] options = { "Ok" };
		PHDialog dialog = new PHDialog( GUIComponents.getAnimatorFrame(), "Project Info", options, panel, PHDialog.PLAIN_MESSAGE );
		dialog.setVisible( true );//blocks
	}

	public static void showFileSourceInfoDialog( FileSource fileSource )
	{
		JPanel panel = new JPanel();
		JPanel left = new JPanel();
		JPanel right = new JPanel();

		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS) );
		left.setLayout( new BoxLayout( left, BoxLayout.Y_AXIS) );
		right.setLayout( new BoxLayout( right, BoxLayout.Y_AXIS) );

		panel.add( left );
		panel.add( right );

		left.add( new JLabel( "Name:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( fileSource.getName() ) );

		left.add( new JLabel( "Path:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( fileSource.getFile().getAbsolutePath() ) );

		left.add( new JLabel( "Type:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( fileSource.getTypeDesc() ) );

		String present = "YES";
		if( fileSource.fileAvailable() == false )
			present = "NO";
		left.add( new JLabel( "File present:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( present ) );

		int width = fileSource.getImageWidth();
		int height = fileSource.getImageHeight();
		String screenSize = ( new Integer( width ) ).toString() + " x " + ( new Integer( height ) ).toString();
		left.add( new JLabel( "Image size:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( screenSize ) );

		String length = "FREE LENGTH";
		if( fileSource instanceof SequencePlaybackSource )
		{
			SequencePlaybackSource fss = (SequencePlaybackSource) fileSource;
			length = ( new Integer(   fss.getProgramLength() ) ).toString()  + " frames" ;
		}
		left.add( new JLabel( "Length:" ) );
		right.add( getSmallRigid() );
		right.add( getRightLabel( length ) );

		String[] options = { "Ok" };
		PHDialog dialog = new PHDialog( GUIComponents.getAnimatorFrame(), "File Source Info", options, panel, PHDialog.PLAIN_MESSAGE );
		dialog.setVisible( true );//blocks
	}


	public static int showFrameSelectDialog( String title, String msg, int value, int lowerBound, int higherBound )
	{
		MFrameSlider slider = new MFrameSlider(msg,  150, 250, value, lowerBound, higherBound );

		MInputArea area = new MInputArea( "" );
		area.add( slider );

		MInputPanel panel = new MInputPanel( title );
		panel.add( area );

		int selectedValue = showMultiInput( panel, 500, 110 );

		if ( selectedValue == 1) //OK button
		{
			return slider.getIntValue();
		}
		else return -1;
	}

	private static JLabel getRightLabel( String s )
	{
		JLabel l = new JLabel( s );
		l.setFont( GUIResources.BASIC_FONT_12 );
		return l;
	}

	private static Component getSmallRigid()
	{
		return Box.createRigidArea( new Dimension( 10, 0 ) );
	}

}//end class
