package io.github.fi0x.sailengine.core;

public interface ILogic
{
	void init() throws Exception;
	void input();
	void update();
	void render();
	void cleanup();
}
