package io.github.fi0x.sailengine.core.rendering;

import io.github.fi0x.sailengine.core.Camera;
import io.github.fi0x.sailengine.core.entity.Model;
import io.github.fi0x.sailengine.core.lighting.DirectionalLight;
import io.github.fi0x.sailengine.core.lighting.PointLight;
import io.github.fi0x.sailengine.core.lighting.SpotLight;

public interface IRenderer<T>
{
	void init() throws Exception;

	void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

	void bind(Model model);

	void unbind();

	void prepare(T t, Camera camera);

	void cleanup();
}
