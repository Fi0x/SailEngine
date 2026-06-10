package io.github.fi0x.sailengine.core.rendering;

import io.github.fi0x.sailengine.core.Camera;
import io.github.fi0x.sailengine.core.ShaderManager;
import io.github.fi0x.sailengine.core.entity.Model;
import io.github.fi0x.sailengine.core.entity.terrain.Terrain;
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
import java.util.List;

public class TerrainRenderer implements IRenderer
{
	private ShaderManager shader;
	private List<Terrain> terrains;

	public TerrainRenderer() throws Exception
	{
		shader = new ShaderManager();
		terrains = new ArrayList<>();
	}

	@Override
	public void init() throws Exception
	{
		shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs"));
		shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fs"));
		shader.link();

		shader.createUniform("textureSampler");
		shader.createUniform("transformationMatrix");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("ambientLight");
		shader.createMaterialUniform("material");
		shader.createUniform("specularPower");
		shader.createDirectionalLightingUniform("directionalLight");
		shader.createPointLightListUniform("pointLights", Constants.MAX_POINT_LIGHTS);
		shader.createSpotLightListUniform("spotLights", Constants.MAX_SPOT_LIGHTS);
	}

	@Override
	public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
					   DirectionalLight directionalLight)
	{
		shader.bind();
		shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());

		RenderManager.renderLights(pointLights, spotLights, directionalLight, shader);
		for (Terrain terrain : terrains)
		{
			bind(terrain.getModel());
			prepare(terrain, camera);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbind();
		}

		terrains.clear();
		shader.unbind();
	}

	@Override
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

	@Override
	public void unbind()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void prepare(Object terrain, Camera camera)
	{
		shader.setUniform("textureSampler", 0);
		shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain) terrain));
		shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
	}

	@Override
	public void cleanup()
	{
		shader.cleanup();
	}

	public List<Terrain> getTerrains()
	{
		return terrains;
	}
}
