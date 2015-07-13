package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.Script;

public class ParametrerScript implements ActionListener{

	private Script s;
	
	public ParametrerScript(Script s) {
		this.s = s;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		s.choisirFichiers();
	}

}
