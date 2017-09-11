package animator.phantom.gui.modals;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import animator.phantom.gui.modals.MInputField;

public class MSlider extends MInputField implements ChangeListener
{
	private JSlider slider;
	private JLabel valueDisplay;

	public MSlider(String msg,  int leftSize, int rightSize, int value, int min, int max )
	{
		setLeftAsLabel( msg );
		slider = new JSlider( min, max, value );
		slider.setPaintTicks(false);
		slider.setPaintLabels(false);
		slider.setPreferredSize( new Dimension( rightSize - 50, 30 ));
		slider.addChangeListener( this );

		valueDisplay = new JLabel( Integer.toString( value ) );
		valueDisplay.setPreferredSize( new Dimension( 50, 30 ));
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.add( slider );
		rightPanel.add( valueDisplay );

		this.rightComponent = rightPanel;

		initPanels();
	}

	
	public void addSliderChangeListener( ChangeListener listener )
	{
		slider.addChangeListener(listener);
	}
	
	public JSlider getSlider(){ return slider; }
	
	public int getIntValue(){ return slider.getValue(); }
	
	public void stateChanged(ChangeEvent e) 
	{
			int value = (int) slider.getValue();
			valueDisplay.setText( Integer.toString( value ) );
	}

}//end class
