package project;

import static org.lwjgl.opengl.GL20.*;

public class Material {

    private float specularIntensity;
    private int specularIntensityLocation;
    private float shininess;
    private int shininessLocation;

    Material(float sIntensity, float theShininess, int shaderProgram){
        specularIntensity = sIntensity;
        shininess = theShininess;

        specularIntensityLocation = glGetUniformLocation(shaderProgram, "material.specularIntensity");
        shininessLocation = glGetUniformLocation(shaderProgram, "material.shininess");
    }

    public void useMaterial(){
        glUniform1f(specularIntensityLocation, specularIntensity);
        glUniform1f(shininessLocation, shininess);
    }
}
