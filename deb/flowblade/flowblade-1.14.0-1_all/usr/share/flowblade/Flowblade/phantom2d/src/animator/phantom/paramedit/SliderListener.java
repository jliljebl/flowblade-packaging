package animator.phantom.paramedit;

/**
* Editors use this interface to get slider edit value changes.
*/ 
public interface SliderListener
{
	public abstract void valueChanged( Object source, float value ); 
} 