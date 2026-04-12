package io.github.fi0x.sailengine.core.rendering;

import io.github.fi0x.sailengine.core.Camera;
import io.github.fi0x.sailengine.core.ShaderManager;
import io.github.fi0x.sailengine.core.WindowManager;
import io.github.fi0x.sailengine.core.entity.Entity;
import io.github.fi0x.sailengine.core.lighting.DirectionalLight;
import io.github.fi0x.sailengine.core.lighting.PointLight;
import io.github.fi0x.sailengine.core.lighting.SpotLight;
import io.github.fi0x.sailengine.core.utils.Constants;
import io.github.fi0x.sailengine.test.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderManager
{
	private final WindowManager window;
	private EntityRender entityRenderer;

	public RenderManager()
	{
		window = Launcher.getWindow();
	}

	public void init() throws Exception
	{
		entityRenderer = new EntityRender();

		entityRenderer.init();
	}

	public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights,
									DirectionalLight directionalLight,
									ShaderManager shader)
	{
		shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
		shader.setUniform("specularPower", Constants.SPECULAR_POWER);

		int numLights = spotLights != null ? spotLights.length : 0;
		for (int i = 0; i < numLights; i++)
			shader.setUniform("spotLights", spotLights[i], i);

		numLights = pointLights != null ? pointLights.length : 0;
		for (int i = 0; i < numLights; i++)
			shader.setUniform("pointLights", pointLights[i], i);

		shader.setUniform("directionalLight", directionalLight);
	}

	public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights,
					   SpotLight[] spotLights)
	{
		clear();

		if (window.isResize())
		{
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(false);
		}

		entityRenderer.render(camera, pointLights, spotLights, directionalLight);
	}

	public void processEntity(Entity entity)
	{
		List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
		if (entityList != null)
			entityList.add(entity);
		else
		{
			List<Entity> newEntityList = new ArrayList<>();
			newEntityList.add(entity);
			entityRenderer.getEntities().put(entity.getModel(), newEntityList);
		}
	}

	public void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void cleanup()
	{
		entityRenderer.cleanup();
	}
}
