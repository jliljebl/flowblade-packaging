package animator.phantom.plugin;

/**
* This error is thrown when plugin is initialized incorrectly.
*/
public class PhantomPluginInitError extends Error
{
	public PhantomPluginInitError( String msg )
	{
		super( msg );
	}

}//end class
