package me.adam.theater;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				new Commands().registerCommands(dispatcher)
		);
	}
}
