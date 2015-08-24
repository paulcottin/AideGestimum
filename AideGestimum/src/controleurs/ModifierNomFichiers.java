package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.Principale;

public class ModifierNomFichiers implements ActionListener {

	Principale p;
	
	public ModifierNomFichiers(Principale p) {
		this.p = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//p.modifNomFichiers();
	}

}
