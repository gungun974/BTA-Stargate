package gungun974.stargate;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class StargateRecipe implements RecipeEntrypoint {

	public static StargateRecipeNamespace STARGATE = new StargateRecipeNamespace();
	public static RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH;
	public static RecipeGroup<RecipeEntryCrafting<?, ?>> BLAST_FURNACE;
	public static RecipeGroup<RecipeEntryCrafting<?, ?>> TROMMEL;

	@Override
	public void onRecipesReady() {
		StargateMod.LOGGER.info("Loading Stargate recipes...");
		resetGroups();
		registerNamespaces();
		load();
	}

	@Override
	public void initNamespaces() {
		StargateMod.LOGGER.info("Loading Stargate recipe namespaces...");
		resetGroups();

		registerNamespaces();
	}

	public void registerNamespaces() {
		STARGATE.register("workbench", WORKBENCH);
		STARGATE.register("blast_furnace", BLAST_FURNACE);
		STARGATE.register("trommel", TROMMEL);
		Registries.RECIPES.register("stargate", STARGATE);
	}

	public void resetGroups() {
		WORKBENCH = new RecipeGroup<RecipeEntryCrafting<?, ?>>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		BLAST_FURNACE = new RecipeGroup<RecipeEntryCrafting<?, ?>>(new RecipeSymbol(new ItemStack(Blocks.FURNACE_BLAST_ACTIVE)));
		TROMMEL = new RecipeGroup<RecipeEntryCrafting<?, ?>>(new RecipeSymbol(new ItemStack(Blocks.TROMMEL_ACTIVE)));
		Registries.RECIPES.unregister("stargate");
	}

	public void load() {
		DataLoader.loadRecipesFromFile("/assets/stargate/recipes/workbench.json");
		DataLoader.loadRecipesFromFile("/assets/stargate/recipes/blast_furnace.json");
		DataLoader.loadRecipesFromFile("/assets/stargate/recipes/trommel.json");

		StargateMod.LOGGER.info("{} recipes in {} groups.", STARGATE.getAllRecipes().size(), STARGATE.size());
	}
}

