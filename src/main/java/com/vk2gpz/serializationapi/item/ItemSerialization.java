package com.vk2gpz.serializationapi.item;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Base64;

public class ItemSerialization {
	private static Method a;
	private static Method b;

	private static CraftItemStack a(org.bukkit.inventory.ItemStack paramItemStack) {
		try {
			if ((paramItemStack instanceof CraftItemStack)) {
				return (CraftItemStack) paramItemStack;
			}
		} catch (IllegalStateException localIllegalStateException1) {
			throw localIllegalStateException1;
		}
		try {
			if (paramItemStack != null) {
				return CraftItemStack.asCraftCopy(paramItemStack);
			}
		} catch (IllegalStateException localIllegalStateException2) {
			throw localIllegalStateException2;
		}
		return null;
	}

	private static void a(NBTBase paramNBTBase, DataOutput paramDataOutput) {
		if (a == null) {
			try {
				a = NBTCompressedStreamTools.class.getDeclaredMethod("a", new Class[]{NBTBase.class, DataOutput.class});
				a.setAccessible(true);
			} catch (Exception localException1) {
				throw new IllegalStateException("Unable to find private write method.", localException1);
			}
		}
		try {
			a.invoke(null, new Object[]{paramNBTBase, paramDataOutput});
		} catch (Exception localException2) {
			throw new IllegalArgumentException("Unable to write " + paramNBTBase + " to " + paramDataOutput, localException2);
		}
	}

	private static NBTBase a(DataInput paramDataInput, int paramInt) {
		if (b == null) {
			try {
				b = NBTCompressedStreamTools.class.getDeclaredMethod("a", new Class[]{DataInput.class, Integer.TYPE, NBTReadLimiter.class});

				b.setAccessible(true);
			} catch (Exception localException1) {
				throw new IllegalStateException("Unable to find private read method.", localException1);
			}
		}
		try {
			return (NBTBase) b.invoke(null, new Object[]{paramDataInput, Integer.valueOf(paramInt), NBTReadLimiter.a});
		} catch (Exception localException2) {
			throw new IllegalArgumentException("Unable to read from " + paramDataInput, localException2);
		}
	}

	public static Inventory fromBase64(String data) {
		ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
		NBTTagList localNBTTagList = (NBTTagList) a(new DataInputStream(localByteArrayInputStream), 0);
		CraftInventoryCustom localCraftInventoryCustom = new CraftInventoryCustom(null, localNBTTagList.size());
		for (int i = 0; i < localNBTTagList.size(); ) {
			NBTTagCompound localNBTTagCompound = localNBTTagList.get(i);
			try {
				if (!localNBTTagCompound.isEmpty()) {


					localCraftInventoryCustom.setItem(i, CraftItemStack.asCraftMirror(net.minecraft.server.v1_7_R4.ItemStack.createStack(localNBTTagCompound)));
				}
			} catch (IllegalStateException localIllegalStateException) {
				throw localIllegalStateException;
			}
			i++;
		}
		return localCraftInventoryCustom;
	}

	public static String toBase64(Inventory inventory) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

		DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);

		NBTTagList localNBTTagList = new NBTTagList();
		for (int i = 0; i < inventory.getSize(); ) {
			NBTTagCompound localNBTTagCompound = new NBTTagCompound();
			CraftItemStack localCraftItemStack = a(inventory.getItem(i));
			try {
				if (localCraftItemStack != null) {
					CraftItemStack.asNMSCopy(localCraftItemStack).save(localNBTTagCompound);
				}
			} catch (IllegalStateException localIllegalStateException) {
				throw localIllegalStateException;
			}
			localNBTTagList.add(localNBTTagCompound);

			i++;
		}
		a(localNBTTagList, localDataOutputStream);

		return Base64.getEncoder().encodeToString(localByteArrayOutputStream.toByteArray());
	}
}
