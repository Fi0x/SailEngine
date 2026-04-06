package io.github.fi0x.sailengine.core;

import io.github.fi0x.sailengine.core.utils.Constants;
import io.github.fi0x.sailengine.test.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager
{
	public static final long NANOSECOND = 1000000000L;
	public static final float FRAMERATE = 1000;

	private static int fps;
	private static float frametime = 1.0f / FRAMERATE;

	private boolean isRunning;

	private WindowManager window;
	private GLFWErrorCallback errorCallback;

	private void init() {
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		window = Launcher.getWindow();
		window.init();
	}

	public void start() {
		init();
		if(isRunning)
			return;

		run();
	}

	private void run() {
		isRunning = true;
		int frames = 0;
		long frameCounter = 0;
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;

		while (isRunning) {
			boolean render = false;
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) NANOSECOND;
			frameCounter += passedTime;

			input();

			while (unprocessedTime > frametime) {
				render = true;
				unprocessedTime -= frametime;

				if(window.windowShouldClose())
					stop();

				if(frameCounter >= NANOSECOND) {
					setFps(frames);
					window.setTitle(Constants.TITLE + "Test FPS: " + getFps());
					frames = 0;
					frameCounter = 0;
				}
			}

			if(render) {
				update();
				render();
				frames++;
			}
		}
		cleanup();
	}

	private void stop() {
		if(!isRunning)
			return;

		isRunning = false;
	}

	private void input() {

	}

	private void render() {
		window.update();
	}

	private void update() {

	}

	private void cleanup() {
		window.cleanup();
		errorCallback.free();
		GLFW.glfwTerminate();
	}

	public static int getFps()
	{
		return fps;
	}

	public static void setFps(int fps)
	{
		EngineManager.fps = fps;
	}
}
