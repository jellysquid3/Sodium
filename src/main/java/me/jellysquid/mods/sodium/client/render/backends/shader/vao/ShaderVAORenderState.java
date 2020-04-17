package me.jellysquid.mods.sodium.client.render.backends.shader.vao;

import me.jellysquid.mods.sodium.client.gl.array.GlVertexArray;
import me.jellysquid.mods.sodium.client.gl.attribute.GlAttributeBinding;
import me.jellysquid.mods.sodium.client.gl.buffer.GlBuffer;
import me.jellysquid.mods.sodium.client.render.backends.ChunkRenderState;
import net.minecraft.client.util.math.Vector3d;
import org.lwjgl.opengl.GL20;

public class ShaderVAORenderState implements ChunkRenderState {
    private final GlBuffer vertexBuffer;
    private final GlVertexArray vertexArray;
    private final GlAttributeBinding[] attributes;

    private boolean init;
    private Vector3d translation;

    public ShaderVAORenderState(GlBuffer vertexBuffer, GlAttributeBinding[] attributes, Vector3d translation) {
        this.vertexBuffer = vertexBuffer;
        this.vertexArray = new GlVertexArray();
        this.attributes = attributes;
        this.translation = translation;
    }

    public void unbind() {
        this.vertexArray.unbind();
    }

    @Override
    public void delete() {
        this.vertexBuffer.delete();
        this.vertexArray.delete();
    }

    public void bind() {
        this.vertexArray.bind();

        if (!this.init) {
            this.vertexBuffer.bind();

            for (GlAttributeBinding binding : this.attributes) {
                GL20.glVertexAttribPointer(binding.index, binding.count, binding.format, binding.normalized, binding.stride, binding.pointer);
                GL20.glEnableVertexAttribArray(binding.index);
            }

            this.vertexBuffer.unbind();

            this.init = true;
        }
    }

    public void draw(int mode) {
        this.vertexBuffer.drawArrays(mode);
    }

    public Vector3d getTranslation() {
        return this.translation;
    }
}
