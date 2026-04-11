package io.github.fi0x.sailengine.core;

import io.github.fi0x.sailengine.core.entity.Entity;
import io.github.fi0x.sailengine.core.entity.Model;
import io.github.fi0x.sailengine.core.lighting.DirectionalLight;
import io.github.fi0x.sailengine.core.lighting.PointLight;
import io.github.fi0x.sailengine.core.lighting.SpotLight;
import io.github.fi0x.sailengine.core.utils.Constants;
import io.github.fi0x.sailengine.core.utils.Transformation;
import io.github.fi0x.sailengine.core.utils.Utils;
import io.github.fi0x.sailengine.test.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager
{
	private final WindowManager window;
	private ShaderManager shader;

	private Map<Model, List<Entity>> entities = new HashMap<>();

	public RenderManager()
	{
		window = Launcher.getWindow();
	}

	public void init() throws Exception
	{
		shader = new ShaderManager();
		shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
		shader.link();

		shader.createUniform("textureSampler");
		shader.createUniform("transformationMatrix");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("ambientLight");
		shader.createMaterialUniform("material");
		shader.createUniform("specularPower");
		shader.createDirectionalLightingUniform("directionalLight");
		shader.createPointLightListUniform("pointLights", 5);
		shader.createSpotLightListUniform("spotLights", 5);
	}

	public void bind(Model model)
	{
		GL30.glBindVertexArray(model.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		shader.setUniform("material", model.getMaterial());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
	}

	public void unbind()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void prepare(Entity entity, Camera camera)
	{
		shader.setUniform("textureSampler", 0);
		shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
		shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
	}

	public void renderLights(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
							 DirectionalLight directionalLight)
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

		shader.bind();
		shader.setUniform("projectionMatrix", window.updateProjectionMatrix());

		renderLights(camera, pointLights, spotLights, directionalLight);
		for (Model model : entities.keySet())
		{
			bind(model);
			List<Entity> entityList = entities.get(model);
			for (Entity entity : entityList)
			{
				prepare(entity, camera);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbind();
		}

		entities.clear();
		shader.unbind();
	}

	public void processEntity(Entity entity)
	{
		List<Entity> entityList = entities.get(entity.getModel());
		if (entityList != null)
			entityList.add(entity);
		else
		{
			List<Entity> newEntityList = new ArrayList<>();
			newEntityList.add(entity);
			entities.put(entity.getModel(), newEntityList);
		}
	}

	public void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void cleanup()
	{
		shader.cleanup();
	}
}
