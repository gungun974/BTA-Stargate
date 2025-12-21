package gungun974.stargate.core;

import gungun974.stargate.StargateMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texturepack.TexturePack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WavefrontLoader {
	private final List<WavefrontMaterial> materials = new ArrayList<>();
	private final Map<String, String> mappedMaterials = new HashMap<>();
	private final String wavefrontPath;

	private float[] vertices = new float[0];
	private int verticesSize = 0;

	private float[] normals = new float[0];
	private int normalsSize = 0;

	private float[] textureCoordinates = new float[0];
	private int textureCoordinatesSize = 0;

	private int[] elements = new int[0];
	private int elementsSize = 0;

	public WavefrontLoader(String wavefrontPath) {
		this.wavefrontPath = wavefrontPath;
		loadModel();
	}

	private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
		int newLength = oldLength + Math.max(minGrowth, prefGrowth >> 1);
		if (newLength < 0) {
			throw new OutOfMemoryError("Array size too large");
		}
		return newLength;
	}

	private void loadModel() {
		Minecraft mc = Minecraft.getMinecraft();
		TexturePack pack = mc.texturePackList.getHighestPriorityTexturePackWithFile(wavefrontPath);
		if (pack == null) {
			StargateMod.LOGGER.error("Wavefront file not found {}", wavefrontPath);
			return;
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(pack.getResourceAsStream(wavefrontPath)))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String[] elements = line.split(" ");
				if (elements.length == 0) {
					continue;
				}
				if (Objects.equals(elements[0], "v")) {
					if (elements.length < 4) {
						continue;
					}
					int index = verticesSize;
					ensureVerticesCapacity(index + 3);
					verticesSize += 3;
					this.vertices[index] = Float.parseFloat(elements[1]);
					this.vertices[index + 1] = Float.parseFloat(elements[2]);
					this.vertices[index + 2] = Float.parseFloat(elements[3]);
				}
				if (Objects.equals(elements[0], "vn")) {
					if (elements.length < 4) {
						continue;
					}
					int index = normalsSize;
					ensureNormalsCapacity(index + 3);
					normalsSize += 3;
					this.normals[index] = Float.parseFloat(elements[1]);
					this.normals[index + 1] = Float.parseFloat(elements[2]);
					this.normals[index + 2] = Float.parseFloat(elements[3]);
				}
				if (Objects.equals(elements[0], "vt")) {
					if (elements.length < 3) {
						continue;
					}
					int index = textureCoordinatesSize;
					ensureTextureCoordinatesCapacity(index + 2);
					textureCoordinatesSize += 2;
					this.textureCoordinates[index] = Float.parseFloat(elements[1]);
					this.textureCoordinates[index + 1] = Float.parseFloat(elements[2]);
				}
				if (Objects.equals(elements[0], "f")) {
					if (elements.length < 4) {
						continue;
					}

					for (int i = 1; i < elements.length; i++) {
						final String[] parts = elements[i].split("/");

						int index = this.elementsSize;
						ensureElementsCapacity(index + 3);
						elementsSize += 3;

						this.elements[index] = Integer.parseInt(parts[0]);
						if (parts.length < 2) {
							this.elements[index + 1] = 0;
						} else {
							this.elements[index + 1] = Integer.parseInt(parts[1]);
						}

						if (parts.length < 3) {
							this.elements[index + 2] = 0;
						} else {
							this.elements[index + 2] = Integer.parseInt(parts[2]);
						}
					}
				}
				if (Objects.equals(elements[0], "mtllib")) {
					if (elements.length < 2) {
						continue;
					}

					Path mtl = Paths.get(wavefrontPath).getParent().resolve(elements[1]);

					loadMaterials(mtl.toString());
				}
				if (Objects.equals(elements[0], "usemtl")) {
					if (elements.length < 2) {
						continue;
					}

					int currentMaterialIndex = -1;

					for (int i = 0; i < materials.size(); i++) {
						WavefrontMaterial material = materials.get(i);
						if (material.name.equals(elements[1])) {
							currentMaterialIndex = i;
							break;
						}
					}

					if (currentMaterialIndex == -1) {
						continue;
					}

					int index = this.elementsSize;
					ensureElementsCapacity(index + 1);
					elementsSize += 1;

					this.elements[index] = -currentMaterialIndex - 1;
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadMaterials(String path) {
		Minecraft mc = Minecraft.getMinecraft();
		TexturePack pack = mc.texturePackList.getHighestPriorityTexturePackWithFile(path);
		if (pack == null) {
			StargateMod.LOGGER.error("Wavefront material file not found {}", path);
			return;
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(pack.getResourceAsStream(path)))) {
			WavefrontMaterial currentMaterial = null;

			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String[] elements = line.split(" ");
				if (elements.length == 0) {
					continue;
				}
				if (Objects.equals(elements[0], "newmtl")) {
					if (elements.length < 2) {
						continue;
					}

					if (currentMaterial != null) {
						materials.add(currentMaterial);
					}

					currentMaterial = new WavefrontMaterial(elements[1]);
					continue;
				}
				if (currentMaterial == null) {
					continue;
				}
				if (Objects.equals(elements[0], "map_Kd")) {
					if (elements.length < 2) {
						continue;
					}
					currentMaterial.texture = elements[1];
				}
			}

			if (currentMaterial != null) {
				materials.add(currentMaterial);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void mapMaterial(String original, String mapped) {
		this.mappedMaterials.put(original, mapped);
	}

	public void render(Tessellator tessellator) {
		tessellator.startDrawing(GL11.GL_TRIANGLES);

		int c = 0;
		while (c < elementsSize) {
			int element = elements[c++];

			if (element == 0) {
				continue;
			}

			if (element < 0) {
				int materialIndex = -element - 1;

				WavefrontMaterial currentMaterial = materials.get(materialIndex);

				if (currentMaterial.texture != null) {
					if (this.mappedMaterials.containsKey(currentMaterial.name)) {
						String mappedName = this.mappedMaterials.get(currentMaterial.name);
						for (WavefrontMaterial material : materials) {
							if (material.name.equals(mappedName)) {
								currentMaterial = material;
								break;
							}
						}
					}

					tessellator.draw();
					tessellator.startDrawing(GL11.GL_TRIANGLES);

					if (currentMaterial.texture != null) {
						Path texture = Paths.get(wavefrontPath).getParent().resolve(currentMaterial.texture);

						TextureManager textureManager = Minecraft.getMinecraft().textureManager;
						textureManager.bindTexture(textureManager.loadTexture(texture.toString()));
					}
				}

				continue;
			}

			int vertexIndex = element - 1;
			int textureCoordinateIndex = elements[c++] - 1;
			int normalIndex = elements[c++] - 1;

			if (normalIndex >= 0) {
				tessellator.setNormal(normals[normalIndex * 3], normals[normalIndex * 3 + 1], normals[normalIndex * 3 + 2]);
			}

			if (textureCoordinateIndex >= 0) {
				tessellator.addVertexWithUV(vertices[vertexIndex * 3], vertices[vertexIndex * 3 + 1], vertices[vertexIndex * 3 + 2], textureCoordinates[textureCoordinateIndex * 2], 1 - textureCoordinates[textureCoordinateIndex * 2 + 1]);
			} else {
				tessellator.addVertex(vertices[vertexIndex * 3], vertices[vertexIndex * 3 + 1], vertices[vertexIndex * 3 + 2]);
			}

		}

		tessellator.draw();
	}

	private void ensureVerticesCapacity(int minCapacity) {
		int oldCapacity = this.vertices.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity);
			float[] newArray = new float[newCapacity];
			System.arraycopy(this.vertices, 0, newArray, 0, oldCapacity);
			this.vertices = newArray;
		}
	}

	private void ensureNormalsCapacity(int minCapacity) {
		int oldCapacity = this.normals.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity);
			float[] newArray = new float[newCapacity];
			System.arraycopy(this.normals, 0, newArray, 0, oldCapacity);
			this.normals = newArray;
		}
	}

	private void ensureTextureCoordinatesCapacity(int minCapacity) {
		int oldCapacity = this.textureCoordinates.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity);
			float[] newArray = new float[newCapacity];
			System.arraycopy(this.textureCoordinates, 0, newArray, 0, oldCapacity);
			this.textureCoordinates = newArray;
		}
	}

	private void ensureElementsCapacity(int minCapacity) {
		int oldCapacity = this.elements.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity);
			int[] newElements = new int[newCapacity];
			System.arraycopy(this.elements, 0, newElements, 0, oldCapacity);
			this.elements = newElements;
		}
	}

	private static class WavefrontMaterial {
		final public String name;
		@Nullable
		public String texture;

		private WavefrontMaterial(String name) {
			this.name = name;
		}
	}
}
