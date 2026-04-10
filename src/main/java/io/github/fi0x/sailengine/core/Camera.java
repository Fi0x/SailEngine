package io.github.fi0x.sailengine.core;

import org.joml.Vector3f;

public class Camera
{
	private Vector3f position;
	private Vector3f rotation;

	public Camera()
	{
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
	}

	public Camera(Vector3f position, Vector3f rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}

	public void movePosition(float x, float y, float z)
	{
		if (z != 0)
		{
			position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1f * z;
			position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
		}
		if (x != 0)
		{
			position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1f * x;
			position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
		}
		position.y += y;
	}

	public void setPosition(float x, float y, float z)
	{
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public void setRotation(float x, float y, float z)
	{
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public void moveRotation(float x, float y, float z)
	{
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public Vector3f getRotation()
	{
		return rotation;
	}
}
