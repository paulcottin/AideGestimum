package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class SearchCSS implements ActionListener{

	utilitaires.SearchCSS s;
	
	public SearchCSS(ArrayList<File> files) {
		this.s = new utilitaires.SearchCSS(files);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		s.search();
	}

}
