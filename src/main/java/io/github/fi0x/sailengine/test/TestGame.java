package io.github.fi0x.sailengine.test;

import io.github.fi0x.sailengine.core.*;
import io.github.fi0x.sailengine.core.entity.Entity;
import io.github.fi0x.sailengine.core.entity.Model;
import io.github.fi0x.sailengine.core.entity.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic
{
	private static final float CAMERA_MOVE_SPEED = 0.05f;
	private static final float MOUSE_SENSITIVITY = 0.2f;

	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;

	private Entity entity;
	private Camera camera;

	private Vector3f cameraInc;

	public TestGame()
	{
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
	}

	@Override
	public void init() throws Exception
	{
		renderer.init();


		Model model = loader.loadObjModel("/models/bunny.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")));
		entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
	}

	@Override
	public void input()
	{
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW.GLFW_KEY_W))
			cameraInc.z -= 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_S))
			cameraInc.z += 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_A))
			cameraInc.x -= 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_D))
			cameraInc.x += 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_F))
			cameraInc.y -= 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_R))
			cameraInc.y += 1;
	}

	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED,
				cameraInc.z * CAMERA_MOVE_SPEED);

		if (mouseInput.isRightButtonPress())
		{
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}

		entity.incRotation(0.0f, 0.5f, 0.0f);
	}

	@Override
	public void render()
	{
		if (window.isResize())
		{
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(true);
		}

		window.setClearColour(0.0f, 0.0f, 0.0f, 0.0f);
		renderer.render(entity, camera);
	}

	@Override
	public void cleanup()
	{
		renderer.cleanup();
		loader.cleanup();
	}
}
