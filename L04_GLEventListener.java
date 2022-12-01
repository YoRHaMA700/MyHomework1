import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class L04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Camera camera;
  private SGNode twoBranchRoot;
  private TransformNode translateX, rotateAll, rotateUpper, rotateUUpper, rotateTail, rotateHead, rotateEar1, rotateEar2, rotateEye1, rotateEye2, rotateM;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  private float rotateTailAngleStart = 90, rotateTailAngle = rotateTailAngleStart;
  private float rotateHeadAngleStart = 90, rotateHeadAngle = rotateHeadAngleStart;
  private float rotateEar1AngleStart = -40, rotateEar1Angle = rotateEar1AngleStart;
  private float rotateEar2AngleStart = -40, rotateEar2Angle = rotateEar2AngleStart;
  private float rotateEye1AngleStart = 0, rotateEye1Angle = rotateEye1AngleStart;
  private float rotateEye2AngleStart = 0, rotateEye2Angle = rotateEye2AngleStart;
  private float rotateMAngleStart = 0, rotateMAngle = rotateMAngleStart;

  /* The constructor is not used to initialise anything */
  public L04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,6f,15f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    cube.dispose(gl);
    tt1.dispose(gl);
    tt2.dispose(gl);
    tt3.dispose(gl);
    sphere.dispose(gl);
    light.dispose(gl);
    lampT.dispose(gl);
  }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Model cube, tt1, tt2, tt3, sphere, lampT;
  private Light light, lampLight;
  
  public void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "container2.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "container2_specular.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "cloud.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "wood.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "wall.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "tree.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "highlight.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "light.jpg");

    
    light = new Light(gl);
    light.setCamera(camera);
    lampLight = new Light(gl);
    lampLight.setCamera(camera);

    Mesh m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_01.txt", "fs_tt_01.txt");
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    // no textures for this model
    tt1 = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId3);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_01.txt", "fs_tt_02.txt");
    material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    tt2 = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId2);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_01.txt", "fs_tt_03.txt");
    material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    tt3 = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId4);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    cube = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId0, textureId1);

    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f),
            new Vec3(1.0f, 0.5f, 0.31f),
            new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    sphere = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId5, textureId6);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_lampT_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    lampT = new Model(gl, camera, light, shader, material, new Mat4(1), m, textureId7);


    twoBranchRoot = new NameNode("two-branch structure");
    translateX
            = new TransformNode("translate(" + -8f + ",0,0)",
            Mat4Transform.translate(-8f,0,0));
    rotateAll
            = new TransformNode("rotateAroundZ("+rotateAllAngle+")",
            Mat4Transform.rotateAroundZ(rotateAllAngle));

    NameNode lowerBranch = new NameNode("lower branch");
    Mat4 m4 = Mat4Transform.scale(0.5f,4,0.5f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLowerBranch
            = new TransformNode("scale(0.5,4,0.5); translate(0,0.5,0)", m4);
    ModelNode cube0Node = new ModelNode("Sphere(0)", sphere);
    //
    TransformNode translateToTop
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0,4,0));
    rotateUpper
            = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",
            Mat4Transform.rotateAroundZ(rotateUpperAngle));
    NameNode upperBranch = new NameNode("upper branch");
    m4 = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeUpperBranch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube1Node = new ModelNode("Sphere(1)", sphere);
    //
    TransformNode translateToTTop
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0,0.5f,0));
    rotateUUpper
            = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",
            Mat4Transform.rotateAroundZ(rotateUpperAngle));
    NameNode uupperBranch = new NameNode("uupper branch");
    m4 = Mat4Transform.scale(0.5f,3f,0.5f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeUUpperBranch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube2Node = new ModelNode("Sphere(2)", sphere);
    //
    TransformNode translateToTail
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0f,0.2f,0));
    rotateTail
            = new TransformNode("rotateAroundZ("+rotateTailAngle+")",
            Mat4Transform.rotateAroundZ(rotateTailAngle));
    NameNode tailBranch = new NameNode("tail branch");
    m4 = Mat4Transform.scale(0.2f,2f,0.2f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeTailBranch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube3Node = new ModelNode("Sphere(3)", lampT);
    //
    TransformNode translateToHead
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(1f,3f,0));
    rotateHead
            = new TransformNode("rotateAroundZ("+rotateHeadAngle+")",
            Mat4Transform.rotateAroundZ(rotateHeadAngle));
    NameNode headBranch = new NameNode("head branch");
    m4 = Mat4Transform.scale(0.5f,1.5f,1f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeHeadBranch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube4Node = new ModelNode("Sphere(3)", lampT);
    //
    TransformNode translateToEar1
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0f,1.5f,-0.5f));
    rotateEar1
            = new TransformNode("rotateAroundZ("+rotateEar1Angle+")",
            Mat4Transform.rotateAroundZ(rotateEar1Angle));
    NameNode ear1Branch = new NameNode("ear1 branch");
    m4 = Mat4Transform.scale(0.2f,1f,0.2f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeEar1Branch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube5Node = new ModelNode("Ear", lampT);
    TransformNode translateToEar2
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0f,1.5f,0.5f));
    rotateEar2
            = new TransformNode("rotateAroundZ("+rotateEar2Angle+")",
            Mat4Transform.rotateAroundZ(rotateEar2Angle));
    NameNode ear2Branch = new NameNode("ear2 branch");
    m4 = Mat4Transform.scale(0.2f,1f,0.2f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeEar2Branch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube6Node = new ModelNode("Ear", lampT);
    //
    TransformNode translateToEye1
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0.5f,-0.2f,-0.3f));
    rotateEye1
            = new TransformNode("rotateAroundZ("+rotateEye1Angle+")",
            Mat4Transform.rotateAroundZ(rotateEye1Angle));
    NameNode eye1Branch = new NameNode("eye1 branch");
    m4 = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeEye1Branch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube7Node = new ModelNode("Eye", sphere);
    TransformNode translateToEye2
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0.5f,-0.2f,0.3f));
    rotateEye2
            = new TransformNode("rotateAroundZ("+rotateEye2Angle+")",
            Mat4Transform.rotateAroundZ(rotateEye2Angle));
    NameNode eye2Branch = new NameNode("eye2 branch");
    m4 = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeEye2Branch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube8Node = new ModelNode("Eye", sphere);
    //
    TransformNode translateToM
            = new TransformNode("translate(0,4,0)", Mat4Transform.translate(-0.5f,0.1f,0f));
    rotateM
            = new TransformNode("rotateAroundZ("+rotateMAngle+")",
            Mat4Transform.rotateAroundZ(rotateMAngle));
    NameNode MBranch = new NameNode("mouse branch");
    m4 = Mat4Transform.scale(0.7f,0.7f,0.7f);
    m4 = Mat4.multiply(m4, Mat4Transform.translate(0f,0.5f,0));
    TransformNode makeMBranch
            = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m4);
    ModelNode cube9Node = new ModelNode("Mouse", sphere);

    // Then, put all the pieces together to make the scene graph
    // Indentation is meant to show the hierarchy.

    twoBranchRoot.addChild(translateX);
    translateX.addChild(rotateAll);
    rotateAll.addChild(lowerBranch);
    lowerBranch.addChild(makeLowerBranch);
    makeLowerBranch.addChild(cube0Node);
    lowerBranch.addChild(translateToTop);
    translateToTop.addChild(rotateUpper);
    rotateUpper.addChild(upperBranch);
    upperBranch.addChild(makeUpperBranch);
    makeUpperBranch.addChild(cube1Node);

    upperBranch.addChild(translateToTTop);
    translateToTTop.addChild(rotateUUpper);
    rotateUUpper.addChild(uupperBranch);
    uupperBranch.addChild(makeUUpperBranch);
    makeUUpperBranch.addChild(cube2Node);

    upperBranch.addChild(translateToTail);
    translateToTail.addChild(rotateTail);
    rotateTail.addChild(tailBranch);
    tailBranch.addChild(makeTailBranch);
    makeTailBranch.addChild(cube3Node);

    uupperBranch.addChild(translateToHead);
    translateToHead.addChild(rotateHead);
    rotateHead.addChild(headBranch);
    headBranch.addChild(makeHeadBranch);
    makeHeadBranch.addChild(cube4Node);

    headBranch.addChild(translateToEar1);
    translateToEar1.addChild(rotateEar1);
    rotateEar1.addChild(ear1Branch);
    ear1Branch.addChild(makeEar1Branch);
    makeEar1Branch.addChild(cube5Node);
    headBranch.addChild(translateToEar2);
    translateToEar2.addChild(rotateEar2);
    rotateEar2.addChild(ear2Branch);
    ear2Branch.addChild(makeEar2Branch);
    makeEar2Branch.addChild(cube6Node);

    headBranch.addChild(translateToEye1);
    translateToEye1.addChild(rotateEye1);
    rotateEye1.addChild(eye1Branch);
    eye1Branch.addChild(makeEye1Branch);
    makeEye1Branch.addChild(cube7Node);
    headBranch.addChild(translateToEye2);
    translateToEye2.addChild(rotateEye2);
    rotateEye2.addChild(eye2Branch);
    eye2Branch.addChild(makeEye2Branch);
    makeEye2Branch.addChild(cube8Node);

    headBranch.addChild(translateToM);
    translateToM.addChild(rotateM);
    rotateM.addChild(MBranch);
    MBranch.addChild(makeMBranch);
    makeMBranch.addChild(cube9Node);

    twoBranchRoot.update();  // IMPORTANT – must be done every time
  }
 

  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    //updateLightColour();
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    lampLight.setPosition(getLampPosition());  // changing light position each frame
    lampLight.render(gl);
    
    cube.setModelMatrix(getMforCubeL1());     // change transform
    cube.render(gl);
    cube.setModelMatrix(getMforCubeL2());     // change transform
    cube.render(gl);
    cube.setModelMatrix(getMforCubeL3());     // change transform
    cube.render(gl);
    cube.setModelMatrix(getMforCubeL4());     // change transform
    cube.render(gl);
    cube.setModelMatrix(getMforCubeF());     // change transform
    cube.render(gl);
    cube.setModelMatrix(getMforCubeT());     // change transform
    cube.render(gl);
    tt1.setModelMatrix(getMforTT1());       // change transform
    tt1.render(gl);
    tt2.setModelMatrix(getMforTT2());       // change transform 这是背景板
    tt2.render(gl);
    tt3.setModelMatrix(getMforTT3());       // change transform
    tt3.render(gl);
    tt3.setModelMatrix(getMforTT4());       // change transform 加了一面墙在右边
    tt3.render(gl);
    sphere.setModelMatrix(getMforSphere());       // change transform 加了一面墙在右边
    sphere.render(gl);
    lampT.setModelMatrix(getMforlampTL());     // change transform
    lampT.render(gl);
    lampT.setModelMatrix(getMforlampTR());     // change transform
    lampT.render(gl);

    updateBranches();          // change the angles for branches each frame
    twoBranchRoot.draw(gl);
  }
  
  // Method to alter light colour over time
  
  private void updateLightColour() {
    double elapsedTime = getSeconds()-startTime;
    Vec3 lightColour = new Vec3();
    lightColour.x = (float)Math.sin(elapsedTime * 2.0f);
    lightColour.y = (float)Math.sin(elapsedTime * 0.7f);
    lightColour.z = (float)Math.sin(elapsedTime * 1.3f);
    Material m = light.getMaterial();
    m.setDiffuse(Vec3.multiply(lightColour,0.5f));
    m.setAmbient(Vec3.multiply(m.getDiffuse(),0.2f));
    light.setMaterial(m);
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 3.4f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }
  private Vec3 getLampPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = -4f*(float)Math.sin(elapsedTime*0.2f);
    float y = 7.5f*(float)Math.sin(elapsedTime*0.2f);
    float z = 0.5f*(float)Math.sin(elapsedTime*0.2f);
    return new Vec3(x,y,z);
  }
  private Mat4 getMforCubeL1() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-4.5f,0.5f,-5f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3f,0.5f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforCubeL2() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-4.5f,0.5f,5f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3f,0.5f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforCubeL3() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(4.5f,0.5f,-5f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3f,0.5f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforCubeL4() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(4.5f,0.5f,5f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3f,0.5f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforCubeF() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0f,6f,0f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(6f,0.5f,6f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforCubeT() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0f,3.5f,0f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(2f,1f,2f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforTT1() {
    float size = 20f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size*1.3f,1f,size), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforTT2() {
    float size = 60f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size*1.5f,size*10,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-1f,size*0.2f,-size*0.5f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforTT3() {
    float size = 20f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-size*0.65f,size*0.5f,0), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforTT4() {
    float size = 20f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(size*0.65f,size*0.5f,0), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforSphere() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(2,3,2), Mat4Transform.translate(0,0.5f,0));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,4,0), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforlampTL(){
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-4f,0.5f,0f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(2f,0.5f,2f), modelMatrix);
    return modelMatrix;
  }
  private Mat4 getMforlampTR(){
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(4f,0.5f,0f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(2f,0.5f,2f), modelMatrix);
    return modelMatrix;
  }

    // ***************************************************
  /* TIME
   */
  private double startTime;
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;
    rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
    rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.2f);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    rotateUUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }

  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
}
