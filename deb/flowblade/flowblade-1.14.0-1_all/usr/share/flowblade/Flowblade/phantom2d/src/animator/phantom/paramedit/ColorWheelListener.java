package animator.phantom.paramedit;

/**
* Editors use this interface to get color wheel edit value changes.
*/ 
public interface ColorWheelListener
{
	/**
	* Called after mouse edit action on color wheel.
	* @param angle Angle of selection in range 0.0 - 360.0.
	* @param distance Distance from wheel center in range from 0.0 to 1.0.
	* @param eventType This is either <code>ColorWheelEditor.DRAG_EVENT</code> or <code>ColorWheelEditor.RELEASE_EVENT</code>.
	*/
	public abstract void valueChanged( float angle, float distance, int eventType ); 
} 