package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import exceptions.ParametrageError;
import utilitaires.Utils;

public class ConvertImg implements ActionListener{

	ArrayList<File> pngFiles;
	
	public ConvertImg(ArrayList<File> files) {
		pngFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".png"))
				pngFiles.add(file);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			(new Utils()).pngToJpeg(pngFiles);
		} catch (ParametrageError e) {
			e.printStackTrace();
		}
	}

}
