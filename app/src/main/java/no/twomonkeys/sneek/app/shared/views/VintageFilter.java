package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;

import no.twomonkeys.sneek.R;

/**
 * Created by simenlie on 01.06.16.
 */
public class VintageFilter extends CustomFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.r, .16666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.b, .83333)).b);\n" +
            "     \n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    public VintageFilter(Context paramContext) {
        super(paramContext, SHADER);
        setRes();
    }

    private void setRes() {
        addInputTexture(R.drawable.nmap);
        addInputTexture(R.drawable.nblowout);
    }
}
