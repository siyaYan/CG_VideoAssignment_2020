import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * Mesh - Helper class cfor importing OBJ's
 *
 * @auther: Jaryd Sartori
 * @uid: u5799628
 * Imports .obj files from blender (requires triangles and not rects) and converts data from blender coordinate system to OpenGL 
 * Coordinate system
 */




public class Mesh {


	public static void loadObjModel(String fileName, Vector<float[]> verts, Vector<float[]> uvs, Vector<float[]> normals) {
		FileReader fr = null;
		
		try {
			fr = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file!");
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		String line;
		Vector<float[]> temp_verts = new Vector<float[]>();
		Vector<float[]> temp_uvs = new Vector<float[]>();
		Vector<float[]> temp_norms = new Vector<float[]>();
		
		Vector<Integer> vIndicies = new Vector<Integer>();
		Vector<Integer> uvIndicies = new Vector<Integer>();
		Vector<Integer> normalIndicies = new Vector<Integer>();

		
		try {
			while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "v":
                        // vertices
                    	float[] tempV = {Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])};
    					temp_verts.add(tempV);
                        break;
                    case "vt":
                        // textures
                    	float[] tempT = {Float.parseFloat(parts[1]), Float.parseFloat(parts[2])};
    					temp_uvs.add(tempT);
                        break;
                    case "vn":
                        // normals
                    	float[] tempN = {Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])};
    					temp_norms.add(tempN);
                        break;
                    case "f":
                        // faces: vertex/texture/normal
                    	String[] v1 = parts[1].split("/");
        				vIndicies.add(Integer.parseInt(v1[0]));
        				uvIndicies.add(Integer.parseInt(v1[1]));
        				normalIndicies.add(Integer.parseInt(v1[2]));
        				String[] v2 = parts[2].split("/");
        				vIndicies.add(Integer.parseInt(v2[0]));
        				uvIndicies.add(Integer.parseInt(v2[1]));
        				normalIndicies.add(Integer.parseInt(v2[2]));
        				String[] v3 = parts[3].split("/");
        				vIndicies.add(Integer.parseInt(v3[0]));
        				uvIndicies.add(Integer.parseInt(v3[1]));
        				normalIndicies.add(Integer.parseInt(v3[2]));
                        break;
                }
            }

			br.close();

			
		} catch(Exception e) {
			e.printStackTrace();
		}
//		Loop vertexes
		for (int i=0;i<vIndicies.size();i++) {
			int vIndex = vIndicies.get(i);
			float[] vertex = temp_verts.get(vIndex-1);
			verts.add(vertex);
		}
//		Loop UV's
		for (int i=0;i<uvIndicies.size();i++) {
			int uvIndex = uvIndicies.get(i);
			float[] vertex = temp_uvs.get(uvIndex-1);
			uvs.add(vertex);
		}
//		Loop Normals
		for (int i=0;i<normalIndicies.size();i++) {
			int nIndex = normalIndicies.get(i);
			float[] vertex = temp_norms.get(nIndex-1);
			normals.add(vertex);
		}
		
		
		
	}	
		
		
		

	
}



