package io.github.fi0x.sailengine.test;

import io.github.fi0x.sailengine.core.*;
import io.github.fi0x.sailengine.core.entity.Entity;
import io.github.fi0x.sailengine.core.entity.Model;
import io.github.fi0x.sailengine.core.entity.Texture;
import io.github.fi0x.sailengine.core.lighting.DirectionalLight;
import io.github.fi0x.sailengine.core.lighting.PointLight;
import io.github.fi0x.sailengine.core.lighting.SpotLight;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

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

	private float lightAngle;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights;
	private SpotLight[] spotLights;

	public TestGame()
	{
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		lightAngle = -90;
	}

	@Override
	public void init() throws Exception
	{
		renderer.init();

		Model model = loader.loadObjModel("/models/cube-custom.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
		entity = new Entity(model, new Vector3f(0f, 0f, -5), new Vector3f(0, 0, 0), 1);

		float lightIntensity = 1.0f;
		Vector3f lightPosition = new Vector3f(0, 0, -3.2f);
		Vector3f lightColour = new Vector3f(1, 1, 1);
		PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);

		Vector3f coneDir = new Vector3f(0, 0, 1);
		float cutoff = (float) Math.cos(Math.toRadians(180));
		SpotLight spotLight = new SpotLight(
				new PointLight(lightColour, new Vector3f(0, 0, 1f), lightIntensity, 0, 0, 1), coneDir, cutoff);

		SpotLight spotLight1 = new SpotLight(pointLight, coneDir, cutoff);

		lightPosition = new Vector3f(-1, -10, 0);
		lightColour = new Vector3f(1, 1, 1);
		directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

		pointLights = new PointLight[]{pointLight};
		spotLights = new SpotLight[]{spotLight, spotLight1};
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

		if (window.isKeyPressed(GLFW.GLFW_KEY_L))
			pointLights[0].getPosition().x += 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_J))
			pointLights[0].getPosition().x -= 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_I))
			pointLights[0].getPosition().y += 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_K))
			pointLights[0].getPosition().y -= 0.1f;

		float lightPos = spotLights[0].getPointLight().getPosition().z;
		if (window.isKeyPressed(GLFW.GLFW_KEY_N))
			spotLights[0].getPointLight().getPosition().z = lightPos + 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_M))
			spotLights[0].getPointLight().getPosition().z = lightPos - 0.1f;
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

		//		entity.incRotation(0.0f, 0.25f, 0.0f);

		lightAngle += 0.5f;
		if (lightAngle > 90)
		{
			directionalLight.setIntensity(0);
			if (lightAngle >= 360)
				lightAngle = -90;
		} else if (lightAngle <= -80 || lightAngle >= 80)
		{
			float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
			directionalLight.setIntensity(factor);
			directionalLight.getColour().y = Math.max(factor, 0.9f);
			directionalLight.getColour().z = Math.max(factor, 0.5f);
		} else
		{
			directionalLight.setIntensity(1);
			directionalLight.getColour().x = 1;
			directionalLight.getColour().y = 1;
			directionalLight.getColour().z = 1;
		}
		double angRad = Math.toRadians(lightAngle);
		directionalLight.getDirection().x = (float) Math.sin(angRad);
		directionalLight.getDirection().y = (float) Math.cos(angRad);
	}

	@Override
	public void render()
	{
		renderer.render(entity, camera, directionalLight, pointLights, spotLights);
	}

	@Override
	public void cleanup()
	{
		renderer.cleanup();
		loader.cleanup();
	}
}
