package de.johni0702.minecraft.view.impl.compat.nothirium;

import meldexun.matrixutil.Matrix4f;
import meldexun.renderlib.util.GLShader;
import meldexun.renderlib.util.GLUtil;
import meldexun.renderlib.util.RenderUtil;

public final class NothiriumShaderCompat {
    private NothiriumShaderCompat() {
    }

    public static void uploadModelView(GLShader shader, boolean includeCameraOffset) {
        Matrix4f matrix = RenderUtil.getModelViewMatrix().copy();
        if (includeCameraOffset) {
            matrix.translate(
                    (float) RenderUtil.getCameraOffsetX(),
                    (float) RenderUtil.getCameraOffsetY(),
                    (float) RenderUtil.getCameraOffsetZ());
        }
        GLUtil.setMatrix(shader.getUniform("u_ModelViewMatrix"), matrix);
    }
}
