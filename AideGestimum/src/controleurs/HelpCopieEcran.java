package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import exceptions.ParametrageError;
import utilitaires.Utils;

public class HelpCopieEcran implements ActionListener{

	private ArrayList<File> files;
	
	public HelpCopieEcran(ArrayList<File> files) {
		this.files = new ArrayList<File>();
		this.files.addAll(files);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			(new Utils()).helpCopieEcran(files);
		} catch (ParametrageError e) {
			e.printMessage();
		}
	}

}
