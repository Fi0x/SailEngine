package io.github.fi0x.sailengine.core;

import org.lwjgl.opengl.GL20;

public class ShaderManager
{
	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;

	public ShaderManager() throws Exception
	{
		programId = GL20.glCreateProgram();
		if (programId == 0)
			throw new Exception("Could not create shader");
	}

	public void createVertexShader(String shaderCode) throws Exception
	{
		vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws Exception
	{
		fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
	}

	public int createShader(String shaderCode, int shaderType) throws Exception
	{
		int shaderId = GL20.glCreateShader(shaderType);
		if (shaderId == 0)
			throw new Exception("Error creating shader for type " + shaderType);

		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
			throw new Exception(
					"Error compiling shader code for type " + shaderType + " ADDITIONAL INFO: " + GL20.glGetShaderInfoLog(
							shaderId, 1024));

		GL20.glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void link() throws Exception
	{
		GL20.glLinkProgram(programId);

		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
			throw new Exception(
					"Error linking shader code" + " ADDITIONAL INFO: " + GL20.glGetProgramInfoLog(programId, 1024));

		if (vertexShaderId != 0)
			GL20.glDetachShader(programId, vertexShaderId);

		if (fragmentShaderId != 0)
			GL20.glDetachShader(programId, fragmentShaderId);

		GL20.glValidateProgram(programId);

		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0)
			throw new Exception("Unable to validate shader code " + GL20.glGetProgramInfoLog(programId, 1024));
	}

	public void bind()
	{
		GL20.glUseProgram(programId);
	}

	public void unbind()
	{
		GL20.glUseProgram(0);
	}

	public void cleanup()
	{
		unbind();
		if (programId != 0)
			GL20.glDeleteProgram(programId);
	}
}
