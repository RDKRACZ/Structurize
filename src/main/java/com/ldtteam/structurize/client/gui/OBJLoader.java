package com.ldtteam.structurize.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ldtteam.structurize.client.render.util.RenderUtils;
import com.ldtteam.structurize.client.render.util.RenderUtils.BuiltBuffer;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.obj.LineReader;

/**
 * Class for loading one obj model.
 */
public class OBJLoader
{
    private final String namespace;
    private final String directory;
    private final String modelFile;
    private OBJModel model;

    public OBJLoader(final String namespace, final String path)
    {
        this.namespace = namespace;
        final int lastSlashPos = path.lastIndexOf('/');
        this.directory = path.substring(0, lastSlashPos);
        this.modelFile = path.substring(lastSlashPos + 1);
    }

    // copied from OBJModel from Forge
    public void loadModel() throws Exception
    {
        model = new OBJModel();

        OBJObject currentObject = null;

        final LineReader reader = new LineReader(Minecraft.getInstance()
            .getResourceManager()
            .getResource(new ResourceLocation(namespace, directory + "/" + modelFile + ".obj")));
        String[] line;
        while ((line = reader.readAndSplitLine(true)) != null)
        {
            switch (line[0])
            {
                case "mtllib": // Loads material library
                    break;

                case "usemtl": // Sets the current material (starts new mesh)
                    break;

                case "v": // Vertex
                    model.positions.add(net.minecraftforge.client.model.obj.OBJModel.parseVector4To3(line));
                    break;

                case "vt": // Vertex texcoord
                    model.texCoords.add(net.minecraftforge.client.model.obj.OBJModel.parseVector2(line));
                    break;

                case "vn": // Vertex normal
                    model.normals.add(net.minecraftforge.client.model.obj.OBJModel.parseVector3(line));
                    break;

                case "vc": // Vertex color (non-standard)
                    model.colors.add(net.minecraftforge.client.model.obj.OBJModel.parseVector4(line));
                    break;

                case "f": // Face
                    final int[][] vertices = new int[line.length - 1][];

                    for (int i = 0; i < vertices.length; i++)
                    {
                        final String vertexData = line[i + 1];
                        final String[] vertexParts = vertexData.split("/");
                        final int[] vertex = Arrays.stream(vertexParts)
                            .mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num))
                            .toArray();

                        if (vertex[0] < 0)
                        {
                            vertex[0] = model.positions.size() + vertex[0];
                        }
                        else
                        {
                            vertex[0]--;
                        }
                        if (vertex.length > 1)
                        {
                            if (vertex[1] < 0)
                            {
                                vertex[1] = model.texCoords.size() + vertex[1];
                            }
                            else
                            {
                                vertex[1]--;
                            }
                            if (vertex.length > 2)
                            {
                                if (vertex[2] < 0)
                                {
                                    vertex[2] = model.normals.size() + vertex[2];
                                }
                                else
                                {
                                    vertex[2]--;
                                }
                                if (vertex.length > 3)
                                {
                                    if (vertex[3] < 0)
                                    {
                                        vertex[3] = model.colors.size() + vertex[3];
                                    }
                                    else
                                    {
                                        vertex[3]--;
                                    }
                                }
                            }
                        }
                        vertices[i] = vertex;
                    }

                    currentObject.faces.add(vertices);
                    break;

                case "s": // Smoothing group (starts new mesh)
                    break;

                case "g":
                    break;

                case "o":
                    final String name = line[1];
                    currentObject = new OBJObject(name);
                    model.objects.add(currentObject);
                    break;

                default:
                    break;
            }
        }
        reader.close();
    }

    public Map<String, BuiltBuffer> loadIntoGL()
    {
        final Map<String, BuiltBuffer> objects = new HashMap<>();

        for (final OBJObject object : model.objects)
        {
            final BufferBuilder builder = RenderUtils.createAndBeginBuffer(RenderUtils.COLORED_SHAPE);

            for (final int[][] face : object.faces)
            {
                if (face.length == 3)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        final Vector3f v = model.positions.get(face[i][0]);
                        builder.pos(v.getX(), v.getY(), v.getZ()).color(255, 0, 0, 255).endVertex();
                    }
                }
            }

            objects.put(object.name, RenderUtils.finishBuffer(builder, RenderUtils.COLORED_SHAPE));
        }

        return objects;
    }

    public class OBJModel
    {
        private final List<Vector3f> positions = new ArrayList<>();
        private final List<Vector2f> texCoords = new ArrayList<>();
        private final List<Vector3f> normals = new ArrayList<>();
        private final List<Vector4f> colors = new ArrayList<>();
        private final List<OBJObject> objects = new ArrayList<>();

        private OBJModel()
        {
        }
    }

    public class OBJObject
    {
        private final List<int[][]> faces = new ArrayList<>();
        private final String name;

        private OBJObject(final String name)
        {
            this.name = name;
        }
    }
}
