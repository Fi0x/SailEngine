package io.github.fi0x.sailengine.test;

import io.github.fi0x.sailengine.core.*;
import io.github.fi0x.sailengine.core.entity.*;
import io.github.fi0x.sailengine.core.entity.terrain.Terrain;
import io.github.fi0x.sailengine.core.lighting.DirectionalLight;
import io.github.fi0x.sailengine.core.lighting.PointLight;
import io.github.fi0x.sailengine.core.lighting.SpotLight;
import io.github.fi0x.sailengine.core.rendering.RenderManager;
import io.github.fi0x.sailengine.core.utils.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class TestGame implements ILogic
{

	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	private Camera camera;
	private Vector3f cameraInc;

	private SceneManager sceneManager;

	public TestGame()
	{
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		sceneManager = new SceneManager(-90);
	}

	@Override
	public void init() throws Exception
	{
		renderer.init();

		Model model = loader.loadObjModel("/models/cube-custom.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);

		Terrain terrain = new Terrain(new Vector3f(0, -1, 0), loader,
				new Material(new Texture(loader.loadTexture("textures/terrain.png")), 0.1f));
		Terrain terrain2 = new Terrain(new Vector3f(-100, -1, 0), loader,
				new Material(new Texture(loader.loadTexture("textures/flowers.png")), 0.1f));
		sceneManager.addTerrain(terrain);
		sceneManager.addTerrain(terrain2);

		Random rand = new Random();
		for (int i = 0; i < 200; i++)
		{
			float x = rand.nextFloat() * 100 - 50;
			float y = rand.nextFloat() * 100 - 50;
			float z = rand.nextFloat() * -300;
			sceneManager.addEntity(new Entity(model, new Vector3f(x, y, z),
					new Vector3f(rand.nextFloat() * 180, rand.nextFloat() * 180, 0), 1));
		}
		sceneManager.addEntity(new Entity(model, new Vector3f(0f, 0f, -5), new Vector3f(0, 0, 0), 1));

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
		sceneManager.setDirectionalLight(new DirectionalLight(lightColour, lightPosition, lightIntensity));

		sceneManager.setPointLights(new PointLight[]{pointLight});
		sceneManager.setSpotLights(new SpotLight[]{spotLight, spotLight1});
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
			sceneManager.getPointLights()[0].getPosition().x += 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_J))
			sceneManager.getPointLights()[0].getPosition().x -= 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_I))
			sceneManager.getPointLights()[0].getPosition().y += 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_K))
			sceneManager.getPointLights()[0].getPosition().y -= 0.1f;

		float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
		if (window.isKeyPressed(GLFW.GLFW_KEY_N))
			sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
		if (window.isKeyPressed(GLFW.GLFW_KEY_M))
			sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
	}

	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		camera.movePosition(cameraInc.x * Constants.CAMERA_MOVE_SPEED, cameraInc.y * Constants.CAMERA_MOVE_SPEED,
				cameraInc.z * Constants.CAMERA_MOVE_SPEED);

		if (mouseInput.isRightButtonPress())
		{
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
		}

		//		entity.incRotation(0.0f, 0.25f, 0.0f);

		sceneManager.incLightAngle(0.5f);
		if (sceneManager.getLightAngle() > 90)
		{
			sceneManager.getDirectionalLight().setIntensity(0);
			if (sceneManager.getLightAngle() >= 360)
				sceneManager.setLightAngle(-90);
		} else if (sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80)
		{
			float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
			sceneManager.getDirectionalLight().setIntensity(factor);
			sceneManager.getDirectionalLight().getColour().y = Math.max(factor, 0.9f);
			sceneManager.getDirectionalLight().getColour().z = Math.max(factor, 0.5f);
		} else
		{
			sceneManager.getDirectionalLight().setIntensity(1);
			sceneManager.getDirectionalLight().getColour().x = 1;
			sceneManager.getDirectionalLight().getColour().y = 1;
			sceneManager.getDirectionalLight().getColour().z = 1;
		}
		double angRad = Math.toRadians(sceneManager.getLightAngle());
		sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
		sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);

		for (Entity entity : sceneManager.getEntities())
			renderer.processEntity(entity);

		for (Terrain terrain : sceneManager.getTerrains())
			renderer.processTerrain(terrain);
	}

	@Override
	public void render()
	{
		renderer.render(camera, sceneManager);
	}

	@Override
	public void cleanup()
	{
		renderer.cleanup();
		loader.cleanup();
	}
}