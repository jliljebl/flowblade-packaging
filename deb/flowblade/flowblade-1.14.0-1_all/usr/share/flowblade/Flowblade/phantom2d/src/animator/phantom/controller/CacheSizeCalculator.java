package animator.phantom.controller;

import animator.phantom.gui.modals.MTextInfo;
import java.io.File;

public class CacheSizeCalculator extends Thread
{
	private MTextInfo dickCacheSize = null;
	private MTextInfo clipsCount = null;
	private File cacheDir = null;

	public CacheSizeCalculator(String cacheDirPath, MTextInfo dickCacheSize, MTextInfo clipsCount)
	{
		this.dickCacheSize = dickCacheSize;
		this.clipsCount = clipsCount;
		this.cacheDir = new File(cacheDirPath);
	}

	public void run()
	{
		long size = folderSize(this.cacheDir, true);


		System.out.println("disk cache size:");
		System.out.println(size);

	}

	public long folderSize(File directory, boolean isRoot)
	{
		long length = 0;
		int dirCount = 0;
		for (File file : directory.listFiles())
		{
			if (ignoreThis(file))
				continue;


			if (file.isFile())
			{
				length += file.length();
			}
			else
			{
				length += folderSize(file, false);
				if (isRoot)
				{
					dirCount += 1;
				}
			}
		}
		if (isRoot)
		{
			dickCacheSize.setText(Long.toString(Math.round(length / 1000000)) + " MB");
			clipsCount.setText(Integer.toString(dirCount));
		}
		return length;
	}

	public static boolean ignoreThis(File file)
	{
		String name = file.getName();
		if(name.startsWith("session"))
			return true;
		if(name.startsWith("temp_frame"))
			return true;

		return false;
	}



}//end class CacheSizeCalculator
