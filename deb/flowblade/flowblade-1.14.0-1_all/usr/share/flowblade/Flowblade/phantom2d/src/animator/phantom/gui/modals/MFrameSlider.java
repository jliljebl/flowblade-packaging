package animator.phantom.gui.modals;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import animator.phantom.controller.ProjectController;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;

public class MFrameSlider extends MInputField implements ChangeListener
{
	private JSlider slider;
	private JLabel val;
	//private JLabel maxVal;

	public MFrameSlider(String msg,  int leftSize, int rightSize, int value, int min, int max )
	{
		setLeftAsLabel( msg );
		slider = new JSlider( min, max, value );
		slider.setPaintTicks(false);
		slider.setPaintLabels(false);
		slider.setPreferredSize( new Dimension( rightSize - 50, 30 ));
		slider.addChangeListener( this );
	
		//valueDisplay = new JLabel( Integer.toString( value ) );
		//valueDisplay.setPreferredSize( new Dimension( 50, 30 ));
		int fps = ProjectController.getFramesPerSecond();
		val = new JLabel();
		//maxVal = new JLabel();
		val.setText( TimeLineDisplayPanel.parseTimeCodeString( value, 6, fps) );


		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.add( slider );
		rightPanel.add( val );
	
		this.rightComponent = rightPanel;

		initPanels();
	}
	
	public int getIntValue(){ return slider.getValue(); }
	
	public void stateChanged(ChangeEvent e) 
	{
			int value = (int) slider.getValue();
			int fps = ProjectController.getFramesPerSecond();
			val.setText( TimeLineDisplayPanel.parseTimeCodeString( value, 6, fps) );
	}
}
