package me.pajic.accessorify.util;

import me.pajic.accessorify.Accessorify;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class GeneralUtil {
	public static final TagKey<Item> LANTERNS = TagKey.create(Registries.ITEM, Accessorify.id("lanterns"));
	public static final TagKey<Item> SHULKER_BOXES = TagKey.create(Registries.ITEM, Accessorify.id("shulker_boxes"));
	public static final TagKey<Item> TOTEMS = TagKey.create(Registries.ITEM, Accessorify.id("totems"));

	public static Identifier vanillaId(String path) {
		//? if 1.20.1
		//return new ResourceLocation(path);
		//? if > 1.20.1
		return Identifier.withDefaultNamespace(path);
	}

	public static Identifier customId(String namespace, String path) {
		//? if 1.20.1
		//return new ResourceLocation(namespace, path);
		//? if > 1.20.1
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static Item itemFromId(Identifier id) {
		//? if > 1.21.1
		return BuiltInRegistries.ITEM.getValue(id);
		//? if <= 1.21.1
		//return BuiltInRegistries.ITEM.get(id);
	}
}
