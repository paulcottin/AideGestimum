package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import utilitaires.Utils;

public class SupprimerImageTrop implements ActionListener {

	ArrayList<File> files;
	
	public SupprimerImageTrop(ArrayList<File> files) {
		this.files = new ArrayList<File>();
		this.files.addAll(files);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			(new Utils()).supprImagesNonUtilisees(files);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
