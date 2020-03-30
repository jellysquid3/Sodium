package me.jellysquid.mods.sodium.client.render.chunk;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkMeshInfo;
import me.jellysquid.mods.sodium.client.render.chunk.compile.LayerMeshInfo;
import me.jellysquid.mods.sodium.client.render.gl.GlVertexArray;
import me.jellysquid.mods.sodium.client.render.gl.GlVertexArrayBuffer;
import me.jellysquid.mods.sodium.client.render.gl.GlVertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.List;

public class ChunkRenderDataVAO implements ChunkRenderData {
    private static final VertexFormat VERTEX_FORMAT = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;

    private final Reference2ReferenceArrayMap<RenderLayer, GlVertexArrayBuffer> vaos = new Reference2ReferenceArrayMap<>();

    public GlVertexArrayBuffer getVertexArrayForLayer(RenderLayer layer) {
        return this.vaos.get(layer);
    }

    @Override
    public void clearData() {
        for (GlVertexArrayBuffer buffer : this.vaos.values()) {
            buffer.delete();
        }

        this.vaos.clear();
    }

    @Override
    public void uploadData(ChunkMeshInfo mesh) {
        List<RenderLayer> removed = new ArrayList<>(this.vaos.keySet());

        for (LayerMeshInfo entry : mesh.getLayers()) {
            GlVertexArrayBuffer buffer = this.vaos.computeIfAbsent(entry.getLayer(), this::createData);
            buffer.upload(entry.takePendingUpload());

            removed.remove(entry.getLayer());
        }

        for (RenderLayer layer : removed) {
            GlVertexArrayBuffer buffer = this.vaos.remove(layer);
            buffer.delete();
        }
    }

    private GlVertexArrayBuffer createData(RenderLayer layer) {
        return new GlVertexArrayBuffer(VERTEX_FORMAT, new GlVertexBuffer(GL15.GL_ARRAY_BUFFER), new GlVertexArray());
    }
}
