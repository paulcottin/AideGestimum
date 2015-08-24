package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import utilitaires.Utils;

public class ExportTaille implements ActionListener{

	ArrayList<File> files;
	
	public ExportTaille(ArrayList<File> files) {
		this.files = files;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		(new Utils()).generateFileSize(files);
	}

}
