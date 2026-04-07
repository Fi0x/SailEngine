package io.github.fi0x.sailengine.test;

import io.github.fi0x.sailengine.core.EngineManager;
import io.github.fi0x.sailengine.core.WindowManager;
import io.github.fi0x.sailengine.core.utils.Constants;
import org.lwjgl.Version;

public class Launcher
{
	private static WindowManager window;
	private static TestGame game;

	static void main(String[] args)
	{
		window = new WindowManager(Constants.TITLE + "Test", 1600, 900, false);
		game = new TestGame();
		EngineManager engine = new EngineManager();
		try
		{
			engine.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static WindowManager getWindow()
	{
		return window;
	}

	public static TestGame getGame()
	{
		return game;
	}
}
