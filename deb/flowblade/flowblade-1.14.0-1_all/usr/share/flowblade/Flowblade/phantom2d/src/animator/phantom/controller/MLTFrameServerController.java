package animator.phantom.controller;

import animator.phantom.renderer.VideoClipSource;
import animator.phantom.gui.modals.MTextInfo;
import java.io.File;

public class MLTFrameServerController
{
	private static MLTFrameServer frameServer;
	private static final String LOAD = "LOAD";
	private static final String ERROR = "ERROR";
	private static final String RENDER_FRAME = "RENDER_FRAME";
	private static String cacheDirPath = null;
	private static CacheSizeCalculator calculateThread = null;

	public static void init( String rootPath, String diskCacheDirPath)
	{
		cacheDirPath = diskCacheDirPath;

		frameServer = new MLTFrameServer( rootPath, diskCacheDirPath );
		frameServer.launchServer();
		boolean success = frameServer.waitForServer();
		if (success == true )
		{
			frameServer.connect();
		}
	}

	public static String loadClipIntoServer( VideoClipSource movieSource )
	{
		String command = LOAD + " " + movieSource.getFile().getAbsolutePath();
		String answer = frameServer.sendCommand(command);
		return sendAnswerOrNullForError( answer );
	}

	public synchronized static String renderFrame( VideoClipSource movieSource, int frame )
	{
		String command = RENDER_FRAME + " " + movieSource.getFile().getAbsolutePath() + " " + Integer.toString( frame );
		String answer = frameServer.sendCommand(command);
		return sendAnswerOrNullForError( answer );
	}

	public static String getSourceFramesFolder( VideoClipSource movieSource )
	{
		return frameServer.getDiskCacheDirPath() + "/" + movieSource.getMD5id();
	}

	private static String sendAnswerOrNullForError( String answer )
	{
		if ( answer.startsWith(ERROR) )
		{
			return null;
		}

		return answer;
	}

	public static void calculateCacheSize(MTextInfo dickCacheSize, MTextInfo clipsCount)
	{
		calculateThread = new CacheSizeCalculator(cacheDirPath, dickCacheSize, clipsCount);
		calculateThread.start();
	}

	public static void clearDiskCache()
	{
		File phantomdir = new File(cacheDirPath);

		for (File rootfile : phantomdir.listFiles())
		{
			if (CacheSizeCalculator.ignoreThis(rootfile))
				continue;

			// rootfile is a frames if not ignored
			File[] frames = rootfile.listFiles();
			for(int i = 0; i < frames.length; i++)
			{
				frames[i].delete();
			}
			rootfile.delete();
		}

	}

}//end class
