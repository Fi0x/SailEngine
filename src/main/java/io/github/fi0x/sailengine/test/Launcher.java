package io.github.fi0x.sailengine.test;

import io.github.fi0x.sailengine.core.WindowManager;
import org.lwjgl.Version;

public class Launcher
{
	public static void main(String[] args) {
		System.out.println("Running with version " + Version.getVersion());
		WindowManager window = new WindowManager("Test Game", 1600, 900, false);
		window.init();

		while (!window.windowShouldClose())
			window.update();

		window.cleanup();
	}
}
