package seemdmax.lcpatches;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mcjty.lostcities.api.ILostCityBuilding;
import net.minecraft.launchwrapper.IClassTransformer;

public class LostCitiesClassTransformer implements IClassTransformer {
	private static final String[] classesBeingTransformed = { "mcjty.lostcities.dimensions.world.lost.BuildingInfo" };

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBeingTransformed) {
		boolean isObfuscated = !name.equals(transformedName);
		int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
		return index != -1 ? transform(index, classBeingTransformed, isObfuscated) : classBeingTransformed;
	}

	private static byte[] transform(int index, byte[] classBeingTransformed, boolean isObfuscated) {
		System.out.println("Transform " + classesBeingTransformed[index] + " got called!");
		try {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classBeingTransformed);
			classReader.accept(classNode, 0);

			System.out.println("Transforming " + classesBeingTransformed[index] + " Is Obf: " + isObfuscated);
			switch (index) {
			case 0:
				transformLCCellars(classNode, isObfuscated);
				break;
			}

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS /* | ClassWriter.COMPUTE_FRAMES */);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classBeingTransformed;
	}

	private static void transformLCCellars(ClassNode buildingInfoClass, boolean isObfuscated) {
		final String BUILDING_INFO = isObfuscated ? "<init>" : "<init>";
		final String BUILDING_INFO_DESC = isObfuscated
				? "(IILmcjty/lostcities/dimensions/world/LostCityChunkGenerator;)V"
				: "(IILmcjty/lostcities/dimensions/world/LostCityChunkGenerator;)V";

		for (MethodNode method : buildingInfoClass.methods) {
			if (method.name.equals(BUILDING_INFO) && method.desc.equals(BUILDING_INFO_DESC)) {
				System.out.println("Found method in BuildingInfo to transform");
				AbstractInsnNode targetNode = null;
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ILOAD) {
						if (((VarInsnNode) instruction).var == 13 & instruction.getNext().getOpcode() == IFGT) {
							System.out.println("Matched");
							targetNode = instruction;
							break;
						}
					}
				}
				if (targetNode != null) {
					System.out.println("Target Node valid");

					InsnList toInsert = new InsnList();
					toInsert.add(new VarInsnNode(ALOAD, 0));
					toInsert.add(
							new MethodInsnNode(INVOKEVIRTUAL, "mcjty/lostcities/dimensions/world/lost/BuildingInfo",
									"getBuilding", "()Lmcjty/lostcities/api/ILostCityBuilding;", false));
					toInsert.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(ILostCityBuilding.class),
							"getMinCellars", "()I", true));
					toInsert.add(
							new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Math.class), "max", "(II)I", false));

					method.instructions.insertBefore(targetNode, toInsert);
					System.out.println("Transform done!");
				} else {
					System.out.println("Something went wrong transforming BuildingInfo!");
				}
			}
		}
	}
}
